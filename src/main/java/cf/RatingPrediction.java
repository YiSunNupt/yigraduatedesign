package cf;

import util.UserDataProcessing;
import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RatingPrediction {
    public static int ITEM_NUM_LIMIT=300;


    public static double userSimilarity(int u_id,int v_id){
        double sim=0.0;
        Map<Integer,Integer> itemRateMap_u=getUserItemRateMap(u_id);

        Map<Integer,Integer> itemRateMap_v=getUserItemRateMap(u_id);

        Set<Integer> itemSet_u=itemRateMap_u.keySet();
        Set<Integer> itemSet_v=itemRateMap_v.keySet();
        //交集
        Set<Integer> intersectionSet=getIntersectionSet(itemSet_u,itemSet_v);

        for(int itemId:intersectionSet){
            int rate_u=getRating(u_id,itemId,true);
            int rate_v=getRating(v_id,itemId,true);
            sim+=userItemRatingSimilarity(rate_u,rate_v)*userItemInterestSimilarity(rate_u,rate_v,
                    getUserAverageRating(u_id,ITEM_NUM_LIMIT),
                    getUserAverageRating(v_id,ITEM_NUM_LIMIT));
        }

        return userInterConfidence(u_id,v_id)*sim/intersectionSet.size();

    }

    public static double getUserAverageRating(int userId,int maxItemId){
        String sql="select avg(rating) from traindata_new where userid="+userId+" and itemid<="+maxItemId;
        Connection conn= WriteAndReadDataWithDB.getConn();
        PreparedStatement pst=null;
        try{
            pst=conn.prepareStatement(sql);
            ResultSet result=pst.executeQuery();
            return result.getDouble(1);


        }catch (SQLException e){
            System.out.println(e.getStackTrace());
        }finally {
            if(pst!=null){
                try {
                    pst.close();
                } catch (SQLException e){
                    System.out.println(e.getStackTrace());
                }
            }
            try {
                conn.close();
            }catch (SQLException e){
                System.out.println(e.getStackTrace());
            }
        }
        return 0;
    }






    public static double userItemRatingSimilarity(int u_rating,int v_rating){
        return 2.0*(1.0-1.0/(1+Math.exp(-Math.abs(u_rating-v_rating))));
    }

    public static double userItemInterestSimilarity(int u_rating,int v_rating,double u_aveRating,double v_aveRating){
        return 2.0*(1.0-1.0/(1+Math.exp(-Math.abs((u_rating-u_aveRating)-(v_rating-v_aveRating)))));
    }

    public static double userInterConfidence(int userId_u,int userId_v){

        Map<Integer,Integer> itemRateMap_u=getUserItemRateMap(userId_u);

        Map<Integer,Integer> itemRateMap_v=getUserItemRateMap(userId_v);

        Set<Integer> itemSet_u=itemRateMap_u.keySet();
        Set<Integer> itemSet_v=itemRateMap_v.keySet();

        //并集
        Set<Integer> unionSet=getUnionSet(itemSet_u,itemSet_v);

        //交集
        Set<Integer> intersectionSet=getIntersectionSet(itemSet_u,itemSet_v);



        return (1.0*intersectionSet.size())/unionSet.size();

    }

    //求并
    public static Set<Integer> getUnionSet(Set<Integer> a,Set<Integer> b){
        Set<Integer> unionSet=new HashSet<Integer>();
        unionSet.addAll(a);
        unionSet.addAll(b);
        return unionSet;
    }

    //求交

    public static Set<Integer> getIntersectionSet(Set<Integer> a,Set<Integer> b){
        Set<Integer> intersectionSet=new HashSet<Integer>();
        intersectionSet.addAll(a);
        intersectionSet.retainAll(b);
        return intersectionSet;
    }

    public static Map<Integer,Integer> getUserItemRateMap(int userid){
        Map<Integer,Integer> itemRateMap=UserDataProcessing.getUserData(
                userid,true,ITEM_NUM_LIMIT);

        return itemRateMap;

    }


    public static int getRating(int userId,int itemId,boolean isTrainData){
        String sql;
        if(isTrainData){
            sql="select itemid from traindata_new where userid="+userId+" and itemid="+itemId;

        }else{
            sql="select itemid from testdata_new where userid="+userId+" and itemid="+itemId;
        }

        Connection conn= WriteAndReadDataWithDB.getConn();
        PreparedStatement pst=null;
        try{
            pst=conn.prepareStatement(sql);
            ResultSet result=pst.executeQuery();
            int rating=result.getInt(1);
            return rating;

        }catch (SQLException e){
            System.out.println(e.getStackTrace());
        }finally {
            if(pst!=null){
                try {
                    pst.close();
                } catch (SQLException e){
                    System.out.println(e.getStackTrace());
                }
            }
            try {
                conn.close();
            }catch (SQLException e){
                System.out.println(e.getStackTrace());
            }
        }
        return 0;
    }
}
