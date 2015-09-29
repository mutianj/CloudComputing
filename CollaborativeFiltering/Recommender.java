/**
 * The MIT License (MIT)
 * Copyright (c)  <JerrySun363> <mutianj>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */




import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;



public class Recommender {
	public HashMap<Integer, ObjVector> movies = new HashMap<Integer, ObjVector>();
	public HashMap<Integer, ObjVector> users = new HashMap<Integer, ObjVector>();
	public ArrayList<String> testQuery = new ArrayList<String>();
	public HashMap<Integer, PriorityQueue<KNNVector>> similarityResults = new HashMap<Integer, PriorityQueue<KNNVector>>();
	/* The choices of Similarity measures.*/
	public static int COS = 0;
	public static int ADJCOS = 1;
	/* The choices of Predictor measures. */
	public static int MEAN = 2;
	public static int WEIGHT = 3;
	public static int ADJWEIGHT = 4;
	/* The choices of user-based and item-based. */
	private static int USER = 5;
	private static int MOVIE = 6;
	
	public static void main(String[] args) {
		String trainFile = args[0];
		String testFile = args[1];
		String outputFile = args[2];
		Recommender rc = new Recommender();
		rc.createTrain(trainFile);
		rc.createTest(testFile);
		
		rc.test(outputFile, 10, Recommender.MEAN, Recommender.COS, Recommender.MOVIE);
	}
	
	/**
	 * This method reads in the trainFile and create movies vectors.
	 * Records from the same movie are consecutive in trainFile.
	 * @param trainFile
	 */
	public void createTrain(String trainFile){
		Scanner sc = null;
		try{
			sc = new Scanner(new File(trainFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		sc.nextLine();
		int movieID = -1;
		int userID = -1;
		byte score = -1;
		MovieVector mv = null;
		int totalScore = 0;
		while (sc.hasNextLine()) {
			String textLine = sc.nextLine();
			String[] text = textLine.split(",");
			int newMovieID = Integer.parseInt(text[0]);
			userID = Integer.parseInt(text[1]);
			score = Byte.parseByte(text[2]);
			if (newMovieID!=movieID) {
				movieID = newMovieID;
				if (mv!=null){
					mv.setAvgScore(totalScore*1.0/mv.getRecords().size());
					this.movies.put(mv.getMovieID(), mv);
					this.createUserVector(mv);
				}
				mv = new MovieVector();
				mv.setMovieID(newMovieID);
				totalScore = 0;
			}
			totalScore += score;
			mv.addRecord(userID, score);
		}
		mv.setAvgScore(totalScore*1.0/mv.getRecords().size());
		this.movies.put(mv.getMovieID(), mv);
		this.createUserVector(mv);
		
		sc.close();
	}
	/**
	 * This method uses movies vectors to create users vectors.
	 * @param mv
	 */
	public void createUserVector(MovieVector mv) {
		for (Integer userID:mv.getRecords().keySet()){
			byte score = mv.getRecords().get(userID);
			if (users.containsKey(userID)){
				UserVector uv = (UserVector) users.get(userID);
				uv.addRecord(mv.getMovieID(), score);
				uv.setTotalScore(uv.getTotalScore()+score);
				uv.setSize(uv.getSize()+1);
			} else {
				UserVector uv = new UserVector();
				uv.setUserID(userID);
				uv.addRecord(mv.getMovieID(), score);
				uv.setTotalScore(score);
				uv.setSize(1);
				users.put(userID, uv);
			}
		}
	}
	/**
	 * This method reads in test file and creates test queries.
	 * @param testFile
	 */
	public void createTest(String testFile){
		Scanner sc = null;
		try{
			sc = new Scanner(new File(testFile));
		} catch (FileNotFoundException e){
			e.printStackTrace();
			System.exit(0);
		}
		sc.nextLine();
		while (sc.hasNextLine()) {
			testQuery.add(sc.nextLine());
		}
		sc.close();
	}
	/**
	 * This method gives the result of predictions.
	 * @param outputFile
	 * @param k
	 * @param predict
	 * @param similary
	 * @param direction
	 */
	public void test(String outputFile, int k, int predict, int similary, int direction){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Predictor predictor;
		if (predict==Recommender.MEAN) {
			predictor = new MeanPredictor();
		} else if (predict == Recommender.WEIGHT){
			predictor = new WeightedPredictor();
		} else {
			predictor = new AdjWeightedPredictor();
		}
		Similarity similarity;
		if (similary==Recommender.COS){
			similarity = new CosineSimilarity();
		} else {
			similarity = new AdjCosineSimilarity();
		}
		HashMap<Integer, ObjVector> base;
		if (direction==Recommender.USER) {
			base = this.users;
		} else {
			base = this.movies;
		}
		Iterator<String> ite = testQuery.iterator();
		while (ite.hasNext()){
			String[] query = ite.next().split(",");
			int movieID = Integer.parseInt(query[0]);
			int userID = Integer.parseInt(query[1]);
			PriorityQueue<KNNVector> knn = null;
			if (this.similarityResults.containsKey(userID)){
				knn = this.similarityResults.get(userID);
			} else {
				knn = knnSearch(userID, k, base, similarity);
				this.similarityResults.put(userID, knn);
			}
			if (predictor instanceof AdjWeightedPredictor){
				((AdjWeightedPredictor) predictor).setAvg(this.users.get(userID).getAvgScore());
			}
			double score = predictor.predictUser(movieID, knn);
			out.println(score);
		}
		out.flush();
		out.close();
	}
	
	/**
	 * This method finds the records with top k similarities.
	 * @param id
	 * @param k
	 * @param base
	 * @param similarity
	 * @return
	 */
	private PriorityQueue<KNNVector> knnSearch(int id, int k, HashMap<Integer, ObjVector> base, Similarity similarity) {
		PriorityQueue<KNNVector> knn = new PriorityQueue<KNNVector>(k, new KCompare());
		ObjVector queryVector = base.get(id);
		base.remove(id);
		for (ObjVector baseVector: base.values()){
			double similar = 0;
			similar = similarity.calSimilarity(queryVector, baseVector);
			knn.add(new KNNVector(baseVector, similar));
			if (knn.size()>k) {
				knn.poll();
			}
		}
		base.put(id, queryVector);
		return knn;
		
	}
}
