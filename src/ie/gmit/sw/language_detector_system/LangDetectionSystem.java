package ie.gmit.sw.language_detector_system;

import ie.gmit.sw.language_detector.LangDetector;
import ie.gmit.sw.language_distribution.LangDistStore;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Uses LangDetectionWorkers with an in-queue and an out-map to allow LangDetectionJobs
 * to be easily queued, processed, and retrieved.
 */
public class LangDetectionSystem {
    private LangDetectionWorker[] workers;
    private BlockingQueue<LangDetectionJob> inQueue;
    private ConcurrentMap<String, LangDetectionJob> outMap;

    /**
     * Constructs a LangDetectionSystem using a store of language frequency distributions, and some extra parameters.
     *
     * @param distStore Store of language distributions.
     * @param langDetector Language detector to use (contains an underlying strategy for detecting languages).
     * @param inQueueCap Maximum size for input queue (blocking).
     * @param numWorkers Number of asynchronous worker threads to run with the system.
     */
    public LangDetectionSystem(LangDistStore distStore, LangDetector langDetector, int inQueueCap, int numWorkers) {
        // initialise queues and workers
        inQueue = new ArrayBlockingQueue<>(inQueueCap);
        outMap = new ConcurrentHashMap<>();
        workers = new LangDetectionWorker[numWorkers];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new LangDetectionWorker(distStore, inQueue, outMap, langDetector);
        }
    }

    /**
     * Starts all workers on their own thread.
     */
    public void go() {
        for (int i = 0; i < workers.length; i++) {
            new Thread(workers[i]).start();
        }
    }

    /**
     * Stops all workers.
     */
    public void stop() {
        for (int i = 0; i < workers.length; i++) {
            workers[i].stop();
        }
    }

    /**
     * Submits a new job to be processed.
     *
     * @param id ID of the new job.
     * @param sampleText User entered query to be detected.
     */
    public void submitJob(String id, String sampleText) {
        try {
            inQueue.put(new LangDetectionJob(id, sampleText));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a specified job has finished being detected by a worker.
     *
     * @param id Job ID.
     * @return True if the job is finished.
     */
    public boolean isJobFinished(String id) {
        return outMap.containsKey(id);
    }

    /**
     * Gets the result of a completed job.
     *
     * @param id Job ID.
     * @return Name of detected language.
     */
    public String getLanguageResult(String id) {
        return outMap.get(id).getResult().getLanguageName();
    }
}
