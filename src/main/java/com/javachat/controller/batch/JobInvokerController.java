package com.javachat.controller.batch;

import javax.servlet.http.HttpServletRequest;

import com.javachat.util.HttpReqRespUtils;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping(value = "/api")
public class JobInvokerController {
    @Value("${chat.batch.token}")
    private String batchToken;

    @Autowired
    JobLauncher jobLauncher;
 
    @Autowired
    Job processJob;
 
    @RequestMapping("/invokejob")
    public String handle(HttpServletRequest request) throws Exception {
        String ipAddress = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        if (!(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1") || ipAddress.equals("0.0.0.0"))){
            return "Illegal request.";
        }
        String queryBatchToken = request.getParameter("batchToken");
        if (!queryBatchToken.equals(this.batchToken)) {
            return "Illegal request.";
        }
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(processJob, jobParameters);
        return "Batch job has been invoked";
    }
}
