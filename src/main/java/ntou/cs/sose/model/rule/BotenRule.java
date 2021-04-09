package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public interface BotenRule {
	public ArrayList checkRule(BotenSwagger botenSwagger);

	public static Boolean checkParameterName(JSONObject swagger, String path, String params) {
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

	public static Boolean checkFlowParameterName(JSONObject swagger, String flowName, String params) {
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

	public static ArrayList getRequireParameter(JSONObject swagger, String path) {
		try {
			ArrayList parArr = JsonPath.read(swagger.toString(), "$.paths." + path + "..[?(@.required==true)].name");
			return parArr;
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
