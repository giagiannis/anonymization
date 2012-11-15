<h2>Overview</h2>
<b>Anonymization</b> is a project currently developed addressing anonymity of users on large, distributed database systems. The purpose
of the project is to develop a fully or partial distributed system, so as to apply k-anonymity property to relational data, 
stored in a distributed filesystem (like HDFS) or in a NoSQL database (like HBase).

<h2>Centralized Algorithms</h2>
In this project we implement well-known algorithms that execute anonymization (the process with which a table is modified so
as to satisfy k-anonymity property), such as Mondrian. The algorithms that are implemented execute local recoding, but
algorithms of different type may be easily added.

<h2>Partial distributed anonymization</h2>
The target of the partial distributed system is to sort data (using an effient method), create multidimensional cuts on them
in order to get separate partitions and then, execute the centralized algorithms in the nodes of the cluster. For this purpose,
a distributed framework such as Hadoop can be used.

<h2>Fully distributed anonymization</h2>
On the other hand, the target of the fully distributed system is to modify a centralized algorithm in order to work in a
distributed way. In order to achieve that the problem must be parallelized and solved in a distributed manner.    