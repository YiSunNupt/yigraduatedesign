package mostpopular;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class MostPopularCache {

    public  static Set<Integer> getTopKItemMostPopular(Connection connection, int maxUserId, int maxItemId,int K) throws Exception {

        String sql="select itemid,count(rating) count from traindata_new where userid<=? and itemid<=?" +
                " group by itemid order by count desc limit ?";

        Set<Integer> mostPopularSet=new HashSet<>();

        PreparedStatement pst=null;
        if(connection==null){
            System.out.println("the connection is null, please check your database connection");
            return null;
        }
        try{
            pst=connection.prepareStatement(sql);

            pst.setInt(1,maxUserId);
            pst.setInt(2,maxItemId);
            pst.setInt(3,K);

            ResultSet results=pst.executeQuery();

            while(results.next()){
                mostPopularSet.add(results.getInt(1));
            }

            results.close();
        }catch (SQLException e){
            System.out.println("SQLException happends");
            System.out.println(e.getStackTrace());
        }


        return mostPopularSet;

    }


}
