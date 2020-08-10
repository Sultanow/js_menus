package de.jsmenues.backend.news;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.ConfigurationRepositoryMock;
import de.jsmenues.redis.repository.IConfigurationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NewsServiceTest {

    private final NewsService service = new NewsService();
    private final IConfigurationRepository repoMock = new ConfigurationRepositoryMock();
    private final Gson gson = new Gson();

    @BeforeEach
    public void setUp() {
        setRedisMock(repoMock);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        Field instance = ConfigurationRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void getAllVisibleNews() {
        //given
        initDefaultNews();
        //when
        Set<NewsItem> items = service.getAllVisibleNews();
        //then
        assertEquals(1, items.size());
    }

    @Test
    void getAllVisibleNewsWithNothingSaved() {
        //given
        //when
        Set<NewsItem> items = service.getAllVisibleNews();
        //then
        assertTrue(items.isEmpty());
    }

    @Test
    void getAllNews() {
        //given
        initDefaultNews();
        //when
        Set<NewsItem> items = service.getAllNews();
        //then
        assertEquals(2, items.size());
    }

    @Test
    void getAllNewsWithNothingSaved() {
        //given
        //when
        Set<NewsItem> items = service.getAllNews();
        //then
        assertTrue(items.isEmpty());
    }

    @Test
    void getAllNewsByTagWithBlankTag() {
        //given
        initDefaultNews();
        //when
        Set<NewsItem> items = service.getAllNewsByTag("");
        //then
        assertTrue(items.isEmpty());
    }

    @Test
    void getAllNewsByTagWithNotExistingTag() {
        //given
        initDefaultNews();
        //when
        Set<NewsItem> items = service.getAllNewsByTag("tag3");
        //then
        assertTrue(items.isEmpty());
    }

    @Test
    void getAllNewsByTagWithExistingTag() {
        //given
        initDefaultNews();
        //when
        Set<NewsItem> items = service.getAllNewsByTag("tag1");
        //then
        assertEquals(1, items.size());
    }

    @Test
    void getAllNewsByTagWithExistingTagUppercase() {
        //given
        initDefaultNews();
        //when
        Set<NewsItem> items = service.getAllNewsByTag("TAG1");
        //then
        assertEquals(1, items.size());
    }

    @Test
    void saveNewsWithCorrectNewsItem() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test").title("Test Title").visible(true).date("2020-08-10").priority("normal").build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
    }

    @Test
    void saveNewsWithIncorrectPriority() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test").title("Test Title").visible(true).date("2020-08-10").priority("default").build();
        //when
        int result = service.saveNews(item);
        //then
        //The Priority should be set to normal
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        NewsItem repoItem = getItemByIdFromRepoMock(result);
        assert repoItem != null;
        assertEquals("normal", repoItem.getPriority());
    }

    @Test
    void saveNewsWithIncorrectDate() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test").title("Test Title").visible(true).date("2020-08-44").priority("default").build();
        //when
        int result = service.saveNews(item);
        //then
        //The date should be set to the date of today.
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        NewsItem repoItem = getItemByIdFromRepoMock(result);
        assert repoItem != null;
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertEquals(today, repoItem.getDate());
    }

    @Test
    void saveNewsWithExistingId() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test").title("Test Title").visible(true).date("2020-08-44").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        //A new Id should be given to the item
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        NewsItem repoItem = getItemByIdFromRepoMock(result);
        assert repoItem != null;
        assertNotEquals(1, repoItem.getId());
    }

    @Test
    void saveNewsWithTagAndExistingChannel() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test #tag1").title("Test Title").visible(true).date("2020-08-10").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        Set<NewsItem> items = service.getAllNewsByTag("tag1");
        assertEquals(2, items.size());
    }

    @Test
    void saveNewsWithTagAndNewChannel() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test #newChannel").title("Test Title").visible(true).date("2020-08-10").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        Set<NewsItem> items = service.getAllNewsByTag("newChannel");
        assertEquals(1, items.size());
    }

    @Test
    void saveNewsWithTagAndExistingChannelUpperCase() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test #TAG1").title("Test Title").visible(true).date("2020-08-10").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        Set<NewsItem> items = service.getAllNewsByTag("tag1");
        assertEquals(2, items.size());
    }

    @Test
    void saveNewsWithMultipleTagsSeparatedWithBlank() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test #tag1 #tagTest").title("Test Title").visible(true).date("2020-08-10").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        Set<NewsItem> itemsTag1 = service.getAllNewsByTag("tag1");
        assertEquals(2, itemsTag1.size());
        Set<NewsItem> itemsTagTest = service.getAllNewsByTag("tagTest");
        assertEquals(1, itemsTagTest.size());
    }

    @Test
    void saveNewsWithMultipleTagsWithoutSeparator() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test #tag1#tagTest").title("Test Title").visible(true).date("2020-08-10").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        Set<NewsItem> itemsTag1 = service.getAllNewsByTag("tag1");
        assertEquals(2, itemsTag1.size());
        Set<NewsItem> itemsTagTest = service.getAllNewsByTag("tagTest");
        assertEquals(1, itemsTagTest.size());
    }

    @Test
    void saveNewsWithMultipleTagsWithoutSeparatorWithSpecialCharAtEnd() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test #tag1#tagTest.").title("Test Title").visible(true).date("2020-08-10").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        Set<NewsItem> itemsTag1 = service.getAllNewsByTag("tag1");
        assertEquals(2, itemsTag1.size());
        Set<NewsItem> itemsTagTest = service.getAllNewsByTag("tagTest");
        assertEquals(1, itemsTagTest.size());
    }

    @Test
    void saveNewsWithMultipleTagsSeparatedWithBlankWithSpecialCharAtEnd() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().text("test #tag1. #tagTest.").title("Test Title").visible(true).date("2020-08-10").priority("default").id(1).build();
        //when
        int result = service.saveNews(item);
        //then
        assertTrue(result > 0);
        assertEquals(3, service.getAllNews().size());
        Set<NewsItem> itemsTag1 = service.getAllNewsByTag("tag1");
        assertEquals(2, itemsTag1.size());
        Set<NewsItem> itemsTagTest = service.getAllNewsByTag("tagTest");
        assertEquals(1, itemsTagTest.size());
    }

    @Test
    void saveNewsFirstNewsItem() {
        //given
        NewsItem item = new NewsItemBuilder().title("test").build();
        //when
        int result = service.saveNews(item);
        //then
        assertEquals(1, result);
        assertEquals("1", repoMock.getVal("news.lastId"));
    }

    @Test
    void deleteNewsWithExistingId() {
        //given
        initDefaultNews();
        //when
        int result = service.deleteNewsItem(1);
        //then
        assertEquals(200, result);
        assertEquals(1, service.getAllNews().size());
        assertTrue(service.getAllNewsByTag("tag1").isEmpty());
    }

    @Test
    void deleteNewsWithNotExistingId() {
        //given
        initDefaultNews();
        //when
        int result = service.deleteNewsItem(4711);
        //then
        assertEquals(400, result);
    }

    @Test
    void changeNewsWithEmptyTitle() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().title("").id(1).build();
        //when
        int result = service.changeNewsItem(item);
        //then
        assertEquals(400, result);
    }

    @Test
    void changeNewsWithNotExistingId() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().title("Test").id(4711).build();
        //when
        int result = service.changeNewsItem(item);
        //then
        assertEquals(400, result);
    }

    @Test
    void changeNewsWithChangedTags() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().title("Test1").id(1).text("#tagNew").visible(true).priority("normal").build();
        //when
        int result = service.changeNewsItem(item);
        //then
        assertEquals(200, result);
    }

    @Test
    void changeNewsWithInvalidDate() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().title("Test1").id(1).text("#tag1").date("43-532-22").build();
        //when
        int result = service.changeNewsItem(item);
        //then
        assertEquals(200, result);
        NewsItem itemDb = getItemByIdFromRepoMock(1);
        assert itemDb != null;
        assertNotEquals(item.getDate(), itemDb.getDate());
    }

    @Test
    void changeNewsWithoutDate() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().title("Test1").id(1).text("#tag1").build();
        //when
        int result = service.changeNewsItem(item);
        //then
        assertEquals(200, result);
        NewsItem itemDb = getItemByIdFromRepoMock(1);
        assert itemDb != null;
        assertEquals(item.getDate(), itemDb.getDate());
    }

    @Test
    void changeNewsWithCorrectdDate() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().title("Test1").id(1).text("#tag1").date("2019-12-21").build();
        //when
        int result = service.changeNewsItem(item);
        //then
        assertEquals(200, result);
        NewsItem itemDb = getItemByIdFromRepoMock(1);
        assert itemDb != null;
        assertEquals(item.getDate(), itemDb.getDate());
    }

    @Test
    void changeNewsWithChangedTitle() {
        //given
        initDefaultNews();
        NewsItem item = new NewsItemBuilder().title("Test6").id(1).text("#tag1").build();
        //when
        int result = service.changeNewsItem(item);
        //then
        assertEquals(200, result);
        NewsItem item_db = getItemByIdFromRepoMock(1);
        assert item_db != null;
        assertEquals(item.getTitle(), item_db.getTitle());
    }

    private void initDefaultNews() {
        String item1 = gson.toJson(new NewsItemBuilder().id(1).title("Test1").text("#tag1").build(), NewsItem.class);
        String item2 = gson.toJson(new NewsItemBuilder().id(2).title("Test2").text("#tag2").visible(false).build(), NewsItem.class);
        repoMock.save("news.item.1", item1);
        repoMock.save("news.item.2", item2);

        String tag1 = gson.toJson(Set.of("1"));
        String tag2 = gson.toJson(Set.of("2"));
        repoMock.save("news.channel.tag1", tag1);
        repoMock.save("news.channel.tag2", tag2);
        repoMock.save("news.lastId", "2");
    }

    private NewsItem getItemByIdFromRepoMock(int id) {
        String val = repoMock.getVal("news.item." + id);
        if (val == null || val.isEmpty())
            return null;
        return gson.fromJson(val, new TypeToken<NewsItem>() {
        }.getType());
    }

    private void setRedisMock(IConfigurationRepository mock) {
        try {
            Field instance = ConfigurationRepository.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
