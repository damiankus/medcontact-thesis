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
        $scope.addDoctorError = false;
        $scope.showSuccessMessage = false;

        $scope.addDoctor = function () {
            console.log($scope.doctor)
            $http.post(REST_API + "admins/doctors/", $scope.doctor)
                .then(function successCallback(response) {
                    $scope.addDoctorError = false;
                    $scope.showSuccessMessage = true;
                }, function errorCallback(response) {
                    console.error("[ERROR]: " + response.data.message);
                    $scope.addDoctorError = true;
                    $scope.showSuccessMessage = false;
                })
        };
    }]);