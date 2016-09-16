'use strict';

/* Controllers */


var app = angular.module('myApp.controllers', []).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/view1', {
				templateUrl : 'partials/home.html',
				controller : 's3Controller'
			}).when('/bucket', {
				templateUrl : 'partials/bucket.html',
				controller : 'bucketController'
			}).otherwise({
				redirectTo : '/view1'
			});
		} ]);




app.controller('s3Controller', function($scope, $modal, $log,$http,$filter, ngTableParams) {

	/* S3 tree  control  */
	$scope.rowCollection={};	
	getAllbucket($scope,$http);
	$scope.display='none';
	$scope.bucketLength={};
	$scope.FlagValue = false;
	$scope.folder={};
	function getAllbucket($scope,$http) {

		$http({
			method: 'POST',
			url: 'http://localhost:8080/S3Web/S3Tree',
			headers: {'Content-Type': 'application/json'},
		}).success(function (data){
			$scope.bucketCollection = data;
			$scope.bucketLength=data;
		});  
	}


	$scope.displayTreefolder = function(index) {
		if(document.getElementById("tree"+index).style.display=='flex'){
			document.getElementById("tree"+index).style='display: none; flex-direction: row';
		}else{
			document.getElementById("tree"+index).style='display: flex; flex-direction: row';
		}
	};

	$scope.folderBucket={};
	$scope.selectedfolderPath={};
	$scope.selectedbucket={};
	$scope.selectBucket = function(bucketName,index) {
		for(var i=0;i<$scope.bucketLength.length;i++){
			if(i==index){
				document.getElementById("bucketTitle"+i).style.backgroundColor='#aaddff';
			}else{
				document.getElementById("bucketTitle"+i).style.backgroundColor='#FFFFFF';
				document.getElementById("tree"+i).style.backgroundColor='#FFFFFF';
			}
		}
		$scope.folderBucket=bucketName;
		$scope.selectedbucket=bucketName;
		$scope.selectedfolderPath='';
		$http({
			method: 'POST',
			url: 'http://localhost:8080/S3Web/SBucket?operation=selectBucket&bucketName='+bucketName,
			headers: {'Content-Type': 'application/json'},
		}).success(function (data)	{
			$scope.rowCollection = data;
		});  

	};


	$scope.selectTreeFolder = function(index,sel,bucketName) {
		//alert(JSON.stringify(sel));
		//$scope.selected=sel;
		if(!$scope.spinnerActive){
			for(var i=0;i<$scope.bucketLength.length;i++){
				if(i==index){
					document.getElementById("bucketTitle"+i).style.backgroundColor='#aaddff';
				}else{
					document.getElementById("bucketTitle"+i).style.backgroundColor='#FFFFFF';
				}
			}
			$scope.selectedbucket=bucketName;
			if(sel.data.id==bucketName){

				$scope.selectedfolderPath = (sel.data.key+"/");
			}else
				if(sel.data.id.indexOf("/")==-1){
					$scope.selectedfolderPath = sel.data.id+"/"+sel.data.key+"/";
				}else{
					$scope.selectedfolderPath = sel.data.id+sel.data.key+"/";
				}
			$scope.rowCollection={};

			$http({
				method: 'POST',
				url: 'http://localhost:8080/S3Web/SBucket?operation=displayTableFolder&NodeId='+sel.data.id+'&NodeKey='+sel.data.key+'&bucketName='+bucketName,
				headers: {'Content-Type': 'application/json'},
			}).success(function (data){
				$scope.rowCollection = data;
				if ($scope.tableParams){
					$scope.tableParams.reload();
				} 
				$scope.tableParams = new ngTableParams({
					page: 1,
					count: 5, // count per page
					sorting: {
						name: 'asc'     // initial sorting
					} 

				}, {
					total:  $scope.rowCollection.length, // length of data

					getData: function($defer, params) {
						// var data = $scope.rowCollection;
						var orderedData =  $filter('orderBy')($scope.rowCollection, params.orderBy()) ;
						//  alert("result="+JSON.stringify(orderedData)); 
						//  $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));   
						$scope.rowCollection=orderedData; 
						$defer.resolve($scope.rowCollection);

					}
				});



			}); 
		}
	};


	$scope.opts = {
			nodeChildren : "children",
			dirSelectable : true,
			isLeaf: function(node) {
				return node.i % 2 == 0;
			}

	};


	$scope.refresh = function(){
		getAllbucket($scope,$http);	
	};


	/*createSubTree($http,$scope);
	function createSubTree($http,$scope) {
		$scope.bucketTree={};
		$http({
			method : 'POST',
			url : 'http://localhost:8080/S3Web/S3Tree',
			headers : {
				'Content-Type' : 'application/json'
			},
			data : $scope.bucketTree
		}).success(function(data) {
			$scope.treedata=data;

		});


	}*/


	/* S3 tree  control  End */

	/* S3 table control   */

	$scope.tableFolder = function(folderName,parent,bucketName) {

		$http({
			method: 'POST',
			url: 'http://localhost:8080/S3Web/SBucket?operation=tableFolder&bucketName='+bucketName+'&Parent='+parent+'&NodeKey='+folderName,
			headers: {'Content-Type': 'application/json'},
		}).success(function (data)	{
			$scope.rowCollection = data;
		});  
	};

	$scope.myfunction = function (data) {
		$( "#resizable" ).resizable({
			maxHeight: 750,
			maxWidth: 370,
			minHeight: 250,
			minWidth: 200
		});
	};

	$scope.openBucket = function() {

		var modalInstance = $modal.open({
			templateUrl : 'myModalContent.html',
			controller : 'ModalInstanceCtrl',
			resolve : {
				items : function() {
					return $scope.treedata;
				}
			}
		});

		modalInstance.result.then(function($http) {
			$http({
				method : 'POST',
				url : 'http://localhost:8080/S3Web/S3Tree',
				headers : {
					'Content-Type' : 'application/json'
				},
				data : $scope.bucketTree
			}).success(function(data) {
				$scope.bucketCollection = data;
			});

		}, function() {
			$log.info('Modal dismissed at: ' + new Date());
		});
	};

//	set Flag Variable
	$scope.setFlag = function(boolean){
		$scope.FlagValue = boolean;
	};

	//Folder Creation 
	$scope.createFolder = function(){
		if(Object.keys($scope.selectedbucket).length>0){
			$http({
				method : 'POST',
				url : 'http://localhost:8080/S3Web/createFolder?bucketName='+$scope.selectedbucket+'&foldername='+$scope.selectedfolderPath,
				headers : {
					'Content-Type' : 'application/json'
				},
				data : $scope.folder
			}).success(function(data) {
				getAllbucket($scope,$http);	
				$scope.rowCollection = data;
				$scope.FlagValue=false;
			});
		}else {
			alert("Please select bucket ");
		}
	};


	//Object Upload
	$scope.uploadFile = function() {

		alert("Inside Upload function");

		alert("folderBucket: "+$scope.folderBucket+"selectedBucket:   "+$scope.selectedbucket+" selectedFolderPath: "+$scope.selectedfolderPath);

		if(Object.keys($scope.selectedbucket).length==0){
			$scope.items = [$scope.folderBucket];

		}else{
			$scope.items = [$scope.selectedbucket,$scope.selectedfolderPath];
		}

		alert("scope items: "+$scope.items);

		$scope.selectedbucket={};
		$scope.selectedfolderPath={};
		$scope.folderBucket={};

		if(Object.keys($scope.items).length>0){

			//alert("hi=="+JSON.stringify($scope.items));

			var modalInstance = $modal.open({
				templateUrl : 'uploadModelContent.html',
				controller : 'UploadModalCtrl',
				resolve : {
					items : function() {
						return $scope.items;
					}
				}
			});
		}	else{
			alert("Please select bucket or folder");
		}
	};


	// Object deletion
	$scope.deleteObject = function() {

		if(Object.keys($scope.selectedbucket).length==0){
			$scope.items = [$scope.folderBucket];

		}else{
			$scope.items = [$scope.selectedbucket,$scope.selectedfolderPath];
		}



		$scope.selectedbucket={};
		$scope.selectedfolderPath={};
		$scope.folderBucket={};
		if(Object.keys($scope.items).length==0){

			//alert("hi=="+JSON.stringify($scope.items));


			var modalInstance = $modal.open({
				templateUrl : 'deleteObjectModelContent.html',
				controller : 'ObjectDeletionModalCtrl',
				resolve : {
					items : function() {
						return $scope.items;
					}
				}
			});

			modalInstance.result.then(function($http) {
				//$scope.bucketTree={};
				$http({
					method : 'POST',
					url : 'http://localhost:8080/S3Web/S3Tree',
					headers : {
						'Content-Type' : 'application/json'
					},
					data : $scope.bucketTree
				}).success(function(data) {
					$scope.bucketCollection = data;
					$scope.rowCollection ={};
				});

			}, function() {
				$log.info('Modal dismissed at: ' + new Date());
			});

		}	else{
			alert("Please select bucket or folder");
		}
	};


});

