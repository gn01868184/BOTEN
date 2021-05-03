package ntou.cs.sose.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

import ntou.cs.sose.entity.BotenSwagger;
import ntou.cs.sose.model.ChatbotConfigurator;
import ntou.cs.sose.model.InputOutputHandler;
import ntou.cs.sose.model.RasaConfigurator;
import ntou.cs.sose.model.rule.BotenRule;
import ntou.cs.sose.model.rule.SwaggerChecker;

public class ServiceManager {
	BotenSwagger botenSwagger;
	Gson gson = new Gson();

	public String doSwaggerCheck(String swaggerURL) {
		botenSwagger = new BotenSwagger(swaggerURL);
		System.out.println(botenSwagger.getSwagger());
		SwaggerChecker swaggerChecker = new SwaggerChecker();
		HashMap<String, Object> chatbotEnabledSwaggerErrors = new HashMap<String, Object>();
		ArrayList<String> message = new ArrayList<String>();

		InputStream inputStream = getClass().getResourceAsStream("/rule.yml");
		Yaml yaml = new Yaml();
		Map<String, ArrayList<String>> data = yaml.load(inputStream);
		ArrayList<String> rules = data.get("applied-rules");
		for (String rule : rules) {
			System.out.println(rule);
			try {
				Class c = Class.forName(rule);
				BotenRule obj = (BotenRule) c.newInstance();
				swaggerChecker.setBotenRule(obj);
				if (!swaggerChecker.execute(botenSwagger).equals(new ArrayList<String>())) {
					message.addAll(swaggerChecker.execute(botenSwagger));
					break;
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
				message.add("InstantiationException: " + e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				message.add("IllegalAccessException: " + e.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				message.add("ClassNotFoundException: " + e.getMessage());
			}
		}

		chatbotEnabledSwaggerErrors.put("chatbot-enabled swagger errors", message);
		String chatbotEnabledSwaggerErrorsStr = gson.toJson(chatbotEnabledSwaggerErrors);
		botenSwagger.setChatbotEnabledSwaggerErrors(new JSONObject(chatbotEnabledSwaggerErrorsStr));
		return botenSwagger.getChatbotEnabledSwaggerErrors().toString(2);
	}

	public String doInputOutputHandler() {
		InputOutputHandler ioc = new InputOutputHandler(botenSwagger);
		String inputOutputConfig = gson.toJson(ioc.getRes());
		botenSwagger.setInputOutputConfig(new JSONObject(inputOutputConfig));
		return botenSwagger.getInputOutputConfig().toString(2);
	}

	public String reviseInputOutputConfig(String inputOutputConfig) {
		System.out.println(inputOutputConfig);
		botenSwagger.setInputOutputConfig(new JSONObject(inputOutputConfig));
		return botenSwagger.getInputOutputConfig().toString(2);
	}

	public String doChatbotConfigurator() {
		ChatbotConfigurator cc = new ChatbotConfigurator(botenSwagger);
		RasaConfigurator rc = new RasaConfigurator();
		String chatbotConfigurator = gson.toJson(cc.getConfig());
		botenSwagger.setBotenConfig(new JSONObject(chatbotConfigurator));
		botenSwagger.setNlu(rc.nluConfigurator(botenSwagger.getInputOutputConfig()));
		botenSwagger.setDomain(rc.domainConfigurator(botenSwagger.getInputOutputConfig()));
		botenSwagger.setStories(rc.storiesConfigurator(botenSwagger.getInputOutputConfig()));
		return "{\"status code\": 200}";
	}

	public String showBotenConfig() {
		return botenSwagger.getBotenConfig().toString(2);
	}

	public String showNlu() {
		return botenSwagger.getNlu();
	}

	public String showDomain() {
		return botenSwagger.getDomain();
	}

	public String showStories() {
		return botenSwagger.getStories();
	}

}
