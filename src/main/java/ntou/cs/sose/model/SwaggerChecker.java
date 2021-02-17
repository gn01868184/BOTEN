package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;

import net.minidev.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class SwaggerChecker {
    JSONObject jsonSchema = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/schema.json")));
    JSONObject swagger = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/foursquareSwagger.json")));
    Schema schema = SchemaLoader.load(jsonSchema);
	public String jsonSchema() {
	    try {
	    	  schema.validate(swagger);
	    	} catch (ValidationException e) {
	    	  // prints #/rectangle/a: -5.0 is not higher or equal to 0
	    	  System.out.println(e.getMessage());
	    	  // prints all ValidationException
	    	  e.getCausingExceptions().stream()
	    	      .map(ValidationException::getMessage)
	    	      .forEach(System.out::println);
	    	}
	    return null;
	}
	public String jsonPath() {
	    try {
	    	LinkedHashMap<Object, Object> author0 = JsonPath.read(swagger.toString(), "$.paths");
	    	System.out.println(author0);
	    }catch (ClassCastException e) {
	    	System.out.println(e.getMessage());
	    }
	    
	    //驗證jsonPath是否輸入正確
	    try {
	    	String json = "{\"date_as_long\" : 1411455611975}";
	    	JSONArray authors = JsonPath.read(json, "1");
	    	System.out.println(authors);
	    }catch (PathNotFoundException e) {
	    	System.out.println(e.getMessage());
	    }
	    
	    return null;
	}
	public String checkChatbotFlow() {
		// 檢查info-x-chatbotFlow與Swagger內的path是否對應
	    if (swagger.has("info")) {
	    	JSONObject info = swagger.getJSONObject("info");
		    if (info.has("x-chatbotFlow")) {
		    	HashMap<String, Object> allFlow = getChatbotFlow();
		    	for (Object key : allFlow.keySet()) {
		    	     System.out.println(key + " : " + allFlow.get(key));
		    	     ArrayList flowArr = (ArrayList) allFlow.get(key);
		    	     for (int i = 0; i < flowArr.size(); i++) { 
			    	    try {
			    	    	LinkedHashMap<Object, Object> path = JsonPath.read(swagger.toString(), "$.paths." + flowArr.get(i));
			    	    	System.out.println("$.paths." + flowArr.get(i) + ": success");
			    	    }catch (PathNotFoundException e) {
			    	    	System.out.println(e.getMessage());
			    	    	System.out.println("#/info/x-chatbotFlow/" + i + "/flow: [" + flowArr.get(i) + "] endpoint not found in Swagger");
			    	    }
		    	     }
				}
			}
		}

	    return null;
	}
	public HashMap<String, Object> getChatbotFlow() {
    	JSONObject info = swagger.getJSONObject("info");
    	org.json.JSONArray chatbotFlow = info.getJSONArray("x-chatbotFlow");
    	HashMap<String, Object> allFlow = new HashMap<String, Object>();
    	ArrayList pathsArr = new ArrayList();
    	for (int i = 0; i < chatbotFlow.length(); i++) { 
    		pathsArr.clear();
    	    org.json.JSONArray flowArray = chatbotFlow.getJSONObject(i).getJSONArray("flow");
    	    String flowName = chatbotFlow.getJSONObject(i).getString("flowName");
	    	for (int j = 0; j < flowArray.length(); j++) {            
	    	    String flow = flowArray.getString(j);
	    	    pathsArr.add(flow);
	    	}
	    	allFlow.put(flowName, pathsArr);
    	}
    	System.out.println("allFlow:");
    	System.out.println(allFlow);
	    return allFlow;
	}
	public ArrayList getAllPath() {
    	org.json.JSONObject paths = swagger.getJSONObject("paths");
    	Iterator<String> keys = paths.keys();
    	  ArrayList pathsArr = new ArrayList();
    	  while (keys.hasNext()) {
    	    String key = keys.next();
    	    pathsArr.add(key);
    	  }
    	  System.out.println("all path:");
    	  System.out.println(pathsArr);
    	  return pathsArr;
	}
}
