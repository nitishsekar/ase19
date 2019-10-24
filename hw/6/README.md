## HW 6
### Team:
 * Satanik Ray - sray7
 * Nitish Sekar - nsekar
  
### Details:  
Assignment on Creating a decision/regression tree for a given dataset:

The DecisionTreeGenerator class is used for this purpose. You first read the dataset and store it in a table object (Tbl):
```
String labelType = tbl.read("diabetes.csv");
```
The read() method returns the class type for the final column. 
To generate the decision tree, we create a DecisionTreeGenerator object and call the createDecisionTree() method, as shown below:
```
DecisionTreeGenerator dt = new DecisionTreeGenerator();
dt.createDecisionTree(tbl,"Sym");   // For Decision Tree
```
Or
```
dt.createDecisionTree(tbl,"Num");   // For Regression Tree
```
While creating the decision tree, we're using MIN_ROWS = 9. We've done this because our splitting code is creating more splits than expected. So, setting a higher MIN_ROWS reduces the depth of the tree. Also, for regression trees, we've set an additional stopping criteria to reduce the depth of the tree. If the SD reduction is less than 10%, we do not split.

Run class TestDT to test the code.  

Output for diabetes.csv is in outputDiabetes.txt  
Output for auto.csv is in outputAuto.txt
