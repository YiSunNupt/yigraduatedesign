import mostpopular.MostPopularCache;
import org.junit.Test;
import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.util.Set;

public class MostPopularCacheTest {

    Connection connection= WriteAndReadDataWithDB.getConn();

    @Test
    public void testGetTopKItemMostPopular() throws Exception {

        Set<Integer> results= MostPopularCache.getTopKItemMostPopular(connection,30,300,50);

        for(int i:results){
            System.out.println(i);
        }
    }
}
