package de.jsmenues.backend.news;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class NewsItem {
    private String title = null;
    private String text = "";
    private int id = 0;
    private String date = "";
    private boolean visible = true;
    private String priority = "normal";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * If priority is not one of the valid ones, set it to normal
     */
    public void validatePriority() {
        if(!isValidPriority()) {
            priority = "normal";
        }
    }

    /**
     * Checks if the Priority is valid
     * @return boolean whether the priority is valid.
     */
    public boolean isValidPriority() {
        return this.priority.matches("low|normal|high");
    }
    /**
     * Checks if a date is valid. If not change it to the actual date.
     */
    public void validateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            formatter.parse(this.date);
        } catch (DateTimeParseException e) {
            this.date = LocalDate.now().format(formatter);
        }
    }

    /**
     * Checks if a date is valid.
     * @return Boolean whether it is valid or not.
     */
    public boolean isVaildDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            formatter.parse(this.date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsItem newsItem = (NewsItem) o;
        return visible == newsItem.visible &&
                Objects.equals(title, newsItem.title) &&
                Objects.equals(text, newsItem.text) &&
                Objects.equals(id, newsItem.id) &&
                Objects.equals(date, newsItem.date) &&
                Objects.equals(priority, newsItem.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, text, id, date, visible, priority);
    }

    @Override
    public String
    toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", id=" + id +
                ", date='" + date + '\'' +
                ", visible=" + visible +
                ", priority='" + priority + '\'' +
                '}';
    }
}
