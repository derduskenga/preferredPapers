/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models.admin.security;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import models.admin.adminmodels.AdminUser;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.Flash;
import play.mvc.Result;
import views.html.securityview.accessFailed;
import views.html.securityview.adminlogin;
import play.Logger;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class MyDeadboltHandler extends AbstractDeadboltHandler
{
    public F.Promise<Result> beforeAuthCheck(Http.Context context)
    {
		Logger.info("Inside before auth check.");
		if(context.session().get("admin_email") == null){
				Flash flashObject = context.flash();
				flashObject.put("error", "Oops! You are not logged in");
				Logger.info("Is null");
				return F.Promise.pure((Result)badRequest(adminlogin.render("Login")));
		}
		Logger.info("Is not null"); 
		return F.Promise.pure(null);
    }

    public F.Promise<Subject> getSubject(final Http.Context context)
    {
        // in a real application, the user name would probably be in the session following a login process
        return F.Promise.promise(new F.Function0<Subject>()
        {
            @Override
            public Subject apply() throws Throwable {
                return AdminUser.findByEmail(context.session().get("admin_email"));
            }
        });
    }

    public DynamicResourceHandler getDynamicResourceHandler(Http.Context context)
    {
        return new MyDynamicResourceHandler();
    }

    @Override
    public F.Promise<Result> onAuthFailure(Http.Context context,
                                                 String content)
    {
        // you can return any result from here - forbidden, etc
        return F.Promise.promise(new F.Function0<Result>()
        {
            @Override
            public Result apply() throws Throwable {
					return ok(accessFailed.render());
            }
        });
    }
}
