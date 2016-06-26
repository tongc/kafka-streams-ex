package io.github.timothyrenner.kstreamex.processor;

import org.junit.Test;

import io.github.timothyrenner.kstreamex.processor.Message;

/** Unit tests the Message class.
 *
 * @author Timothy Renner
 */
public class Message_UnitTest {

    /** Tests that the constructor throws an exception when the type is
     *  invalid.
     */
    @Test(expected=IllegalArgumentException.class)
    public void constructor_test1() {
        
        new Message("not-valid", 0.1);

    } // Close constructor_test1.
} // Close Message_UnitTest.
