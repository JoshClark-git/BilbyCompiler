package semanticAnalyzer.signatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import semanticAnalyzer.types.Type;

public class PromotionSignature {
	private List<Promotion> promotions;
	private FunctionSignature signature;
	private Type resultType;
	
	public PromotionSignature(FunctionSignature signature, List<Promotion> promotions) {
		this.promotions = new ArrayList<Promotion>(promotions);
		this.signature = signature;
		this.resultType = signature.resultType().atomicType();
	}
	

	public Type resultType() {
		return resultType;
	}
	public FunctionSignature getSignature() {
		return signature;
	}
	
	public List<Promotion> getPromotions() {
		return promotions;
	}

	public int numPromotions() {
        int numPromotions = 0;
 
        for(Promotion promotion: promotions) {
                if(promotion != Promotion.NONE) {
                    numPromotions++;
                }
        }
        return numPromotions;
	}

	public static List<PromotionSignature> makeAll(FunctionSignatures functionSignatures, List<Type> actuals) {
		List<PromotionSignature> all = new ArrayList<PromotionSignature>();
		for(FunctionSignature functionSignature : functionSignatures) {
			all.addAll(makeAll(functionSignature,actuals));
		}
		return all;
	}


	private static List<PromotionSignature> makeAll(FunctionSignature functionSignature, List<Type> actuals) {
		List<PromotionSignature> result = new ArrayList<PromotionSignature>();
		if(actuals.size() == 1) {
			Type actual = actuals.get(0);
			for(Promotion promotion : Promotion.values()) {
				if(promotion.applies(actual)) {
					Type promotedActual = promotion.apply(actual);
					PromotionSignature promotionSignature = tryTypes(functionSignature, promotion, promotedActual);
					if(promotionSignature != nullInstance()) {
						result.add(promotionSignature);
					}
				}
			}
			return result;
		}
		else if(actuals.size() == 2) {
			Type actual0 = actuals.get(0);
			Type actual1 = actuals.get(1);
			for(Promotion promotion0 : Promotion.values()){
				for(Promotion promotion1 : Promotion.values()) {
					if((promotion0.applies(actual0)) && (promotion1.applies(actual1))) {
						Type promotedActual0 = promotion0.apply(actual0);
						Type promotedActual1 = promotion1.apply(actual1);
						PromotionSignature promotionSignature = tryTypes(functionSignature, promotion0,promotion1,promotedActual0, promotedActual1);
						if(promotionSignature != nullInstance()) {
							result.add(promotionSignature);
						}
						
					}
				}
			}
			return result;
		}
		else if(actuals.size() == 3) {
			Type actual0 = actuals.get(0);
			Type actual1 = actuals.get(1);
			Type actual2 = actuals.get(2);
			for(Promotion promotion0 : Promotion.values()){
				for(Promotion promotion1 : Promotion.values()) {
					for(Promotion promotion2 : Promotion.values()) {
						if((promotion0.applies(actual0)) && (promotion1.applies(actual1)) && (promotion2.applies(actual2)) ) {
							Type promotedActual0 = promotion0.apply(actual0);
							Type promotedActual1 = promotion1.apply(actual1);
							Type promotedActual2 = promotion2.apply(actual1);
							PromotionSignature promotionSignature = tryTypes(functionSignature, promotion0,promotion1,promotion2,promotedActual0, promotedActual1,promotedActual2);
							if(promotionSignature != nullInstance()) {
								result.add(promotionSignature);
							}
							
						}
					}
				}
			}
			return result;
		}
		else {
			throw new RuntimeException("makeAll called with more than two actuals");
		}
	}
	
	private static PromotionSignature tryTypes(FunctionSignature functionSignature, Promotion promotion0, Promotion promotion1, Type promotedActual0, Type promotedActual1) {
		 if(functionSignature.accepts(Arrays.asList(promotedActual0, promotedActual1))) {
             return new PromotionSignature(functionSignature, Arrays.asList(promotion0, promotion1));
     }
     else {
         return nullInstance();
     }
	}
	
	private static PromotionSignature tryTypes(FunctionSignature functionSignature, Promotion promotion0, Promotion promotion1, Promotion promotion2, Type promotedActual0, Type promotedActual1, Type promotedActual2) {
		 if(functionSignature.accepts(Arrays.asList(promotedActual0, promotedActual1,promotedActual2))) {
            return new PromotionSignature(functionSignature, Arrays.asList(promotion0, promotion1,promotion2));
    }
    else {
        return nullInstance();
    }
	}


	private static PromotionSignature tryTypes(FunctionSignature functionSignature, Promotion promotion, Type promotedActual) {
        if(functionSignature.accepts(Arrays.asList(promotedActual))) {
                return new PromotionSignature(functionSignature, Arrays.asList(promotion));
        }
        else {
            return nullInstance();
        }
    }

	static private PromotionSignature nullInstance = null;
	private static PromotionSignature nullInstance() {
		if(nullInstance == null) {
			nullInstance = new PromotionSignature(FunctionSignature.nullInstance(), new ArrayList<Promotion>());
		}
		return nullInstance;
	}

}
