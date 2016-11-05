'use strict';

myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/reservation', {
        templateUrl: 'views/patients/reservation/reservation.html',
        controller: 'ReservationCtrl'
    });
}]);

myApp.controller('ReservationCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location',
    function (REST_API, $rootScope, $scope, $http, $location) {
    console.log("reservationController ", REST_API, $rootScope);
}]);