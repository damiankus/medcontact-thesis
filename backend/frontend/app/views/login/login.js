
'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'views/login/login.html',
        controller: 'LoginCtrl'
    });
}]);

myApp.controller('LoginCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $scope.loginError = false;
        $scope.loggedIn = false;
        if ($rootScope.resetPassword == true) {
            $scope.resetPassword = true;
            $rootScope.resetPassword = false;
        } else {
            $scope.resetPassword = false;
        }

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
                        $scope.resetPassword = false;
                        switch ($rootScope.userDetails.role){
                            case 'DOCTOR':
                                $location.url('/add-schedule');
                                break;
                            case 'ADMIN':
                                console.log("a");
                                $location.url('/add-doctor');
                                break;
                            case 'PATIENT':
                                $location.url('/reservation');
                                break;
                            default:
                                console.error("Unknown user role: ", $rootScope.userDetails.role)
                        }
                    }, function errorCallback(response) {
                        console.log("[ERROR]: Login error");
                        console.log(response);
                        
                        $scope.loginError = true;
                        $scope.loggedIn = false;
                        $scope.resetPassword = false;
                    });

            }, function errorCallback(response) {
                console.log("[ERROR]: " + response.data.message);
                $scope.loginError = true;
                $scope.loggedIn = false;
                $scope.resetPassword = false;
            });
        }
    }]);