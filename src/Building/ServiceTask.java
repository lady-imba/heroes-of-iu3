package Building;

import java.io.Serializable;

public class ServiceTask implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean completed = false;
    private Runnable onCompleteCallback;
    private transient Thread serviceThread;

    public ServiceTask(Thread serviceThread) {
        this.serviceThread = serviceThread;
    }

    public synchronized void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
        if (completed && callback != null) {
            callback.run();
        }
    }

    public synchronized void complete() {
        if (!completed) {
            completed = true;
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }
        }
    }

    public synchronized boolean isDone() {
        return completed;
    }

    public void setServiceThread(Thread serviceThread) {
        this.serviceThread = serviceThread;
    }

    public Thread getServiceThread() {
        return serviceThread;
    }
} 