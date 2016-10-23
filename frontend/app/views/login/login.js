'use strict';

angular.module('myApp.login', ['ngRoute'])
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'views/login/login.html',
    controller: 'LoginCtrl'
  });
}])

.controller('LoginCtrl', ['REST_API', '$scope', function(REST_API, $scope) {
  console.log("LOGGER: LoginCtr, api: " + REST_API);

  $scope.login = function () {
    console.log("make HTTP request");

  }

}]);