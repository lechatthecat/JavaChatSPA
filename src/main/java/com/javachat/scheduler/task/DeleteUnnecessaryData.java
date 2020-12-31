package com.javachat.scheduler.task;

import com.javachat.service.BatchService;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
 
public class DeleteUnnecessaryData implements Tasklet {
    @Autowired
    BatchService batchService;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception 
    {
        System.out.println("Delete Unnecessary Data Job start..");
 
        boolean isSuccess = batchService.deleteUnncessaryData();
        if (!isSuccess) {
            throw new Exception("Batch failed.");
        }
        System.out.println("Delete Unnecessary Data Job done..");
        return RepeatStatus.FINISHED;
    }    
}