'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/add-doctor', {
        templateUrl: 'views/admin/add-doctor/add-doctor.html',
        controller: 'AddDoctorCtrl'
    });
}]);

myApp.controller('AddDoctorCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
        $scope.emailTaken = false;
        $scope.addDoctorError = false;
        $scope.showSuccessMessage = false;

        $scope.addDoctor = function () {
            console.log($scope.doctor);
            
            $http.post(REST_API + "admins/" + $rootScope.userDetails.id + "/doctors/", $scope.doctor)
                .then(function successCallback(response) {
                	console.log("ADDED");
                	$scope.emailTaken = false;
                    $scope.addDoctorError = false;
                    $scope.showSuccessMessage = true;
                    
                }, function errorCallback(response) {
                	console.log("response");
                	console.log(response);
                	
                	$scope.addDoctorError = true;
                	$scope.showSuccessMessage = false;
                	$scope.emailTaken = false;
                	
                	if (response.status == 400) {
                		$scope.emailTaken = true;
                		$scope.addDoctorError = false;
                		
                		console.error("[ERROR]: Email taken");
                		
                	} else if (response.status == 503) {
                		$scope.emailTaken = false;
                		$scope.addDoctorError = true;
                		
                		console.error("[ERROR]: Couldn't create room for a doctor");
                		
                	} else if (response.status == 404) {
                		$scope.emailTaken = false;
                		$scope.addDoctorError = true;
                		
                		console.error("[ERROR]: Service not found");
                	}
                	
                })
        };
    }]);