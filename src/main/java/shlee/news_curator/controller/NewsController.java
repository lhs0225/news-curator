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

    @PostMapping("/news/add")
    public String addNews(@RequestParam String title,
                          @RequestParam(required = false) String originalUrl,
                          @RequestParam(required = false) String source,
                          @RequestParam(required = false) String category) {
        String summary = newsService.summarize(title);
        newsService.saveNews(title, summary, originalUrl, source, category);
        return "redirect:/";
    }
}
