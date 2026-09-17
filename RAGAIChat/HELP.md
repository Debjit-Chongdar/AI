# RAG with AI CHAT API
## Retrieve Augmentation Generate
* **[ Local/WIKI Document RAG ]**
  1. Read All Document, 
  2. split into small chunks then store all chunks into Vector Storage 
  3. fetch chunks from Vector DB based on query 
  4. pass it with the query to AI 
  5. get the final output. 

* **[ WebSearch RAG ]** {Tavily}
  1. Read from website
  2. pass it with the query to AI
  3. get the final output

## Steps to Local/WIKI Document based RAG
### Install Docker desktop
* create an account by sign in [here](https://www.docker.com/products/docker-desktop/)
* docker.desktop   download   install (using same credential)
* It should run successfully 
### Changes to run the Qdrant DB in Docker
* Lib to connect spring boot and docker

        <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-docker-compose</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
* add **compose.yml**
* Add config for qdrant db in **application.yml** / **properties**
  *     spring.docker.compose.stop.command=down
  *     spring.ai.vectorstore.qdrant.initialize-schema=true
  *     spring.ai.vectorstore.qdrant.host=localhost
  *     spring.ai.vectorstore.qdrant.port=6334
  *     spring.ai.vectorstore.qdrant.collection-name=ragTest    
* Add below dependency to use QDRANT DB as **Vector** Store

  
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-qdrant</artifactId>
    </dependency>
### If we are using Ollama
* Run this cmd 

        ollama pull mxbai-embed-large
### Qdarnt DB Dashboard
http://localhost:6333/dashboard
* Load the data at the very beginning, as it will not change with each request
* Remove the devtool dependency if we are refreshing during debug, otherwise it will append the same data into the vector store 
### PDF, EXCEL document reader

        <dependency>
			<groupId>org.springframework.ai</groupId>
			<artifactId>spring-ai-tika-document-reader</artifactId>
		</dependency>
* load the pdf 
* convert into List<'Document'>
* Using TokenTextSplitter split the document to a smaller chunk
* then add into Vector Store
### Controller
* Create the SearchRequest
* by calling vector store get the Document list
* combine the document text in a single text
* pass this text as system message using template
#### Using "RetrievalAugmentationAdvisor"
* Create VectorStoreDocumentRetriever (VectorStore, topK, SimilarityThreshold)
* pass it into documentRetriever of RetrievalAugmentationAdvisor builder method
* remove all 4 above-mentioned step in controller
### DB Console 
http://localhost:8080/h2-console

SELECT * FROM SPRING_AI_CHAT_MEMORY;
### Steps to follow
* start ollama
* start Docker Desktop
* start the application

WebSearch API key: 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