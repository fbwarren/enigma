package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Frank Warren
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            HashMap<String, Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[numRotors];
        _plugboard = new Permutation("", _alphabet);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** _rotors getter.
     * @return _rotors */
    Rotor[] getRotors() {
        return _rotors;
    }

    /** _plugboard getter.
     * @return _plugboard */
    Permutation getPlugboard() {
        return _plugboard;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String rotors) {
        String[] nameArr = rotors.split("\\s+");
        if (nameArr.length != numRotors()) {
            throw error("Number of rotors to insert != number of slots.");
        }
        boolean movingAdded = false;
        int pawlsAdded = 0;
        for (int i = 0; i < nameArr.length; i += 1) {
            String rotorName = nameArr[i];
            Rotor rotor = _allRotors.get(rotorName);
            if (rotor == null) {
                throw error("Bad rotor name or not enough rotors.");
            }
            if (rotors.indexOf(" " + rotorName + " ")
                    != rotors.lastIndexOf(" " + rotorName + " ")) {
                throw error("Cannot have a rotor in multiple slots.");
            }
            if (i == 0 && !rotor.reflecting()) {
                throw error("First rotor must be a reflector.");
            }
            if (rotor.reflecting() && i > 0) {
                throw error("Cannot have multiple reflectors.");
            }
            if (!rotor.rotates()) {
                if (movingAdded) {
                    throw error("All stationary rotors must "
                            + "be before moving rotors.");
                }
            } else {
                movingAdded = true;
                pawlsAdded += 1;
            }
            _rotors[i] = rotor;
        }
        if (pawlsAdded != numPawls()) {
            throw error("Machine must have the number of moving "
                    + "rotors specified in setting.");
        }
    }
    /** Set my rotors according to SETTING and ringSetting, which must be a
     *  string of numRotors()-1 characters in my alphabet. The first letter
     *  refers to the leftmost rotor setting (not counting the reflector).
     *  @param setting The initial setting of the rotor.
     *  @param ringSetting The internal shift of the permutations. */
    void setRotors(String setting, String ringSetting) {
        if (setting.length() != numRotors() - 1) {
            throw error("Length of settings not"
                    + " equal to number of rotors - 1.");
        }
        for (int i = 0; i < setting.length(); i += 1) {
            if (!_alphabet.contains(setting.charAt(i))) {
                throw error("Initial position of rotors and ring setting must"
                        + " be a character in the alphabet");
            }
            if (ringSetting != null) {
                _rotors[i + 1].setRing(ringSetting.charAt(i));
            }
            _rotors[i + 1].set(setting.charAt(i));
            _rotors[i + 1].set(_rotors[i + 1].permutation().wrap(
                    _rotors[i + 1].setting() - _rotors[i + 1].ringSetting()));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        for (Character key: plugboard.getMap().keySet()) {
            if (plugboard.permute(key) != plugboard.invert(key)
                    || plugboard.getMap().get(key) == key) {
                throw error("Plugboard cycles setting must contain "
                        + "exactly two characters.");
            }
        }
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        for (int i = numRotors() - numPawls(); i < numRotors(); i += 1) {
            if (i == numRotors() - 1 || getRotors()[i + 1].atNotch()) {
                getRotors()[i].advance();
                if (i < numRotors() - 2 && !getRotors()[i + 2].atNotch()) {
                    getRotors()[i + 1].advance();
                }
            }
        }
        c = _plugboard.permute(c);
        for (int i = getRotors().length - 1; i >= 0; i -= 1) {
            c = getRotors()[i].convertForward(c);
        }
        for (int i = 1; i < getRotors().length; i += 1) {
            c = getRotors()[i].convertBackward(c);
        }
        c = _plugboard.permute(c);
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String ret = "";
        char[] arr = msg.toCharArray();
        for (char c: arr) {
            if (_alphabet.contains(c)) {
                ret += _alphabet.toChar(convert(_alphabet.toInt(c)));
            } else if (!Character.isWhitespace(c)) {
                throw error("Can't convert character that's not in alphabet.");
            }
        }
        return ret;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors & number of moving rotors. */
    private int _numRotors, _pawls;

    /** Map of rotor names to Rotor objects. */
    private HashMap<String, Rotor> _allRotors;

    /** Plugboard permutation. */
    private Permutation _plugboard;

    /** The rotors in the slots of this machine instance. */
    private Rotor[] _rotors;
}
