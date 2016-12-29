'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/doctor/personal-data', {
        templateUrl: 'views/doctors/personal-data/personal-data.html',
        controller: 'DoctorPersonalDataCtrl'
    });
}]);

myApp.controller('PersonalDataCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
        $scope.badRequest = false;
        $scope.changeSuccess = false;
        
        $scope.changePersonalData = function () {
            var doctorData = {
                    firstName: $scope.userDetails.firstName,
                    lastName: $scope.userDetails.lastName,
                    email: $scope.userDetails.email,
                    university: $scope.userDetails.userDetails.university,
                    title: $scope.userDetails.title,
                    biography: $scope.userDetails.biography,
                    oldPassword: $scope.userDetails.oldPassword,
                    newPassword1: $scope.userDetails.newPassword1,
                    newPassword2: $scope.userDetails.newPassword2
            };
        	
            $http.put(REST_API + "doctors/" + $rootScope.userDetails.id, doctorData)
                .then(function successCallback(response) {
                    console.log("Data changed");
                    
                    console.log($rootScope.userDetails);
                    UserService.setUser(doctorData);
                    $rootScope.userDetails = UserService.getUser();
                    
                    console.log($rootScope.userDetails);
                    $scope.badRequest = false;
                    $scope.changeSuccess = true;
                    
                }, function errorCallback(response) {
                    console.log("[ERROR]: " + response.data.message);
                    $scope.badRequest = true;
                    $scope.changeSuccess = false;
                })
        }
    }]);