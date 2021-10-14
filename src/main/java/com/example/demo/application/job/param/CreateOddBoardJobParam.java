package com.example.demo.application.job.param;

import lombok.Getter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@JobScope
@Getter
public class CreateOddBoardJobParam {
    private long minId;
    private long maxId;

    @Value("#{jobParamters[minId]}")
    private void setMinId(long minId) {
        this.minId = minId;
    }

    @Value("#{jobParamters[maxnId]}")
    private void setMaxId(long maxId) {
        this.maxId = maxId;
    }

}
