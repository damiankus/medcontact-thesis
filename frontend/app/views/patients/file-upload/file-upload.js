'use strict';



myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/file-upload', {
        templateUrl: 'views/patients/file-upload/file-upload.html',
        controller: 'FileUploadCtrl'
    });
}]);

myApp.controller('FileUploadCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
		
		$rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
		$scope.sortField = "uploadTime";
		$scope.sortReversed = true;
		$scope.fileServiceUrl = REST_API + "patients/" + $rootScope.userDetails.id + "/files";
		
		$scope.getReservations = function () {
			$http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/reservations")
			.then(function successCallback(response) {
				$scope.reservations	 = response.data;
				$scope.reservations.forEach(function (reservation, index) {
					reservation.startDateTime = new Date(reservation.startDateTime);
					reservation.endDateTime = new Date(reservation.endDateTime);
				});
				
			}, function errorCallback(response) {
				console.log("[ERROR]: " + response.data.message);
			});
		}
		
		if ($scope.reservations === undefined
				|| $scope.reservations == null) {
			
			$scope.getReservations();
		}
		
		/* Load file info from the server */
		
		$scope.getSharedFiles = function () {
			$http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/sharedFiles")
			.then(function successCallback(response) {
				$scope.sharedFiles = response.data;
				$scope.fileReservationMap = {};
				
				$scope.files.forEach(function (file, index) {
					$scope.fileReservationMap[file.id] = {};
					$scope.reservations.forEach(function (reservation, index) {
						$scope.fileReservationMap[file.id][reservation.id] = false;
					});
				});
				
				console.log($scope.fileReservationMap);
				
				$scope.sharedFiles.forEach(function (item, index) {
					$scope.fileReservationMap[item.fileEntryId][item.reservationId] = true;
				});
				
			}, function errorCallback(response) {
				console.log("[ERROR]: " + response);
			});
		}
		
		$scope.getFiles = function () {
			$http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/fileEntries")
			.then(function successCallback(response) {
				$scope.files = response.data;
				$scope.files.forEach(function (item) {
					item.uploadTime = new Date(item.uploadTime);
				});
				
				$scope.getSharedFiles();
				
			}, function errorCallback(response) {
				console.log("[ERROR]: " + response);
			});
		}
		
		$scope.showBriefly = function (parent, child) {
			$(parent).append($(child));
			
			child.fadeIn()
				.delay(500)
				.fadeOut()
				.delay(500);
			
			setTimeout(function () {
				child.remove();
			}, 1500);
		}
		
		$scope.indicateSuccess = function (selector) {
			var successLabel = $("<label class='collapse btn btn-success glyphicon glyphicon-ok'></label>");
			$scope.showBriefly($(selector), successLabel);
		};
		
		$scope.indicateFailure = function (selector) {
			var successLabel = $("<label class='collapse btn btn-danger glyphicon glyphicon-remove'></label>");
			$scope.showBriefly($(selector), successLabel);
		};
		
		$scope.initListeners = function () {
		
			var fileInput = $("#uploaded-file");
			var cancelBtn = $("#file-cancel-btn");
			var filenameArea = $("#filename-area");
			var uploadBtn = $("#file-upload-btn");
			var defaultTextWidth = 20;
			
			filenameArea.css("resize", "none");
			filenameArea.attr("rows", 1);
			$("#uploaded-file").change(function () {
				
				/* Show the filename in the text area */
				
				if (fileInput[0].files[0] !== null) {
					console.log("# of files: " + fileInput[0].files.length);
					var fileNames = "";
					var maxLength = 0;
					var fileCount = fileInput[0].files.length;
					
					for (var file of fileInput[0].files) {
						fileNames += file.name + "\n";
						
						if (file.name.length > maxLength) {
							maxLength = file.name.length;
						}
					}
					
					filenameArea.attr("rows", fileCount)
					filenameArea.attr("cols", (maxLength + 2 * fileCount));
					filenameArea.val(fileNames);
					cancelBtn.removeClass("hidden");
					uploadBtn.attr("disabled", false);
					
				} else {
					cancelBtn.addClass("hidden");
					uploadBtn.attr("disabled", true);
				}
			});
			
			$("#file-cancel-btn").click(function () {
				setControlsToDefault(fileInput, uploadBtn, cancelBtn, filenameArea);
			});
			
			$("#file-upload-form").unbind("submit").bind("submit", function (event) {
				event.preventDefault();
				
				var formData = new FormData($(this)[0]);
				
				$.ajax({
					url: $scope.fileServiceUrl,
					type: 'POST',
					data: formData,
					xhrFields: {
						withCredentials: true
					},
					async: true,
					success: function (data) {
						setControlsToDefault(fileInput, uploadBtn, cancelBtn, filenameArea);
						cancelBtn.addClass("hidden");
						$scope.indicateSuccess("#file-upload-controls");
						
						$scope.getFiles();
					},
					error: function(jq, status, message) {
						$scope.indicateFailure("#file-upload-controls");
				        alert("[ERROR]: Files upload failure");
				    },
					
					cache: false,
					contentType: false,
					processData: false,
					crossDomain: true
				});
			});
			
			$scope.toggleFileShare = function (fileId, reservationId) {
				
				if ($scope.fileReservationMap[fileId][reservationId] == false) {
					/* Share file */
					
					$http({
						method: "POST",
						url: REST_API + "patients/" + $rootScope.userDetails.id + "/sharedFiles",
						headers: {
							"Content-Type": "application/json"
						},
						data: {
							fileEntryId: fileId,
							reservationId: reservationId
						}
					})
					.then(function successCallback(response) {
						$scope.fileReservationMap[fileId][reservationId] = true;
						$("#dropdown-" + fileId + " #reservation-" + reservationId).addClass("dropdown-item-selected");
						console.log("[INFO]: File has been successfully shared");
						
					}, function errorCallback(response) {
							$scope.indicateFailure("#toolbar-" + fileId);
			            	console.log("[ERROR]: Couldn't share the file");
		            });
				} else {
					/* Cancel already shared file */
					
					$http.delete(REST_API + "patients/" + $rootScope.userDetails.id + "/sharedFiles/file/" + fileId + "/reservation/" + reservationId)
					.then(function successCallback(response) {
						$scope.fileReservationMap[fileId][reservationId] = false;
						$("#dropdown-" + fileId + " #reservation-" + reservationId).removeClass("dropdown-item-selected");
						console.log("[INFO]: Share has been successfully cancelled");
						
					}, function errorCallback(response) {
							$scope.indicateFailure("#toolbar-" + fileId);
			            	console.log("[ERROR]: Couldn't share the file");
		            });
				}
			};
			
			$("html").on("click", function (e) {
				if ($(e.target).parents(".dropdown").length == 0) {
					$(".dropdown").each(function (index, val) {
						$(val).removeClass("open");
					});
				}
			});
		}

		$scope.openDropdownForFileId = function (fileId) {
			$("#dropdown-" + fileId).addClass("open");
			
			$scope.reservations.forEach(function (reservation, index) {
				console.log("fileId: "+ fileId + ", reservation: " + reservation.id + " -> " + $scope.fileReservationMap[fileId][reservation.id]);
				if ($scope.fileReservationMap[fileId][reservation.id] == true) {
					$("#dropdown-" + fileId + " #reservation-" + reservation.id).addClass("dropdown-item-selected");
				}
			});
		};
		
		$scope.deleteFile = function (fileId) {
			$http.delete(REST_API + "patients/" + $rootScope.userDetails.id + "/files/" + fileId)
			.then(function successCallback(response) {
				$scope.getFiles();
				console.log("File has been successfully deleted");
				
			}, function errorCallback(response) {
            	console.log("[ERROR]: Couldn't delete the file");
            });
		};
		
		$scope.getFiles();
		$scope.initListeners();
	}]);

function setControlsToDefault(fileInput, uploadBtn, cancelBtn, filenameArea) {
	fileInput.val("");
	uploadBtn.attr("disabled", true);
	cancelBtn.addClass("hidden");
	filenameArea.val("");
	filenameArea.attr("rows", 1);
	filenameArea.attr("cols", 20);
}