package com.wixct.pachong.web.controller;

import com.jfinal.kit.FileKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.wixct.pachong.config.DataSourceConfig;
import com.wixct.pachong.web.githubproject.Githubproject;
import com.wixct.pachong.web.githubproject.GithubprojectController;
import com.wixct.pachong.web.rest.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class.getName());

    @Value("${message.content.filedir}")
    private  String filedir;

    @Value("${message.content.baseurl}")
    private  String baseurl;


    @GetMapping("/")
    public String index() {
        return "forward:/page/githubproject/githubprojectList.html";
    }

    @GetMapping("/messages")
    public String messages() {
        return "forward:/page/messages/messageList.html";
    }

    @RequestMapping(value="/getmessages")
    @ResponseBody
    public Object getmessages(@Nullable @RequestBody HashMap requestBody){
        if(requestBody!=null) {
            for (Object key : requestBody.keySet()) {
                Record record = new Record();
                HashMap vlue = (HashMap) requestBody.get(key);
//                String filename=StringUtils.substring(key.toString(),StringUtils.lastIndexOf(key.toString(),"/"));
                record.set("id", key);
                record.set("title", vlue.get("title"));
                record.set("title_image", vlue.get("title_image"));
                record.set("url", vlue.get("url"));
                record.set("time_str", vlue.get("time_str"));
                record.set("category_str", vlue.get("category_str"));
                record.set("content", baseurl +key);
                record.set("update", new Date());
                Record r = Db.findById("messages", "id", key);
                if (null == r) {
                    Db.use("ds1").save("messages", "id", record);
                    try {
                        FileUtils.writeStringToFile(new File(filedir+key), (String) vlue.get("content"),"UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            logger.error("更新数据："+requestBody.keySet().size());
            System.out.println("获取的配置文件的值："+filedir+"\nURL地址："+baseurl);
            return new Result().success();
        }else{
            return new Result().failure().setMessage("body is null");
        }

    }

    @ApiOperation("消息列表")
    @RequestMapping(value="/messageshow",method= RequestMethod.GET)
    @ResponseBody
    public Object show(@ApiParam("分类") @RequestParam(value="plang",defaultValue="",required=false) String plang,
                       @ApiParam("标题") @RequestParam(value="pname",defaultValue="",required=false) String pname,
                       @ApiParam("项目更新日期") @RequestParam(value="pupdate",defaultValue="",required=false) String pupdate,
                       @ApiParam("第几页") @RequestParam(value="page",defaultValue="1",required=false) int page,
                       @ApiParam("每页多少条") @RequestParam(value="limit",defaultValue="100",required=false) int limit){

        String searchKey = plang;
        List<Object> params = new ArrayList<Object>();

        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum=page;
        int pageSize=limit;
        StringBuffer sql_select = new StringBuffer();
        StringBuffer sql_exp = new StringBuffer();
        sql_select.append("select * ");
        sql_exp.append("from MESSAGES a where 1=1 ");
        if(StringUtils.isNoneBlank(searchKey)){
            sql_exp.append("and a.category_str=? ");
            params.add(searchKey);
        }
        if(StringUtils.isNoneBlank(pname)){
            sql_exp.append("and a.title like ? ");
            params.add("%"+pname+"%");
        }
        if(StringUtils.isNoneBlank(pupdate)){
            sql_exp.append("and a.time_str like ? ");
            params.add(pupdate+"%");
        }
        sql_exp.append("order by a.time_str desc");
        logger.error(sql_exp.toString());
        Page<Record> pages= Db.use("ds1").paginate(pageNum,pageSize,sql_select.toString(),sql_exp.toString(),params.toArray());
        List result=new ArrayList();
        for(Record r:pages.getList()){
            result.add(r.getColumns());
        }
        resultMap.put("data",result);
        resultMap.put("count", pages.getTotalRow());
        resultMap.put("code", 0);
        resultMap.put("msg", "");
        return resultMap;
    }

}
