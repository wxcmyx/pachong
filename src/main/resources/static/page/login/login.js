layui.config({
	base : "js/"
}).use(['form','layer'],function(){
	var form = layui.form,
		layer = parent.layer === undefined ? layui.layer : parent.layer,
		$ = layui.jquery;
	//video背景
	$(window).resize(function(){
		if($(".video-player").width() > $(window).width()){
			$(".video-player").css({"height":$(window).height(),"width":"auto","left":-($(".video-player").width()-$(window).width())/2});
		}else{
			$(".video-player").css({"width":$(window).width(),"height":"auto","left":-($(".video-player").width()-$(window).width())/2});
		}
	}).resize();
	
	$(document).keyup(function(evnet) {
		if (evnet.keyCode == '13') {
			$(".login_btn").click;
		}
	});
	
	//登录按钮事件
	form.on("submit(login)",function(data){
        var account = data.field.account
        var password = data.field.password
        var code = data.field.code
        $.ajax({
            type:"get",
            url:"/login",
            data:{
            	"account": account,
            	"password": password,
            	"randomCode": code
            },
            success:function(res){
            	if(res.rc == 0){
            		layer.msg('登录成功', {icon: 6, time: 1000});
            		setTimeout(function(){
            			location.href = "/";
            		},1000)
            	}else{
            		layer.msg(res.msg, {icon: 5, time: 1000});
            	}
            },
            error:function(error){
            	layer.msg('服务器请求失败', {icon: 5, time: 1000}); 
            }
        });
		return false;
	})
})

function changeRandomCode(){
	document.getElementById("CreateCheckCode").src="/random_code?"+Math.random();
}
