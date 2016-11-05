'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'views/login/login.html',
        controller: 'LoginCtrl'
    });
}]);

// .controller('LoginCtrl', ['REST_API', 'ROLE', '$rootScope', '$scope', '$http', '$location',
myApp.controller('LoginCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $scope.loginError = false;
        $scope.loggedIn = false;

        $scope.login = function () {
            var request = {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                url: REST_API + "login",
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                data: $scope.user
            };

            $http(request).then(function successCallback(response) {
                $http.get(REST_API + "who")
                    .then(function successCallback(identity) {
                        UserService.setUser(identity.data);
                        console.log("user: " + JSON.stringify(UserService.getUser()));
                        $rootScope.userDetails = UserService.getUser();
                        $scope.loginError = false;
                        $scope.loggedIn = true;
                        $location.url('/reservation');

                    }, function errorCallback(response) {
                        console.log("[ERROR]: " + response.data.message);
                        $scope.loginError = true;
                        $scope.loggedIn = false;
                    });

            }, function errorCallback(response) {
                console.log("[ERROR]: " + response.data.message);
                $scope.loginError = true;
                $scope.loggedIn = false;
            });
        }
    }]);