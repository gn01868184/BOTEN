package ntou.cs.sose.model.rule;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public class PathsChatbotFlowRule implements BotenRule {
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
				// Check chatbotFlow
				try {
					org.json.JSONArray xChatbotFlow = getObj.getJSONArray("x-chatbotFlow");
					for (int j = 0; j < xChatbotFlow.length(); j++) {
						boolean checkFlowName = false;
						boolean checkPath = false;
						JSONObject flowObj = (JSONObject) xChatbotFlow.get(j);
						// Check the correspondence between chatbotFlow and paths of info
						for (Object flowName : chatbotFlow.keySet()) {
							if (flowObj.getString("flowName").equals(flowName)) {
								ArrayList flowPathsArr = (ArrayList) chatbotFlow.get(flowName);
								for (int z = 0; z < flowPathsArr.size(); z++) {
									if (flowPathsArr.get(z).equals(path)) {
										checkPath = true;
										break;
									}
								}
								checkFlowName = true;
								break;
							}
						}
						if (!checkFlowName) {
							chatbotEnabledSwaggerErrors.add("#/paths/" + path + "/x-chatbotFlow/flowName: Unfilled ["
									+ flowObj.getString("flowName") + "] in info/x-chatbotFlow/{int}/flowName");
						}
						if (!checkPath) {
							chatbotEnabledSwaggerErrors.add("#/paths/" + path + "/x-chatbotFlow/: Unfilled [" + path
									+ "] not found in " + flowObj.getString("flowName"));
						}
						// Check getSlots_parameterName
						try {
							ArrayList flowName = JsonPath.read(flowObj.toString(), "$..[?(@.getSlots)].flowName");
							ArrayList resParameterName = JsonPath.read(flowObj.toString(),
									"$..[?(@.getSlots)]..parameterName");
							if (!resParameterName.isEmpty()) {
								for (int k = 0; k < resParameterName.size(); k++) {
									String par = (String) resParameterName.get(k);
									if (!BotenRule.checkParameterName(swagger, path, par)) {
										chatbotEnabledSwaggerErrors.add("#/paths/" + path
												+ "/x-chatbotFlow/getSlots/parameterName: The parameter of [" + par
												+ "] not found in Swagger");
									}
									if (!BotenRule.checkFlowParameterName(swagger, (String) flowName.get(0), par)) {
										chatbotEnabledSwaggerErrors.add("#/paths/" + path
												+ "/x-chatbotFlow/getSlots/parameterName: The parameter of [" + par
												+ "] not found in responseToSlots");
									}
								}
							}
						} catch (ClassCastException e) {
							System.out.println(e.getMessage());
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
