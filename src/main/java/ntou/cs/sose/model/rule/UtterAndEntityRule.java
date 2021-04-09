package ntou.cs.sose.model.rule;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public class UtterAndEntityRule implements BotenRule {
	@Override
	public ArrayList checkRule(BotenSwagger botenSwagger) {
		JSONObject swagger = botenSwagger.getSwagger();
		ArrayList chatbotEnabledSwaggerErrors = new ArrayList();
		ArrayList allPath = botenSwagger.allPath();
		HashMap<String, Object> chatbotFlow = botenSwagger.chatbotFlow();
		JSONObject paths = swagger.getJSONObject("paths");
		for (int i = 0; i < allPath.size(); i++) {
			String path = (String) allPath.get(i);
			JSONObject pathObj = paths.getJSONObject(path);
			try {
				JSONObject getObj = pathObj.getJSONObject("get");
				// Check bot-utter
				chatbotEnabledSwaggerErrors.addAll(checkUtterAndEntity(swagger, path, getObj, "x-bot-utter"));
				// Check user-entity
				chatbotEnabledSwaggerErrors.addAll(checkUtterAndEntity(swagger, path, getObj, "x-user-entity"));
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		}
		return chatbotEnabledSwaggerErrors;

	}

	public ArrayList checkUtterAndEntity(JSONObject swagger, String path, JSONObject getObj, String xNmae) {
		ArrayList errorMessages = new ArrayList();
		// Check params of x-bot-utter and x-user-entity
		try {
			org.json.JSONArray xArr = getObj.getJSONArray(xNmae);
			ArrayList parArr = JsonPath.read(xArr.toString(), "$..parameterName");
			ArrayList allRequireParameter = BotenRule.getRequireParameter(swagger, path);
			// check RequireParameter
			for (int j = 0; j < allRequireParameter.size(); j++) {
				String requireParameter = (String) allRequireParameter.get(j);
				if (!parArr.contains(requireParameter)) {
					errorMessages.add("#/paths/" + path + "/" + xNmae + "/parameterName: The required parameter ["
							+ requireParameter + "] is not filled");
				}
			}
			// Check if there is this parameter in Swagger
			for (int j = 0; j < parArr.size(); j++) {
				String parameter = (String) parArr.get(j);
				if (!BotenRule.checkParameterName(swagger, path, parameter)) {
					errorMessages.add("#/paths/" + path + "/" + xNmae + "/parameterName: The parameter of [" + parameter
							+ "] not found in Swagger");
				}
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return errorMessages;
	}
}
