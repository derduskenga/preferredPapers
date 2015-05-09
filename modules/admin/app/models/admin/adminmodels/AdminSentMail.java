package models.admin.adminmodels;

import play.data.validation.Constraints;
import javax.persistence.*;
import play.mvc.*;
import play.db.ebean.Model;
import models.client.Client;
import java.util.Date;


@Entity
public class AdminSentMail extends Model{
		@Id
		public Long admin_sent_mail_id;
		@Column(nullable=false)
		@ManyToOne
		public AdminUser adminUser;
		
		@Column(nullable=false)
		@Lob
		public String message;

		@Column(nullable=false)
		public String sent_to;
		
		@Column(nullable = false)
		public Date sent_on;
		
		public void saveAdminSentMail(){
			if(admin_sent_mail_id == null)
				save();
			else	
				update();
		}
}