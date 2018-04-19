package springskeleton.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job unconfirmedUsersCleaningJob() {
        return this.jobBuilderFactory.get("unconfirmedUsersCleaningJob").incrementer(new RunIdIncrementer())
                .start(cleanUnconfirmedUsersStep()).build();
    }

    @Bean
    public Tasklet unconfirmedUsersCleaningTasklet() {
        return new UnconfirmedUsersCleaningTasklet();
    }

    @Bean
    protected Step cleanUnconfirmedUsersStep() {
        return this.stepBuilderFactory.get("cleanUnconfirmedUsersStep").tasklet(unconfirmedUsersCleaningTasklet())
                .allowStartIfComplete(true).build();
    }

}
