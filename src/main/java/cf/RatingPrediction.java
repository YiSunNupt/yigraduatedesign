package cf;

import util.UserDataProcessing;
import util.WriteAndReadDataWithDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RatingPrediction {

    //item id 上限
    //public static int ITEM_NUM_LIMIT=300;

    //public static int NEAREST_K_VALUE=5;

    private static Connection connection=WriteAndReadDataWithDB.getConn();


    //生成包含所有用户评分的二维矩阵
    public double[][] getRatingsIncludingPrediction(int maxUserId, int maxItemId,int SimilarNeighborNum){

        double[][] ratings=new double[maxUserId+1][maxItemId+1];
        for(int userId=1;userId<=maxUserId;userId++){

            double[] userRatings=userRatingPredict(userId,maxUserId,maxItemId,SimilarNeighborNum);

            for(int itemId=1;itemId<=maxItemId;itemId++){
                ratings[userId][itemId]=userRatings[itemId];
            }

            System.out.println("the "+userId+"-th user's ratings are put in ======================" +
                    "=====================================================");

        }

        return ratings;

    }



    //K表示用于评分估计的最近邻居数,maxUserId用来截取一部分用户数据，而不是直接拿出所有用户数据
    //返回item对应的评分，下标i对应i-th item，所在位置的double值为评分
    public double[] userRatingPredict(int userId,int maxUserId, int maxItemId,int K){

        double aveRatingOfUser=getUserAverageRating(connection,userId,maxItemId);

        List<UserAndSimilarity> userSimilarityList=new ArrayList<UserAndSimilarity>();
        for(int i=1;i<=maxUserId;i++){
            UserAndSimilarity listEle=new UserAndSimilarity(i,userSimilarity(userId,i,maxItemId));
            userSimilarityList.add(listEle);
        }

        userSimilarityList.sort(new Comparator<UserAndSimilarity>() {
            @Override
            public int compare(UserAndSimilarity us1, UserAndSimilarity us2) {

                return Double.compare(us1.getSimlarity(),us2.getSimlarity());


               /* if (us1.getSimlarity()> us2.getSimlarity()) {
                    return 1;
                } else if (us1.getSimlarity()== us2.getSimlarity()) {
                    return 0;
                } else {
                    return -1;
                }*/
            }
        });

        Collections.reverse(userSimilarityList);

        int neighborNum;
        if(K+1<=maxUserId){
            neighborNum=K;
        }else{
            neighborNum=maxUserId;
        }

        double[] allRatingsOfUser=new double[maxItemId+1];

        for(int item=1;item<=maxItemId;item++){

            int rating=getRating(connection,userId,item,true);
            if(rating!=0){
                allRatingsOfUser[item]=rating;
            }else{

                double up=0;
                double down=0;
                //list的get(0)会返回自己，我们不要这个
                for(int i=1;i<=neighborNum;i++){

                    //i-th大的similarity的用户id与similarity
                    UserAndSimilarity singleUserSimilarity=userSimilarityList.get(i);
                    int anotherUserId=singleUserSimilarity.getIndex();
                    int neighborRating=getRating(connection,anotherUserId,item,true);
                    double aveRatingOfAnotherUser=getUserAverageRating(connection,anotherUserId,maxItemId);
                    if(neighborRating==0){
                        continue;
                    }else{
                        double similarityOfTwoUser=singleUserSimilarity.getSimlarity();
                        up+=(neighborRating-aveRatingOfAnotherUser)*similarityOfTwoUser;
                        down+=Math.abs(similarityOfTwoUser);
                    }
                }


                if(down==0){
                    allRatingsOfUser[item]=0;
                }else {
                    double endResult = aveRatingOfUser + up * 1.0 / down;
                    allRatingsOfUser[item] = endResult;
                }

                System.out.println("the "+item+"-th item's predict rating is "+allRatingsOfUser[item]);

            }
        }

        return allRatingsOfUser;




    }

    class UserAndSimilarity {



        private int index;
        private double simlarity;

        public UserAndSimilarity(int index,double similarity){
            this.index=index;
            this.simlarity=similarity;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getSimlarity() {
            return simlarity;
        }

        public void setSimlarity(double simlarity) {
            this.simlarity = simlarity;
        }





    }


    public static double userSimilarity(int u_id,int v_id,int maxItemId){
        double sim=0.0;
        Map<Integer,Integer> itemRateMap_u=getUserItemRateMap(u_id,maxItemId);

        Map<Integer,Integer> itemRateMap_v=getUserItemRateMap(v_id,maxItemId);

        Set<Integer> itemSet_u=itemRateMap_u.keySet();
        Set<Integer> itemSet_v=itemRateMap_v.keySet();
        //交集
        Set<Integer> intersectionSet=getIntersectionSet(itemSet_u,itemSet_v);

        double ave_u=getUserAverageRating(connection,u_id,maxItemId);
        double ave_v=getUserAverageRating(connection,v_id,maxItemId);

        for(int itemId:intersectionSet){
            int rate_u=getRating(connection,u_id,itemId,true);
            int rate_v=getRating(connection,v_id,itemId,true);
            sim+=userItemRatingSimilarity(rate_u,rate_v)*userItemInterestSimilarity(rate_u,rate_v,
                    ave_u, ave_v);
        }

        double userConfidence=userInterConfidence(u_id,v_id,maxItemId);

        if(intersectionSet.size()==0){
            return 0;
        }else {
            double simiResult = userConfidence * sim / intersectionSet.size();
            return simiResult;
        }

    }

    public static double getUserAverageRating(Connection connection,int userId,int maxItemId){
        String sql="select avg(rating) from traindata_new where userid="+userId+" and itemid<="+maxItemId;
        Connection conn= connection;
        PreparedStatement pst=null;
        try{
            pst=conn.prepareStatement(sql);
            ResultSet result=pst.executeQuery();
            double aveRating=0;
            if(result.next()) {
                aveRating = result.getDouble(1);
            }
            result.close();
            return aveRating;


        }catch (SQLException e){
            System.out.println(e.getStackTrace());
        }finally {
            if(pst!=null){
                try {
                    pst.close();
                } catch (SQLException e){
                    System.out.println(e.getStackTrace());
                }
            }

        }
        return 0;
    }






    public static double userItemRatingSimilarity(int u_rating,int v_rating){
        double res=2.0*(1.0-1.0/(1+Math.exp(-Math.abs(u_rating-v_rating))));
        return res;
    }

    public static double userItemInterestSimilarity(int u_rating,int v_rating,double u_aveRating,double v_aveRating){
        double res=2.0*(1.0-1.0/(1+Math.exp(-Math.abs((u_rating-u_aveRating)-(v_rating-v_aveRating)))));
        return res;
    }

    public static double userInterConfidence(int userId_u,int userId_v,int maxItemId){

        Map<Integer,Integer> itemRateMap_u=getUserItemRateMap(userId_u,maxItemId);

        Map<Integer,Integer> itemRateMap_v=getUserItemRateMap(userId_v,maxItemId);

        Set<Integer> itemSet_u=itemRateMap_u.keySet();
        Set<Integer> itemSet_v=itemRateMap_v.keySet();

        //并集
        Set<Integer> unionSet=getUnionSet(itemSet_u,itemSet_v);

        //交集
        Set<Integer> intersectionSet=getIntersectionSet(itemSet_u,itemSet_v);



        double res=(1.0*intersectionSet.size())/unionSet.size();
        return res;

    }

    //求并
    public static Set<Integer> getUnionSet(Set<Integer> a,Set<Integer> b){
        Set<Integer> unionSet=new HashSet<Integer>();
        unionSet.addAll(a);
        unionSet.addAll(b);
        return unionSet;
    }

    //求交

    public static Set<Integer> getIntersectionSet(Set<Integer> a,Set<Integer> b){
        Set<Integer> intersectionSet=new HashSet<Integer>();
        intersectionSet.addAll(a);
        intersectionSet.retainAll(b);
        return intersectionSet;
    }

    public static Map<Integer,Integer> getUserItemRateMap(int userid,int maxItemId){
        Map<Integer,Integer> itemRateMap=UserDataProcessing.getUserData(connection,
                userid,true,maxItemId);

        return itemRateMap;

    }

    public static int getRating(Connection connection,int userId,int itemId,boolean isTrainData){
        String sql;
        if(isTrainData){
            sql="select rating from traindata_new where userid="+userId+" and itemid="+itemId;

        }else{
            sql="select rating from testdata_new where userid="+userId+" and itemid="+itemId;
        }

        Connection conn= connection;
        PreparedStatement pst=null;
        try{
            pst=conn.prepareStatement(sql);
            ResultSet result=pst.executeQuery();

            int rating=0;
            if(result.next()) {
                rating = result.getInt(1);
            }
            result.close();
            return rating;

        }catch (SQLException e){
            System.out.println(e.getStackTrace());
        }finally {

            if(pst!=null){
                try {
                    pst.close();
                } catch (SQLException e){
                    System.out.println(e.getStackTrace());
                }
            }
        }
        return 0;
    }


    public static void  fianlCloseConnection(){
        if(connection!=null){
            try{
                connection.close();
            }catch (SQLException e){
                System.out.println("close Database Connection Failed");
                System.out.println(e.getStackTrace());
            }
        }
    }
}
