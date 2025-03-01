#==================================================#
# Optional: add input file to the HDFS
#==================================================#

# Make the directory
hadoop fs -rm -r ~/input/
hadoop fs -mkdir -p ~/input/

# Upload the input file to the HDFS
hadoop fs -put input/sample_input.txt ~/input/

#==================================================#
# Run the pagerank job
#==================================================#

# Recompile java code and wrap to jar
# Compile the java code and store classes in classes directory
javac -d classes pagerank/PageRank.java

# Create a jar file from the compiled classes
jar -cvf pagerank.jar -C classes/ .


# Get the current timestamp
timestamp=$(date +%s)

# Remove the output file
hadoop fs -rm -r ~/output/

# Run the pagerank job
hadoop jar pagerank.jar PageRank ~/input/ ~/output/

# Make the output directory
mkdir -p output-$timestamp
hadoop fs -get ~/output/ output-$timestamp
