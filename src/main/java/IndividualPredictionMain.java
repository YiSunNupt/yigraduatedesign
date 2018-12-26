import redis.clients.jedis.Jedis;
import util.DataReadAndWrite;
import util.InfoMatrixGenerating;
import util.ItemRequestHitoryProbComputing;
import util.RedisPoolConnection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndividualPredictionMain {




    public static void main(String[] args) throws Exception {
        IndividualPredictionMain indiPre=new IndividualPredictionMain();
        Set<Integer> predictSet=indiPre.getTopKPredictItemOfSingleUser(1,100);

        //有的用户并没有请求
        // i>=463的用户都没有再做请求，所以此用户之后均返回NaN
        for(int i=1;i<=943;i++) {
            System.out.print(i);
            System.out.print("  ");
            System.out.println(indiPre.computeHitRate(
                    "F:\\chromeDownload\\ml-100k\\ml-100k\\u1.test",
                    i,
                    predictSet));
        }
    }

    //返回预测的用户i最有可能请求的前K个item
    public Set<Integer> getTopKPredictItemOfSingleUser(int user_id,int K) throws Exception {
        IndividualPrediction indiPre=new IndividualPrediction();

        //由历史访问获得的文件历史访问概率
        double[] historyReqPrb= ItemRequestHitoryProbComputing.computeHistoryRequestProb();

        Jedis jedis=new RedisPoolConnection().getLocalJedis();

        //找出用户1的历史访问记录
        List<String> reqHistory=jedis.lrange("user_"+user_id,0,-1);

        //
        int[] reqHisArr=new int[1683];

        for(String ele:reqHistory){
            String[] eleArr=ele.split("_");
            reqHisArr[Integer.valueOf(eleArr[0])]=1;
        }


        int[][] genreMatrix=InfoMatrixGenerating.getInfoMatrix("F:\\chromeDownload\\ml-100k\\ml-100k\\genrematrix.txt",1682,19," ");

        double[][] itemGenreSimilarMatrix=new SimilarMatrix().generateItemSimilarMatrix(genreMatrix);

        //对用户1进行需求预测，返回probsPredicts[i]表示user_1对i-th内容的预测请求概率
        double[] probsPredicts=indiPre.getUnBrowsedContentPredictProbsWithGenreMatrix(
                reqHisArr, historyReqPrb,itemGenreSimilarMatrix);

        int[] topKPredict=indiPre.pickTopKItem(probsPredicts,K);

        Set<Integer> topKPredictSet=new HashSet<Integer>();

        for(int ele:topKPredict){
            topKPredictSet.add(ele);
        }

        return topKPredictSet;
    }



    public double computeHitRate(String testDataPath,int user_id,Set<Integer> predictSet){

        List<String> lineList=DataReadAndWrite.readFromFile(testDataPath);

        Set<Integer> userActualReq=new HashSet<Integer>();
        for(String str : lineList){
            String[] strArr=str.split("\t");
            if(Integer.valueOf(strArr[0])!=user_id){
                continue;
            }else{
                userActualReq.add(Integer.valueOf(strArr[1]));
            }
        }

        int hitNum=0;
        for(int i:userActualReq){
            if(predictSet.contains(i)){
                hitNum+=1;
            }
        }


        return hitNum*1.0/userActualReq.size();

    }
}
