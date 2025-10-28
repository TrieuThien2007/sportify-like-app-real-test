package com.example;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
public class Spotifyapp {
  private static Clip audioClip;
  private static String directoryPath =
    "F:\\Song For Spotify App\\Song";

  public static void main(final String[] args) {
    Song[] library = readAudioLibrary();
    Scanner sc = new Scanner(System.in);

    String userInput = "";
    while (!userInput.equals("q")) {
      menu();
      userInput = sc.nextLine().trim().toLowerCase();
      handleMenu(userInput, library, sc);
    }
    sc.close();
  }
  public static void menu() {
    System.out.println("---- SpotifyApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[P]lay");
    System.out.println("[F]avorite");
    System.out.println("[Q]uit");
    System.out.println("");
    System.out.print("Enter q to Quit:");
  }
  public static void handleMenu(String userInput, Song[] library, Scanner sc) {
    switch (userInput) {
      case "h":
        System.out.println("-->Home<--");
        break;
      case "s":
        System.out.println("-->Search by title<--");
        searchAndPlay(library, sc );
        break;
      case "l":
        System.out.println("-->Library<--");
        showLibraryAndPlay(library, sc );
        break;
      case "p":
        System.out.println("-->Play<--");
        play(library, sc);
        break;
        case "f":
        System.out.println("-->Favorites<--");
        showFavorites(library);
        break;
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    }
  }
      public static void showFavorites(Song[] library) {
      System.out.println("--> Your Favorite Songs <--");
      boolean hasFavorite = false;
      for (Song s : library) {
      if (s.isFavorite()) {
      hasFavorite = true;
      System.out.println("Your Favorite Songs: " + s.name() + " - " + s.artist());
    }
  }
  if (!hasFavorite) {
    System.out.println("No favorite songs yet!");
  }
}
  public static void searchAndPlay(Song[] library, Scanner sc) {
    System.out.print("Find The Song: ");
    String songName = sc.nextLine().trim();
    if (!songName.toLowerCase().endsWith(".wav")) {
        songName += ".wav";
    }

    Song foundSong = null;
    for (Song s : library) {
        if (s.fileName().equalsIgnoreCase(songName)) {
            foundSong = s;
            break;
        }
    }

    if (foundSong == null) {
        System.out.println("No Found \"" + songName + "\" In The Library!!");
        return;
    }

    System.out.println("Found The Song: " + foundSong.fileName());
    System.out.print("Do You Want To Play This Song? (Y/N): ");
    String choice = sc.nextLine().trim().toLowerCase();

    if (choice.equals("y")) {
        playSelectedSong(foundSong, sc);
        System.out.println("Artist: " + foundSong.artist());
        System.out.println("Year: " + foundSong.year());
        System.out.println("Genre: " + foundSong.genre());
    } else {
        System.out.println("Back To Menu...");
    }
}

private static void playSelectedSong(Song selectedSong, Scanner sc) {
    String filename = selectedSong.fileName();

    if (!filename.toLowerCase().endsWith(".wav")) {
        filename += ".wav";
    }

    String filePath = directoryPath + "/" + filename;
    File file = new File(filePath);
    if (!file.exists()) {

        filePath = directoryPath + "/wav/" + filename;
        file = new File(filePath);
    }

    if (!file.exists()) {
        System.out.println("No Found Music File: " + filePath);
        return;
    }

    try {
        if (audioClip != null && audioClip.isOpen()) {
            audioClip.close();
        }

        AudioInputStream in = AudioSystem.getAudioInputStream(file);
        audioClip = AudioSystem.getClip();
        audioClip.open(in);
        audioClip.setMicrosecondPosition(0);
        audioClip.start();
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        System.out.println("--Now Playing:--");
        System.out.println("Title: " + selectedSong.name());
        System.out.println("Artist: " + selectedSong.artist());
        System.out.println("Year: " + selectedSong.year());
        System.out.println("Genre: " + selectedSong.genre());
        System.out.print("Do You Want To Add This Song To Favorites? (Y/N): ");
        String favChoice = sc.nextLine().trim().toLowerCase();
        if (favChoice.equals("y")) {
        selectedSong.setFavorite(true);
        System.out.println(" Added To Favorites!");
        } else {
        selectedSong.setFavorite(false);
        }
        System.out.println("--Thanks For Using Our App--");
    } catch (Exception e) {
        System.out.println("Error When Playing: " + e.getMessage());
    }
}

