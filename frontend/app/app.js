'use strict';

// Declare app level module which depends on views, and components
var myApp = angular.module('myApp', ['ngRoute'])
// .constant('REST_API', "https://medcontact-api.herokuapp.com/")
    .constant('REST_API', "http://localhost:8080/")
    .constant('WS_ENDPOINT', 'ws://localhost:8080/ws/websocket')
    .config(['$locationProvider', '$routeProvider', '$httpProvider',
        function ($locationProvider, $routeProvider, $httpProvider) {
            $locationProvider.hashPrefix('!');
            $routeProvider.otherwise({redirectTo: '/login'});
            $httpProvider.defaults.withCredentials = true;
        }]
    )
    .factory('UserService', function () {
        var user = {};
        var accessor = {};

        if (window.sessionStorage) {
            accessor = {
                setUser: function (details) {
                    window.sessionStorage.setItem("userDetails", JSON.stringify(details));
                },

                getUser: function () {
                    return JSON.parse(window.sessionStorage.getItem("userDetails"));
                }
            }
        } else {
            accessor = {
                setUser: function (details) {
                    user = details;
                },

                getUser: function () {
                    return user;
                }
            }
        }

        accessor.updateScope = function (scope) {
            for (var prop in user) {
                scope.user[prop] = user[prop];
            }
        };
        
        accessor.getUserOrRedirect = function (location, url) {
        	var user = accessor.getUser();
        	
        	if (user === undefined
    			|| user === null) {
        		
        		location.url(url);
        	}
        	
        	return user;
        };

        return accessor;
    });
