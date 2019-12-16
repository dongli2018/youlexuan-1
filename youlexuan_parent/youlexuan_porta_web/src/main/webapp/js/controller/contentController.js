app.controller("contentController",function ($scope,contentService) {
    //根据cid查寻广告
    $scope.contentList=[];
    $scope.findContentByCid=function (cid) {
        contentService.findContentByCid(cid).success(function (data) {
            if(data){
                $scope.contentList=data;
            }
        })
    }
    $scope.toSearch=function () {
        location.href="http://localhost:9007/search.html#?kw="+$scope.keywords;
    }
})