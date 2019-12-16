//pay控制层
app.controller('payController' ,function($scope,payService){
	//创建二维码
	$scope.createNative=function () {
		payService.createNative().success(function (resp) {
			if(resp != null){
				$scope.money= resp.total_fee;//金额
				$scope.out_trade_no= resp.out_trade_no;//订单号
				//二维码
				var qr = new QRious({
					element:document.getElementById('myCode'),
					size:250,
					level:'H',
					value:resp.qrcode
				});
                queryPayStatus(resp.out_trade_no);//查询支付状态
			}
		})
	}
    // 查询支付状态
    queryPayStatus = function(out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function(response) {
            if (response.success) {
                location.href="paysuccess.html#?money="+$scope.money;
            } else {
                if(response.message == '二维码超时'){
                    document.getElementById('timeout').innerHTML='二维码已过期，刷新页面重新获取二维码';
                }else{
                    location.href="payfail.html";
                }
            }
        });
    }
    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }
});
