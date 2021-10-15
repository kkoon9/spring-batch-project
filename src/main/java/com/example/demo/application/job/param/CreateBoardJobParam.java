package com.example.demo.application.job.param;

import lombok.Getter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@JobScope
@Getter
public class CreateBoardJobParam {
    private String name2;

    @Value("#{jobParamters[name2]}")
    private void setName(String name2) {
        this.name2 = name2;
    }
}
