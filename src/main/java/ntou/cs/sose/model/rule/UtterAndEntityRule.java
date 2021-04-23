package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public class UtterAndEntityRule extends ParameterNameRule {
	@Override
	public ArrayList<String> checkRules(BotenSwagger botenSwagger) {
		JSONObject swagger = botenSwagger.getSwagger();
		ArrayList<String> chatbotEnabledSwaggerErrors = new ArrayList<String>();
		ArrayList<String> allPath = botenSwagger.allPath();
		JSONObject paths = swagger.getJSONObject("paths");
		for (int i = 0; i < allPath.size(); i++) {
			String path = (String) allPath.get(i);
			JSONObject pathObj = paths.getJSONObject(path);
			try {
				JSONObject getObj = pathObj.getJSONObject("get");
				// Check bot-utter
				chatbotEnabledSwaggerErrors.addAll(checkUtterAndEntity(botenSwagger, path, getObj, "x-bot-utter"));
				// Check user-entity
				chatbotEnabledSwaggerErrors.addAll(checkUtterAndEntity(botenSwagger, path, getObj, "x-user-entity"));
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		}
		return chatbotEnabledSwaggerErrors;

	}

	public ArrayList<String> checkUtterAndEntity(BotenSwagger botenSwagger, String path, JSONObject getObj,
			String xName) {
		ArrayList<String> errorMessages = new ArrayList<String>();
		JSONObject swagger = botenSwagger.getSwagger();
		// Check params of x-bot-utter and x-user-entity
		try {
			org.json.JSONArray xArr = getObj.getJSONArray(xName);
			ArrayList<String> parArr = JsonPath.read(xArr.toString(), "$..parameterName");
			ArrayList<Object> allRequireParameter = botenSwagger.getRequireParameters(path);
			// check RequireParameter
			for (int j = 0; j < allRequireParameter.size(); j++) {
				String requireParameter = (String) allRequireParameter.get(j);
				if (!parArr.contains(requireParameter)) {
					errorMessages.add("#/paths/" + path + "/" + xName + "/parameterName: The required parameter ["
							+ requireParameter + "] is not filled");
				}
			}
			// Check if there is this parameter in Swagger
			for (int j = 0; j < parArr.size(); j++) {
				String parameter = (String) parArr.get(j);
				if (!checkParameterNames(swagger, path, parameter)) {
					errorMessages.add("#/paths/" + path + "/" + xName + "/parameterName: The parameter of [" + parameter
							+ "] not found in Swagger");
				}
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return errorMessages;
	}
}
