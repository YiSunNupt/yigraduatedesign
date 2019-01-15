import org.junit.Test;
import userclassfication.UserClassfication;

import java.util.Map;
import java.util.Set;

public class UserClassficationTest {


    @Test
    public void testGetUserAcessNum(){
        int[] result=new UserClassfication().getUserAcessNum(30,300);
        int i=0;
        for(int ele:result){
            System.out.println("the "+(i++)+"-th user's access num is "+ele);
        }
    }

    @Test
    public void testClassUserIntoLevelSet(){
        UserClassfication userClass=new UserClassfication();

        int[] userAccessNum=new UserClassfication().getUserAcessNum(30,300);
        Map<Integer, Set<Integer>> levelMap=userClass.classUserIntoLevelSet(userAccessNum,4);
        System.out.println();

    }
}
