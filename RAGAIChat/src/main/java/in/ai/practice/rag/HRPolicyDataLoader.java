package in.ai.practice.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HRPolicyDataLoader {
    private final VectorStore vectorStore;
    @Value("classpath:/pdf/HR-Handbook.pdf")
    private Resource pdfFile;

    public HRPolicyDataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    //@PostConstruct
    // commented to disable text based HR policy
    public void loadTxtData() {
        List<String> sentences = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("system/hrpolicy.txt");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sentences.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Document> documents = sentences.stream().map(Document::new).collect(Collectors.toList());
        vectorStore.add(documents);
    }

    @PostConstruct
    public void loadPdfData() {
        TikaDocumentReader documentReader = new TikaDocumentReader(pdfFile);
        List<Document> documents= documentReader.get();
        //here we are storing complete document in vector Store, which will increase token usage
        //vectorStore.add(documents);
        TextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(50)
                .withMaxNumChunks(300)
                .build();
        List<Document> textSplittedDocument = textSplitter.split(documents);
        vectorStore.add(textSplittedDocument);
    }
}