    public static void showLibraryAndPlay(Song[] library,Scanner sc ) {
    System.out.println("-->List Of Songs<--");

    for (int i = 0; i < library.length; i++) {
        System.out.println((i + 1) + ". " + library[i].name());
    }
    System.out.print("Choose The Number For The Song(or 0 To Return): ");
    int choice = -1;
    try {
        choice = Integer.parseInt(sc.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("Choose A Valid Number");
        return;
    }

    if (choice == 0) {
        System.out.println("Back To Home.");
        return;
    }

    if (choice < 1 || choice > library.length) {
        System.out.println("Error No Song Found");
        return;
    }
    Song selectedSong = library[choice - 1];
     System.out.println("--Now Playing:--");
        System.out.println("Title: " + selectedSong.name());
        System.out.println("Artist: " + selectedSong.artist());
        System.out.println("Year: " + selectedSong.year());
        System.out.println("Genre: " + selectedSong.genre());
    final String filename = selectedSong.fileName();
    final String filePath = directoryPath + "/wav/" + filename;
    final File file = new File(filePath);

    if (audioClip != null) {
        audioClip.close();
    }

    try {
        audioClip = AudioSystem.getClip();
        final AudioInputStream in = AudioSystem.getAudioInputStream(file);
        audioClip.open(in);
        audioClip.setMicrosecondPosition(0);
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        System.out.print("Do You Want To Add This Song To Favorites? (Y/N): ");
        String favChoice = sc.nextLine().trim().toLowerCase();
        if (favChoice.equals("y")) {
        selectedSong.setFavorite(true);
        System.out.println(" Added To Favorites!");
        } else {
      selectedSong.setFavorite(false);
}
        System.out.println("Playing: " + selectedSong.name());
    } catch (Exception e) {
        System.out.println("Error Playing The Song");
    }
      System.out.println("--Thanks For Using Our App--");
}
  public static void play(Song[] library,Scanner sc ) {
    System.out.println("Enter The Name Of Your Song:");
    String songNameInput = sc.nextLine().trim()+".wav";

    Song selectedSong = null;
    for (Song s : library){
      if(s.fileName().equalsIgnoreCase(songNameInput)){
        selectedSong = s;
        break;
      }
    }
    if (selectedSong == null) {
      System.out.println("Song not found in the library!!");
      return;
    }
     System.out.println("--Now Playing:--");
        System.out.println("Title: " + selectedSong.name());
        System.out.println("Artist: " + selectedSong.artist());
        System.out.println("Year: " + selectedSong.year());
        System.out.println("Genre: " + selectedSong.genre());
    final String filename = selectedSong.fileName();
    final String filePath = directoryPath + "/wav/" + filename;
    final File file = new File(filePath);
    if (audioClip != null) {
      audioClip.close();
    }

    try {
      audioClip = AudioSystem.getClip();
      final AudioInputStream in = AudioSystem.getAudioInputStream(file);

      audioClip.open(in);
      audioClip.setMicrosecondPosition(0);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        System.out.print("Do You Want To Add This Song To Favorites? (Y/N): ");
        String favChoice = sc.nextLine().trim().toLowerCase();
        if (favChoice.equals("y")) {
        selectedSong.setFavorite(true);
        System.out.println(" Added To Favorites!");
        } else {
    selectedSong.setFavorite(false);
}
        System.out.println("--Thanks For Using Our App--");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static Song[] readAudioLibrary() {
    final String jsonFileName = "audio-library.json";
    final String filePath = directoryPath + "/" + jsonFileName;

    Song[] library = null;
    try {
      System.out.println("Reading the file " + filePath);
      JsonReader reader = new JsonReader(new FileReader(filePath));
      library = new Gson().fromJson(reader, Song[].class);
    } catch (Exception e) {
      System.out.printf("ERROR: unable to read the file %s\n", filePath);
      System.out.println();
    }

    return library;
  }
}
