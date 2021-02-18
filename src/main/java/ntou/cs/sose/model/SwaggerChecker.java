package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;

import net.minidev.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class SwaggerChecker {
	JSONObject jsonSchema = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/schema.json")));
	JSONObject swagger = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/foursquareSwagger.json")));
	Schema schema = SchemaLoader.load(jsonSchema);
	HashMap<String, Object> errorJson = new HashMap<String, Object>();
	ArrayList<String> errorMessages = new ArrayList<String>();

	public String sc() {
		if (jsonSchema()) {

		}
		checkInfo();
		checkPaths();
		System.out.println(errorMessages);
		return null;
	}

	public boolean jsonSchema() {
		try {
			schema.validate(swagger);
			return true;
		} catch (ValidationException e) {
			System.out.println("jsonSchema:");
			// prints all ValidationException
			e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(x -> {
				System.out.println(x);
				errorMessages.add(x);
			});
			return false;
		}
	}

	public void checkInfo() {
		// 檢查info-x-chatbotFlow與Swagger內的path是否對應
		JSONObject info = swagger.getJSONObject("info");
		if (info.has("x-chatbotFlow")) {
			HashMap<String, Object> allFlow = getChatbotFlow();
			for (Object flowName : allFlow.keySet()) {
				ArrayList pathsArr = (ArrayList) allFlow.get(flowName);
				for (int i = 0; i < pathsArr.size(); i++) {
					try {
						JsonPath.read(swagger.toString(), "$.paths." + pathsArr.get(i));
					} catch (PathNotFoundException e) {
						System.out.println(e.getMessage());
						errorMessages.add("#/info/x-chatbotFlow/" + i + "/flow: [" + pathsArr.get(i)
								+ "] path not found in Swagger");
					}
				}
			}
		}
	}

	public void checkPaths() {
		JSONObject paths = swagger.getJSONObject("paths");
		ArrayList pathsArr = getAllPath();
		System.out.println(pathsArr);
		for (int i = 0; i < pathsArr.size(); i++) {
			String path = (String) pathsArr.get(i);
			JSONObject pathObj = paths.getJSONObject(path);
			try {
				JSONObject getObj = pathObj.getJSONObject("get");
				// 檢查x-chatbotFlow
				try {
					org.json.JSONArray chatbotFlow = getObj.getJSONArray("x-chatbotFlow");
					HashMap<String, Object> allFlow = getChatbotFlow();
					for (int j = 0; j < chatbotFlow.length(); j++) {
						boolean checkFlowName = false;
						boolean checkPath = false;
						JSONObject flowObj = (JSONObject) chatbotFlow.get(0);
						// 檢查與info對應
						for (Object flowName : allFlow.keySet()) {
							if (flowObj.getString("flowName").equals(flowName)) {
								ArrayList flowPathsArr = (ArrayList) allFlow.get(flowName);
								for (int z = 0; z < flowPathsArr.size(); z++) {
									if (flowPathsArr.get(z).equals(path)) {
										checkPath = true;
										break;
									}
								}

								System.out.println(checkPath);
								checkFlowName = true;
								break;
							}
						}
						if (!checkFlowName) {
							errorMessages.add("#/paths/" + path + "/x-chatbotFlow/" + j + "/flowName: Unfilled ["
									+ flowObj.getString("flowName") + "] in info/x-chatbotFlow/{int}/flowName");
						}
						if (!checkPath) {
							errorMessages.add("#/paths/" + path + "/x-chatbotFlow/" + j + ": Unfilled [" + path
									+ "] not found in " + flowObj.getString("flowName"));
						}
						// 檢查jsonPath
						try {
							ArrayList JsonPathArr = JsonPath.read(flowObj.toString(), "$..jsonPath");
							if (!JsonPathArr.isEmpty()) {
								for (int k = 0; k < JsonPathArr.size(); k++) {
									String jsonPath = (String) JsonPathArr.get(k);
									if (!checkJsonPath(jsonPath)) {
										errorMessages.add("#/paths/" + path + "/x-chatbotFlow/" + j
												+ "/responseToSlots/" + k + "/jsonPath: Not jsonPath");
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
	}

	public HashMap<String, Object> getChatbotFlow() {
		JSONObject info = swagger.getJSONObject("info");
		org.json.JSONArray chatbotFlow = info.getJSONArray("x-chatbotFlow");
		HashMap<String, Object> allFlow = new HashMap<String, Object>();
		for (int i = 0; i < chatbotFlow.length(); i++) {
			ArrayList pathsArr = new ArrayList();
			org.json.JSONArray flowArray = chatbotFlow.getJSONObject(i).getJSONArray("flow");
			String flowName = chatbotFlow.getJSONObject(i).getString("flowName");
			for (int j = 0; j < flowArray.length(); j++) {
				String flow = flowArray.getString(j);
				pathsArr.add(flow);
			}
			allFlow.put(flowName, pathsArr);
		}
		return allFlow;
	}

	public ArrayList getAllPath() {
		org.json.JSONObject paths = swagger.getJSONObject("paths");
		Iterator<String> keys = paths.keys();
		ArrayList pathsArr = new ArrayList();
		while (keys.hasNext()) {
			String key = keys.next();
			pathsArr.add(key);
		}
		return pathsArr;
	}

	public String jsonPath() {
		try {
			LinkedHashMap<Object, Object> author0 = JsonPath.read(swagger.toString(), "$.paths");
			System.out.println(author0);
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public Boolean checkJsonPath(String jsonPath) {
		// 驗證jsonPath是否輸入正確
		try {
			String json = "{\"test_data\" : \"This is the test message\"}";
			JSONArray authors = JsonPath.read(json, jsonPath);
			return true;
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
			return true;
		} catch (PathNotFoundException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public Boolean checkParameterName(String path, String param) {
		// 驗證ParameterName是否在Swagger內
		// $.paths.{path}..parameters..name
		return false;
	}
	// 拿到所有的requare參數
	// $.paths./v2/venues/suggestcompletion.[?(@.required==true)].name
}
