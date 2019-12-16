//login控制层
app.controller('loginController' ,function($scope, loginService){
	// 回显姓名
	$scope.getName=function () {
		loginService.getName().success(function (data) {
			data=data.replace(/\"/g,"");
			$scope.name=data;
		})
	}
});	
