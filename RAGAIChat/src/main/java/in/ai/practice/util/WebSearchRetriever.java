package in.ai.practice.util;

import in.ai.practice.client.TavilyApiService;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.List;

public class WebSearchRetriever implements DocumentRetriever {

    private final int RESULT_LIMIT;
    private final int DEFAULT_RESULT_LIMIT = 5;
    private final TavilyApiService tavilyApiService;

    public WebSearchRetriever(int resultLimit, TavilyApiService tavilyApiService) {
        this.RESULT_LIMIT = resultLimit;
        this.tavilyApiService = tavilyApiService;
    }
    @Override
    public List<Document> retrieve(Query query) {
        String q = query.text();
        return tavilyApiService.search(q, RESULT_LIMIT);
    }
}
