package ab3;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Interface zur Implementierung einer Turingmaschine (Mehrband, einseitig beschränkt)
 *
 */
public interface TuringMachine {
    /**
     * Setzt die Turingmaschine zurück. Dabei wird der Bandinhalt aller Bänder
     * gelöscht, der derzeitige Zustand wird auf den Ausgangszustand
     * zurückgesetzt, sowie der Schreib-/Lesekopf auf das erste Zeichen des
     * Bandes gesetzt. Nach einem erneuten Aufruf von setInput(...) kann also
     * eine neue Berechnung beginnen.
     */
    public void reset();

    /**
     * Liefert den aktuellen Zustand der Maschine
     *
     * @return aktueller Zustand der Maschine
     *
     * @throws IllegalStateException
     *             Wird geworfen, wenn die Maschine im Fehlerzustand ist.
     */
    public int getCurrentState() throws IllegalStateException;

    /**
     * Gibt das Input-Alphabet der Turingmaschine. Das Bandalphabet
     * unterscheidet sich vom Input-Alphabet nur durch das zusätzlich
     * vorhandene Leerzeichen (das durch "null" repräsentiert wird).
     *
     * @param alphabet Menge der Symbole
     * @throws IllegalArgumentException wenn die Menge den Wert "null" beinhaltet
     */
    public void setAlphabet(Set<Character> alphabet) throws IllegalArgumentException;

    /**
     * Liefert das Input-Alphabet der Maschine.
     *
     * @return Menge der Symbole
     */
    public Set<Character> getAlphabet();

    /**
     * Fügt einen Übergang bei einer TM mit nur einem Band hinzu. 
     *
     * @param fromState Der Ausgangszustand, in dem der Übergang anwendbar ist
     * @param read das zu lesenden Zeichen am (einzigen) Band
     * @param toState Der Folgezustand, wenn der Übergang ausgeführt wurde
     * @param write das zu schreibenden Zeichen am (einzigen) Band
     * @param move die Kopf-Bewegungen am (einzigen) Band
     *
     * @throws IllegalArgumentException
     *             Wird geworfen, wenn der Ausgangszustand der Haltezustand
     *             ist; wenn ein Übergang nicht deterministisch ist (bzgl.
     *             fromState, read); wenn ein Symbol nicht Teil des
     *             Bandalphabets ist; wenn ein Zustand nicht existiert; oder
     *             wenn die TM mehr als ein Band hat.
     */
    public void addTransition(int fromState,
		              Character read,
			      int toState,
		              Character write,
			      Movement move) throws IllegalArgumentException;

    /**
     * Fügt einen Übergang hinzu. Dabei gibt das Array read an der Stelle i an,
     * was beim Übergang vom Band i jeweils gelesen werden muss (also unter dem
     * Schreib-/Lesekopf steht; 0-indexiert).  Das Array write ist analog
     * aufgebaut, und gibt an, welches Symbol auf jedes Band jeweils
     * geschrieben wird. Das Array move ist ebenfalls analog aufgebaut, und
     * beschreibt die Bewegungen der Schreib-/Leseköpfe der jeweiligen Bänder.
     *
     * @param fromState Der Ausgangszustand, in dem der Übergang anwendbar ist
     * @param read Array mit den zu lesenden Zeichen pro Band
     * @param toState Der Folgezustand, wenn der Übergang ausgeführt wurde
     * @param write Array mit den zu schreibenden Zeichen pro Band
     * @param move Array mit den Kopf-Bewegungen pro Band
     *
     * @throws IllegalArgumentException
     *             Wird geworfen, wenn der Ausgangszustand der Haltezustand
     *             ist; wenn ein Übergang nicht deterministisch ist (bzgl.
     *             fromState, read); wenn ein Symbol nicht Teil des
     *             Bandalphabets ist; oder wenn ein Zustand nicht existiert.
     */
    public void addTransition(int fromState,
		              Character[] read,
			      int toState,
		              Character[] write,
			      Movement[] move) throws IllegalArgumentException;

    /**
     * Liefert die Anzahl der Zustände.
     */
    public int getNumberOfStates();

    /**
     * Liefert die Anzahl an Bändern zurück.
     *
     * @return Anzahl der Bänder
     */
    public int getNumberOfTapes();

    /**
     * Setzt die Anzahl der Zustände der Maschine. Die Machine enthält nur
     * "normale" Zustände, sowie den akzeptierenden Haltezustand. Der
     * verwerfende Haltezustand wird nie explizit realisiert.
     *
     * @param numStates Anzahl der Zustände (inklusive akzeptierendem Haltezustand)
     */
    public void setNumberOfStates(int numStates) throws IllegalArgumentException;

    /**
     * Gibt die Anzahl an zu verwendenen Schreib/Lese-Bändern an. Das erste
     * Band (Band 0) ist immer das Input-Band.
     *
     * @param numTapes Anzahl an Schreib/Lese-Bändern
     * @throws IllegalArgumentException wenn numTapes < 1 ist
     */
    public void setNumberOfTapes(int numTapes) throws IllegalArgumentException;

