package itemrating;

import java.util.Map;
import java.util.Set;

public class ItemRatingAfterAHPWeight {

    //下标对应等级
    private static double[] levelWeight=new double[]{0,0.0517,0.1448,0.2588,0.5477};


    //获得AHP加权后每个item的评分
    public static double[] getItemRatingAferAHP(double[][] userToRating, Map<Integer, Set<Integer>> levelUserMap){

        //将等级与用户id转换成userId对应level的数组，其中数组下标表示用户id
        Set<Integer> levelIDSet=levelUserMap.keySet();
        int[] userToLevelArr=new int[userToRating.length];

        for(int level:levelIDSet){
            Set<Integer> LevelUserSet=levelUserMap.get(level);
            for(int userInOneLevelSet:LevelUserSet){
                userToLevelArr[userInOneLevelSet]=level;
            }
        }


        double[] itemTotalRating=new double[userToRating[0].length];
        //计算每个item的综合加权评分，i表示itemid
        for(int i=1;i<userToRating[0].length;i++){

            //先计算每个等级用户对这个item的平均评分
            double[] AverageRatingOfEveryLevel=new double[levelUserMap.size()+1];
            //j表示用户id
            for(int j=1;j<userToRating.length;j++){
                AverageRatingOfEveryLevel[userToLevelArr[j]]+=userToRating[j][i];
            }

            for(int level:levelIDSet){
                //对应一个item，不同等级用户的平均分
                AverageRatingOfEveryLevel[level]=AverageRatingOfEveryLevel[level]*1.0/levelUserMap.get(level).size();
            }

            //根据不同等级用户的平均评分，做加权处理获得最终item评分
            double totalRating=0.0;
            for(int level:levelIDSet){
                totalRating+=AverageRatingOfEveryLevel[level]*levelWeight[level];
            }

            itemTotalRating[i]=totalRating;
        }

        return itemTotalRating;
    }
}
