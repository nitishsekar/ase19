### HW 9

Team:
<br><b>Satanik Ray - sray7</b>
<br><b>Nitish Sekar - nsekar</b>

<b>Details</b>

In this assingnment, we've written code to generate RPTrees using an entire dataset (HW 7), and incrementally using anomalous rows. To create an incremental RPTree, run class ```TestAnomalousRowAdd```.  
  
Run class ```TestRPTreeProbes``` to perform the comparison between the two types of RPTrees. By default, the ```magicAlpha``` is set as 0.5 in class ```RPTreeGenerator```. You can change it there.  
  
To calculate the "same" score, we consider two clusters same if all of their goals result in <b>true</b> after running ```Num.same()```. Check <i>report.md</i> for a detailed report on the same scores. 
