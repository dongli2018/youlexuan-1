//user控制层 
app.controller('userController' ,function($scope, userService){
	// 保存
	$scope.save = function() {

		if($scope.entity.password != $scope.password){
			alert("两次密码不一致")
			return;
		}
		userService.save($scope.entity,$scope.smscode).success(function(response) {
			if (response.success) {
				// 跳转登录页面
				location.href="login.html"
			} else {
				alert(response.message);
			}
		});
	};
	$scope.entity={"phone":""};
	//发送验证码
	$scope.sendSms=function () {
		if($scope.entity.phone == ""){
			alert("请输入手机号码");
			return;
		}
		userService.sendSms($scope.entity.phone).success(function (data) {
			if(data.success){
				var i = 10;
				$("#bto").attr('disabled',"disabled");
				var timer = setInterval(function () {
					if(i <= 0){
						$("#bto").removeAttr("disabled");
						$("#bto").html("获取短信验证码");
						clearInterval(timer);
					}else {
						$("#bto").html(--i+"秒后重发");
					}

				},"1000")

			}else {
				alert(data.message);
			}
		})

	}
});	
