Team:
<b>Satanik Ray - sray7</b>
<b>Nitish Sekar - nsekar</b>

<b>Details

Folder Resultant DataSets contain the following :
(a) Two class datasets for each leaf cluster, class1, from RPTree and the cluster it envies the most, class2 (used for local learning).
(b) Two class dataset for rows ranked by its domination. Top 20% have a class-system of best while the remaining 80% have a class-system of rest (global learning)

Folder Resultant Decision Trees contain the following :
(a) Trees.md for the print out of the trees from (a) above. 
(b) resultantGlobalLearningDT.md for the print out of the tree from (b) above. 

Run <b>TestDominate</b> to read auto.csv, randomly shuffle the rows, select a 100 of them, compare its domination with the other 99 rows and rank them accordingly. 

Run <b>TestRP</b> to read auto.csv, generate an RPTree from that, determine the leaf clusters and the cluster each envies the most, generate a two class-system dataset from each leaf & envy combination and then learn a decision tree from each. 

Run <b>TestDominate</b> to read auto.csv, rank each row by its domination over the other rows, generate a two class-system dataset for top 20% and rest 80% rows and learn a decision tree from that. 
