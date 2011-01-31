package br.com.manish.ahy.fxadmin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskHelper implements Runnable {

    // Using a fixed threadpool to run the asynchronous task
    private static final ExecutorService QUEUE = Executors.newFixedThreadPool(10);

    // the "parent" JavaFX AsyncTask instance
    private final Task peer;

    public AsyncTaskHelper(Task peer) {
        this.peer = peer;
    }

    // called from AsyncTask.start() method - will add this task to the thread pool queue
    public void start() {
        QUEUE.execute(this);
    }

    // called by the thread pool queue to start the task
    @Override
    public void run() {
        peer.taskRun();
    }

    // interface to be implemented by the "parent" JavaFX AsyncTask
    public static interface Task {
        public void taskRun();
    }
}