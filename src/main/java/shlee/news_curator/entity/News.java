package shlee.news_curator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "news", indexes = {
        @Index(name = "idx_news_category", columnList = "category"),
        @Index(name = "idx_news_published_at", columnList = "publishedAt")
})
@Getter
@Setter
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String summary;

    @Column(length = 2000)
    private String originalUrl;

    private String source;

    private String category;

    private String imageUrl;

    private LocalDateTime publishedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
