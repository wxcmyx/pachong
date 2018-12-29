package com.wixct.pachong.web.githubproject;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * GithubprojectInterceptor
 */
public class GithubprojectInterceptor implements Interceptor {
    public final static String SESSION_NAME_USER="cuser";
	public final static String ADMIN="admin";
	
	@Override
	public void intercept(Invocation inv) {
//		ManageUserInfo localUsers = inv.getController().getSessionAttr(CommonInterceptor.SESSION_NAME_USER);
//			if(localUsers.getStr("PERMISSION")!=null){
//				if(localUsers.getStr("PERMISSION").indexOf("admin;")<0){
//				if(localUsers.getStr("PERMISSION").indexOf("githubproject;")<0){
//					return;
//				}
//			}
//			
//		}
		// TODO Auto-generated method stub
		inv.invoke();
		
	}
}
