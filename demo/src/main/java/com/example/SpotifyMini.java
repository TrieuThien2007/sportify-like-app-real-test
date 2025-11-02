package com.example;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class SpotifyMini extends JFrame {
    private Clip clip;
    private JList<String> list;
    private DefaultListModel<String> model;
    private JTextArea comment;
    private JLabel status;
    private Map<String,String> comments = new HashMap<>();
    private Set<String> fav = new HashSet<>();
    private String dir = "F:/Song For Spotify App/Song/wav";

    public SpotifyMini() {
        setTitle("🎵 Mini Spotify");
        setSize(550, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Danh sách bài hát ---
        model = new DefaultListModel<>();
        File folder = new File(dir);
        File[] songs = folder.exists() ? folder.listFiles((d,n)-> n.toLowerCase().endsWith(".wav")) : null;
        if (songs != null) for (File f : songs) model.addElement(f.getName());
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane listPane = new JScrollPane(list);

        comment = new JTextArea(5,20);
        JScrollPane commentPane = new JScrollPane(comment);

        JPanel p = new JPanel(new GridLayout(2,3,5,5));
        JButton play = new JButton("▶ Play");
        JButton pause = new JButton("⏸ Pause");
        JButton back = new JButton("⏪ -5s");
        JButton next = new JButton("⏩ +5s");
        JButton reset = new JButton("🔄 Reset");
        JButton favBtn = new JButton("⭐ Fav");
        p.add(play); p.add(pause); p.add(back); p.add(next); p.add(reset); p.add(favBtn);

        status = new JLabel("Select a song", SwingConstants.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPane, commentPane);
        split.setDividerLocation(200);
        add(status, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);

        // --- Sự kiện ---
        play.addActionListener(e -> playSong());
        pause.addActionListener(e -> pauseOrResume());
        back.addActionListener(e -> move(-5_000_000));
        next.addActionListener(e -> move(5_000_000));
        reset.addActionListener(e -> resetSong());
        favBtn.addActionListener(e -> toggleFavorite());
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

    // Khi bấm Fav: nếu bài đã có trong fav thì bỏ, ngược lại thêm + cập nhật biểu tượng ★
    private void toggleFavorite() {
        String s = cleanName(list.getSelectedValue());
        if (s == null) return;
        if (fav.contains(s)) fav.remove(s);
        else fav.add(s);
        refreshListDisplay();
    }

    // Cập nhật lại danh sách hiển thị ngôi sao ★ cho bài ưa thích
    private void refreshListDisplay() {
        for (int i = 0; i < model.size(); i++) {
            String name = cleanName(model.get(i));
            if (fav.contains(name)) model.set(i, name + " ★");
            else model.set(i, name);
        }
    }

    // Loại bỏ ký hiệu ★ khi lấy tên file thật
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
        SwingUtilities.invokeLater(SpotifyMini::new);
    }
}

