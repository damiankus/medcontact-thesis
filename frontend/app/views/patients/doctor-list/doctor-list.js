'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/doctor-list', {
        templateUrl: 'views/patients/doctor-list/doctor-list.html',
        controller: 'DoctorListCtrl'
    });
}]);

myApp.controller('DoctorListCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
<<<<<<< HEAD
        $rootScope.userDetails = UserService.getUserOrRedirect($location, 'login');
        
		getDoctors();
        $scope.a = "a";
=======
        getDoctors();
>>>>>>> 97350675f24519b154d746fc8d9fdbaa70815fd6

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