package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import ntou.cs.sose.entity.BotenSwagger;

public class JsonSchemaValidator implements BotenRule {
	private final JSONObject jsonSchema = new JSONObject(
			new JSONTokener(getClass().getResourceAsStream("/schema.json")));
	private final Schema schema = SchemaLoader.load(jsonSchema);

	@Override
	public ArrayList checkRule(BotenSwagger botenSwagger) {
		JSONObject swagger = botenSwagger.getSwagger();
		ArrayList chatbotEnabledSwaggerErrors = new ArrayList();
		try {
			schema.validate(swagger);
			System.out.println("jsonSchema Success");
			return chatbotEnabledSwaggerErrors;
		} catch (ValidationException e) {
			System.out.print("jsonSchema: ");
			System.out.println(e.getMessage());
			chatbotEnabledSwaggerErrors.add(e.getMessage());
			return chatbotEnabledSwaggerErrors;
		}
	}

}
