package edu.oregonstate.mist.classsearchapi

import edu.oregonstate.mist.classsearchapi.core.Sample
import org.junit.Test
import static org.junit.Assert.*

class SampleTest {
    @Test
    public void testSample() {
        assertTrue(new Sample().message == 'hello world')
    }
}
