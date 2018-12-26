import java.util.Set;

public class SimilarMatrix {


    //输入参数为需要计算相似度的矩阵，可以是item的类型矩阵genreMatrix，也可以是item的用户评分矩阵,
    //item为横坐标
    double[][] generateItemSimilarMatrix(int[][] computeMatrix){

        double[][] itemSimilarMatrix =new double[computeMatrix.length][computeMatrix.length];

        for(int i=0;i<itemSimilarMatrix.length;i++){
            for(int j=i;j<itemSimilarMatrix.length;j++){
                //计算genre向量的余弦相似性
                if(j==i){
                    itemSimilarMatrix[i][j]=1;
                }else {

                    itemSimilarMatrix[i][j] = cosSimilarity(computeMatrix[i], computeMatrix[j]);
                }
            }
        }


        //对称填充
        for(int i=1;i<itemSimilarMatrix.length;i++){
            for(int j=0;j<i;j++){
                itemSimilarMatrix[i][j]=itemSimilarMatrix[j][i];
            }
        }

        return itemSimilarMatrix;
    }



    double[][] generateMatrixWithRadio(double[][] matrixA,double[][] matrixB,double radio) throws Exception{

        if(matrixA==null||matrixB==null){
            return null;
        }

        if(matrixA.length!=matrixB.length || matrixA[0].length!=matrixB[0].length){
            throw new Exception("generate matrix with two different scale original matrixs!");
        }


        double[][] resultMatrix=new double[matrixA.length][matrixA[0].length];
        for(int i=0;i<matrixA.length;i++){
            for(int j=0;j<matrixA[0].length;j++){
                resultMatrix[i][j]=matrixA[i][j]+radio*matrixB[i][j];
            }
        }

        return resultMatrix;
    }


    //历史平均打分

    private double cosSimilarity(int[] vec1,int[] vec2){
        //这里其实要考虑一下分母为0的情况，我就不写了
        return pointMulti(vec1,vec2)/squares(vec1,vec2);



    }



    // 求平方和

    private double squares(int[] vec1,int[] vec2) {
        double result1=0;
        double result2=0;

        for(int i=0;i<vec1.length;i++){
            result1+=vec1[i]*vec1[i];
            result2+=vec2[i]*vec2[i];
        }

        return Math.sqrt(result1)*Math.sqrt(result2);




    }



    // 点乘法

    private double pointMulti(int[] vec1,int[] vec2) {

        double result=0;
        for(int i=0;i<vec1.length;i++){
            result+=vec1[i]*vec2[i];
        }
        return result;
    }

}
