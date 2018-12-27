import org.junit.Test;

public class TestSimilarMatrix {


    public double[][] testGenerateItemSimilarMatrix(int[][] computeMatrix){
        return new SimilarMatrix().generateItemSimilarMatrix(computeMatrix);
    }


    //public static void main(String[] args){
     //   int[][] matrix={{2,3,4,3},{2,3,4,3},{1,1,1,1},{2,4,5,1}};

      //  double[][] outMatrix=new TestSimilarMatrix().testGenerateItemSimilarMatrix(matrix);

      //  System.out.println(outMatrix);
    //}



    @Test
    public void testGetUserMatrix(){
        int[][] userMatrix=SimilarMatrix.getUserMatrix("F:\\chromeDownload\\ml-100k\\ml-100k\\usermatrix.txt");

        System.out.println("rows: "+userMatrix.length+"  "+"cols: "+userMatrix[0].length);

        for(int i=0;i<5;i++){
            for(int j=0;j<userMatrix[0].length;j++){
                System.out.print(userMatrix[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
