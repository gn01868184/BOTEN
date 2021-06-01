package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import ntou.cs.sose.entity.BotenSwagger;

public class DomainHandler extends InputOutputHandler {

	private HashMap<String, Object> domain = new HashMap<String, Object>();

	public DomainHandler(BotenSwagger req) {
		super(req);
		domain = post_domain();
	}

	public HashMap<String, Object> getDomain() {
		return domain;
	}

	public HashMap<String, Object> post_domain() {
		HashMap<String, Object> domain = new HashMap<String, Object>();
		ArrayList<String> intents = post_intents();
		ArrayList<String> entities = post_entities();
		HashMap<String, Object> slots = post_slots();
		HashMap<String, Object> utter = post_utter();
		ArrayList<String> actions = post_actions();
		HashMap<String, Object> forms = post_forms();
		HashMap<String, Object> sessionConfig = post_sessionConfig();
		domain.put("version", "2.0");
		domain.put("intents", intents);
		domain.put("entities", entities);
		domain.put("slots", slots);
		domain.put("actions", actions);
		domain.put("forms", forms);
		domain.put("responses", utter);
		domain.put("session_config", sessionConfig);

		return domain;
	}

	public ArrayList<String> post_intents() {
		ArrayList<String> flow = setFlow();
		ArrayList<String> intents = new ArrayList<String>();
		for (int i = 0; i < flow.size(); i++) {
			intents.add("get_api_" + flow.get(i));
			intents.add("parameters_list_" + flow.get(i));
			intents.add("fill_parameters_" + flow.get(i));
		}
		intents.add("inform");
		intents.add("auto_get_location");
		return intents;
	}

	public ArrayList<String> post_entities() {
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

	public HashMap<String, Object> post_slots() {
		ArrayList<String> entities = post_entities();
		HashMap<String, Object> slots = new HashMap<String, Object>();
		HashMap<String, String> type = new HashMap<String, String>();
		type.put("type", "text");
		for (int i = 0; i < entities.size(); i++) {
			slots.put((String) entities.get(i), type);
		}
		slots.put("auto_latitude", type);
		slots.put("auto_longitude", type);
		return slots;
	}

	public ArrayList<String> post_actions() {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add("action_slots_values");
		actions.add("action_use_api");
		actions.add("action_auto_location");
		actions.add("action_set_default");
		actions.add("action_set_intent");
		return actions;
	}

	public HashMap<String, Object> post_forms() {
		HashMap<String, Object> form = new HashMap<String, Object>();
		for (String path : allPath) {
			HashMap<String, Object> formObject = new HashMap<String, Object>();
			HashMap<String, Object> required_slots = new HashMap<String, Object>();
			for (String par : getParameters(path)) {
				ArrayList<Object> parametersArray = new ArrayList<Object>();
				HashMap<String, String> parametersObject = new HashMap<String, String>();
				parametersObject.put("type", "from_entity");
				parametersObject.put("entity", par);
				parametersArray.add(parametersObject);
				required_slots.put(par, parametersArray);
			}
			formObject.put("required_slots", required_slots);
			form.put(path + "_form", formObject);
		}

		for (String flow : allFlow) {
			HashMap<String, Object> formObject = new HashMap<String, Object>();
			HashMap<String, Object> required_slots = new HashMap<String, Object>();
			for (String par : removeRepeatParameters(chatbotFlow, flow)) {
				ArrayList<Object> parametersArray = new ArrayList<Object>();
				HashMap<String, String> parametersObject = new HashMap<String, String>();
				parametersObject.put("type", "from_entity");
				parametersObject.put("entity", par);
				parametersArray.add(parametersObject);
				required_slots.put(par, parametersArray);
			}
			formObject.put("required_slots", required_slots);
			form.put(flow + "_form", formObject);
		}
		return form;
	}

	public HashMap<String, Object> post_utter() {
		ArrayList<String> entities = post_entities();
		HashMap<String, Object> utter = new HashMap<String, Object>();
		for (int i = 0; i < entities.size(); i++) {
			try {
				ArrayList<String> JsonPathArr = JsonPath.read(swagger.toString(),
						"$..x-bot-utter[?(@.parameterName==\"" + entities.get(i) + "\")].utter");
				HashMap<String, String> text = new HashMap<String, String>();
				text.put("text", JsonPathArr.get(0));
				ArrayList<Object> textObject = new ArrayList<Object>();
				textObject.add(text);
				utter.put("utter_ask_" + entities.get(i), textObject);
			} catch (ClassCastException e) {
				System.out.println(e.getMessage());
			} catch (IndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
			}
		}
		return utter;
	}

	public HashMap<String, Object> post_sessionConfig() {
		HashMap<String, Object> sessionConfig = new HashMap<String, Object>();
		sessionConfig.put("session_expiration_time", 60);
		sessionConfig.put("carry_over_slots_to_new_session", true);
		return sessionConfig;
	}

	public ArrayList<String> getParameters(String pathName) {
		ArrayList<String> parameters = new ArrayList<String>();
		ArrayList<String> utterParameterName = new ArrayList<String>();
		try {
			utterParameterName = JsonPath.read(swagger.toString(),
					"$.paths.['" + pathName + "'].get.x-bot-utter[*].parameterName");
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		} catch (PathNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			JSONArray parametersArray = (JSONArray) ((JSONObject) ((JSONObject) ((JSONObject) swagger.get("paths"))
					.get(pathName)).get("get")).get("parameters");
			for (int i = 0; i < parametersArray.length(); i++) {
				if (utterParameterName.contains(((JSONObject) parametersArray.get(i)).get("name"))) {
					parameters.add((String) ((JSONObject) parametersArray.get(i)).get("name"));
				}
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return parameters;
	}

	public ArrayList<String> removeRepeatParameters(HashMap<String, Object> flowObj, String flowName) {
		ArrayList<String> path = (ArrayList<String>) flowObj.get(flowName);
		ArrayList<String> allParameters = new ArrayList<String>();
		for (int i = 0; i < path.size(); i++) {
			ArrayList<String> Parameters = getParameters((String) path.get(i));
			for (int j = 0; j < Parameters.size(); j++) {
				if (!allParameters.contains(Parameters.get(j))) {
					allParameters.add(Parameters.get(j));
				}
			}
		}
		return allParameters;
	}
}
