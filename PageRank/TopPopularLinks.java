import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.lang.Integer;
import java.util.StringTokenizer;
import java.util.TreeSet;

// >>> Don't Change
public class TopPopularLinks extends Configured implements Tool {
    public static final Log LOG = LogFactory.getLog(TopPopularLinks.class);

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TopPopularLinks(), args);
        System.exit(res);
    }

    public static class IntArrayWritable extends ArrayWritable {
        public IntArrayWritable() {
            super(IntWritable.class);
        }

        public IntArrayWritable(Integer[] numbers) {
            super(IntWritable.class);
            IntWritable[] ints = new IntWritable[numbers.length];
            for (int i = 0; i < numbers.length; i++) {
                ints[i] = new IntWritable(numbers[i]);
            }
            set(ints);
        }
    }
// <<< Don't Change

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        FileSystem fs = FileSystem.get(conf);
        Path tmpPath = new Path("/mp2/tmp");
        fs.delete(tmpPath, true);

        Job jobA = Job.getInstance(conf, "Link Count Map");
        jobA.setOutputKeyClass(IntWritable.class);
        jobA.setOutputValueClass(IntWritable.class);

        jobA.setMapperClass(LinkCountMap.class);
        jobA.setReducerClass(LinkCountReduce.class);

        FileInputFormat.setInputPaths(jobA, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobA, tmpPath);

        jobA.setJarByClass(TopPopularLinks.class);
        jobA.waitForCompletion(true);

        Job jobB = Job.getInstance(conf, "Top Popular Links");
        jobB.setOutputKeyClass(IntWritable.class);
        jobB.setOutputValueClass(IntWritable.class);

        jobB.setMapOutputKeyClass(NullWritable.class);
        jobB.setMapOutputValueClass(IntArrayWritable.class);

        jobB.setMapperClass(TopLinksMap.class);
        jobB.setReducerClass(TopLinksReduce.class);
        jobB.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(jobB, tmpPath);
        FileOutputFormat.setOutputPath(jobB, new Path(args[1]));

        jobB.setInputFormatClass(KeyValueTextInputFormat.class);
        jobB.setOutputFormatClass(TextOutputFormat.class);

        jobB.setJarByClass(TopPopularLinks.class);
        return jobB.waitForCompletion(true) ? 0 : 1;
    }

    public static class LinkCountMap extends Mapper<Object, Text, IntWritable, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] pages = line.split(":");
            StringTokenizer linksTo = new StringTokenizer(pages[1], " ");
            while (linksTo.hasMoreTokens()) {
            	String nextToken = linksTo.nextToken();
            	context.write(new IntWritable(Integer.parseInt(nextToken)), new IntWritable(1));
            }
            context.write(new IntWritable(Integer.parseInt(pages[0])), new IntWritable(0));
        }
    }

    public static class LinkCountReduce extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
            	sum+=val.get();
            }
           	context.write(key, new IntWritable(sum));
        }
    }

    public static class TopLinksMap extends Mapper<Text, Text, NullWritable, IntArrayWritable> {
        Integer N;
        private TreeSet<Pair<Integer, Integer>> countToLinksMap = new TreeSet<Pair<Integer, Integer>>();
        
        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
            this.N = conf.getInt("N", 10);
        }
        @Override
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        	Integer count = Integer.parseInt(value.toString());
        	Integer Link = Integer.parseInt(key.toString());
        	countToLinksMap.add(new Pair<Integer, Integer>(count, Link));
        	if (countToLinksMap.size()>this.N) {
        		countToLinksMap.remove(countToLinksMap.first());
        	}
        }
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
        	for (Pair<Integer, Integer> item: countToLinksMap){
            	Integer[] nums = {item.second,item.first};
            	IntArrayWritable val = new IntArrayWritable(nums);
            	context.write(NullWritable.get(), val);
            }
        }
    }

    public static class TopLinksReduce extends Reducer<NullWritable, IntArrayWritable, IntWritable, IntWritable> {
        Integer N;
        private TreeSet<Pair<Integer,Integer>> countToLinksMap = new TreeSet<Pair<Integer,Integer>>();
        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
            this.N = conf.getInt("N", 10);
        }
        @Override
        public void reduce(NullWritable key,Iterable<IntArrayWritable> values,Context context) throws IOException, InterruptedException {
        	for (IntArrayWritable val:values){
        		IntWritable[] pair = (IntWritable[]) val.toArray();
        		Integer count = pair[1].get();
        		Integer link = pair[0].get();
        		countToLinksMap.add(new Pair<Integer,Integer>(count, link));
        		if (countToLinksMap.size()>this.N){
        			countToLinksMap.remove(countToLinksMap.first());
        		}
        	}
        	for (Pair<Integer,Integer> item: countToLinksMap){
        		IntWritable count = new IntWritable(item.first);
        		IntWritable link = new IntWritable(item.second);
        		context.write(link,count);
        	}
        }
    }
}
