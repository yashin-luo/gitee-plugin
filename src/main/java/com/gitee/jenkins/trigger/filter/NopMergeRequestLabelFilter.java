package com.gitee.jenkins.trigger.filter;

import java.util.Collection;

/**
 * @author Robin Müller
 */
class NopMergeRequestLabelFilter implements MergeRequestLabelFilter {
    @Override
    public boolean isMergeRequestAllowed(Collection<String> labels) {
        return true;
    }
}
