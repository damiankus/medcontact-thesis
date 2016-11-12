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
        $rootScope.userDetails = UserService.getUser();

        $scope.addSchedule = function () {
            moment.locale('pl');
            var start = new Date(moment($scope.date + " " + $scope.startTime, "D MMMM YYYY H:mm"));
            var end = new Date(moment($scope.date + " " + $scope.endTime, "D MMMM YYYY H:mm"));

            if (isValidDate(start) && isValidDate(end)) {
                $http.post(REST_API + "doctors/" + $rootScope.userDetails.id + "/schedules", {
                    start: start,
                    end: end
                })
                    .then(function successCallback(response) {
                        console.log("Success")
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    })
            }
            else{
                console.error("Wrong data", $scope.date, $scope.startTime, $scope.endTime)
            }

        };


        $(function () {
            $('#datePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'D MMMM YYYY'
            }).on("dp.change", function () {
                $scope.date = $("#datePicker").val();
            });

            $('#startTimePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'H:mm'
            }).on("dp.change", function () {
                $scope.startTime = $("#startTimePicker").val();
            });

            $('#endTimePickerDiv').datetimepicker({
                locale: 'pl',
                format: 'H:mm'
            }).on("dp.change", function () {
                $scope.endTime = $("#endTimePicker").val();
            });
        });
    }]);