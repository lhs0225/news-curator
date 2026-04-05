package shlee.news_curator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shlee.news_curator.service.NewsService;

@Controller
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("newsList", newsService.findAll());
        return "index";
    }

    // HTMX: 폼 제출 → fragment만 반환
    @PostMapping("/news/add")
    public String addNews(@RequestParam String title,
                          @RequestParam(required = false) String originalUrl,
                          @RequestParam(required = false) String source,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String _htmx,
                          Model model) {
        String summary = newsService.summarize(title, originalUrl);
        newsService.saveNews(title, summary, originalUrl, source, category);
        model.addAttribute("newsList", newsService.findAll());
        return "fragments/news-grid :: news-grid";
    }

    // HTMX: 카테고리 필터링 → fragment 반환
    @GetMapping("/news/filter")
    public String filterNews(@RequestParam(required = false) String category,
                             Model model) {
        if (category == null || category.isBlank()) {
            model.addAttribute("newsList", newsService.findAll());
        } else {
            model.addAttribute("newsList", newsService.findByCategory(category));
        }
        return "fragments/news-grid :: news-grid";
    }
}
