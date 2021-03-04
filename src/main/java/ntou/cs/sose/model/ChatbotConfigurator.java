package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class ChatbotConfigurator {
	JSONObject swagger;

	public HashMap<String, Object> chatbotConfigurator(JSONObject req) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		swagger = req;

		return res;
	}

	public HashMap<String, Object> botenConfig() {
		HashMap<String, Object> config = new HashMap<String, Object>();
		ArrayList allPath = GetInformation.getAllPath(swagger);
		ArrayList allFlow = GetInformation.getAllFlow(swagger);
		JSONArray servers = (JSONArray) swagger.get("servers");
		String url = (String) ((JSONObject) servers.get(0)).get("url");
		config.put("url", url);
		for (int i = 0; i < allPath.size(); i++) {
			config.put(GetInformation.changeSign((String) allPath.get(i)), setPath((String) allPath.get(i)));
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
}
