app.service('brandService',function ($http) {
    this.save=function (entity) {
        var methodName = 'add';
        if (entity.id != null) {
            methodName = 'update';
        }
        return  $http.post('../brand/' + methodName + '.do', entity);
    }

    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id)
    }

    this.delete = function (ids) {
        return $http.post('../brand/delete.do?ids='+ids)
    }

    this.search=function (page,size,searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&size='+size,searchEntity)
    }
    this.selectBrand=function () {
        return $http.get("../brand/selectBrand.do")
    }
});