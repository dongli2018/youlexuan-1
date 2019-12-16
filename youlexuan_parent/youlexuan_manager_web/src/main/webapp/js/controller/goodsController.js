//goods控制层 
app.controller('goodsController' ,function($scope, $controller, goodsService,itemCatService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		goodsService.save($scope.entity).success(function(response) {
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
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		goodsService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {};
	// 搜索
	$scope.search = function(page,size){			
		goodsService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
	//状态转化文字
	$scope.statusArr=["未审核","审核通过",'审核不通过',"关闭"];
	//查找分类
	$scope.itemCatArr=[];
	$scope.findItemCat=function () {
		itemCatService.findAll().success(function (data) {
			if(data){
				for (var i=0;i<data.length;i++){
					$scope.itemCatArr[data[i].id]=data[i].name;
				}
			}
		})
	}
	//更改状态
	$scope.uploadStatus=function (ids,status) {
		goodsService.uploadStatus(ids,status).success(function (data) {
			if(data.success){
				$scope.reload();
			}else {
				alert(data.message)
			}
		})
	}
});	
