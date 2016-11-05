'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/doctor-list', {
        templateUrl: 'views/patients/doctor-list/doctor-list.html',
        controller: 'DoctorListCtrl'
    });
}]);

myApp.controller('DoctorListCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        getDoctors();


        function getDoctors() {
            $http.get(REST_API + "patients/" + $rootScope.userDetails.id + "/doctors")
                .then(function successCallback(response) {
                        console.log(response);
                        $scope.doctors = response.data;
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    }
                )
        }
    }]);