package ntou.cs.sose.model;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Test {
	public static void main(String[] args) {
		Test te = new Test();
	    Class tes = te.getClass();
		JSONObject swagger = new JSONObject(new JSONTokener(tes.getResourceAsStream("/foursquareSwagger.json")));
		SwaggerChecker sc = new SwaggerChecker();
		sc.sc(swagger);
		
		InputOutputHandler ioc = new InputOutputHandler();
		ioc.ioc(swagger);
		
		ChatbotConfigurator chatbotConfigurator = new ChatbotConfigurator();
		chatbotConfigurator.chatbotConfigurator(swagger);
		System.out.println(ioc.setFlow());
	}
}
