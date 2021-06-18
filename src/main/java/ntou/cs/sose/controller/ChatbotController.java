package ntou.cs.sose.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatbotController {
	ServiceManager serviceManager = new ServiceManager();

	@GetMapping(value = "/SwaggerCheck", produces = "application/json")
	@ResponseBody
	public String swaggerCheck(@RequestParam String swaggerURL) {
		return serviceManager.doSwaggerCheck(swaggerURL);
	}

	@GetMapping(value = "/InputOutputHandler", produces = "application/json")
	@ResponseBody
	public String inputOutputHandler() {
		return serviceManager.doInputOutputHandler();
	}

	@GetMapping(value = "/ChatbotConfigurator", produces = "application/json")
	@ResponseBody
	public String chatbotConfigurator() {
		return serviceManager.doChatbotConfigurator();
	}

	@PostMapping(value = "/ReviseInputOutputConfig", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String reviseInputOutputConfig(@RequestBody String inputOutputConfig) {
		return serviceManager.reviseInputOutputConfig(inputOutputConfig);
	}

	@GetMapping(value = "/botenConfig.json", produces = "application/json")
	@ResponseBody
	public String showBotenConfig() {
		return serviceManager.showBotenConfig();
	}

	@GetMapping(value = "/nlu.yml", produces = "text/yaml")
	@ResponseBody
	public String showNlu() {
		return serviceManager.showNlu();
	}

	@GetMapping(value = "/domain.yml", produces = "text/yaml")
	@ResponseBody
	public String showDomain() {
		return serviceManager.showDomain();
	}

	@GetMapping(value = "/stories.yml", produces = "text/yaml")
	@ResponseBody
	public String showStories() {
		return serviceManager.showStories();
	}

	@GetMapping(value = "/rules.yml", produces = "text/yaml")
	@ResponseBody
	public String showRules() {
		return serviceManager.showRules();
	}

	@GetMapping(value = "/title.json", produces = "application/json")
	@ResponseBody
	public String showTitle() {
		return serviceManager.showTitle();
	}
}