import cf.RatingPrediction;
import hitrate.HitRateComputing;
import itemrating.ItemRatingAfterAHPWeight;
import itemrating.ItemRatingConsideringAccessHistory;
import mostpopular.MostPopularCache;
import org.junit.Test;
import userclassfication.UserClassfication;
import util.UserToRatingArrPickTopK;
import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class HitRateComputingTest {


    private Connection conn= WriteAndReadDataWithDB.getConn();

    @Test
    public void testGetItemAccessList(){

        ArrayList<Integer> results=HitRateComputing.getItemAccessList(conn,30,300);

        for(int i:results){
            System.out.println(i);
        }
    }


    @Test
    public void testComputeHitRate() throws Exception {


        int topK=20;

        double[][] userRating=new RatingPrediction().getRatingsIncludingPrediction(30,300,5);

        UserClassfication userClass=new UserClassfication();
        int[] userAccessNum=new UserClassfication().getUserAcessNum(30,300);

        Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,4);

        double[] totalRatingsAfterAHP= ItemRatingAfterAHPWeight.getItemRatingAferAHP(userRating,levelMap);


        double[] itemRatingsAfterConsideringAccessHistory= ItemRatingConsideringAccessHistory.getAllItemRatingsConsiderAccessHistory(
                conn,totalRatingsAfterAHP,30,1);

        Set<Integer> MyAlgorithmSet= UserToRatingArrPickTopK.getTopKItem(itemRatingsAfterConsideringAccessHistory,topK);

        Set<Integer> MostPopularSet= MostPopularCache.getTopKItemMostPopular(conn,30,300,topK);



        ArrayList<Integer> accessList=HitRateComputing.getItemAccessList(conn,30,300);


        double myWayHitRate=HitRateComputing.computeHitRate(accessList,MyAlgorithmSet);

        double mostPopularHitRate=HitRateComputing.computeHitRate(accessList,MostPopularSet);

        System.out.println("the hit rate of my way is "+myWayHitRate);
        System.out.println("the hit rate of most popular strategy is "+mostPopularHitRate);
    }
}
