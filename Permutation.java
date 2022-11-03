package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Frank Warren
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _map = new HashMap<Character, Character>();
        _reverseMap = new HashMap<Character, Character>();
        parseCycles(cycles);
    }

    /** Checks to make sure that the string containing the cycles is properly
     *  formatted and adds all the cycles to the permutation.
     * @param cycles A string that represents the cycles of this permutation.
     *               parseCycles will check for formatting. */
    private void parseCycles(String cycles) {
        boolean inCycle = false;
        String cycle;
        int i = 0;
        while (i < cycles.length()) {
            cycle = "";
            while (!inCycle && i < cycles.length()) {
                if (cycles.charAt(i) == '(') {
                    inCycle = true;
                    if (cycles.charAt(i + 1) == ')') {
                        throw error("Improper cycles formatting "
                                                + "(empty cycle () found)");
                    }
                } else if (!Character.isWhitespace(cycles.charAt(i))) {
                    throw error("Improper cycles formatting "
                            + "(character outside of parentheses)");
                }
                i += 1;
            }
            while (inCycle && i < cycles.length()) {
                char c = cycles.charAt(i);
                if (c == ')') {
                    inCycle = false;
                } else {
                    if (!alphabet().contains(c)) {
                        throw error("Improper cycles formatting "
                                + "(character not in alphabet)");
                    } else if (cycles.lastIndexOf(c) != i) {
                        throw error("Improper cycles formatting "
                                + "(repeated character)");
                    }
                    cycle += c;
                }
                i += 1;
            }
            if (!cycle.equals("")) {
                addCycle(cycle);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        int i = 0;
        while (i < cycle.length() - 1) {
            char from = cycle.charAt(i), to = cycle.charAt(i + 1);
            _map.put(from, to);
            _reverseMap.put(to, from);
            i += 1;
        }
        _map.put(cycle.charAt(cycle.length() - 1), cycle.charAt(0));
        _reverseMap.put(cycle.charAt(0), cycle.charAt(cycle.length() - 1));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        char in = alphabet().toChar(p);
        return alphabet().toInt(permute(in));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        char in = alphabet().toChar(c);
        return alphabet().toInt(invert(in));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!alphabet().contains(p)) {
            throw error("Cannot permute character not in alphabet.");
        }
        return _map.getOrDefault(p, p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!alphabet().contains(c)) {
            throw error("Cannot permute character not in alphabet.");
        }
        return _reverseMap.getOrDefault(c, c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return (_map.size() == alphabet().size());
    }

    /** _map getter.
     * @return _map*/
    HashMap<Character, Character> getMap() {
        return _map;
    }

    /** _reverseMap getter.
     * @return _reverseMap */
    HashMap<Character, Character> getReverseMap() {
        return _reverseMap;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** A map of characters and their output, or vice versa. **/
    private HashMap<Character, Character> _map, _reverseMap;
}
