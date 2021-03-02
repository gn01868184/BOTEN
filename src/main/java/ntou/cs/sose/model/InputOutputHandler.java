package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class InputOutputHandler {
	JSONObject swagger;

	public HashMap<String, Object> ioc(JSONObject req) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		swagger = req;
		HashMap<String, Object> nlu = setNlu();
		HashMap<String, Object> domain = setDomain();
		HashMap<String, Object> stories = setStories();
		res.put("nlu", nlu);
		res.put("domain", domain);
		res.put("stories", stories);
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
		ArrayList intents = setIntents();
		ArrayList entities = setEntities();
		HashMap<String, Object> slots = setSlots();
		ArrayList actions = setActions();
		ArrayList forms = setForms();
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
		ArrayList flow = setFlow();
		HashMap<String, Object> stories = new HashMap<String, Object>();
		for (int i = 0; i < flow.size(); i++) {
			stories.put("into " + flow.get(i) + "_form path", setIntoPath((String) flow.get(i)));
			stories.put(flow.get(i) + " parameters list", setParametersList((String) flow.get(i)));
			stories.put("use " + flow.get(i) + " path", setUsePath((String) flow.get(i)));
		}
		return stories;
	}

	public ArrayList getAllPath() {
		org.json.JSONObject paths = swagger.getJSONObject("paths");
		Iterator<String> keys = paths.keys();
		ArrayList pathsArr = new ArrayList();
		while (keys.hasNext()) {
			String key = keys.next();
			pathsArr.add(key);
		}
		return pathsArr;
	}

	public ArrayList getAllFlow() {
		ArrayList flowArr = new ArrayList();
		try {
			flowArr = JsonPath.read(swagger.toString(), "$.info..x-chatbotFlow..flowName");
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return flowArr;
	}

	public ArrayList getAllParameter() {
		ArrayList parArr = new ArrayList();
		try {
			parArr = JsonPath.read(swagger.toString(), "$.paths..x-user-entity");
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return parArr;
	}

	public HashMap<String, Object> setIntent() {
		HashMap<String, Object> intent = new HashMap<String, Object>();
		ArrayList flow = setFlow();
		ArrayList inform = setInformIntent();
		for (int i = 0; i < flow.size(); i++) {
			intent.put("get_" + (String) flow.get(i), setGetIntent((String) flow.get(i)));
			intent.put("parameters_list_" + (String) flow.get(i), setParametersListIntent((String) flow.get(i)));
			intent.put("fill_parameters_" + (String) flow.get(i), setFillParameters((String) flow.get(i)));
		}

		intent.put("inform", inform);
		return intent;
	}

	public ArrayList setGetIntent(String flow) {
		ArrayList train = new ArrayList();
		train.add("I want to get " + flow);
		train.add("I want to use " + flow);
		return train;
	}

	public ArrayList setParametersListIntent(String flow) {
		ArrayList train = new ArrayList();
		train.add("I want to see the " + flow + " parameters list");
		train.add("see the " + flow + " parameters list");
		return train;
	}

	public ArrayList setFillParameters(String flow) {
		ArrayList train = new ArrayList();
		train.add("Fill in the " + flow + " parameters");
		train.add("Fill " + flow);
		return train;
	}

	public ArrayList setInformIntent() {
		ArrayList allParameter = getAllParameter();
		ArrayList inform = new ArrayList();
		ArrayList parameterArr = new ArrayList();
		ArrayList notRepeatParma = new ArrayList();
		ArrayList entityValue = new ArrayList();
		ArrayList entityValueArr = new ArrayList();
		try {
			parameterArr = JsonPath.read(allParameter.toString(), "$..parameterName");
			notRepeatParma = (ArrayList) parameterArr.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i < notRepeatParma.size(); i++) {
				entityValue = JsonPath.read(allParameter.toString(),
						"$..[?(@.parameterName==\"" + notRepeatParma.get(i) + "\")].entityValue");
				entityValueArr = (ArrayList) entityValue.get(0);
				for (int j = 0; j < entityValueArr.size(); j++) {
					inform.add("[" + notRepeatParma.get(i) + "](" + entityValueArr.get(j) + ")");
				}
			}
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return inform;
	}

	public HashMap<String, Object> setRegex() {
		ArrayList allParameter = getAllParameter();
		ArrayList allRegexArr = new ArrayList();
		ArrayList parameterArr = new ArrayList();
		ArrayList notRepeatParma = new ArrayList();
		HashMap<String, Object> regexObj = new HashMap<String, Object>();
		try {
			allRegexArr = JsonPath.read(allParameter.toString(), "$..[?(@.regex)]");
			parameterArr = JsonPath.read(allRegexArr.toString(), "$..parameterName");
			notRepeatParma = (ArrayList) parameterArr.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i < notRepeatParma.size(); i++) {
				ArrayList regexTwoDimensionalArray = JsonPath.read(allRegexArr.toString(),
						"$.[?(@.parameterName==\"" + notRepeatParma.get(i) + "\")].regex");
				ArrayList regexArr = new ArrayList();
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

	public ArrayList setFlow() {
		ArrayList allPath = getAllPath();
		ArrayList allFlow = getAllFlow();
		ArrayList flow = new ArrayList();
		// change / to _
		for (int i = 0; i < allPath.size(); i++) {
			String path = (String) allPath.get(i);
			ArrayList pathArr = new ArrayList();
			String changePath = "";
			for (String retval : path.split("/")) {
				if (!retval.equals("")) {
					changePath = changePath + retval + "_";
				}
			}
			flow.add(changePath.substring(0, changePath.length() - 1));
		}
		flow.addAll(allFlow);
		return flow;
	}

	public ArrayList setIntents() {
		ArrayList flow = setFlow();
		ArrayList intents = new ArrayList();
		for (int i = 0; i < flow.size(); i++) {
			intents.add("get_" + flow.get(i));
			intents.add("parameters_list_" + flow.get(i));
			intents.add("fill_parameters_" + flow.get(i));
		}
		return intents;
	}

	public ArrayList setEntities() {
		ArrayList allParameter = getAllParameter();
		ArrayList parameterArr = new ArrayList();
		ArrayList notRepeatParma = new ArrayList();
		try {
			parameterArr = JsonPath.read(allParameter.toString(), "$..parameterName");
			notRepeatParma = (ArrayList) parameterArr.stream().distinct().collect(Collectors.toList());
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
		return notRepeatParma;
	}

	public HashMap<String, Object> setSlots() {
		ArrayList entities = (ArrayList) setEntities();
		HashMap<String, Object> slots = new HashMap<String, Object>();
		HashMap<String, String> type = new HashMap<String, String>();
		type.put("type", "unfeaturized");
		for (int i = 0; i < entities.size(); i++) {
			slots.put((String) entities.get(i), type);
		}
		return slots;
	}

	public ArrayList setActions() {
		ArrayList entities = setEntities();
		ArrayList actions = new ArrayList();
		for (int i = 0; i < entities.size(); i++) {
			actions.add("utter_ask_" + entities.get(i));
		}
		actions.add("action_slots_values");
		actions.add("action_use_api");
		return actions;
	}

	public ArrayList setForms() {
		ArrayList flow = setFlow();
		ArrayList forms = new ArrayList();
		HashMap<String, Object> formsObj = new HashMap<String, Object>();
		for (int i = 0; i < flow.size(); i++) {
			forms.add(flow.get(i) + "_form");
		}
		return forms;
	}

	public HashMap<String, String> setUtter() {
		ArrayList entities = setEntities();
		HashMap<String, String> utter = new HashMap<String, String>();
		for (int i = 0; i < entities.size(); i++) {
			try {
				ArrayList JsonPathArr = JsonPath.read(swagger.toString(),
						"$..x-bot-utter[?(@.parameterName==\"" + entities.get(i) + "\")].utter");
				utter.put("utter_ask_" + entities.get(i), (String) JsonPathArr.get(0));
			} catch (ClassCastException e) {
				System.out.println(e.getMessage());
			}
		}
		return utter;
	}

	public HashMap<String, Object> setIntoPath(String flow) {
		ArrayList actions = new ArrayList();
		HashMap<String, Object> intoPath = new HashMap<String, Object>();
		actions.add(flow + "_form");
		actions.add("form{\"name\": \"" + flow + "_form\"}");
		actions.add("form{\"name\": \"null\"}");
		intoPath.put("fill_" + flow, actions);
		return intoPath;
	}

	public HashMap<String, Object> setParametersList(String flow) {
		ArrayList actions = new ArrayList();
		HashMap<String, Object> parametersListPath = new HashMap<String, Object>();
		actions.add("action_slots_values");
		parametersListPath.put("parameters_list_" + flow, actions);
		return parametersListPath;
	}

	public HashMap<String, Object> setUsePath(String flow) {
		ArrayList actions = new ArrayList();
		HashMap<String, Object> usePath = new HashMap<String, Object>();
		actions.add("action_use_api");
		usePath.put("get_" + flow, actions);
		return usePath;
	}
}
