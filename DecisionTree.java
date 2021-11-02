//Name: Rayan Bouamrane
//Student ID: 260788250

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

    DTNode rootDTNode;
    int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split
    //Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
    public static final long serialVersionUID = 343L;

    public DecisionTree(ArrayList<Datum> datalist, int min) {
        minSizeDatalist = min;
        rootDTNode = (new DTNode()).fillDTNode(datalist);
    }

    class DTNode implements Serializable {
        //Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
        public static final long serialVersionUID = 438L;
        boolean leaf;
        int label = -1;      // only defined if node is a leaf
        int attribute; // only defined if node is not a leaf
        double threshold;  // only defined if node is not a leaf


        DTNode left, right; //the left and right child of a particular node. (null if leaf)

        DTNode() {
            leaf = true;
            threshold = Double.MAX_VALUE;
        }


        // this method takes in a datalist (ArrayList of type datum) and a minSizeInClassification (int) and returns
        // the calling DTNode object as the root of a decision tree trained using the datapoints present in the
        // datalist variable
        // Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold


        DTNode fillDTNode(ArrayList<Datum> datalist) {


            if (datalist.size() == 0)
                return null;

            this.leaf = true;
            for (int i = 0; i < datalist.size(); i++) {
                boolean templab = (datalist.get(0).y == datalist.get(i).y);
                if (!templab) {
                    this.leaf = false;
                    break;
                }
            }

            if (this.leaf || calcEntropy(datalist) == 1) {
                label = datalist.get(0).y;
                return this;

            }


            double bAvgEntropy = 1;
            int bAttr = -1;
            double bThresh = -1;

            ArrayList<Datum> dleft = new ArrayList<Datum>();
            ArrayList<Datum> dright = new ArrayList<Datum>();

            for (int attr = 0; attr < 2; attr++) {
                ArrayList<Datum> tempdleft = new ArrayList<Datum>();
                ArrayList<Datum> tempdright = new ArrayList<Datum>();

                for (int dat = 0; dat < datalist.size(); dat++) {
                    double splitpoint = datalist.get(dat).x[attr];

                    for (int dat2 = 0; dat2 < datalist.size(); dat2++) {
                        if (datalist.get(dat2).x[attr] < splitpoint) {
                            tempdleft.add(datalist.get(dat2));
                        } else tempdright.add(datalist.get(dat2));
                    }

                    double cAvgEntropy = ((double) tempdleft.size() / datalist.size()) * calcEntropy(tempdleft) +
                            ((double) tempdright.size() / datalist.size()) * calcEntropy(tempdright);
                    if (bAvgEntropy > cAvgEntropy && tempdleft.size() > minSizeDatalist
                            && tempdright.size() > minSizeDatalist) {
                        bAvgEntropy = cAvgEntropy;
                        bAttr = attr;
                        bThresh = datalist.get(dat).x[attr];

                    }
                    tempdleft.clear();
                    tempdright.clear();
                }
            }
            if (bAttr != -1 && bThresh != -1) {
                for (int dat2 = 0; dat2 < datalist.size(); dat2++) {
                    if (datalist.get(dat2).x[bAttr] < bThresh) {
                        dleft.add(datalist.get(dat2));
                    } else dright.add(datalist.get(dat2));
                }
            }
            this.threshold = bThresh;
            this.attribute = bAttr;
            this.left = fillDTNode(dleft);
            this.right = fillDTNode(dright);

            return this;
        }

        //This is a helper method. Given a datalist, this method returns the label that has the most
        // occurences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.
        int findMajority(ArrayList<Datum> datalist) {
            int l = datalist.get(0).x.length;
            int[] votes = new int[l];

            //loop through the data and count the occurrences of datapoints of each label
            for (Datum data : datalist) {
                votes[data.y] += 1;
            }
            int max = -1;
            int max_index = -1;
            //find the label with the max occurrences
            for (int i = 0; i < l; i++) {
                if (max < votes[i]) {
                    max = votes[i];
                    max_index = i;
                }
            }
            return max_index;
        }


        // This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
        // returns its corresponding label, as determined by the decision tree
        int classifyAtNode(double[] xQuery) {
            //YOUR CODE HERE

            return -1; //dummy code.  Update while completing the assignment.
        }


        //given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
        //at DTNode object passed as the parameter
        public boolean equals(Object dt2) {

            if (this == dt2)
                return true;

            if (!(dt2 instanceof DTNode))
                return false;

            DTNode node2 = (DTNode) dt2;

            if (this.leaf && node2.leaf)
                if (this.label == node2.label)
                    return true;

            if (!this.leaf && !node2.leaf)
                if (this.attribute == node2.attribute && this.threshold == node2.threshold)
                    return (this.left.equals(node2.left) && this.right.equals(node2.right));
            return false;
        }

    }


    //Given a dataset, this retuns the entropy of the dataset
    double calcEntropy(ArrayList<Datum> datalist) {
        double entropy = 0;
        double px = 0;
        float[] counter = new float[2];
        if (datalist.size() == 0)
            return 0;
        double num0 = 0.00000001, num1 = 0.000000001;

        //calculates the number of points belonging to each of the labels
        for (Datum d : datalist) {
            counter[d.y] += 1;
        }
        //calculates the entropy using the formula specified in the document
        for (int i = 0; i < counter.length; i++) {
            if (counter[i] > 0) {
                px = counter[i] / datalist.size();
                entropy -= (px * Math.log(px) / Math.log(2));
            }
        }

        return entropy;
    }


    // given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
    int classify(double[] xQuery) {
        DTNode node = this.rootDTNode;
        return node.classifyAtNode(xQuery);
    }

    // Checks the performance of a DecisionTree on a dataset
    //  This method is provided in case you would like to compare your
    //results with the reference values provided in the PDF in the Data
    //section of the PDF

    String checkPerformance(ArrayList<Datum> datalist) {
        DecimalFormat df = new DecimalFormat("0.000");
        float total = datalist.size();
        float count = 0;

        for (int s = 0; s < datalist.size(); s++) {
            double[] x = datalist.get(s).x;
            int result = datalist.get(s).y;
            if (classify(x) != result) {
                count = count + 1;
            }
        }

        return df.format((count / total));
    }


    //Given two DecisionTree objects, this method checks if both the trees are equal by
    //calling onto the DTNode.equals() method
    public static boolean equals(DecisionTree dt1, DecisionTree dt2) {
        boolean flag = true;
        flag = dt1.rootDTNode.equals(dt2.rootDTNode);
        return flag;
    }


}

