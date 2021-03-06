# Cloud Bigtable on Managed VM's (Hello World for Cloud Bigtable)

A simple Hello World app that takes your an opaque user ID and uses it as a key to count how often you've
visited.  The app also provides a simple JSON REST client that enables the GET, POST, and DELETE Verbs.

# IMPORTANT – TEMPORARY
# Roll back to this version of the SDK
`gcloud config set --scope=installation component_manager/fixed_sdk_version 0.9.55`

`gcloud components update`

### IMPORTANT - The java samples require that the hbase-bigtable jar be installed in your local maven repository manually:

Download the [Jar](https://storage.googleapis.com/cloud-bigtable/jars/bigtable-hbase/bigtable-hbase-0.1.5.jar)

```mvn install:install-file -Dfile=bigtable-hbase-0.1.5.jar -DgroupId=com.google.cloud.bigtable -DartifactId=bigtable-hbase -Dversion=0.1.5 -Dpackaging=jar -DgeneratePom=true```

## Project setup, installation, and configuration

1. Get a copy of the [AppEngine Runtime for Managed VM's](https://github.com/GoogleCloudPlatform/appengine-java-vm-runtime/)

1. Build the Docker Image -- You will need to do this when the SDK is updated.

  `cd docker; docker build -t appengine-mvn-opensource .` 

1. Go to the [Cloud Console](https://cloud.google.com/console) and create or select your project.

 You will need the ProjectID later.

1. Enable Billing.

1. Select **APIs & Auth > APIs**  

1. Enable the **Cloud Bigtable API** and the **Cloud Bigtable Admin API**

1. Select **APIs & Auth > Credentials**

1. Select **Generate new JSON key**

1. Set the environment variable `GOOGLE_APPLICATION_CREDENTIALS` to point to your json key

 `export GOOGLE_APPLICATION_CREDENTIALS=~/path_to_key.json`

1. Build the Docker Image for this project

 `cd docker; docker build -t gae-bt-v02 .;cd ..`
 
1. Select **Storage > Cloud Bigtable > New Cluster**

  You will need both the Zone and the Unique ID
  
1. Using [gcloud](https://cloud.google.com/sdk/), login.

 `gcloud auth login`
 
1. Follow the instructions (?? WHERE ??) to enable `hbase shell`

1. Launch `hbase shell`

1. Create the table (tableName, Column Family)

 `create table 'gae-hello', 'visits'`
 
 `create table 'from-json', 'cf1', 'cf2', 'cf3', 'cf4'`
 
1. Exit `hbase shell` using ctrl-c


### Running Locally

1. `cd ../helloworld`

1. Set the `project_ID` in `pom.xml`

1. Set `PROJECT_ID`, `CLUSTER_UNIQUE_ID`, and `Zone` (if necessary) in `src/main/java/com/example/bigtable/managedvms/BigtableHelper.java`

1. Copy your keyfile *.json to `src/main/webapp/WEB-INF`

1. In `src/main/webapp/Dockerfile`, add the line 

 `env GOOGLE_APPLICATION_CREDENTIALS=/app/WEB-INF/KEYFILENAME.json`

 Note - this step is only required for running locally in a container.

1. Build the java artifacts
 
 `mvn clean package`

1. run the application

 `mvn clean gcloud:run`
 
1. go to [localhost:8080](localhost:8080)

### Deploying as a managed VM app

1. Set the `project_ID` in `pom.xml`

1. Set `PROJECT_ID`, `CLUSTER_UNIQUE_ID`, and `Zone` (if necessary) in `src/main/java/com/example/bigtable/managedvms/BigtableHelper.java`

1. Deploy the application

 `mvn clean gcloud:deploy`
 
1. go to **ProjectID.appspot.com**

## Using JSON

1. Entities (rows) can be accessed using //projectID.appspot.com/json/rowkey
  * GET - Will wrap up everything as JSON
  * POST - Will convert the JSON to ColumnFamily : Qualifier and write the data
  * DELETE - Will remove the row.

1. `curl -H "Content-Type: application/json" -X POST -d '{"username":"red","id":535}' http://localhost:8080/json/blueword`

1. `curl -X GET http://localhost:8080/json/blueword`

1. `curl -H "Content-Type: application/json" -X DELETE  http://localhost:8080/json/blueword`

## Contributing changes

* See [CONTRIBUTING.md](../../CONTRIBUTING.md)


## Licensing

* See [LICENSE](../../LICENSE)
