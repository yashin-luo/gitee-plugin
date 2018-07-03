package com.gitee.jenkins.gitee.api.impl;

import com.gitee.jenkins.gitee.api.model.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * @author Robin Müller
 * @author Yashin Luo
 *
 */
@Path("/api/v5")
interface GiteeV5ApiProxy extends GiteeApiProxy {
    String ID = "v5";
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/projects/{projectId}/merge_requests/{mergeRequestId}/merge")
    void acceptMergeRequest(@PathParam("projectId") Integer projectId,
                            @PathParam("mergeRequestId") Integer mergeRequestId,
                            @FormParam("merge_commit_message") String mergeCommitMessage,
                            @FormParam("should_remove_source_branch") boolean shouldRemoveSourceBranch);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/repos/{ownerPath}/{repoPath}/pulls/{prNumber}/comments")
    void createMergeRequestNote(@PathParam("ownerPath") String ownerPath,
                                @PathParam("repoPath") String repoPath,
                                @PathParam("prNumber") Integer prNumber,
                                @FormParam("body") String body);

    @HEAD
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    void headCurrentUser();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    User getCurrentUser();

}