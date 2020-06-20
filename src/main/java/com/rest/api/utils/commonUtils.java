package com.rest.api.utils;

import com.rest.api.entity.AmountInfo;
import com.rest.api.entity.CardInfo;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class commonUtils {
    public static String getCurrentDateId() {
        Date date_now = new Date(System.currentTimeMillis());
        Random m_random = new Random();

        SimpleDateFormat seventeen_format = new SimpleDateFormat("yyyyMMddHHmmssSS");
        //현재시각밀리세컨까지+2자리난수 -> 19자리 -> 앞에 P또는 C붙여서 20자리 만듬
        return seventeen_format.format(date_now)+String.valueOf(Math.abs(m_random.nextInt())%10)+String.valueOf(Math.abs(m_random.nextInt())%10);
    }

    public static String makeTransData(CardInfo cardInfo, Integer installment, AmountInfo amountInfo, String uId, String encCardInfo) {
        String returnStr = "";
        String cardNum = cardInfo.getCardNum();
        String validPeriod = cardInfo.getValidPeriod();
        String cvc = cardInfo.getCvc();
        Integer paymentAmount = amountInfo.getPaymentAmount();
        Integer vatAmount = amountInfo.getVatAmount();

        returnStr = makeStringByType(cardNum,3,20)
                + makeStringByType(installment.toString(),2,2)
                + makeStringByType(validPeriod,3,4)
                + makeStringByType(cvc,3,3)
                + makeStringByType(paymentAmount.toString(),1,10)
                + makeStringByType(vatAmount.toString(),1,10)
                + makeStringByType(uId,4,20)
                + makeStringByType(encCardInfo,4,300)
                + makeStringByType(" ",4,47);

        return returnStr;
    }

    public static String makeStringByType(String msg, int type, int strlen) {
        //type : 1 숫자    : 우측으로 정렬, 빈자리 공백, ex) 4자리 숫자 : 3 -> "   3"
        //type : 2 숫자(0) : 우측으로 정렬, 빈자리 0  , ex) 4자리 숫자 : 3 -> "0003"
        //type : 3 숫자(L) : 좌측으로 정렬, 빈자리 공백, ex) 4자리 숫자 : 3 -> "3   "
        //type : 4 문자    : 좌측으로 정렬, 빈자리 공백, ex) 4자리 문자 : ABC-> "ABC "
        String returnStr = "";
        if(msg.length()>strlen) {

        } else {
            if(type == 1) {
                returnStr = String.format("%" + strlen + "s", msg);
            } else if(type == 2 ) {
                returnStr = String.format("%" + strlen + "s", msg).replace(' ', '0');
            } else if(type == 3 ) {
                returnStr = String.format("%-" + strlen + "s", msg);
            } else if(type == 4 ) {
                returnStr = String.format("%-" + strlen + "s", msg);
            } else {

            }
        }
        return returnStr;
    }

    public static boolean isOnlyDigit(String str) throws RuntimeException {
        boolean isOnlyDigit = false;
        if(str!=null&&str.length()>0){
            String filteredStr = str.replaceAll("[0-9]","");
            if(filteredStr.length() == 0) {
                isOnlyDigit = true;
            } else {
                isOnlyDigit = false;
            }
        }
        return isOnlyDigit;
    }
}


