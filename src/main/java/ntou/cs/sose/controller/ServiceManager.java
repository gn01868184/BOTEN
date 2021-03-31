package ntou.cs.sose.controller;

import org.json.JSONObject;

import com.google.gson.Gson;

import ntou.cs.sose.entity.BotenSwagger;
import ntou.cs.sose.model.ChatbotConfigurator;
import ntou.cs.sose.model.InputOutputHandler;
import ntou.cs.sose.model.MyHttpURLConnection;
import ntou.cs.sose.model.RasaConfigurator;
import ntou.cs.sose.model.SwaggerChecker;

public class ServiceManager {
	BotenSwagger botenSwagger = new BotenSwagger();
	Gson gson = new Gson();

	public String doSwaggerCheck(String swaggerURL) {
		MyHttpURLConnection httpConnection = new MyHttpURLConnection();
		String swagger = httpConnection.connection(swaggerURL);
		botenSwagger.setSwagger(new JSONObject(swagger));
		System.out.println(botenSwagger.getSwagger());

		SwaggerChecker sc = new SwaggerChecker();
		String chatbotEnabledSwaggerErrors = gson.toJson(sc.swaggerChecker(botenSwagger));
		botenSwagger.setChatbotEnabledSwaggerErrors(new JSONObject(chatbotEnabledSwaggerErrors));
		return botenSwagger.getChatbotEnabledSwaggerErrors().toString(2);
	}

	public String doInputOutputHandler() {
		InputOutputHandler ioc = new InputOutputHandler();
		String inputOutputConfig = gson.toJson(ioc.inputOutputHandler(botenSwagger));
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
