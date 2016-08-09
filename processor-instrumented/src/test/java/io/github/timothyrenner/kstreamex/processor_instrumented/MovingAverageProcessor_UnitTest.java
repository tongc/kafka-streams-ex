package io.github.timothyrenner.kstreamex.processor_instrumented;

import org.junit.Test;

import io.github.timothyrenner.kstreamex.processor_instrumented
    .MovingAverageProcessor;

public class MovingAverageProcessor_UnitTest {

    /** Tests that the constructor throws an `IllegalArgumentException` when
     *  the alpha value is not between zero and one.
     */
    @Test(expected=IllegalArgumentException.class)
    public void constructor_test1() {

        new MovingAverageProcessor(-0.2);

    } // CLose constructor_test1.

} // Close MovingAverageProcessor_UnitTest.
