package com.red.aop.main;

import com.red.aop.implementation.SQLiteDAO;
import com.red.aop.objects.Author;
import com.red.aop.objects.Mp3;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 30.07.2016.
 */
public class Start {
    public static void main(String[] args) {
        Mp3 first = new Mp3();
        first.setName("AnotherSong1111");
        Author author = new Author();
        author.setName("Aldrich");
        first.setAuthor(author);
//
//        Mp3 second = new Mp3();
//        second.setName("New second Song");
//        second.setAuthor("Unknown");
//
//        List<Mp3> list = new ArrayList<>();
//        list.add(first);
//        list.add(second);
//
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        SQLiteDAO sqLiteDao = (SQLiteDAO) context.getBean("sqliteDao");
//
        System.out.println(sqLiteDao.insert(first));


    }

}
