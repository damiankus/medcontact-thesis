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

        console.log("add-schedule", $rootScope);
    }]);