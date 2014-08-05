Akubra (https://wiki.duraspace.org/display/AKUBRA/Akubra+Project) is a file system abstraction layer
which is used by fedora-commons (http://fedora-commons.org/)

This implementation enables fedora-commons to use an HBase table sitting on a Hadoop filesystem (http://hadoop.apache.org/)
as an underlying object and datastream storage.

akubra-hbase is still in an early development state and in no way ready for production use!

Installation instructions (Fedora Commons 3.7.1, Hadoop 2.3.0, HBase 0.96.1.1):
---------------------------------------------------------------

### Dependencies

Copy the following dependencies to your fedora webapp's WEB-INF/lib directory:

From $HADOOP_HOME/lib/:
+ commons-configuration-1.6.jar
+ commons-lang-2.6.jar
+ guava-11.0.2.jar
+ protobuf-java-2.5.0.jar

From $HADOOP_HOME/:
+ hadoop-auth-2.3.0.jar
+ hadoop-common-2.3.0.jar

From $HBASE_HOME/:
+ hbase-client-0.96.1.1.jar
+ hbase-common-0.96.1.1.jar
+ hbase-protocol-0.96.1.1.jar

From $HBASE_HOME/lib/:
+ htrace-core-2.01.jar
+ netty-3.6.6.Final.jar
+ zookeeper-3.4.5.jar

From target/ (after building the project):
+ akubra-hbase-0.0.1-SNAPSHOT.jar

Remove the following dependencies from your fedora webapp's WEB-INF/lib directory:

- google-collections-1.0.jar


### Configuration

Open the file ```$FEDORA_HOME/server/config/spring/akubra-llstore.xml``` and edit the two beans ```fsObjectStore``` and ```fsDataStreamStore``` to use the class ```eu.scapeproject.akubra.HBaseBlobStore``` and the two beans ```fsObjectStoreMapper``` and ```fsDatastreamStoreMapper``` to be of class ```eu.scapeproject.akubra.HBaseIdMapper```


	<bean name="fsObjectStore" class="eu.scapeproject.akubra.HBaseBlobStore" singleton="true">
		<constructor-arg value="hdfs://localhost:9000/fedora/objects"/>
                <constructor-arg value="myTable"/>
	</bean>
	
	<bean name="fsObjectStoreMapper" class="eu.scapeproject.akubra.HBaseIdMapper" singleton="true">
		<constructor-arg ref="fsObjectStore"/>
	</bean>


	<bean name="fsDatastreamStore" class="eu.scapeproject.akubra.HBaseBlobStore" singleton="true">
		<constructor-arg value="hdfs://localhost:9000/fedora/datastreams"/>
                <constructor-arg value="myTable"/>
	</bean>

	<bean name="fsDatastreamStoreMapper" class="eu.scapeproject.akubra.HBaseIdMapper" singleton="true">
		<constructor-arg ref="fsDatastreamStore"/>
	</bean>


### License

akubra-hbase is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
