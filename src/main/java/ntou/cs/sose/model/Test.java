package ntou.cs.sose.model;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileWriter;
import java.io.IOException;

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

		bs.setNlu(rc.nluConfigurator(bs.getInputOutputConfig()));
		bs.setDomain(rc.domainConfigurator(bs.getInputOutputConfig()));
		bs.setStories(rc.storiesConfigurator(bs.getInputOutputConfig()));

		String chatbotConfigurator = gson.toJson(cc.chatbotConfigurator(bs));
		bs.setBotenConfig(new JSONObject(chatbotConfigurator));
		
		FileWriter fw;
		try {
			fw = new FileWriter("nlu.md");
			fw.write(bs.getNlu());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fw = new FileWriter("domain.yml");
			fw.write(bs.getDomain());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fw = new FileWriter("stories.md");
			fw.write(bs.getStories());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fw = new FileWriter("botenConfig.json");
			fw.write(bs.getBotenConfig().toString(2));
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print(cc.chatbotConfigurator(bs));
	}
}
