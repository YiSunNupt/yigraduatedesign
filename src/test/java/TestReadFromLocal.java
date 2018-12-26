import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import util.DataReadAndWrite;
import util.RedisPoolConnection;

import java.util.List;

public class TestReadFromLocal {

    //public static void main(String[] args){
        //List<String> result=new util.DataReadAndWrite().readFromFile(
        //        "F:\\chromeDownload\\ml-100k\\ml-100k\\u1.base");

        //for(int i=0;i<20;i++) {
         //   System.out.println(result.get(i));
        //}

       // System.out.println(result.size());

        //连接本地的 Redis 服务
        //Jedis jedis = new Jedis("localhost",6379);
        //System.out.println("连接成功");
        //查看服务是否运行
        //System.out.println("服务正在运行: "+jedis.ping());




    //}

    Jedis jedis = new RedisPoolConnection().getLocalJedis();

    @Test
    public void testPutGet() {

        try {
            jedis.select(1);
            jedis.set("name","grace");
            System.out.println(jedis.get("name"));
            Assert.assertTrue("grace".equals(jedis.get("name")));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Test
    public void testReadAndStoreDate(){
        System.out.println("清空库中所有数据："+jedis.flushDB());
        new DataReadAndWrite().readAndStore("F:\\chromeDownload\\ml-100k\\ml-100k\\u1.base",jedis);

        List<String> list = jedis.lrange("user_2",0,-1);
        for(int i=0; i<list.size(); i++) {
            System.out.println("列表项为: "+list.get(i));
        }

        //System.out.println("清空库中所有数据："+jedis.flushDB());
    }


}
