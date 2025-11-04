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

        //List of the song
        model = new DefaultListModel<>();
        File folder = new File(dir);
        File[] songs = folder.exists() ?folder.listFiles((d,n)->n.toLowerCase().endsWith(".wav")): null;
        if (songs !=null) for (File f : songs)
            model.addElement(f.getName());
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFront(new Font("Arial", Font.PLAIN, 16));
        JScrollPane listPane = new JScrollPane(list);

        comment = new JTextArea(5,5);
        JScrollPane commentPane = new JScrollPane(comment);

        JPanel controlPanel = new JPanel();
        JButton playButton = new JButton("Play");
        JButton pause = new JButton("Pause");
        JButton back = new JButton("Back");
        JButton next = new JButton("Next");
        JButton reset = new JButton("Reset");
        JButton favorite = new JButton("Favorite");
    }    
}
