import Utils.Utils;

import java.io.IOException;

/**
 * Created by sdlds on 2016/7/26.
 */
public class OnlineJudge {
    public static void main(String[] args){

    }
    private static void find_problem() throws IOException{
        String URL = "http://112.124.38.1/OnlineJudge/problem/show/";
        for(int i=100;i<100000;i++){
            String final_URL = URL + i;
            String originstr = Utils.streamToString(Utils.getUrlStream(final_URL));
            
        }
    }
}
