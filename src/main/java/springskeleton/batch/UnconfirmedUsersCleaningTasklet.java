package springskeleton.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import springskeleton.service.AdminService;

public class UnconfirmedUsersCleaningTasklet implements Tasklet {

    private static final Logger logger = LogManager.getLogger();

    private AdminService adminService;

    @Override
    public RepeatStatus execute(final StepContribution arg0, final ChunkContext arg1) {
        logger.info("Executing daily unconfirmed request tokens cleaning process");
        this.adminService.deleteUnconfirmedUsers();
        this.adminService.deleteUnconfirmedTokens();
        return RepeatStatus.FINISHED;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

}