//Please note that $modalInstance represents a modal window (instance)
//dependency.
//It is not the same as the $modal service used above.

app.controller('ModalInstanceCtrl', function($scope, $modalInstance, items,
		$http) {
	$scope.bucket = {};
	$scope.createBucket = function() {
		$http({
			method : 'POST',
//			url : 'http://localhost:8080/S3Web/SBucket?operation=create',
			headers : {
				'Content-Type' : 'application/json'
			},
			data : $scope.bucket
		}).success(function(data) {
			$modalInstance.close($http);
		});

	};

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

});

//Object Deletion model panel
app.controller('ObjectDeletionModalCtrl', function($scope, $modal, $modalInstance, items,
		$http) {
	//alert("Inside Delete Object Controller!!");
	$scope.bucket = {};
	var bucketName='';
	var folderpath='';
	if(angular.isUndefined(items[1])){
		bucketName=items[0];
		folderpath='';	
	}else{
		bucketName=items[0];
		folderpath=items[1];
	}

	$scope.folder={};
	$scope.deleteObj = function() {
		$http({
			method : 'POST',
			url : 'http://localhost:8080/S3Web/deleteObject?bucketName='+bucketName+'&foldername='+folderpath,
			headers : {
				'Content-Type' : 'application/json'
			},
			data : $scope.folder
		}).success(function(data) {

			//alert("Data Is: " +data);

			$scope.modalInstance = $modal.open({
				templateUrl : 'displayTextModelContent.html',
				controller : 'DisplayTextModalCtrl',
				resolve : {
					items : function() {
						return data;
					}
				}
			});

			$modalInstance.close($http);
		});

	};

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

});

