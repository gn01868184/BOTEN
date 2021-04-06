package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;

import net.minidev.json.JSONArray;
import ntou.cs.sose.entity.BotenSwagger;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class SwaggerChecker {
	private JSONObject jsonSchema = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/schema.json")));
	private Schema schema = SchemaLoader.load(jsonSchema);
	private JSONObject swagger;

	private ArrayList allPath;
	private HashMap<String, Object> chatbotFlow;
	private HashMap<String, Object> swaggerErrorJson = new HashMap<String, Object>();
	private ArrayList<String> errorMessages = new ArrayList<String>();

	public HashMap<String, Object> swaggerChecker(BotenSwagger req) {
		swagger = req.getSwagger();
		chatbotFlow = req.chatbotFlow();
		allPath = req.allPath();
		if (jsonSchema()) {
			checkInfo();
			checkPaths();
		}
		System.out.println(errorMessages);
		swaggerErrorJson.put("chatbot-enabled swagger errors", errorMessages);
		return swaggerErrorJson;
	}

	public boolean jsonSchema() {
		try {
			schema.validate(swagger);
			return true;
		} catch (ValidationException e) {
			System.out.println("jsonSchema:");
			System.out.println(e.getMessage());
			errorMessages.add(e.getMessage());
			return false;
		}
	}

	public void checkInfo() {
		// 檢查info-x-chatbotFlow與Swagger內的path是否對應
		JSONObject info = swagger.getJSONObject("info");
		if (info.has("x-chatbotFlow")) {
			for (Object flowName : chatbotFlow.keySet()) {
				ArrayList pathsArr = (ArrayList) chatbotFlow.get(flowName);
				for (int i = 0; i < pathsArr.size(); i++) {
					try {
						JsonPath.read(swagger.toString(), "$.paths." + pathsArr.get(i));
					} catch (PathNotFoundException e) {
						System.out.println(e.getMessage());
						errorMessages.add("#/info/x-chatbotFlow/flow: The path of [" + pathsArr.get(i)
								+ "] not found in Swagger");
					}
				}
			}
		}
	}

	public void checkPaths() {
		JSONObject paths = swagger.getJSONObject("paths");
		for (int i = 0; i < allPath.size(); i++) {
			String path = (String) allPath.get(i);
			JSONObject pathObj = paths.getJSONObject(path);
			try {
				JSONObject getObj = pathObj.getJSONObject("get");
				// check chatbotFlow
				chatbotFlow(path, getObj);
				// check bot-utter
				checkUtterAndEntity(path, getObj, "x-bot-utter");
				// check user-entity
				checkUtterAndEntity(path, getObj, "x-user-entity");
				// check bot-utter user-entity 對應
				checkUtterAndEntityCorrespond(path, getObj);
				// check jsonpPath-result
				checkResult(path, getObj);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void chatbotFlow(String path, JSONObject getObj) {
		// 檢查x-chatbotFlow
		try {
			org.json.JSONArray xChatbotFlow = getObj.getJSONArray("x-chatbotFlow");
			for (int j = 0; j < xChatbotFlow.length(); j++) {
				boolean checkFlowName = false;
				boolean checkPath = false;
				JSONObject flowObj = (JSONObject) xChatbotFlow.get(j);
				// 檢查與info對應
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
					errorMessages.add("#/paths/" + path + "/x-chatbotFlow/flowName: Unfilled ["
							+ flowObj.getString("flowName") + "] in info/x-chatbotFlow/{int}/flowName");
				}
				if (!checkPath) {
					errorMessages.add("#/paths/" + path + "/x-chatbotFlow/: Unfilled [" + path + "] not found in "
							+ flowObj.getString("flowName"));
				}
				// 檢查responseToSlots_jsonPath
				try {
					ArrayList JsonPathArr = JsonPath.read(flowObj.toString(), "$..jsonPath");
					if (!JsonPathArr.isEmpty()) {
						for (int k = 0; k < JsonPathArr.size(); k++) {
							String jsonPath = (String) JsonPathArr.get(k);
							if (!checkJsonPath(jsonPath)) {
								errorMessages.add("#/paths/" + path + "/x-chatbotFlow/responseToSlots/jsonPath: ["
										+ jsonPath + "] is not jsonPath");
							}
						}
					}
				} catch (ClassCastException e) {
					System.out.println(e.getMessage());
				}
				// 檢查getSlots_parameterName
				try {
					ArrayList flowName = JsonPath.read(flowObj.toString(), "$..[?(@.getSlots)].flowName");
					ArrayList resParameterName = JsonPath.read(flowObj.toString(), "$..[?(@.getSlots)]..parameterName");
					if (!resParameterName.isEmpty()) {
						for (int k = 0; k < resParameterName.size(); k++) {
							String par = (String) resParameterName.get(k);
							if (!checkParameterName(path, par)) {
								errorMessages.add(
										"#/paths/" + path + "/x-chatbotFlow/getSlots/parameterName: The parameter of ["
												+ par + "] not found in Swagger");
							}
							if (!checkFlowParameterName((String) flowName.get(0), par)) {
								errorMessages.add(
										"#/paths/" + path + "/x-chatbotFlow/getSlots/parameterName: The parameter of ["
												+ par + "] not found in responseToSlots");
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
	}

	public void checkUtterAndEntity(String path, JSONObject getObj, String xNmae) {
		// 檢查x-bot-utter和x-user-entity 參數
		try {
			org.json.JSONArray xArr = getObj.getJSONArray(xNmae);
			ArrayList parArr = JsonPath.read(xArr.toString(), "$..parameterName");
			ArrayList allRequireParameter = getRequireParameter(path);
			// 檢查RequireParameter
			for (int j = 0; j < allRequireParameter.size(); j++) {
				String requireParameter = (String) allRequireParameter.get(j);
				if (!parArr.contains(requireParameter)) {
					errorMessages.add("#/paths/" + path + "/" + xNmae + "/parameterName: The required parameter ["
							+ requireParameter + "] is not filled");
				}
			}
			// 檢查Swagger內是否有這個參數
			for (int j = 0; j < parArr.size(); j++) {
				String parameter = (String) parArr.get(j);
				if (!checkParameterName(path, parameter)) {
					errorMessages.add("#/paths/" + path + "/" + xNmae + "/parameterName: The parameter of [" + parameter
							+ "] not found in Swagger");
				}
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
	}

	public void checkUtterAndEntityCorrespond(String path, JSONObject getObj) {
		// 檢查x-bot-utter和x-user-entity 對應
		try {
			org.json.JSONArray botUtter = getObj.getJSONArray("x-bot-utter");
			org.json.JSONArray userEntity = getObj.getJSONArray("x-user-entity");
			ArrayList botParArr = JsonPath.read(botUtter.toString(), "$..parameterName");
			ArrayList userParArr = JsonPath.read(userEntity.toString(), "$..parameterName");
			// 檢查x-bot-utter填寫的參數x-user-entity是否有填
			for (int j = 0; j < botParArr.size(); j++) {
				String botParameter = (String) botParArr.get(j);
				if (!userParArr.contains(botParameter)) {
					errorMessages
							.add("#/paths/" + path + "/x-user-entity/parameterName: Unfilled [" + botParameter + "]");
				}
			}
			// 檢查x-user-entity填寫的參數x-bot-utter是否有填
			for (int j = 0; j < userParArr.size(); j++) {
				String userParameter = (String) userParArr.get(j);
				if (!botParArr.contains(userParameter)) {
					errorMessages
							.add("#/paths/" + path + "/x-bot-utter/parameterName: Unfilled [" + userParameter + "]");
				}
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
	}

	public void checkResult(String path, JSONObject getObj) {
		// 檢查x-bot-jsonpPath-result
		try {
			JSONObject result = getObj.getJSONObject("x-bot-jsonpPath-result");
			// 檢查jsonPath
			try {
				ArrayList JsonPathArr = JsonPath.read(result.toString(), "$..jsonPath");
				if (!JsonPathArr.isEmpty()) {
					for (int k = 0; k < JsonPathArr.size(); k++) {
						String jsonPath = (String) JsonPathArr.get(k);
						if (!checkJsonPath(jsonPath)) {
							errorMessages.add("#/paths/" + path + "/x-bot-jsonpPath-result/result/jsonPath: ["
									+ jsonPath + "] is not jsonPath");
						}
					}
				}
			} catch (ClassCastException e) {
				System.out.println(e.getMessage());
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
	}

	public Boolean checkJsonPath(String jsonPath) {
		// 驗證jsonPath是否輸入正確
		// 無法驗證，只能看有沒有那個值
		try {
			String json = "{\"response\" : \"This is the test message\"}";
			JSONArray authors = JsonPath.read(json, jsonPath);
			return true;
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
			return true;
		} catch (PathNotFoundException e) {
			System.out.println(e.getMessage());
			return true;
		}

	}

	public Boolean checkParameterName(String path, String params) {
		Boolean checkPar = false;
		try {
			ArrayList parArr = JsonPath.read(swagger.toString(), "$.paths." + path + "..parameters..name");
			for (int i = 0; i < parArr.size(); i++) {
				if (parArr.get(i).equals(params)) {
					return true;
				}
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public Boolean checkFlowParameterName(String flowName, String params) {
		Boolean checkPar = false;
		try {
			ArrayList parArr = JsonPath.read(swagger.toString(), "$.paths..x-chatbotFlow[?(@.flowName==\"" + flowName
					+ "\")].responseToSlots[?(@.parameterName==\"" + params + "\")]");
			if (!parArr.isEmpty()) {
				return true;
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public ArrayList getRequireParameter(String path) {
		try {
			ArrayList parArr = JsonPath.read(swagger.toString(), "$.paths." + path + "..[?(@.required==true)].name");
			return parArr;
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
