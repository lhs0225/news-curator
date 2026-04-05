package shlee.news_curator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shlee.news_curator.entity.News;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByCategory(String category);

    List<News> findAllByOrderByCreatedAtDesc();
}
