app.controller("baseController",function ($scope) {
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 5,
        perPageOptions: [5, 10, 20, 30],
        onChange: function(){
            //切换页码，重新加载
            $scope.reload();
        }
    };
    $scope.selectIds = [];

    $scope.selectId = function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id)
        } else {
            var number = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(number,1);
        }
    }
    //全选
    $scope.selectAll=function($event){
        var state = $event.target.checked;
        $(".eachbox").each(function (index,obj) {
            obj.checked=state;
            var id = parseInt($(obj).parent().next().text());
            if(state){
                $scope.selectIds.push(id)
            } else {
                var number = $scope.selectIds.indexOf(id);
                $scope.selectIds.splice(number,1);
            }
        })
    };

    $scope.reload=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    }

    //从json串查找是否存在
    $scope.selectObjByKey=function (list,key,keyValue) {
        for(i=0;i<list.length;i++){
            if(list[i][key] == keyValue){
                return list[i];
            }
        }
        return null;
    }
});