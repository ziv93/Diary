package com.java1234.dao;

import com.java1234.model.Diary;
import com.java1234.model.PageBean;
import com.java1234.util.DateUtil;
import com.java1234.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 日记内容数据访问类
 *
 * @author Administrator
 */
public class DiaryDao {

    //t_diary和t_diaryType表的内连接查询所有信息
    public List<Diary> diaryList(Connection con, PageBean pageBean, Diary s_diary) throws Exception {
        List<Diary> diaryList = new ArrayList<Diary>();
        StringBuffer sb = new StringBuffer("select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");
        if (StringUtil.isNotEmpty(s_diary.getTitle())) {
            sb.append(" and t1.title like '%" + s_diary.getTitle() + "%'");
        }
        if (s_diary.getTypeId() != -1) {
            sb.append(" and t1.typeId=" + s_diary.getTypeId());
        }
        if (StringUtil.isNotEmpty(s_diary.getReleaseDateStr())) {
            sb.append(" and DATE_FORMAT(t1.releaseDate,'%Y年%m月')='" + s_diary.getReleaseDateStr() + "'");
        }
        sb.append(" order by t1.releaseDate desc");
        if (pageBean != null) {
            sb.append(" limit " + pageBean.getStart() + "," + pageBean.getPageSize());             //分页查询
        }
        PreparedStatement pstmt = con.prepareStatement(sb.toString());
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Diary diary = new Diary();
            diary.setDiaryId(rs.getInt("diaryId"));                    //这里给Diary对象设置了3个值
            diary.setTitle(rs.getString("title"));
            diary.setContent(rs.getString("content"));                 //这个在哪里用？
            diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
            diaryList.add(diary);
        }
        return diaryList;
    }

    //t_diary和t_diaryType表的内连接查询所有信息的总数
    public int diaryCount(Connection con, Diary s_diary) throws Exception {
        StringBuffer sb = new StringBuffer("select count(*) as total from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");
        if (StringUtil.isNotEmpty(s_diary.getTitle())) {
            sb.append(" and t1.title like '%" + s_diary.getTitle() + "%'");
        }
        if (s_diary.getTypeId() != -1) {
            sb.append(" and t1.typeId=" + s_diary.getTypeId());
        }
        if (StringUtil.isNotEmpty(s_diary.getReleaseDateStr())) {
            sb.append(" and DATE_FORMAT(t1.releaseDate,'%Y年%m月')='" + s_diary.getReleaseDateStr() + "'");
        }
        PreparedStatement pstmt = con.prepareStatement(sb.toString());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("total");
        } else {
            return 0;
        }
    }

    //查询t_diary表按年月份合计查询
    public List<Diary> diaryCountList(Connection con) throws Exception {
        List<Diary> diaryCountList = new ArrayList<Diary>();
        String sql = "SELECT DATE_FORMAT(releaseDate,'%Y年%m月') as releaseDateStr ,COUNT(*) AS diaryCount  FROM t_diary GROUP BY DATE_FORMAT(releaseDate,'%Y年%m月') ORDER BY DATE_FORMAT(releaseDate,'%Y年%m月') DESC;";
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Diary diary = new Diary();
            diary.setReleaseDateStr(rs.getString("releaseDateStr"));     //这里只给Diray对象设置了两个值
            diary.setDiaryCount(rs.getInt("diaryCount"));
            diaryCountList.add(diary);
        }
        return diaryCountList;
    }

    //t_diary和t_diaryType表的内连接查询指定id的信息
    public Diary diaryShow(Connection con, String diaryId) throws Exception {
        String sql = "select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId and t1.diaryId=?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, diaryId);
        ResultSet rs = pstmt.executeQuery();
        Diary diary = new Diary();
        if (rs.next()) {
            diary.setDiaryId(rs.getInt("diaryId"));
            diary.setTitle(rs.getString("title"));
            diary.setContent(rs.getString("content"));
            diary.setTypeId(rs.getInt("typeId"));
            diary.setTypeName(rs.getString("typeName"));
            diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
        }
        return diary;
    }

    //t_diary表日记增加
    public int diaryAdd(Connection con, Diary diary) throws Exception {
        String sql = "insert into t_diary values(null,?,?,?,now())";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, diary.getTitle());
        pstmt.setString(2, diary.getContent());
        pstmt.setInt(3, diary.getTypeId());
        return pstmt.executeUpdate();
    }

    //t_diary表日记删除
    public int diaryDelete(Connection con, String diaryId) throws Exception {
        String sql = "delete from t_diary where diaryId=?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, diaryId);
        return pstmt.executeUpdate();
    }

    //t_diary表日记跟新
    public int diaryUpdate(Connection con, Diary diary) throws Exception {
        String sql = "update t_diary set title=?,content=?,typeId=? where diaryId=?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, diary.getTitle());
        pstmt.setString(2, diary.getContent());
        pstmt.setInt(3, diary.getTypeId());
        pstmt.setInt(4, diary.getDiaryId());
        return pstmt.executeUpdate();
    }

    //t_diary表是否存在指定id的日记
    public boolean existDiaryWithTypeId(Connection con, String typeId) throws Exception {
        String sql = "select * from t_diary where typeId=?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, typeId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }
}
