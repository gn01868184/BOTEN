package ntou.cs.sose.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import ntou.cs.sose.model.SwaggerChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.stream.Collectors;

import org.json.*;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {
	String swagger;
	
	 @RequestMapping("/home")
	    public String greeting() {
	        return "index";
	    }
	
	@GetMapping(value = "/swaggerCheck", produces = "application/json")
	@ResponseBody
    public String swaggerCheck(@RequestParam String swaggerURL) {
		try {
			URL url = new URL(swaggerURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			
			BufferedReader br = null;
			if (200 <= con.getResponseCode() && con.getResponseCode() <= 399) {
			    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			    String responseBody = br.lines().collect(Collectors.joining());
			    swagger = responseBody;
			    JSONObject jsonObject = new JSONObject(responseBody);
			    System.out.println(jsonObject);
				SwaggerChecker sc = new SwaggerChecker();
				Gson gson = new Gson();
				String json = gson.toJson(sc.sc(jsonObject)); 
			    return json.toString();
			} else {
			    System.out.println("Error: " + con.getResponseCode());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		  }
		return null;
    }
	
	@GetMapping(value = "/s", produces = "application/json")
	@ResponseBody
    public String test() {
		return swagger;
    }

}