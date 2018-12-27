package util;


import java.io.*;
import java.util.List;
import java.util.Set;

//用于获取文件的类型矩阵，评分矩阵
public class InfoMatrixGenerating {


    public static void writeUserMatrixFile(){
        List<String> itemsList = new DataReadAndWrite().readFromFile(
                "F:\\chromeDownload\\ml-100k\\ml-100k\\u.user");



        File file =new File("F:\\chromeDownload\\ml-100k\\ml-100k\\usermatrix.txt");
        //if file doesnt exists, then create it
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());

            BufferedWriter bw = new BufferedWriter(fw);


            //写入usermatrix
            for(String line:itemsList){
                String[] lineEleArr=line.split("\\|");
                String content="";

                //年龄，以五岁为一个阶段
                content+=String.valueOf(Integer.valueOf(lineEleArr[1])/5);
                content+=" ";


                //M: male 0,F: female 1
                if(lineEleArr[2].equals("M")){
                    content+="0";
                    content+=" ";
                }else{
                    content+="1";
                    content+=" ";
                }


                if(lineEleArr[3].equals("administrator")){
                    content+="1";
                    content+=" ";
                }else if(lineEleArr[3].equals("artist")){
                    content+="2";
                    content+=" ";
                }else if(lineEleArr[3].equals("doctor")){
                    content+="3";
                    content+=" ";
                }else if(lineEleArr[3].equals("educator")){
                    content+="4";
                    content+=" ";
                }else if(lineEleArr[3].equals("engineer")){
                    content+="5";
                    content+=" ";
                }else if(lineEleArr[3].equals("entertainment")){
                    content+="6";
                    content+=" ";
                }else if(lineEleArr[3].equals("executive")){
                    content+="7";
                    content+=" ";
                }else if(lineEleArr[3].equals("healthcare")){
                    content+="8";
                    content+=" ";
                }else if(lineEleArr[3].equals("homemaker")){
                    content+="9";
                    content+=" ";
                }else if(lineEleArr[3].equals("lawyer")){
                    content+="10";
                    content+=" ";
                }else if(lineEleArr[3].equals("librarian")){
                    content+="11";
                    content+=" ";
                }else if(lineEleArr[3].equals("marketing")){
                    content+="12";
                    content+=" ";
                }else if(lineEleArr[3].equals("none")){
                    content+="13";
                    content+=" ";
                }else if(lineEleArr[3].equals("other")){
                    content+="14";
                    content+=" ";
                }else if(lineEleArr[3].equals("programmer")){
                    content+="15";
                    content+=" ";
                }else if(lineEleArr[3].equals("retired")){
                    content+="16";
                    content+=" ";
                }else if(lineEleArr[3].equals("salesman")){
                    content+="17";
                    content+=" ";
                }else if(lineEleArr[3].equals("scientist")){
                    content+="18";
                    content+=" ";
                }else if(lineEleArr[3].equals("student")){
                    content+="19";
                    content+=" ";
                }else if(lineEleArr[3].equals("technician")){
                    content+="20";
                    content+=" ";
                }else if(lineEleArr[3].equals("writer")){
                    content+="21";
                    content+=" ";
                }


                //这里读取了u1.test中的数据，为了数据更好一点，加一下u1.test的数据吧
                int[] requestHistory= ItemRequestHitoryProbComputing.getRequestHistory(Integer.valueOf(lineEleArr[0]));


                int[] requestHistoryInTest=getTestData("F:\\chromeDownload\\ml-100k\\ml-100k\\u1.test",lineEleArr[0]);

                for(int i=1;i<=1682;i++){
                    content+=String.valueOf(requestHistory[i]+requestHistoryInTest[i]);
                    content+=" ";

                }


                content=content.substring(0,content.length()-1);

                bw.write(content);
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static int[] getTestData(String path,String user_id) {

        int[] ItemRateRecord=new int[1683];

        List<String> lineList=DataReadAndWrite.readFromFile(path);
        for(String lineStr:lineList){
            String[] linStrArr=lineStr.split("\t");
            if(linStrArr[0].equals(user_id)){
                ItemRateRecord[Integer.valueOf(linStrArr[1])]=Integer.valueOf(linStrArr[2]);
            }
        }

        return ItemRateRecord;
    }


    public static void WriteGenreMatrixFile() {

        List<String> itemsList = new DataReadAndWrite().readFromFile(
                "F:\\chromeDownload\\ml-100k\\ml-100k\\u.item");


        File file =new File("F:\\chromeDownload\\ml-100k\\ml-100k\\genrematrix.txt");
        //if file doesnt exists, then create it
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());

            BufferedWriter bw = new BufferedWriter(fw);


            //写入genrematrix
            for(String line:itemsList){
                String[] lineEleArr=line.split("\\|");
                String content="";
                for(int i=5;i<24;i++){
                  content+=lineEleArr[i];
                  content+=" ";
                }
                content=content.substring(0,content.length()-1);

                bw.write(content);
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //参数matrix文件路径，行数，列数，分隔符
    public static int[][] getInfoMatrix(String path,int rows,int cols,String delimiter) throws Exception {
        List<String> lineList = new DataReadAndWrite().readFromFile(path);

        int[][] infoMatrix=new int[rows][cols];

        if(lineList.size()<rows){
            throw new Exception("矩阵文件未能填充满您所需要的特征矩阵:行数不够");
        }

        for(int i=0;i<rows;i++){
            String line=lineList.get(i);


            String[] lineArr=line.split(delimiter);
            if(lineArr.length<cols){
                throw new Exception("矩阵文件未能填充满您所需要的特征矩阵:列数不够");
            }


            for (int j=0;j<lineArr.length;j++){
                infoMatrix[i][j]=Integer.valueOf(lineArr[j]);
            }

        }

        return infoMatrix;

    }

}
