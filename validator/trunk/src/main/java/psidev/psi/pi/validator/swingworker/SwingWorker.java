/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 30-Aug-2007
 * Time: 12:26:58
 */
package psidev.psi.pi.validator.swingworker;

import javax.swing.SwingUtilities;

/**
 * This is the 3rd version of SwingWorker (also known as SwingWorker 3), an
 * abstract class that you subclass to perform GUI-related work in a dedicated
 * thread. For instructions on and examples of using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after creating it.
 */
public abstract class SwingWorker {
    /**
     * Class to maintain reference to current worker thread under separate synchronization control.
     */
    private static class ThreadVar {
        /**
         * 
         */
        private Thread thread;
        
        /**
         * Constructor.
         * @param t 
         */
        ThreadVar(Thread t) {
            this.thread = t;
        }
        
        /**
         * Gets the thread.
         * @return 
         */
        synchronized Thread get() {
            return this.thread;
        }
        
        /**
         * 
         */
        synchronized void clear() {
            this.thread = null;
        }
    }

    /**
     * Members.
     */
    private ThreadVar threadVar;
    private Object value;

    /**
     * Get the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     * @return Object
     */
    protected synchronized Object getValue() {
        return this.value;
    }

    /**
     * Set the value produced by worker thread
     */
    private synchronized void setValue(Object x) {
        this.value = x;
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     * @return Object
     */
    public abstract Object construct();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished() {
        // do nothing
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        Thread t = this.threadVar.get();
        if (t != null) {
            t.interrupt();
        }
        
        this.threadVar.clear();
    }

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the <code>construct</code> method
     */
    public Object get() {
        while (true) {
            Thread t = this.threadVar.get();
            if (t == null) {
                return getValue();
            }
            
            try {
                t.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }

    /**
     * Start a thread that will call the <code>construct</code> method and then exit.
     */
    public SwingWorker() {
        final Runnable doFinished = () -> {
            finished();
        };

        Runnable doConstruct = () -> {
            try {
                setValue(construct());
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
            finally {
                threadVar.clear();
            }
            SwingUtilities.invokeLater(doFinished);
        };

        this.threadVar = new ThreadVar(new Thread(doConstruct));
    }

    /**
     * Start the worker thread.
     */
    public void start() {
        Thread t = this.threadVar.get();
        if (t != null) {
            if (!t.isInterrupted()) {
                t.start();
            }
        }
    }
}
