package ntou.cs.sose.test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import ntou.cs.sose.entity.BotenSwagger;
import ntou.cs.sose.model.DomainHandler;
import ntou.cs.sose.model.NluHandler;
import ntou.cs.sose.model.RulesHandler;
import ntou.cs.sose.model.StroiesHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RasaTest {

	public static void main(String[] args) {
		BotenSwagger botenSwagger = new BotenSwagger(
				"https://gn01868184.github.io/jsonExample.github.io/foursquareSwagger.json");
		NluHandler nluHandler = new NluHandler(botenSwagger);
		DomainHandler domainHandler = new DomainHandler(botenSwagger);
		StroiesHandler stroiesHandler = new StroiesHandler(botenSwagger);
		RulesHandler rulesHandler = new RulesHandler(botenSwagger);
		HashMap<String, Object> rasaConfig = new HashMap<String, Object>();
		rasaConfig.put("nlu", nluHandler.getNlu());
		rasaConfig.put("domain", domainHandler.getDomain());
		rasaConfig.put("stories", stroiesHandler.getStroies());
		rasaConfig.put("rules", rulesHandler.getRules());

		try {
			String inputOutputConfig = new ObjectMapper().writeValueAsString(rasaConfig);
			botenSwagger.setInputOutputConfig(inputOutputConfig);
			System.out.println(botenSwagger.getInputOutputConfig());
			
			ObjectMapper mapper = new ObjectMapper();
			LinkedHashMap<String, Object> map = mapper.readValue(inputOutputConfig, LinkedHashMap.class);
			System.out.println(map.get("rules"));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
