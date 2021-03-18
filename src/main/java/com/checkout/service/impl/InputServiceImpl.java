package com.checkout.service.impl;

import com.checkout.model.PricingRule;
import com.checkout.service.InputService;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.apache.commons.lang3.StringUtils.isAlpha;
import static org.apache.commons.lang3.StringUtils.isBlank;

@NoArgsConstructor
public class InputServiceImpl implements InputService {

    private Scanner input;

    @Override
    public String loadPricingRules() {
        String pricingRule;
        do {
            input = new Scanner(System.in);
            System.out.println("Please input the pricing rules in the format: <SKU>,<unit price>, and optional <special price>. \nE.g: A,50,3 for 130;B,30,2 for 45;C,20");
            pricingRule = input.nextLine();

        } while (!validatePricingRules(pricingRule));
        return pricingRule;
    }

    @Override
    public boolean validatePricingRules(String rules) {
        if (isBlank(rules)) {
            return false;
        }

        val rulesArray = rules.split(";");
        if (rulesArray.length == 0) return false;

        for (val row: rulesArray) {
            if (!row.contains(",")) return false;
        }

        val invalidSKULabel = stream(rulesArray)
                .map(row -> row.toUpperCase().charAt(0))
                .anyMatch(c -> !Character.isLetter(c));
        if (invalidSKULabel) return false;

        val invalidRowLength = stream(rulesArray)
                .map(row -> row.split(","))
                .anyMatch(row -> row.length < 2 || row.length > 3);
        if (invalidRowLength) return false;

        val skuList = stream(rulesArray)
                .map(row -> row.toUpperCase().charAt(0))
                .collect(toList());
        val hasDuplicates = skuList.size() != skuList.stream().distinct().count();
        if (hasDuplicates) return false;

        val invalidSpecialPrice = stream(rulesArray)
                .map(row -> row.split(","))
                .filter(arr -> arr.length == 3)
                .anyMatch(row -> !row[2].contains("for"));
        if (invalidSpecialPrice) return false;

        return true;
    }

    @Override
    public String loadBasket(Set<PricingRule> rules) {
        String transactionList;
        do {
            input = new Scanner(System.in);
            System.out.println("Please input basket items in the format, e.g. ABBABBA. Ensure that the supplied SKUs exist in the current pricing rule.");
            transactionList = input.nextLine();

        } while (!validateBasket(transactionList, rules));
        return transactionList;
    }

    @Override
    public boolean validateBasket(String basket, Set<PricingRule> rules) {
        if (isBlank(basket)) {
            return false;
        }

        val transactionArr = basket.split("");
        if (stream(transactionArr).anyMatch(t -> !isAlpha(t))) {
            return false;
        }

        val skuSet = rules.stream().map(PricingRule::getItem).collect(toSet());
        if (stream(transactionArr).anyMatch(t -> !skuSet.contains(t))) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, List<String>> parseBasket(String basket) {
        if (isBlank(basket) || stream(basket.split("")).anyMatch(str -> !isAlpha(str))) {
            return emptyMap();
        }

        return stream(basket.split("")).collect(Collectors.groupingBy(sku -> sku));
    }

    @Override
    public double calculateTotal(Map<String, List<String>> basket, Set<PricingRule> rules) {
        if (anyNull(basket, rules) || basket.isEmpty() || rules.isEmpty()) {
            return 0;
        }

        return basket.entrySet().stream().mapToDouble(entry -> {
            val sku = entry.getKey();
            val list = entry.getValue();
            val optional = rules.stream()
                    .filter(rule -> nonNull(rule.getItem()) && rule.getItem().equals(sku))
                    .findFirst();

            if (optional.isPresent()) {
                val pricingRule = optional.get();
                val specialRule = pricingRule.getSpecialPrice();
                val unitPrice = pricingRule.getUnitPrice();

                // If the SKU has a special price, then check if the # of occurrences (count) of the SKU is a factor of the quantity value in the special price,
                // and calculate the total price using the remainder and the closest multiple of the special price quantity value to the SKU count.
                if (nonNull(specialRule)) {
                    val quantity = specialRule.getLeft();
                    val value = specialRule.getRight();
                    val count = list.size();
                    val remainder = count % quantity;
                    val units = (double) (count - remainder) / quantity;
                    return (units * (units % quantity == 0 || units % quantity == units ? value : unitPrice)) + (remainder * unitPrice);
                } else {
                    return unitPrice * list.size();
                }
            } else {
                return 0;
            }

        }).sum();
    }

    @Override
    public boolean stop() {
        input = new Scanner(System.in);
        System.out.println("Would you like to start a new transaction? Please answer with Y or N:");

        var answer = input.next();
        while (!validateContinue(answer)) {
            System.out.println("Incorrect input, please answer Y or N:");
            answer = input.next();
        }
        return answer.equals("Y");
    }

    @Override
    public boolean validateContinue(String continueInput) {
        return !isBlank(continueInput) && continueInput.trim().length() == 1 && (continueInput.trim().equals("Y") || continueInput.trim().equals("N"));
    }
}
