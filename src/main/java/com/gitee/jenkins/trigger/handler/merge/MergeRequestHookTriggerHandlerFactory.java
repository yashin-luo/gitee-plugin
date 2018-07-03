package com.gitee.jenkins.trigger.handler.merge;

import com.gitee.jenkins.gitee.hook.model.Action;
import com.gitee.jenkins.gitee.hook.model.State;
import com.gitee.jenkins.trigger.TriggerOpenMergeRequest;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Robin Müller
 */
public final class MergeRequestHookTriggerHandlerFactory {

    private MergeRequestHookTriggerHandlerFactory() {}

    public static MergeRequestHookTriggerHandler newMergeRequestHookTriggerHandler(boolean triggerOnMergeRequest,
    		                                                                       boolean triggerOnAcceptedMergeRequest,
    		                                                                       boolean triggerOnClosedMergeRequest,
                                                                                   TriggerOpenMergeRequest triggerOpenMergeRequest,
                                                                                   boolean skipWorkInProgressMergeRequest,
                                                                                   boolean triggerOnApprovedMergeRequest,
                                                                                   boolean cancelPendingBuildsOnUpdate) {
        if (triggerOnMergeRequest || triggerOnAcceptedMergeRequest || triggerOnClosedMergeRequest || triggerOpenMergeRequest != TriggerOpenMergeRequest.never || triggerOnApprovedMergeRequest) {
        	return new MergeRequestHookTriggerHandlerImpl(retrieveAllowedStates(triggerOnMergeRequest, triggerOnAcceptedMergeRequest, triggerOnClosedMergeRequest, triggerOpenMergeRequest), 
            											  retrieveAllowedActions(triggerOnApprovedMergeRequest),
                                                          skipWorkInProgressMergeRequest, cancelPendingBuildsOnUpdate);
        } else {
            return new NopMergeRequestHookTriggerHandler();
        }
    }

	private static Set<Action> retrieveAllowedActions(boolean triggerOnApprovedMergeRequest) {
		Set<Action> allowedActions = EnumSet.of(Action.open, Action.update, Action.close, Action.merge);
		if (triggerOnApprovedMergeRequest)
			allowedActions.add(Action.approved);
		return allowedActions;
	}

	private static List<State> retrieveAllowedStates(boolean triggerOnMergeRequest, 
			                                         boolean triggerOnAcceptedMergeRequest, 
			                                         boolean triggerOnClosedMergeRequest,
			                                         TriggerOpenMergeRequest triggerOpenMergeRequest) {
        List<State> result = new ArrayList<>();
        if (triggerOnMergeRequest) {
            result.add(State.opened);
            result.add(State.open);
            result.add(State.reopened);
        }
        if (triggerOnAcceptedMergeRequest)  {
        	result.add(State.merged);
        }
        if (triggerOnClosedMergeRequest) {
        	result.add(State.closed);
        }
        if (triggerOpenMergeRequest != TriggerOpenMergeRequest.never) {
            result.add(State.updated);
        }
        
        return result;
    }
}
