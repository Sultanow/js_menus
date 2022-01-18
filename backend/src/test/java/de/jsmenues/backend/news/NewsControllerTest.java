package de.jsmenues.backend.news;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;

public class NewsControllerTest extends JerseyTest {
    private final NewsService service = mock(NewsService.class, RETURNS_DEEP_STUBS);

    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return new ResourceConfig(NewsController.class)
                .register(new AbstractBinder() {
                    @Override
                    public void configure() {
                        bind(service).to(NewsService.class);
                    }
                });
    }

    @Test
    public void getAllNewsWithEmptyList() {
        //given
        when(service.getAllVisibleNews()).thenReturn(Collections.emptySet());
        //when
        Response response = target("/news").request().get();
        //then
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAllNews() {
        //given
        when(service.getAllNews()).thenReturn(Collections.emptySet());
        //when
        Response response = target("/news/all").request().get();
        //then
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getNewsByChannelWithEmptyList() {
        //given
        when(service.getAllNewsByTag(anyString())).thenReturn(Collections.emptySet());
        //when
        Response response = target("/news/tag/test").request().get();
        //then
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(Collections.emptySet(), response.readEntity(new GenericType<Set<NewsItem>>() {
        }));
    }

    @Test
    public void createNewsWithEmptyTitle() {
        //given
        NewsItem item = new NewsItem();
        item.setTitle(null);
        //when
        Response response = target("/news").request().put(Entity.entity(item, MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(400, response.getStatus());
        verify(service, times(0)).saveNews(any(NewsItem.class));
    }

    @Test
    public void createNewsWithTitle() {
        //given
        NewsItem item = new NewsItem();
        item.setTitle("Test");
        when(service.saveNews(any(NewsItem.class))).thenReturn(1);
        //when
        Response response = target("/news")
                .request()
                .put(Entity.entity(item, MediaType.APPLICATION_JSON)
                );
        //then
        Assertions.assertEquals(200, response.getStatus());
        verify(service, times(1)).saveNews(any(NewsItem.class));
    }

    @Test
    public void changeNewsItemWithoutSendingItem() {
        //given
        //when
        Response response = target("/news/4711").request().post(Entity.entity(null, MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void changeNewsItemWithNotExistingId() {
        //given
        NewsItem item = new NewsItem();
        item.setTitle("Test");
        item.setId(4711);
        when(service.changeNewsItem(item)).thenReturn(400);
        //when
        Response response = target("/news/4711").request().post(Entity.entity(item, MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void changeNewsItemWithDifferentId() {
        //given
        NewsItem item = new NewsItem();
        item.setId(1234);
        item.setTitle("Test");
        //when
        Response response = target("/news/4711").request().post(Entity.entity(item, MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void changeNewsItemWithEmptyTitle() {
        //given
        NewsItem item = new NewsItem();
        item.setId(4711);
        item.setTitle("");
        //when
        Response response = target("/news/4711").request().post(Entity.entity(item, MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void changeNewsItemWithCorrectValues() {
        //given
        NewsItem item = new NewsItem();
        item.setId(4711);
        item.setTitle("Test");
        when(service.changeNewsItem(item)).thenReturn(200);
        //when
        Response response = target("/news/4711").request().post(Entity.entity(item, MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void changeNewsItemWithStringAsId() {
        //given
        NewsItem item = new NewsItem();
        //when
        Response response = target("/news/test").request().post(Entity.entity(item, MediaType.APPLICATION_JSON));
        //then
        Assertions.assertEquals(404, response.getStatus());
    }

    @Test
    public void deleteNewsItemExisting() {
        //given
        when(service.deleteNewsItem(4711)).thenReturn(true);
        //when
        Response response = target("/news/4711").request().delete();
        //then
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void deleteNewsItemNotExisting() {
        //given
        when(service.deleteNewsItem(4711)).thenReturn(false);
        //when
        Response response = target("/news/4711").request().delete();
        //then
        Assertions.assertEquals(400, response.getStatus());
    }
}
