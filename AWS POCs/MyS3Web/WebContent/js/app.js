'use strict';

// Declare app level module which depends on views, and components

angular.module('myApp', ['ngRoute','myApp.controllers','mm.foundation','treeControl','ngTable','ngLoadingSpinner','ngUpload']).
config(['$routeProvider', function($routeProvider) {
	  $routeProvider.otherwise({redirectTo: '/view1'});
	}]);

