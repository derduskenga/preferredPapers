package models.client;

import play.data.validation.Constraints;
import javax.persistence.*;
import play.mvc.*;
import play.db.ebean.Model;
import models.client.Client;
import models.writer.FreelanceWriter;
import java.util.Date;

@Entity
public class ClientSentMail extends Model{
	@Id
	public Long client_sent_mail_id;
	@Column(nullable=false)
	@ManyToOne
	public Client client;
	@Column(nullable=false)
	@Lob
	public String message;

	@Column(nullable=false)
	public String sent_to;
	
	@Column(nullable = false)
	public Date sent_on;
	
	public void saveClientSentMail(){
		if(client_sent_mail_id == null)
			save();
		else	
			update();
	}
}

 
