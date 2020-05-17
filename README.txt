
== Package breakdown  ==
ie.gmit.sw.
    language_detector: ...

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

As such, the key conclusions I make from these findings is as follows:
- Training data is key for the training of neural networks. With limited data (~50 samples per input class), any configuration of the network is extremely sensitive to change, and deep learning (use of >1 hidden layer) is difficult to achieve or impossible.
- Configuring neural networks is a battle to find the "sweet spot" in many different areas. In a lot of cases, it's the battle between "not enough information" or "too much information" for the neural network to handle. These two sides relate to overfitting and underfitting.
- Training a network with a vast number of output classes is a big challenge. A large output layer requires a large hidden layer to provide enough meaningful feature information, and a large hidden layer requires a large input layer to provide enough input information to discover features. In order to train a large neural network, you also need large amounts of quality training data, to ensure that nodes do not get "starved" of information. As everything gets bigger, everyting gets harder to configure and fine-tune, and takes exponentially longer to train.

== Assumptions/Clarifications ==


== Extras ==

