/**
 * 
 */
package com.wixct.pachong;

import com.wixct.pachong.web.githubproject.Githubproject;
import com.wixct.pachong.web.openopen.Openopen;
import com.wixct.pachong.web.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;
import java.util.List;



/**
 * @author xcwang
 *
 */
public class ExecuteBuild {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(ExecuteBuild.class.getName());


	public void executeToday() {
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
		
		
	}
	public int buildGithubPage(String pageurl,String dateStr) throws IOException{
		System.out.println("访问："+pageurl);
		Document doc2=Jsoup.connect(pageurl).get();
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
//				System.out.println("descript："+descript);
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
			}else{
				Githubproject g=lsg.get(0);
				g.setDESCRIPT(descript).setLANG(type).setURL(projectHrefStr).setNAME(name).setSTAR(Integer.parseInt(star)).setUPDATE(dateStr);
				g.update();
			}
		}
		return newsHeadlines2.size();
	}
	public void executeAsDate(String startStr) {
		Date startDate=DateUtil.parseDate(startStr,"yyyy-MM-dd");
		Date now=new Date();
		int daysToNow=DateUtil.diffDate(now,startDate);
		for(int i=0;i<=daysToNow;i++) {
			String nowStr = DateUtil.formatDate(now, "yyyy-MM-dd");
			Openopen openopen;
			try {
				String complateUrl = "http://www.open-open.com/github/view/github" + nowStr + ".html";
				List<Openopen> opls = Openopen.dao.find("select * from OPENOPEN where DATE=?", new Object[]{nowStr});
				if (opls.size() <= 0) {
					openopen = new Openopen().setDATE(nowStr).setURL(complateUrl).setCOUNT(buildGithubPage(complateUrl, nowStr));
					openopen.save();
				} else {
					if (opls.get(0).getCOUNT().equals(0)) {
						openopen = opls.get(0).setCOUNT(buildGithubPage(complateUrl, nowStr));
						openopen.update();
					}else{
						openopen = opls.get(0).setCOUNT(buildGithubPage(complateUrl, nowStr));
						openopen.update();
					}
				}
				now=DateUtil.addDate(now, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		List<Openopen> opls=Openopen.dao.find("select * from OPENOPEN where COUNT=0");
//		String datestr;
//		for(Openopen open:opls){
//			datestr=open.getDATE();
//			String complateUrl="http://www.open-open.com/github/view/github"+datestr+".html";
//			try {
//				open.setCOUNT(buildGithubPage(complateUrl,datestr));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}


	}

	public static void main(String[] args){
		DbUtils.initDb();
		ExecuteBuild hj=new ExecuteBuild();
		hj.executeAsDate("2018-07-29");
	}

}
