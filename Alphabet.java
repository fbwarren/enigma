package enigma;
import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Frank Warren
 */
class Alphabet {
    /** The alphabet string used in Alphabet construction. */
    private String _alphabet;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        checkAlphabet(chars);
        _alphabet = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Checks the string used in Alphabet construction for validity.
     * @param chars The string of characters used to build the Alphabet. */
    private void checkAlphabet(String chars) {
        for (int i = 0; i < chars.length(); i += 1) {
            char ch = chars.charAt(i);
            if (chars.indexOf(ch, i + 1) != -1) {
                throw error("Duplicate character in entry.");
            }
            if (Character.isWhitespace(ch) || ch == '(' || ch == ')'
                                            || ch == '*') {
                throw error("Invalid character in alphabet.");
            }
        }
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _alphabet.indexOf(ch) != -1;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabet.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return _alphabet.indexOf(ch);
    }

    /** _alphabet getter.
     * @return _alphabet */
    String getAlphabet() {
        return _alphabet;
    }
}
