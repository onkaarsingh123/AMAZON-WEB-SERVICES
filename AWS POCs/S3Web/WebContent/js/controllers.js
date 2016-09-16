'use strict';

/* Controllers */

var app = angular.module('myApp.controllers', []).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/login', {
				templateUrl : 'partials/login.html',
				controller : 'formCtrl'
			}).when('/view1', {
				templateUrl : 'partials/home.html',
				controller : 's3Controller'
			}).otherwise({
				redirectTo : '/login'
			});
		} ]);

app.controller('formCtrl', function($scope,$http) {
	
	 $scope.formData = {};
	 $scope.Login = function() {
	console.log($scope.formData.uName+$scope.formData.password);
	var url = '/ldapCheck';
  $http({
		method: 'POST',
		url: '/S3Web/ldapCheck',
		data:$.param($scope.formData),
		headers: {'Content-Type': 'application/x-www-form-urlencoded' },
	}).success(function (data){
		console.log("Yipee...!!");
		window.location = '#/view1';
		
	}); 
	 }
});


app.controller('s3Controller', function($scope, $modal, $log,$http,$filter, ngTableParams) {

	/* S3 tree  control  */
	$scope.rowCollection={};	
	getAllbucket($scope,$http);
	$scope.display='none';
	$scope.bucketLength={};
	$scope.FlagValue = false;
	$scope.folder={};
	$scope.bucketNameDisplay="";
	$scope.folderNameDisplay= "";
	$scope.checkboxes = {};
	$scope.items = [];
	
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
		$scope.bucketNameDisplay=$scope.selectedbucket;
		$scope.folderNameDisplay = "";
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
			$scope.folderNameDisplay = $scope.selectedfolderpath;
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
			$scope.folderNameDisplay = parent;
			$scope.selectedfolderPath = parent;
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
		if($scope.selectedbucket){
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

		console.log("Upload File called..!");
		if(Object.keys($scope.selectedbucket).length==0){
			$scope.items = [$scope.folderBucket];
			console.log("In If..."+$scope.items);

		}else{
			console.log("In Else..."+$scope.items);
			$scope.items = [$scope.selectedbucket,$scope.folderNameDisplay];
		}
		
		if($scope.selectedbucket && $scope.selectedbucket != ""){
			console.log($scope.items);
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
		$scope.selectedbucket={};
		$scope.selectedfolderPath={};
		$scope.folderBucket={};

	};	
	


	// Object deletion
	$scope.deleteObject = function() {
		
//		if(Object.keys($scope.selectedbucket).length==0){
//			$scope.items = [$scope.folderBucket];
//			console.log("Inside If.."+$scope.selectedbucket);
//
//		}else{
//			$scope.items = [$scope.selectedbucket,$scope.selectedfolderPath];
//			console.log("Inside else.."+$scope.selectedbucket);
//		}
		
		console.log("Inside delete.."+$scope.selectedbucket);
		var type="";
		if($scope.rowCollection)
			{
				
				angular.forEach($scope.rowCollection, function(item) {
					//console.log("Inside Each Loop"+angular.isUndefined($scope.checkboxes.items));
					if(angular.isUndefined($scope.checkboxes.items))
					{
						type = "bucket";
						return;
					}	
					else
						if($scope.checkboxes.items[item.key] == true) {
							$scope.items.push(item);
							console.log(item)
							type = "files";
														}
	         });
				console.log(Object.keys($scope.items).length+"... Type... "+type);
			}
		
			
				console.log("deleting this bucket");
			
		

		//$scope.selectedbucket={};
		$scope.selectedfolderPath={};
		$scope.folderBucket={};
		
		
		if(type == "files"){
			console.log("after delete "+type)
			//alert("hi=="+JSON.stringify($scope.items));


			var modalInstance = $modal.open({
				templateUrl : 'deleteObjectModelContent.html',
				controller : 'ObjectDeletionModalCtrl',
				resolve : {
				items : function() {
					console.log(Object.keys($scope.items).length);
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
					$scope.checkboxes = {};
	                $scope.items = [];
				});

			}, function() {
				$log.info('Modal dismissed at: ' + new Date());
			});

		}
		else if($scope.selectedbucket)
			{
				console.log("Inside Bucket..."+type)
				var modalInstance = $modal.open({
					templateUrl : 'deleteBucketModelContent.html',
					controller : 'BucketDeletionModalCtrl',
					resolve : {
					data : function() 
						{				console.log($scope.selectedbucket)
										return $scope.selectedbucket;
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
						$scope.checkboxes = {};
		                $scope.items = [];
					});

				}, function() {
					$log.info('Modal dismissed at: ' + new Date());
				});
				
				
				
				
			}
		
		else{
			alert("Please select bucket or folder in delete"+$scope.selectedbucket
					);
		}
	};


});

//Object Upload model panel
app.controller('UploadModalCtrl', function($scope, $modal, $modalInstance, items,$http) {
	$scope.bucket = {};
	$scope.uploadProgress = 0;
	var bucketName='';
	var folderpath='';
	
	console.log("Inside upload Modal..");
	$scope.creds = {
	  bucket: '',
	  access_key: 'AKIAIDW3GDK2ZPLDFR6Q',
	  secret_key: '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'
	};
	var formdata = new FormData();
	formdata.append( 'file', $scope.file );
	if(items[1] == ""){
		bucketName=items[0];
		folderpath='';	
		$scope.creds.bucket = bucketName;
	}else{
		bucketName=items[0];
		folderpath=items[1];
		$scope.creds.bucket = bucketName+"/"+folderpath;
		if($scope.creds.bucket.charAt($scope.creds.bucket.length-1) == '/'){
			$scope.creds.bucket = $scope.creds.bucket.slice(0, -1);
		}
	}

	$scope.folder={};
	$scope.upload = function() {
	  // Configure The S3 Object 
      $modalInstance.close();
	  AWS.config.update({ accessKeyId: $scope.creds.access_key, secretAccessKey: $scope.creds.secret_key });
	  AWS.config.region = 'us-east-1';
	  var bucket = new AWS.S3({ params: { Bucket: $scope.creds.bucket } });
	 
	  console.log("Uploading...!!");
	  if($scope.file) {
	    var params = { Key: $scope.file.name, ContentType: $scope.file.type, Body: $scope.file };
	 
	    bucket.putObject(params, function(err, data) {
	      if(err) {
	        // There Was An Error With Your S3 Config
	        //alert(err.message);
	    	toastr.error(err.message,err.code);
	        return false;
	      }
	      else {
	        // Success!
	        //alert('Upload Done');
	    	toastr.success('File Uploaded Successfully', 'Done');
	        setTimeout(function() {
              $scope.uploadProgress = 0;
              $scope.$apply();
            }, 4000);
	      }
	    })
	    .on('httpUploadProgress',function(progress) {
	          // Log Progress Information
	    	$scope.uploadProgress = Math.round(progress.loaded / progress.total * 100);
	    	$scope.$apply();
	    	console.log($scope.uploadProgress + '% done');
	    });
	  }
	  else {
	    // No File Selected
	    //alert('No File Selected');
		toastr.error('Please select a file to upload');
	  }
	};
	$scope.$watch('$scope.uploadProgress',function(newValue,oldValue){
		$scope.uploadProgressView = newValue;
	});

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

//Please note that $modalInstance represents a modal window (instance)
//dependency.
//It is not the same as the $modal service used above.

app.controller('ModalInstanceCtrl', function($scope, $modalInstance, items,
		$http) {
	$scope.bucket = {};
	$scope.createBucket = function() {
		$http({
			method : 'POST',
			url : 'http://localhost:8080/S3Web/SBucket?operation=create',
			headers : {
				'Content-Type' : 'application/json'
			},
			data : $scope.bucket
		}).success(function(data) {
			$modalInstance.close($http);
			refresh();
		});

	};

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

});

//Object Deletion model panel
app.controller('ObjectDeletionModalCtrl', function($scope, $modal, $modalInstance, items,
		$http) {
	$scope.deleteObj = function() {
		$http({
			method : 'POST',
			url : 'http://localhost:8080/S3Web/deleteObject',
			headers : {
				'Content-Type' : 'application/json'
			},
			data : items
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

//Bucket Deletion model panel
app.controller('BucketDeletionModalCtrl', function($scope, $modal, $modalInstance, data,
		$http) {
	$scope.deleteBucket = function() {
		 
		console.log("Delete Bucket Called...!!" + data);
		$http({
			method : 'POST',
			url : 'http://localhost:8080/S3Web/deleteBucket?bucketName='+data,
			headers : {
				'Content-Type' : 'application/json'
			}
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
		alert("Inside Upload!!"+"bucket name..."+bucketName+"  folderpath.."+folderpath);

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


