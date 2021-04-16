package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public class UtterAndEntityCorrespondRule implements BotenRule {
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
				// Check if x-bot-utter and x-user-entity correspond
				try {
					org.json.JSONArray botUtter = getObj.getJSONArray("x-bot-utter");
					org.json.JSONArray userEntity = getObj.getJSONArray("x-user-entity");
					ArrayList<String> botParArr = JsonPath.read(botUtter.toString(), "$..parameterName");
					ArrayList<String> userParArr = JsonPath.read(userEntity.toString(), "$..parameterName");
					// Check if the parameter x-user-entity filled in by x-bot-utter is filled in
					for (int j = 0; j < botParArr.size(); j++) {
						String botParameter = (String) botParArr.get(j);
						if (!userParArr.contains(botParameter)) {
							chatbotEnabledSwaggerErrors.add("#/paths/" + path
									+ "/x-user-entity/parameterName: Unfilled [" + botParameter + "]");
						}
					}
					// Check whether the parameter x-bot-utter filled in by x-user-entity is filled
					// in
					for (int j = 0; j < userParArr.size(); j++) {
						String userParameter = (String) userParArr.get(j);
						if (!botParArr.contains(userParameter)) {
							chatbotEnabledSwaggerErrors.add(
									"#/paths/" + path + "/x-bot-utter/parameterName: Unfilled [" + userParameter + "]");
						}
					}
				} catch (JSONException e) {
					System.out.println(e.getMessage());
				}

			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		}
		return chatbotEnabledSwaggerErrors;

	}

}
