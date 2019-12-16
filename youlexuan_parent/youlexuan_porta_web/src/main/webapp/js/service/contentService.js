app.service("contentService",function ($http) {
    this.findContentByCid=function (cid) {
        return $http.get('../porta/findContentByCid?id='+cid);
    }
})