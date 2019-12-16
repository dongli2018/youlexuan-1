//cart控制层
app.controller('cartController' ,function($scope,cartService){
	//查购物车信息
	$scope.findCartList = function () {
		cartService.findCartList().success(function (data) {
			if(data){
				$scope.cartList = data;
				$scope.totalNum=0;
				$scope.totalPrice=0;
				totalNumer($scope.cartList);
			}
		})
	}
	//增删商品数量
	$scope.changNum=function (itemId,num) {
		cartService.changNum(itemId,num).success(function (data) {
			if(data.success){
				$scope.findCartList();
			}else {
				alert(data.message)
			}
		})
	}
	//计算总计
	totalNumer=function (list) {
		for(var i = 0; i < list.length; i++) {
			for(var j = 0; j < list[i].orderItemList.length; j++) {
				$scope.totalNum += list[i].orderItemList[j].num;
				$scope.totalPrice += list[i].orderItemList[j].totalFee;
			}
		}
	}
	//获取地址列表
		$scope.findAddressList=function(){
			cartService.findAddressList().success(
				function(response){
					$scope.addressList=response;
					//设置默认地址
					for(var i=0;i< $scope.addressList.length;i++){
						if($scope.addressList[i].isDefault=='1'){
							$scope.selectAddr=$scope.addressList[i];
							break;
						}
					}
				}
			);
		};
	//选择地址
	$scope.selectAddress=function(address){
		$scope.selectAddr=address;
	};
	$scope.order={paymentType:'1'};
//选择支付方式
	$scope.selectPayType=function(type){
		$scope.order.paymentType= type;
	}
    //保存订单
    $scope.submitOrder=function(){
        $scope.order.receiverAreaName=$scope.selectAddr.address;//地址
        $scope.order.receiverMobile=$scope.selectAddr.mobile;//手机
        $scope.order.receiver=$scope.selectAddr.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function(response){
                if(response.success){
                    //页面跳转
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="pay_offline.html";
                    }
                }else{
                    alert(response.message);	//也可以跳转到提示页面
                }
            });
    }
});
