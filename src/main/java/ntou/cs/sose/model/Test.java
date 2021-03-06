package ntou.cs.sose.model;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Test {
	public static void main(String[] args) {
		Test te = new Test();
	    Class tes = te.getClass();
		JSONObject swagger = new JSONObject(new JSONTokener(tes.getResourceAsStream("/foursquareSwagger.json")));
		SwaggerChecker sc = new SwaggerChecker();
		sc.swaggerChecker(swagger);
		
		InputOutputHandler ioc = new InputOutputHandler();
		ioc.inputOutputHandler(swagger);
		
		ChatbotConfigurator cc = new ChatbotConfigurator();
		cc.chatbotConfigurator(swagger);
		System.out.println(cc.botenConfig());
	}
}
