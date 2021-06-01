package ntou.cs.sose.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class JsonControllerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// parse JSON
		String jsonString = "{\"gender\":null,\"age\":20,\"name\":\"Mary\"}";
		String jsonAsYaml = null;
		try {
			JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
			jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// save it as YAML
		System.out.println(jsonAsYaml);
	}

}
