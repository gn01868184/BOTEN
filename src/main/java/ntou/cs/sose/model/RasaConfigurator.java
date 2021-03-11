package ntou.cs.sose.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class RasaConfigurator {
	public String nluConfigurator(JSONObject inputOutputConfig) {
		String nlu = "";
		JSONObject nluJsonObject = (JSONObject) inputOutputConfig.get("nlu");
		JSONObject intentJsonObject = (JSONObject) nluJsonObject.get("intent");
		JSONObject regexJsonObject = (JSONObject) nluJsonObject.get("regex");

		for (String intentName : intentJsonObject.keySet()) {
			JSONArray training = (JSONArray) intentJsonObject.get(intentName);
			nlu += "## intent:" + intentName + "\n";
			for (int j = 0; j < training.length(); j++) {
				nlu += "- " + training.get(j) + "\n";
			}
			nlu += "\n";
		}
		if (!regexJsonObject.equals(null)) {
			for (String intentName : regexJsonObject.keySet()) {
				JSONArray training = (JSONArray) regexJsonObject.get(intentName);
				nlu += "## regex:" + intentName + "\n";
				for (int j = 0; j < training.length(); j++) {
					nlu += "- " + training.get(j) + "\n";
				}
				nlu += "\n";
			}
		}
		return nlu;
	}

	public String domainConfigurator(JSONObject inputOutputConfig) {
		String domain = "session_config:\n" + "  session_expiration_time: 60\n"
				+ "  carry_over_slots_to_new_session: true\n";
		JSONObject domainJsonObject = (JSONObject) inputOutputConfig.get("domain");
		JSONArray intents = (JSONArray) domainJsonObject.get("intents");
		domain += "intents:\n" + myArrayToString(intents);
		JSONArray entities = (JSONArray) domainJsonObject.get("entities");
		domain += "entities:\n" + myArrayToString(entities);
		JSONObject slots = (JSONObject) domainJsonObject.get("slots");
		domain += "slots:\n";
		for (String slotsName : slots.keySet()) {
			domain += "  " + slotsName + ":\n";
			String type = ((JSONObject) slots.get(slotsName)).getString("type");
			domain += "    type: " + type + "\n";
		}
		JSONObject responses = (JSONObject) domainJsonObject.get("utter");
		domain += "responses:\n";
		for (String utter : responses.keySet()) {
			domain += "  " + utter + ":\n";
			String text = responses.getString(utter);
			domain += "  - text: " + text + "\n";
		}
		JSONArray actions = (JSONArray) domainJsonObject.get("actions");
		domain += "actions:\n" + myArrayToString(actions);
		JSONArray forms = (JSONArray) domainJsonObject.get("forms");
		domain += "forms:\n" + myArrayToString(forms);
		return domain;
	}

	public String storiesConfigurator(JSONObject inputOutputConfig) {
		String stories = "";
		JSONObject storiesJsonObject = (JSONObject) inputOutputConfig.get("stories");

		for (String storiesName : storiesJsonObject.keySet()) {
			JSONObject intentsJsonObject = (JSONObject) storiesJsonObject.get(storiesName);
			stories += "## " + storiesName + "\n";
			for (String intent : intentsJsonObject.keySet()) {
				JSONArray actions = (JSONArray) intentsJsonObject.get(intent);
				stories += "* " + intent + "\n";
				for (int j = 0; j < actions.length(); j++) {
					stories += " - " + actions.get(j) + "\n";
				}
			}
			stories += "\n";
		}
		return stories;
	}

	public String myArrayToString(JSONArray array) {
		String text = "";
		for (int i = 0; i < array.length(); i++) {
			text += " - " + array.getString(i) + "\n";
		}
		return text;
	}
}
