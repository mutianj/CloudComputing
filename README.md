# CloudComputing
### NaiveBayes Classifier For News

Training data format: <br/>
`Label1[,Label2,Label3...] NewsDocument1` <br/>
`Label1[,Label2,Label3...] NewsDocument2` <br/>
One document could have multiple labels.

Testing data format: <br/>
`NewDocument1` <br/>
`NewDocument2`

Run: <br/>
`cat train.txt | java NBTrain | java NBTest test.txt` <br/>
This allows us to process large amount of data without having to hold it all in memory.
