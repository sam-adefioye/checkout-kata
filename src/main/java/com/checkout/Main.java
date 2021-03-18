package com.checkout;

import com.checkout.service.impl.InputServiceImpl;
import com.checkout.service.impl.PricingRuleServiceImpl;
import lombok.val;

import java.util.Locale;

import static java.lang.String.format;
import static java.text.NumberFormat.getCurrencyInstance;

public class Main {

    public static void main(String[] args) {
        val pricingRuleService = new PricingRuleServiceImpl();
        val inputService = new InputServiceImpl();
        do {
            val pricingRules = inputService.loadPricingRules();
            val parsedRules = pricingRuleService.parseRules(pricingRules.split(";"));

            pricingRuleService.printRules(parsedRules);

            val basketItems = inputService.loadBasket(parsedRules);
            val parsedBasket = inputService.parseBasket(basketItems);
            val checkoutTotal = inputService.calculateTotal(parsedBasket, parsedRules);

            val currency = getCurrencyInstance(Locale.UK);
            System.out.println(format("The checkout total for the basket %s is: %s", basketItems, currency.format(checkoutTotal)));

        } while (inputService.stop());
    }
}
