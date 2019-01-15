package hitrate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HitRateComputing {

    public static double computeHitRate(ArrayList<Integer> itemAccessList, Set<Integer> caheItemSet){

        int hitCount=0;
        for(int i:itemAccessList){

            if(caheItemSet.contains(i)){
                hitCount++;

            }

        }

        return hitCount*1.0/itemAccessList.size();
    }



    public static ArrayList<Integer> getItemAccessList(Connection connection,int maxUserId,int maxItemId){

        String sql="select itemid from testdata_new where userid<=? and itemid<=?";

        ArrayList<Integer> itemAccessList=new ArrayList<>();

        PreparedStatement pst=null;
        if(connection==null){
            System.out.println("the connection is null, please check your database connection");
            return null;
        }
        try{
            pst=connection.prepareStatement(sql);

            pst.setInt(1,maxUserId);
            pst.setInt(2,maxItemId);

            ResultSet results=pst.executeQuery();

            while(results.next()){
                itemAccessList.add(results.getInt(1));
            }

            results.close();
        }catch (SQLException e){
            System.out.println("SQLException happends");
            System.out.println(e.getStackTrace());
        }

        return itemAccessList;
    }
}
