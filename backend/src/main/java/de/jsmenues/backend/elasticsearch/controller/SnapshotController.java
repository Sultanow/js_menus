package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.client.indexlifecycle.LifecyclePolicyMetadata;
import org.elasticsearch.client.slm.SnapshotInvocationRecord;
import org.elasticsearch.client.slm.SnapshotLifecyclePolicyMetadata;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.snapshots.RestoreInfo;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.dao.SnapshotDao;
import de.jsmenues.backend.zabbixservice.ZabbixElasticsearchSynchronization;

@Path("/elasticsearch")
public class SnapshotController {
    private static Logger LOGGER = LoggerFactory.getLogger(SnapshotController.class);

    /**
     * Create snapshot
     * 
     * @param snapshotName
     * @return snapshot is created true or false
     */
    @PermitAll
    @POST
    @Path("/creatSnapshot")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSnapshot(@QueryParam("snapshotname") String snapshotName) throws IOException {

        RestStatus result = SnapshotDao.creatSnapshot(snapshotName);
        return Response.ok(result.toString()).build();
    }

    /**
     * Get snapshot
     * 
     * @param snapshotName
     * @return list of snapshot info
     */
    @PermitAll
    @POST
    @Path("/getSnapshot")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSnapshotByName(@QueryParam("snapshotname") String snapshotName) throws IOException {

        List<SnapshotInfo> result = SnapshotDao.getSnapshot(snapshotName);

        return Response.ok(result.toString()).build();
    }

    /**
     * Delete snapshot by name
     * 
     * @param snapshotName
     * @return snapshot is deleted true or false
     */
    @PermitAll
    @DELETE
    @Path("/deleteSnapshotByName")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteSnapshot(@QueryParam("snapshotname") String snapshotName) throws IOException {

        boolean result = SnapshotDao.deleteSnapshot(snapshotName);
        String stringResult = String.valueOf(result);
        return Response.ok(stringResult).build();
    }

    /**
     * Restore index by index pattern
     * 
     * @param snapshotName
     * @return restore info
     */
    @PermitAll
    @POST
    @Path("/restoreIndex")
    @Produces(MediaType.TEXT_PLAIN)
    public Response restoreAllIndex(@QueryParam("snapshotname") String snapshotName,
            @QueryParam("indexpattern") String indexPattern) throws IOException {

        RestoreInfo result = SnapshotDao.restoreIndexfromSnapshot(snapshotName, indexPattern);
        String stringResult = String.valueOf(result);
        return Response.ok(stringResult).build();
    }

    /**
     * Create lifecycle to create snapshot in regular period
     * 
     * @return lifecycle is created true or false
     */
    @PermitAll
    @POST
    @Path("/createLifeCycle")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createLifecycle() throws IOException {

        boolean result = SnapshotDao.createLifecycle();
        String stringResult = String.valueOf(result);
        return Response.ok(stringResult).build();
    }

    /**
     * Used to start life ycle
     * 
     * @return lifecycle ist started true or false
     */
    @PermitAll
    @POST
    @Path("/satrtLifeCycle")
    @Produces(MediaType.TEXT_PLAIN)
    public Response startLifecycle() throws IOException {

        boolean result = SnapshotDao.startLifeCycle();
        String stringResult = String.valueOf(result);
        return Response.ok(stringResult).build();
    }

    /**
     * Used to stop lifecycle
     * 
     * @return lifecycle ist stoped true or false
     */
    @PermitAll
    @POST
    @Path("/stopLifeCycle")
    @Produces(MediaType.TEXT_PLAIN)
    public Response stopLifecycle() throws IOException {

        boolean result = SnapshotDao.stopLifeCycle();
        String stringResult = String.valueOf(result);
        return Response.ok(stringResult).build();
    }

    /**
     * Get last success snapshot name
     * 
     * @return snopshot name
     */
    @PermitAll
    @POST
    @Path("/getLastSuccessSnapshotName")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLifecycle() throws IOException {

        String result = SnapshotDao.getLastSuccessSnapshotName();

        return Response.ok(result).build();

    }

    /**
     * Used to know if repository "backup" exist
     * 
     * @return true or false
     */
    @PermitAll
    @POST
    @Path("/ifRepositoryExist")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRepo() throws IOException {

        boolean result = SnapshotDao.ifRepositoryExist();
        String stringResult = String.valueOf(result);
        return Response.ok(stringResult).build();

    }

    /**
     * Used to stop the synchronizatio betwenn zabbix and elasticseach
     * 
     * @return true or false
     */
    @PermitAll
    @POST
    @Path("/stopSynchronization")
    @Produces(MediaType.TEXT_PLAIN)
    public Response stopSynchronization() throws IOException {
        if (ZabbixElasticsearchSynchronization.stopSynchronization) {
            ZabbixElasticsearchSynchronization.stopSynchronization = false;
        } else {
            ZabbixElasticsearchSynchronization.stopSynchronization = true;
        }
        return Response.ok(ZabbixElasticsearchSynchronization.stopSynchronization).build();

    }
}
