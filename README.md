# Simple Scaling Server
   
   
## Discription    

This is an example of a simple scalable server utilizing a configurable thread pool and Java NIO. The client is used to generate a randomly generated string and sends messages at a set interval to the server. The Server receives the strings from the client and hashes them using the hash function SHA1 and sends the result back to the client to be verified.  

## __Packages__ 
  
  
### client
|Class|Description|
|:-----|:-----------|
|[client](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/client/Client.java)|Client generates and sends a message at a given interval of 8kb, keeping track of a SHA1 hash code to compare with hash returned by server. Basic producer/consumer demonstration|
|[~~writer~~](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/client/Writer.java)|Written to perform the random generated message in a seperate thread from client, but is no longer used|
### operations
|Class|Description|
|:-----|:-----------|
|[MessageHashCode](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/operations/MessageHashCode.java)|Contains a single static method that calculates a message hash code using SHA1|
### pool
|Class|Description|
|:-----|:-----------|
|[ThreadPool](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/pool/ThreadPool.java)|Container for the threads utilizing a BlockingQueue in the resource package|
|[ThreadPoolManager](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/pool/ThreadPoolManager.java)|Distributes tasks to threads in the ThreadPool|
|[ThreadPoolWorker](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/pool/ThreadPoolWorker.java)| Worker thread that excutes tasks given to it from the ThreadPoolManager|
### resource
|Class|Description|
|:-----|:-----------|
|[BlockingLinkedList](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/resource/BlockingLinkedList.java)|not used|
|[BlockingQueue](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/resource/BlockingQueue.java)|Implementation of a blocking queue to hold both workers and tasks|
|[ChangeOps](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/resource/ChangeOps.java)|Class to store channel from a selector and save the new ops to be updated by the server|
|[IndividualClientThroughPut](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/resource/IndividualClientThroughPut.java)|Stores an integer, could easily be removed|
### server
|Class|Description|
|:-----|:-----------|
|[Server](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/server/Server.java)|Server class that monitors for incoming connections and read operations. Passes read operations to the job queue to be handled by the ThreadPoolManager|
### tasks
|Class|Description|
|:-----|:-----------|
|[Shutdown](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/tasks/Shutdown.java)|Poison task to kill the server|
|[Task](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/tasks/Task.java)|Primary task that handles the read, hash and write operation for the current channel. ALso updates the stats of the statistics printer for the 20s Server Message.|
|[TestTask](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/tasks/TestTask.java)| Test task |
### util
|Class|Description|
|:-----|:-----------|
|[StatisticsPrinterClient](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/util/StatisticsPrinterClient.java)|Singleton Thread that keeps track of sent and received messages, printing result every 2 seconds|
|[StatisticsPrinterServer](https://github.com/Abellendir/Scaling-Threadpool-Server/blob/master/src/cs455/scaling/util/StatisticsPrinterServer.java)|Singleton Thread that keeps track of Throughput of server, printing result every 2 seconds|
