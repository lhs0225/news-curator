package shlee.news_curator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shlee.news_curator.entity.News;

public interface NewsRepository extends JpaRepository<News, Long> {
}
