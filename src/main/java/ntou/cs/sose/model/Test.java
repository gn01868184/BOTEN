package ntou.cs.sose.model;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;

import ntou.cs.sose.entity.BotenSwagger;

public class Test {
	public static void main(String[] args) {
		Gson gson = new Gson();
		
		Test te = new Test();
		Class tes = te.getClass();
		JSONObject swagger = new JSONObject(new JSONTokener(tes.getResourceAsStream("/foursquareSwagger.json")));

		BotenSwagger bs = new BotenSwagger();
		bs.setSwagger(swagger);

		SwaggerChecker sc = new SwaggerChecker();
		InputOutputHandler ioc = new InputOutputHandler();
		ChatbotConfigurator cc = new ChatbotConfigurator();
		RasaConfigurator rc = new RasaConfigurator();
		
		sc.swaggerChecker(bs);
		
		String inputOutputConfig = gson.toJson(ioc.inputOutputHandler(bs));
		bs.setInputOutputConfig(new JSONObject(inputOutputConfig));
		
		cc.chatbotConfigurator(bs);
		
		bs.setNlu(rc.nluConfigurator(bs.getInputOutputConfig()));
		bs.setStories(rc.storiesConfigurator(bs.getInputOutputConfig()));
		
		System.out.println(bs.getStories());

	}
}
