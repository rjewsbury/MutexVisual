package mutex.display.controllers;

import mutex.display.ElementController;
import mutex.editor.model.Field;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;

import java.util.List;

public abstract class CustomDisplay extends ElementController
{
    public CustomDisplay(int threads) {
        initializeDisplay(threads);
    }

    public abstract void initializeDisplay(int threads);

//    @Override
//    public void setNumThreads(int n) {
//
//    }

    @Override
    public void addThreads(AlgorithmThreadGroup threads) {
        //to avoid forcing the user to handle an AlgorithmThreadGroup,
        //turn it into a normal array
        //by building a copy instead of using the original directly,
        //this still protects the group from modification
        AlgorithmThread[] threadArray = new AlgorithmThread[threads.size()];
        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i] = threads.getThread(i);
        }
        addThreads(threadArray);
    }

    public abstract void addThreads(AlgorithmThread[] threads);

//    @Override
//    public void clearThreads() {
//
//    }

//    @Override
//    public List<ViewElement> getElements() {
//        return null;
//    }

//    @Override
//    public void update(AlgorithmThread updated) {
//
//    }

    @Override
    public List<Field> getEditableFields() {
        return null;
    }

    @Override
    public boolean setField(Field fields) {
        return false;
    }
}
