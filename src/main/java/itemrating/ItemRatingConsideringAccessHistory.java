package itemrating;

import java.sql.PreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

//引入用户已经请求文件人数，用作之后的分析
public class ItemRatingConsideringAccessHistory {


    //consideringFactor是考虑访问历史因素对内容缓存的重要性程度
    public static double[] getAllItemRatingsConsiderAccessHistory(Connection connection,
                                                                  double[] allItemRating, int maxUserId,
                                                                  double consideringFactor){

        double[] resultRatings=new double[allItemRating.length];
        for(int i=1;i<allItemRating.length;i++){
            resultRatings[i]=allItemRating[i]*(1-consideringFactor*getItemRequestedUserRatio(connection,maxUserId,i));
        }
        return resultRatings;
    }

    public static double getItemRequestedUserRatio(Connection connection, int maxUserId, int itemId){

        String sql="select count(userid) from traindata_new where userid<=? and itemid=?";
        PreparedStatement pst=null;
        try{
            pst=connection.prepareStatement(sql);
            pst.setInt(1,maxUserId);
            pst.setInt(2,itemId);
            ResultSet resultSet=pst.executeQuery();
            int count=0;
            if(resultSet.next()){
                count=resultSet.getInt(1);
            }
            resultSet.close();
            return count*1.0/maxUserId;

        }catch (SQLException e){
            System.out.println("error in requesting database");
            System.out.println(e.getStackTrace());
        }finally {
            if(pst!=null){
                try{
                    pst.close();
                }catch (SQLException E){
                    System.out.println("close prepared statement failed");
                }
            }
        }

        return 0.0;
    }
}
