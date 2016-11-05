'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/personal-data', {
        templateUrl: 'views/patients/personal-data/personal-data.html',
        controller: 'PersonalDataCtrl'
    });
}]);

myApp.controller('PersonalDataCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {

    }]);