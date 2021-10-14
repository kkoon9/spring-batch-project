package com.example.demo.application.job;

import com.example.demo.application.model.BoardModel;
import com.example.demo.domain.entity.Board;
import com.example.demo.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Configuration
@Slf4j
public class CreateOddBoardJobConfig {
    private static final int CHUNK_SIZE = 10;
    private static final int GRID_SIZE = 10;
    private static final int POOL_SIZE = 10;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JdbcTemplate demoJdbcTemplate;


    public CreateOddBoardJobConfig(JobBuilderFactory jobBuilderFactory,
                                   StepBuilderFactory stepBuilderFactory,
                                   @Qualifier("demoJdbcTemplate") JdbcTemplate demoJdbcTemplate) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.demoJdbcTemplate = demoJdbcTemplate;
    }

    @Bean
    public Job createOddBoardJob() throws Exception {
        return jobBuilderFactory.get("createOddBoardJob")
                .start(createOddBoardManager())
                .build();
    }

    @Bean
    public Step createOddBoardManager() {
        return stepBuilderFactory.get("createOddBoardManager")
                .partitioner()
                .partitionHandler()
                .build();
    }

    @Bean
    @StepScope
    public Parti

}
