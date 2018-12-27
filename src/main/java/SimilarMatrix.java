import util.DataReadAndWrite;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SimilarMatrix {

    public static int[][] getUserMatrix(String path){
        List<String> lineList= DataReadAndWrite.readFromFile(path);

        int strArrLenth=lineList.get(0).split(" ").length;

        int[][] userMatrix=new int[lineList.size()][strArrLenth];


        for(int i=0;i<lineList.size();i++){
            String strInList=lineList.get(i);
            String[] strArr=strInList.split(" ");
            for(int j=0;j<strArr.length;j++){
                userMatrix[i][j]=Integer.valueOf(strArr[j]);
            }
        }

        return userMatrix;

    }

    public double[][] generateUserSimilarMatrix(int[][] userMatrix,double radio1,double radio2) throws Exception {

        int[][] individualFeature=new int[943][3];
        for(int i=0;i<943;i++){
            for(int j=0;j<3;j++){
                individualFeature[i][j]=userMatrix[i][j];
            }
        }

        double[][] similarMatrixWithUserFeature=generateItemSimilarMatrix(individualFeature);



        int[][] personPrefrenceFeature=new int[943][1682];

        for(int i=0;i<943;i++){
            for(int j=3;j<1685;j++){
                personPrefrenceFeature[i][j-3]=userMatrix[i][j];
            }
        }


        double[][] similarMatrixWithUserPrefrence=generatePrefrenceSimilarMatrix(personPrefrenceFeature);

        return generateMatrixWithRadio(similarMatrixWithUserFeature,radio1,similarMatrixWithUserPrefrence,radio2);

    }


    //输入参数为需要计算相似度的矩阵，可以是item的类型矩阵genreMatrix，也可以是item的用户评分矩阵,
    //item为横坐标
    public double[][] generateItemSimilarMatrix(int[][] computeMatrix){

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


    public double[][] generatePrefrenceSimilarMatrix(int[][] prefrenceMatrix){

        double[][] prefrenceSimilarMatrix =new double[prefrenceMatrix.length][prefrenceMatrix.length];

        for(int i=0;i<prefrenceSimilarMatrix.length;i++){
            for(int j=i;j<prefrenceSimilarMatrix.length;j++){


                //两个用户共同观看的item数量计数
                int samePrefrenceCount=0;

                //计算prefrence向量的余弦相似性
                if(j==i){
                    prefrenceSimilarMatrix[i][j]=1;
                }else {

                    int[] arrI= Arrays.copyOf(prefrenceMatrix[i],prefrenceMatrix[i].length);
                    int[] arrJ=Arrays.copyOf(prefrenceMatrix[j],prefrenceMatrix[j].length);


                    for(int k=0;k<prefrenceSimilarMatrix[i].length;k++){
                        if(arrI[k]==0||arrJ[k]==0){
                            arrI[k]=0;
                            arrJ[k]=0;
                        }else{
                            samePrefrenceCount++;
                        }
                    }

                    if(samePrefrenceCount==0){
                        prefrenceSimilarMatrix[i][j]=0;
                    }else {

                        prefrenceSimilarMatrix[i][j] = cosSimilarity(arrI, arrJ) * samePrefrenceCount;
                    }

                }
            }
        }


        //对称填充
        for(int i=1;i<prefrenceSimilarMatrix.length;i++){
            for(int j=0;j<i;j++){
                prefrenceSimilarMatrix[i][j]=prefrenceSimilarMatrix[j][i];
            }
        }

        return prefrenceSimilarMatrix;

    }



    double[][] generateMatrixWithRadio(double[][] matrixA,double radio1,double[][] matrixB,double radio2) throws Exception{

        if(matrixA==null||matrixB==null){
            return null;
        }

        if(matrixA.length!=matrixB.length || matrixA[0].length!=matrixB[0].length){
            throw new Exception("generate matrix with two different scale original matrixs!");
        }


        double[][] resultMatrix=new double[matrixA.length][matrixA[0].length];
        for(int i=0;i<matrixA.length;i++){
            for(int j=0;j<matrixA[0].length;j++){
                resultMatrix[i][j]=radio1*matrixA[i][j]+radio2*matrixB[i][j];
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
