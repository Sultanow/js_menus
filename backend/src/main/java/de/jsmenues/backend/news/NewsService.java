package de.jsmenues.backend.news;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class NewsService {
    private final Gson gson = new Gson();

    public Set<NewsItem> getAllVisibleNews() {
        Map<String, String> allNews = getAllNewsFromDb();
        Set<NewsItem> news = new HashSet<>();
        if (!allNews.isEmpty()) {
            allNews.forEach((key, val) -> {
                NewsItem newsItem = gson.fromJson(val, new TypeToken<NewsItem>() {
                }.getType());
                if (newsItem.isVisible())
                    news.add(newsItem);
            });
        }
        return news;
    }

    public Set<NewsItem> getAllNews() {
        Map<String, String> allNews = getAllNewsFromDb();
        Set<NewsItem> news = new HashSet<>();
        if (!allNews.isEmpty()) {
            allNews.forEach((key, val) -> {
                NewsItem newsItem = gson.fromJson(val, new TypeToken<NewsItem>() {
                }.getType());
                news.add(newsItem);
            });
        }
        return news;
    }

    private Map<String, String> getAllNewsFromDb() {
        return ConfigurationRepository.getRepo().getAllByPattern("news.item");
    }

    public Set<NewsItem> getAllNewsByTag(String tag) {
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        String channelVal = repo.getVal("news.channel." + tag.toLowerCase());
        Set<NewsItem> news = new HashSet<>();
        if (channelVal == null || !channelVal.isEmpty()) {
            Set<String> keys = gson.fromJson(channelVal, new TypeToken<Set<String>>() {
            }.getType());
            if (keys != null && !keys.isEmpty()) {
                keys.forEach(key -> {
                    String item = repo.getVal("news.item." + key);
                    NewsItem newsItem = gson.fromJson(item, new TypeToken<NewsItem>() {
                    }.getType());
                    news.add(newsItem);
                });
            }
        }
        return news;
    }

    public int saveNews(NewsItem item) {
        if (null == item || null == item.getTitle() || item.getTitle().isEmpty()) {
            return -1;
        }
        item.validatePriority();
        item.validateDate();
        int newId = getLastNewsId() + 1;
        item.setId(newId);
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        repo.save("news.lastId", Integer.toString(newId));
        repo.save("news.item." + newId, gson.toJson(item, NewsItem.class));
        Set<String> tags = findAllTagsInText(item.getText());
        addNewsToTagList(newId, tags);
        return newId;
    }

    private Set<String> findAllTagsInText(String text) {
        Set<String> tags = new HashSet<>();
        Set<String> words = Set.of(text.toLowerCase().split(" "));
        words.forEach(word -> {
            if (word.contains("#")) {
                String[] tagList = word.split("#");
                for (int i = 0; i < tagList.length; ++i) {
                    tagList[i] = tagList[i].replaceAll("[-+.^:,]$", "");
                }
                //TODO remove special Chars at the end
                if (!word.matches("^#.*")) {
                    tagList = Arrays.copyOfRange(tagList, 1, tagList.length - 1);
                }
                tags.addAll(Set.of(tagList));

            }
        });
        return tags.stream().filter(e -> !e.isEmpty()).collect(Collectors.toSet());
    }

    private int getLastNewsId() {
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        String id = repo.getVal("news.lastId");
        if (id == null || id.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(id);
        }
    }

    public int changeNewsItem(NewsItem item) {
        if (null == item || null == item.getTitle() || item.getTitle().isEmpty())
            return 400;
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        String json = repo.getVal("news.item." + item.getId());
        if (null == json || json.isEmpty())
            return 400;
        NewsItem itemDb = gson.fromJson(json, new TypeToken<NewsItem>() {
        }.getType());
        if (!item.getText().equals(itemDb.getText())) {
            Set<String> newTags = findAllTagsInText(item.getText());
            Set<String> oldTags = findAllTagsInText(itemDb.getText());
            deleteNewsFromTagList(item.getId(), oldTags);
            addNewsToTagList(item.getId(), newTags);
            itemDb.setText(item.getText());
        }

        if (!item.getTitle().equals(itemDb.getTitle())) {
            itemDb.setTitle(item.getTitle());
        }

        if (!item.getPriority().equals(itemDb.getPriority()) && item.isValidPriority()) {
            itemDb.setPriority(item.getPriority());
        }

        if (!item.getDate().equals(itemDb.getDate()) && item.isVaildDate()) {
            itemDb.setDate(item.getDate());
        }

        if (item.isVisible() != itemDb.isVisible()) {
            itemDb.setVisible(item.isVisible());
        }
        repo.save("news.item." + itemDb.getId(), gson.toJson(itemDb, NewsItem.class));
        return 200;
    }

    public int deleteNewsItem(int id) {
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        String item = repo.getVal("news.item." + id);
        if (null == item || item.isEmpty()) {
            return 400;
        }
        NewsItem news = gson.fromJson(item, new TypeToken<NewsItem>() {
        }.getType());
        Set<String> tags = findAllTagsInText(news.getText());
        repo.delete("news.item." + id);
        deleteNewsFromTagList(id, tags);
        return 200;
    }

    private void deleteNewsFromTagList(int id, Set<String> tags) {
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        tags.forEach(tag -> {
            Set<String> keys = gson.fromJson(repo.getVal("news.channel." + tag), new TypeToken<Set<String>>() {
            }.getType());
            keys.remove(Integer.toString(id));
            repo.save("news.channel." + tag, gson.toJson(keys));
        });
    }

    private void addNewsToTagList(int id, Set<String> tags) {
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        tags.forEach(tag -> {
            String channel = repo.getVal("news.channel." + tag);
            Set<String> channelVal;
            if (null == channel || channel.isEmpty()) {
                channelVal = new HashSet<>();
            } else {
                channelVal = gson.fromJson(channel, new TypeToken<Set<String>>() {
                }.getType());
            }
            channelVal.add(Integer.toString(id));
            repo.save("news.channel." + tag, gson.toJson(channelVal));
        });
    }
}
