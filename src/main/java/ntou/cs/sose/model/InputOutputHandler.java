package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.jayway.jsonpath.JsonPath;

import ntou.cs.sose.entity.BotenSwagger;

public class InputOutputHandler {
	protected JSONObject swagger;
	protected ArrayList<String> allPath;
	protected ArrayList<String> allFlow;
	protected HashMap<String, Object> chatbotFlow;

	public InputOutputHandler() {
	}

	public InputOutputHandler(BotenSwagger req) {
		swagger = req.getSwagger();
		allPath = req.allPath();
		allFlow = req.allFlow();
		chatbotFlow = req.chatbotFlow();
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

	public ArrayList<String> setFlow() {
		ArrayList<String> flow = new ArrayList<String>();
		// change / to _
		for (int i = 0; i < allPath.size(); i++) {
			flow.add(BotenSwagger.changeSign((String) allPath.get(i)));
		}
		flow.addAll(allFlow);
		return flow;
	}

	public String jsonToYaml(String string) {
		String jsonAsYaml = null;
		try {
			JsonNode jsonNodeTree = new ObjectMapper().readTree(string);
			jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonAsYaml;
	}
}
