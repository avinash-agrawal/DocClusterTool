DocClusterTool
Version: 0.0001

Description:
This application takes a collection of documents, converts them into a WEKA ARFF
format, and performs WEKA hierarchical clustering algorithms to those documents.
Right now, only a collection that has separate simple SGML format document files
in one directory can be clustered.


How to Run:
This application will have two (possibly three) parts.

1) The document management is handled by DocumentManager. Document management is
defined by converting a raw document collection into the required WEKA ARFF
format of word vectors. This is done by first converting the collection into a
single ARFF file that contains the filename, contents, and classifier for each
document. The current version just uses a dummy classifier for each document
because DocClusterTool can only handle a collection that has no given
classifiers. After this ARFF file is created, it is used to create another ARFF
that contains word vectors of each document. This is the ARFF file that is
needed for clustering. Lastly, the document management is handled through the
interface.

2) The clustering management is handled by ClusterManager. Clustering management
is defined by taking the word vector ARFF file produced by DocumentManager and
clustering those documents. ClusterManager has to be run manually right now.
Soon, it will be integrated into another tab in the interface. After the
clustering is finished, then a pop-up of a 2D graph will appear that displays
the results. 

3) There may be another section that handles the graphical results.


Required Libraries:
All required libraries are included in the lib directory. They will need to be
added to the build path. They all have their own copyright licenses and they
should be represented here. 

Apache Commons IO - http://mirror.nexcess.net/apache//commons/io/binaries/commons-io-2.4-bin.tar.gz
Snowball Stemmer - http://snowball.tartarus.org/dist/libstemmer_java.tgz
WEKA - http://www.cs.waikato.ac.nz/ml/weka/downloading.html
