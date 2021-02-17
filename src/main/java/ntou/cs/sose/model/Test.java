package ntou.cs.sose.model;

public class Test {
	public static void main(String[] args) {
		SwaggerChecker sc = new SwaggerChecker();
		System.out.println("jsonSchema:");
		sc.jsonSchema();
		System.out.println("jsonPath:");
		sc.jsonPath();
		System.out.println("jsonParse:");
		sc.jsonParse();
		System.out.println("chatbotFlow:");
		sc.chatbotFlow();
		
	}
}
