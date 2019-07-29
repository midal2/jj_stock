package jj.biztrip.comm;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getDateStr(String pattern){
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(new Date());
    }

    public static String getDateStr(){
        return getDateStr("yyMMddHHmmss");
    }
}
