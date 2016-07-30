package com.red.aop.main;

import com.red.aop.implementation.SQLiteDAO;
import com.red.aop.objects.Mp3;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Steven on 30.07.2016.
 */
public class Start {
    public static void main(String[] args) {
        Mp3 mp3 = new Mp3();
        mp3.setName("New Song");
        mp3.setAuthor("Steven O'Connel");


        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        SQLiteDAO sqLiteDao = (SQLiteDAO) context.getBean("sqliteDao");

        System.out.println(sqLiteDao.getStatistic());


    }

}
