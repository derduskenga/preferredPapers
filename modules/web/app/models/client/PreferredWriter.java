package models.client;

import play.data.validation.Constraints;
import javax.persistence.*;
import play.mvc.*;
import play.db.ebean.Model;
import models.client.Client;
import models.writer.FreelanceWriter;

@Entity
public class PreferredWriter extends Model{
	@Id
	public Long preferred_writer_entry_id;
	@ManyToOne
	public Client client;
	@ManyToOne
	public FreelanceWriter freelanceWriter;
	
}

