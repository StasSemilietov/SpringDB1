package com.red.aop.interfaces;

import com.red.aop.objects.Mp3;

import java.util.List;
import java.util.Map;

/**
 * Created by Steven on 30.07.2016.
 */
public interface MP3Dao {
    void insert(Mp3 mp3);
    void delete(Mp3 mp3);
    void delete(int id);
    Mp3 getByID(int id);
    List<Mp3> getMP3ListByName(String name);
    List<Mp3> getMP3ListByAuthor(String author);
    int getMp3Count();
    Map<String,Integer> getStatistic();

}
