package de.jsmenues.backend.elasticsearch.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.snapshots.RestoreInfo;
import org.elasticsearch.snapshots.SnapshotInfo;

import de.jsmenues.backend.elasticsearch.dao.SnapshotDao;
import de.jsmenues.backend.zabbixservice.ZabbixElasticsearchSynchronization;

@Path("/elasticsearch/snapshot")
public class SnapshotController {
    private final SnapshotDao dao;

    @Inject
    public SnapshotController(SnapshotDao dao) {
        this.dao = dao;
    }

    /**
     * Create snapshot
     *
     * @param snapshotName
     * @return snapshot is created true or false
     */
    @PermitAll
    @PUT
    @Path("/{snapshotname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSnapshot(@PathParam("snapshotname") String snapshotName) {
        RestStatus result = dao.createSnapshot(snapshotName);
        return Response.ok(result.toString()).build();
    }

    /**
     * Get snapshot
     *
     * @param snapshotName
     * @return list of snapshot info
     */
    @PermitAll
    @GET
    @Path("/{snapshotname}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSnapshotByName(@PathParam("snapshotname") String snapshotName) {
        List<SnapshotInfo> result = dao.getSnapshot(snapshotName);
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
    @Path("/{snapshotname}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteSnapshot(@PathParam("snapshotname") String snapshotName) {
        boolean result = dao.deleteSnapshot(snapshotName);
        return Response.ok(result).build();
    }

    /**
     * Restore index by index pattern
     *
     * @param snapshotName
     * @return restore info
     */
    @PermitAll
    @GET
    @Path("/restore/{snapshotname}/{indexpattern}/{rename}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response restoreAllIndex(@PathParam("snapshotname") String snapshotName,
                                    @PathParam("indexpattern") String indexPattern,
                                    @PathParam("rename") String rename) {
        RestoreInfo result = dao.restoreIndexFromSnapshot(snapshotName, indexPattern, rename);
        return Response.ok(result).build();
    }

    /**
     * Create lifecycle to create snapshot in regular period
     *
     * @return lifecycle is created true or false
     */
    @PermitAll
    @PUT
    @Path("/lifeCycle")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createLifecycle() {
        boolean result = dao.createLifecycle();
        return Response.ok(result).build();
    }

    /**
     * Used to start life ycle
     *
     * @return lifecycle ist started true or false
     */
    @PermitAll
    @POST
    @Path("/lifeCycle/start")
    @Produces(MediaType.TEXT_PLAIN)
    public Response startLifecycle() {
        boolean result = dao.startLifeCycle();
        return Response.ok(result).build();
    }

    /**
     * Used to stop lifecycle
     *
     * @return lifecycle ist stoped true or false
     */
    @PermitAll
    @POST
    @Path("/lifeCycle/stop")
    @Produces(MediaType.TEXT_PLAIN)
    public Response stopLifecycle() {
        boolean result = dao.stopLifeCycle();
        return Response.ok(result).build();
    }

    /**
     * Get last success snapshot name
     *
     * @return snopshot name
     */
    @PermitAll
    @GET
    @Path("/lastSuccessSnapshotName")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLifecycle() {
        String result = dao.getLastSuccessSnapshotName();
        return Response.ok(result).build();
    }

    /**
     * Used to know if repository "backup" exist
     *
     * @return true or false
     */
    @PermitAll
    @GET
    @Path("/ifRepositoryExist")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRepo() {
        boolean result = dao.doesBackupRepositoryExist();
        return Response.ok(result).build();
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
    // TODO: this actually just toggles and does not stop, is this the wanted behaviour?
    public Response stopSynchronization() {
        ZabbixElasticsearchSynchronization.stopSynchronization = !ZabbixElasticsearchSynchronization.stopSynchronization;
        return Response.ok(ZabbixElasticsearchSynchronization.stopSynchronization).build();

    }
}
