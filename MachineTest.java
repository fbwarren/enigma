package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Frank Warren
 */
public class MachineTest {

    private Rotor rotor;
    private String alpha = UPPER_STRING;
    private Alphabet alphabet = new Alphabet();
    private HashMap<String, Rotor> rotorMap = new HashMap<String, Rotor>();

    /**
     * Sets rotorMap to the rotors given in
     * NAVALA in testutils.
     */
    private void standardRotorSet() {
        for (String name : NAVALA.keySet()) {
            Permutation perm = new Permutation(NAVALA.get(name), UPPER);
            if (name.equals("B") || name.equals("C")) {
                rotor = new Reflector(name, perm);
            } else if (name.equals("Beta") || name.equals("Gamma")) {
                rotor = new FixedRotor(name, perm);
            } else {
                rotor = new MovingRotor(name, perm, "");
            }
            rotorMap.put(rotor.name(), rotor);
        }
    }

    /**
     * Verify the machine constructor works
     */
    @Test
    public void constructorTest() {
        standardRotorSet();
        Machine machine = new Machine(alphabet, 5, 4, rotorMap);
        assertEquals(5, machine.numRotors());
        assertEquals(4, machine.numPawls());
        assertEquals(machine.numRotors(), machine.getRotors().length);
    }

    @Test
    public void insertRotorsTest() {
        standardRotorSet();
        Machine machine = new Machine(alphabet, 5, 4, rotorMap);
        machine.insertRotors("B I II III IV");
        assertTrue(machine.getRotors()[0].reflecting());
        assertEquals("I", machine.getRotors()[1].name());
    }

    @Test
    public void setRotorsTest() {
        standardRotorSet();
        Machine machine = new Machine(alphabet, 5, 4, rotorMap);
        machine.insertRotors("B I II III IV");
        machine.setRotors("ABCD", null);
        assertEquals('D', alphabet.toChar(machine.getRotors()[4].setting()));
    }

    @Test
    public void setPlugboardTest() {
        standardRotorSet();
        Machine machine = new Machine(alphabet, 5, 4, rotorMap);
        machine.setPlugboard(new Permutation("(AB) (CD) (FG)", alphabet));
        assertEquals('D', (char) machine.getPlugboard().getMap().get('C'));
    }
}
