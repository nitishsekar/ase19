## HW 7
### Team:
 * Satanik Ray - sray7
 * Nitish Sekar - nsekar
  
### Details:  
Assignment on Creating a Random Projection (RP) Tree for a given dataset:

The RPTreeGenerator class is used for this purpose. You first read the dataset and store it in a table object (Tbl):
```
tbl.read("pom310000.csv");
```
The read() method returns the class type for the final column. 
To generate the RP tree, we create a RPTreeGenerator object and call the generateRPTree() method, as shown below:
```
RPTreeGenerator rpTG = new RPTreeGenerator();
RPTree node = rpTG.generateRPTree(tbl);
```
While creating the RP tree, we're using P = 2 and N (Iterations for FastMap) = 10.
Run class TestRP to test the code.  

Output for pom310000.csv is in pom3Output.txt  
Output for xomo10000.csv is in xomoOutput.txt
