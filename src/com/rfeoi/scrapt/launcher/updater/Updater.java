package com.rfeoi.scrapt.launcher.updater;

import java.io.*;
import java.net.URL;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Updater {
    private final String VERSION_URL = "http://github.io/rfoei/scrapt/update.html";
    private String versionString;
    private String updateURL;
    private File version;
    private File jar;
    private boolean isNew;
    private int empty;

    public Updater() throws IOException {
        initialize();
        check();
    }

    public boolean isNew() {
        return isNew;
    }

    private void check() throws IOException {
        URL url = new URL(VERSION_URL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
        String versionStringWeb = reader.readLine();
        reader.close();
        String version = versionStringWeb.split(";")[0];
        updateURL = versionStringWeb.split(";")[1];
        if (!version.equals(versionString) || empty == 2){
            update();
            isNew = true;
        }else if (empty == 1){
            writeVersionString(version);
        }
    }

    private void writeVersionString(String string) throws IOException {
        if (version.exists())version.delete();
        version.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(version));
        writer.write(versionString);
        writer.close();
    }
    private void update() throws IOException {
        URL url = new URL(updateURL);
        if (jar.exists()) jar.delete();
        jar.createNewFile();
        InputStream inputStream = url.openConnection().getInputStream();
        FileWriter writer = new FileWriter(jar);
        int input;
        while((input = inputStream.read()) != 0){
            writer.write(input);
        }
        inputStream.close();
        writer.close();
        writeVersionString(versionString);
    }
    private void initialize() throws IOException {
        version = new File(System.getProperty("user.home") + File.separatorChar + ".scrapt" + File.separatorChar + "version.txt");
        jar = new File(System.getProperty("user.home") + File.separatorChar + ".scrapt" + File.separatorChar + "scrapt.jar");
        if (!version.getParentFile().exists()) {
            version.getParentFile().mkdir();
        }
        if (!version.exists()) {
            version.createNewFile();
            versionString = "missing";
            if (jar.exists()) empty = 1;
            else empty = 2;
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(version));
            versionString = reader.readLine();
            reader.close();
        }
    }


}
