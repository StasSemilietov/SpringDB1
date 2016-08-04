package com.red.aop.implementation;

import com.red.aop.interfaces.MP3Dao;
import com.red.aop.objects.Author;
import com.red.aop.objects.Mp3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Steven on 30.07.2016.
 * http://www.ibm.com/developerworks/ru/library/j-ts1/
 */
@Component("sqliteDao")
public class SQLiteDAO implements MP3Dao {
    private static final String mp3Table = "mp3";
    private static final String mp3View = "mp3_view";

    private SimpleJdbcInsert insertMP3;

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertMP3 = new SimpleJdbcInsert(dataSource).withTableName("mp3").usingColumns("name", "author");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, timeout = 1)
    public int insertMP3(Mp3 mp3) {

        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());

        int author_id = insertAuthor(mp3.getAuthor());

        String sqlInsertMP3 = "insert into mp3 (author_id, name) VALUES (:authorId, :mp3Name)";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params = new MapSqlParameterSource();
        params.addValue("mp3Name", mp3.getName());
        params.addValue("authorId", author_id);

        return jdbcTemplate.update(sqlInsertMP3, params);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public int insertAuthor(Author author) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());

        String sqlInsertAuthor = "insert into author (name) VALUES (:authorName)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("authorName", author.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sqlInsertAuthor, params, keyHolder);

        return keyHolder.getKey().intValue();

    }


    @Override
    public int insertList(List<Mp3> listMP3) {
//        String sql = "insert into mp3 (name, author) VALUES (:author, :name)";
//
//        SqlParameterSource[] params = new SqlParameterSource[listMP3.size()];
//
//        int i = 0;
//
//        for (Mp3 mp3 : listMP3) {
//            MapSqlParameterSource p = new MapSqlParameterSource();
//            p.addValue("name", mp3.getName());
//            p.addValue("author", mp3.getAuthor());
//
//            params[i] = p;
//            i++;
//        }
//
//        // SqlParameterSource[] batch =
//        // SqlParameterSourceUtils.createBatch(listMP3.toArray());
//        int[] updateCounts = jdbcTemplate.batchUpdate(sql, params);
//        return updateCounts.length;
        int count = 0;
        for(Mp3 mp3 : listMP3){
            insertMP3(mp3);
            count++;
        }
        return count;
    }

    @Override
    public Map<String, Integer> getStat() {
        String sql = "select author_name, count(*) as count from " + mp3View + " group by author_name";

        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {

            public Map<String, Integer> extractData(ResultSet rs) throws SQLException {
                Map<String, Integer> map = new TreeMap<>();
                while (rs.next()) {
                    String author = rs.getString("author_name");
                    int count = rs.getInt("count");
                    map.put(author, count);
                }
                return map;
            };

        });

    }

    @Override
    public void delete(int id) {
        String sql = "delete from mp3 where id=:id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void delete(Mp3 mp3) {
        delete(mp3.getId());
    }

    @Override
    public Mp3 getMP3ByID(int id) {
        String sql = "select * from " + mp3View + " where mp3_id=:mp3_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mp3_id", id);

        return jdbcTemplate.queryForObject(sql, params, new MP3RowMapper());
    }

    @Override
    public List<Mp3> getMP3ListByName(String mp3Name) {
        String sql = "select * from " + mp3View + " where upper(mp3_name) like :mp3_name";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mp3_name", "%" + mp3Name.toUpperCase() + "%");

        return jdbcTemplate.query(sql, params, new MP3RowMapper());
    }

    @Override
    public List<Mp3> getMP3ListByAuthor(String author) {
        String sql = "select * from " + mp3View + " where upper(author_name) like :author_name";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("author_name", "%" + author.toUpperCase() + "%");

        return jdbcTemplate.query(sql, params, new MP3RowMapper());
    }

    @Override
    public int getMP3Count() {
        String sql = "select count(*) from " + mp3Table;
        return jdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class);
    }

    private static final class MP3RowMapper implements RowMapper<Mp3> {

        @Override
        public Mp3 mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author();
            author.setId(rs.getInt("author_id"));
            author.setName("author_name");

            Mp3 mp3 = new Mp3();
            mp3.setId(rs.getInt("mp3_id"));
            mp3.setName(rs.getString("mp3_name"));
            mp3.setAuthor(author);
            return mp3;
        }

    }
}
