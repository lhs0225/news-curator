package shlee.news_curator.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shlee.news_curator.repository.NewsRepository;

@Component
public class DatabaseCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(DatabaseCleanupScheduler.class);

    private final NewsRepository newsRepository;

    public DatabaseCleanupScheduler(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * 매일 KST 00:00에 뉴스 데이터를 전체 초기화한다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void cleanupDaily() {
        long count = newsRepository.count();
        newsRepository.deleteAllInBatch();
        log.info("일일 DB 초기화 완료: {}건 삭제됨", count);
    }
}
