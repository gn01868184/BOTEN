package ntou.cs.sose.model.rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ntou.cs.sose.entity.BotenSwagger;

public class SwaggerValidator implements BotenRule {
	@Override
	public ArrayList<String> checkRules(BotenSwagger botenSwagger) {
		String swaggerUrl = botenSwagger.getUrl();
		ArrayList<String> chatbotEnabledSwaggerErrors = new ArrayList<String>();

		try {
			StringBuilder stringBuilder = new StringBuilder("https://validator.swagger.io/validator/debug");
			stringBuilder.append("?url=");
			stringBuilder.append(URLEncoder.encode(swaggerUrl, "UTF-8"));
			URL url = new URL(stringBuilder.toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			BufferedReader br = null;
			if (200 <= con.getResponseCode() && con.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String responseBody = br.lines().collect(Collectors.joining());
				JsonObject convertedObject = new Gson().fromJson(responseBody, JsonObject.class);
				try {
					JsonArray messages = convertedObject.get("schemaValidationMessages").getAsJsonArray();
					for (int i = 0; i < messages.size(); i++) {
						chatbotEnabledSwaggerErrors.add(((JsonObject) messages.get(i)).get("message").toString());
					}
				} catch (NullPointerException e) {
					System.out.println("SwaggerValidator:" + e.getMessage());
				}
			} else {
				chatbotEnabledSwaggerErrors.add("Response error: " + con.getResponseCode());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chatbotEnabledSwaggerErrors;
	}

}
