package based.category;

import org.javalite.activejdbc.Model;

public class Category extends Model {
	@SuppressWarnings("unused")
	private String name;
	@SuppressWarnings("unused")
	private double weight;
	
	 static{
	        validatePresenceOf("name", "weight");
	    }    
	

}
