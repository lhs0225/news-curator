package shlee.news_curator.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import shlee.news_curator.entity.News;
import shlee.news_curator.repository.NewsRepository;

import java.util.List;

@Service
public class NewsService {

    private final ChatClient chatClient;
    private final NewsRepository newsRepository;

    // 뉴스 본문을 찾기 위한 CSS selector 우선순위
    private static final String[] CONTENT_SELECTORS = {
        // 1. 한국형 구체적 ID/클래스 (네이버, 다음 및 주요 언론사)
        "#articleBodyContents",    // 네이버 뉴스
        "#newsEndContents",        // 네이버 TV/연예
        "#articleBody",            // 주요 일간지
        "#harmonyContainer",       // 카카오/다음 뉴스
        ".article_view",           // 카카오/다음 뉴스
        ".article_body",           // 중앙일보 등 다수
        ".news_body",              // 일반적인 뉴스 본문 클래스

        // 2. 해외형 구체적 클래스 (NYT, WSJ, CNN 등)
        ".StoryBodyCompanionColumn", // NYT
        ".article__content",         // CNN
        ".caas-body",               // Yahoo News
        ".post-content",            // 워드프레스 기반 뉴스
        ".article-content",         // 일반적인 해외 뉴스

        // 3. 범용 시맨틱 태그 및 ARIA 역할 (가장 넓은 범위)
        "article",                  // HTML5 표준
        "[role='main'] article",    // 접근성 가이드 준수 사이트
        "main"                      // 메인 콘텐츠 영역
    };

    public NewsService(ChatClient.Builder chatClientBuilder, NewsRepository newsRepository) {
        this.chatClient = chatClientBuilder.build();
        this.newsRepository = newsRepository;
    }

    /**
     * URL이 있으면 본문을 크롤링해서 요약, 없으면 제목 기반 요약
     */
    public String summarize(String title, String url) {
        String content = fetchArticleContent(url);

        String prompt;
        if (content != null && !content.isBlank()) {
            prompt = """
                    다음 뉴스 기사를 3줄 내외로 핵심만 요약해줘.

                    제목: %s
                    본문: %s""".formatted(title, truncate(content, 3000));
        } else {
            prompt = """
                    다음 뉴스를 3줄 내외로 요약해줘: %s""".formatted(title);
        }

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * URL에서 뉴스 본문 텍스트를 추출
     */
    private String fetchArticleContent(String url) {
        if (url == null || url.isBlank()) return null;

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10_000)
                    .get();

            // 우선순위대로 selector를 시도
            for (String selector : CONTENT_SELECTORS) {
                Element found = doc.selectFirst(selector);
                if (found != null && !found.text().isBlank()) {
                    return found.text();
                }
            }

            // 모든 selector 실패 시 <p> 태그들을 모아서 반환
            String fallback = doc.select("p").text();
            return fallback.isBlank() ? null : fallback;
        } catch (Exception e) {
            return null;
        }
    }

    private String truncate(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
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
