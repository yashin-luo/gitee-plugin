package com.gitee.jenkins.workflow;

import static com.gitee.jenkins.connection.GiteeConnectionProperty.getClient;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.ExportedBean;

import com.gitee.jenkins.cause.GiteeWebHookCause;
import com.gitee.jenkins.gitee.api.GiteeClient;
import com.gitee.jenkins.gitee.api.model.MergeRequest;
import com.google.common.collect.ImmutableSet;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;

/**
 * @author <a href="mailto:robin.mueller@1und1.de">Robin Müller</a>
 */
@ExportedBean
public class AddGiteeMergeRequestCommentStep extends Step {

    private static final Logger LOGGER = Logger.getLogger(AddGiteeMergeRequestCommentStep.class.getName());

    private String comment;

    @DataBoundConstructor
    public AddGiteeMergeRequestCommentStep(String comment) {
        this.comment = StringUtils.isEmpty(comment) ? null : comment;
    }

	@Override
	public StepExecution start(StepContext context) throws Exception {
		return new AddGiteeMergeRequestCommentStepExecution(context, this);
	}
	
    public String getComment() {
        return comment;
    }

    @DataBoundSetter
    public void setComment(String comment) {
        this.comment = StringUtils.isEmpty(comment) ? null : comment;
    }

    public static class AddGiteeMergeRequestCommentStepExecution extends AbstractSynchronousStepExecution<Void> {
        private static final long serialVersionUID = 1;

        private final transient Run<?, ?> run;

        private final transient AddGiteeMergeRequestCommentStep step;

        AddGiteeMergeRequestCommentStepExecution(StepContext context, AddGiteeMergeRequestCommentStep step) throws Exception {
            super(context);
            this.step = step;
            run = context.get(Run.class);
        }
        
        @Override
        protected Void run() throws Exception {
            GiteeWebHookCause cause = run.getCause(GiteeWebHookCause.class);
            if (cause != null) {
                MergeRequest mergeRequest = cause.getData().getMergeRequest();
                if (mergeRequest != null) {
                    GiteeClient client = getClient(run);
                    if (client == null) {
                        println("No Gitee connection configured");
                    } else {
                        try {
                            client.createMergeRequestNote(mergeRequest, step.getComment());
                        } catch (WebApplicationException | ProcessingException e) {
                            printf("Failed to add comment on Merge Request for project '%s': %s%n", mergeRequest.getProjectId(), e.getMessage());
                            LOGGER.log(Level.SEVERE, String.format("Failed to add comment on Merge Request for project '%s'", mergeRequest.getProjectId()), e);
                        }
                    }
                }
            }
            return null;
        }

        private void println(String message) {
            TaskListener listener = getTaskListener();
            if (listener == null) {
                LOGGER.log(Level.FINE, "failed to print message {0} due to null TaskListener", message);
            } else {
                listener.getLogger().println(message);
            }
        }

        private void printf(String message, Object... args) {
            TaskListener listener = getTaskListener();
            if (listener == null) {
                LOGGER.log(Level.FINE, "failed to print message {0} due to null TaskListener", String.format(message, args));
            } else {
                listener.getLogger().printf(message, args);
            }
        }

        private TaskListener getTaskListener() {
            StepContext context = getContext();
            if (!context.isReady()) {
                return null;
            }
            try {
                return context.get(TaskListener.class);
            } catch (Exception x) {
                return null;
            }
        }
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {

        @Override
        public String getDisplayName() {
            return "Add comment on Gitee Merge Request";
        }

        @Override
        public String getFunctionName() {
            return "addGiteeMRComment";
        }
        
		@Override
		public Set<Class<?>> getRequiredContext() {
			return ImmutableSet.of(TaskListener.class, Run.class);
		}
    }
}
