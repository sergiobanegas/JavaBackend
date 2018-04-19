package springskeleton.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TasksScheduler {

    private static final Logger logger = LogManager.getLogger();

    private final JobLauncher jobLauncher;

    private final Job job;

    @Autowired
    public TasksScheduler(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @Scheduled(cron = "${spring.batch.delays.daily}")
    public void run() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        final JobExecution execution = this.jobLauncher.run(job,
                new JobParametersBuilder().addLong("uniqueness", System.nanoTime()).toJobParameters());
        logger.info("Unconfirmed request tokens cleaning process finished with status : " + execution.getStatus());
    }

}
