package com.javachat.scheduler.task;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
 
public class TaskTwo implements Tasklet {
 
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception 
    {
        System.out.println("MyTaskTwo start..");
 
        // ... your code
         
        System.out.println("MyTaskTwo done..");
        return RepeatStatus.FINISHED;
    }    
}