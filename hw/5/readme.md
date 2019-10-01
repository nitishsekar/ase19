## HW 5
### Team:
 * Satanik Ray - sray7
 * Nitish Sekar - nsekar
  
### Details:  
Assignment on splitting Num & Sym based on SD & Entropy respectively.
The HW document said that the function should be able to receive a list of anything. So, we've written code to receive a list of Strings that contain an x,y pair (comma separated). We also send a keyword - Sym or Num - specifying the type of y values present in the list.

The requested format of the function call was:
```
Div2(lst,x=first, y=last,yis=Num)
```
We have written the code in the SplitAttributes class, with the method given below:
```
identifySplit(list,"Sym") // The y-column is a Sym
identifySplit(list,"Num") // The y-column is a Num
```
Run class TestSplitAttributes to test the code.  

Output for Symtest.txt is in outputSymtest.txt  
Output for Numtest.txt is in outputNumtest.txt
