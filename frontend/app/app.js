'use strict';

// Declare app level module which depends on views, and components
var myApp = angular.module('myApp', ['ngRoute'])
<<<<<<< HEAD
 .constant('REST_API', "https://medcontact-api.herokuapp.com/")
//    .constant('REST_API', "http://localhost:8080/")
=======
// .constant('REST_API', "https://medcontact-api.herokuapp.com/")
    .constant('REST_API', "http://localhost:8080/")
>>>>>>> 916d4433a442e64b52bdce8dbfe8b7c690a84158
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
        	var userDetails = accessor.getUser();
        	
        	if (userDetails === undefined || userDetails === null) {
        		location.url(url);
        		
        	} else {
        		return userDetails;
        	}
        };

        return accessor;
    });
