# CloudComputing
1. NaiveBayes Classifier For News

Training data format: \\
Label1[,Label2,Label3...] NewsDocument1 \\
Label1[,Label2,Label3...] NewsDocument2

Testing data format: \\
NewDocument1 \\
NewDocument2

How to run: \\
cat train.txt | java NBTrain | java NBTest test.txt \\
This allows us to process large amount of data without having to hold it all in memory.
