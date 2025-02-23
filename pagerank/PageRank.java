import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PageRank {

    // Mapper: emits structure and rank contributions.
    public static class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {
        private Text outKey = new Text();
        private Text outValue = new Text();
        
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            // Input line: node<TAB>currentRank<TAB>outlink1,outlink2,...
            String[] parts = value.toString().split("\t");
            if (parts.length < 2) return; // must have at least node and rank
            
            String node = parts[0];
            double currentRank = Double.parseDouble(parts[1]);
            String outlinks = parts.length == 3 ? parts[2] : "";
            String[] links = outlinks.isEmpty() ? new String[0] : outlinks.split(",");
            int numLinks = links.length;
            
            // Emit the node structure (so we don’t lose the outlinks)
            outKey.set(node);
            outValue.set("LINKS:" + outlinks);
            context.write(outKey, outValue);
            
            // Emit rank contributions for each outlink
            if (numLinks > 0) {
                double contribution = currentRank / numLinks;
                for (String link : links) {
                    outKey.set(link.trim());
                    outValue.set(String.valueOf(contribution));
                    context.write(outKey, outValue);
                }
            }
        }
    }

    // Reducer: sums contributions and reconstructs the node structure.
    public static class PageRankReducer extends Reducer<Text, Text, Text, Text> {
        private static final double DAMPING = 0.85;
        private Text outValue = new Text();
        
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            double sumContributions = 0.0;
            String outlinks = "";
            
            // Process each value: either a rank contribution or the structure (prefixed with LINKS:)
            for (Text val : values) {
                String s = val.toString();
                if (s.startsWith("LINKS:")) {
                    outlinks = s.substring("LINKS:".length());
                } else {
                    sumContributions += Double.parseDouble(s);
                }
            }
            
            // Compute new rank with damping factor: newRank = (1 - d) + d * (sum of contributions)
            double newRank = (1 - DAMPING) + DAMPING * sumContributions;
            // Output in the same format as the input (for the next iteration)
            outValue.set(newRank + "\t" + outlinks);
            context.write(key, outValue);
        }
    }

    // Driver: sets up the MapReduce job for one iteration.
    public static void main(String[] args) throws Exception {
        // Arguments: inputPath outputPath
        if (args.length != 2) {
            System.err.println("Usage: PageRank <input path> <output path>");
            System.exit(-1);
        }
        
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "PageRank");
        job.setJarByClass(PageRank.class);
        
        job.setMapperClass(PageRankMapper.class);
        job.setReducerClass(PageRankReducer.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
