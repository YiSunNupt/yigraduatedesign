import org.junit.Test;

public class TestIndividualPrediction {


    @Test
    public void testPickTopK(){

        int[] out=new IndividualPrediction().pickTopKItem(new double[]{0,0.9,1,0.3,0.5,0,6,0.4,1,0.8},5);
        for(int i:out){
            System.out.println(i);
        }

    }
}
