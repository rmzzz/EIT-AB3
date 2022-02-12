package ab3;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

import ab3.TuringMachine.Movement;
import ab3.TuringMachine.TapeContent;

import ab3.impl.SchachnerIsmailov.Ab3Impl;

public class Ab3Tests {
    
    private static Ab3 factory = new Ab3Impl();

    private static boolean basicTestsOK = true;
    private static int punkte = 0;

    private static TuringMachine getMachineBasicLeftRight() {

	TuringMachine tm = factory.getEmptyTM();
	tm.setAlphabet(new HashSet<>(Arrays.asList('a')));
	tm.setNumberOfTapes(1);
	tm.setNumberOfStates(3);
	tm.setHaltingState(0);
	tm.setInitialState(1);

	tm.addTransition(1, 'a', 2, 'a', Movement.Right);
	tm.addTransition(2, null, 0, 'a', Movement.Left);

	return tm;
    }

    private static TuringMachine getMachineOneTape() {

	TuringMachine tm = factory.getEmptyTM();
	tm.setAlphabet(new HashSet<>(Arrays.asList('a')));
	tm.setNumberOfTapes(1);
	tm.setNumberOfStates(4);
	tm.setHaltingState(0);
	tm.setInitialState(3);

	tm.addTransition(1, 'a', 2, null, Movement.Stay);
	tm.addTransition(1, null, 0, null, Movement.Stay);
	tm.addTransition(2, null, 1, null, Movement.Right);
	tm.addTransition(3, 'a', 2, null, Movement.Stay);
	tm.addTransition(3, null, 2, null, Movement.Stay);

	return tm;
    }

