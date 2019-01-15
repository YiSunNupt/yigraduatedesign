package mainanalysis;

import cf.RatingPrediction;
import hitrate.HitRateComputing;
import itemrating.ItemRatingAfterAHPWeight;
import itemrating.ItemRatingConsideringAccessHistory;
import mostpopular.MostPopularCache;
import userclassfication.UserClassfication;
import util.UserToRatingArrPickTopK;
import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainProgram {

    private static Connection conn= WriteAndReadDataWithDB.getConn();


    public static void main(String[] args) throws Exception {
        int maxUserId=30;
        int maxItemId=300;
        int maxNeighborNumWhenPredictRating=5;
        final int userLevelNum=4;

        //用于考虑用户访问历史对评分预测的影响
        double userRequestHistoryFactor=1;

        double[][] userRating=new RatingPrediction().getRatingsIncludingPrediction(maxUserId,maxItemId,maxNeighborNumWhenPredictRating);

        UserClassfication userClass=new UserClassfication();
        int[] userAccessNum=new UserClassfication().getUserAcessNum(maxUserId,maxItemId);

        Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,userLevelNum);

        double[] totalRatingsAfterAHP= ItemRatingAfterAHPWeight.getItemRatingAferAHP(userRating,levelMap);


        double[] itemRatingsAfterConsideringAccessHistory= ItemRatingConsideringAccessHistory.getAllItemRatingsConsiderAccessHistory(
                conn,totalRatingsAfterAHP,maxUserId,userRequestHistoryFactor);

        int[] topKArr={5,10,20,30,40,50,100};

        for(int topK:topKArr) {


            Set<Integer> MyAlgorithmSet = UserToRatingArrPickTopK.getTopKItem(itemRatingsAfterConsideringAccessHistory, topK);

            Set<Integer> MostPopularSet = MostPopularCache.getTopKItemMostPopular(conn, maxUserId, maxItemId, topK);


            ArrayList<Integer> accessList = HitRateComputing.getItemAccessList(conn, maxUserId, maxItemId);


            double myWayHitRate = HitRateComputing.computeHitRate(accessList, MyAlgorithmSet);

            double mostPopularHitRate = HitRateComputing.computeHitRate(accessList, MostPopularSet);

            System.out.println("the cache capacity is "+topK);

            System.out.println("the hit rate of my way is " + myWayHitRate);
            System.out.println("the hit rate of most popular strategy is " + mostPopularHitRate);
            System.out.println("===============================================================================================");
        }
    }
}
