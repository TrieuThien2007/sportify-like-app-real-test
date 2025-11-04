package com.example;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class SpotifyFront extends JFrame {
    private Clip clip;
    private JList<String> list;
    private DefaultListModel<String> model;
    private JTextArea comment;
    private JLabel status;
    private Map<String, String>comments = new HashMap<>();
    private Set<String> fav = new HashSet<>();
    private String dir ="F://Song For Spotify App//Song//wav";
    public SpotifyFront(){
        setTitle(" Spotify App Interface");
        setSize(700,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }    
}
