package ntou.cs.sose.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import ntou.cs.sose.model.HttpConnection;
import ntou.cs.sose.model.InputOutputHandler;
import ntou.cs.sose.model.SwaggerChecker;

import org.json.*;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {
	String swagger;
	JSONObject swaggerObj = new JSONObject();
	Gson gson = new Gson();

	@RequestMapping("/home")
	public String greeting() {
		return "index";
	}

	@GetMapping(value = "/swaggerCheck", produces = "application/json")
	@ResponseBody
	public String swaggerCheck(@RequestParam String swaggerURL) {
		HttpConnection httpConnection = new HttpConnection();
		swagger = httpConnection.connection(swaggerURL);
		swaggerObj = new JSONObject(swagger);
		System.out.println(swaggerObj);

		SwaggerChecker sc = new SwaggerChecker();
		String json = gson.toJson(sc.sc(swaggerObj));
		return json.toString();
	}

	@GetMapping(value = "/InputOutputHandler", produces = "application/json")
	@ResponseBody
	public String test() {
		InputOutputHandler ioc = new InputOutputHandler();
		String json = gson.toJson(ioc.ioc(swaggerObj));
		return json.toString();
	}

}