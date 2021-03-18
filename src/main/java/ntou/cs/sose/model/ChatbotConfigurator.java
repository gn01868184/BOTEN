package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import ntou.cs.sose.entity.BotenSwagger;

public class ChatbotConfigurator {
	private JSONObject swagger;
	private HashMap<String, Object> config;
	private ArrayList allPath;
	private ArrayList allFlow;
	private HashMap<String, Object> chatbotFlow;

	public HashMap<String, Object> chatbotConfigurator(BotenSwagger req) {
		swagger = req.getSwagger();
		allPath = req.allPath();
		allFlow = req.allFlow();
		chatbotFlow = req.chatbotFlow();
		config = botenConfig();
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
		HashMap<String, Object> pathObj = new HashMap<String, Object>();
		pathObj.put("pathName", pathName);
		pathObj.put("parameters", getParameters(pathName));
		return pathObj;
	}

	public ArrayList getParameters(String pathName) {
		ArrayList pathObj = new ArrayList();
		JSONArray parameters = (JSONArray) ((JSONObject) ((JSONObject) ((JSONObject) swagger.get("paths"))
				.get(pathName)).get("get")).get("parameters");
		for (int i = 0; i < parameters.length(); i++) {
			HashMap<String, Object> parametersObj = new HashMap<String, Object>();
			parametersObj.put("in", ((JSONObject) parameters.get(i)).get("in"));
			parametersObj.put("name", ((JSONObject) parameters.get(i)).get("name"));
			pathObj.add(parametersObj);
		}
		return pathObj;
	}

	public HashMap<String, Object> setFlowParameters(HashMap<String, Object> flowObj, String flowName) {
		HashMap<String, Object> flowParameters = new HashMap<String, Object>();
		flowParameters.put("parameters", removeRepeatParameters(flowObj, flowName));
		flowParameters.put("flow", getResponseToSlotsGetSlots(flowObj, flowName));
		return flowParameters;
	}

	public ArrayList getResponseToSlotsGetSlots(HashMap<String, Object> flowObj, String flowName) {
		ArrayList allFlow = new ArrayList();
		ArrayList path = (ArrayList) flowObj.get(flowName);
		for (int i = 0; i < path.size(); i++) {
			HashMap<String, Object> flow = new HashMap<String, Object>();
			flow.put("pathName", path.get(i));
			try {
				ArrayList responseToSlots = JsonPath.read(swagger.toString(), "$.paths." + path.get(i)
						+ ".get.x-chatbotFlow.[?(@.flowName==\"" + flowName + "\")].responseToSlots");
				ArrayList getSlots = JsonPath.read(swagger.toString(), "$.paths." + path.get(i)
						+ ".get.x-chatbotFlow.[?(@.flowName==\"" + flowName + "\")].getSlots");
				if (!responseToSlots.equals(new ArrayList())) {
					ArrayList responseToSlotsArr = new ArrayList();
					for (int j = 0; j < responseToSlots.size(); j++) {
						responseToSlotsArr.addAll((Collection) responseToSlots.get(j));
					}
					flow.put("responseToSlots", responseToSlotsArr);
				}
				if (!getSlots.equals(new ArrayList())) {
					ArrayList getSlotsArr = new ArrayList();
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

	public ArrayList removeRepeatParameters(HashMap<String, Object> flowObj, String flowName) {
		ArrayList path = (ArrayList) flowObj.get(flowName);
		ArrayList allParameters = new ArrayList();
		for (int i = 0; i < path.size(); i++) {
			ArrayList Parameters = getParameters((String) path.get(i));
			for (int j = 0; j < Parameters.size(); j++) {
				if (!allParameters.contains(Parameters.get(j))) {
					allParameters.add(Parameters.get(j));
				}
			}
		}
		return allParameters;
	}
}
