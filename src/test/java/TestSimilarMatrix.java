public class TestSimilarMatrix {


    public double[][] testGenerateItemSimilarMatrix(int[][] computeMatrix){
        return new SimilarMatrix().generateItemSimilarMatrix(computeMatrix);
    }


    public static void main(String[] args){
        int[][] matrix={{2,3,4,3},{2,3,4,3},{1,1,1,1},{2,4,5,1}};

        double[][] outMatrix=new TestSimilarMatrix().testGenerateItemSimilarMatrix(matrix);

        System.out.println(outMatrix);
    }
}
