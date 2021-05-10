package de.jsmenues.backend.statistic;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class HttpClientTest extends JerseyTest {
    private HttpServer server;
    private static final String BASE_URI = "http://localhost:8081/test";
    private final HttpClient client = new HttpClient();

    @TempDir
    Path tempDir;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI));
        server.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override
            public void service(Request request, Response response) {
                response.setStatus(200);
            }
        });
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        server.shutdown();
        super.tearDown();
    }

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return new ResourceConfig(HttpClient.class).register(MultiPartFeature.class);
    }

    @Test
    void sendFileDataToPythonService() throws IOException {
        //given
        Path filePath = tempDir.resolve("test.txt");
        Files.write(filePath, Arrays.asList("1","2"));
        javax.ws.rs.core.Response response;
        //when
        response = client.sendFileDataToPythonService(BASE_URI,filePath.toFile().getAbsolutePath(), "", "");
        //then
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void sendFileDataToPythonServiceWithNotExistingFile() {
        //given
        javax.ws.rs.core.Response response;
        //when
        response =  client.sendFileDataToPythonService(BASE_URI, "notExisitingFile.txt", "", "");
        //then
        Assertions.assertEquals(500, response.getStatus());
    }
}
