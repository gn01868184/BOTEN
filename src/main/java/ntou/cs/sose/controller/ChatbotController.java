package ntou.cs.sose.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping(value = "/swaggerCheck", produces = "application/json")
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
}