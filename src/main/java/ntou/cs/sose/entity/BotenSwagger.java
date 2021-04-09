package ntou.cs.sose.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class BotenSwagger {
	private String url;
	private JSONObject swagger;
	private JSONObject chatbotEnabledSwaggerErrors;
	private JSONObject inputOutputConfig;
	private JSONObject botenConfig;
	private String nlu;
	private String domain;
	private String stories;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject getSwagger() {
		return swagger;
	}

	public void setSwagger(JSONObject swagger) {
		this.swagger = swagger;
	}

	public JSONObject getChatbotEnabledSwaggerErrors() {
		return chatbotEnabledSwaggerErrors;
	}

	public void setChatbotEnabledSwaggerErrors(JSONObject chatbotEnabledSwaggerErrors) {
		this.chatbotEnabledSwaggerErrors = chatbotEnabledSwaggerErrors;
	}

	public JSONObject getInputOutputConfig() {
		return inputOutputConfig;
	}

	public void setInputOutputConfig(JSONObject inputOutputConfig) {
		this.inputOutputConfig = inputOutputConfig;
	}

	public JSONObject getBotenConfig() {
		return botenConfig;
	}

	public void setBotenConfig(JSONObject botenConfig) {
		this.botenConfig = botenConfig;
	}

	public String getNlu() {
		return nlu;
	}

	public void setNlu(String nlu) {
		this.nlu = nlu;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getStories() {
		return stories;
	}

	public void setStories(String stories) {
		this.stories = stories;
	}

	public ArrayList allPath() {
		org.json.JSONObject paths = swagger.getJSONObject("paths");
		Iterator<String> keys = paths.keys();
		ArrayList pathsArr = new ArrayList();
		while (keys.hasNext()) {
			String key = keys.next();
			pathsArr.add(key);
		}
		return pathsArr;
	}

	public ArrayList allFlow() {
		ArrayList flowArr = new ArrayList();
		try {
			flowArr = JsonPath.read(swagger.toString(), "$.info..x-chatbotFlow..flowName");
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return flowArr;
	}

	public HashMap<String, Object> chatbotFlow() {

		JSONObject info = swagger.getJSONObject("info");
		HashMap<String, Object> allFlow = new HashMap<String, Object>();
		try {
			org.json.JSONArray chatbotFlow = info.getJSONArray("x-chatbotFlow");
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
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return allFlow;
	}

	public static String changeSign(String pathName) {
		// change / to _
		ArrayList pathArr = new ArrayList();
		String changedPath = "";
		for (String retval : pathName.split("/")) {
			if (!retval.equals("")) {
				changedPath = changedPath + retval + "_";
			}
		}
		changedPath = changedPath.substring(0, changedPath.length() - 1);
		return changedPath;
	}
}
