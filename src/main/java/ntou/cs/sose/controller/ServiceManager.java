package ntou.cs.sose.controller;

import org.json.JSONObject;

import com.google.gson.Gson;

import ntou.cs.sose.entity.BotenSwagger;
import ntou.cs.sose.model.ChatbotConfigurator;
import ntou.cs.sose.model.InputOutputHandler;
import ntou.cs.sose.model.MyHttpURLConnection;
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
		String chatbotEnabledSwaggerErrors = gson.toJson(sc.swaggerChecker(botenSwagger.getSwagger()));
		botenSwagger.setChatbotEnabledSwaggerErrors(new JSONObject(chatbotEnabledSwaggerErrors));
		return botenSwagger.getChatbotEnabledSwaggerErrors().toString();
	}

	public String doInputOutputHandler() {
		InputOutputHandler ioc = new InputOutputHandler();
		String inputOutputConfig = gson.toJson(ioc.inputOutputHandler(botenSwagger.getSwagger()));
		botenSwagger.setInputOutputConfig(new JSONObject(inputOutputConfig));
		return botenSwagger.getInputOutputConfig().toString();
	}

	public String doChatbotConfigurator() {
		ChatbotConfigurator cc = new ChatbotConfigurator();
		String chatbotConfigurator = gson.toJson(cc.chatbotConfigurator(botenSwagger.getSwagger()));
		botenSwagger.setChatbotConfigurator(new JSONObject(chatbotConfigurator));
		return botenSwagger.getChatbotConfigurator().toString();
	}
}
