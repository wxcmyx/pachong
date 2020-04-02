function showdate(n) 
{ 
var uom = new Date(new Date()-0+n*86400000); 
uom = uom.getFullYear() + "-" + (uom.getMonth()+1<10?"0"+(uom.getMonth()+1):uom.getMonth()+1) + "-" + (uom.getDate()<10?("0"+uom.getDate()):uom.getDate()); 
return uom; 
} 
layui.use(['form','layer','table','laytpl','laydate'],function(){
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        laytpl = layui.laytpl,
        table = layui.table;
    var laydate = layui.laydate;

    //用户列表
    var tableIns = table.render({
        elem: '#githubprojectList',
        url : './messageshow',
        //cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [25,100,250,500,1000],
        limit : 100,
        id : "githubprojectListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: 'category_str', title: '分类', width:110, align:'left'},
            {field: 'title', title: '消息标题', minWidth:500, align:"left",templet:function(d){
                    return '<a href="'+d.content+'" target="_blank">'+d.title+'</a>';
                }},
            {field: 'time_str', title: '发布时间', width:150, align:'left'},
            {field: 'update', title: '更新日期',width:180,align:'center'}
        ]]
    });
    //绑定日期
    laydate.render({
        elem: '#pupdate' //指定元素
//        ,show: true
        ,value:showdate(-2)
    });
    //搜索函数
    function searchRender(){
        if($(".searchVal").val() != ''){
            var index = layer.load(0); //添加laoding,0-2两种方式
            table.reload("githubprojectListTable",{
                page: {
                    curr: 1 //重新从第 1 页开始
                },
                where: {
                    plang: $("#plang").val(),  //搜索的关键字
                    pjname: $("#pjname").val(),
                    pupdate:$("#pupdate").val()
                },
                done: function() {
                    layer.close(index);
                }
            })
        }else{
            layer.msg("请输入搜索的内容");
        }
    }
    //搜索【此功能需要后台配合，所以暂时没有动态效果演示】
    $(".search_githubproject_btn").on("click",searchRender);
    //回车事件
    $('.search_input').bind('keydown', function (event) {
        var event = window.event || arguments.callee.caller.arguments[0];
        if (event.keyCode == 13){
            searchRender()
        }

    });

})
