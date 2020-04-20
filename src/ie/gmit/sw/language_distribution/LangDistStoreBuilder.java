package ie.gmit.sw.language_distribution;

import ie.gmit.sw.sample_parser.SampleParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder pattern used to build language distribution stores.
 */
public class LangDistStoreBuilder {
    private List<SampleParser> parsers;
    private LangDistStore store;

    /**
     * Creates a new builder.
     */
    public LangDistStoreBuilder() {
        parsers = new ArrayList<>();
    }

    /**
     * Creates a new LangDistMap to use for this builder.
     *
     * @param hashRange Range of hashed k-mer values (passed to constructor of LangDistMap).
     * @param kmerLength Length of k-mer to use for this store.
     * @return This builder.
     */
    public LangDistStoreBuilder withMappedStore(int hashRange, int kmerLength) {
        store = new LangDistMap(hashRange ,kmerLength);

        return this;
    }

    /**
     * Registers another SampleParser with this builder, which will parse when build() is called.
     *
     * @param parser Parser to register.
     * @return This builder.
     */
    public LangDistStoreBuilder registerParser(SampleParser parser) {
        parsers.add(parser);

        return this;
    }

    /**
     * Builds the LangDistStore by parsing all registered parsers, and recording the associated k-mers
     * with the specified LangDistStore type.
     *
     * @return Resulting LangDistStore after building.
     */
    public LangDistStore build() {
        for (SampleParser parser : parsers) {
            parser.parseAll(store);
        }

        return store;
    }
}
