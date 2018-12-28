 
Requirements
Docker and Java runtime are required to run the application  (Skip if already installed )
1)Install docker https://docs.docker.com/install/
2)JRE (tested on jdk1.8.0_45.jdk ) 
https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

**Steps to set up and run the application**
1)Run the below command to start the elastic search docker container
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.0.1 &

2) build the application and run it
./gradlew  build -xtest && java -jar build/libs/news-scrapper-0.1.0.jar  
Alternatively import it in an IDE and run com.handzap.assignment.scrapper.Application

The test com.handzap.assignment.scrapper.ApplicationFunctionalTest contains test for scenarios given in the 
assignment .It can be run after the application is up.
The test gets data from the newspaper archive for few articles and tests the search scenarios given below 


Set up data ,
This is not needed if com.handzap.assignment.scrapper.ApplicationFunctionalTest is already run ,the test
sets up data on start up
run the below command
curl -X POST "localhost:8080/v1/site" -H 'Content-Type: application/json' -d'{"baseUrl":"https://www.thehindu.com/archive/web","maxRecords":10}'

This will scrape the site data upto a maximum of maxRecords ,if specified.
If maxRecords is not specified ,it will scrape all articles from archive
The records can be searched in the elastic search REST API available at  
http://localhost:9200/news/_search

List of application APIs
1)Find all authors
example http://localhost:8080/v1/articles/authors

2)Find by author
exampl http://localhost:8080/v1/articles?author=John%20Ashbourne

3)Find by Title and Description
exampl http://localhost:8080/v1/articles?description=A%20clove%20of%20garlic%20a%20day%20keeps%20swine&title=Homilies%20for%20swine%20flu

4)Find by date
exampl http://localhost:8080/v1/articles?fromDate=2009-08-15&toDate=2009-08-16
or date can be specified with time
http://localhost:8080/v1/articles?fromDate=2009-08-15T18:32:09&toDate=2009-08-15T18:35:09
this dates without a timezone are considered in default timezone +05:30 ,the timezone can be specified in the url
but it has to be encoded due to + in the url by (replacing + with %2B)
http://localhost:8080/v1/articles?fromDate=2009-08-15T18:32:09&toDate=2009-08-15T18:35:09%2B05:30
Alternatively the POST API can be used to specify the search fields in request body as json

5) Find by City
City data is not available in all articles ,the articles which have a City can be searched by this field
example http://localhost:8080/v1/articles?city=Bangalore

6) Find by Category
example http://localhost:8080/v1/articles/?category=Bengaluru


The search fields can be specified with other search fields together
example http://localhost:8080/v1/articles/?author=John%20Ashbourne&title=Why%20triage%20will%20not%20be

What is not yet implemented
Pagination is not yet implemented and the number of search results are not limited and only requirement is 
specifying one search parameter

Other notes :
The application uses jsoup HTML parser to extract news archives and changes to site may require fixes to the
data extractor
