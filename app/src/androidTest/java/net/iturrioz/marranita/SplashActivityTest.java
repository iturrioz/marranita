package net.iturrioz.marranita;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;

import net.iturrioz.marranita.SplashActivity;

public class SplashActivityTest extends ActivityInstrumentationTestCase2<SplashActivity> {

    public SplashActivityTest() {
        super(SplashActivity.class);
    }

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
