package util;

import java.util.*;

public class UserToRatingArrPickTopK {

    public static Set<Integer> getTopKItem(double[] itemRatings,int K){

        ArrayList<ItemAndRatingPOJO> itemAndRatingList=new ArrayList<>();
        Set<Integer> topKItemIdSet=new HashSet<>();
        for(int i=1;i<itemRatings.length;i++){
            itemAndRatingList.add(new ItemAndRatingPOJO(i,itemRatings[i]));
        }

        itemAndRatingList.sort(new Comparator<ItemAndRatingPOJO>() {
            @Override
            public int compare(ItemAndRatingPOJO o1, ItemAndRatingPOJO o2) {

                return Double.compare(o1.getRating(),o2.getRating());

            }
        });

        Collections.reverse(itemAndRatingList);


        for(int i=0;i<K;i++){
            topKItemIdSet.add(itemAndRatingList.get(i).getItemId());
        }

        return topKItemIdSet;

    }

}

class ItemAndRatingPOJO{
    private int itemId;
    private double rating;

    public ItemAndRatingPOJO(int itemId, double rating) {
        this.itemId = itemId;
        this.rating = rating;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
