package com.checkout.service;

import com.checkout.model.PricingRule;

import java.util.Set;

/**
 * This class parses, creates and prints {@link PricingRule}s.
 */
public interface PricingRuleService {

    /**
     * Parse the supplied pricing rules to a set of {@link PricingRule}.
     * @param rules The pricing rules.
     * @return Set of {@link PricingRule}s.
     */
    Set<PricingRule> parseRules(String[] rules);

    /**
     * Print the supplied set of {@link PricingRule}s.
     * @param rules The {@link PricingRule}s.
     */
    void printRules(Set<PricingRule> rules);
}
