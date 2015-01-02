package net.iturrioz.marranita;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

public class MainUnitTest extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testNothing() throws Exception {
        assertTrue(true);
    }

    @Suppress
    public void testThisFails() throws Exception {
        assertTrue(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
