package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

public interface ParameterNameRule extends BotenRule{
	public default boolean checkParameterNames(JSONObject swagger, String path, String params) {
		try {
			ArrayList<String> parArr = JsonPath.read(swagger.toString(), "$.paths.['" + path + "']..parameters..name");
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
}
