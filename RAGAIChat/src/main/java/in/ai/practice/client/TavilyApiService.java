package in.ai.practice.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.document.Document;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

public class TavilyApiService {

    private final RestClient restClient;
    private final String TAVILY_API_BASE_URL = "https://api.tavily.com/search";
    //@Value("${TAVILY_API_KEY}")
    private String API_KEY;

    public TavilyApiService() {
        this.restClient = RestClient.builder().build();
        this.API_KEY = System.getenv("TAVILY_API_KEY");
    }

    public List<Document> search(String query, int resultLimit) {

        TavilyResponsePayload tavilyResponsePayload = restClient
                .post()
                .uri(TAVILY_API_BASE_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(new TavilyRequestPayload(query, "advance", resultLimit))
                .retrieve()
                .body(TavilyResponsePayload.class);
        if(tavilyResponsePayload == null || CollectionUtils.isEmpty(tavilyResponsePayload.results())){
            return List.of();
        }
        List<Document> documents = tavilyResponsePayload.results().stream()
                .map(hit ->
                        Document.builder().text(hit.content())
                                .score(hit.score())
                                .metadata("title", hit.title())
                                .metadata("url",  hit.url())
                                .build()
                ).collect(Collectors.toList());
        return documents;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record TavilyRequestPayload(String query, String searchDepth, int maxResult){}

    record TavilyResponsePayload(List<Hit> results){
        record Hit(String title, String url, double score, String content){}
    }
}

