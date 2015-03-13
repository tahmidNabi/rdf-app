# rdf-app
A migration utility for migrating RDF datasets to Neo4j graph database

**Prequisities:**
* Java 1.7 or higher
* Apache Maven
* An up and running instance of Neo4j graph database

**How to build:**

This utility uses Apache Maven. So, just download/clone the repository, open up a terminal/command-line,
traverse to the project directory and run "mvn clean package"

e.g.

<code>
~/projects/rdf-app $ mvn clean package
</code>

**How to run:**

Running the above maven command will produce an executable jar which will be under the "target" folder under the project directory.

To run a migration, follow the following steps:

* Open up a terminal/command-line
* Traverse to $PROJECT_HOME/target (where PROJECT_HOME is the location of the rdf-app)
* Run "java -jar rdf-to-neo4j.jar `<Neo4J database URI>` `<InputFolder>` `<OutputFolder>`"
* After the utility finishes running, the RDF datasets will be present in the Neo4J graph database instance

The `<Neo4J database URI>` is the service root of your up and running Neo4J graph. If you are running a Neo4J instance locally,
the value would be **http://localhost:7474/db/data**

`<InputFolder>` is the folder where your RDF dataset files are located. Note that currently the utility **only** parses
RDF datasets in **XML format**

The utility also writes the generated RDF queries to a text file. `<OutputFolder>` is the location where the utility writes
these files.

e.g.

<code>
~/projects/rdf-app/target $ java -jar rdf-to-neo4j.jar http://localhost:7474/db/data small-bams query
</code>
