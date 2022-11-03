package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Frank Warren
 */
public class PermutationTest {

    /**
     * Permutation getter.
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */
    Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        return new Permutation(cycles, alphabet);
    }

    /**
     * Alphabet getter.
     * @return an Alphabet with chars as its characters
     * @see Alphabet for description of the Alphabet constructor
     */
    Alphabet getNewAlphabet(String chars) {
        return new Alphabet(chars);
    }

    /**
     * Default alphabet getter.
     * @return a default Alphabet with characters ABCD...Z
     * @see Alphabet for description of the Alphabet constructor
     */
    Alphabet getNewAlphabet() {
        return new Alphabet();
    }

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    @Test
    public void checkBasics() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        Permutation perm1 = getNewPermutation("(ab)", getNewAlphabet("ba"));
        assertEquals(perm.size(), 26);
        assertEquals(perm1.size(), 2);
        assertEquals(perm.size(), perm.alphabet().size());
        assertEquals(perm1.size(), perm1.alphabet().size());
        assertEquals(perm.alphabet(), alpha);
        assertFalse(perm.derangement());
        assertTrue(perm1.derangement());
    }

    @Test
    public void checkCycleParsing() {
        Alphabet alpha = getNewAlphabet("ABC123");
        Permutation perm = getNewPermutation(" (A1) (B2)        (C) ", alpha);
        checkPerm("Cycle parsing", "ABC123", "12CAB3", perm, alpha);
    }

    @Test
    public void checkIdTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }

    @Test
    public void testRotorTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation(NAVALA.get("I"), alpha);
        checkPerm("Rotor I, Setting A", UPPER_STRING,
                NAVALA_MAP.get("I"), perm, alpha);
    }

    @Test
    public void testInvertChar() {
        Permutation p = getNewPermutation("(BACD) (EF)",
                getNewAlphabet("ABCDEF"));
        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));
        assertEquals('F', p.invert('E'));
        assertEquals(1, p.invert(0));
        assertEquals(3, p.invert(1));
        assertEquals(1, p.invert(96));
    }

    @Test
    public void testPermuteChar() {
        Alphabet alpha = getNewAlphabet();
        Permutation p = getNewPermutation(NAVALA.get("I"), alpha);
        assertEquals('E', p.permute('A'));
        assertEquals('B', p.permute('W'));
        assertEquals('G', p.permute('F'));
        assertEquals('S', p.permute('S'));
        assertEquals(4, p.permute(0));
        assertEquals(4, p.permute(52));
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));

        char[] test = new char[]{'a', 'F', ' ', '('};
        for (char c : test) {
            try {
                p.permute(c);
            } catch (EnigmaException name) {
                try {
                    p.invert(c);
                } catch (EnigmaException name1) {
                    System.out.println("Case '" + c + "' threw "
                            + "exception successfully.");
                }
            }
        }
        p.permute('*');
    }

    @Test(expected = EnigmaException.class)
    public void testBadCycles() {
        Alphabet alpha = getNewAlphabet();

        String[] test = new String[]{"B", "(ab)", "(())", "()", "(*)",
            "(AB))   (CD)", "ABC", "(A B)", "(A) (B) (A)"};
        for (String s : test) {
            try {
                getNewPermutation(s, alpha);
            } catch (EnigmaException name) {
                System.out.println("Successful exception for"
                        + " malformed cycle \"" + s + "\"");
            }
        }
        getNewPermutation("((ABC))", alpha);
    }

    @Test(expected = EnigmaException.class)
    public void testBadAlphabet() {
        String[] test = new String[]{" ", "AA", "AB C", "A(", "A*"};
        for (String s: test) {
            Permutation perm = getNewPermutation("", getNewAlphabet(s));
        }
    }
}
