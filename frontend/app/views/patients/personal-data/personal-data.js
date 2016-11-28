'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/personal-data', {
        templateUrl: 'views/patients/personal-data/personal-data.html',
        controller: 'PersonalDataCtrl'
    });
}]);

myApp.controller('PersonalDataCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");

        $scope.changePersonalData = function () {
            var personalDataPassword = {
                firstName: $scope.userDetails.firstName,
                lastName: $scope.userDetails.lastName,
                email: $scope.userDetails.email,
                oldPassword: $scope.userDetails.oldPassword,
                newPassword1: $scope.userDetails.newPassword1,
                newPassword2: $scope.userDetails.newPassword2
            }

            $http.put(REST_API + "patients/" + $rootScope.userDetails.id, personalDataPassword)
                .then(function successCallback(response) {
                    console.log("Success");
                    var personalData = {
                        id: $rootScope.userDetails.id,
                        firstName: $scope.userDetails.firstName,
                        lastName: $scope.userDetails.lastName,
                        email: $scope.userDetails.email,
                        role: "PATIENT"
                    };
                    console.log($rootScope.userDetails);
                    UserService.setUser(personalData);
                    $rootScope.userDetails = UserService.getUser();
                    console.log($rootScope.userDetails);
                }, function errorCallback(response) {
                    console.log("[ERROR]: " + response.data.message);
                })
        }
    }]);