package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Frank Warren
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            System.out.println(new File(name).getAbsolutePath());
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        String plugboard, rotors;
        while (_input.hasNextLine()) {
            if (_input.findInLine("\\*") != null) {
                rotors = "";
                try {
                    for (int i = 0; i < machine.numRotors(); i++) {
                        rotors += _input.next() + " ";
                    }
                } catch (NullPointerException e) {
                    throw error("Not enough rotors in config line.");
                }
                String initialPos = _input.next();
                String ringSetting = null;
                if (_input.hasNext(" [^\\s\\(]{"
                        + (machine.numRotors() - 1) + "}[\\s\\n\\r]?")) {
                    ringSetting = _input.next("[^\\s\\(]{"
                        + (machine.numRotors() - 1) + "}[\\s\\n\\r]?");
                }

                plugboard = _input.findInLine("(\\(..\\)\\s*)+");
                if (_input.hasNextLine()) {
                    _input.nextLine();
                }
                setUp(machine, rotors, initialPos, ringSetting, plugboard);
            } else {
                String message = _input.nextLine();
                try {
                    String output = machine.convert(message);
                    printMessageLine(output);
                } catch (NullPointerException e) {
                    throw error("No configuration line found.");
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            HashMap<String, Rotor> allRotors = new HashMap<String, Rotor>();
            while (_config.hasNext()) {
                String name = _config.next();
                String type = _config.next();
                String tempPerm = "";
                while (_config.hasNext("\\((([^\\s+])|\\++)+\\)")) {
                    tempPerm += _config.next();
                }
                Permutation perm = new Permutation(tempPerm,
                        _alphabet);
                switch (type.charAt(0)) {
                case 'M':
                    allRotors.put(name, new MovingRotor(name, perm,
                            type.substring(1)));
                    break;
                case 'N':
                    allRotors.put(name, new FixedRotor(name, perm));
                    break;
                case 'R':
                    allRotors.put(name, new Reflector(name, perm));
                    break;
                default:
                    break;
                }
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment.
     * @param m The machine that will be configured.
     * @param rotors The string containing rotor information in the format
     *               I II III IV
     * @param initialPos A string containing the initial setting of the rotors
     *                   Is a series of N characters representing the initial
     *                   positions of each of the non-reflector rotors.
     *                   N = # slots - 1
     * @param ringSetting A string containing the ring setting of the rotors.
     * @param plugboard A string containing 0 or more 2-character cycles
     *                  representing the plugboard of the machine. */
    private void setUp(Machine m, String rotors, String initialPos,
                       String ringSetting, String plugboard) {
        m.insertRotors(rotors);
        m.setRotors(initialPos, ringSetting);
        if (!(plugboard == null)) {
            m.setPlugboard(new Permutation(plugboard, _alphabet));
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        char[] arr = msg.toCharArray();
        for (int i = 0; i < arr.length; i += 1) {
            _output.print(arr[i]);
            if (i % 5 == 4) {
                _output.print(' ');
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
