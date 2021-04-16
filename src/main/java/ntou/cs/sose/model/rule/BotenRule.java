package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import ntou.cs.sose.entity.BotenSwagger;

public interface BotenRule {
	public ArrayList<String> checkRules(BotenSwagger botenSwagger);
}
