package ab3.impl.SchachnerIsmailov;

import java.util.Arrays;
import java.util.Objects;
import static ab3.impl.SchachnerIsmailov.Constants.*;

public record TransitionKey(int fromState, char... readTape) {
    public boolean matches(int state, char... read) {
        if (state != this.fromState) {
            return false;
        }
        for (int i = 0; i < readTape.length; i++) {
            if (readTape[i] != read[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransitionKey that = (TransitionKey) o;
        return fromState == that.fromState && Arrays.equals(readTape, that.readTape);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fromState);
        result = 31 * result + Arrays.hashCode(readTape);
        return result;
    }

    @Override
    public String toString() {
        return "(" + fromState + ", " + new String(readTape) + ')';
    }
}
