# dibbla-hadoop-cluster

A fast way to construct a (pseudo) hadoop distributed cluster with docker. Inspired by [big-data-europe/docker-hadoop](https://github.com/big-data-europe/docker-hadoop).

## Run the Containers

```bash
docker compose up -d
```

## Map Reduce with In-Docker Compile
1. Update yum
2. Install JDK (official image does not have JDK, only JRE)
3. Put the files in the local directory to the container
4. Compile the java files
5. Run the map reduce job while pray to the Big Data God