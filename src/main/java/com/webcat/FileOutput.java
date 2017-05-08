package com.webcat;

import com.google.inject.Inject;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.NumberField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.streams.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Level;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import org.apache.commons.io.FileUtils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.inject.assistedinject.Assisted;


/**
 * This is the plugin. Your class should implement one of the existing plugin
 * interfaces. (i.e. AlarmCallback, MessageInput, MessageOutput)
 */
public class FileOutput implements MessageOutput{

    private boolean shutdown;
    private File file;
    private String messageBuffer;


    private static final String CK_OUTPUT_FOLDER = "output_folder";
    private static final String CK_OUTPUT_FILE = "output_file";
    private static final String CK_FLUSH_TIME = "flush_time";
    private static final Logger LOG = LoggerFactory.getLogger(FileOutput.class);

    @Inject
    public FileOutput(@Assisted Stream stream , @Assisted Configuration conf){

        String output_folder = conf.getString(CK_OUTPUT_FOLDER);
        String file_name = conf.getString(CK_OUTPUT_FILE);
        String full_file_name = output_folder + "/" + file_name ;
        file = new File(full_file_name);
        Integer flush_time=5;
        try {
            flush_time = conf.getInt(CK_FLUSH_TIME);
        } catch (Exception e) {
        }

        shutdown = false;
        messageBuffer="";

        Timer timer = new Timer();
        WriteBuffer wb = new WriteBuffer(this);

        LOG.info(" File Output Plugin has been configured with the following parameters:");
        LOG.info(CK_OUTPUT_FOLDER + " : " + output_folder );
        LOG.info(CK_OUTPUT_FILE + " : " + file_name );
        LOG.info(CK_FLUSH_TIME + " : " + flush_time );

        timer.scheduleAtFixedRate(wb, 0, flush_time * 1000);

    }

    @Override
    public boolean isRunning() {
        return !shutdown;
    }

    @Override
    public void stop() {
        shutdown = true;


    }

    @Override
    public void write(List<Message> msgs) throws Exception {
        for (Message msg: msgs) {
            write(msg);
        }
    }

    @Override
    public void write(Message msg) throws Exception {
        if (shutdown) {
            return;
        }

        messageBuffer+=msg.getMessage();
        messageBuffer+="\n";

    }

    public void writeBuffer(){

        try {
            // 3rd parameter boolean append = true
                FileUtils.writeStringToFile(file, messageBuffer, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        messageBuffer="";

    }

    public interface Factory extends MessageOutput.Factory<FileOutput> {
        @Override
        FileOutput create(Stream stream, Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageOutput.Descriptor {
        public Descriptor() {
            super("FileOutput Output", false, "", "Forwards stream to Plain File.");
        }
    }

    public static class Config extends MessageOutput.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest configurationRequest = new ConfigurationRequest();
            configurationRequest.addField(new TextField(CK_OUTPUT_FOLDER,
                    "Output folder in which the output file will be written.",
                    "/tmp/", "Output folder in which the output file will be written.",
                    ConfigurationField.Optional.NOT_OPTIONAL)
            );
            configurationRequest.addField(new TextField(CK_OUTPUT_FILE,
                    "File's name in which the output will be written",
                    "file_output",
                    "File's name in which the output will be written",
                    ConfigurationField.Optional.NOT_OPTIONAL)
            );
            configurationRequest.addField(new NumberField(CK_FLUSH_TIME,
                    "Flush period time in seconds.",
                    15,
                    "Flush time period/interval",
                    ConfigurationField.Optional.NOT_OPTIONAL)
            );

            return configurationRequest;
        }
    }

}


class WriteBuffer extends TimerTask {


    private FileOutput fo;
    public WriteBuffer(FileOutput fo){super(); this.fo=fo; }
    public void run() {

        fo.writeBuffer();
    }
}