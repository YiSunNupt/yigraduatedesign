package util;


import java.io.*;
import java.util.List;

//用于获取文件的类型矩阵，评分矩阵
public class InfoMatrixGenerating {


    public static void WriteGenreMatrixFile() {

        List<String> itemsList = new TrainDataReadAndWriteToStorage().readFromFile(
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
        List<String> lineList = new TrainDataReadAndWriteToStorage().readFromFile(path);

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
