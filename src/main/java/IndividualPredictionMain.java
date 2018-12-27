import redis.clients.jedis.Jedis;
import util.DataReadAndWrite;
import util.InfoMatrixGenerating;
import util.ItemRequestHitoryProbComputing;
import util.RedisPoolConnection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndividualPredictionMain {

    public static IndividualPredictionMain indiPreMain=new IndividualPredictionMain();
    public static IndividualPrediction indiPre=new IndividualPrediction();

    public static double[] historyReqPrb= ItemRequestHitoryProbComputing.computeHistoryRequestProb();

    static double RADIO=0;


    public static void main(String[] args) throws Exception {


        List<String> lineList=DataReadAndWrite.readFromFile("F:\\chromeDownload\\ml-100k\\ml-100k\\u1.test");

        int K=10;
        int count=0;

        for(int i=1;i<=462;i++) {

            System.out.println("=====i="+i);

            Set<Integer> predictSet = indiPreMain.getTopKPredictItemOfSingleUser(i, K);


            //有的用户并没有请求
            // i>=463的用户都没有再做请求，所以此用户之后均返回NaN

            //double[] individualPredictHitRate=new double[944];

            //
            double individualPredictHitRate = indiPreMain.computeSingleUserHitRateFromTestDataFile(
                    lineList,
                    i,
                    predictSet);


            //根据历史数据得出的历史访问概率，进行最流行缓存，与我们提出的个人预测并缓存作比较
            Set<Integer> topKHistoryRequestSet = new HashSet<Integer>();
            int[] topKHistoryRequestedItem = indiPre.pickTopKItem(historyReqPrb, K);

            for (int ele : topKHistoryRequestedItem) {
                topKHistoryRequestSet.add(ele);
            }

            Set<Integer> userActualReq = getActualRequestSet(lineList, i);
            double MostPopularCacheHirRate = computeHitRate(topKHistoryRequestSet, userActualReq);

            if(individualPredictHitRate-MostPopularCacheHirRate>=0){
                System.out.println("count increse");
                count++;
            }
        }


        System.out.println(count);

    }

    //返回预测的用户i最有可能请求的前K个item
    public Set<Integer> getTopKPredictItemOfSingleUser(int user_id,int K) throws Exception {

        Jedis jedis=new RedisPoolConnection().getLocalJedis();

        //找出用户1的历史访问记录
        List<String> reqHistory=jedis.lrange("user_"+user_id,0,-1);

        //
        int[] reqHisArr=new int[1683];

        int[] rates=new int[1683];

        for(String ele:reqHistory){
            String[] eleArr=ele.split("_");
            reqHisArr[Integer.valueOf(eleArr[0])]=1;
            rates[Integer.valueOf(eleArr[0])]=Integer.valueOf(eleArr[1]);
        }


        int[][] genreMatrix=InfoMatrixGenerating.getInfoMatrix("F:\\chromeDownload\\ml-100k\\ml-100k\\genrematrix.txt",1682,19," ");

        double[][] itemGenreSimilarMatrix=new SimilarMatrix().generateItemSimilarMatrix(genreMatrix);

        //对用户1进行需求预测，返回probsPredicts[i]表示user_1对i-th内容的预测请求概率
        double[] probsPredicts=indiPre.getUnBrowsedContentPredictProbsWithGenreMatrix(
                reqHisArr,rates,RADIO, historyReqPrb,itemGenreSimilarMatrix);

        int[] topKPredict=indiPre.pickTopKItem(probsPredicts,K);

        Set<Integer> topKPredictSet=new HashSet<Integer>();

        for(int ele:topKPredict){
            topKPredictSet.add(ele);
        }

        return topKPredictSet;
    }



    public double computeSingleUserHitRateFromTestDataFile(List<String> lineList,int user_id,Set<Integer> predictSet){


        Set<Integer> userActualReq=getActualRequestSet(lineList,user_id);

        return computeHitRate(predictSet,userActualReq);

    }

    //lineList test data文件读取之后的行列表
    public static Set<Integer> getActualRequestSet(List<String> lineList,int user_id){

        Set<Integer> userActualReq=new HashSet<Integer>();
        for(String str : lineList){
            String[] strArr=str.split("\t");
            if(Integer.valueOf(strArr[0])!=user_id){
                continue;
            }else{
                userActualReq.add(Integer.valueOf(strArr[1]));
            }
        }

        return userActualReq;
    }

    public static double computeHitRate(Set<Integer> predict,Set<Integer> actual){
        int hitNum=0;
        for(int i:actual){
            if(predict.contains(i)){
                hitNum+=1;
            }
        }
        return hitNum*1.0/actual.size();
    }
}
