package ntou.cs.sose.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {
	ServiceManager serviceManager = new ServiceManager();

	@RequestMapping("/home")
	public String greeting() {
		return "index";
	}

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

	@GetMapping(value = "/nlu.md", produces = "application/json")
	@ResponseBody
	public String showNlu() {
		return serviceManager.showNlu();
	}

	@GetMapping(value = "/domain.yml", produces = "application/json")
	@ResponseBody
	public String showDomain() {
		return serviceManager.showDomain();
	}

	@GetMapping(value = "/stories.md", produces = "application/json")
	@ResponseBody
	public String showStories() {
		return serviceManager.showStories();
	}
}