package com.wixct.pachong.web.githubproject;


import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.wixct.pachong.web.openopen.Openopen;
import com.wixct.pachong.web.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Api(tags="GITHUB项目管理")
@RestController
public class GithubprojectController extends Controller {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(GithubprojectController.class.getName());


	public void index() {
		render("/page/githubproject/messageList.html");
	}

    @ApiOperation("GITHUB项目列表")
    @RequestMapping(value="/githubproject/show",method= RequestMethod.GET)
    @ResponseBody
	public Object show(@ApiParam("项目描述") @RequestParam(value="plang",defaultValue="",required=false) String plang,
                     @ApiParam("项目名称/描述关键词") @RequestParam(value="pjname",defaultValue="",required=false) String pjname,
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
		sql_exp.append("from GITHUBPROJECT a where 1=1 ");
		if(StringUtils.isNoneBlank(searchKey)){
			sql_exp.append("and LANG=? ");
			params.add(searchKey);
		}
		if(StringUtils.isNoneBlank(pjname)){
			sql_exp.append("and (NAME like ? or DESCRIPT like ?) ");
			params.add("%" + pjname+ "%");
            params.add("%" + pjname+ "%");
		}
		if(StringUtils.isNoneBlank(pupdate)){
			sql_exp.append("and a.UPDATE=? ");
			params.add(pupdate);
		}
		sql_exp.append("order by a.UPDATE desc");
		logger.error(sql_exp.toString());
		Page<Githubproject> pages= Githubproject.dao.paginate(pageNum,pageSize,sql_select.toString(),sql_exp.toString(),params.toArray());
		resultMap.put("data", pages.getList());
		resultMap.put("count", pages.getTotalRow());
		resultMap.put("code", 0);
		resultMap.put("msg", "");
		return resultMap;
	}
    @ApiOperation("GITHUB项目添加")
    @RequestMapping(value="/githubproject/add",method= RequestMethod.GET)
    @ResponseBody
    public Object add(@ApiParam("项目更新日期") @RequestParam(value="pupdate",defaultValue="",required=false) String pupdate){

	    Map<String, Object> resultMap = new HashMap<String, Object>();
        Date now=new Date();
        if(StringUtils.isNoneBlank(pupdate)){
            now=DateUtil.parseDate(pupdate,"yyyy-MM-dd");
        }
        int i=0;
        now=now==null?new Date():now;
//        now= DateUtil.addDate(now, -2);
        String nowStr=DateUtil.formatDate(now,"yyyy-MM-dd");
        int todayNum=0;
        String todayIsNew="第一次";
        String wrongMsg="";

        Openopen openopen;
        try {
            String complateUrl="http://www.open-open.com/github/view/github"+nowStr+".html";
            List<Openopen> opls=Openopen.dao.use("ds1").find("select * from OPENOPEN where DATE=?",new Object[]{nowStr});
            if(opls.size()<=0){
                todayNum=buildGithubPage(complateUrl,nowStr);
                openopen=new Openopen().setDATE(nowStr).setURL(complateUrl).setCOUNT(todayNum);
                openopen.save();

            }else{
                todayIsNew="非第一次";
                if(opls.get(0).getCOUNT().equals(0)){
                    todayNum=buildGithubPage(complateUrl,nowStr);
                    openopen=opls.get(0).setCOUNT(todayNum);
                    openopen.update();
                }
            }
            //now=DateUtil.addDate(now, -1);
        } catch (IOException e) {
            e.printStackTrace();
            wrongMsg=e.getMessage();
            i=1;
        }finally {
            resultMap.put("code", i);
            resultMap.put("msg",i==0?nowStr+todayIsNew+"共获取"+todayNum+"条":"增加GITHUB项目失败:"+wrongMsg);
            return resultMap;
        }
    }
    @Scheduled(cron = "0 0 8 * * ?")
    @RequestMapping(value="/githubproject/hourjob",method= RequestMethod.GET)
    @ResponseBody
    public Object hourjob() {
        Date now=new Date();
        now= DateUtil.addDate(now, -2);
        String nowStr=DateUtil.formatDate(now,"yyyy-MM-dd");
        Openopen openopen;
        try {
            String complateUrl="http://www.open-open.com/github/view/github"+nowStr+".html";
            List<Openopen> opls=Openopen.dao.find("select * from OPENOPEN where DATE=?",new Object[]{nowStr});
            if(opls.size()<=0){
                openopen=new Openopen().setDATE(nowStr).setURL(complateUrl).setCOUNT(buildGithubPage(complateUrl,nowStr));
                openopen.save();
            }else{
                if(opls.get(0).getCOUNT().equals(0)){
                    openopen=opls.get(0).setCOUNT(buildGithubPage(complateUrl,nowStr));
                    openopen.update();
                }
            }
            //now=DateUtil.addDate(now, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
    public int buildGithubPage(String pageurl,String dateStr) throws IOException{
        logger.error("访问："+pageurl);
        Document doc2= Jsoup.connect(pageurl).get();
        Elements newsHeadlines2 = doc2.select(".rowlink tr");
        for (Element headline2 : newsHeadlines2) {
            String type=StringUtils.substringBetween(headline2.toString(), "<td class=\"table-language\">", "</td>");
            String projectHrefStr=StringUtils.substringBetween(headline2.toString(), "<a href=\"", "\"");
            String name=StringUtils.substringBetween(headline2.toString(), "target=\"_blank\">", "</a>");
            String descript=StringUtils.substringBetween(headline2.toString(), "\"table-description\">", "</td>");
            String star=StringUtils.substringBetween(headline2.toString(), "<td class=\"table-change\">", "</td>");

            if(StringUtils.isNotBlank(type)){
                type=type.trim();
            }
            if(StringUtils.isNotBlank(projectHrefStr)){
                projectHrefStr=projectHrefStr.trim();
            }
            if(StringUtils.isNotBlank(name)){
                name=name.trim();
            }
            if(StringUtils.isNotBlank(descript)){
                descript=descript.trim();
            }
            if(StringUtils.isNotBlank(star)){
                star=star.trim();
            }
            List<Githubproject> lsg=Githubproject.dao.find("select * from GITHUBPROJECT where NAME=?", new Object[]{name});

            if(lsg.size()<=0){
                //logger.error(dateStr+"--"+type+"--"+projectHrefStr);
                Githubproject g=new Githubproject();
                g.setDESCRIPT(descript).setLANG(type).setURL(projectHrefStr).setNAME(name).setSTAR(Integer.parseInt(star)).setUPDATE(dateStr);
                g.save();
            }
        }
        return newsHeadlines2.size();
    }
}


