'use strict';

angular.module('myApp.fileUpload', ['ngRoute'])

.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/file-upload', {
        templateUrl: 'views/patients/file-upload/file-upload.html',
        controller: 'FileUploadCtrl'
    });
}])

.controller('FileUploadCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location',
    function (REST_API, $rootScope, $scope, $http, $location) {
	
		if ($rootScope.userDetails !== undefined
			&& $rootScope.userDetails.id !== undefined
			&& $rootScope.userDetails.id != null) {
			
			console.log("userDetailsfound");
			var user = $rootScope.userDetails;
			$scope.fileServiceUrl = REST_API + "patients/" + user.id + "/files";
			
			var fileInput = $("#uploaded-file");
			var cancelBtn = $("#file-cancel-btn");
			var filenameArea = $("#filename-area");
			var uploadBtn = $("#file-upload-btn");
			var defaultTextWidth = 20;
			
			filenameArea.css("resize", "none");
			filenameArea.attr("rows", 1);
			$("#uploaded-file").change(function () {
				
				/* Show the filename in the field */
				if (fileInput[0].files[0] !== null) {
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
				fileInput.val("");
				uploadBtn.attr("disabled", true);
				filenameArea.val("");
				filenameArea.attr("rows", 1);
				console.log(defaultTextWidth);
				filenameArea.attr("cols", defaultTextWidth);
			});
		} else {
			console.log("No user data has been found");
			$location.url("/");
		}
	}]);
