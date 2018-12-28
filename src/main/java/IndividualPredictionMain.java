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


    //public static void main(String[] args) throws Exception {


     //   List<String> lineList=DataReadAndWrite.readFromFile("F:\\chromeDownload\\ml-100k\\ml-100k\\u1.test");

     //   int K=10;
     //   int count=0;

     //   for(int i=1;i<=462;i++) {

      //      System.out.println("=====i="+i);

     //       Set<Integer> predictSet = indiPreMain.getTopKPredictItemOfSingleUser(i, K);


            //有的用户并没有请求
            // i>=463的用户都没有再做请求，所以此用户之后均返回NaN

            //double[] individualPredictHitRate=new double[944];

            //
     //       double individualPredictHitRate = indiPreMain.computeSingleUserHitRateFromTestDataFile(
      //              lineList,
     //               i,
       //             predictSet);


            //根据历史数据得出的历史访问概率，进行最流行缓存，与我们提出的个人预测并缓存作比较
       //     Set<Integer> topKHistoryRequestSet = new HashSet<Integer>();
      //      int[] topKHistoryRequestedItem = indiPre.pickTopKItem(historyReqPrb, K);

       //     for (int ele : topKHistoryRequestedItem) {
        //        topKHistoryRequestSet.add(ele);
       //     }

        //    Set<Integer> userActualReq = getActualRequestSet(lineList, i);
        //    double MostPopularCacheHirRate = computeHitRate(topKHistoryRequestSet, userActualReq);

        //    if(individualPredictHitRate-MostPopularCacheHirRate>=0){
       //         System.out.println("count increse");
       //         count++;
       //     }
       // }


      //  System.out.println(count);

    //}

    public static double[] getHitRateOnMostPopularCache(int cacheNum,int usersNum){

        double[] hitRateOfAllUsers=new double[usersNum+1];

        //根据历史数据得出的历史访问概率，进行最流行缓存，与我们提出的个人预测并缓存作比较
        Set<Integer> topKHistoryRequestSet = new HashSet<Integer>();
        int[] topKHistoryRequestedItem = indiPre.pickTopKItem(historyReqPrb, cacheNum);

        for (int ele : topKHistoryRequestedItem) {
                topKHistoryRequestSet.add(ele);
        }


        List<String> lineList=DataReadAndWrite.readFromFile("F:\\chromeDownload\\ml-100k\\ml-100k\\u1.test");
        for(int i=1;i<hitRateOfAllUsers.length;i++) {
            Set<Integer> userActualReq = getActualRequestSet(lineList, i);

            hitRateOfAllUsers[i]=computeHitRate(topKHistoryRequestSet,userActualReq);
        }

        return hitRateOfAllUsers;


    }

    public double getHitRateAfterPredictBySimilarUserPrefrence(int user_id,int similarUserNum,int CacheNum_K) throws Exception {


        int[][] userMatrix=SimilarMatrix.getUserMatrix("F:\\chromeDownload\\ml-100k\\ml-100k\\usermatrix.txt");

        double radio1=1;
        double radio2=1;

        int itemNum=1682;

        double[][] userSimilarMatrix=new SimilarMatrix().generateUserSimilarMatrix(userMatrix,radio1,radio2);

        int[] topKSimilarUsers=getTopKSimilarUsers(userSimilarMatrix,user_id,similarUserNum);

        //返回这些相似用户最喜欢的K个item序号
        int[] topKPrefrenceItem=getTopKitemsWithSimilarUsers(topKSimilarUsers,CacheNum_K,itemNum);



        //计算缓存命中概率
        List<String> lineList=DataReadAndWrite.readFromFile("F:\\chromeDownload\\ml-100k\\ml-100k\\u1.test");

        Set<Integer> userActualReq = getActualRequestSet(lineList, user_id);

        Set<Integer> prerictItemSet=new HashSet<Integer>();

        for(int item:topKPrefrenceItem){
            prerictItemSet.add(item);
        }
        double MostPopularCacheHirRate = computeHitRate(prerictItemSet, userActualReq);

        return MostPopularCacheHirRate;

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


    //找出与user_id这个用户相似的topK个用户
    public int[] getTopKSimilarUsers(double[][] userSimilarMatrix,int user_id,int K){
        int userNum=userSimilarMatrix.length;


        double[] similarVector=userSimilarMatrix[user_id-1];


        //调整原来的相似向量，因为原来的相思向量下标i表示第i+1个用户，纠正后下标和用户序号一致
        double[] similarVectorAfterAdjustIndex=new double[similarVector.length+1];

        for(int i=1;i<similarVectorAfterAdjustIndex.length;i++){
            similarVectorAfterAdjustIndex[i]=similarVector[i-1];
        }


        int[] topKUsers=indiPre.pickTopKItem(similarVectorAfterAdjustIndex,K);

        return topKUsers;

    }

    //根据相似用户寻找这些用户最喜欢的item,
    public int[] getTopKitemsWithSimilarUsers(int[] similarUsers,int K,int itemNum){

        double[] itemReRating=new double[itemNum+1];
        int[] userNumPreferOnItem=new int[itemNum+1];

        for(int i:similarUsers){

            //返回的数组是用户i的访问历史，下标i对于i-th文件，存放的内容是用户对这个文件的打分
            int [] requestHistoryOfUser_I=ItemRequestHitoryProbComputing.getRequestHistory(i);

            for(int j=1;j<requestHistoryOfUser_I.length;j++){
                itemReRating[j]+=requestHistoryOfUser_I[j];
                if(requestHistoryOfUser_I[j]!=0){
                    userNumPreferOnItem[j]+=1;
                }
            }
        }

        for(int n=1;n<itemReRating.length;n++){
            if(userNumPreferOnItem[n]==0) {
                itemReRating[n]=0;
            }else{
                itemReRating[n] = itemReRating[n] * 1.0 / userNumPreferOnItem[n];
            }
        }


        return indiPre.pickTopKItem(itemReRating,K);

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
