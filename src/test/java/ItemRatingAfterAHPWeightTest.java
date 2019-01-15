import cf.RatingPrediction;
import itemrating.ItemRatingAfterAHPWeight;
import org.junit.Test;
import userclassfication.UserClassfication;

import java.util.Map;
import java.util.Set;

public class ItemRatingAfterAHPWeightTest {


    @Test
    public void testGetItemRatingAferAHP(){

        double[][] userRating=new RatingPrediction().getRatingsIncludingPrediction(30,300,5);

        UserClassfication userClass=new UserClassfication();
        int[] userAccessNum=new UserClassfication().getUserAcessNum(30,300);

        Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,4);

        double[] totalRatingsAfterAHP= ItemRatingAfterAHPWeight.getItemRatingAferAHP(userRating,levelMap);

        for(int i=1;i<totalRatingsAfterAHP.length;i++){
            System.out.println("the "+i+"-th item's total rating is: "+totalRatingsAfterAHP[i]);
        }
    }
}
