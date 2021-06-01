package ntou.cs.sose.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ntou.cs.sose.entity.BotenSwagger;

public class RulesHandler extends InputOutputHandler {
	private HashMap<String, Object> rules = new HashMap<String, Object>();

	public RulesHandler(BotenSwagger req) {
		super(req);
		rules = post_rules();
	}

	public HashMap<String, Object> getRules() {
		return rules;
	}

	public HashMap<String, Object> post_rules() {
		ArrayList<String> flow = setFlow();
		HashMap<String, Object> rules = new HashMap<String, Object>();
		ArrayList<Object> rule = new ArrayList<Object>();

		for (int i = 0; i < flow.size(); i++) {
			HashMap<String, Object> activateFormRule = new HashMap<String, Object>();
			activateFormRule.put("rule", "Activate " + flow.get(i) + "_form");
			activateFormRule.put("steps", post_activateForm((String) flow.get(i)));

			LinkedHashMap<String, Object> submitFormRule = new LinkedHashMap<String, Object>();
			HashMap<String, Object> condition = new HashMap<String, Object>();
			submitFormRule.put("rule", "Submit " + flow.get(i) + "_form");
			condition.put("active_loop", flow.get(i) + "__form");
			submitFormRule.put("condition", condition);
			submitFormRule.put("steps", post_submitForm((String) flow.get(i)));

			HashMap<String, Object> parametersListRule = new HashMap<String, Object>();
			parametersListRule.put("rule", "See " + flow.get(i) + " parameters list");
			parametersListRule.put("steps", post_parametersList((String) flow.get(i)));

			HashMap<String, Object> usePathRule = new HashMap<String, Object>();
			usePathRule.put("rule", "Use " + flow.get(i));
			usePathRule.put("steps", post_usePath((String) flow.get(i)));

			rule.add(activateFormRule);
			rule.add(submitFormRule);
			rule.add(parametersListRule);
			rule.add(usePathRule);
		}

		HashMap<String, Object> autoLocationRule = new HashMap<String, Object>();
		autoLocationRule.put("rule", "Set location");
		autoLocationRule.put("steps", post_autoLocationPath());
		rule.add(autoLocationRule);

		rules.put("version", "2.0");
		rules.put("rules", rule);
		return rules;
	}

	public ArrayList<Object> post_activateForm(String flow) {
		ArrayList<Object> steps = new ArrayList<Object>();
		HashMap<String, String> intent = new HashMap<String, String>();
		HashMap<String, String> setIntentAction = new HashMap<String, String>();
		HashMap<String, String> setDefaultAction = new HashMap<String, String>();
		HashMap<String, String> formaction = new HashMap<String, String>();
		HashMap<String, String> activeLoop = new HashMap<String, String>();
		intent.put("intent", "fill_parameters_" + flow);
		setIntentAction.put("action", "action_set_intent");
		setDefaultAction.put("action", "action_set_default");
		formaction.put("action", flow + "_form");
		activeLoop.put("active_loop", flow + "_form");

		steps.add(intent);
		steps.add(setIntentAction);
		steps.add(setDefaultAction);
		steps.add(formaction);
		steps.add(activeLoop);
		return steps;
	}

	public ArrayList<Object> post_submitForm(String flow) {
		ArrayList<Object> steps = new ArrayList<Object>();
		HashMap<String, String> formaction = new HashMap<String, String>();
		HashMap<String, String> activeLoop = new HashMap<String, String>();

		HashMap<String, ArrayList<Object>> slotWasSet = new HashMap<String, ArrayList<Object>>();
		ArrayList<Object> slotWasSetArray = new ArrayList<Object>();
		HashMap<String, String> requestedSlot = new HashMap<String, String>();

		HashMap<String, String> slotsValuesAction = new HashMap<String, String>();
		HashMap<String, String> useApiAction = new HashMap<String, String>();

		formaction.put("action", flow + "_form");
		activeLoop.put("active_loop", null);

		requestedSlot.put("requested_slot", null);
		slotWasSetArray.add(requestedSlot);
		slotWasSet.put("slot_was_set", slotWasSetArray);

		slotsValuesAction.put("action", "action_slots_values");
		useApiAction.put("action", "action_use_api");

		steps.add(formaction);
		steps.add(activeLoop);
		steps.add(slotWasSet);
		steps.add(slotsValuesAction);
		steps.add(useApiAction);

		return steps;
	}

	public ArrayList<Object> post_parametersList(String flow) {
		ArrayList<Object> steps = new ArrayList<Object>();
		HashMap<String, String> intent = new HashMap<String, String>();
		HashMap<String, String> action = new HashMap<String, String>();
		intent.put("intent", "parameters_list_" + flow);
		action.put("action", "action_slots_values");
		steps.add(intent);
		steps.add(action);
		return steps;
	}

	public ArrayList<Object> post_usePath(String flow) {
		ArrayList<Object> steps = new ArrayList<Object>();
		HashMap<String, String> intent = new HashMap<String, String>();
		HashMap<String, String> action = new HashMap<String, String>();
		intent.put("intent", "get_api_" + flow);
		action.put("action", "action_use_api");
		steps.add(intent);
		steps.add(action);
		return steps;
	}

	public ArrayList<Object> post_autoLocationPath() {
		ArrayList<Object> steps = new ArrayList<Object>();
		HashMap<String, String> intent = new HashMap<String, String>();
		HashMap<String, String> action = new HashMap<String, String>();
		intent.put("intent", "auto_get_location");
		action.put("action", "action_auto_location");
		steps.add(intent);
		steps.add(action);
		return steps;
	}
}
