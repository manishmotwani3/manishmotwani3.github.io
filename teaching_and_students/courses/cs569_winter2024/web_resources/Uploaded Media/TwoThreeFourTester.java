import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class TwoThreeFourTester {
    public static boolean printDebug = false;

    enum Operation {
	ADD      { int eval(TwoThreeFourIntSet tree, int arg) { return tree.add(arg) ? 1 : 0; } },
	REMOVE   { int eval(TwoThreeFourIntSet tree, int arg) { return tree.remove(arg) ? 1 : 0; } },
	CONTAINS { int eval(TwoThreeFourIntSet tree, int arg) { return tree.contains(arg) ? 1 : 0; } },
	PRINT    { int eval(TwoThreeFourIntSet tree, int arg) { System.out.println(tree.treeString()); return 1; } },
	ARRAY    { int eval(TwoThreeFourIntSet tree, int arg) { System.out.println(tree.toString()); return 1; } },
	HEIGHT   { int eval(TwoThreeFourIntSet tree, int arg) { return tree.height(); } },
	SIZE     { int eval(TwoThreeFourIntSet tree, int arg) { return tree.size(); } };
	abstract int eval(TwoThreeFourIntSet tree, int arg);
    }

    public static void readScript(BufferedReader in) {
	TwoThreeFourIntSet tree = new TwoThreeFourIntSet();
	String line;
	try {
	    while ((line = in.readLine()) != null) {
		String[] splitLine = line.split(" ");
		if (splitLine.length > 2) {
		    System.err.println("Too many arguments");
		}
		String command = splitLine[0].toUpperCase();
		int arg = -1;
		if (splitLine.length > 1) {
		   arg = Integer.parseInt(splitLine[1]);
		}
		Operation op = null;
		try {
		    op = Enum.valueOf(Operation.class, command);
		} catch (IllegalArgumentException e) {
		    System.err.println("Unknown command");
		    continue;
		}
		int result = op.eval(tree, arg);
		System.out.println(op + " " + arg + " = " + result);
	    }
	} catch (IOException e) {
	    return;
	}
    }

    public static void randomTest(int numOps, int range) {
	// FUZZ TESTER!!!
	TwoThreeFourIntSet tree = new TwoThreeFourIntSet();
	Random random = new Random();
	// values keeps a list of all values that we believe should be in the tree.
	List<Integer> values = new ArrayList<Integer>();
	// Lookup enum matching command
	Operation[] opSet = EnumSet.allOf(Operation.class).toArray(new Operation[0]);
	for (int i = 0; i < numOps; ++i) {
	    Operation op = opSet[random.nextInt(opSet.length)];
	    int arg = random.nextInt(range);
	    switch (op) {
		case ADD:
		    values.add(arg);
		    break;
		case REMOVE:
		case CONTAINS:
		    // If the chosen operation is REMOVE or CONTAINS, we may
		    // either choose a value that we know is inside the 2-3-4
		    // tree (75% chance), or just pick a random number that
		    // may or may not be in the tree.
		    if (values.size() > 0) {
			boolean useValid = random.nextInt(100) > 75;
			int pos = random.nextInt(values.size());
			if (useValid) {
			    arg = values.get(pos);
			}
			if (op == Operation.REMOVE) {
			    values.remove(pos);
			}
			break;
		    }
		default:
		    break;
	    }
	    int result = op.eval(tree, arg);
	    System.out.println(op + " " + arg + " = " + result);
	}
    }

    public static void usage(String message) {
	if (message != null) {
	    System.err.println(message);
	}
	System.err.println(
	    "Usage: java TwoThreeFourTester [--file=filename] [--interactive] [--random] [--numops=#] [--range=#] [--debug]");
	System.exit(1);
    }

    public static void main(String[] args) {
	BufferedReader file = null;
	boolean random = false;
	int range = 1000;
	int numOps = 500;

	for (String arg: args) {
	    if (arg.equals("--debug")) {
		printDebug = true;
		TwoThreeFourIntSet.printDebug = true;
	    } else if (arg.equals("--help") || arg.equals("-h")) {
		usage(null);
	    } else if (arg.startsWith("--numops=")) {
		numOps = Integer.parseInt(arg.substring("--numops=".length()));
	    } else if (arg.startsWith("--range=")) {
		range = Integer.parseInt(arg.substring("--range=".length()));
	    } else if (arg.equals("--random")) {
		random = true;
	    } else if (arg.startsWith("--file=")) {
		String filename = arg.substring("--file=".length());
		try {
		    file = new BufferedReader(new InputStreamReader(
			new DataInputStream(new FileInputStream(filename))));
		} catch (IOException e) {
		    usage("Could not open file '" + filename + "'");
		}
	    } else if (arg.equals("--interactive")) {
		file = new BufferedReader(new InputStreamReader(System.in));
	    } else {
		usage("Unknown flag '" + arg + "'");
	    }
	}

	if (random) {
	    randomTest(numOps, range);
	}

	if (file != null) {
	    readScript(file);
	}
    }
}
