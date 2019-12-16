//type_template控制层 
app.controller('typeTemplateController' ,function($scope, $controller, typeTemplateService,brandService,specificationService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		typeTemplateService.save($scope.entity).success(function(response) {
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
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//字符串转json
				$scope.entity.brandIds=JSON.parse($scope.entity.brandIds);
				$scope.entity.specIds=JSON.parse($scope.entity.specIds);
				$scope.entity.customAttributeItems=JSON.parse($scope.entity.customAttributeItems);
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		typeTemplateService.dele($scope.selectIds).success(
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
		typeTemplateService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
	$scope.selectBrand=function(){
		brandService.selectBrand().success(function (data) {
			$scope.brandList={data:data}
		})
	}
    $scope.selectSpecification=function(){
        specificationService.selectSpecification().success(function (data) {
            $scope.specificationList={data:data}
        })
    }
    $scope.addTableRow=function () {
		$scope.entity.customAttributeItems.push({})
	}
	$scope.deleteRow=function (index) {
		$scope.entity.customAttributeItems.splice(index,1);
	}
});	
