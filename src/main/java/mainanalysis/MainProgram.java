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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainProgram {

    private static Connection conn= WriteAndReadDataWithDB.getConn();


    public static void main(String[] args) throws Exception {

        considerDifferentFileRequestedHistoryFactor();
        //considerDifferentMaxUserId();

    }


    public static void considerDiffentCacheCapacity() throws Exception {
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


    public static void considerDifferentMaxUserId() throws Exception {
        int[] maxUserIdArr={10,20,30,40,50,60,70,80,90,100,150,200};
        //int[] maxUserIdArr={50};
        int maxItemId=300;
        int maxNeighborNumWhenPredictRating=5;
        final int userLevelNum=4;
        double userRequestHistoryFactor=0.14;
        int topK=40;

        Map<Integer,Double[]> resultMap=new HashMap<>();

        for (int maxUserId:maxUserIdArr){
            double[][] userRating=new RatingPrediction().getRatingsIncludingPrediction(maxUserId,maxItemId,maxNeighborNumWhenPredictRating);

            UserClassfication userClass=new UserClassfication();
            int[] userAccessNum=new UserClassfication().getUserAcessNum(maxUserId,maxItemId);

            Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,userLevelNum);

            double[] totalRatingsAfterAHP= ItemRatingAfterAHPWeight.getItemRatingAferAHP(userRating,levelMap);


            double[] itemRatingsAfterConsideringAccessHistory= ItemRatingConsideringAccessHistory.getAllItemRatingsConsiderAccessHistory(
                    conn,totalRatingsAfterAHP,maxUserId,userRequestHistoryFactor);
            Set<Integer> MyAlgorithmSet = UserToRatingArrPickTopK.getTopKItem(itemRatingsAfterConsideringAccessHistory, topK);

            Set<Integer> MostPopularSet = MostPopularCache.getTopKItemMostPopular(conn, maxUserId, maxItemId, topK);


            ArrayList<Integer> accessList = HitRateComputing.getItemAccessList(conn, maxUserId, maxItemId);


            double myWayHitRate = HitRateComputing.computeHitRate(accessList, MyAlgorithmSet);

            double mostPopularHitRate = HitRateComputing.computeHitRate(accessList, MostPopularSet);

            resultMap.put(maxUserId,new Double[]{myWayHitRate,mostPopularHitRate});
        }

        Set<Integer> keySet=resultMap.keySet();
        for(int maxId:keySet) {

            System.out.println("the maxUserId is " + maxId);

            System.out.println("the hit rate of my way is " + resultMap.get(maxId)[0]);
            System.out.println("the hit rate of most popular strategy is " + resultMap.get(maxId)[1]);
            System.out.println("===============================================================================================");
        }
    }


    public static void considerDifferentFileRequestedHistoryFactor() throws Exception {
        int maxUserId=30;
        int maxItemId=300;
        int maxNeighborNumWhenPredictRating=5;
        final int userLevelNum=4;
        int topK=40;
        //double[] userRequestHistoryFactorArr={0,0.1,0.3,0.5,0.7,1,3,5,7,10,50,100};

        double[] userRequestHistoryFactorArr={0,0.02,0.04,0.06,0.08,0.1,0.12,0.14,0.16,0.18,0.2,
                0.22,0.24,0.26,0.28,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7};



        double[][] userRating=new RatingPrediction().getRatingsIncludingPrediction(maxUserId,maxItemId,maxNeighborNumWhenPredictRating);

        UserClassfication userClass=new UserClassfication();
        int[] userAccessNum=new UserClassfication().getUserAcessNum(maxUserId,maxItemId);

        Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,userLevelNum);

        double[] totalRatingsAfterAHP= ItemRatingAfterAHPWeight.getItemRatingAferAHP(userRating,levelMap);

        Set<Integer> MostPopularSet = MostPopularCache.getTopKItemMostPopular(conn, maxUserId, maxItemId, topK);
        ArrayList<Integer> accessList = HitRateComputing.getItemAccessList(conn, maxUserId, maxItemId);
        double mostPopularHitRate = HitRateComputing.computeHitRate(accessList, MostPopularSet);

        for (double userRequestHistoryFactor:userRequestHistoryFactorArr){
            double[] itemRatingsAfterConsideringAccessHistory= ItemRatingConsideringAccessHistory.getAllItemRatingsConsiderAccessHistory(
                    conn,totalRatingsAfterAHP,maxUserId,userRequestHistoryFactor);
            Set<Integer> MyAlgorithmSet = UserToRatingArrPickTopK.getTopKItem(itemRatingsAfterConsideringAccessHistory, topK);
            double myWayHitRate = HitRateComputing.computeHitRate(accessList, MyAlgorithmSet);
            System.out.println("userRequestHistoryFactor "+userRequestHistoryFactor);
            System.out.println("the hit rate of my way is " + myWayHitRate);
            System.out.println("the hit rate of most popular strategy is " + mostPopularHitRate);
            System.out.println("===============================================================================================");
        }

    }
}
