import cf.RatingPrediction;
import itemrating.ItemRatingAfterAHPWeight;
import itemrating.ItemRatingConsideringAccessHistory;
import org.junit.Test;
import userclassfication.UserClassfication;
import util.UserToRatingArrPickTopK;
import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

public class UserToRatingArrPickTopKTest {

    private static Connection conn= WriteAndReadDataWithDB.getConn();

    @Test
    public void testGetTopKItem(){
        Set<Integer> result= UserToRatingArrPickTopK.getTopKItem(new double[]{10.0,10.0,2.0,5.0,4.0,8.0,5.0,2.0,3.0},5);
        for(int i:result){
            System.out.println(i);
        }
    }

    @Test
    public void getTopKRatingItemId(){
        double[][] userRating=new RatingPrediction().getRatingsIncludingPrediction(30,300,5);

        UserClassfication userClass=new UserClassfication();
        int[] userAccessNum=new UserClassfication().getUserAcessNum(30,300);

        Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,4);

        double[] totalRatingsAfterAHP= ItemRatingAfterAHPWeight.getItemRatingAferAHP(userRating,levelMap);


        double[] itemRatingsAfterConsideringAccessHistory=ItemRatingConsideringAccessHistory.getAllItemRatingsConsiderAccessHistory(
                conn,totalRatingsAfterAHP,30,1);

        Set<Integer> result= UserToRatingArrPickTopK.getTopKItem(itemRatingsAfterConsideringAccessHistory,50);
        for(int i:result){
            System.out.println(i);
        }

    }

}
