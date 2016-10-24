'use strict';

angular.module('myApp.login', ['ngRoute'])
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'views/login/login.html',
    controller: 'LoginCtrl'
  });
}])

.controller('LoginCtrl', ['REST_API', '$scope', '$http', function(REST_API, $scope, $http) {
  console.log("LOGGER: LoginCtr, api: " + REST_API);

  $scope.login = function () {
    console.log("POST " + REST_API + "signup/save", $scope.user)

    $http(
        {
          method: "POST",
          url: REST_API + "login",
          data: $scope.user
        }
    ).then(onSuccess(), onFailure());

    function onSuccess(response) {
      console.log("SUCCESS ", response)
    }

    function onFailure(response) {
      console.log("FAILURE", response)
    }

  }

}]);