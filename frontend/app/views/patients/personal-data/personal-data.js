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

            $http.post(REST_API + "patients/" + $rootScope.userDetails.id + "/personal-data", personalDataPassword)
                .then(function successCallback(response) {
                    console.log("Success")
                    var personalData = {
                        id: $rootScope.userDetails.id,
                        firstName: $scope.userDetails.firstName,
                        lastName: $scope.userDetails.lastName,
                        email: $scope.userDetails.email
                    }
                    UserService.setUser(personalData)
                    $rootScope.userDetails = personalData;
                }, function errorCallback(response) {
                    console.log("[ERROR]: " + response.data.message);
                })
        }
    }]);