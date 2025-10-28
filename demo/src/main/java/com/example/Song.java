package com.example;

public class Song {

  private String name;
  private String artist;
  private String fileName;
  private String year;
  private String genre;
  public String toString() {
    String s;
    s = "{ ";
    s += "name: " + name;
    s += ", ";
    s += "artist: " + artist;
    s += ", ";
    s += "fileName: " + fileName;
    s += ", ";
    s += "year: " + year;
    s += ", ";
    s += "genre: " + genre;
    s += " }";

    return s;
  }

  public String name() {
    return this.name;
  }

  public String artist() {
    return this.artist;
  }

  public String fileName() {
    return this.fileName;
  }
  public String year() {
    return this.year;
  }
  public String genre() {
    return this.genre;
  }
    private boolean isFavorite;

  public boolean isFavorite() {
  return this.isFavorite;
}

  public void setFavorite(boolean favorite) {
  this.isFavorite = favorite;
}
}
