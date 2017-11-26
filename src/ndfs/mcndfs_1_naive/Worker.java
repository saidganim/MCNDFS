package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker extends Thread {

    private final Graph graph;
    private final Colors colors = new Colors();
    private boolean result = false;
    private int threadId;
    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class CycleFoundException extends Exception {
    }

    /**
     * Constructs a Worker object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public Worker(File promelaFile, int id) throws FileNotFoundException {
        this.threadId = id;
        this.graph = GraphFactory.createGraph(promelaFile);
    }

    private void dfsRed(graph.State s) throws CycleFoundException {
        colors.makePink(s, true);
        for (graph.State t : graph.post(s)) {
            if (colors.hasColor(t, Color.CYAN)) {
                throw new CycleFoundException();
            } else if (colors.isPink(t) && colors.isRed(t)) {
                dfsRed(t);
            }
            if(s.isAccepting()){
                colors.decrementCounter(s);
                colors.waitForState(s);
            }
            colors.makeRed(s, true);
            colors.makePink(s, false);
        }
    }

    private void dfsBlue(graph.State s) throws CycleFoundException {

        colors.color(s, Color.CYAN);
        for (graph.State t : graph.post(s)) {
            if (colors.hasColor(t, Color.WHITE) && !colors.isRed(t)) {
                dfsBlue(t);
            }
        }
        if (s.isAccepting()) {
            colors.incrementCounter(s);
            dfsRed(s);
        }
        colors.color(s, Color.BLUE);
    }

    private void nndfs(graph.State s) throws CycleFoundException {
        dfsBlue(s);
    }

    public void run() {
        try {
            nndfs(graph.getInitialState());
        } catch (CycleFoundException e) {
            result = true;
        }
    }

    public boolean getResult() {
        return result;
    }
}