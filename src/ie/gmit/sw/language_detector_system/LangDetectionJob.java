package ie.gmit.sw.language_detector_system;

import ie.gmit.sw.Lang;

/**
 * A simple class representing a language detection job.
 */
public class LangDetectionJob {
    private String id;
    private String sampleText;
    private Lang result;

    public LangDetectionJob(String id, String sampleText) {
        this.id = id;
        this.sampleText = sampleText;
    }

    /**
     * Job ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sample text (query text entered by the user).
     */
    public String getSampleText() {
        return sampleText;
    }

    /**
     * Detected language (null until set externally).
     */
    public Lang getResult() {
        return result;
    }

    public void setResult(Lang result) {
        this.result = result;
    }

    /**
     * Compares jobs based on job ID.
     *
     * @param otherObj LangDetectionJob to compare to.
     * @return True if their IDs match.
     */
    @Override
    public boolean equals(Object otherObj) {
        // (this covers otherObj == null)
        if (!(otherObj instanceof LangDetectionJob)) {
            return false;
        }

        LangDetectionJob otherJob = (LangDetectionJob) otherObj;

        return id.equals(otherJob.getId());
    }
}
