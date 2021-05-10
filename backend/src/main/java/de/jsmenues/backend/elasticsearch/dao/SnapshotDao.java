package de.jsmenues.backend.elasticsearch.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.slm.DeleteSnapshotLifecyclePolicyRequest;
import org.elasticsearch.client.slm.GetSnapshotLifecyclePolicyRequest;
import org.elasticsearch.client.slm.GetSnapshotLifecyclePolicyResponse;
import org.elasticsearch.client.slm.PutSnapshotLifecyclePolicyRequest;
import org.elasticsearch.client.slm.SnapshotInvocationRecord;
import org.elasticsearch.client.slm.SnapshotLifecyclePolicy;
import org.elasticsearch.client.slm.SnapshotLifecyclePolicyMetadata;
import org.elasticsearch.client.slm.SnapshotRetentionConfiguration;
import org.elasticsearch.client.slm.StartSLMRequest;
import org.elasticsearch.client.slm.StopSLMRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.repositories.fs.FsRepository;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.snapshots.RestoreInfo;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.elasticsearch.ElasticsearchConnector;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SnapshotDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotDao.class);

    private final ElasticsearchConnector connector;

    @Inject
    public SnapshotDao(ElasticsearchConnector connector) {
        this.connector = connector;
    }

    /**
     * Create repository
     *
     * @return repository is created true or false
     */
    public boolean createRepository() {
        AcknowledgedResponse acknowledgedResponse = null;
        PutRepositoryRequest putRepositoryRequest = new PutRepositoryRequest();
        String locationKey = FsRepository.LOCATION_SETTING.getKey();
        String locationValue = "./backup";
        Settings settings = Settings.builder().put(locationKey, locationValue).build();
        putRepositoryRequest.settings(settings);
        putRepositoryRequest.name("backup");
        putRepositoryRequest.type(FsRepository.TYPE);
        putRepositoryRequest.verify(true);
        try {
            acknowledgedResponse = connector.getClient()
                    .snapshot()
                    .createRepository(putRepositoryRequest, RequestOptions.DEFAULT);
            if (acknowledgedResponse.isAcknowledged()) {
                LOGGER.info("backup repository is created");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " repository isn't created");
        }
        return acknowledgedResponse != null && acknowledgedResponse.isAcknowledged();
    }

    /**
     * Used to know if repository "backup" exist
     *
     * @return ture or false
     */

    public boolean doesBackupRepositoryExist() {
        try {
            GetRepositoriesResponse response;
            GetRepositoriesRequest request = new GetRepositoriesRequest();
            String[] repositories = new String[]{"backup"};
            request.repositories(repositories);
            response = connector.getClient()
                    .snapshot()
                    .getRepository(request, RequestOptions.DEFAULT);
            return response.repositories().get(0).name().equals("backup");
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "somethings wrong with elasticsearch");
        }
        return false;
    }

    /**
     * Create snapshot for history and host indices
     *
     * @param snapshotName The name of the snapshot to create
     * @return snapshot status
     */
    public RestStatus createSnapshot(String snapshotName) {
        try {
            CreateSnapshotRequest snapshotRequest = new CreateSnapshotRequest();
            snapshotRequest.repository("backup");
            snapshotRequest.snapshot(snapshotName);
            snapshotRequest.indices("history*", "host*", "expectedvalues");
            snapshotRequest.partial(true);
            snapshotRequest.waitForCompletion(true);
            CreateSnapshotResponse snapshotResponse = connector.getClient()
                    .snapshot()
                    .create(snapshotRequest, RequestOptions.DEFAULT);
            return snapshotResponse.status();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + snapshotName + " isn't created");
        }
        return null;
    }

    /**
     * Create lifecycle policy
     *
     * @return lifecycle is created true or false
     */
    public boolean createLifecycle() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("history*", Collections.singletonList("idx1"));
            config.put("host*", Collections.singletonList("idx2"));
            config.put("expected*", Collections.singletonList("idx3"));
            SnapshotRetentionConfiguration retention = new SnapshotRetentionConfiguration(TimeValue.timeValueDays(30),
                    1, 30);
            // A snapshot is taken every day at 03:00 Am in Europ/Berlin zone and saved 30
            // days
            SnapshotLifecyclePolicy policy = new SnapshotLifecyclePolicy("00", "snapshot", "0 0 1 * * ?", "backup",
                    config, retention);
            PutSnapshotLifecyclePolicyRequest request = new PutSnapshotLifecyclePolicyRequest(policy);
            org.elasticsearch.client.core.AcknowledgedResponse resp = connector.getClient()
                    .indexLifecycle().putSnapshotLifecyclePolicy(request, RequestOptions.DEFAULT);
            return resp.isAcknowledged();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "somethings wrong with elasticsearch");
        }
        return false;
    }

    /**
     * Used to get name lastSuccess snapshot
     *
     * @return lastSuccess snapshot
     */
    public String getLastSuccessSnapshotName() {
        GetSnapshotLifecyclePolicyRequest getSnapshotLifecyclePolicyRequest = new GetSnapshotLifecyclePolicyRequest(
                "00");
        GetSnapshotLifecyclePolicyResponse getResponse = null;
        try {
            getResponse = connector.getClient().indexLifecycle()
                    .getSnapshotLifecyclePolicy(getSnapshotLifecyclePolicyRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + "somethings wrong with elasticsearch");
        }
        SnapshotLifecyclePolicyMetadata policyMeta = getResponse.getPolicies().get("00");
        SnapshotInvocationRecord lastSuccess = policyMeta.getLastSuccess();

        return lastSuccess.getSnapshotName();
    }

    /**
     * Used to start snapshot lifecycle
     *
     * @return lifecycle is started true or false
     */
    public boolean startLifeCycle() {
        boolean isStarted = false;
        try {
            StartSLMRequest startSLMRequest = new StartSLMRequest();

            org.elasticsearch.client.core.AcknowledgedResponse response = connector.getClient()
                    .indexLifecycle().startSLM(startSLMRequest, RequestOptions.DEFAULT);

            isStarted = response.isAcknowledged();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " somethings wrong with elasticsearch");
        }
        return isStarted;
    }

    /**
     * Used to stop snapshot lifecycle
     *
     * @return lifecycle is stopped true or false
     */
    public boolean stopLifeCycle() {
        try {
            StopSLMRequest stopSLMRequest = new StopSLMRequest();

            org.elasticsearch.client.core.AcknowledgedResponse response = connector.getClient()
                    .indexLifecycle().stopSLM(stopSLMRequest, RequestOptions.DEFAULT);

            return response.isAcknowledged();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " somethings wrong with elasticsearch");
        }
        return false;
    }

    /**
     * Used to delete lifecycle
     *
     * @return lifecycle is deleted true or false
     */
    public boolean deleteLifeCycle(String id) {
        try {
            DeleteSnapshotLifecyclePolicyRequest deleteRequest = new DeleteSnapshotLifecyclePolicyRequest(id);

            org.elasticsearch.client.core.AcknowledgedResponse response = connector.getClient()
                    .indexLifecycle().deleteSnapshotLifecyclePolicy(deleteRequest, RequestOptions.DEFAULT);

           return response.isAcknowledged();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " somethings wrong with elasticsearch");
        }
        return false;
    }

    /**
     * Used to get snapshot info
     *
     * @param snapshotName The name of the snapshot
     * @return snapshot information
     */
    public List<SnapshotInfo> getSnapshot(String snapshotName) {
        try {
            GetSnapshotsRequest getSnapshotsRequest = new GetSnapshotsRequest();
            getSnapshotsRequest.repository("backup");
            String[] snapshots = {snapshotName};
            getSnapshotsRequest.snapshots(snapshots);
            GetSnapshotsResponse getSnapshotsResponse = connector.getClient().snapshot()
                    .get(getSnapshotsRequest, RequestOptions.DEFAULT);
            return getSnapshotsResponse.getSnapshots();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + snapshotName + " isn't exist or somethings wrong with elasticsearch");
        }
        return null;
    }

    /**
     * Restore index from snapshot
     *
     * @param snapshotName
     * @param indexPattern
     * @param indexNewName
     * @return restore information
     */
    public RestoreInfo restoreIndexFromSnapshot(String snapshotName, String indexPattern, String indexNewName) {
        try {
            RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest("backup", snapshotName);
            restoreSnapshotRequest.indices(indexPattern);
            restoreSnapshotRequest.renamePattern(indexPattern + "(.+)");
            restoreSnapshotRequest.renameReplacement(indexNewName + "$1");
            restoreSnapshotRequest.ignoreIndexSettings("index.refresh_interval", "index.search.idle.after");
            restoreSnapshotRequest.waitForCompletion(true);
            RestoreSnapshotResponse restoreSnapshotResponse = connector.getClient().snapshot()
                    .restore(restoreSnapshotRequest, RequestOptions.DEFAULT);
            return restoreSnapshotResponse.getRestoreInfo();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + snapshotName
                    + "isn't exist, somethings wrong with elasticsearch or rename the index");

        }
        return null;
    }

    /**
     * Delete snapshot by name
     *
     * @param snapshotName
     * @return snapshot is deleted?
     */
    public boolean deleteSnapshot(String snapshotName) {
        try {
            DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest("backup");
            deleteSnapshotRequest.snapshots(snapshotName);
            AcknowledgedResponse response = connector.getClient().snapshot()
                    .delete(deleteSnapshotRequest, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + snapshotName + " isn't exist or somethings wrong with elasticsearch");
        }
        return false;
    }
}
