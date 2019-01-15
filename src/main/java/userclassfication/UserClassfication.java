package userclassfication;


import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//根据用户访问项目频率对用户进行分级，初步分为四级
public class UserClassfication {

    private static String TRAIN_DATA_BASE = "traindata_new";

    private static String COUNT_SQL_HEAD = "select count(*) from ";


    public Map<Integer, Set<Integer>> classUserIntoLevelSet(int[] UserAccessNum,int levelNum){


        List<UserAndAccessFrequency> userAndAccessFreqList=new ArrayList<>();
        int totalNum=0;
        for(int i:UserAccessNum){
            totalNum+=i;
        }


        double[] userAcessFrequency=getUserAccessFrequency(UserAccessNum);
        for(int i=1;i<=UserAccessNum.length-1;i++){
            UserAndAccessFrequency uaf=new UserAndAccessFrequency(i,userAcessFrequency[i]);
            userAndAccessFreqList.add(uaf);
        }


        userAndAccessFreqList.sort(new Comparator<UserAndAccessFrequency>() {
            @Override
            public int compare(UserAndAccessFrequency o1, UserAndAccessFrequency o2) {
                if(o1.frequency>o2.frequency){
                    return 1;
                }else if(o1.frequency<o2.frequency){
                    return -1;
                }else {
                    return 0;
                }
            }
        });

        //level 1 2 3 4，level 4 级别最高，应该赋予最大重要性
        Map<Integer, Set<Integer>> levelToUserMemberMap=new HashMap<>();

        int levelUserNum=(UserAccessNum.length-1)/levelNum;

        //这之中不分类最高level中的元素
        for(int i=1;i<=levelNum-1;i++){

            Set<Integer> levelUserSet=new HashSet<>();
            for(int j=(i-1)*levelUserNum;j<i*levelUserNum;j++){
                levelUserSet.add(userAndAccessFreqList.get(j).getUserId());
            }

            levelToUserMemberMap.put(i,levelUserSet);
        }

        Set<Integer> topLevelUserSet=new HashSet<>();
        for(int j=(levelNum-1)*levelUserNum;j<userAndAccessFreqList.size();j++){
            topLevelUserSet.add(userAndAccessFreqList.get(j).getUserId());
        }

        levelToUserMemberMap.put(levelNum,topLevelUserSet);

        return levelToUserMemberMap;


    }


    //下标都是从1开始，表示userId
    public double[] getUserAccessFrequency(int[] UserAccessNum){

        double[] result=new double[UserAccessNum.length];

        int totalNum=0;
        for(int i:UserAccessNum){
            totalNum+=i;
        }
        for(int i=1;i<=UserAccessNum.length-1;i++){
            double freq=UserAccessNum[i]*1.0/totalNum;

            result[i]=freq;
        }

        return result;

    }

    public int[] getUserAcessNum(int maxUserId, int maxItemId) {

        //都是从下标1开始，下标对应用户id
        int[] allUserAccessNum = new int[maxUserId + 1];


        Connection conn = WriteAndReadDataWithDB.getConn();
        PreparedStatement pstat = null;
        String sql = COUNT_SQL_HEAD + TRAIN_DATA_BASE + " where userid=? and itemid<=?";
        try {
            pstat = conn.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println(e.getStackTrace());
            return null;
        }


        try {
            for (int i = 1; i <= maxUserId; i++) {
                pstat.setInt(1, i);
                pstat.setInt(2, maxItemId);

                ResultSet result = pstat.executeQuery();
                result.next();
                allUserAccessNum[i] = result.getInt(1);
                System.out.println("the " + i + "-th user access got and is " + allUserAccessNum[i]);
                result.close();
            }

        } catch (SQLException e) {
            System.out.println(e.getStackTrace());
        } finally {
            try {
                pstat.close();
                conn.close();
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }


        return allUserAccessNum;

    }


}

class UserAndAccessFrequency {
    int userId;
    double frequency;

    public UserAndAccessFrequency(int userId, double frequency) {
        this.userId = userId;
        this.frequency = frequency;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}