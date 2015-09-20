# CloudComputing
1. NaiveBayes Classifier For News

Training data format: <br/>
`Label1[,Label2,Label3...] NewsDocument1` <br/>
`Label1[,Label2,Label3...] NewsDocument2`

Testing data format: <br/>
`NewDocument1` <br/>
`NewDocument2`

How to run: <br/>
`cat train.txt | java NBTrain | java NBTest test.txt` <br/>
This allows us to process large amount of data without having to hold it all in memory.
