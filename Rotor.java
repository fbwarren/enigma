package enigma;
import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Frank Warren
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _ringSetting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Ringsetting getter.
     * @return ringsetting */
    int ringSetting() {
        return _ringSetting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        set(alphabet().toInt(cposn));
    }

    /** Set setting() to POSN.  */
    void setRing(int posn) {
        _ringSetting = posn;
    }

    /** Set setting() to character CPOSN. */
    void setRing(char cposn) {
        setRing(alphabet().toInt(cposn));
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int beforeSetting = permutation().permute(p + setting());
        return permutation().wrap(beforeSetting - setting());
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int beforeSetting = permutation().invert(e + setting());
        return permutation().wrap(beforeSetting - setting());
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() { }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Current position of the rotor. **/
    private int _setting;

    /** The ring setting of the rotor. **/
    private int _ringSetting;

}