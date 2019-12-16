app.controller("brandController",function ($scope,$controller,brandService) {
    //继承
    $controller("baseController", {
        $scope : $scope
    });
    //
    $scope.save=function () {
        brandService.save($scope.entity).success(function (data) {
            if (data.success) {
                $scope.reload();
            } else {
                alert(data.message)
            }
        })
    }

    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (data) {
            $scope.entity=data;
        })
    }


    $scope.delete=function () {
        if($scope.selectIds.length>0){
            brandService.delete($scope.selectIds).success(function (data) {
                if(data.success){
                    $scope.reload();
                    $scope.selectIds=[];
                } else {
                    alert(data.message)
                }
            })
        }else {
            alert("请选择要删除的品牌")
        }
    }

    //条件查询
    $scope.searchEntity={};
    $scope.search=function (page,size) {
        brandService.search(page,size,$scope.searchEntity).success(function (data) {
            $scope.list=data.rows;
            $scope.paginationConf.totalItems=data.total;
        })
    }
})