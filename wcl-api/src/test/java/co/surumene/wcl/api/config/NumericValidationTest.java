package co.surumene.wcl.api.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumericValidationTest {
    @Test void preservesFiniteAndIntervalSemantics() {
        assertTrue(NumericValidation.unit(0.0));
        assertTrue(NumericValidation.unit(1.0));
        assertFalse(NumericValidation.unit(Double.NaN));
        assertFalse(NumericValidation.unit(Double.POSITIVE_INFINITY));
        assertFalse(NumericValidation.unit(-0.01));
        assertFalse(NumericValidation.unit(1.01));
        assertTrue(NumericValidation.positive(0.01));
        assertFalse(NumericValidation.positive(0.0));
        assertTrue(NumericValidation.nonNegative(0.0));
        assertFalse(NumericValidation.nonNegative(-0.01));
        assertTrue(NumericValidation.finite(2.0));
        assertFalse(NumericValidation.finite(Double.NEGATIVE_INFINITY));
    }
    @Test void retainsIntegerAndOrderingRules() {
        assertTrue(NumericValidation.atLeastOne(1));
        assertFalse(NumericValidation.atLeastOne(0));
        assertTrue(NumericValidation.nonNegative(0));
        assertFalse(NumericValidation.nonNegative(-1));
        assertTrue(NumericValidation.ordered(1, 1));
        assertFalse(NumericValidation.ordered(2, 1));
    }
}
