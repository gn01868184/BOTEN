package ntou.cs.sose.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ntou.cs.sose.entity.BotenSwagger;
import ntou.cs.sose.model.ChatbotConfigurator;
import ntou.cs.sose.model.DomainHandler;
import ntou.cs.sose.model.InputOutputHandler;
import ntou.cs.sose.model.NluHandler;
import ntou.cs.sose.model.RulesHandler;
import ntou.cs.sose.model.StroiesHandler;
import ntou.cs.sose.model.TitleConfigurator;
import ntou.cs.sose.model.rule.BotenRule;
import ntou.cs.sose.model.rule.SwaggerChecker;

public class ServiceManager {
	BotenSwagger botenSwagger;
	Gson gson = new GsonBuilder().serializeNulls().create();

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
		NluHandler nluHandler = new NluHandler(botenSwagger);
		DomainHandler domainHandler = new DomainHandler(botenSwagger);
		StroiesHandler stroiesHandler = new StroiesHandler(botenSwagger);
		RulesHandler rulesHandler = new RulesHandler(botenSwagger);
		HashMap<String, Object> rasaConfig = new HashMap<String, Object>();
		rasaConfig.put("nlu", nluHandler.getNlu());
		rasaConfig.put("domain", domainHandler.getDomain());
		rasaConfig.put("stories", stroiesHandler.getStroies());
		rasaConfig.put("rules", rulesHandler.getRules());

		try {
			String inputOutputConfig = new ObjectMapper().writeValueAsString(rasaConfig);
			botenSwagger.setInputOutputConfig(inputOutputConfig);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return botenSwagger.getInputOutputConfig();
	}

	public String reviseInputOutputConfig(String inputOutputConfig) {
		System.out.println(inputOutputConfig);
		botenSwagger.setInputOutputConfig(inputOutputConfig);
		return botenSwagger.getInputOutputConfig();
	}

	public String doChatbotConfigurator() {
		ChatbotConfigurator chatbotConfigurator = new ChatbotConfigurator(botenSwagger);
		InputOutputHandler inputOutputHandler = new InputOutputHandler();
		String chatbotConfig = gson.toJson(chatbotConfigurator.getConfig());
		botenSwagger.setBotenConfig(new JSONObject(chatbotConfig));
		try {
			ObjectMapper mapper = new ObjectMapper();
			String inputOutputConfig = botenSwagger.getInputOutputConfig();
			LinkedHashMap<String, Object> map = mapper.readValue(inputOutputConfig, LinkedHashMap.class);
			botenSwagger.setNlu(inputOutputHandler.jsonToYaml(new ObjectMapper().writeValueAsString(map.get("nlu"))));
			botenSwagger
					.setDomain(inputOutputHandler.jsonToYaml(new ObjectMapper().writeValueAsString(map.get("domain"))));
			botenSwagger.setStories(
					inputOutputHandler.jsonToYaml(new ObjectMapper().writeValueAsString(map.get("stories"))));
			botenSwagger
					.setRules(inputOutputHandler.jsonToYaml(new ObjectMapper().writeValueAsString(map.get("rules"))));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public String showRules() {
		return botenSwagger.getRules();
	}

	public String showTitle() {
		TitleConfigurator titleConfigurator = new TitleConfigurator(botenSwagger);
		String title = gson.toJson(titleConfigurator.getTitle());
		return title;
	}
}
