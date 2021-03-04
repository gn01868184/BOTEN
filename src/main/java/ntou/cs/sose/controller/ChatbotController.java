package ntou.cs.sose.controller;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import ntou.cs.sose.model.MyHttpURLConnection;
import ntou.cs.sose.entity.BotenSwagger;
import ntou.cs.sose.model.ChatbotConfigurator;
import ntou.cs.sose.model.InputOutputHandler;
import ntou.cs.sose.model.SwaggerChecker;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {
	BotenSwagger botenSwagger = new BotenSwagger();
	Gson gson = new Gson();

	@RequestMapping("/home")
	public String greeting() {
		return "index";
	}

	@GetMapping(value = "/swaggerCheck", produces = "application/json")
	@ResponseBody
	public String swaggerCheck(@RequestParam String swaggerURL) {
		MyHttpURLConnection httpConnection = new MyHttpURLConnection();
		String swagger = httpConnection.connection(swaggerURL);
		botenSwagger.setSwagger(new JSONObject(swagger));
		System.out.println(botenSwagger.getSwagger());

		SwaggerChecker sc = new SwaggerChecker();
		String json = gson.toJson(sc.sc(botenSwagger.getSwagger()));
		return json.toString();
	}

	@GetMapping(value = "/InputOutputHandler", produces = "application/json")
	@ResponseBody
	public String inputOutputHandler() {
		InputOutputHandler ioc = new InputOutputHandler();
		String json = gson.toJson(ioc.ioc(botenSwagger.getSwagger()));
		return json.toString();
	}

	@GetMapping(value = "/ChatbotConfigurator", produces = "application/json")
	@ResponseBody
	public String chatbotConfigurator() {
		ChatbotConfigurator chatbotConfigurator = new ChatbotConfigurator();
		String json = gson.toJson(chatbotConfigurator.chatbotConfigurator(botenSwagger.getSwagger()));
		return json.toString();
	}
}