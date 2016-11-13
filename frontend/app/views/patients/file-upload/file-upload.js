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
		
		if ($rootScope.userDetails.id !== undefined
				&& $rootScope.userDetails.id !== null) {
			
			$scope.fileServiceUrl = REST_API + "patients/" + $rootScope.userDetails.id + "/files";
			
			var fileInput = $("#uploaded-file");
			var cancelBtn = $("#file-cancel-btn");
			var filenameArea = $("#filename-area");
			var uploadBtn = $("#file-upload-btn");
			var successIcon = $("#file-upload-success");
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
				var submittedFiles = [];
				var fileEntries = [];
				
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
						successIcon.fadeIn()
							.delay(500)
							.fadeOut()
							.delay(500);
					},
					error: function(jq, status, message) {
				        alert("[ERROR]: Files upload failure");
				    },
					
					cache: false,
					contentType: false,
					processData: false,
					crossDomain: true
				});
		});
			
		} else {
			console.log("No user data has been found");
			$location.url("/");
		}
		
		/* Load file info from the server */
		
		$http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/fileEntries")
			.then(function successCallback(response) {
				$scope.files = response.data._embedded.fileEntries;
				
			}, function errorCallback(response) {
				console.log("[ERROR]: " + response);
			});
	}]);

function setControlsToDefault(fileInput, uploadBtn, cancelBtn, filenameArea) {
	fileInput.val("");
	uploadBtn.attr("disabled", true);
	filenameArea.val("");
	filenameArea.attr("rows", 1);
	filenameArea.attr("cols", 20);
}