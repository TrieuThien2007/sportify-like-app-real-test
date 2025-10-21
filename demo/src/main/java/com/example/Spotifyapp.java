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
    Scanner input = new Scanner(System.in);

    String userInput = "";
    while (!userInput.equals("q")) {
      menu();
      userInput = input.nextLine();
      userInput = userInput.toLowerCase();
      handleMenu(userInput, library);
    }
    input.close();
  }
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[P]lay");
    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter q to Quit:");
  }
  public static void handleMenu(String userInput, Song[] library) {
    switch (userInput) {
      case "h":
        System.out.println("-->Home<--");
        break;
      case "s":
        System.out.println("-->Search by title<--");
        break;
      case "l":
        System.out.println("-->Library<--");
        showLibraryAndPlay(library);
        break;
      case "p":
        System.out.println("-->Play<--");
        play(library);
        break;
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    }
  }
    public static void showLibraryAndPlay(Song[] library) {
    System.out.println("-->List Of Songs<--");

    for (int i = 0; i < library.length; i++) {
        System.out.println((i + 1) + ". " + library[i].name());
    }
    Scanner sc = new Scanner(System.in);
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
        System.out.println("Playing: " + selectedSong.fileName());
    } catch (Exception e) {
        System.out.println("Error Playing The Song");
    }
}
  public static void play(Song[] library) {
  Scanner scanner = new Scanner(System.in);
    System.out.println("Enter The Name Of Your Song:");
    String songNameInput = scanner.nextLine().trim()+".wav";

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
