package com.javachat.config;

import com.javachat.scheduler.task.DeleteUnnecessaryData;
//import com.javachat.scheduler.task.TaskTwo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobs;
 
    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private DeleteUnnecessaryData deleteUnnecessaryData;

    @Bean
    public Step stepOne(){
        return steps.get("stepOne")
                .tasklet(deleteUnnecessaryData)
                .build();
    }
     
    // @Bean
    // public Step stepTwo(){
    //     return steps.get("stepTwo")
    //             .tasklet(new TaskTwo())
    //             .build();
    // }   
     
    @Bean
    public Job deleteUnnecessaryData(){
        return jobs.get("deleteUnnecessaryData")
                .incrementer(new RunIdIncrementer())
                .start(stepOne())
                //.next(stepTwo())
                .build();
    }

    @Bean
    public DeleteUnnecessaryData beanDeleteUnnecessaryData(){
        return new DeleteUnnecessaryData();
    }
}