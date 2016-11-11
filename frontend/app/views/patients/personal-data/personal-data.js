'use strict';


myApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/personal-data', {
        templateUrl: 'views/patients/personal-data/personal-data.html',
        controller: 'PersonalDataCtrl'
    });
}]);

myApp.controller('PersonalDataCtrl', ['REST_API', "$rootScope", '$scope', '$http', '$location', 'UserService',
    function (REST_API, $rootScope, $scope, $http, $location, UserService) {
		$rootScope.userDetails = UserService.getUserOrRedirect($location, 'login');
		
		$scope.updateInputs = function () {
			$('#user-data-form input[type=text], input[type=email]').each(function () {
				$(this).val($rootScope.userDetails[$(this).attr('name')]);
			});
		}
		
		$scope.updateInputs();
		
		$('#restore-btn').click(function () {
			$scope.updateInputs();
		});
		
		$('#user-data-form').submit(function (event) {
//			event.preventDefault();
			
		});
    }]);