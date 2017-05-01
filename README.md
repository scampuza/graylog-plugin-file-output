# FileOutput Plugin for Graylog

[![Build Status](https://travis-ci.org/scampuza.svg?branch=master)](https://travis-ci.org/scampuza)


**Required Graylog version:** 2.0 and later

This Plugin has a very specific purpose:  Write the messages to an Output plain file, indicated in the Plugin configuration parameters.



Installation
------------

[Download the plugin](https://github.com/scampuza/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.


Usage
-----

Once you have installed the plugin, you can configure an Output of type  com.webcat.FileOutput, with this two simple parameters:

file_name: File to be created in which the messages will be written to.
output_folder: Absolute path of the folder in which the file will be written.


Getting started
---------------

This project is using Maven 3 and requires Java 8 or higher.

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog plugin directory.
* Restart the Graylog.

Plugin Release
--------------

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```

This sets the version numbers, creates a tag and pushes to GitHub. Travis CI will build the release artifacts and upload to GitHub automatically.
