package com.example.demo.application.job;

import com.example.demo.application.job.param.CreateArticleJobParam;
import com.example.demo.application.model.ArticleModel;
import com.example.demo.domain.entity.Article;
import com.example.demo.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateArticleJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JdbcTemplate demoJdbcTemplate;
    private final EntityManagerFactory demoEntityManagerFactory;

    public CreateArticleJobConfig(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  @Qualifier("demoJdbcTemplate") JdbcTemplate demoJdbcTemplate,
                                  @Qualifier("demoEntityManagerFactory") EntityManagerFactory demoEntityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.demoJdbcTemplate = demoJdbcTemplate;
        this.demoEntityManagerFactory = demoEntityManagerFactory;
    }

    @Bean
    public Job createArticleJob() {
        return jobBuilderFactory.get("createArticleJob")
                .incrementer(new RunIdIncrementer())
                .start(createArticleStep())
                .build();
    }

    @Bean
    public Step createArticleStep() {
        return stepBuilderFactory.get("createArticleStep")
                .<ArticleModel, Article>chunk(10)
                .reader(createArticleReader())
                .processor(createArticleProcessor())
                .writer(createArticleWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<ArticleModel> createArticleReader() {
        log.info("PARAM!!!!!!! {}", createArticleJobParam.getName());
        return new FlatFileItemReaderBuilder<ArticleModel>()
                .name("createArticleReader")
                .resource(new ClassPathResource("Articles.csv"))
                .delimited()
                .names("title", "content")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(ArticleModel.class)
                .build();
    }

    @Bean
    public ItemProcessor<ArticleModel, Article> createArticleProcessor() {
        LocalDateTime now = LocalDateTime.now();
        return articleModel -> Article.builder()
                .title(articleModel.getTitle())
                .content(articleModel.getContent())
                .createdAt(now)
                .build();
    }

    @Bean
    public ItemWriter<Article> createArticleWriter() {
        return articles -> demoJdbcTemplate.batchUpdate("insert into Article (title, content, createdAt) values (?, ?, ?)",
                articles,
                10,
                ((ps, article) -> {
                    ps.setObject(1, article.getTitle());
                    ps.setObject(2, article.getContent());
                    ps.setObject(3, article.getCreatedAt());
                }));
    }

    // Jpa 같은 경우는 entityManagerFactory를 사용
    @Bean
    public ItemWriter<Article> writerByJpa() {
        return new JpaItemWriterBuilder<Article>()
                .entityManagerFactory(this.demoEntityManagerFactory)
                .build();
    }

//    // Jdbc 같은 경우는 dataSource를 사용
//    @Bean
//    public ItemWriter<Article> writerByJdbc() {
//        return new JdbcBatchItemWriterBuilder<Article>()
//                .dataSource(this.demoDataSource)
//                .build();
//    }
}