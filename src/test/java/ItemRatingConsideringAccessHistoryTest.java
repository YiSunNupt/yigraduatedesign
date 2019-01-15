import cf.RatingPrediction;
import itemrating.ItemRatingAfterAHPWeight;
import itemrating.ItemRatingConsideringAccessHistory;
import org.junit.Test;
import userclassfication.UserClassfication;
import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

public class ItemRatingConsideringAccessHistoryTest {

    private static Connection conn= WriteAndReadDataWithDB.getConn();

    @Test
    public  void testGetAllItemRatingsConsiderAccessHistory(){
        double[] allRatings=new double[]{0.0,3.0,5.0,4.0,1.0,1.0};
        double[] itemRatingsAfterProcess=ItemRatingConsideringAccessHistory.getAllItemRatingsConsiderAccessHistory(
                conn,allRatings,30,1);
        for(int i=1;i<allRatings.length;i++){
            System.out.println("the "+i+"-th item result rating is "+itemRatingsAfterProcess[i]);
        }

    }

    @Test
    public  void testGetAllItemRatingsConsiderAccessHistoryAndUserLevel(){
        double[][] userRating=new RatingPrediction().getRatingsIncludingPrediction(30,300,5);

        UserClassfication userClass=new UserClassfication();
        int[] userAccessNum=new UserClassfication().getUserAcessNum(30,300);

        Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,4);

        double[] totalRatingsAfterAHP= ItemRatingAfterAHPWeight.getItemRatingAferAHP(userRating,levelMap);

        double[] itemRatingsAfterProcess=ItemRatingConsideringAccessHistory.getAllItemRatingsConsiderAccessHistory(
                conn,totalRatingsAfterAHP,30,1);
        for(int i=1;i<totalRatingsAfterAHP.length;i++){
            System.out.println("the "+i+"-th item result rating is "+itemRatingsAfterProcess[i]);
        }
    }
}
