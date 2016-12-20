package Utils;

import java.io.File;

/**
 * Created by sdlds on 2016/11/17.
 */
public class AutoChangeFileName {
    private static int number = 1;
    private static String picNameHead = "test_";
    public static void main(String args[]){
        String path = "C:\\Users\\sdlds\\IdeaProjects\\TwitterDemo\\app\\src\\main\\res\\drawable\\Sherry";
        getFile(path);
    }

    public static void getFile(String path){
        File file = new File(path);
        File[] allFiles = file.listFiles();
        System.out.println(allFiles.length);
        for(int i=0;i<allFiles.length;i++){
            if(allFiles[i].isFile()){
                System.out.println(allFiles[i].getName());
                if(allFiles[i].renameTo(new File(path+"\\"+picNameHead+number+".jpg"))){
                    number++;
                    System.out.println(allFiles[i].getName());
                }
            }
            else{
                if(allFiles[i].isDirectory()){
                    getFile(allFiles[i].getPath());
                }
            }
        }
    }
}
