import redis.clients.jedis.Jedis;
import util.InfoMatrixGenerating;
import util.ItemRequestHitoryProbComputing;
import util.RedisPoolConnection;

import java.util.List;

public class IndividualPredictionMain {




    public static void main(String[] args) throws Exception {
        IndividualPrediction indiPre=new IndividualPrediction();

        //由历史访问获得的文件历史访问概率
        double[] historyReqPrb= ItemRequestHitoryProbComputing.computeHistoryRequestProb();

        Jedis jedis=new RedisPoolConnection().getLocalJedis();

        //找出用户1的历史访问记录
        List<String> reqHistory=jedis.lrange("user_"+1,0,-1);

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

        int[] topKPredict=indiPre.pickTopKItem(probsPredicts,100);


        double total=0;

        //for(int ele:topKPredict){
        //    total+=probsPredicts[ele];
        //}

        for(int i=0;i<probsPredicts.length;i++){
            total+=probsPredicts[i];
        }


        //for(int i=262;i<269;i++){
         //   System.out.println(probsPredicts[i]);
       // }
        System.out.println(total);
    }
}
