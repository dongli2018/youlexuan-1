//文件上传服务层
app.service("uploadService",function ($http) {
	this.uploadFile=function () {
		var data = new FormData();
		console.log(file.files[0])
		data.append("file",file.files[0]);

		return $http({
			method:"post",
			url:'../upload.do',
			data:data,
			headers : {
				'Content-Type' : undefined
			},
			transformRequest : angular.identity
		});
	}
})