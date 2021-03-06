/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models.admin.security;

import be.objectify.deadbolt.core.DeadboltAnalyzer;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.utils.PluginUtils;
import play.Logger;
import play.libs.F;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class MyDynamicResourceHandler implements DynamicResourceHandler
{
    private static final Map<String, DynamicResourceHandler> HANDLERS = new HashMap<String, DynamicResourceHandler>();

    static
    {
        HANDLERS.put("pureLuck",
                     new AbstractDynamicResourceHandler()
                     {
                         public boolean isAllowed(String name, 
                                                  String meta, 
                                                  DeadboltHandler deadboltHandler,
                                                  Http.Context context)
                         {
                             return System.currentTimeMillis() % 2 == 0;
                         }
                     });
        HANDLERS.put("viewProfile",
                     new AbstractDynamicResourceHandler()
                     {
                         public boolean isAllowed(final String name,
                                                  final String meta,
                                                  final DeadboltHandler deadboltHandler,
                                                  final Http.Context context)
                         {
                             return deadboltHandler.getSubject(context)
                                                   .map(new F.Function<Subject, Boolean>() {
                                                       @Override
                                                       public Boolean apply(Subject subject) throws Throwable {
                                                           boolean allowed;
                                                           if (DeadboltAnalyzer.hasRole(subject, "admin"))
                                                           {
                                                               allowed = true;
                                                           }
                                                           else
                                                           {
                                                               // a call to view profile is probably a get request, so
                                                               // the query string is used to provide info
                                                               Map<String, String[]> queryStrings = context.request().queryString();
                                                               String[] requestedNames = queryStrings.get("userName");
                                                               allowed = requestedNames != null
                                                                       && requestedNames.length == 1
                                                                       && requestedNames[0].equals(subject.getIdentifier());
                                                           }

                                                           return allowed;
                                                       }
                                                   }).get(1, TimeUnit.SECONDS);
                         }
                     });
    }
    
    public boolean isAllowed(String name,
                             String meta,
                             DeadboltHandler deadboltHandler,
                             Http.Context context)
    {
        DynamicResourceHandler handler = HANDLERS.get(name);
        boolean result = false;
        if (handler == null)
        {
            Logger.error("No handler available for " + name);
        }
        else
        {
            result = handler.isAllowed(name,
                                       meta,
                                       deadboltHandler,
                                       context);
        }
        return result;
    }

    public boolean checkPermission(final String permissionValue,
                                   final DeadboltHandler deadboltHandler,
                                   final Http.Context ctx)
    {
        return deadboltHandler.getSubject(ctx)
                              .map(new F.Function<Subject, Boolean>()
                              {
                                  @Override
                                  public Boolean apply(Subject subject) throws Throwable
                                  {
                                      boolean permissionOk = false;

                                      if (subject != null)
                                      {
                                          List<? extends Permission> permissions = subject.getPermissions();
                                          for (Iterator<? extends Permission> iterator = permissions.iterator(); !permissionOk && iterator.hasNext(); )
                                          {
                                              Permission permission = iterator.next();
                                              permissionOk = permission.getValue().contains(permissionValue);
                                          }
                                      }

                                      return permissionOk;
                                  }
                              }).get(1, TimeUnit.SECONDS);
    }
}
