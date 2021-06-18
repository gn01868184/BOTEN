package ntou.cs.sose.model;

import java.util.HashMap;

import org.json.JSONObject;

import ntou.cs.sose.entity.BotenSwagger;

public class TitleConfigurator {
	private JSONObject swagger;
	private HashMap<String, String> title = new HashMap<String, String>();

	public TitleConfigurator(BotenSwagger req) {
		swagger = req.getSwagger();
		title.put("title", (String) swagger.getJSONObject("info").get("title"));
	}

	public HashMap<String, String> getTitle() {
		return title;
	}
}
