package com.checkout.service.impl;

import com.checkout.model.PricingRule;
import lombok.val;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InputServiceImplTest {

    private InputServiceImpl underTest;

    @Before
    public void setUp() {
        underTest = new InputServiceImpl();
    }

    @Test
    public void testLoadPricingRules() {
        // Happy Path
        val inputRules = "A,50,3 for 130;B,30,2 for 45;C,20";
        setUserInput(inputRules);
        assertThat(underTest.loadPricingRules()).isEqualTo(inputRules);
    }

    @Test
    public void testValidatePricingRules() {
        // Rules are null or empty or whitespace
        assertThat(underTest.validatePricingRules(null)).isFalse();
        assertThat(underTest.validatePricingRules("")).isFalse();
        assertThat(underTest.validatePricingRules("   ")).isFalse();

        // Rules are empty
        assertThat(underTest.validatePricingRules(";;")).isFalse();

        // Rule rows are not comma-separated
        assertThat(underTest.validatePricingRules("A503 for 130;B302 for 45;C20")).isFalse();

        // SKUs are not letters
        assertThat(underTest.validatePricingRules("1,50,3 for 130;2,30,2 for 45;3,20")).isFalse();

        // Rule rows contain less than 2 or more than 3 items
        assertThat(underTest.validatePricingRules("A;B,30,2 for 45,150;C,20")).isFalse();

        // Rules contain duplicate SKUs
        assertThat(underTest.validatePricingRules("A,50,3 for 130;A,30,2 for 45;C,20")).isFalse();

        // Rules contain invalid special price
        assertThat(underTest.validatePricingRules("A,50,3 for 130;B,30,2-45;C,20")).isFalse();

        // Happy Path
        assertThat(underTest.validatePricingRules("A,50,3 for 130;B,30,2 for 45;C,20")).isTrue();
    }

    @Test
    public void testLoadBasket() {
        // Happy Path
        val inputBasket = "ABBABBA";
        val pricingRules = Set.of(
                new PricingRule("A", 50, new MutablePair<>(3,130)),
                new PricingRule("B", 30, new MutablePair<>(2,45))
        );
        setUserInput(inputBasket);
        assertThat(underTest.loadBasket(pricingRules)).isEqualTo(inputBasket);
    }

    @Test
    public void testValidateBasket() {
        val pricingRules = Set.of(
                new PricingRule("A", 50, new MutablePair<>(3,130)),
                new PricingRule("B", 30, new MutablePair<>(2,45))
        );

        // Basket is null,empty or whitespace
        assertThat(underTest.validateBasket(null, pricingRules)).isFalse();
        assertThat(underTest.validateBasket("", pricingRules)).isFalse();
        assertThat(underTest.validateBasket("   ", pricingRules)).isFalse();

        // Basket does not contain letters
        assertThat(underTest.validateBasket("12323", pricingRules)).isFalse();

        // Basket items are not in the pricing rules
        assertThat(underTest.validateBasket("CDB", pricingRules)).isFalse();

        // Happy Path
        assertThat(underTest.validateBasket("ABBABBA", pricingRules)).isTrue();
    }

    @Test
    public void testParseBasket() {
        // Basket is null, empty or has whitespaces
        assertThat(underTest.parseBasket(null)).isEmpty();
        assertThat(underTest.parseBasket("")).isEmpty();
        assertThat(underTest.parseBasket("    ")).isEmpty();

        // Basket contain non-alphabet characters
        assertThat(underTest.parseBasket("A-B-B-A-B-B-A")).isEmpty();

        // Happy Path
        assertThat(underTest.parseBasket("ABBABBA")).isNotEmpty();
    }

    @Test
    public void testCalculateTotal() {
        val rules = Set.of(
                new PricingRule("A", 50, new MutablePair<>(3,130)),
                new PricingRule("B", 30, new MutablePair<>(2,45))
        );

        val basket = Map.of(
                "A", List.of("A", "A", "A", "A"),
                "B", List.of("B", "B", "B")
        );

        // Basket is null or empty and/or rules are null or empty
        assertThat(underTest.calculateTotal(null, null)).isZero();
        assertThat(underTest.calculateTotal(null, rules)).isZero();
        assertThat(underTest.calculateTotal(basket, null)).isZero();
        assertThat(underTest.calculateTotal(basket, emptySet())).isZero();
        assertThat(underTest.calculateTotal(emptyMap(), rules)).isZero();

        // Happy Path
        assertThat(underTest.calculateTotal(basket, rules)).isEqualTo(180 + 75);
    }

    @Test
    public void testStop() {
        // Happy Path - "Y"
        setUserInput("Y");
        assertThat(underTest.stop()).isTrue();

        // Happy Path - "N"
        setUserInput("N");
        assertThat(underTest.stop()).isFalse();
    }

    @Test
    public void testValidateContinue() {
        // Happy Path "Y" or "N"
        assertThat(underTest.validateContinue("Y")).isTrue();
        assertThat(underTest.validateContinue("N")).isTrue();

        // Input is null, empty or has whitespaces
        assertThat(underTest.validateContinue(null)).isFalse();
        assertThat(underTest.validateContinue("")).isFalse();
        assertThat(underTest.validateContinue("   ")).isFalse();

        // Input length is longer than one
        assertThat(underTest.validateContinue("YN")).isFalse();

        // Input not equal to "Y" or "N"
        assertThat(underTest.validateContinue("Hello")).isFalse();
    }

    public void setUserInput(String userInput) {
        val inputStream = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(inputStream);
    }
}