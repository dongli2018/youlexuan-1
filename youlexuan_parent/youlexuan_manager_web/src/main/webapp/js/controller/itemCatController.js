//item_cat控制层 
app.controller('itemCatController' ,function($scope, $controller, itemCatService,typeTemplateService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		itemCatService.save($scope.entity,$scope.parentId).success(function(response) {
			if (response.success) {
				// 重新加载
				$scope.findByParentId($scope.parentId);
			} else {
				alert(response.message);
			}
		});
	}
	
	//查询实体 
	$scope.findOne = function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		itemCatService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.parentId);
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {};
	// 搜索
	$scope.search = function(page,size){			
		itemCatService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
	$scope.parentId=0;
	//根据上级ID显示下级列表
	$scope.findByParentId=function(parentId){
		$scope.parentId=parentId;
		itemCatService.findByParentId(parentId).success(
			function(response){
				$scope.list=response;
			}
		);
	}
	//计数器
	$scope.count=1;
	//设置级别
	$scope.setCount=function(value){
		$scope.count=value;
	}
	//读取列表
	$scope.selectList=function(p_entity){
		if($scope.count==1){//如果为1级
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.count==2){//如果为2级
			$scope.entity_1=p_entity;
			$scope.entity_2=null;
		}
		if($scope.count==3){//如果为3级
			$scope.entity_2=p_entity;
		}
		$scope.findByParentId(p_entity.id);	//查询此级下级列表
	}
	//查询模板
	$scope.selectTemplate=function () {
		typeTemplateService.selectTemplate().success(function (data) {
			if(data){
				$scope.selectTemplateList={data:data};
			}
		})

	}
});	
