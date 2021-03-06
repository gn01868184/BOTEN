package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class GetInformation {
	public static ArrayList getAllPath(JSONObject swagger) {
		org.json.JSONObject paths = swagger.getJSONObject("paths");
		Iterator<String> keys = paths.keys();
		ArrayList pathsArr = new ArrayList();
		while (keys.hasNext()) {
			String key = keys.next();
			pathsArr.add(key);
		}
		return pathsArr;
	}

	public static ArrayList getAllFlow(JSONObject swagger) {
		ArrayList flowArr = new ArrayList();
		try {
			flowArr = JsonPath.read(swagger.toString(), "$.info..x-chatbotFlow..flowName");
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return flowArr;
	}

	public static HashMap<String, Object> getChatbotFlow(JSONObject swagger) {
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
