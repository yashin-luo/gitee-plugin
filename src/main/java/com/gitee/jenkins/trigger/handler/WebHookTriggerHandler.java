package com.gitee.jenkins.trigger.handler;

import com.gitee.jenkins.gitee.hook.model.WebHook;
import com.gitee.jenkins.trigger.filter.BranchFilter;
import com.gitee.jenkins.trigger.filter.MergeRequestLabelFilter;
import hudson.model.Job;

/**
 * @author Robin Müller
 */
public interface WebHookTriggerHandler<H extends WebHook> {

    void handle(Job<?, ?> job, H hook, boolean ciSkip, BranchFilter branchFilter, MergeRequestLabelFilter mergeRequestLabelFilter);
}
