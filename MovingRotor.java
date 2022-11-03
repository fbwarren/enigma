package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Frank Warren
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    public String toString() {
        return "Moving Rotor " + name();
    }

    @Override
    void advance() {
        set(permutation().wrap((setting() + 1)));
    }

    @Override
    boolean atNotch() {
        char setting = alphabet().toChar(
                permutation().wrap(setting() + ringSetting()));
        return (_notches.indexOf(setting) != -1);
    }

    /** Setter method for _notches.
     *
     * @param notches The notches on the rotor.
     */
    void setNotches(String notches) {
        _notches = notches;
    }

    /** The notches of this movingRotor.
     */
    private String _notches;
}
