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

        $(function() {
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
            console.log($scope.schedule);
        }
    }]);