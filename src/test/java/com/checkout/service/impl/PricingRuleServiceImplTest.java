package com.checkout.service.impl;

import com.checkout.model.PricingRule;
import lombok.val;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PricingRuleServiceImplTest {

    private PricingRuleServiceImpl underTest;

    @Before
    public void setUp() {
        underTest = new PricingRuleServiceImpl();
    }

    @Test
    public void testParseRules() {
        // Happy Path
        val pricingRules = new String[]{"A,50,3 for 100", "B,30,2 for 45", "C,20"};
        assertThat(underTest.parseRules(pricingRules)).isNotEmpty();

        // Pricing rules are null or empty
        assertThat(underTest.parseRules(null)).isEmpty();
        assertThat(underTest.parseRules(new String[]{})).isEmpty();

        // Pricing rules don't contain commas
        assertThat(underTest.parseRules(new String[]{"A503 for 100", "B302 for 45", "C20"})).isEmpty();
    }

    @Test
    public void testPrintRules() {
        // Set of rules is null, empty or contains null rules
        var outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream((outputStream)));

        underTest.printRules(null);
        assertThat(outputStream.toString()).isEmpty();

        underTest.printRules(emptySet());
        assertThat(outputStream.toString()).isEmpty();

        underTest.printRules(new HashSet<>(asList(null, null, null)));
        assertThat(outputStream.toString()).isEmpty();

        // Happy Path
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream((outputStream)));

        val rules = Set.of(
                new PricingRule("A", 50, new MutablePair<>(3,130)),
                new PricingRule("B", 30, new MutablePair<>(2,45)),
                new PricingRule("C", 20)
        );
        underTest.printRules(rules);

        val output = outputStream.toString();
        rules.forEach(r -> {
            assertThat(output).contains(r.getItem());
            assertThat(output).contains(String.valueOf(r.getUnitPrice()));
            assertThat(output).contains(r.getFormattedSpecialPrice());
        });
    }
}