//goods控制层 
app.controller('goodsController' ,function($scope, $controller,$location, goodsService,uploadService,itemCatService,typeTemplateService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});

	// 保存
	$scope.save = function() {

		$scope.entity.goodsDesc.introduction=editor.html();

		goodsService.save($scope.entity).success(function(response) {
			if (response.success) {
				alert('保存成功');
				$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
				editor.html('');//清空富文本编辑器
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
		goodsService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}
    
	// //新增
	// $scope.add=function () {
	// 	$scope.entity.goodsDesc.introduction = editor.html();
	// 	goodsService.add($scope.entity).success(function (data) {
	// 		if(data.success){
	// 			alert(data.message);
	// 			$scope.entity={goods:{},goodsDesc:{itemImages:[],customAttributeItems:[], specificationItems:[]}};
	// 			editor.html('');//清空
	// 		}else {
	// 			alert(data.message)
	// 		}
	// 	})
	// }
	//上传图片
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (data) {
			if(data.success){
				$scope.itemImages.url=data.message;
			}else {
				alert(data.message)
			}
		})
	};
	//entity的初始值
	//添加图片到列表
	$scope.entity={goods:{},goodsDesc:{itemImages:[],customAttributeItems:[], specificationItems:[]}};
	$scope.saveImg=function () {
			$scope.entity.goodsDesc.itemImages.push($scope.itemImages);
	};
	//从列表删除图片
	$scope.delImg=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	//顶级分类下拉
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(function (data) {
			if(data){
				$scope.itemCat1List=data;

			}
		})
	};
	//二级下拉框
	$scope.$watch("entity.goods.category1Id",function (newId,oldId) {
		if(newId){
			itemCatService.findByParentId(newId).success(function (data) {
				$scope.itemCat2List=data;
				$scope.itemCat3List=[];
				$scope.entity.goods.typeTemplateId="";
			})
		}
	})
	//三级下拉框
	$scope.$watch("entity.goods.category2Id",function (newId,oldId) {
		if(newId){
			itemCatService.findByParentId(newId).success(function (data) {
				$scope.itemCat3List=data;
				$scope.entity.goods.typeTemplateId="";
			})
		}
	});
	//模板id
	$scope.$watch("entity.goods.category3Id",function (newId,oldId) {
		if(newId){
			itemCatService.findOne(newId).success(function (data) {
				$scope.entity.goods.typeTemplateId=data.typeId;
			})
		}
	})
	//品牌选择
	$scope.$watch("entity.goods.typeTemplateId",function (newId,oldId) {
		if(newId){
			typeTemplateService.findOne(newId).success(function (data) {
				$scope.brandIds=JSON.parse(data.brandIds);
				//扩展属性选择
				if($location.search()["id"] == null){
					$scope.entity.goodsDesc.customAttributeItems=JSON.parse(data.customAttributeItems);
				}

			});
			typeTemplateService.selectOption(newId).success(function (data) {
				$scope.optionList=data;
			})
			// $scope.entity.goodsDesc.specificationItems=[];
		}
	})
	//保存规格选项
	$scope.uploadSpec=function ($event,Name,Value) {
		var obj = $scope.selectObjByKey($scope.entity.goodsDesc.specificationItems,"attributeName",Name);
		if(obj != null){
			if($event.target.checked){
				obj.attributeValue.push(Value);
			}else{
				//取消勾选
				obj.attributeValue.splice(obj.attributeValue.indexOf(Value),1);
				//如果一个没有,就删除这个obj
				if(obj.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(obj,1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({attributeName:Name,attributeValue:[Value]});
		}
	}
	//增加行数
	$scope.creatItem=function () {
		//初始声明
		$scope.entity.items=[{spec:{},price:0,num:999,status:'0',isDefault:'0'}];
		var item = $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<item.length;i++){
			$scope.entity.items=$scope.addRow($scope.entity.items,item[i].attributeName,item[i].attributeValue)
		}
	}
	$scope.addRow=function (list,columName,columValue) {
		var newList=[];
		for(var i=0;i<list.length;i++){
			var oldRow = list[i];
			for(var j=0;j<columValue.length;j++){
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columName]=columValue[j];
				newList.push(newRow);
			}
		}
		return newList;
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
	//修改

	$scope.findOne=function () {
		//接收参数
		var id = $location.search()["id"];
		if(id == null){
			return null;
		}
		goodsService.findOne(id).success(function (data) {
			if(data){
				$scope.entity=data;
				var introduction = $scope.entity.goodsDesc.introduction;
				//富文本
				editor.html(introduction);
				//回显照片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//回显扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//回显规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//回显下拉勾
				$scope.check=function(name,value){
					var obj = $scope.selectObjByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
					if(obj == null){
						return false
					}else {
						if(obj.attributeValue.indexOf(value)>=0){
							return true;
						}else {
							return false;
						}
					}
				}
				//回显规格下拉表
				for(var i=0;i<$scope.entity.items.length;i++){
					$scope.entity.items[i].spec=JSON.parse($scope.entity.items[i].spec)
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
