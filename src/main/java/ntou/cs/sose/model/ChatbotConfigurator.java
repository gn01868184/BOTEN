package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import ntou.cs.sose.entity.BotenSwagger;

public class ChatbotConfigurator {
	private JSONObject swagger;
	private HashMap<String, Object> config;
	private ArrayList<String> allPath;
	private ArrayList<String> allFlow;
	private HashMap<String, Object> chatbotFlow;

	public ChatbotConfigurator(BotenSwagger req) {
		swagger = req.getSwagger();
		allPath = req.allPath();
		allFlow = req.allFlow();
		chatbotFlow = req.chatbotFlow();
		config = botenConfig();
	}

	public HashMap<String, Object> getConfig() {
		return config;
	}

	public HashMap<String, Object> botenConfig() {
		HashMap<String, Object> config = new HashMap<String, Object>();
		try {
			JSONArray servers = (JSONArray) swagger.get("servers");
			String url = (String) ((JSONObject) servers.get(0)).get("url");
			config.put("url", url);
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		config.put("auto", setAutoLocation());
		try {
			for (int i = 0; i < allPath.size(); i++) {
				config.put(BotenSwagger.changeSign((String) allPath.get(i)), setPath((String) allPath.get(i)));
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		try {
			for (int i = 0; i < allFlow.size(); i++) {
				config.put((String) allFlow.get(i), setFlowParameters(chatbotFlow, (String) allFlow.get(i)));
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return config;
	}

	public HashMap<String, Object> setPath(String pathName) {
		Gson gson = new Gson();
		HashMap<String, Object> pathObj = new HashMap<String, Object>();
		String result = ((JSONObject) ((JSONObject) ((JSONObject) swagger.get("paths")).get(pathName)).get("get"))
				.get("x-bot-jsonpPath-result").toString();
		HashMap<String, Object> resultObj = gson.fromJson(result, new TypeToken<HashMap<String, Object>>() {
		}.getType());
		pathObj.put("pathName", pathName);
		pathObj.put("parameters", getParameters(pathName));
		pathObj.put("x-bot-jsonpPath-result", resultObj);
		return pathObj;
	}

	public ArrayList<HashMap<String, Object>> getParameters(String pathName) {
		ArrayList<HashMap<String, Object>> pathObj = new ArrayList<HashMap<String, Object>>();
		ArrayList<String> utterParameterName = new ArrayList<String>();
		JSONArray parameters = (JSONArray) ((JSONObject) ((JSONObject) ((JSONObject) swagger.get("paths"))
				.get(pathName)).get("get")).get("parameters");
		try {
			utterParameterName = JsonPath.read(swagger.toString(),
					"$.paths.['" + pathName + "'].get.x-bot-utter[*].parameterName");
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		} catch (PathNotFoundException e) {
			System.out.println(e.getMessage());
		}
		for (int i = 0; i < parameters.length(); i++) {
			if (utterParameterName.contains(((JSONObject) parameters.get(i)).get("name"))) {
				HashMap<String, Object> parametersObj = new HashMap<String, Object>();
				parametersObj.put("in", ((JSONObject) parameters.get(i)).get("in"));
				parametersObj.put("name", ((JSONObject) parameters.get(i)).get("name"));
				pathObj.add(parametersObj);
			}

		}
		return pathObj;
	}

	public HashMap<String, Object> setFlowParameters(HashMap<String, Object> flowObj, String flowName) {
		HashMap<String, Object> flowParameters = new HashMap<String, Object>();
		flowParameters.put("parameters", removeRepeatParameters(flowObj, flowName));
		flowParameters.put("flow", getResponseToSlotsGetSlots(flowObj, flowName));
		return flowParameters;
	}

	public ArrayList<HashMap<String, Object>> getResponseToSlotsGetSlots(HashMap<String, Object> flowObj,
			String flowName) {
		ArrayList<HashMap<String, Object>> allFlow = new ArrayList<HashMap<String, Object>>();
		ArrayList<String> path = (ArrayList<String>) flowObj.get(flowName);
		for (int i = 0; i < path.size(); i++) {
			HashMap<String, Object> flow = new HashMap<String, Object>();
			flow.put("intent", BotenSwagger.changeSign((String) path.get(i)));
			try {
				ArrayList<Collection> responseToSlots = JsonPath.read(swagger.toString(), "$.paths.['" + path.get(i)
						+ "'].get.x-chatbotFlow.[?(@.flowName==\"" + flowName + "\")].responseToSlots");
				ArrayList<Collection> getSlots = JsonPath.read(swagger.toString(), "$.paths.['" + path.get(i)
						+ "'].get.x-chatbotFlow.[?(@.flowName==\"" + flowName + "\")].getSlots");
				if (!responseToSlots.equals(new ArrayList<Collection>())) {
					ArrayList<String> responseToSlotsArr = new ArrayList();
					for (int j = 0; j < responseToSlots.size(); j++) {
						responseToSlotsArr.addAll((Collection) responseToSlots.get(j));
					}
					flow.put("responseToSlots", responseToSlotsArr);
				}
				if (!getSlots.equals(new ArrayList<String>())) {
					ArrayList<Collection> getSlotsArr = new ArrayList<Collection>();
					for (int j = 0; j < getSlots.size(); j++) {
						getSlotsArr.addAll((Collection) getSlots.get(j));
					}
					flow.put("getSlots", getSlotsArr);
				}
			} catch (PathNotFoundException e) {
				System.out.println(e.getMessage());
			}
			allFlow.add(flow);
		}
		return allFlow;
	}

	public HashMap<String, String> setAutoLocation() {
		HashMap<String, String> autoLocation = new HashMap<String, String>();
		autoLocation.put("latitude,longitude", "");
		autoLocation.put("latitude", "");
		autoLocation.put("longitude", "");
		try {
			ArrayList<HashMap<String, String>> readValue = JsonPath.read(swagger.toString(),
					"$..x-bot-utter[?(@.auto)]");
			for (HashMap<String, String> obj : readValue) {
				autoLocation.put(obj.get("auto"), obj.get("parameterName"));
				System.out.println(obj);
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return autoLocation;
	}

	public ArrayList<HashMap<String, Object>> removeRepeatParameters(HashMap<String, Object> flowObj, String flowName) {
		ArrayList<String> path = (ArrayList<String>) flowObj.get(flowName);
		ArrayList<HashMap<String, Object>> allParameters = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < path.size(); i++) {
			ArrayList<HashMap<String, Object>> Parameters = getParameters((String) path.get(i));
			for (int j = 0; j < Parameters.size(); j++) {
				if (!allParameters.contains(Parameters.get(j))) {
					allParameters.add(Parameters.get(j));
				}
			}
		}
		return allParameters;
	}
}
