import org.junit.Test;
import util.UserDataProcessing;
import util.WriteAndReadDataWithDB;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

public class UserDataProcessingTest {

    @Test
    public void testGetUserData(){

        Connection conn= WriteAndReadDataWithDB.getConn();
        Map<Integer,Integer> result= UserDataProcessing.getUserData(conn,1,true,1682);

        System.out.println("rows: "+result.size());
        Set<Integer> set=result.keySet();

        for(Integer i:set){
            System.out.print(i+" : ");
            System.out.println(result.get(i));
        }
    }
}
