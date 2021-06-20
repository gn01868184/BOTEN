package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public class NluHandler extends InputOutputHandler {

	private HashMap<String, Object> nlu = new HashMap<String, Object>();

	public NluHandler(BotenSwagger req) {
		super(req);
		nlu = post_nlu();
	}

	public HashMap<String, Object> getNlu() {
		return nlu;
	}

	public HashMap<String, Object> post_nlu() {
		HashMap<String, Object> nlu = new HashMap<String, Object>();
		ArrayList<Object> nluArrayList = post_nluArrayList();
		nlu.put("version", "2.0");
		nlu.put("nlu", nluArrayList);
		return nlu;
	}

	public ArrayList<Object> post_nluArrayList() {
		ArrayList<Object> nluArrayList = new ArrayList<Object>();
		HashMap<String, Object> regexObject = post_regex();

		for (String path : allPath) {
			LinkedHashMap<String, String> getApiPath = new LinkedHashMap<String, String>();
			getApiPath.put("intent", "get_api_" + BotenSwagger.changeSign(path));
			getApiPath.put("examples", post_getIntent(path));
			nluArrayList.add(getApiPath);

			LinkedHashMap<String, String> parametersListPath = new LinkedHashMap<String, String>();
			parametersListPath.put("intent", "parameters_list_" + BotenSwagger.changeSign(path));
			parametersListPath.put("examples", post_parametersListIntent(path));
			nluArrayList.add(parametersListPath);

			LinkedHashMap<String, String> fillParametersPath = new LinkedHashMap<String, String>();
			fillParametersPath.put("intent", "fill_parameters_" + BotenSwagger.changeSign(path));
			fillParametersPath.put("examples", post_fillParameters(path));
			nluArrayList.add(fillParametersPath);
		}

		for (String flow : allFlow) {
			LinkedHashMap<String, String> getApiPath = new LinkedHashMap<String, String>();
			getApiPath.put("intent", "get_api_" + flow);
			getApiPath.put("examples", post_flow_getIntent(flow));
			nluArrayList.add(getApiPath);

			LinkedHashMap<String, String> parametersListPath = new LinkedHashMap<String, String>();
			parametersListPath.put("intent", "parameters_list_" + flow);
			parametersListPath.put("examples", post_flow_parametersListIntent(flow));
			nluArrayList.add(parametersListPath);

			LinkedHashMap<String, String> fillParametersPath = new LinkedHashMap<String, String>();
			fillParametersPath.put("intent", "fill_parameters_" + flow);
			fillParametersPath.put("examples", post_flow_fillParameters(flow));
			nluArrayList.add(fillParametersPath);
		}

		LinkedHashMap<String, String> inform = new LinkedHashMap<String, String>();
		inform.put("intent", "inform");
		inform.put("examples", post_informIntent());
		nluArrayList.add(inform);

		LinkedHashMap<String, String> autoGetLocation = new LinkedHashMap<String, String>();
		autoGetLocation.put("intent", "auto_get_location");
		autoGetLocation.put("examples",
				"- Latitude of my current location is [25.130486599999998](auto_latitude), longitude of my current location is [121.73195779999999](auto_longitude)\n"
						+ "- Longitude is [121.736299](auto_longitude), latitude is [25.129253](auto_latitude)\n");
		nluArrayList.add(autoGetLocation);

		for (Entry<String, Object> regex : regexObject.entrySet()) {
			LinkedHashMap<String, String> regexInNlu = new LinkedHashMap<String, String>();
			regexInNlu.put("regex", regex.getKey());
			regexInNlu.put("examples", (String) regex.getValue());
			nluArrayList.add(regexInNlu);
		}

		return nluArrayList;
	}

	public HashMap<String, Object> post_regex() {
		ArrayList<String> allParameter = getAllParameter();
		ArrayList<Object> allRegexArr = new ArrayList<Object>();
		ArrayList<Object> parameterArr = new ArrayList<Object>();
		ArrayList<Object> notRepeatParma = new ArrayList<Object>();
		HashMap<String, Object> regexObj = new HashMap<String, Object>();
		try {
			allRegexArr = JsonPath.read(allParameter.toString(), "$..[?(@.regex)]");
			parameterArr = JsonPath.read(allRegexArr.toString(), "$..parameterName");
			notRepeatParma = (ArrayList<Object>) parameterArr.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i < notRepeatParma.size(); i++) {
				ArrayList<Object> regexTwoDimensionalArray = JsonPath.read(allRegexArr.toString(),
						"$.[?(@.parameterName==\"" + notRepeatParma.get(i) + "\")].regex");
				String regexArr = "";
				for (int j = 0; j < regexTwoDimensionalArray.size(); j++) {
					ArrayList<String> regex = (ArrayList<String>) regexTwoDimensionalArray.get(j);
					for (int z = 0; z < regex.size(); z++) {
						regexArr += "- " + regex.get(z) + "\n";
					}
				}
				regexObj.put((String) notRepeatParma.get(i), regexArr);
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return regexObj;
	}

	public String post_getIntent(String path) {
		String train = "";
		try {
			JSONArray templates = swagger.getJSONObject("paths").getJSONObject(path).getJSONObject("get")
					.getJSONObject("x-input-template").getJSONArray("useEndpoint");
			for (Object template : templates) {
				train += "- " + template + "\n";
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train += "- I want to get " + BotenSwagger.changeSign(path) + "\n";
			train += "- I want to use " + BotenSwagger.changeSign(path) + "\n";
		}
		return train;
	}

	public String post_parametersListIntent(String path) {
		String train = "";
		try {
			JSONArray templates = swagger.getJSONObject("paths").getJSONObject(path).getJSONObject("get")
					.getJSONObject("x-input-template").getJSONArray("parameterList");
			for (Object template : templates) {
				train += "- " + template + "\n";
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train += "- I want to see the " + BotenSwagger.changeSign(path) + " parameters list\n";
			train += "- see the " + BotenSwagger.changeSign(path) + " parameters list\n";
		}
		return train;
	}

	public String post_fillParameters(String path) {
		String train = "";
		try {
			JSONArray templates = swagger.getJSONObject("paths").getJSONObject(path).getJSONObject("get")
					.getJSONObject("x-input-template").getJSONArray("fillParameter");
			for (Object template : templates) {
				train += "- " + template + "\n";
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train += "- Fill in the " + BotenSwagger.changeSign(path) + " parameters\n";
			train += "- Fill " + BotenSwagger.changeSign(path) + "\n";
		}
		return train;
	}

	public String post_flow_getIntent(String flow) {
		String train = "";
		try {
			ArrayList<String> templates = JsonPath.read(swagger.toString(),
					"$.info.x-chatbotFlow[?(@.flowName==\"" + flow + "\")].x-input-template.useEndpoint[*]");
			for (String template : templates) {
				train += "- " + template + "\n";
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train += "- I want to get " + flow + "\n";
			train += "- I want to use " + flow + "\n";
		}
		return train;
	}

	public String post_flow_parametersListIntent(String flow) {
		String train = "";
		try {
			ArrayList<String> templates = JsonPath.read(swagger.toString(),
					"$.info.x-chatbotFlow[?(@.flowName==\"" + flow + "\")].x-input-template.parameterList[*]");
			for (String template : templates) {
				train += "- " + template + "\n";
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train += "- I want to see the " + flow + " parameters list\n";
			train += "- see the " + flow + " parameters list\n";
		}
		return train;
	}

	public String post_flow_fillParameters(String flow) {
		String train = "";
		try {
			ArrayList<String> templates = JsonPath.read(swagger.toString(),
					"$.info.x-chatbotFlow[?(@.flowName==\"" + flow + "\")].x-input-template.fillParameter[*]");
			for (String template : templates) {
				train += "- " + template + "\n";
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train += "- Fill in the " + flow + " parameters\n";
			train += "- Fill " + flow + "\n";
		}
		return train;
	}

	public String post_informIntent() {
		ArrayList<String> allParameter = getAllParameter();
		String inform = "";
		ArrayList<String> parameterArr = new ArrayList<String>();
		ArrayList<String> notRepeatParma = new ArrayList<String>();
		ArrayList<Object> entityValue = new ArrayList<Object>();
		ArrayList<Object> entityValueArr = new ArrayList<Object>();
		try {
			parameterArr = JsonPath.read(allParameter.toString(), "$..parameterName");
			notRepeatParma = (ArrayList<String>) parameterArr.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i < notRepeatParma.size(); i++) {
				entityValue = JsonPath.read(allParameter.toString(),
						"$..[?(@.parameterName==\"" + notRepeatParma.get(i) + "\")].entityValue");
				entityValueArr = (ArrayList<Object>) entityValue.get(0);
				for (int j = 0; j < entityValueArr.size(); j++) {
					inform += "- [" + entityValueArr.get(j) + "](" + notRepeatParma.get(i) + ")\n";
				}
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return inform;
	}
}
