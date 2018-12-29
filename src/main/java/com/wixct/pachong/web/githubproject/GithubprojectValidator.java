package com.wixct.pachong.web.githubproject;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * GithubprojectValidator.
 */
public class GithubprojectValidator extends Validator {
	
	protected void validate(Controller controller) {
        //validateRequiredString("githubproject.", "Msg", "请输入Githubproject标题!");
	}
	
	protected void handleError(Controller controller) {
		controller.keepModel(Githubproject.class);
		
		String actionKey = getActionKey();
		if (actionKey.equals("/githubproject/save"))
			controller.render("add.html");
		else if (actionKey.equals("/githubproject/update"))
			controller.render("edit.html");
	}
}
