package de.jsmenues.backend.news;

class NewsItemBuilder {

    private final NewsItem item;

    public NewsItemBuilder() {
        this.item = new NewsItem();
    }

    public NewsItemBuilder title(String title) {
        this.item.setTitle(title);
        return this;
    }

    public NewsItemBuilder text(String text) {
        this.item.setText(text);
        return this;
    }

    public NewsItemBuilder id(int id) {
        this.item.setId(id);
        return this;
    }

    public NewsItemBuilder date(String date) {
        this.item.setDate(date);
        return this;
    }

    public NewsItemBuilder visible(boolean visible) {
        this.item.setVisible(visible);
        return this;
    }

    public NewsItemBuilder priority(String priority) {
        this.item.setPriority(priority);
        return this;
    }

    public NewsItem build() {
        return this.item;
    }
}
