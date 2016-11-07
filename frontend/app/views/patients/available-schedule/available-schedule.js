'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/available-schedule/:doctorId', {
        templateUrl: 'views/patients/available-schedule/available-schedule.html',
        controller: 'AvailableScheduleCtrl'
    });
}]);

myApp.controller('AvailableScheduleCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService', '$routeParams',
    function (REST_API, $rootScope, $scope, $http, $location, UserService, $routeParams) {
        getSchedule();

        function getSchedule() {
            $http.get(REST_API + "doctors/" + $routeParams.doctorId + "/schedules")
                .then(function successCallback(response) {
                        $scope.schedules = response.data;
                        $scope.schedules.forEach(function(schedule){
                       schedule.startDateTime = new Date(schedule.startDateTime);
                       schedule.endDateTime = new Date(schedule.endDateTime);
                    });
                        console.log($scope.schedules, $scope.schedules[0]);
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    }
                )
        }
    }]);