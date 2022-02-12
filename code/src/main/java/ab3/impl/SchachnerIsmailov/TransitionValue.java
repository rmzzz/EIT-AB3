package ab3.impl.SchachnerIsmailov;

import ab3.TuringMachine;

import java.util.Arrays;
import java.util.stream.Collectors;

public record TransitionValue(int toState, char[] writeTapes, TuringMachine.Movement[] moveTapes) {
    @Override
    public String toString() {
        return "(" + toState
                + ", " + new String(writeTapes)
                + ", " + Arrays.stream(moveTapes)
                .map(m -> switch (m) {
                    case Left -> "←";
                    case Right -> "→";
                    case Stay -> "•";
                }).collect(Collectors.joining())
                + ')';
    }
}
