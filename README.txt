
== Package/Class Breakdown  ==
ie.gmit.sw.
    language: n-gram hashing using LangDist and PartitionedLangDist. LangStats used to record TP/FP/precision for each language.
    neural_network.config: neural network configuration, configurable by the user or through optimized default values.
    neural_network.phase: loosely coupled neural network phases (enforcing SRP), E.g. training is done in TrainingPhase. PhaseManager acts as a facade for these phases.
    test: manual testing classes, mainly used to test individual phases. Not strictly part of the application itself.
    Runner: program entry point, just runs AILanguageDetection
    AILanguageDetection: orchestrates the overall flow of the application. Not much in here because of the use of the PhaseManager facade.

== Flow of the Program ==
The flow of this application is as follows:
  1. Network configuration by the user
  2. Training data creation phase
  3. Training phase
  4. Validation phase
  5. Prediction phase (of user input)

The program allows configuration of many aspects of the neural network. This includes the hashing range, n-gram length, and more. The first option you are offered is whether or not you want to configure all of these options yourself, or use the "optimized default parameters" which I have selected. The optimized defaults exist as a benchmark of the program's best performing neural network configuration. This is expected to train to ~91% in well under 3 minutes (time may vary on different hardware). Keep in mind that some network configurations could be catastrophically bad.

== Neural Network Design ==
I have mostly stuck to the design outlined in the project specification document. A hashed n-gram feature vector generated from a language sample is fed into the network as input, and a 235 neuron softmax output predicts the language classification.

Here are my findings for the configuration of the network:
- Initially, I found it very difficult to train the network beyond "guessing" accuracy. This may have been because I wasn't using a good topology or training data, and/or because my code contained bugs which meant that the network wasn't training on meaningful data at all.
- Once the network's accuracy went beyond "guessing", I noticed that the network was extremely sensitive to any sort of change. After refactoring some of the code later on, it became far less volatile. I guess that by refactoring, I unknowingly fixed a bug that was preventing the network from training in a reliable way.

- Multiple hashed n-gram feature vectors can be used as input at once, and I have found this to improve the network's accuracy. For example, two hashed n-gram feature vectors of size 256 can be used back to back as input, the first vector representing 1-grams and the second vector representing 2-grams. This comes to a total of 512 input neurons.
- A hashing vector of size 350 seems to be fairly optimal for my network, with quite a bit of leeway either way. Choosing this is a careful balance: if the vector is too small you will have too many collisions, but if the vector is too big then the data becomes too "sparse", lowering the network's accuracy and increasing it's training time. Because of the above, this means that ~700 input neurons seems about right for the network.
- Usage of n-grams of size >3 is detrimental. This must mean there are too many permutations of n-gram for sizes above 3, relative to the length and number of language samples. The network has to find matching patterns of n-grams between languages to make good predictions, and it seems like n-grams of size 3 or greater are too "unique" to be useful.
- Using more than 1 hidden layer is not effective. I would attribute this to the limited training data available: each language only has about ~50 reference samples in the given wili text file. The number of language samples for each language can be shown using this Linux command:
  awk -F '@' '{print $NF}' wili-2018-Small-11750-Edited.txt | sort | uniq -c | sort -r -k 1,1

- As for activation functions, I have found tanh() to be quite effective on my hidden layer, while ReLU also performs quite well. I believe the reason that tanh works well for me might be because of how my input data is normalized - the vector sums to 1, so increasing values at a vector index translates to an exponential increase in "hits" for that index, which matches the sharp curve of the tanh activation function. SoftMax should be used for the output layer, as the fact that all output neurons then sum to 1 make it very easy to extract each languages classification probability.
- I tried various metrics to decide the number of neurons in the hidden layer. I have found that generally, "(input + output) / 4" is a good formula in this case. A sizable number of neurons is needed here, to provide enough information for the large 235 neuron output of the network.
- I found a high dropout value to be very effective in improving the network's accuracy as well. This also suggests that there is a lack of training data to effectively train the network - using a high dropout value prevents the network from overfitting, allowing it to make more use out of a smaller training set.
- The default values for initialUpdate and maxStep are fine - adjusting them saw little improvement, or made things worse. I noticed that even Encog's Javadoc recommends just using the default values here.

