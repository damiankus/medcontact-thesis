'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/add-schedule', {
        templateUrl: 'views/doctors/add-schedule/add-schedule.html',
        controller: 'AddScheduleCtrl'
    });
}]);

var isValidDate = function (date) {
    if (Object.prototype.toString.call(date) === "[object Date]") {
        // it is a date
        if (isNaN(date.getTime())) {  // d.valueOf() could also work
            console.error("date is not valid");
            return false;
        }
        else {
            // date is valid
            return true;
        }
    }
    else {
        console.error("it is not a date");
        return false;
    }
};

myApp.controller('AddScheduleCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
        getSchedule();
        $scope.emptySchedule = false;

        function getSchedule() {
            $http.get(REST_API + "doctors/" + $rootScope.userDetails.id + "/reservations/UNRESERVED")
                .then(function successCallback(response) {
                        response.data.forEach(function (schedule) {
                            schedule.startDateTime = new Date(schedule.startDateTime);
                            schedule.endDateTime = new Date(schedule.endDateTime);
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


        $scope.addSchedule = function () {
            moment.locale('pl');
            var start = new Date(moment($scope.date + " " + $scope.startTime, "D MMMM YYYY H:mm"));
            var end = new Date(moment($scope.date + " " + $scope.endTime, "D MMMM YYYY H:mm"));

            if (isValidDate(start) && isValidDate(end)) {
                $http.post(REST_API + "doctors/" + $rootScope.userDetails.id + "/reservation", {
                    start: start,
                    end: end
                })
                    .then(function successCallback(response) {
                        getSchedule()
                        console.log("Success")
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    })
            }
            else {
                console.error("Wrong data", $scope.date, $scope.startTime, $scope.endTime)
            }

        };


        $(function () {
            $('#datePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'D MMMM YYYY',
                minDate:Date.now(),
            }).on("dp.change", function () {
                $scope.date = $("#datePicker").val();
            });

            $('#startTimePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'H:mm',
                maxDate:(new Date()).setHours(23, 59, 59, 0)
            }).on("dp.change", function (e) {
                $scope.startTime = $("#startTimePicker").val();
                $('#endTimePickerDiv').data("DateTimePicker").minDate(e.date)
            });

            $('#endTimePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'H:mm',
                minDate:Date.now(),
                maxDate:(new Date()).setHours(23, 59, 59, 0)
            }).on("dp.change", function (e) {
                $scope.endTime = $("#endTimePicker").val();
                $('#startTimePickerDiv').data("DateTimePicker").maxDate(e.date);
            });
        });
    }]);