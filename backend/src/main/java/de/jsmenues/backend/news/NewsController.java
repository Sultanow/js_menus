package de.jsmenues.backend.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/news")
public class NewsController {
    private final NewsService newsService;
    private final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Inject
    public NewsController(NewsService service) {
        newsService = service;
    }

    @PermitAll
    @GET
    public Response getAllVisibleNews() {
        Set<NewsItem> news = newsService.getAllVisibleNews();
        return Response.ok(news).build();
    }

    @PermitAll
    @GET
    @Path("/all")
    public Response getAllNews() {
        Set<NewsItem> news = newsService.getAllNews();
        return Response.ok(news).build();
    }

    @PermitAll
    @GET
    @Path("/tag/{tag}")
    public Response getNewsByTag(@PathParam("tag") String tag) {
        Set<NewsItem> news = newsService.getAllNewsByTag(tag);
        return Response.ok(news).build();
    }

    @PermitAll
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNewsItem(NewsItem item) {
        logger.warn(item.toString());
        if (item.getTitle() == null || item.getTitle().isEmpty()) {
            return Response.status(400, "No Title given").build();
        }
        int success = newsService.saveNews(item);
        return Response.ok(success).build();
    }

    @PermitAll
    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeNewsItem(@PathParam("id") int id, NewsItem item) {
        if (item == null || id != item.getId() || item.getTitle() == null || item.getTitle().isEmpty()) {
            return Response.status(400, "Malformed user input").build();
        }
        int result = newsService.changeNewsItem(item);
        if(result == 200) {
            return Response.ok().build();
        } else {
            return Response.status(result).build();
        }
    }

    @PermitAll
    @DELETE
    @Path("{id}")
    public Response deleteNewsItem(@PathParam("id") int id) {
        boolean success = newsService.deleteNewsItem(id);
        return Response.status(success ? 200 : 400).build();
    }
}

