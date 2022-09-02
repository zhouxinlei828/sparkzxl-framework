package com.github.sparkzxl.data.sync.admin.listener;

/**
 * description: Data cache to compare if data has changed.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:09:19
 */
public class ConfigDataCache {
    
    private final String group;

    private volatile String md5;

    private final String json;

    private volatile long lastModifyTime;
    
    /**
     * Instantiates a new Config data cache.
     *
     * @param group          the group
     * @param json           the json
     * @param md5            the md5
     * @param lastModifyTime the last modify time
     */
    public ConfigDataCache(final String group, final String json, final String md5, final long lastModifyTime) {
        this.group = group;
        this.json = json;
        this.md5 = md5;
        this.lastModifyTime = lastModifyTime;
    }
    
    /**
     * Update.
     *
     * @param md5            the md 5
     * @param lastModifyTime the last modify time
     */
    protected synchronized void update(final String md5, final long lastModifyTime) {
        this.md5 = md5;
        this.lastModifyTime = lastModifyTime;
    }
    
    /**
     * Gets group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }
    
    /**
     * Gets md5.
     *
     * @return the md5
     */
    public String getMd5() {
        return md5;
    }
    
    /**
     * Gets last modify time.
     *
     * @return the last modify time
     */
    public long getLastModifyTime() {
        return lastModifyTime;
    }
    
    /**
     * Gets json.
     *
     * @return the json
     */
    public String getJson() {
        return json;
    }

    @Override
    public String toString() {
        return "{"
                + "group='" + group + '\''
                + ", md5='" + md5 + '\''
                + ", lastModifyTime=" + lastModifyTime
                + '}';
    }
}
