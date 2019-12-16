//content控制层 
app.controller('contentController' ,function($scope, $controller, contentService,uploadService,contentCategoryService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		contentService.save($scope.entity).success(function(response) {
			if (response.success) {
				// 重新加载
				$scope.reload();
			} else {
				alert(response.message);
			}
		});
	}
	
	//查询实体 
	$scope.findOne = function(id){				
		contentService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		contentService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reload();
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {};
	// 搜索
	$scope.search = function(page,size){			
		contentService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
	//上传图片
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (data) {
			if(data.success){
				$scope.entity.pic=data.message;
			}else {
				alert(data.message)
			}
		})
	};
	//查询广告分类
	$scope.findContentCate=function () {
		contentCategoryService.findContentCate().success(function (data) {
			if(data){
				$scope.contentCatList=data;
			}else {
				alert(data.message)
			}
		})
	}
	//状态转为文字
	$scope.statusArr=["不启用","启用"];
});	
