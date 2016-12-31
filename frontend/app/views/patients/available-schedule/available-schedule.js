'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/available-schedule/:doctorId', {
        templateUrl: 'views/patients/available-schedule/available-schedule.html',
        controller: 'AvailableScheduleCtrl'
    });
}]);

myApp.controller('AvailableScheduleCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService', 'TimeService', '$routeParams',
    function (REST_API, $rootScope, $scope, $http, $location, UserService, TimeService, $routeParams) {
        $rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
        getSchedule();
        $scope.emptySchedule = false;

        function getSchedule() {
            $http.get(REST_API + "doctors/" + $routeParams.doctorId + "/reservations/UNRESERVED")
                .then(function successCallback(response) {

                        response.data.forEach(function (schedule) {
                            schedule.startDateTime = TimeService.parseWithTimezone(schedule.startDateTime);
                            schedule.endDateTime = TimeService.parseWithTimezone(schedule.endDateTime);
                            schedule.day = moment(schedule.startDateTime).format("DD MM YYYY");
                        });
                        response.data = _.groupBy(response.data, function (schedule) {
                            return schedule.day;
                        });

                        $scope.schedules = [];
                        for (var key in response.data) {
                            if (response.data.hasOwnProperty(key)) {
                                $scope.schedules.push({key:new Date(moment(key, "DD MM YYYY")), values:response.data[key]})
                            }
                        }

                        if (!(typeof $scope.schedules !== 'undefined' && $scope.schedules.length > 0)) {
                            $scope.emptySchedule = true;
                        }
                    },
                    function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    }
                )
        }

        $scope.bookTerm = function (reservationId) {
            $http.put(REST_API + "patients/" + $rootScope.userDetails.id + "/reservations/" + reservationId)
                .then(function successCallback(response) {
                        console.log("success");
                        getSchedule();
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    }
                )
        }
    }]);