package com.doridori.lib.model.patterns;

/**
 * Simple state machine. If need something that needs to handle a big transition matrix look at the SMC compiler
 *
 * Takes a generic so you can define you own root states. Can just use {@link com.doridori.lib.model.patterns.StateMachine.State} if you want.
 *
 * User: doriancussen
 * Date: 05/11/2012
 */
public class StateMachine<T extends StateMachine.State> {

    private T mCurrentState;

    public synchronized void nextState(T nextState){
        if(null != mCurrentState)
            mCurrentState.exitingState();

        mCurrentState = nextState;
        nextState.enteringState();
    }

    /**
     * Convenience method. This can be called when you are finishing with the state machine to trigger any cleanup
     * code in your last state inside the {@link com.doridori.lib.model.patterns.StateMachine.State#exitingState()} method
     *
     * @param finalState you may want to set a final state that is just a stub so any state machine calls after this method has
     *                   called will do nothing but not throw an NPE. Can be null
     */
    public synchronized void finish(T finalState){
        if(null != mCurrentState)
            mCurrentState.exitingState();

        if(null != finalState){
            nextState(finalState);
        }else{
            mCurrentState = null;
        }
    }

    public synchronized T getCurrentState(){
        return mCurrentState;
    }

    public static abstract class State{
        public void enteringState(){};
        public void exitingState(){};
    }


}
