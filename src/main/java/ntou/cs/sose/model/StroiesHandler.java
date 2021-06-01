package ntou.cs.sose.model;

import java.util.HashMap;

import ntou.cs.sose.entity.BotenSwagger;

public class StroiesHandler extends InputOutputHandler {
	private HashMap<String, Object> stories = new HashMap<String, Object>();

	public StroiesHandler(BotenSwagger req) {
		super(req);
		stories = post_stories();
	}

	public HashMap<String, Object> getStroies() {
		return stories;
	}

	public HashMap<String, Object> post_stories() {
		HashMap<String, Object> stories = new HashMap<String, Object>();
		stories.put("version", "2.0");
		stories.put("stories", null);
		return stories;
	}
}
