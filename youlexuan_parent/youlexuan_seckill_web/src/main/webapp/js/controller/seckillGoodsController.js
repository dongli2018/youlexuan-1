//seckill_goods控制层 
app.controller('seckillGoodsController' ,function($scope, $location,$interval,seckillGoodsService,seckillOrderService){
	//查询实体 
	$scope.findList= function(){
		seckillGoodsService.findList().success(
			function(response){
				$scope.list= response;
			}
		);				
	}
	//查询单体
	$scope.findOne=function(){
		seckillGoodsService.findOne($location.search()['id']).success(function (resp) {
			if(resp){
				$scope.entity = resp;
				//读秒
				 var time = Math.floor((new Date($scope.entity.endTime).getTime() - new Date().getTime()) / 1000);
				 timeCount = $interval(function () {
					 if(time > 0){
					 	time -= 1;
					 	$(".overtime").html(convertTimeString(time));
					 }else {
					 	$interval.cancel(timeCount);
					 }
				 },1000)
			}
		})
	};
	//转换秒为天小时分钟秒，格式：XXX天 10:22:33
	convertTimeString = function(allsecond){
		var days= Math.floor(allsecond/(60*60*24));//天数
		days = days < 10 ? "0" + days : days;
		var hours= Math.floor((allsecond-days*60*60*24)/(60*60));//小时数
		hours = hours < 10 ? "0"+ hours : hours;
		var minutes= Math.floor((allsecond -days*60*60*24 - hours*60*60)/60);//分钟数
		minutes = minutes < 10 ? "0" + minutes : minutes;
		var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
		seconds = seconds < 10 ? "0" + seconds : seconds;
		var timeString="";
		if(days>0){
			timeString=days+"天 ";
		}
		return "距离结束: " + timeString+hours+":"+minutes+":"+seconds;
	}
	$scope.submitOrder = function (id) {
		seckillOrderService.submitOrder(id).success(function (data) {
			if(data.success){
				location.href="pay.html";
			}else {
				location.href="seckill-index.html";
			}
		})
	}
});	
