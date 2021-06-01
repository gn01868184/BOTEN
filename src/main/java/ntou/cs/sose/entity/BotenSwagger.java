package ntou.cs.sose.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.model.MyHttpURLConnection;

public class BotenSwagger {
	private String url;
	private JSONObject swagger;
	private JSONObject chatbotEnabledSwaggerErrors;
	private String inputOutputConfig;
	private JSONObject botenConfig;
	private String nlu;
	private String domain;
	private String stories;
	private String rules;

	public BotenSwagger(String url) {
		this.url = url;
		MyHttpURLConnection httpConnection = new MyHttpURLConnection();
		swagger = new JSONObject(httpConnection.connection(url));
	}

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

	public String getInputOutputConfig() {
		return inputOutputConfig;
	}

	public void setInputOutputConfig(String inputOutputConfig) {
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

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public ArrayList<String> allPath() {
		org.json.JSONObject paths = swagger.getJSONObject("paths");
		Iterator<String> keys = paths.keys();
		ArrayList<String> pathsArr = new ArrayList<String>();
		while (keys.hasNext()) {
			String key = keys.next();
			pathsArr.add(key);
		}
		return pathsArr;
	}

	public ArrayList<String> allFlow() {
		ArrayList<String> flowArr = new ArrayList<String>();
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
				ArrayList<String> pathsArr = new ArrayList<String>();
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
		String changedPath = "";
		for (String retval : pathName.split("/")) {
			if (!retval.equals("")) {
				changedPath = changedPath + retval + "_";
			}
		}
		changedPath = changedPath.substring(0, changedPath.length() - 1);
		return changedPath;
	}

	public ArrayList<Object> getRequireParameters(String path) {
		try {
			ArrayList<Object> parArr = JsonPath.read(swagger.toString(),
					"$.paths.['" + path + "']..[?(@.required==true)].name");
			return parArr;
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
