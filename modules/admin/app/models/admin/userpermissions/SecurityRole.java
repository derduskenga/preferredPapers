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
package models.admin.userpermissions;

import be.objectify.deadbolt.core.models.Role;
import play.db.ebean.Model;
import play.data.validation.Constraints;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.Id;
import play.data.validation.ValidationError;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Entity
public class SecurityRole extends Model implements Role
{
    @Id
    public Long id;

    @Column(unique=true)
    @NotNull
    @Constraints.Required(message="Security role name is required")
    public String name;

    public static final Finder<Long, SecurityRole> find = new Finder<Long, SecurityRole>(Long.class,
                                                                                         SecurityRole.class);

    public String getName()
    {
        return name;
    }

    public static SecurityRole findByName(String name)
    {
        return find.where()
                   .eq("name",
                       name)
                   .findUnique();
    }
    
    public List<ValidationError> validate(){
				List<ValidationError> errors = new ArrayList<>();
				SecurityRole securityRole = SecurityRole.find.where().eq("name", name).findUnique();
				if (securityRole != null && !securityRole.id.equals(id)) {
							errors.add(new ValidationError("name", "Security name already exists."));
							return errors;
				}
				return null;
	}
	
	 public List<SecurityRole> getAllSecurityRoles(){
			return find.where()
								.findList();
	 }
}
