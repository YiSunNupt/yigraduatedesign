package util;

import redis.clients.jedis.Jedis;
import util.RedisPoolConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DataReadAndWrite {


    public void readAndStore(String path,Jedis jedis){
        List<String> list=readFromFile(path);
        storeDataInDataBase(list,jedis);
    }


    public static List<String> readFromFile(String path){
        List<String> list = new ArrayList<String>();
        try
        {
            String encoding = "GBK";
            File file = new File(path);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return list;
    }






    //数据库打算使用redis
    public void storeDataInDataBase(List<String> dataList,Jedis jedis){

        if(jedis!=null) {
            System.out.println("Redis连接成功");
        }else{
            System.out.println("Redis连接失败");
            return;
        }
        try {
            for (String line : dataList) {
                String[] lineArr = line.split("\t");

                jedis.lpush("user_" + lineArr[0], lineArr[1] + "_" + lineArr[2]);
            }
        } catch (Exception e){
            e.printStackTrace();
    }
    }



}
