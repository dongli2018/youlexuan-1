//seckill_goods服务层
app.service('seckillGoodsService', function($http){
	// 查询实体
	this.findList=function () {
		return $http.get('/seckillGoods/findList.do');
	}
	//查询单体
	this.findOne=function(id){
		return $http.get('/seckillGoods/findOne.do?id='+id);
	}
});