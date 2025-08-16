This program is called WordTracker.

It reads text files and stores every unique word in a binary search tree. It keeps track of which file and the line numbers they appear on. It can also merge results from multiple runs unless you delete the previous history.

To delete previous history and show each test file by itself, you must delete the old saved repository file first.
To do that, in Command Prompt use:
del repository.ser

Running the program

1. Open Command Prompt.
2. Go to the folder where the program files are located (where the WordTracker.jar file is).
3. Choose what you want to do:
   - If you want to clear previous history:
     del repository.ser
4. To run the tests (make sure you keep the res/ path because that’s where the test files are):

   For test 1:
   java -jar WordTracker.jar res/test1.txt -pf

   For test 2:
   java -jar WordTracker.jar res/test2.txt -pf

   For test 3:
   java -jar WordTracker.jar res/test3.txt -pf

   If you want to do test 3 with the output written to a results file:
   java -jar WordTracker.jar res/test3.txt -po -fresults.txt

Flag explanations:
-pf   print words in alphabetical order with list of files
-p1   print words in alphabetical order with list of files & line numbers
-po   print words in alphabetical order with list of files, line numbers & frequency count
-f<filename>   write the output to the given file instead of printing to console

Note:
If you don’t delete repository.ser before running, the program will merge new results with previous runs instead of starting fresh.
