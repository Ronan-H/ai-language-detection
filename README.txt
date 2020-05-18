
== Package breakdown  ==
ie.gmit.sw.
    language_detector: ...

== Flow of the program ==
The flow of this application is as follows:
  1. Creation of the training data
  2. Network topology and training
  3. Accuracy, sensitivity, and specificity stats
  4. Test your own input (from a file)

The program allows configuration of many aspects of the neural network. This includes the hashing range, n-gram length, and more. The first option you are offered is whether or not you want to configure all of these options yourself, or use the "optimized default parameters" which I have selected. The optimized defaults exist as a benchmark of the program's best performing neural network configuration. This is expected to train to ~85% in about 1 minute (time may vary on different hardware). Keep in mind that even a small change to the network's optimal configuration could lead to catastrophicly bad results.

== Neural network design ==
I have mostly stuck to the design outlined in the project specification document. A hashed n-gram feature vector generated from a language sample is fed into the network as input, and a 235 neuron softmax output predicts the language classification.

Here are my findings for the configuration of the network:
- A hashing vector of size 256 seems to be fairly optimal for my network. I was surprised at how sensitive this value is - it's a careful balance between too many collisions if the vector is too small, and giving the network data that is too "sparse" if the vector is too big.
- Multiple hashed n-gram feature vectors can be used as input at once, and I have found this to improve the network's accuracy. For example, two hashed n-gram feature vectors of size 256 can be used back to back as input, the first vector representing 1-grams and the second vector representing 2-grams. This comes to a total of 512 input neurons.
- Usage of n-grams of size >3 is detrimental. This must mean there are too many permutations of n-gram for sizes above 3, relative to the size and number of language samples.
- Using more than 1 hidden layer is not effective. I would attribute this to the limited training data available: each language only has about ~50 reference samples in the given wili text file.
- As for activation funtions, I have found tanh to be quite effective on my hidden layer, while ReLU also performs quite well. I believe the reason that tanh works well for me might be because of how my input data is normalized - increasing values of a vector index translate to a large increase in "hits" for that index, which matches the sharp curve of the tanh activation function. SoftMax should be used for the output layer, as the fact that all output neurons then sum to 1 make it very easy to extract each languages classification probability.
- I tried various metrics to decide the number of neurons in the hidden layer. I have found that generally, "(input + output) / 2" is a good formula in this case. A sizable number of neurons is needed here, to provide enough information for the large 235 neuron output of the network.
- I found a high dropout value to be very effective in improving the network's accuracy as well. This also suggests the lack of training data - using a high droppout value prevents the network from overfitting, allowing it to make more use out of a smaller training set.
- The default values for initialUpdate and maxStep are fine - adjusting them saw little imrovement, or made things worse.

As such, the key conclusions I make from these findings is as follows:
- Training data is key for the training of neural networks. With limited data (~50 samples per input class), any configuration of the network is extremely sensitive to change, and deep learning (use of >1 hidden layer) is difficult to achieve or impossible.
- Configuring neural networks is a battle to find the "sweet spot" in many different areas. In a lot of cases, it's the battle between "not enough information" or "too much information" for the neural network to handle. These two sides relate to overfitting and underfitting.
- Training a network with a vast number of output classes is a big challenge. A large output layer requires a large hidden layer to provide enough meaningful feature information, and a large hidden layer requires a large input layer to provide enough input information to discover features. In order to train a large neural network, you also need large amounts of quality training data, to ensure that nodes do not get "starved" of information. As everything gets bigger, everyting gets harder to configure and fine-tune, and takes exponentially longer to train.

Here are some of the other things that I tried, that I have left out of the final project:
- Hashing the frequencies from multiple lengths of n-gram into the same vector. This does improve accuracy, but I achieved better results when these vectors were separated.
- Instead of training the network using the individual ~11k samples, I tried combining the samples by their language. For example, all samples for English were hashed and stored in a single vector. This gave a more accurate n-gram distribution for each language. The issue, though, is that 235 sets of training data is just not enough to properly train the network. Perhaps combining the samples in some other way could lead to better accuracy.
- Computing the cosine distances of a sample's hashed n-gram  distribution to the combined vectors (as described above) of all languages. In the previous language detection project, I found that cosine distance was actually a very good metric for detecting languages in this way. Perhaps the network could spot patterns in the cosine distance matrix, and be able to make predictions with an even higher prescision than the cosine distance metric itself? To my surprise, I wasn't able to get any success with this method at all. I suspect there may have been a bug somewhere, but I didn't want to spend any more time persuing the method.

== Assumptions/Clarifications ==
- Without using negative training data, I could not calculate the sensitivity or specificity of the network. In lieu of this, the validation stage of the program calculates the accuracy of the network, and calculates the precision of the network in predicting each of the 235 languages. This is useful to see which of the languages the network has no problem predicting, and which languages it can't seem to tell apart from the others. This information would be key in improving the network further.

== Extras ==
- Highly configurable network, giving the user 6 different selections. Each option has an explanation, guidance on which value should be picked, and a recommended "best" value. Each option is also limited to a set number of preset values, to guide the user and prevent them from creating an unusable network.

