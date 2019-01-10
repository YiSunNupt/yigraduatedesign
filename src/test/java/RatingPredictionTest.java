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
}
