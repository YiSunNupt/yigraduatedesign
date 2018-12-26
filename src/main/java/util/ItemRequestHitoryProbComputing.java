package util;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;

public class ItemRequestHitoryProbComputing {

    public static double[] computeHistoryRequestProb(){

        int[] itemHisReqNum=new int[1683];

        Jedis jedis=new RedisPoolConnection().getLocalJedis();

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






}