    /**
     * Setzt die Nummer des (akzeptierenden) Haltezustandes
     * @param state Nummer des Haltezustandes
     * @throws IllegalArgumentException falls state nicht möglich ist
     */
    public void setHaltingState(int haltingState) throws IllegalArgumentException;

    /**
     * Setzt den initialen Zustand der Maschine.
     *
     * @param state Startzustand
     * @throws IllegalArgumentException falls state nicht möglich ist
     */
    public void setInitialState(int initialState) throws IllegalArgumentException;

    /**
     * Setzt den initialen Inhalt des Input-Bandes und setzt den
     * Schreib-/Lesekopf auf das erste Zeichen des Inhaltes. "abc" liefert
     * somit den Inhalt "...abc..." wobei der Schreib-/Lesekopf über dem 'a'
     * steht. Rechts und links von "abc" befinden sich eine unendliche, aber
     * natürlich nicht explizit abgespeicherte, Reihe an Leerzeichen.
     *
     * @param content Der Bandinhalt des Input-Bandes als String
     */
    public void setInput(String content);

    /** Führt einen Ableitungsschritt der Turingmaschine aus. Ist kein
     * passender Übergang in der Übergangsfunktion vorhanden, so befindet sich
     * die Maschine nach dem Schritt im Fehlerzustand (= nichtakzeptierender
     * Haltezustand, der aber nicht als tatsächlicher Zustand in der Maschine
     * vorkommt).
     *
     * @throws IllegalStateException
     *             Wird geworfen, wenn die Maschine bereits im Haltezustand
     *             oder Fehlerzustand ist.
     */
    public void doNextStep() throws IllegalStateException;

    /**
     * Liefert true, wenn sich die Maschine im Haltezustand befindet; sonst false
     */
    public boolean isInHaltingState();

    /**
     * Liefert true, wenn die Maschine im Fehlerzustand ist; sonst false
     */
    public boolean isInErrorState();

    /**
     * Liefert die Konfiguration der Maschine für jedes Band. Ist
     * isInErrorState() == true, wird null zurück geliefert. Führende und
     * nachfolgende Leerzeichen sollen entfernt (bzw. optimalerweise während
     * der Abarbeitung garnicht gespeichert) werden.
     *
     * @return Konfiguration der Maschine.
     */
    public List<TapeContent> getTapeContents();

    /**
     * Liefert die Konfiguration der Maschine für das angegebene Band. Ist
     * isInErrorState() == true, wird null zurück geliefert. Führende und
     * nachfolgende Leerzeichen sollen entfernt (bzw. optimalerweise während
     * der Abarbeitung garnicht gespeichert) werden.
     *
     * @param tape das Band, von dem die Konfiguration zurückgegeben werden soll
     * @return Konfiguration des angegebenen Bandes
     */
    public TapeContent getTapeContent(int tape);

    /**
     * Bewegerichtungen des Schreib/Lese-Kopfes
     */
    public enum Movement {
	/**
	 * Kopf bewegt sich nach links
	 */
	Left,

	/**
	 * Kopf bewegt sich nach rechts
	 **/
	Right,

	/**
	 * Kopf bewegt sich nicht
	 */
	Stay
    }

    /**
     * Konfiguration eines Bandes samt Schreib-/Lesekopf der TM
     */
    public class TapeContent {
	/**
	 * Alle Zeichen auf dem Band vom Beginn des Bandes bis zur Stelle links vom Kopf
	 * (darf leer sein).
	 */
	private Character[] leftOfHead;
	/**
	 * Das Zeichen, das sich aktuell unter dem Kopf befindet.
	 */
	private Character belowHead;
	/**
	 * Alle Zeichen von der Stelle rechts vom Kopf bis zum letzten von '#'
	 * verschiedenen Zeichen (darf leer sein).
	 */
	private Character[] rightOfHead;

	public Character[] getLeftOfHead() {
	    return leftOfHead;
	}

	public Character getBelowHead() {
	    return belowHead;
	}

	public Character[] getRightOfHead() {
	    return rightOfHead;
	}

	public TapeContent(Character[] leftOfHead, Character belowHead, Character[] rightOfHead) {
	    super();
	    this.leftOfHead = leftOfHead;
	    this.belowHead = belowHead;
	    this.rightOfHead = rightOfHead;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof TapeContent) {
		TapeContent tc = (TapeContent) obj;
		if (belowHead != tc.belowHead) return false;
		if (!Arrays.equals(leftOfHead, tc.leftOfHead)) return false;
		if (!Arrays.equals(rightOfHead, tc.rightOfHead)) return false;
		return true;
	    } else {
		return false;
	    }
	}

	@Override
	public String toString() {
	    return "BEGIN:>" + printArray(leftOfHead) + "'" + belowHead + "'" + printArray(rightOfHead) + "<:END";
	}

	private String printArray(Character[] arr)
	{
	    String s = "";
	    for(Character c:arr) {
		if(c != null) s+=c;
		else s+=' ';
	    }
	    return s;
	}
    }
}
