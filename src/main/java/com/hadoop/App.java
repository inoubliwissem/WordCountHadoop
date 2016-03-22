package com.hadoop;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
/**
 * Hello world!
 *
 */
public class App {
// map function
 public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
 	     private final static IntWritable one = new IntWritable(1);
 	     private Text word = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
	       String line = value.toString();
	       StringTokenizer tokenizer = new StringTokenizer(line);
	       while (tokenizer.hasMoreTokens()) {
	         word.set(tokenizer.nextToken());
	         output.collect(word, one);
	       }
	     }
	   }
// reduce function
public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
	     public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
	       int sum = 0;
      while (values.hasNext()) {
         sum += values.next().get();
	       }
	       output.collect(key, new IntWritable(sum));
     }
   }
  // Job mapreduce
   public static void main(String[] args) throws Exception {
                  
            if (args.length != 2) {
            System.err.println("not arguments");
            System.exit(0);
            }
            String input = args[0];
            String output = args[1];

             long startTime = System.currentTimeMillis();

	     JobConf conf = new JobConf(App.class);
             conf.setJobName("App");

	     conf.setOutputKeyClass(Text.class);
	     conf.setOutputValueClass(IntWritable.class);

	     conf.setMapperClass(Map.class);
	     //this to acclerate the calcule
	     // add partionner
	     conf.setCombinerClass(Reduce.class);
             conf.setReducerClass(Reduce.class);

	     conf.setInputFormat(TextInputFormat.class);
             conf.setOutputFormat(TextOutputFormat.class);

	     FileInputFormat.setInputPaths(conf, new Path(input));
	     FileOutputFormat.setOutputPath(conf, new Path(output));

	     JobClient.runJob(conf);
             long endTime = System.currentTimeMillis();
             System.err.println("run time = : "+(endTime-startTime));
	   }
	}
