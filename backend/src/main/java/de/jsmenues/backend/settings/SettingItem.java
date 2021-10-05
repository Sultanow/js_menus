package de.jsmenues.backend.settings;

import javax.ws.rs.FormParam;

public class SettingItem {
    @FormParam("key")
    String key;
    @FormParam("value")
    String value;
    @FormParam("oldValue")
    String oldValue;

    public SettingItem() {

    }

    public SettingItem(String key, String value, String oldValue) {
        super();
        this.key = key;
        this.value = value;
        this.oldValue = oldValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    @Override
    public String toString() {
        return "key=" + this.key + ";value=" + this.value;
    }

}
