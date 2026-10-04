package in.ai.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class McpApplication {

	public static void main(String[] args) {
		System.out.println(System.getenv("PATH"));
		SpringApplication.run(McpApplication.class, args);
	}

}
