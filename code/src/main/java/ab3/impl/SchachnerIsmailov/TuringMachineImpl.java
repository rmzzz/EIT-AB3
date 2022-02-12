package ab3.impl.SchachnerIsmailov;

import ab3.TuringMachine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static ab3.impl.SchachnerIsmailov.Constants.*;

public class TuringMachineImpl implements TuringMachine {

    private int currentState;
    private Set<Character> alphabet;
    private int numberOfStates;
    private int numberOfTapes;
    private int initialState;
    private int haltingState;
    private StringBuilder[] tapes;
    private int[] heads;
    private LinkedHashMap<TransitionKey, TransitionValue> transitions = new LinkedHashMap<>();

    @Override
    public void reset() {
        setNumberOfTapes(numberOfTapes);
        currentState = initialState;
    }

    @Override
    public int getCurrentState() throws IllegalStateException {
        return currentState;
    }

    @Override
    public void setAlphabet(Set<Character> alphabet) throws IllegalArgumentException {
        this.alphabet = alphabet;
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public void addTransition(int fromState, Character read, int toState, Character write, Movement move) throws IllegalArgumentException {
        addTransition(fromState, new Character[]{read}, toState, new Character[]{write}, new Movement[]{move});
    }

    @Override
    public void addTransition(int fromState, Character[] read, int toState, Character[] write, Movement[] move) throws IllegalArgumentException {
        if (fromState == haltingState || fromState >= numberOfStates || toState >= numberOfStates
                || read.length != numberOfTapes || write.length != numberOfTapes || move.length != numberOfTapes) {
            // Wird geworfen, wenn der Ausgangszustand der Haltezustand ist;
            // wenn ein Übergang nicht deterministisch ist (bzgl. fromState, read);
            // wenn ein Symbol nicht Teil des Bandalphabets ist;
            // oder wenn ein Zustand nicht existiert.
            throw new IllegalArgumentException();
        }
        checkAlphabet(read);
        checkAlphabet(write);
        var key = new TransitionKey(fromState, wrapSpace(read));
        transitions.put(key, new TransitionValue(toState, wrapSpace(write), move));
    }

    char[] wrapSpace(Character[] src) {
        char[] chars = new char[src.length];
        for (int i = 0; i < src.length; i++) {
            chars[i] = src[i] != null ? src[i] : SPACE;
        }
        return chars;
    }
    Character unwrapSpace(char src) {
        return src == SPACE ? null :src;
    }

    void checkAlphabet(Character... symbols) throws IllegalArgumentException {
        for (Character c : symbols) {
            if (c != null && !alphabet.contains(c)) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public int getNumberOfStates() {
        return numberOfStates;
    }

    @Override
    public int getNumberOfTapes() {
        return numberOfTapes;
    }

    @Override
    public void setNumberOfStates(int numStates) throws IllegalArgumentException {
        if (numStates < 2)
            throw new IllegalArgumentException();
        this.numberOfStates = numStates;
    }

    @Override
    public void setNumberOfTapes(int numTapes) throws IllegalArgumentException {
        if (numTapes < 1)
            throw new IllegalArgumentException();
        this.numberOfTapes = numTapes;
        this.tapes = IntStream.range(0, numTapes)
                .mapToObj(i -> new StringBuilder().append(SPACE))
                .toArray(StringBuilder[]::new);
        this.heads = new int[numTapes];
    }

    @Override
    public void setHaltingState(int haltingState) throws IllegalArgumentException {
        if(haltingState >= numberOfStates)
            throw new IllegalArgumentException();
        this.haltingState = haltingState;
    }

    @Override
    public void setInitialState(int initialState) throws IllegalArgumentException {
        this.initialState = initialState;
        currentState = initialState;
    }

    @Override
    public void setInput(String content) {
        this.tapes[0] = new StringBuilder(content).append(SPACE);
        this.heads[0] = 0;
    }

    Character[] str2TapeContent(CharSequence content) {
        return content.chars().mapToObj(c -> unwrapSpace((char)c)).toArray(Character[]::new);
    }

    @Override
    public void doNextStep() throws IllegalStateException {
        if(isInHaltingState() || isInErrorState())
            throw new IllegalStateException();

        char[] read = new char[numberOfTapes];
        for(int i = 0; i < numberOfTapes; i++) {
            read[i] = tapes[i].charAt(heads[i]);
        }
        TransitionKey tKey = new TransitionKey(currentState, read);
        TransitionValue tValue = transitions.get(tKey);
        if (tValue == null) {
            currentState = numberOfStates; ///< error state
        } else {
            currentState = tValue.toState();

            for (int i = 0; i < numberOfTapes; i++) {
                var write = tValue.writeTapes()[i];
                var move = tValue.moveTapes()[i];
                StringBuilder tape = tapes[i];
                int head = heads[i];
                tape.setCharAt(head, write);
                switch (move) {
                    case Left:
                        if (head == 0) {
                            tape.insert(0, SPACE);
                        } else {
                            heads[i]--;
                        }
                        break;
                    case Right:
                        if (head == tape.length() - 1) {
                            tape.append(SPACE);
                        }
                        heads[i]++;
                        break;
                    case Stay:
                        // nothing to do
                        break;
                }
            }
        }
    }

    @Override
    public boolean isInHaltingState() {
        return currentState == haltingState;
    }

    @Override
    public boolean isInErrorState() {
        return currentState >= numberOfStates;
    }

    @Override
    public List<TapeContent> getTapeContents() {
        if(isInErrorState())
            return null;
        return IntStream.range(0, numberOfTapes)
                .mapToObj(this::getTapeContent)
                .collect(Collectors.toList());
    }

    @Override
    public TapeContent getTapeContent(int tape) {
        if (isInErrorState())
            return null;
        StringBuilder t = tapes[tape];
        int h = heads[tape];
        return new TapeContent(str2TapeContent(h > 0 ? t.substring(0, h).stripLeading() : ""),
                unwrapSpace(t.charAt(h)),
                str2TapeContent(t.substring(h + 1).stripTrailing()));
    }
}
