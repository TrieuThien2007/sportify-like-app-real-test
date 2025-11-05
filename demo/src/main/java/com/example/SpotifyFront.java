package com.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class SpotifyFront extends JFrame {
    private Clip clip;
    private JList<String> list;
    private DefaultListModel<String> model;
    private JTextArea comment;
    private JLabel status;
    private Map<String, String> comments = new HashMap<>();
    private Set<String> fav = new HashSet<>();
    private Map<String, String[]> info = new HashMap<>();
    private JLabel infoLabel;
    private String dir = "F://Song For Spotify App//Song//wav";

    public SpotifyFront() {
        setTitle(" Spotify App Interface");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultListModel<>();
        File folder = new File(dir);
        File[] songs = folder.exists() ? folder.listFiles((d, n) -> n.toLowerCase().endsWith(".wav")) : null;
        if (songs != null)
            for (File f : songs)
                model.addElement(f.getName());
        list = new JList<>(model);

        info.put("Awesome God.wav", new String[] { "Hillsong UNITED", "Worship", "2005" });
        info.put("Blessed Be Your Name.wav", new String[] { "Matt Redman", "Worship", "2002" });
        info.put("Fall Never Change.wav", new String[] { "Issac Thai", "Pop", "2022" });
        info.put("Fountain Of Joy.wav", new String[] { "Called To Worship", "Pop", "2022" });
        info.put("Hope Of The Nations.wav", new String[] { "Brian Doerksen", "Worship", "2003" });
        info.put("Hymm Of Heaven.wav", new String[] { "Phil Wickham", "Worship", "2021" });
        info.put("Living Hope.wav", new String[] { "Phil Wickham", "Worship", "2018" });
        info.put("Love Never Fall.wav", new String[] { "Bui Gai Chay", "Pop", "2021" });
        info.put("Sing Forever.wav", new String[] { "Bui Gai Chay", "Pop-rock", "2025" });
        info.put("Start From Me.wav", new String[] { "Called To Worship", "Rock", "2023" });

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane listPane = new JScrollPane(list);

        comment = new JTextArea(5, 20);
        JScrollPane commentPane = new JScrollPane(comment);

        JPanel p = new JPanel(new GridLayout(2, 3, 5, 5));
        JButton play = new JButton("Play");
        JButton pause = new JButton("Pause");
        JButton back = new JButton("Back 5s");
        JButton next = new JButton("Next 5s");
        JButton reset = new JButton("Reset");
        JButton favorite = new JButton("Favorite #");

        p.add(play);
        p.add(pause);
        p.add(back);
        p.add(next);
        p.add(reset);
        p.add(favorite);
        status = new JLabel("Select a Song to Play", SwingConstants.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPane, commentPane);
        split.setDividerLocation(200);
        infoLabel = new JLabel(" ", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        infoLabel.setForeground(Color.DARK_GRAY);

        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.add(status);
        northPanel.add(infoLabel);
        add(northPanel, BorderLayout.NORTH);

        add(split, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);

        play.addActionListener(e -> playSong());
        pause.addActionListener(e -> pauseOrResume());
        back.addActionListener(e -> move(-5_000_000));
        ;
        next.addActionListener(e -> move(5_000_000));
        reset.addActionListener(e -> resetSong());
        favorite.addActionListener(e -> toggleFavorite());
        list.addListSelectionListener(e -> {
            loadComment();
            showInfo();
        });
        JSlider bar = new JSlider(0, 100, 0);
        JPanel top = new JPanel(new BorderLayout());
        top.add(northPanel, BorderLayout.NORTH);
        top.add(bar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        new javax.swing.Timer(500, e -> {
            if (clip != null && clip.isRunning())
                bar.setValue((int) (100.0 * clip.getMicrosecondPosition() / clip.getMicrosecondLength()));
        }).start();
        bar.addChangeListener(ev -> {
            if (clip != null && !clip.isRunning())
                clip.setMicrosecondPosition((long) (clip.getMicrosecondLength() * bar.getValue() / 100.0));
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveComment();
            }
        });

        setVisible(true);
    }

    private void showInfo() {
        String s = cleanName(list.getSelectedValue());
        if (s == null) {
            infoLabel.setText(" ");
            return;
        }
        String[] i = info.get(s);
        if (i != null)
            infoLabel.setText("Artist: " + i[0] + "   |   Genre: " + i[1] + "   |   Year:" + i[2]);
        else
            infoLabel.setText("No info available");
    }

    private void playSong() {
        String s = cleanName(list.getSelectedValue());
        if (s == null)
            return;
        saveComment();
        try {
            if (clip != null && clip.isOpen()) {
                clip.stop();
                clip.close();
            }
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
        if (clip == null)
            return;
        if (clip.isRunning()) {
            clip.stop();
            status.setText("Paused");
        } else {
            clip.start();
            status.setText("Resumed");
        }
    }

    private void move(long delta) {
        if (clip == null)
            return;
        long pos = Math.max(0, Math.min(clip.getMicrosecondLength(), clip.getMicrosecondPosition() + delta));
        clip.setMicrosecondPosition(pos);
    }

    private void resetSong() {
        if (clip == null)
            return;
        clip.stop();
        int opt = JOptionPane.showConfirmDialog(this, "Restart song from beginning?", "Reset",
                JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            clip.setMicrosecondPosition(0);
            clip.start();
        } else
            clip.start();
    }

    private void toggleFavorite() {
        String s = cleanName(list.getSelectedValue());
        if (s == null)
            return;
        if (fav.contains(s))
            fav.remove(s);
        else
            fav.add(s);
        refreshListDisplay();
    }

    private void refreshListDisplay() {
        for (int i = 0; i < model.size(); i++) {
            String name = cleanName(model.get(i));
            if (fav.contains(name))
                model.set(i, name + " #");
            else
                model.set(i, name);
        }
    }

    private String cleanName(String s) {
        if (s == null)
            return null;
        return s.replace(" #", "").trim();
    }

    private void saveComment() {
        String songName = cleanName(list.getSelectedValue());
        if (songName == null)
            return;

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File("audio-library.json");

            if (!file.exists())
                return;

            FileReader reader = new FileReader(file);
            java.lang.reflect.Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> songs = gson.fromJson(reader, type);
            reader.close();

            for (Map<String, Object> s : songs) {
                if (s.get("fileName").equals(songName)) {
                    s.put("comment", comment.getText());
                    break;
                }
            }

            FileWriter writer = new FileWriter(file);
            gson.toJson(songs, writer);
            writer.close();

        } catch (IOException ex) {
            System.out.println("Error saving comment to JSON: " + ex.getMessage());
        }
    }

    private void loadComment() {
        String songName = cleanName(list.getSelectedValue());
        if (songName == null) {
            comment.setText("");
            return;
        }

        try {
            Gson gson = new Gson();
            File file = new File("audio-library.json");
            if (!file.exists())
                return;

            FileReader reader = new FileReader(file);
            java.lang.reflect.Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> songs = gson.fromJson(reader, type);
            reader.close();

            for (Map<String, Object> s : songs) {
                if (s.get("fileName").equals(songName)) {
                    String cmt = (String) s.get("comment");
                    comment.setText(cmt != null ? cmt : "");
                    break;
                }
            }

        } catch (IOException ex) {
            System.out.println("Error loading comment from JSON: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpotifyFront::new);
    }

}
