'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/add-schedule', {
        templateUrl: 'views/doctors/add-schedule/add-schedule.html',
        controller: 'AddScheduleCtrl'
    });
}]);

myApp.controller('AddScheduleCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $rootScope.userDetails = UserService.getUser();

        $(function () {
            $('input[name="daterange"]').daterangepicker({
                timePicker: true,
                timePickerIncrement: 15,
                timePicker24Hour: true,
                locale: {
                    format: 'DD-MM-YYYY HH:mm'
                }
            });
        });

        console.log("add-schedule", $rootScope);

        $scope.addSchedule = function () {
            if (/^(\d\d-\d\d-\d\d\d\d \d\d:\d\d - \d\d-\d\d-\d\d\d\d \d\d:\d\d)$/.test($scope.schedule)) {
                var start = new Date($scope.schedule.match(/\d\d-\d\d-\d\d\d\d \d\d:\d\d/g)[0]);
                var end = new Date($scope.schedule.match(/\d\d-\d\d-\d\d\d\d \d\d:\d\d/g)[1]);

                $http.post(REST_API + "doctors/" + $rootScope.userDetails.id + "/schedules", {start: start, end: end})
                    .then(function successCallback(response) {
                        console.log("Success")
                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                    })
            }

            console.log("Wrong format.");
        }
    }]);