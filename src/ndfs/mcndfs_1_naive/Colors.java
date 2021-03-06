package ndfs.mcndfs_1_naive;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import graph.State;

/**
 * This class provides a color map for graph states.
 */
public class Colors {

    private final Map<State, Color> map = new HashMap<State, Color>();
    private final Map<State, Boolean> pinkMap = new HashMap<State, Boolean>(); // to maintain pink states locally
    private static Map<State, Boolean> redMap = new HashMap<State, Boolean>(); // to maintain red states globally
    private static Map<State, AtomicInteger> counterMap = new HashMap<State, AtomicInteger>(); // to maintain counter for states globally

    /**
     * Returns <code>true</code> if the specified state has the specified color,
     * <code>false</code> otherwise.
     *
     * @param state
     *            the state to examine.
     * @param color
     *            the color
     * @return whether the specified state has the specified color.
     */
    public boolean hasColor(State state, Color color) {

        // The initial color is white, and is not explicitly represented.
        if (color == Color.WHITE) {
            return map.get(state) == null;
        } else {
            return map.get(state) == color;
        }
    }

    public boolean isPink(State state){
       Boolean value = pinkMap.get(state);
       return value == null? false : value.booleanValue();
    }

    public void makePink(State state, boolean value){
        if(!pinkMap.containsKey(state))
            pinkMap.put(state, new Boolean(true));
        else
            pinkMap.put(state, new Boolean(value));
    }

    synchronized public static boolean isRed(State state){
        Boolean value = redMap.get(state);
        return value == null? false : value.booleanValue();
    }

    synchronized public static void makeRed(State state, boolean value){
        redMap.put(state, new Boolean(value));
    }

    synchronized public static int incrementCounter(State state){
        if(!counterMap.containsKey(state))
            counterMap.put(state, new AtomicInteger(0));
        return counterMap.get(state).incrementAndGet();
    }

    synchronized public static int decrementCounter(State state){
        int result;
        if(!counterMap.containsKey(state))
            counterMap.put(state, new AtomicInteger(0));
        result = counterMap.get(state).decrementAndGet();
        synchronized (counterMap) {
            counterMap.notifyAll();
        }
        return result;
    }

    public static void waitForState(State state){
        synchronized (counterMap) {
            AtomicInteger counter = counterMap.get(state);
            if(counter == null)
                return;
            while (counter.get() != 0) {
                try {
                    counterMap.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Gives the specified state the specified color.
     *
     * @param state
     *            the state to color.
     * @param color
     *            color to give to the state.
     */
    public void color(State state, Color color) {
        if (color == Color.WHITE) {
            map.remove(state);
        } else {
            map.put(state, color);
        }
    }
}
