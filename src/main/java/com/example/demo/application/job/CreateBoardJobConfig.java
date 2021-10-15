package com.example.demo.application.job;

import com.example.demo.application.model.BoardModel;
import com.example.demo.domain.entity.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
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
import java.time.LocalDateTime;

@Configuration
@Slf4j
public class CreateBoardJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JdbcTemplate demoJdbcTemplate;
    private final EntityManagerFactory demoEntityManagerFactory;

    private static final int CHUNK_SIZE = 10;

    public CreateBoardJobConfig(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  @Qualifier("demoJdbcTemplate") JdbcTemplate demoJdbcTemplate,
                                  @Qualifier("demoEntityManagerFactory") EntityManagerFactory demoEntityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.demoJdbcTemplate = demoJdbcTemplate;
        this.demoEntityManagerFactory = demoEntityManagerFactory;
    }

    @Bean
    public Job createBoardJob() {
        return jobBuilderFactory.get("createBoardJob")
                .incrementer(new RunIdIncrementer())
                .start(createBoardStep())
                .build();
    }

    @Bean
    public Step createBoardStep() {
        return stepBuilderFactory.get("createBoardStep")
                .<BoardModel, Board>chunk(CHUNK_SIZE)
                .reader(createBoardReader())
                .processor(createBoardProcessor())
                .writer(createBoardWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<BoardModel> createBoardReader() {
        return new FlatFileItemReaderBuilder<BoardModel>()
                .name("createBoardReader")
                .resource(new ClassPathResource("Boards.csv"))
                .delimited()
                .names("title", "content")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(BoardModel.class)
                .build();
    }

    @Bean
    public ItemProcessor<BoardModel, Board> createBoardProcessor() {
        return boardModel -> Board.builder()
                .title(boardModel.getTitle())
                .content(boardModel.getContent())
                .build();
    }

    @Bean
    public ItemWriter<Board> createBoardWriter() {
        return boards -> demoJdbcTemplate.batchUpdate("insert into Board (title, content, createdAt) values (?, ?, ?)",
                boards,
                10,
                ((ps, board) -> {
                    ps.setObject(1, board.getTitle());
                    ps.setObject(2, board.getContent());
                    ps.setObject(3, LocalDateTime.now());
                }));
    }
//    // Jdbc 같은 경우는 dataSource를 사용
//    @Bean
//    public ItemWriter<Board> writerByJdbc() {
//        return new JdbcBatchItemWriterBuilder<Board>()
//                .dataSource(this.demoDataSource)
//                .build();
//    }
}
