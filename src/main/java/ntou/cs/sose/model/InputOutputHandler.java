package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public class InputOutputHandler {
	private JSONObject swagger;
	private ArrayList<String> allPath;
	private ArrayList<String> allFlow;
	private HashMap<String, Object> res = new HashMap<String, Object>();

	public InputOutputHandler(BotenSwagger req) {
		swagger = req.getSwagger();
		allPath = req.allPath();
		allFlow = req.allFlow();
		HashMap<String, Object> nlu = setNlu();
		HashMap<String, Object> domain = setDomain();
		HashMap<String, Object> stories = setStories();
		res.put("nlu", nlu);
		res.put("domain", domain);
		res.put("stories", stories);
	}

	public HashMap<String, Object> getRes() {
		return res;
	}

	public HashMap<String, Object> setNlu() {
		HashMap<String, Object> nlu = new HashMap<String, Object>();
		HashMap<String, Object> intent = setIntent();
		HashMap<String, Object> regex = setRegex();
		nlu.put("intent", intent);
		nlu.put("regex", regex);
		return nlu;
	}

	public HashMap<String, Object> setDomain() {
		HashMap<String, Object> domain = new HashMap<String, Object>();
		ArrayList<String> intents = setIntents();
		ArrayList<String> entities = setEntities();
		HashMap<String, Object> slots = setSlots();
		ArrayList<String> actions = setActions();
		ArrayList<String> forms = setForms();
		HashMap<String, String> utter = setUtter();
		domain.put("intents", intents);
		domain.put("entities", entities);
		domain.put("slots", slots);
		domain.put("actions", actions);
		domain.put("forms", forms);
		domain.put("utter", utter);
		return domain;
	}

	public HashMap<String, Object> setStories() {
		ArrayList<String> flow = setFlow();
		HashMap<String, Object> stories = new HashMap<String, Object>();
		for (int i = 0; i < flow.size(); i++) {
			stories.put("into " + flow.get(i) + "_form path", setIntoPath((String) flow.get(i)));
			stories.put(flow.get(i) + " parameters list", setParametersList((String) flow.get(i)));
			stories.put("use " + flow.get(i) + " path", setUsePath((String) flow.get(i)));
		}
		stories.put("auto location path", setAutoLocationPath());
		return stories;
	}

	public ArrayList<String> getAllParameter() {
		ArrayList<String> parArr = new ArrayList<String>();
		try {
			parArr = JsonPath.read(swagger.toString(), "$.paths..x-user-entity");
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return parArr;
	}

	public HashMap<String, Object> setIntent() {
		HashMap<String, Object> intent = new HashMap<String, Object>();
		ArrayList<String> flow = setFlow();
		ArrayList<String> inform = setInformIntent();
		ArrayList<String> autoGetLocation = setAutoGetLocation();
		for (int i = 0; i < flow.size(); i++) {
			intent.put("get_" + (String) flow.get(i), setGetIntent((String) flow.get(i)));
			intent.put("parameters_list_" + (String) flow.get(i), setParametersListIntent((String) flow.get(i)));
			intent.put("fill_parameters_" + (String) flow.get(i), setFillParameters((String) flow.get(i)));
		}

		intent.put("inform", inform);
		intent.put("auto_get_location", autoGetLocation);

		return intent;
	}

	public ArrayList<String> setGetIntent(String flow) {
		ArrayList<String> train = new ArrayList<String>();
		try {
			JSONArray template = (JSONArray) ((JSONObject) ((JSONObject) swagger.get("info")).get("x-input-template"))
					.get("useEndpoint");
			for (int i = 0; i < template.length(); i++) {
				train.add(captureTemplate(flow, template.getString(i)));
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train.add("I want to get " + flow);
			train.add("I want to use " + flow);
		}
		return train;
	}

	public ArrayList<String> setParametersListIntent(String flow) {
		ArrayList<String> train = new ArrayList<String>();
		try {
			JSONArray template = (JSONArray) ((JSONObject) ((JSONObject) swagger.get("info")).get("x-input-template"))
					.get("parameterList");
			for (int i = 0; i < template.length(); i++) {
				train.add(captureTemplate(flow, template.getString(i)));
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train.add("I want to see the " + flow + " parameters list");
			train.add("see the " + flow + " parameters list");
		}
		return train;
	}

	public ArrayList<String> setFillParameters(String flow) {
		ArrayList<String> train = new ArrayList<String>();
		try {
			JSONArray template = (JSONArray) ((JSONObject) ((JSONObject) swagger.get("info")).get("x-input-template"))
					.get("fillParameter");
			for (int i = 0; i < template.length(); i++) {
				train.add(captureTemplate(flow, template.getString(i)));
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			train.add("Fill in the " + flow + " parameters");
			train.add("Fill " + flow);
		}
		return train;
	}

	public String captureTemplate(String flow, String template) {
		String captureTemplate = "";
		try {
			String[] templateArr = template.split("\\$\\{");
			String[] templateBack = templateArr[1].split("\\}");
			captureTemplate = templateArr[0] + flow;
			try {
				captureTemplate += templateBack[1];
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("ArrayIndexOutOfBoundsException: " + e.getMessage());
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("ArrayIndexOutOfBoundsException: " + e.getMessage());
		}
		return captureTemplate;
	}

	public ArrayList<String> setInformIntent() {
		ArrayList<String> allParameter = getAllParameter();
		ArrayList<String> inform = new ArrayList<String>();
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
					inform.add("[" + entityValueArr.get(j) + "](" + notRepeatParma.get(i) + ")");
				}
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return inform;
	}

	public ArrayList<String> setAutoGetLocation() {
		ArrayList<String> train = new ArrayList<String>();
		train.add(
				"Latitude of my current location is [25.130486599999998](auto_latitude), longitude of my current location is [121.73195779999999](auto_longitude)");
		train.add("Longitude is [121.736299](auto_longitude), latitude is [25.129253](auto_latitude)");
		return train;
	}

	public HashMap<String, Object> setRegex() {
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
				ArrayList<Object> regexArr = new ArrayList<Object>();
				for (int j = 0; j < regexTwoDimensionalArray.size(); j++) {
					regexArr.addAll((Collection) regexTwoDimensionalArray.get(j));
				}
				regexObj.put((String) notRepeatParma.get(i), regexArr);
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return regexObj;
	}

	public ArrayList<String> setFlow() {
		ArrayList<String> flow = new ArrayList<String>();
		// change / to _
		for (int i = 0; i < allPath.size(); i++) {
			flow.add(BotenSwagger.changeSign((String) allPath.get(i)));
		}
		flow.addAll(allFlow);
		return flow;
	}

	public ArrayList<String> setIntents() {
		ArrayList<String> flow = setFlow();
		ArrayList<String> intents = new ArrayList<String>();
		for (int i = 0; i < flow.size(); i++) {
			intents.add("get_" + flow.get(i));
			intents.add("parameters_list_" + flow.get(i));
			intents.add("fill_parameters_" + flow.get(i));
		}
		intents.add("inform");
		intents.add("auto_get_location");
		return intents;
	}

	public ArrayList<String> setEntities() {
		ArrayList<String> allParameter = getAllParameter();
		ArrayList<String> parameterArr = new ArrayList<String>();
		try {
			parameterArr = JsonPath.read(allParameter.toString(), "$..parameterName");
			parameterArr = (ArrayList<String>) parameterArr.stream().distinct().collect(Collectors.toList());
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		parameterArr.add("auto_latitude");
		parameterArr.add("auto_longitude");
		return parameterArr;
	}

	public HashMap<String, Object> setSlots() {
		ArrayList<String> entities = setEntities();
		HashMap<String, Object> slots = new HashMap<String, Object>();
		HashMap<String, String> type = new HashMap<String, String>();
		type.put("type", "unfeaturized");
		for (int i = 0; i < entities.size(); i++) {
			slots.put((String) entities.get(i), type);
		}
		slots.put("auto_latitude", type);
		slots.put("auto_longitude", type);
		return slots;
	}

	public ArrayList<String> setActions() {
		ArrayList<String> entities = setEntities();
		ArrayList<String> actions = new ArrayList<String>();
		for (int i = 0; i < entities.size(); i++) {
			actions.add("utter_ask_" + entities.get(i));
		}
		actions.add("action_slots_values");
		actions.add("action_use_api");
		actions.add("action_auto_location");
		return actions;
	}

	public ArrayList<String> setForms() {
		ArrayList<String> forms = new ArrayList<String>();
		forms.add("parameters_form");
		return forms;
	}

	public HashMap<String, String> setUtter() {
		ArrayList<String> entities = setEntities();
		HashMap<String, String> utter = new HashMap<String, String>();
		for (int i = 0; i < entities.size(); i++) {
			try {
				ArrayList<String> JsonPathArr = JsonPath.read(swagger.toString(),
						"$..x-bot-utter[?(@.parameterName==\"" + entities.get(i) + "\")].utter");
				utter.put("utter_ask_" + entities.get(i), (String) JsonPathArr.get(0));
			} catch (ClassCastException e) {
				System.out.println(e.getMessage());
			} catch (IndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
			}
		}
		return utter;
	}

	public HashMap<String, Object> setIntoPath(String flow) {
		ArrayList<String> actions = new ArrayList<String>();
		HashMap<String, Object> intoPath = new HashMap<String, Object>();
		actions.add("parameters_form");
		actions.add("form{\"name\": \"parameters_form\"}");
		actions.add("form{\"name\": null}");
		intoPath.put("fill_parameters_" + flow, actions);
		return intoPath;
	}

	public HashMap<String, Object> setParametersList(String flow) {
		ArrayList<String> actions = new ArrayList<String>();
		HashMap<String, Object> parametersListPath = new HashMap<String, Object>();
		actions.add("action_slots_values");
		parametersListPath.put("parameters_list_" + flow, actions);
		return parametersListPath;
	}

	public HashMap<String, Object> setUsePath(String flow) {
		ArrayList<String> actions = new ArrayList<String>();
		HashMap<String, Object> usePath = new HashMap<String, Object>();
		actions.add("action_use_api");
		usePath.put("get_" + flow, actions);
		return usePath;
	}

	public HashMap<String, Object> setAutoLocationPath() {
		ArrayList<String> actions = new ArrayList<String>();
		HashMap<String, Object> usePath = new HashMap<String, Object>();
		actions.add("action_auto_location");
		usePath.put("auto_get_location", actions);
		return usePath;
	}
}
