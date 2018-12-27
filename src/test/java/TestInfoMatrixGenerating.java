import org.junit.Test;
import util.InfoMatrixGenerating;

public class TestInfoMatrixGenerating {


    @Test
    public void testGenerateGenreMatrixFile(){
        InfoMatrixGenerating.WriteGenreMatrixFile();
    }

    @Test
    public void testGenerateGenreMatrix() throws  Exception{
        int[][] resultMatrix=InfoMatrixGenerating.getInfoMatrix("F:\\chromeDownload\\ml-100k\\ml-100k\\genrematrix.txt",1682,19," ");
        System.out.println("Done");
    }

    @Test
    public void testGenerateUserMatrixFile(){
        InfoMatrixGenerating.writeUserMatrixFile();
    }
}
