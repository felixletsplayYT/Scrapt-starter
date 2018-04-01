package com.rfeoi.scrapt.launcher.updater;


import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Updater {
    private final String RELEASE_URL = "https://api.github.com/repos/rfeoi/scrapt/releases/latest";
    private String installedVersion;
    private String versionString;
    private String updateURL;
    private File version;
    private File jar;
    private String updateMessage;
    private boolean isNew;
    private int empty;

    public Updater() throws IOException{
        initialize();
        check();
    }

    public boolean isNew() {
        return isNew;
    }

    public String getUpdateMessage(){
        return updateMessage;
    }

    public String getVersionString() {
        return versionString;
    }

    private void check() throws IOException {
        URL url = new URL(RELEASE_URL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
        String JSONAnswer = reader.readLine();
        boolean foundTag = false;
        boolean foundName = false;
        for (String s : JSONAnswer.split(",")){
            if (s.contains("\"tag_name\"") && !foundTag){
                versionString = s.split("\"")[3];
                foundTag = true;
            }
            else if (s.contains("\"name\"") && !foundName){
                updateMessage = s.split("\"")[3];
                foundName = true;
            }
            if (foundName && foundTag){
                break;
            }
        }
        updateURL = "https://github.com/rfeoi/Scrapt/releases/download/" + versionString + "/scrapt.jar";
        if (!installedVersion.equals(this.versionString) || empty == 2) {
            update();
            isNew = true;
        } else if (empty == 1) {
            writeVersionString(versionString);
        }
    }

    private void writeVersionString(String string) throws IOException {
        if (version.exists()) version.delete();
        version.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(version));
        writer.write(versionString);
        writer.close();
    }

    private void update() throws IOException {
        URL url = new URL(updateURL);
        if (jar.exists()) jar.delete();
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(jar);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
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
            installedVersion = "missing";
            if (jar.exists()) empty = 1;
            else empty = 2;
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(version));
            installedVersion = reader.readLine();
            reader.close();
        }
    }


}
