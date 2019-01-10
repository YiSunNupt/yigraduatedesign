package cf;

public class RatingPrediction {










    public static double userItemRatingSimilarity(int u_rating,int v_rating){
        return 2.0*(1.0-1.0/(1+Math.exp(-Math.abs(u_rating-v_rating))));
    }

    public static double userItemInterestSimilarity(int u_rating,int v_rating,double u_aveRating,double v_aveRating){
        return 2.0*(1.0-1.0/(1+Math.exp(-Math.abs((u_rating-u_aveRating)-(v_rating-v_aveRating)))));
    }

    public static double userInterConfidence(int userId_u,int userId_v){


        return 1.0;

    }

    //public void getUserData(int userid){

    //}
}
