package com.checkout.service.impl;

import com.checkout.model.PricingRule;
import com.checkout.service.PricingRuleService;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

@NoArgsConstructor
public class PricingRuleServiceImpl implements PricingRuleService {
    private static final List<String> TABLE_HEADERS = List.of("Item", "Unit Price", "Special Price");
    private static final String COLUMN_SPACE = format("%1$10s", "");

    @Override
    public Set<PricingRule> parseRules(String[] rules) {
        if (isNull(rules) || rules.length == 0) return emptySet();

        return Arrays.stream(rules)
                .filter(row -> row.contains(","))
                .map(row -> {
                    val rowDetails = row.split(",");
                    if (rowDetails.length == 3) {
                        val specialPrice = rowDetails[2].trim().replace(" ", "").split("for");
                        return new PricingRule(rowDetails[0], parseDouble(rowDetails[1]), new MutablePair<>(parseInt(specialPrice[0]), parseInt(specialPrice[1])));
                    } else {
                        return new PricingRule(rowDetails[0], parseDouble(rowDetails[1]));
                    }
                })
                .collect(toSet());
    }

    @Override
    public void printRules(Set<PricingRule> rules) {
        if (isNull(rules) || rules.isEmpty() || rules.stream().anyMatch(Objects::isNull)) {
            return;
        }

        System.out.println(String.join(COLUMN_SPACE, TABLE_HEADERS));
        rules.stream()
                .sorted(Comparator.comparing(PricingRule::getItem))
                .forEach(rule -> System.out.println(rule.getItem() + COLUMN_SPACE + rule.getUnitPrice() + COLUMN_SPACE + rule.getFormattedSpecialPrice()));
    }
}
