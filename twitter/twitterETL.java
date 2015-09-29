import java.io.Serializable;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import com.google.common.collect.Lists;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import twitter4j.Status;

public final class TwitterETL {
  public static class TweetInputDoc implements Serializable {
    private String user;
    private String text;
    private Boolean isRetweet;

    public String getUser() {
      return this.user;
    }

    public void setUser(String userName) {
      this.user = userName;
    }

    public int getText() {
      return this.text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public Boolean getIsRetweet() {
        return this.isRetweet;
    }
    public void setIsRetweet(Boolean type){
        return this.isRetweet = type;
    }
  }

  public static void main(String[] args) {

    // Create the context with a 1000 second batch size
    String filtersArg = cli.getOptionValue("tweetFilters");
    String[] filters = (filtersArg != null) ? filtersArg.split(",") : new String[0];
    SparkConf sparkConf = new SparkConf().setAppName("TwitterETL").setMaster("local[*]");
    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(1000));
    SQLContext sqlContext = new sqlContext(jssc);

    JavaReceiverInputDStream<Status> tweets =
      TwitterUtils.createStream(jssc, null, filters);

    JavaDStream<TweetInputDocument> docs = tweets.map(new Function<Status, TweetInputDocument>() {
      public TweetInputDocument call(Status status) {
        TweetInputDocument record = new TweetInputDocument();
        record.setUser(status.getUser().getScreenName());
        record.setText(status.getText().replaceAll("\\s+", " "));
        record.setIsRetweet(status.isRetweet());
        return record;
      }
    });
    docs.foreachRDD(new Function<JavaRDD<TweetInputDocument>, time, Void>(){
      public void call (JavaRDD<TweetInputDocument> rdd, Time time) {
        SQLContext sqlContext = JavaSQLContextSingleton.getInstance();
        return rdd;
      }
    });
    DataFrame wordsDataFrame = sqlContext.createDataFrame(rdd, TweetInputDocument.class);
    schemaPeople.registerTempTable("tweets");

    jssc.start();
    jssc.awaitTermination();
  }

  public Option[] getOptions() {
    return new Option[]{
      OptionBuilder
              .withArgName("LIST")
              .hasArg()
              .isRequired(false)
              .withDescription("List of Twitter keywords to filter on, separated by commas")
              .create("tweetFilters")
    };
  }
}



