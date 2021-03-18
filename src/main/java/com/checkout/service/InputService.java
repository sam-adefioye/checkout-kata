package com.checkout.service;

import com.checkout.model.PricingRule;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A helper class to parse and validate user input.
 */
public interface InputService {

    /**
     * Read in user-supplied pricing rules.
     * @return The pricing rules.
     */
    String loadPricingRules();

    /**
     * Validate the user-supplied pricing rules.
     * @param rules The pricing rules.
     * @return <code>true</code> if they are valid, otherwise <code>false</code>.
     */
    boolean validatePricingRules(String rules);

    /**
     * Read in user-supplied SKU basket.
     * @param rules The set of {@link PricingRule}s.
     * @return The list of SKUs in the basket.
     */
    String loadBasket(Set<PricingRule> rules);

    /**
     * Validate the user-supplied SKUs in the basket.
     * @param basket The basket.
     * @param rules The set of {@link PricingRule}s.
     * @return <code>true</code> if they are valid, otherwise <code>false</code>.
     */
    boolean validateBasket(String basket, Set<PricingRule> rules);

    /**
     * Parse the basket of SKUs.
     * @param basket The user-supplied basket.
     * @return The map of SKU to SKU-counts.
     */
    Map<String, List<String>> parseBasket(String basket);

    /**
     * Calculate the checkout total.
     * @param basket The basket.
     * @param rules The set of {@link PricingRule}s.
     * @return The checkout total.
     */
    double calculateTotal(Map<String, List<String>> basket, Set<PricingRule> rules);

    /**
     * Determine whether the user would like to stop or start again.
     * @return <code>true</code> if they would like to stop, otherwise <code>false</code>.
     */
    boolean stop();

    /**
     * Validate the user's input for stopping the program.
     * @param continueInput The user's input.
     * @return <code>true</code> if their input equals 'Y' or 'N', otherwise <code>false</code>.
     */
    boolean validateContinue(String continueInput);
}
