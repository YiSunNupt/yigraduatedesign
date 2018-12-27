package util;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemRequestHitoryProbComputing {

    private static Jedis jedis=new RedisPoolConnection().getLocalJedis();

    public static double[] computeHistoryRequestProb(){

        int[] itemHisReqNum=new int[1683];



        for(int i=1;i<=943;i++){
            List<String> reqHistory=jedis.lrange("user_"+i,0,-1);

            for(int j=0;j<reqHistory.size();j++){
                itemHisReqNum[Integer.valueOf(reqHistory.get(j).split("_")[0])]+=1;
            }
        }

        double[] itemHisReqProb=new double[1683];

        int requestTotalNum=0;
        for(int i=0;i<itemHisReqNum.length;i++){
            requestTotalNum+=itemHisReqNum[i];
        }

        for(int i=1;i<1683;i++){
            itemHisReqProb[i]=(itemHisReqNum[i]*1.0)/requestTotalNum;
        }

        return itemHisReqProb;

    }



    //返回访问历史访问文件序号相应的分数result[i]表示i-th被用户user_id打的分数
    public static int[] getRequestHistory(int user_id){

        int[] itemHisReqNum=new int[1683];

        List<String> reqHistoryList=jedis.lrange("user_"+user_id,0,-1);

        for(String ele:reqHistoryList){
            itemHisReqNum[Integer.valueOf(ele.split("_")[0])]=Integer.valueOf(ele.split("_")[1]);
        }

        return itemHisReqNum;
    }

}
