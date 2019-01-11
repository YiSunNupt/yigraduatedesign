import cf.RatingPrediction;
import org.junit.Test;

public class RatingPredictionTest {

    @Test
    public void testUserItemRatingSimilarity() {
        System.out.println(RatingPrediction.userItemRatingSimilarity(5,1));
    }


    @Test
    public void testUserItemInterestSimilarity(){
        System.out.println(RatingPrediction.userItemInterestSimilarity(3,3,2,2));

    }

    @Test
    public void testUserInterConfidence(){
        int u=1;
        int v=2;
        double res=RatingPrediction.userInterConfidence(u,v);
        System.out.println("confidence result between "+u+" and "+v+" is "+res);

    }
    @Test
    public void testUserSimilarity(){
        int u=1;
        int v=2;
        double res=RatingPrediction.userSimilarity(u,v);

        System.out.println("total similarity between "+u+" and "+v+" is "+res);

    }

    @Test
    public void testUserRatingPredict(){
        int userId=1;
        double[] ratings=new RatingPrediction().userRatingPredict(userId,30,300,5);

        for(int i=1;i<ratings.length;i++) {
            System.out.println("the "+i+"-th item's rating of user "+userId+" is "+ratings[i]);
        }
    }

}