//Object Upload model panel
app.controller('UploadModalCtrl', function($scope, $modal, $modalInstance, items,
		$http) {
	alert("Inside Upload Object Controller!!");
	$scope.bucket = {};
	var bucketName='';
	var folderpath='';
	var formdata = new FormData();
	formdata.append( 'file', $scope.file );
	if(angular.isUndefined(items[1])){
		bucketName=items[0];
		folderpath='';	
	}else{
		bucketName=items[0];
		folderpath=items[1];
	}

	$scope.folder={};
	$scope.uploadObj = function() {
		alert("Inside Upload!!");
		$http({
			method : 'POST',
			url : 'http://localhost:8080/S3Web/upload?bucketName='+bucketName+'&foldername='+folderpath,
			data:formdata,
			headers : {
				'Content-Type' : 'multipart/form-data'
			},
			transformRequest: function(data, headersGetterFunction) {
		        return data; // do nothing! FormData is very good!
		    },
			 
		}).success(function(data) {

			alert("Data Is: " +data);

			$scope.modalInstance = $modal.open({
				templateUrl : 'displayTextModelContent.html',
				controller : 'DisplayTextModalCtrl',
				resolve : {
					items : function() {
						return data;
					}
				}
			});

			$modalInstance.close($http);
		});

	};

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

});
//Folder model panel
app.controller('DisplayTextModalCtrl', function($scope, $modalInstance, items,
		$http) {
	$scope.textDisplay = {};
	$scope.textDisplay = items;
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

});


//Use to  resizr div
window.onload = function () {

	setTimeout(function() {
		angular.element(document.getElementById('s3Control')).scope().myfunction('treeResize');    
	}, 1000);

};


