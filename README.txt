
== Package breakdown & Design patterns ==
ie.gmit.sw.
    language_detector_system: classes relating to the queueing and processing of language detection jobs
        (where LangDetectionSystem behaves as a facade)
    language_detector: classes relating to language detection algorithms
        (using the strategy pattern)
        (where LangDetectors can be easily constructed using the factory pattern used in LangDetectorFactory)
    language_distribution: classes relating to the storage of k-mer frequency data, and the recording of k-mer occurrences
        (where LangDistStoreBuilder, a builder pattern, can be used to make the construction of these stores much easier)
    sample_parser: classes relating to the parsing of language samples (pairs of language text and language name)

== Design choices ==
- All areas of the project are highly abstracted and loosely coupled, allowing easy extension or modification of the codebase
(E.g., you could easily extend SampleParser to allow language samples to be read from a webpage).

- Several design patterns are utilised, hiding complexity and improving flexibility.

- Instead of using TreeMaps, I decided to try out using arrays to record k-mer occurrences, where the k-mer is hashed to a
value within the range of a large int array. I was hoping that collisions wouldn't affect the accuracy of the language
detection very much, and it seems to work just fine (and is much faster than using Maps).

- Language detection is done using the strategy pattern, allowing the language detection algorithm to be easily swapped
at runtime.

- Language detection jobs are processed asynchronously by a number of workers, on their own thread. BlockingQueues are used,
allowing a large number of jobs to be queued and processed seamlessly.

== Assumptions/Clarifications ==
- I assumed sub-packages are allowed even though the package ie.gmit.sw must be used.
- I was unsure if the dataset text file should be placed in "/data/..." or "data/...". I decided to leave the path specified
in the template web.xml file unchanged, and so I had to place the file in my tomcat/bin folder for it to be recognised,
NOT in the root of my system drive as specified in the template ServiceHandler.java javadoc.

== References ==
- DoubleArrayIndexComparator is loosely based on this StackOverflow answer: https://stackoverflow.com/a/4859279

== Extras ==
- 3 separate language detection metrics implemented, which the user can choose between at runtime (from the options dropdown):
    "Out-of-place"
    "Simple distance"
    "Cosine similarity" (MOST ACCURATE)