    private static TuringMachine getMachineTwoTapes() {

	TuringMachine tm = factory.getEmptyTM();
	tm.setAlphabet(new HashSet<>(Arrays.asList('0', '1')));
	tm.setNumberOfTapes(2);
	tm.setNumberOfStates(6);
	tm.setHaltingState(0);
	tm.setInitialState(1);

	// gehe zum Ende des Input-Bandes
	tm.addTransition(1, new Character[] { '0', null }, 1, new Character[] { '0', null }, new Movement[] { Movement.Right, Movement.Stay });
	tm.addTransition(1, new Character[] { '1', null }, 1, new Character[] { '1', null }, new Movement[] { Movement.Right, Movement.Stay });
	tm.addTransition(1, new Character[] { null, null }, 2, new Character[] { null, null }, new Movement[] { Movement.Left, Movement.Stay });

	// kopiere Input-Band rückwärts auf das zweite Band
	tm.addTransition(2, new Character[] { '0', null }, 2, new Character[] { '0', '0' }, new Movement[] { Movement.Left, Movement.Right });
	tm.addTransition(2, new Character[] { '1', null }, 2, new Character[] { '1', '1' }, new Movement[] { Movement.Left, Movement.Right });
	tm.addTransition(2, new Character[] { null, null }, 3, new Character[] { null, null }, new Movement[] { Movement.Stay, Movement.Left });

	// gehe zum Anfang des zweiten Bandes
	tm.addTransition(3, new Character[] { null, '0' }, 3, new Character[] { null, '0' }, new Movement[] { Movement.Stay, Movement.Left });
	tm.addTransition(3, new Character[] { null, '1' }, 3, new Character[] { null, '1' }, new Movement[] { Movement.Stay, Movement.Left });
	tm.addTransition(3, new Character[] { null, null }, 4, new Character[] { null, null }, new Movement[] { Movement.Right, Movement.Right });

	// XOR zwischen Band 1 und Band 2, mit Output auf Band 1
	tm.addTransition(4, new Character[] { '0', '0' }, 4, new Character[] { '0', '0' }, new Movement[] { Movement.Right, Movement.Right});
	tm.addTransition(4, new Character[] { '1', '0' }, 4, new Character[] { '1', '0' }, new Movement[] { Movement.Right, Movement.Right});
	tm.addTransition(4, new Character[] { '0', '1' }, 4, new Character[] { '1', '1' }, new Movement[] { Movement.Right, Movement.Right});
	tm.addTransition(4, new Character[] { '1', '1' }, 4, new Character[] { '0', '1' }, new Movement[] { Movement.Right, Movement.Right});
	tm.addTransition(4, new Character[] { null, null }, 5, new Character[] { null, null }, new Movement[] { Movement.Left, Movement.Left});

	// gehe zurück zu den Bandanfängen und halte
	tm.addTransition(5, new Character[] { '0', '0' }, 5, new Character[] { '0', '0' }, new Movement[] { Movement.Left, Movement.Left});
	tm.addTransition(5, new Character[] { '1', '0' }, 5, new Character[] { '1', '0' }, new Movement[] { Movement.Left, Movement.Left});
	tm.addTransition(5, new Character[] { '0', '1' }, 5, new Character[] { '0', '1' }, new Movement[] { Movement.Left, Movement.Left});
	tm.addTransition(5, new Character[] { '1', '1' }, 5, new Character[] { '1', '1' }, new Movement[] { Movement.Left, Movement.Left});
	tm.addTransition(5, new Character[] { null, null }, 0, new Character[] { null, null }, new Movement[] { Movement.Stay, Movement.Stay});

	return tm;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Beginn der Tests
    ///////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testBasicInitialState() {

	boolean before = basicTestsOK;
	basicTestsOK = false;
	
	TuringMachine tm = getMachineBasicLeftRight();

	// Maschine hat 1 Band
	assertEquals(1, tm.getNumberOfTapes());

	// Input festlegen
	tm.reset();
	tm.setInput("a");

	// Prüfe Konfiguration
	assertEquals(1, tm.getCurrentState());
	assertFalse(tm.isInHaltingState());
	assertFalse(tm.isInErrorState());
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals('a', tape.getBelowHead());
	assertArrayEquals(new Character[0], tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	basicTestsOK = before;
    }
    
    @Test
    public void testBasicOneStep() {

	boolean before = basicTestsOK;
	basicTestsOK = false;
	
	TuringMachine tm = getMachineBasicLeftRight();

	// Maschine hat 1 Band
	assertEquals(1, tm.getNumberOfTapes());

	// Input festlegen
	tm.reset();
	tm.setInput("a");

	// einen Schritt ausführen
	tm.doNextStep();

	// Prüfe Konfiguration
	assertEquals(2, tm.getCurrentState());
	assertFalse(tm.isInHaltingState());
	assertFalse(tm.isInErrorState());
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[] { 'a' }, tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[0], tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	basicTestsOK = before;
    }
 
    @Test
    public void testBasicTwoStep() {

	boolean before = basicTestsOK;
	basicTestsOK = false;
	
	TuringMachine tm = getMachineBasicLeftRight();

	// Maschine hat 1 Band
	assertEquals(1, tm.getNumberOfTapes());

	// Input festlegen
	tm.reset();
	tm.setInput("a");

	// zwei Schritte ausführen
	tm.doNextStep();
	tm.doNextStep();

	// Prüfe Konfiguration
	assertEquals(0, tm.getCurrentState());
	assertTrue(tm.isInHaltingState());
	assertFalse(tm.isInErrorState());
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals('a', tape.getBelowHead());
	assertArrayEquals(new Character[] { 'a' }, tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	basicTestsOK = before;
    }
 
    @Test
    public void testBasicFailure() {

	boolean before = basicTestsOK;
	basicTestsOK = false;
	
	TuringMachine tm = getMachineBasicLeftRight();

	// Maschine hat 1 Band
	assertEquals(1, tm.getNumberOfTapes());

	// Input festlegen
	tm.reset();
	tm.setInput("aa");

	// zwei Schritte ausführen
	tm.doNextStep();
	tm.doNextStep();

	// Prüfe Konfiguration
	assertTrue(tm.isInErrorState());
	assertEquals(null, tm.getTapeContent(0));
	assertEquals(null, tm.getTapeContents());

	assertThrows(IllegalStateException.class, () -> {
	    tm.doNextStep();
	});

	basicTestsOK = before;
    }


    @Test
    public void testAlphabet() {

	boolean before = basicTestsOK;
	basicTestsOK = false;

	HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd'));

	TuringMachine tm = factory.getEmptyTM();
	tm.setAlphabet(alphabet);

	assertEquals(alphabet, tm.getAlphabet());

	basicTestsOK = before;
    }

    @Test
    public void testStateErrors() {
	assertThrows(IllegalArgumentException.class, () -> {
	    TuringMachine tm = factory.getEmptyTM();
	    tm.setNumberOfStates(2);
	    tm.setHaltingState(2);

            basicTestsOK = false;
        });

	assertThrows(IllegalArgumentException.class, () -> {
	    TuringMachine tm = factory.getEmptyTM();
	    tm.setNumberOfStates(0);

            basicTestsOK = false;
        });
    }

    @Test
    public void testTapeErrors() {
	assertThrows(IllegalArgumentException.class, () -> {
	    TuringMachine tm = factory.getEmptyTM();
	    tm.setNumberOfTapes(0);

            basicTestsOK = false;
        });
    }

    @Test
    public void testTransitionErrors() {
	assertThrows(IllegalArgumentException.class, () -> {
	    TuringMachine tm = getMachineBasicLeftRight();
	    tm.addTransition(0, 'a', 0, 'a', Movement.Stay);

            basicTestsOK = false;
        });

	assertThrows(IllegalArgumentException.class, () -> {
	    TuringMachine tm = getMachineBasicLeftRight();
	    tm.addTransition(3, 'a', 3, 'a', Movement.Stay);

            basicTestsOK = false;
        });
    }

    @Test
    public void testMachineOneTapeA() {
	TuringMachine tm = getMachineOneTape();

	// Maschine hat 1 Band
	assertEquals(1, tm.getNumberOfTapes());

	// Lasse die Maschine bis zum Haltezustand laufen
	tm.reset();
	tm.setInput("aaaaa");
	while (!tm.isInHaltingState()) {
	    tm.doNextStep();
	}

	// Prüfe den Bandinhalt
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[0], tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	punkte += 2;
    }

    @Test
    public void testMachineOneTapeB() {
	TuringMachine tm = getMachineOneTape();

	// Maschine hat 1 Band
	assertEquals(1, tm.getNumberOfTapes());

	// Lasse die Maschine bis zum Haltezustand laufen
	tm.reset();
	tm.setInput("aaaaaaaaaaaaaaaaaaaaaaaa");
	while (!tm.isInHaltingState()) {
	    tm.doNextStep();
	}

	// Prüfe den Bandinhalt
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[0], tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	punkte += 2;
    }

    @Test
    public void testMachineOneTapeC() {
	TuringMachine tm = getMachineOneTape();

	// Maschine hat 1 Band
	assertEquals(1, tm.getNumberOfTapes());

	// Lasse die Maschine bis zum Haltezustand laufen
	tm.reset();
	tm.setInput("");
	while (!tm.isInHaltingState()) {
	    tm.doNextStep();
	}

	// Prüfe den Bandinhalt
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[0], tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	punkte += 2;
    }

    @Test
    public void testMachineTwoTapesA() {
	TuringMachine tm = getMachineTwoTapes();

	// Maschine hat 2 Bänder
	assertEquals(2, tm.getNumberOfTapes());

	// Lasse die Maschine bis zum Haltezustand laufen
	tm.reset();
	tm.setInput("1101");
	while (!tm.isInHaltingState()) {
	    tm.doNextStep();
	}

	// Prüfe den Bandinhalt
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[] {'0', '1', '1', '0'}, tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	tape = tm.getTapeContent(1);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[] {'1', '0', '1', '1'}, tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(1));

	punkte += 2;
    }

    @Test
    public void testMachineTwoTapesB() {
	TuringMachine tm = getMachineTwoTapes();

	// Maschine hat 2 Bänder
	assertEquals(2, tm.getNumberOfTapes());

	// Lasse die Maschine bis zum Haltezustand laufen
	tm.reset();
	tm.setInput("11011");
	while (!tm.isInHaltingState()) {
	    tm.doNextStep();
	}

	// Prüfe den Bandinhalt
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[] {'0', '0', '0', '0', '0'}, tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	tape = tm.getTapeContent(1);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[] {'1', '1', '0', '1', '1'}, tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(1));

	punkte += 2;
    }

    @Test
    public void testMachineTwoTapesC() {
	TuringMachine tm = getMachineTwoTapes();

	// Maschine hat 2 Bänder
	assertEquals(2, tm.getNumberOfTapes());

	// Lasse die Maschine bis zum Haltezustand laufen
	tm.reset();
	tm.setInput("");
	while (!tm.isInHaltingState()) {
	    tm.doNextStep();
	}

	// Prüfe den Bandinhalt
	TapeContent tape = tm.getTapeContent(0);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[0], tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(0));

	tape = tm.getTapeContent(1);
	assertArrayEquals(new Character[0], tape.getLeftOfHead());
	assertEquals(null, tape.getBelowHead());
	assertArrayEquals(new Character[0], tape.getRightOfHead());
	assertEquals(tape, tm.getTapeContents().get(1));

	punkte += 2;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Ende der Tests
    ///////////////////////////////////////////////////////////////////////////

    @AfterAll
    public static void printPoints() {

	if(basicTestsOK) punkte += 1;
	else punkte /= 2;

	System.out.println("Gesamtpunkte: " + punkte);
    }

}
