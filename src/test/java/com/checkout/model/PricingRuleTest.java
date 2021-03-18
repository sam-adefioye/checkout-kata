package com.checkout.model;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PricingRuleTest {

    @Test
    public void testGetFormattedSpecialPrice() {
        // Happy Path
        var pricingRule = PricingRule.builder().specialPrice(new ImmutablePair<>(1, 100)).build();
        assertThat(pricingRule.getFormattedSpecialPrice()).isEqualTo("1 for 100");

        // Special price is null
        pricingRule = PricingRule.builder().build();
        assertThat(pricingRule.getFormattedSpecialPrice()).isEmpty();
    }
}