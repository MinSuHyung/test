package com.rest.api.service;

import com.rest.api.model.response.CommonResult;
import com.rest.api.model.response.ListResult;
import com.rest.api.model.response.SingleResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    public enum CommonResponse {
        SUCCESS(0, "성공하였습니다."),
        FAIL(-1, "실패하였습니다."),
        DUPLICATE(-10, "중복호출입니다."),
        NO_EXIST(-2, "결제내역이 존재하지 않습니다."),
        CHK_CARDNUM(-3,"카드번호는(10~16자리 숫자)입니다."),
        CHK_VALIDPERIOD(-4,"유효기간은(4자리숫자,mmyy)입니다."),
        CHK_CVC(-5,"cvc는(3자리숫자)입니다."),
        CHK_INST(-6,"할부개월수는 0(일시불), 1~12입니다."),
        CHK_PAYAMT(-7,"결제금액은(100원이상, 10억원 이하, 숫자)입니다."),
        CHK_CANAMT(-8,"취소하려는 금액이 남은 결제금액보다 큽니다."),
        CHK_CANVATAMT(-9,"취소하려는 부가가치세 금액이 남은 부가가치세 금액보다 큽니다."),
        CHK_CANBALVATAMT(-9,"취소후 남은 부가가치세 금액이 남은 결제 금액보다 큽니다.");

        int code;
        String msg;

        CommonResponse(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    // 단일건 결과를 처리하는 메소드
    public <T> SingleResult<T> getSingleResult(T data, CommonResponse code ) {
        SingleResult<T> result = new SingleResult<>();
        result.setData(data);
        setCommonResult(result, code);
        return result;
    }
    // 다중건 결과를 처리하는 메소드
    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setList(list);
        setSuccessResult(result);
        return result;
    }
    // 성공 결과만 처리하는 메소드
    public CommonResult getSuccessResult() {
        CommonResult result = new CommonResult();
        setSuccessResult(result);
        return result;
    }
    // 실패 결과만 처리하는 메소드
    public CommonResult getFailResult() {
        CommonResult result = new CommonResult();
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
        return result;
    }
    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setSuccessResult(CommonResult result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setCommonResult(CommonResult result, CommonResponse code) {
        if(code.getCode() < 0) {
            result.setSuccess(false);
        } else {
            result.setSuccess(true);
        }
        result.setCode(code.getCode());
        result.setMsg(code.getMsg());
    }
}


