package com.checkout.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import static java.util.Objects.nonNull;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PricingRule {
    private String item;
    private double unitPrice;
    private Pair<Integer, Integer> specialPrice;

    public PricingRule(String item, double unitPrice) {
        this.item = item;
        this.unitPrice = unitPrice;
    }

    public String getFormattedSpecialPrice() {
        return nonNull(specialPrice) ? specialPrice.getLeft() + " for " + specialPrice.getRight() : "";
    }
}