With those findings in mind, here is the overall topology of the neural network
 (CLI selectable options are written in square brackets, everything else is fixed):
- Input format: [vectorSize] * [ngramLength] input vector of FP numbers in the range 0..1 (adds to 1),
                followed by a 235 length one-hot encoding to specify the sample's language,
                limiting language samples to [sampleLimit] samples per language
- Input layer:  null (linear) activation, has bias, neurons matching the input format 1:1,                [dropout] applied
- Hidden layer: tanh() activation,        has bias, [hiddenSize] neurons (based on the selected formula), [dropout] applied
- Output layer: SoftMax() activation,      no bias, 235 neurons,                                          [dropout] applied
- Training:     using 5-fold cross-validation, for [numEpochs]

The key conclusions and "learning outcomes" I make from these findings is as follows:
- In machine learning, bugs are quite difficult to find. The presence of bugs in the creation of the training data, for example, just leads to the network not being able to train to a good standard of accuracy, or even to any standard. As such, I believe the use of comprehensive automated testing could be of great use in the development of neural networks.
- Training data is key for the training of neural networks, especially for deep learning. With limited data (~50 samples per input class), the network is quite sensitive to changes in configuration, and deep learning (use of several hidden layers) is difficult to achieve.
- Configuring neural networks is a battle to find the "sweet spot" in many different areas. In a lot of cases, it's the battle between "not enough information" or "too much information" for the neural network to handle. These two sides relate to "overfitting" and "underfitting", respectively.
- Training a network with a vast number of output classes is a big challenge. A large output layer requires a large hidden layer to provide enough meaningful feature information, and a large hidden layer requires a large input layer to provide enough input information to discover features. To train a large neural network, you also need large amounts of quality training data, to ensure that nodes do not get "starved" of information. As everything gets bigger, everything gets harder to configure and fine-tune, and takes exponentially longer to train.

Here are some of the other things that I tried, that I have left out of the final project:
- Hashing the frequencies from multiple lengths of n-gram into the same vector. This does improve accuracy, but I achieved better results when these vectors are separated.
- Instead of training the network using the individual ~11k samples, I tried combining the samples by their language. For example, all samples for English were hashed and stored in a single vector. This gave a more accurate n-gram distribution for each language. The issue, though, is that 235 sets of training data is just not enough to properly train the network. Perhaps combining the samples in some other way could lead to better accuracy.
- Computing the cosine distances of a sample's hashed n-gram distribution to the combined vectors (as described above) of all languages. In the previous language detection project, I found that cosine distance was a very good metric for detecting languages in this way. Perhaps the network could spot patterns in the cosine distance matrix, and be able to make predictions with an even higher precision than the cosine distance metric itself? To my surprise, I wasn't able to get any success with this method at all. I suspect there may have been a bug somewhere, but I didn't want to spend any more time in pursuing the method.

== Assumptions/Clarifications ==
- Without using negative training data, I could not calculate the sensitivity or specificity of the network. In place of this, the validation stage of the program calculates the accuracy of the network, and calculates the precision of the network in predicting each of the 235 languages. This is useful to see which of the languages the network has no problem predicting, and which languages it can't seem to tell apart from the others. This information would be key to improving the network's accuracy even further.
- Although the spec said to use Encog 3.2, Encog 3.4 was the version that was given on Moodle, so that's the one I used.

== Extras ==
- Highly configurable network, giving the user 6 different selection choices. Each option has an explanation, guidance on which value should be picked, and a recommended "best" value. Each option is also limited to a set number of preset values, to guide the user and prevent them from creating an unusable network.
- The option to use multiple n-gram vectors at once, configurable through the above extra.
- Loosely coupled OO design, making use of several different design patterns.
