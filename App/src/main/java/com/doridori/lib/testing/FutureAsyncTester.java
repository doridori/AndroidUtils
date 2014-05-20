package com.doridori.lib.testing;

import android.os.Handler;
import android.os.HandlerThread;
import org.apache.http.MethodNotSupportedException;

import java.util.concurrent.*;

/**
 * @author Dorian Cussen
 * Date: 15/11/2012
 *
 * <p>Class for testing async methods. We could just use a Runnable combined with some wait logic dotted around and a syncronized var to store the result but this class abstracts that boilerstuff away. This is useful inside of a testing framework like JUnit where we want to Assert some result before exiting the test method in question. We cant use CountDownLatch or Futures individually for the following reasons.</p>
 * <or>
 * <li>CountDownLatch - this will basically just cause the main thread to wait - this is not that useful for Android style async callbacks which generally seem to be initiated via Loopers / Handlers and get called on the main thread anyway. The thread would just wait and then the callback would come after the assert</li>
 * <li>FutureTask - this needs the Runnable / Callable thats passed in to return a result / exception. When the run or call method exits any threads waiting on the FutureRunnable will be released - not good for asynchronous calls with callbacks on the same thread!</li>
 * </or>
 * <p>What this class will do utilise Loopers (via HandlerThread) and Latches to provide a simple-to-use class for testing those async calls Android style! The use of HandlerThread means that the thread wont die strait after calling the .run() method and wont just close after a passed in Runnables.run() method is called (which would be useless for async). Also it means any handlers that are created by the called async methods can communicate back to this worker thread. If we did this using a thread without a looper this aspect would not work.</p>
 * <p>While its common to mock out a lot of these types of calls I want to test a remote api with multiple user types and a class that does this is very much needed for me!</p>
 *
 * <b>From API level 9 onwards - shouldnt matter as TEST proj only</b>
 *
 * <p>Check out the test methods for this class in the Test project (same package but in test project) to see how you would use it</p>
 *
 */
public abstract class FutureAsyncTester<T> implements RunnableFuture<FutureAsyncTestResult<T>> {

    private HandlerThread mHandlerThread;
    private FutureAsyncTestResult<T> mResult;
    /**
     * The countDownLatch here is just used for blocking behaviour (with an optional timeout setting) to support the RunnableFuture inherited get() methods
     */
    private CountDownLatch mCountDownLatch;
    /**
     * To communicate with the worker looper
     */
    private Handler mHandler;
    private boolean mHasStarted = false;

    public FutureAsyncTester() {
        mCountDownLatch = new CountDownLatch(1);
        mHandlerThread = new HandlerThread(FutureAsyncTester.class.getName());

    }

    /**
     * Dont call directly - allow the get() methods to call
     */
    public void start(){
        if(mHasStarted)
            throw new RuntimeException("only call start() once. Note it is called auto in both of the get() methods below so you prob wont call explicitly");

        mHasStarted = true;

        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.post(this);
    }

    /**
     * Override this and call setFutureResult() methods in your async callbacks
     */
    @Override
    public abstract void run();

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new RuntimeException(new MethodNotSupportedException("not supported - dont need for testing!"));
    }

    @Override
    public boolean isCancelled() {
        throw new RuntimeException(new MethodNotSupportedException("not supported - dont need for testing!"));
    }

    @Override
    public boolean isDone() {
        return null != mResult;
    }

    /**
     * Will call start() and therefore internally call {@link #run()}
     *
     * @return should never return null as it should wait until the result is received
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    @Override
    public FutureAsyncTestResult<T> get() throws InterruptedException {
        start();
        mCountDownLatch.await();
        return mResult;
    }

    /**
     * Will call start() and therefore internally call {@link #run()}
     *
     * @param timeout
     * @param unit
     * @return will return null if the timeout set ends up being shorter than the run() method takes to complete (and therefore {@link #setFutureResult(Object, Exception, boolean)} has not been called yet})
     * @throws InterruptedException
     */
    @Override
    public FutureAsyncTestResult<T> get(long timeout, TimeUnit unit) throws InterruptedException {
        start();
        mCountDownLatch.await(timeout, unit);
        return mResult;
    }

    /**
     * This should be called from inside your overridden {@link #run()} implementation. These args will be wrapped
     * and passed back from your get() method IF <code>returnResult</code> == true.
     *
     * @param result if successful set a result
     * @param e if an exception is caught set this instead (marking failure)
     * @param returnResult pass in false if this is an intermediary result and you do not want the .get to return yet. Only do this if you are going to set another result at some point after!
     */
    public synchronized void setFutureResult(T result, Exception e, boolean returnResult){
        mResult = new FutureAsyncTestResult<T>(result, e);

        if(returnResult){
            mCountDownLatch.countDown();
            mHandlerThread.quit();
        }
    }

    /**
     * Same as calling setFutureResult(result, null, true)
     *
     * @param result
     */
    public synchronized void setFutureResultAndReturn(T result){
        setFutureResult(result, null, true);
    }

    /**
     * Same as calling setFutureResult(null, e, true)
     *
     * @param e the exception
     */
    public synchronized void setFutureResultAndReturn(Exception e){
        setFutureResult(null, e, true);
    }

    /**
     * You probably want to just get this from calling {@link #get()} but if you want to grab inside your run() method this will do
     *
     * @return may be null
     */
    public FutureAsyncTestResult<T> getResult()
    {
        return mResult;
    }
}