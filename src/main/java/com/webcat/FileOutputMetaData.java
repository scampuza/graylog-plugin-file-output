package com.webcat;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * Implement the PluginMetaData interface here.
 */
public class FileOutputMetaData implements PluginMetaData {
    private static final String PLUGIN_PROPERTIES = "com.webcat.graylog-plugin-file-output/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "com.webcat.FileOutputPlugin";
    }

    @Override
    public String getName() {
        return "FileOutput";
    }

    @Override
    public String getAuthor() {
        return "Santiago Campuzano <scampuza@gmail.com>";
    }

    @Override
    public URI getURL() {
        return URI.create("https://github.com/scampuza");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 0, "unknown"));
    }

    @Override
    public String getDescription() {
        // TODO Insert correct plugin description
        return "Description of FileOutput plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.from(0, 0, 0, "unknown"));
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
