package com.webcat;

import com.google.inject.Inject;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.streams.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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

    private String output_folder;
    private String file_name;


    @Inject
    public FileOutput(@Assisted Stream stream , @Assisted Configuration conf){

        output_folder = conf.getString("output_folder");
        file_name = conf.getString("file_name");
        shutdown = false;


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

        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd-MM-yyyy");

        try{
            dateString = sdfr.format(msg.getTimestamp().toDate());
        }catch (Exception ex ){
            System.out.println(ex);
        }

        BufferedWriter bw = null;
        String full_file_name = this.output_folder + "/" + this.file_name + "_" + dateString;
        try {
            // APPEND MODE SET HERE
            bw = new BufferedWriter(new FileWriter(full_file_name, true));
            bw.write(msg.getMessage());
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                // just ignore it
            }
        } // end try/catch/finally


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
            return configurationRequest;
        }
    }

}
