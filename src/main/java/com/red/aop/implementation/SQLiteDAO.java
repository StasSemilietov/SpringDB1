package com.red.aop.implementation;

import com.red.aop.interfaces.MP3Dao;
import com.red.aop.objects.Mp3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Steven on 30.07.2016.
 */
@Component("sqliteDao")
public class SQLiteDAO implements MP3Dao {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private DataSource dataSource;
    private SimpleJdbcInsert insertMp3;

    @Autowired
    public void setDataSource(DataSource dataSource){

        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.dataSource = dataSource;
        this.insertMp3 = new SimpleJdbcInsert(dataSource).withTableName("mp3").usingColumns("name","author");
    }
    @Override
    public void insert(Mp3 mp3) {
        String sqlQuery = "INSERT INTO MP3 (name,author) VALUES (:name, :author)";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("name", mp3.getName());
        param.addValue("author", mp3.getAuthor());
        jdbcTemplate.update(sqlQuery,param);
    }
    @Override
    public void delete(Mp3 mp3) {
        delete(mp3.getId());
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM MP3 WHERE id=:id";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id",id);
        int update = jdbcTemplate.update(sqlQuery, param);

    }
    @Override
    public Mp3 getByID(int id) {
        String sql = "SELECT * FROM MP3 WHERE id=:id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id",id);
        return jdbcTemplate.queryForObject(sql,parameterSource,new MP3RowMapper());
    }
    @Override
    public List<Mp3> getMP3ListByName(String name) {
        String sql = "SELECT * FROM MP3 WHERE UPPER(name) LIKE :name";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name","%" + name.toUpperCase() + "%");
        return jdbcTemplate.query(sql,parameterSource,new MP3RowMapper());
    }
    @Override
    public List<Mp3> getMP3ListByAuthor(String author) {
        String sql = "SELECT * FROM MP3 WHERE UPPER(author) LIKE :author";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author","%" + author.toUpperCase() + "%");
        return jdbcTemplate.query(sql,parameterSource,new MP3RowMapper());
    }

    @Override
    public int getMp3Count() {
        String sql = "SELECT (COUNT)* FROM MP3";
        return jdbcTemplate.getJdbcOperations().queryForObject(sql,Integer.class);
    }

    @Override
    public Map<String, Integer> getStatistic() {
        String sql = "SELECT author, count(*) as count from mp3 group by author";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String,Integer> map = new TreeMap<>();
                while (resultSet.next()){
                    String author = resultSet.getString("author");
                    int count = resultSet.getInt("count");
                    map.put(author,count);
                }
                return map;
            }
        });
    }

    private final static class MP3RowMapper implements RowMapper<Mp3> {
        @Override
        public Mp3 mapRow(ResultSet resultSet, int i) throws SQLException {
            Mp3 mp3 = new Mp3();
            mp3.setId(resultSet.getInt("id"));
            mp3.setName(resultSet.getString("name"));
            mp3.setAuthor(resultSet.getString("author"));
            return mp3;
        }
    }
}
