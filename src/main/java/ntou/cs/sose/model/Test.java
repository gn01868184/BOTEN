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

		BotenSwagger botenSwagger = new BotenSwagger();

		SwaggerChecker sc = new SwaggerChecker();
		InputOutputHandler ioc = new InputOutputHandler();
		ChatbotConfigurator cc = new ChatbotConfigurator();
		RasaConfigurator rc = new RasaConfigurator();
		
		MyHttpURLConnection httpConnection = new MyHttpURLConnection();
		String swagger = httpConnection.connection("https://gn01868184.github.io/jsonExample.github.io/error.json");
		botenSwagger.setSwagger(new JSONObject(swagger));
		System.out.println(botenSwagger.getSwagger());

		String chatbotEnabledSwaggerErrors = gson.toJson(sc.swaggerChecker(botenSwagger));
		System.out.println(sc.swaggerChecker(botenSwagger));
		botenSwagger.setChatbotEnabledSwaggerErrors(new JSONObject(chatbotEnabledSwaggerErrors));
		
		
	}
}
