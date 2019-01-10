package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserDataProcessing {

    private static String SQL_HEAD_PART="select userid,itemid,rating,timestamp from ";
    private static String SQL_TAIL_PART_USERID=" where userid=?";

    private static String SQL_TAIL_PART_ITEMID_LIMIT=" and itemid <=?";


    //根据用户id，返回其item-rating映射
    public static Map<Integer, Integer> getUserData(int userId, boolean isTrainDateTable, int itemNumLimit){
        Connection conn=WriteAndReadDataWithDB.getConn();
        PreparedStatement pstat=null;
        String querySql;
        if(isTrainDateTable){
            querySql = SQL_HEAD_PART+" traindata_new "+SQL_TAIL_PART_USERID+SQL_TAIL_PART_ITEMID_LIMIT;
        }else{
            querySql=SQL_HEAD_PART+" testdata_new "+SQL_TAIL_PART_USERID+SQL_TAIL_PART_ITEMID_LIMIT;
        }
        try {
            pstat=conn.prepareStatement(querySql);
            pstat.setInt(1,userId);
            pstat.setInt(2,itemNumLimit);

            ResultSet res=pstat.executeQuery();
            Map<Integer,Integer> resultMap=new HashMap<Integer,Integer>();
            while(res.next()) {
                int itemId =res.getInt(2);
                int rating=res.getInt(3);
                resultMap.put(itemId,rating);
            }

            return resultMap;

        }catch(SQLException e){
            System.out.println(e.getStackTrace());
        }finally{
            if(pstat!=null){
                try {
                    pstat.close();
                } catch (SQLException e){
                    System.out.println(e.getStackTrace());
                }
            }
            if(conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getStackTrace());
                }
            }
        }
        return null;
    }
}
