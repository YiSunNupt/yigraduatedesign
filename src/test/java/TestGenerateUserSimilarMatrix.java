import org.junit.Test;

public class TestGenerateUserSimilarMatrix {


    @Test
    public void testGenerateUserSimilarMatrix() throws Exception {
        double[][] userSimilarMatrix=new SimilarMatrix().generateUserSimilarMatrix(
                SimilarMatrix.getUserMatrix("F:\\chromeDownload\\ml-100k\\ml-100k\\usermatrix.txt"),1,1);

        for(int i=0;i<10;i++){
            for(int j=0;j<userSimilarMatrix[0].length;j++){
                System.out.print(userSimilarMatrix[i][j]);
                System.out.print(" ");
            }

            System.out.println();
        }
    }
}
