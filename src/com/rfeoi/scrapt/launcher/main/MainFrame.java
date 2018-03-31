package com.rfeoi.scrapt.launcher.main;

import com.rfeoi.scrapt.launcher.updater.Updater;

import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class MainFrame extends JFrame implements ActionListener{
    private JPanel selectPanel;
    private JTextField pathToProjectFile;
    private JButton pathToProjectFileSelecter;
    private JButton start;
    private JButton stop;
    private Process exec;
    public MainFrame() {
        this.setVisible(true);
        try {
            Updater updater = new Updater();
            if (updater.isNew()) {
                JOptionPane.showMessageDialog(null, "Updated! Version: " + updater.getVersionString()+" Description: "+ updater.getUpdateMessage());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Update error: Check your Internet Connection.");
            e.printStackTrace();
        }
        this.setLayout(new GridLayout(4, 1));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        selectPanel = new JPanel();
        selectPanel.setLayout(new GridLayout(1,2));
        pathToProjectFile = new JTextField();
        pathToProjectFile.setText(System.getProperty("user.home"));
        pathToProjectFileSelecter = new JButton("...");
        pathToProjectFileSelecter.addActionListener(this);
        selectPanel.add(pathToProjectFile);
        selectPanel.add(pathToProjectFileSelecter);
        this.add(selectPanel);
        start = new JButton("Start Scrapt Script");
        stop = new JButton("Stop Scrapt Script");
        stop.setEnabled(false);
        start.addActionListener(this);
        stop.addActionListener(this);
        this.add(start);
        this.add(stop);
        this.setSize((int) this.getMaximumSize().getHeight() / 3, (int) this.getMaximumSize().getHeight() / 2);
    }

    private int start(String command) {
        int exit = Integer.MIN_VALUE;
        try {
            exec = Runtime.getRuntime().exec(command);
            InputStream inputStream = exec.getInputStream();
            InputStream errorStream = exec.getErrorStream();
            while (exec.isAlive()) {
                while (inputStream.available() > 0) {
                    System.out.print((char)inputStream.read() + "");
                }
                while (errorStream.available() > 0) {
                    System.err.print((char)errorStream.read());
                }
            }
            inputStream.close();
            errorStream.close();
            start.setEnabled(true);
            stop.setEnabled(false);
            exit = exec.exitValue();
            exec.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        return exit;
    }
    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton invoker = (JButton)e.getSource();
        if (invoker.getText().equals(start.getText())){
            start.setEnabled(false);
            stop.setEnabled(true);
            //TODO Make JAVA Command dynamic
            start("java -jar " + System.getProperty("user.home") + File.separatorChar + ".scrapt" + File.separatorChar + "scrapt.jar " + pathToProjectFile.getText());
        }else if (invoker.getText().equals(stop.getText())){
            if (exec.isAlive())exec.destroy();
            start.setEnabled(true);
            stop.setEnabled(false);
        }else if (invoker.getText().equals(pathToProjectFileSelecter.getText())){
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Scrapt Project File.");
            if (new File(pathToProjectFile.getText()).exists())chooser.setCurrentDirectory(new File(pathToProjectFile.getText()));
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    if (f.getName().endsWith(".scrapt")) return true;
                    return false;
                }

                @Override
                public String getDescription() {
                    return "Scrapt Project File";
                }
            });
            this.setFocusable(false);
            int i = chooser.showOpenDialog(null);
            if (i == 0){
                pathToProjectFile.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }
}
