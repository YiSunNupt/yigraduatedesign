package util;
import com.sun.org.apache.xerces.internal.impl.dv.xs.TimeDV;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class WriteAndReadDataWithDB {

    private static Connection conn = getConn();
    private static PreparedStatement pstmt=null;
    private static String insertAllDataSql = "insert into alldata (userid,itemid,rating,timestamp) values(?,?,?,?)";
    private static String insertTrainDataSql = "insert into traindata (userid,itemid,rating,timestamp) values(?,?,?,?)";
    private static String insertTestDataSql = "insert into testdata (userid,itemid,rating,timestamp) values(?,?,?,?)";

    private static String insertNewTrainDataSql = "insert into traindata_new (userid,itemid,rating,timestamp) values(?,?,?,?)";
    private static String insertNewTestDataSql = "insert into testdata_new (userid,itemid,rating,timestamp) values(?,?,?,?)";



    private static String queryAllDataSql="select userid,itemid,rating,timestamp from alldata order by timestamp ASC limit ?,?";
    private static String queryTrainDataSql="select userid,itemid,rating,timestamp from taindata order by timestamp ASC limit ?,?";
    private static String queryTestDataSql="select userid,itemid,rating,timestamp from testdata order by timestamp ASC limit ?,?";


    public static void generateNewTrainAndTestData(double trainDataRadio){
        String queryAllDataWithUserIdSql="select userid,itemid,rating,timestamp from alldata where userid=?";
        for(int i=1;i<=943;i++){
            int dataCount=1;
            try{
                pstmt = conn.prepareStatement(queryAllDataWithUserIdSql);

                pstmt.setInt(1,i);

                ResultSet res=pstmt.executeQuery();
                res.last();
                int rows = res.getRow();
                int throldNum=(int) (rows*trainDataRadio);

                res.first();

                PreparedStatement insertPstmt;
                do{
                    if(dataCount<=throldNum){
                        insertPstmt=conn.prepareStatement(insertNewTrainDataSql);

                    }else{
                        insertPstmt=conn.prepareStatement(insertNewTestDataSql);

                    }
                    insertPstmt.setInt(1,res.getInt(1));
                    insertPstmt.setInt(2,res.getInt(2));
                    insertPstmt.setInt(3,res.getInt(3));
                    insertPstmt.setInt(4,res.getInt(4));
                    int InsertFlag=insertPstmt.executeUpdate();
                    dataCount++;
                    if(InsertFlag>0){
                        System.out.println("write the "+dataCount+"-th data success");
                    }else{
                        System.out.println("write the "+dataCount+"-th data failed");
                    }

                }while(res.next());

            }catch(SQLException sqle){
                System.out.println(sqle.getStackTrace());
            }
        }
        if(pstmt!=null){
            try {
                pstmt.close();
            } catch (SQLException e){
                System.out.println(e.getStackTrace());
            }
        }
        if(conn!=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getStackTrace());
            }
        }

    }


    //1：训练数据，0：测试数据
    public static void generateTrainAndTestData(boolean isTrainData){
        int count=0;
        try{

            pstmt = conn.prepareStatement(queryAllDataSql);

            if(isTrainData) {
                pstmt.setInt(1, 0);
                pstmt.setInt(2, 80000);
                //pstmt.setString(2,String.valueOf(0));
                //pstmt.setString(3,String.valueOf(80000));
            }else{
                pstmt.setInt(1, 80000);
                pstmt.setInt(2, 20000);
            }


            ResultSet res=pstmt.executeQuery();
            int userId;
            int itemId;
            int rating;
            int timeStamp;
            PreparedStatement insertPstmt;


            if(isTrainData) {
                insertPstmt= conn.prepareStatement(insertTrainDataSql);
            }else{
                insertPstmt= conn.prepareStatement(insertTestDataSql);
            }

            while(res.next()){
                count++;

                userId=res.getInt(1);
                itemId=res.getInt(2);
                rating=res.getInt(3);
                timeStamp=res.getInt(4);

                insertPstmt.setInt(1,userId);
                insertPstmt.setInt(2,itemId);
                insertPstmt.setInt(3,rating);
                insertPstmt.setInt(4,timeStamp);
                int InsertFlag=insertPstmt.executeUpdate();
                if(InsertFlag>0){
                    System.out.println("write the "+count+"-th data success");
                }else{
                    System.out.println("write the "+count+"-th data failed");
                }
            }
        }catch (SQLException sqle){
            System.out.println(sqle.getStackTrace());
        }finally {

            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e){
                    System.out.println(e.getStackTrace());
                }
            }
            try {
                conn.close();
            }catch (SQLException e){
                System.out.println(e.getStackTrace());
            }

        }
    }

    public static void writeDataToDB() {

        List<String> DataStringList = DataReadAndWrite.readFromFile(
                "F:\\chromeDownload\\ml-100k\\u1.test");

        int count=0;

        try {
            pstmt=conn.prepareStatement(insertTestDataSql);
            for (String str : DataStringList) {
                count++;
                String[] strArr = str.split("\t");
                pstmt.setString(1, strArr[0]);
                pstmt.setString(2, strArr[1]);
                pstmt.setString(3, strArr[2]);
                pstmt.setString(4, strArr[3]);
                int res=pstmt.executeUpdate();
                if(res>0){
                    System.out.println("write the "+count+"-th data success");
                }else{
                    System.out.println("write the "+count+"-th data failed");
                }
            }

        }catch (SQLException e){
            System.out.println(e.getStackTrace());
        }finally {
            if(pstmt!=null){
                try {
                    pstmt.close();
                } catch (SQLException e){
                    System.out.println(e.getStackTrace());
                }
            }
            try {
                conn.close();
            }catch (SQLException e){
                System.out.println(e.getStackTrace());
            }

        }
    }

    public static void main(String[] args){
        generateNewTrainAndTestData(0.8);

    }

    protected static Connection getConn() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/yigraduate";
        String username = "user";
        String password = "12345";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


}
