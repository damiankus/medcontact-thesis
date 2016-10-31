'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'myApp.login',
    'myApp.logout',
    'myApp.signUp',
    'myApp.reservation',
    'myApp.fileUpload',
    'myApp.version'
])
// .constant('REST_API', "https://medcontact-api.herokuapp.com/")
    .constant('REST_API', "http://localhost:8080/")
    .config(['$locationProvider', '$routeProvider', '$httpProvider', 
             	function ($locationProvider, $routeProvider, $httpProvider) {
            $locationProvider.hashPrefix('!');
            $routeProvider.otherwise({redirectTo: '/login'});
            $httpProvider.defaults.withCredentials = true;
        }]
    );
