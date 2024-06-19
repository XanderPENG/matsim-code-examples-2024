# The content in this file can be ***twofold***:,
   1. Record: How to create a basic/multimodal MATSim network using existing APIs.
   2. Record: The ideas, thoughts, and important information (e.g., data structure) 
   of our own networkParser and networkGenerator.

## Create a multimodal MATSim network using existing APIs
### 1. using @multimodal contrib to create a network (see [here](RunCreateMultimodalNetworkFromOSM.java)) 
However, it seems this API can only generate network during runtime, and the generated network is not saved as a file.
### 2. using ___@pt2matsim___  to create a multimodal network (see [here](RunCreateMultimodalNetworkbyPT2Matsim.java));

## Add Attributes into the cycling network
- we can use @addOverridingLinkProperties (in the @SupersonicOsmNetworkReader) to add link properties to the network;
- 