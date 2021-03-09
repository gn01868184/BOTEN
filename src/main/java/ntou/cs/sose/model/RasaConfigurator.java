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
}
