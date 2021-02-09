package ntou.cs.sose.model;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;

import org.json.*;

public class SwaggerChecker {
	    
	public String test() {
	    JSONObject jsonSchema = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/schema.json")));
	    Schema schema = SchemaLoader.load(jsonSchema);
	    try {
	    	  schema.validate(new JSONObject("{\"test\" : {\"t\":{}}}") );
	    	} catch (ValidationException e) {
	    	  // prints #/rectangle/a: -5.0 is not higher or equal to 0
	    	  System.out.println(e.getMessage());
	    	  // prints all ValidationException
	    	  e.getCausingExceptions().stream()
	    	      .map(ValidationException::getMessage)
	    	      .forEach(System.out::println);
	    	}
	    return null;
	}
}
