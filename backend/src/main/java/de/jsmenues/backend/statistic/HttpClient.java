package de.jsmenues.backend.statistic;

import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;

@Singleton
public class HttpClient {
    Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

    /**
     * Send a file to the python web service and wait for the answer.
     *
     * @param url        URL to access
     * @param fileName   Filename with full path
     * @param fieldName  Additional Field Name
     * @param fieldValue Additional Field Value
     * @return Response Object of the client call to the python service or Response 401 if a exception is thrown.
     */
    Response sendFileDataToPythonService(String url, String fileName, String fieldName, String fieldValue) {
        try (FormDataMultiPart formDataMultiPart = new FormDataMultiPart()) {
            final FileDataBodyPart filePart = new FileDataBodyPart("file", new File(fileName));
            final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field(fieldName, fieldValue).bodyPart(filePart);

            final WebTarget target = client.target(url);
            final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
            multipart.close();

            return response;
        } catch (Exception e) {
            LOGGER.warn("Exception: " + e.getMessage());
            LOGGER.info("Stacktrace: ", e);
            return Response.status(500).build();
        }
    }

}
