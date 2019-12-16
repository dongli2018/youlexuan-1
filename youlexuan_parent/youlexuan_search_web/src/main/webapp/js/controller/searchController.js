app.controller("searchController",function ($scope,$location,searchService) {
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (data) {
            if(data){
                $scope.resultMap=data;
                createPage();
            }
        })
    };
    //造页码
    createPage=function(){
        $scope.pageShow=[];
        var firstPage=1;
        var lastPage=$scope.resultMap.totalPage;
        if ($scope.resultMap.totalPage > 5){
             if($scope.searchMap.pageNum <= 3){
                 lastPage=5;
             }else if($scope.searchMap.pageNum >= $scope.resultMap.totalPage-2){
                 firstPage=$scope.resultMap.totalPage-4;
             }else {
                 firstPage=$scope.searchMap.pageNum-2;
                 lastPage=$scope.searchMap.pageNum+2;
             }
         }
        for(var i = firstPage; i <=lastPage;i++){
            $scope.pageShow.push(i)
        }
    }
    //定义查询对象
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNum':1,'pageSize':20,sortName:'',sortValue:''};
    //添加查询条件
    $scope.addSearchMap=function (key,value) {
        if(key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }
    //移除查询条件
    $scope.deleSearchMap=function (key) {
        if(key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key]='';
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
 //分页
    $scope.changePage=function(pageNum){
        pageNum = parseInt(pageNum);
        if(pageNum < 1 || pageNum > $scope.resultMap.totalPage){
            return;
        }
        $scope.searchMap.pageNum=pageNum;
        $scope.search();
    }
    $scope.toSearch=function(){
        $scope.searchMap.pageNum=1;
        $scope.searchMap.category='';
        $scope.searchMap.brand='';
        $scope.searchMap.spec={};
        $scope.searchMap.price='';
        $scope.searchMap.sortName='';
        $scope.searchMap.sortValue='';
        $scope.search();
    }
  $scope.addSort=function (sortNm,sortVu) {
      $scope.searchMap.sortName=sortNm;
      $scope.searchMap.sortValue=sortVu;
      $scope.search();
  }
  //页面初始查询
    $scope.initSearch=function () {
        $scope.searchMap.keywords = $location.search()["kw"];
        $scope.search();
    }
});