//user服务层
app.service('userService', function($http){
	// 保存
	this.save = function(entity,smscode) {
		return $http.post('../user/add.do?code='+smscode, entity);
	}
	//发送验证码
	this.sendSms=function (phone) {
		return $http.post("../user/sendSms.do?phone="+phone);
	}
});