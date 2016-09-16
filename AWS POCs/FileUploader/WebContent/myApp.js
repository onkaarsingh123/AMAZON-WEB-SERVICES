var mainApp = angular.module("mainApp", []);

mainApp.controller('myController', function($scope) {


angular.element(document).ready(function() {
	
	console.log("welcome");
	
	angular.element('#uploadForm').ajaxForm({

		beforeSubmit : function(arr, $form, options) {

		

			console.log("hiii");
			
			arr[1].value = "rohit";
			arr[2].value = "ihare";
			
		},
		beforeSend : function() {
			console.log("hiii1");
		},

		uploadProgress : function(event, position, total, percentComplete) {
			
			console.log("hiii2");
		},
		success : function() {

			
		},
		complete : function(xhr) {

			

		

		}

	});

});
});