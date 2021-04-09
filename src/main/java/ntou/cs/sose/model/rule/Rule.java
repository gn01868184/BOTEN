package ntou.cs.sose.model.rule;

import java.util.ArrayList;

import ntou.cs.sose.entity.BotenSwagger;

public enum Rule {
	SWAGGERVALVALIDATOR {
		@Override
		public ArrayList doValidation(BotenSwagger botenSwagger) {
			SwaggerChecker swaggerChecker = new SwaggerChecker();
			swaggerChecker.setBotenRule(new SwaggerValidator());
			return swaggerChecker.execute(botenSwagger);
		}
	},
	JSONSCHEMAVALVALIDATOR {
		@Override
		public ArrayList doValidation(BotenSwagger botenSwagger) {
			SwaggerChecker swaggerChecker = new SwaggerChecker();
			swaggerChecker.setBotenRule(new JsonSchemaValidator());
			return swaggerChecker.execute(botenSwagger);
		}
	},
	INFOCHATBOTFLOWRULE {
		@Override
		public ArrayList doValidation(BotenSwagger botenSwagger) {
			SwaggerChecker swaggerChecker = new SwaggerChecker();
			swaggerChecker.setBotenRule(new InfoChatbotFlowRule());
			return swaggerChecker.execute(botenSwagger);
		}
	},
	PATHSCHATBOTFLOWRULE {
		@Override
		public ArrayList doValidation(BotenSwagger botenSwagger) {
			SwaggerChecker swaggerChecker = new SwaggerChecker();
			swaggerChecker.setBotenRule(new PathsChatbotFlowRule());
			return swaggerChecker.execute(botenSwagger);
		}
	},
	UTTERANDENTITYRULE {
		@Override
		public ArrayList doValidation(BotenSwagger botenSwagger) {
			SwaggerChecker swaggerChecker = new SwaggerChecker();
			swaggerChecker.setBotenRule(new UtterAndEntityRule());
			return swaggerChecker.execute(botenSwagger);
		}
	},
	UTTERANDENTITYCORRESPONDRULE {
		@Override
		public ArrayList doValidation(BotenSwagger botenSwagger) {
			SwaggerChecker swaggerChecker = new SwaggerChecker();
			swaggerChecker.setBotenRule(new UtterAndEntityCorrespondRule());
			return swaggerChecker.execute(botenSwagger);
		}
	};

	public abstract ArrayList doValidation(BotenSwagger botenSwagger);
}
