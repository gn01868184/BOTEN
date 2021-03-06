package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class ChatbotConfigurator {
	JSONObject swagger;

	public HashMap<String, Object> chatbotConfigurator(JSONObject req) {
		swagger = req;
		return botenConfig();
	}

	public HashMap<String, Object> botenConfig() {
		HashMap<String, Object> config = new HashMap<String, Object>();
		ArrayList allPath = GetInformation.getAllPath(swagger);
		ArrayList allFlow = GetInformation.getAllFlow(swagger);
		try {
			JSONArray servers = (JSONArray) swagger.get("servers");
			String url = (String) ((JSONObject) servers.get(0)).get("url");
			config.put("url", url);
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		try {
			for (int i = 0; i < allPath.size(); i++) {
				config.put(GetInformation.changeSign((String) allPath.get(i)), setPath((String) allPath.get(i)));
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		try {
			HashMap<String, Object> flowObj = GetInformation.getChatbotFlow(swagger);
			for (int i = 0; i < allFlow.size(); i++) {
				config.put((String) allFlow.get(i), setFlowParameters(flowObj, (String) allFlow.get(i)));
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

	public HashMap<String, Object> getResponseToSlotsGetSlots(HashMap<String, Object> flowObj, String flowName) {
		HashMap<String, Object> flow = new HashMap<String, Object>();
		ArrayList path = (ArrayList) flowObj.get(flowName);
		for (int i = 0; i < path.size(); i++) {
			flow.put("pathName", path.get(i));
			try {
				ArrayList responseToSlots = JsonPath.read(swagger.toString(), "$.paths." + path.get(i)
						+ ".get.x-chatbotFlow..[?(@.flowName==\"" + flowName + "\")]..responseToSlots");
				ArrayList getSlots = JsonPath.read(swagger.toString(), "$.paths." + path.get(i)
						+ ".get.x-chatbotFlow..[?(@.flowName==\"" + flowName + "\")]..getSlots");
				if (responseToSlots.equals(null)) {
					flow.put("responseToSlots", responseToSlots.get(0));
				}
				if (getSlots.equals(null)) {
					flow.put("getSlots", getSlots.get(0));
				}
			} catch (PathNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
		return flow;
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
