//login服务层
app.service('loginService', function($http){
	//回显
	this.getName=function () {
		return $http.get('../user/getName.do');
	}
});