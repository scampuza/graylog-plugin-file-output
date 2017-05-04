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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    static private String output_folder;
    static private String file_name;
    static private BufferedWriter bw;
    static private String full_file_name;
    static private File file;
    static private Integer flush_time;
    static private String messageBuffer;
    static private Timer timer;

    private WriteBuffer wb;


    @Inject
    public FileOutput(@Assisted Stream stream , @Assisted Configuration conf){

        output_folder = conf.getString("output_folder");
        file_name = conf.getString("file_name");
        full_file_name = this.output_folder + "/" + this.file_name ; //+ "_" + dateString;
        file = new File(full_file_name);
        flush_time = conf.getInt("flush_time");
        shutdown = false;
        bw = null;
        messageBuffer="";

        timer = new Timer();
        wb = new WriteBuffer(this);
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
            configurationRequest.addField(new TextField("output_folder", "Output folder in which the output file will be written.", "/tmp/", "Output folder in which the output file will be written.", ConfigurationField.Optional.NOT_OPTIONAL));
            configurationRequest.addField(new TextField("file_name", "File's name in which the output will be written", "file_output", "File's name in which the output will be written", ConfigurationField.Optional.NOT_OPTIONAL));
            configurationRequest.addField(new NumberField("flush_time", "Flush period time in seconds.", 3, "Flush time period/interval", ConfigurationField.Optional.NOT_OPTIONAL));

            return configurationRequest;
        }
    }

}


class WriteBuffer extends TimerTask {


    private FileOutput fo;
    public WriteBuffer(FileOutput fo){super(); this.fo=fo; if(fo != null) {System.out.println("Here!");}}
    public void run() {

        fo.writeBuffer();
    }
}