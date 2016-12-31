'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/doctor/personal-data', {
        templateUrl: 'views/doctors/personal-data/personal-data.html',
        controller: 'DoctorPersonalDataCtrl'
    });
}]);

myApp.controller('DoctorPersonalDataCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
        $rootScope.userDetails = UserService.getUserOrRedirect($location, "/login");
        $scope.badRequest = false;
        $scope.changeSuccess = false;
        
        (function () {
        	$("#specialty-input").keyup(function (e) {
        		if ($(this).val().length > 0) {
        			$http.get(REST_API + "doctors/specialties/like/" + $(this).val())
        			.then(function successCallback(response) {
        				$scope.suggestedSpecialties = response.data;
        				
        				if (response.data.length > 0) {
        					$("#specialties-dropdown").addClass("open");
        				} else {
        					$("#specialties-dropdown").removeClass("open");
        				}
        				
        			}, function errorCallback(response) {
        				console.log("[ERROR]: Couldn't load specialty suggestions");
        			});
        		}
        		
        		var event = window.event ? window.event : e;
        		if (e.keyCode == 40) {
        			$("#suggested-specialties-menu li:first").focus();
        		}
        	});
        	
			$("html").on("click", function (e) {
				if ($(e.target).parents(".dropdown").length == 0) {
					$(".dropdown").each(function (index, val) {
						$(val).removeClass("open");
					});
				}
			});
    	})();
    
        $scope.getDoctorInfo = function () {
            $http.get(REST_API + "doctors/" + $rootScope.userDetails.id)
            .then(function successCallback(response) {
                    $scope.doctorInfo = response.data;
                    
                }, function errorCallback(response) {
                    console.log("[ERROR]: " + response.data.message);
                }
            );
        }
        
        $scope.deleteSpecialty = function (index) {
        	$scope.doctorInfo.specialties.splice(index, 1);
        };
        
        $scope.addSpecialty = function (specialty) {
        	if ($scope.doctorInfo.specialties.indexOf(specialty) == -1) {
        		$scope.doctorInfo.specialties.push(specialty);
        	}
        	$("#specialty-input").val("");
        	$("#specialties-dropdown").removeClass("open");
        }
        
        $scope.addSpecialtyFromTextField = function () {
        	var specialty = $("#specialty-input").val()
        	if ($scope.doctorInfo.specialties.indexOf(specialty) == -1) {
        		$scope.doctorInfo.specialties.push(specialty);
        	}
        	$("#specialty-input").val("");
        	$("#specialties-dropdown").removeClass("open");
        }
        
        $scope.changePersonalData = function () {
            $http.put(REST_API + "doctors/" + $rootScope.userDetails.id, $scope.doctorInfo)
            .then(function successCallback(response) {
            	UserService.setUser($scope.doctorInfo);
                $rootScope.userDetails = $scope.doctorInfo;
                $scope.badRequest = false;
                $scope.changeSuccess = true;
                
            }, function errorCallback(response) {
                console.log("[ERROR]: " + response.data.message);
                $scope.badRequest = true;
                $scope.changeSuccess = false;
            });
        }
        
        $scope.getDoctorInfo();
    }]);