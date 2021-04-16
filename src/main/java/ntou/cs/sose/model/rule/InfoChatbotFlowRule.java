package ntou.cs.sose.model.rule;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import ntou.cs.sose.entity.BotenSwagger;

public class InfoChatbotFlowRule implements BotenRule {
	@Override
	public ArrayList<String> checkRules(BotenSwagger botenSwagger) {
		JSONObject swagger = botenSwagger.getSwagger();
		HashMap<String, Object> chatbotFlow = botenSwagger.chatbotFlow();
		ArrayList<String> chatbotEnabledSwaggerErrors = new ArrayList<String>();

		// 檢查info-x-chatbotFlow與Swagger內的path是否對應
		JSONObject info = swagger.getJSONObject("info");
		if (info.has("x-chatbotFlow")) {
			for (Object flowName : chatbotFlow.keySet()) {
				ArrayList<String> pathsArr = (ArrayList<String>) chatbotFlow.get(flowName);
				for (int i = 0; i < pathsArr.size(); i++) {
					try {
						JsonPath.read(swagger.toString(), "$.paths.['" + pathsArr.get(i) + "']");
					} catch (PathNotFoundException e) {
						System.out.println(e.getMessage());
						chatbotEnabledSwaggerErrors.add("#/info/x-chatbotFlow/flow: The path of [" + pathsArr.get(i)
								+ "] not found in Swagger");
					}
				}
			}
		}
		return chatbotEnabledSwaggerErrors;
	}

}
