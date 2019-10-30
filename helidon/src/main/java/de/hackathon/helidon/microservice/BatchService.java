package de.hackathon.helidon.microservice;

import com.google.gson.Gson;
import de.hackathon.redis.data.Batch;
import de.hackathon.redis.repository.ConfigurationRepoStub;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@RequestScoped
public class BatchService {

    @Inject
    private ConfigurationRepoStub repoStub;

    @Path("/createBatch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String createBatch(JsonObject jsonObject) {
        // does not replace existing batches
        Batch batch = new Batch(jsonObject.getString("batchId"), jsonObject.getString("duration"));
        repoStub.storeConfigurationIfAbsent(batch);
        return String.format("Batch %s created", batch.getBatchId());
    }

    @GET
    @Path("/getBatch/{batchId}")
    public String readBatch(@PathParam("batchId") String batchId) {
        Batch batch = repoStub.retrieveConfiguration(batchId);
        return new Gson().toJson(batch);
    }

    @Path("/deleteBatch")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteBatch(String batchId) {
        repoStub.deleteBatch(batchId);
        return String.format("Batch %s deleted", batchId);
    }

}
