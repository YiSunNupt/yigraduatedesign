import org.junit.Test;
import util.ItemRequestHitoryProbComputing;

public class TestItemHistoryRequestProbabilty {


    @Test
    public void testGenerateHistoricalRequestProb(){
        double[] probs=ItemRequestHitoryProbComputing.computeHistoryRequestProb();

        for(int i=1;i<=1682;i++){
            System.out.println(probs[i]);
        }

        double total=0;
        for(int i=1;i<=1682;i++){
            total+=probs[i];
        }

        System.out.println(total);
    }
}
