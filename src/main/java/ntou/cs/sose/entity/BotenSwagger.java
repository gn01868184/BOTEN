package ntou.cs.sose.entity;

import org.json.JSONObject;

public class BotenSwagger {
	private JSONObject swagger;
	private JSONObject chatbotEnabledSwaggerErrors;
	private JSONObject inputOutputConfig;
	private JSONObject chatbotConfigurator;

	public JSONObject getSwagger() {
		return swagger;
	}

	public void setSwagger(JSONObject swagger) {
		this.swagger = swagger;
	}

	public JSONObject getChatbotEnabledSwaggerErrors() {
		return chatbotEnabledSwaggerErrors;
	}

	public void setChatbotEnabledSwaggerErrors(JSONObject chatbotEnabledSwaggerErrors) {
		this.chatbotEnabledSwaggerErrors = chatbotEnabledSwaggerErrors;
	}

	public JSONObject getInputOutputConfig() {
		return inputOutputConfig;
	}

	public void setInputOutputConfig(JSONObject inputOutputConfig) {
		this.inputOutputConfig = inputOutputConfig;
	}

	public JSONObject getChatbotConfigurator() {
		return chatbotConfigurator;
	}

	public void setChatbotConfigurator(JSONObject chatbotConfigurator) {
		this.chatbotConfigurator = chatbotConfigurator;
	}
}
