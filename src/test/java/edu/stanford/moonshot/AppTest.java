package edu.stanford.moonshot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    private static final boolean runTests = false;

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        if (runTests) App.runApp();
    }

    public void testNews() {
        if (runTests) News.runApp();
    }
}
