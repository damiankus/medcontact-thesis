'use strict';

angular.module('myApp.reservation', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/reservation', {
            templateUrl: 'views/patients/reservation/reservation.html',
            controller: 'ReservationCtrl'
        });
    }])

    .controller('ReservationCtrl', ['REST_API', '$rootScope', '$scope', '$http', '$location', function (REST_API, $rootScope, $scope, $http, $location) {
        console.log("reservation " + REST_API, $scope);
        console.log($rootScope)


    }]);