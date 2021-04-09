package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import ntou.cs.sose.entity.BotenSwagger;

public class SwaggerChecker {
	private BotenRule botenRule;

	public ArrayList execute(BotenSwagger botenSwagger) {
		return botenRule.checkRule(botenSwagger);
	}

	public void setBotenRule(BotenRule botenRule) {
		this.botenRule = botenRule;
	}

}
