//item控制层 
app.controller('pageController' ,function($scope,$http){

	$scope.num=1;
	//增减商品	
	$scope.changeNum=function(n){
		$scope.num=$scope.num+n;
		if($scope.num <1){
		
			$scope.num=1;
		}
	
	};
	
	//更改规格
	$scope.specificationItems={};
	$scope.updateSpec=function(specName,opName){
		$scope.specificationItems[specName] = opName;
		searchSku();

	}
	//选中状态
	$scope.select=function(specNm,value){

		if($scope.specificationItems[specNm] == value){
			return true;
		}else{return false;}
	}

		//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=itemList[0];
		//默认用户选中
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}

		//匹配两个对象
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;
	}
		//查询SKU
	searchSku=function(){
		for(var i=0;i<itemList.length;i++ ){
			if( matchObject(itemList[i].spec ,$scope.specificationItems ) ){
				$scope.sku=itemList[i];
				return;
			}
		}
	}
	//添加购物车
	$scope.addCart=function(){
		$http.get('http://localhost:9013/cart/addGoodsToCartList.do?itemId='
			+ $scope.sku.id +'&num='+$scope.num, {'withCredentials':true}).success(
			function(response){
				if(response.success){
					//跳转到购物车页面
					location.href='http://localhost:9013/cart.html';
				}else{
					alert(response.message);
				}
			}
		);
	};
});	
