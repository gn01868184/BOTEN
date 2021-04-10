package ntou.cs.sose.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;

import ntou.cs.sose.entity.BotenSwagger;
import ntou.cs.sose.model.ChatbotConfigurator;
import ntou.cs.sose.model.InputOutputHandler;
import ntou.cs.sose.model.MyHttpURLConnection;
import ntou.cs.sose.model.RasaConfigurator;
import ntou.cs.sose.model.rule.Rule;

public class ServiceManager {
	BotenSwagger botenSwagger = new BotenSwagger();
	Gson gson = new Gson();

	public String doSwaggerCheck(String swaggerURL) {
		MyHttpURLConnection httpConnection = new MyHttpURLConnection();
		botenSwagger.setUrl(swaggerURL);
		String swagger = httpConnection.connection(botenSwagger.getUrl());
		botenSwagger.setSwagger(new JSONObject(swagger));
		System.out.println(botenSwagger.getSwagger());
		HashMap<String, Object> chatbotEnabledSwaggerErrors = new HashMap<String, Object>();
		ArrayList message = new ArrayList();
		for (Rule rule : Rule.values()) {
			System.out.println(rule);
			if (!rule.doValidation(botenSwagger).equals(new ArrayList())) {
				message.addAll(rule.doValidation(botenSwagger));
				break;
			}
		}
		chatbotEnabledSwaggerErrors.put("chatbot-enabled swagger errors", message);
		String chatbotEnabledSwaggerErrorsStr = gson.toJson(chatbotEnabledSwaggerErrors);
		botenSwagger.setChatbotEnabledSwaggerErrors(new JSONObject(chatbotEnabledSwaggerErrorsStr));
		return botenSwagger.getChatbotEnabledSwaggerErrors().toString(2);
	}

	public String doInputOutputHandler() {
		InputOutputHandler ioc = new InputOutputHandler();
		String inputOutputConfig = gson.toJson(ioc.inputOutputHandler(botenSwagger));
		botenSwagger.setInputOutputConfig(new JSONObject(inputOutputConfig));
		return botenSwagger.getInputOutputConfig().toString(2);
	}

	public String reviseInputOutputConfig(String inputOutputConfig) {
		System.out.println(inputOutputConfig);
		botenSwagger.setInputOutputConfig(new JSONObject(inputOutputConfig));
		return botenSwagger.getInputOutputConfig().toString(2);
	}

	public String doChatbotConfigurator() {
		ChatbotConfigurator cc = new ChatbotConfigurator();
		RasaConfigurator rc = new RasaConfigurator();
		String chatbotConfigurator = gson.toJson(cc.chatbotConfigurator(botenSwagger));
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
