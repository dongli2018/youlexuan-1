//cart服务层
app.service('cartService', function($http){
	//查询购物车
	this.findCartList=function () {
		return $http.get('cart/findCartList.do');
	}
	//增删商品数量
	this.changNum=function (itemId,num) {
		return $http.post("cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
	}
	//获取地址列表
	this.findAddressList=function(){
		return $http.get('address/findListByLoginUser.do');
	}
	//保存订单
	this.submitOrder=function(order){
		return $http.post('order/add.do',order);
	}
});