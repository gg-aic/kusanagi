package cz.jan.maly.model.sflo;

import cz.jan.maly.model.Fact;

import java.util.Set;

/**
 * FactSetCardinality term to be implemented by concrete term
 * Created by Jan on 17-Dec-16.
 */
public abstract class FactSetCardinalityTerm<V,T extends Fact<Set<V>>> implements TermInterface {
    protected final T fact;
    protected final double constant;

    protected FactSetCardinalityTerm(T fact, double constant) {
        this.fact = fact;
        this.constant = constant;
    }
}
