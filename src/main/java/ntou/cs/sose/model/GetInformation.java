package ntou.cs.sose.model;

import java.util.ArrayList;
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
