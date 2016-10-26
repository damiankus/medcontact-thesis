'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'myApp.login',
    'myApp.signUp',
    'myApp.reservation',
    'myApp.version'
])
// .constant('REST_API', "https://medcontact-api.herokuapp.com/")
    .constant('REST_API', "http://localhost:8080/")
    .config(['$locationProvider', '$routeProvider', function ($locationProvider, $routeProvider) {
            $locationProvider.hashPrefix('!');

            $routeProvider.otherwise({redirectTo: '/login'});

        }]
    );
