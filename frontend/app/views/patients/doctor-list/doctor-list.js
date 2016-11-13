'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/doctor-list', {
        templateUrl: 'views/patients/doctor-list/doctor-list.html',
        controller: 'DoctorListCtrl'
    });
}]);

myApp.controller('DoctorListCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
		$rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
        getDoctors();

        function getDoctors() {
            $http.get(REST_API + "doctors")
                .then(function successCallback(response) {
                        $scope.doctors = response.data;
                        console.log($scope.doctors);
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    }
                )
        }
    }]);