app.controller("loginController",function ($scope,loginService) {
    $scope.getName=function () {
        loginService.getName().success(function (data) {
            $scope.Name=data.substr(1,data.lastIndexOf("\"")-1);
            // $scope.Name=data.replace(/\"/g,"");
        })
    }
})