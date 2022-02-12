package ab3.impl.SchachnerIsmailov;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransitionKeyTest {
    @Test
    void testEquals() {
        assertEquals(new TransitionKey(0, 'a'), new TransitionKey(0, 'a'));
    }
}