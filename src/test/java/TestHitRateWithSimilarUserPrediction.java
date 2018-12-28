import org.junit.Test;

public class TestHitRateWithSimilarUserPrediction {


    @Test
    public void testHitRateWithSimilarUserPrediction() throws Exception {

        int user_id=1;
        int cacheNum=100;
        int similarUserNum=50;
        IndividualPredictionMain ipm=new IndividualPredictionMain();
        double[] hitRateWithMostPopularCache=ipm.getHitRateOnMostPopularCache(cacheNum,943);



        int count=0;
        for(int i=1;i<=100;i++){
            double hitRate=ipm.getHitRateAfterPredictBySimilarUserPrefrence(i,similarUserNum,cacheNum);
            System.out.print("similar users predict : "+hitRate+"  ");

            System.out.print("most popular predict : "+hitRateWithMostPopularCache[i]+"  ");
            double gapBetween2Strategy=hitRate-hitRateWithMostPopularCache[i];
            System.out.println(gapBetween2Strategy);
            if(gapBetween2Strategy>=0){
                count++;
            }
        }

        System.out.println(count);





    }
}
