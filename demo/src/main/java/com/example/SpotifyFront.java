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
    private Map<String,String> comments = new HashMap<>();
    private Set<String> fav = new HashSet<>();
    private String dir ="F://Song For Spotify App//Song//wav";
    public SpotifyFront(){
        setTitle(" Spotify App Interface");
        setSize(700,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //List of the song
        model = new DefaultListModel<>();
        File folder = new File(dir);
        File[] songs = folder.exists() ?folder.listFiles((d,n)->n.toLowerCase().endsWith(".wav")): null;
        if (songs !=null) for (File f : songs)
            model.addElement(f.getName());
        list = new JList<>(model);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane listPane = new JScrollPane(list);

        comment = new JTextArea(5,20);
        JScrollPane commentPane = new JScrollPane(comment);

        JPanel p = new JPanel(new GridLayout(2,3,5,5));
        JButton play = new JButton("Play");
        JButton pause = new JButton("Pause");
        JButton back = new JButton("Back");
        JButton next = new JButton("Next");
        JButton reset = new JButton("Reset");
        JButton favorite = new JButton("Favorite");

        p.add(play); p.add(pause); p.add(back); p.add(next); p.add(reset); p.add(favorite);
        status = new JLabel ("Select a Song to Play",SwingConstants.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPane, commentPane);
        split.setDividerLocation (200);
        add(status, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);

        play.addActionListener(e -> playSong());
        pause.addActionListener(e -> pauseOrRemuse());  
        back.addActionListener (e -> move(-5_000_000));;
        next.addActionListener (e -> move(5_000_000));
        reset.addActionListener (e -> resetSong());
        favorite.addActionListener (e -> toggleFavorite());
        list.addListSelectionListener(e -> loadComment());
        setVisible(true);
    }    
    private void playSong() {
        String s = cleanName(list.getSelectedValue());
        if (s == null) return;
        saveComment();
        try {
            if (clip != null && clip.isOpen()) { clip.stop(); clip.close(); }
            File f = new File(dir, s);
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(f));
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            status.setText("Playing: " + s);
        } catch (Exception ex) {
            status.setText("Error: " + ex.getMessage());
        }
    }
    private void pauseOrResume() {
        if (clip == null) return;
        if (clip.isRunning()) { clip.stop(); status.setText("Paused"); }
        else { clip.start(); status.setText("Resumed"); }
    }
    private void move(long delta) {
       if (clip == null) return;
        long pos = Math.max(0, Math.min(clip.getMicrosecondLength(), clip.getMicrosecondPosition() + delta));
        clip.setMicrosecondPosition(pos);
    }
     private void resetSong() {
        if (clip == null) return;
        clip.stop();
        int opt = JOptionPane.showConfirmDialog(this, "Restart song from beginning?", "Reset", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) { clip.setMicrosecondPosition(0); clip.start(); }
        else clip.start();
    }
    private void toggleFavorite() {
        String s = cleanName(list.getSelectedValue());
        if (s == null) return;
        if (fav.contains(s)) fav.remove(s);
        else fav.add(s);
        refreshListDisplay();
}
    private void refreshListDisplay() {
        for (int i = 0; i < model.size(); i++) {
            String name = cleanName(model.get(i));
            if (fav.contains(name)) model.set(i, name + " ★");
            else model.set(i, name);
        }
    }
    private String cleanName(String s) {
        if (s == null) return null;
        return s.replace(" ★", "").trim();
    }
   private void saveComment() {
        String s = cleanName(list.getSelectedValue());
        if (s != null) comments.put(s, comment.getText());
    }

    private void loadComment() {
        String s = cleanName(list.getSelectedValue());
        if (s != null) comment.setText(comments.getOrDefault(s, ""));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpotifyFront::new);
    }

}
