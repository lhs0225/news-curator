package shlee.news_curator.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import shlee.news_curator.entity.News;
import shlee.news_curator.repository.NewsRepository;

import java.util.List;

@Service
public class NewsService {

    private final ChatClient chatClient;
    private final NewsRepository newsRepository;

    public NewsService(ChatClient.Builder chatClientBuilder, NewsRepository newsRepository) {
        this.chatClient = chatClientBuilder.build();
        this.newsRepository = newsRepository;
    }

    public String summarize(String title) {
        String prompt = """
                다음 뉴스를 3줄 내외로 요약해줘: %s""".formatted(title);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public News saveNews(String title, String summary, String originalUrl,
                         String source, String category) {
        var news = new News();
        news.setTitle(title);
        news.setSummary(summary);
        news.setOriginalUrl(originalUrl);
        news.setSource(source);
        news.setCategory(category);
        return newsRepository.save(news);
    }

    public List<News> findAll() {
        return newsRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<News> findByCategory(String category) {
        return newsRepository.findByCategory(category);
    }
}
