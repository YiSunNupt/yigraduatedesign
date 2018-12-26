import util.InfoMatrixGenerating;

import java.util.*;

//根据个人浏览记录预测
public class IndividualPrediction {

    //类型矩阵
    //private int[][] genreMatrix= InfoMatrixGenerating.getInfoMatrix(
    //        "F:\\chromeDownload\\ml-100k\\ml-100k\\genrematrix.txt",1682,19," ");

    //入参是浏览记录及相应item的评分，所有文件的请求概率，这个请求概率需要动态维护，没有请求过得概率就为0
    //rateRadio表示之前的用户评分在预测中的权重
    //可以这样啊，先训练一段时间，并不做缓存，而是等一定时间之后在做缓存，这样，各种数据也就有了,viewReqProbs长度为item的数量，
    // 需要动态维护
    //返回未请求文件的预测请求概率的map

    //可能存在的问题，计算量太大，可不可以先对内容进行聚类，然后只在所聚的类中进行预测
    Map<Integer,Double> predictRequestProb(HashMap<Integer,Integer> recordsAndRate, double rateRadio,double[] viewReqProbs,
                                int itemNums,double[][] genreSimilarMatrix){
        Map<Integer,Double> pridictedReqProbs=new HashMap<Integer,Double>();

        Set<Integer> indexSet=recordsAndRate.keySet();
        for(int i=0;i<itemNums;i++){
            if(indexSet.contains(i)){
                continue;
            }else{
                //根据之前的浏览记录计算i-th内容的流行度
                double up=0;
                double down=0;

                for(int j : indexSet){
                    up+=(genreSimilarMatrix[i][j]+rateRadio*recordsAndRate.get(j))*viewReqProbs[j];
                    down+=genreSimilarMatrix[i][j]+rateRadio*recordsAndRate.get(j);

                }
                pridictedReqProbs.put(i,up/down);
            }
        }


        return pridictedReqProbs;

    }



    //只根据个人的浏览历史及内容类型的相似度来做请求概率预测
    //hisRecord长度就是item的数目，然后值以0|1来表示本用户是否访问过，
    // 注意hisRecord[0]是空着还是表示第一个item，我们这里选择空着，从1开始，viewProbs也是从1开始
    //genreMatrix下标是从0，开始

    //返回预测数组，数组下标index就是i-th item（从1开始）
    public double[] getUnBrowsedContentPredictProbsWithGenreMatrix(int[] hisRecord, double[] viewProbs,
                                                                   double[][] similarMatrixWithGenre){
        double[] probsPredicts=new double[similarMatrixWithGenre.length+1];

        for(int i=1;i<=similarMatrixWithGenre.length;i++){
            //表示i-th文件没有浏览过
            double predictProb=0;
            if(hisRecord[i]!=1){
                double up=0;
                double down=0;

                for(int j=1;j<=similarMatrixWithGenre.length;j++){
                    if(hisRecord[j]==1){
                        up+=similarMatrixWithGenre[i-1][j-1]*viewProbs[j];
                        down+=similarMatrixWithGenre[i-1][j-1];
                    }
                }

                if(up==0||down==0){
                    predictProb=0;
                }else {
                    predictProb = up / down;
                }
            }



            probsPredicts[i]=predictProb;
        }

        return probsPredicts;
    }

    public int[] pickTopKItem(double[] probsPredicts, int K){
        return pickTopKItem(probsPredicts,K,false);
    }


    //zeroIncluded=0,表示我们不考虑index=0的元素，从1开始
    public int[] pickTopKItem(double[] probsPredicts, int K,boolean zeroIncluded){


        double[] copyOfProbs= Arrays.copyOf(probsPredicts,probsPredicts.length);

        int[] topK=new int[K];
        for(int i=0;i<K;i++){

            int max=zeroIncluded?0:1;

            for(int index=zeroIncluded?0:1;index<copyOfProbs.length;index++){
                //当前轮最大下标

                if(copyOfProbs[index]>copyOfProbs[max]){
                    max=index;
                }

            }

            topK[i]=max;
            copyOfProbs[max]=-1;
        }

        return topK;
    }





}
