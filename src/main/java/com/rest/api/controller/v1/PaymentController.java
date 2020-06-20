package com.rest.api.controller.v1;

import com.rest.api.entity.*;
import com.rest.api.model.response.SingleResult;
import com.rest.api.model.response.CommonResult;
import com.rest.api.repo.CancellationJpaRepo;
import com.rest.api.repo.PaymentJpaRepo;
import com.rest.api.service.ResponseService;
import com.rest.api.utils.commonUtils;
import com.rest.api.utils.crypto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.rest.api.utils.commonUtils.isOnlyDigit;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class PaymentController {
    private final PaymentJpaRepo paymentJpaRepo;
    private final CancellationJpaRepo CancellationJpaRepo;
    private final ResponseService responseService;
    String Key = "Secret Key";


    @ApiOperation(value = "결제조회", notes = "UId로 결제정보를 조회한다")
    @GetMapping(value = "/GetPayment/{uid}")
    public SingleResult<GetResultDTO> findPaymentById(@ApiParam(value = "UID", required = true) @PathVariable String uid) {
        GetResultDTO resultDTO = null;
        List<CancellationResultDTO> cacellationResultDTOs = new ArrayList<>();
        CancellationResultDTO cacellationResultDTO = null;
        Payment payment = null;
        int cancellationSumAmount = 0;
        int cancellationVatSumAmount = 0;
        int balPayAmount = 0;
        int balVatAmount = 0;
        int paymentAmount = 0;
        int vatAmount = 0;
        String decCardInfo = "";
        String[] splitCardInfo = null;
        String maskedCardNum = "";
        String validPeriod = "";
        String cvc = "";
        LocalDateTime paymentTime = null;
        ResponseService.CommonResponse msgCode = ResponseService.CommonResponse.FAIL;

        try {
            payment = paymentJpaRepo.findByUid(uid);
            if (payment == null) {
                msgCode = ResponseService.CommonResponse.NO_EXIST;
            } else {

                if (payment.getCancellations() != null && payment.getCancellations().size() > 0) {
                    for (int i = 0; i < payment.getCancellations().size(); i++) {
                        cacellationResultDTO = CancellationResultDTO.builder()
                                .cancellationAmount(payment.getCancellations().get(i).getCancellationAmount())
                                .cancellationVatAmount(payment.getCancellations().get(i).getVatAmount())
                                .canceledAt(payment.getCancellations().get(i).getCreatedAt())
                                .build();
                        cancellationSumAmount = cancellationSumAmount + payment.getCancellations().get(i).getCancellationAmount();
                        cancellationVatSumAmount = cancellationVatSumAmount + payment.getCancellations().get(i).getVatAmount();

                        cacellationResultDTOs.add(cacellationResultDTO);
                    }
                }

                decCardInfo = crypto.decryptAES256(payment.getCardInfo(), Key);
                System.out.println(decCardInfo);
                if (decCardInfo != null) {
                    splitCardInfo = decCardInfo.split("\\|");
                }
                System.out.println(splitCardInfo[0]);
                System.out.println(splitCardInfo[1]);
                System.out.println(splitCardInfo[2]);

                String maskingString = new String(new char[splitCardInfo[0].length()-9]).replace("\0","*");
                maskedCardNum = splitCardInfo[0].substring(0,6)+maskingString+splitCardInfo[0].substring(splitCardInfo[0].length()-3);
                validPeriod = splitCardInfo[1];
                cvc = splitCardInfo[2];
                paymentTime = payment.getCreatedAt();
                paymentAmount = payment.getPaymentAmount();
                vatAmount = payment.getVatAmount();
                balPayAmount = paymentAmount - cancellationSumAmount;
                balVatAmount = vatAmount - cancellationVatSumAmount;

                resultDTO = GetResultDTO.builder()
                        .uid(uid)
                        .maskedCardNum(maskedCardNum)
                        .validPeriod(validPeriod)
                        .cvc(cvc)
                        .createdAt(paymentTime)
                        .paymentAmount(paymentAmount)
                        .vatAmount(vatAmount)
                        .cancellationSumAmount(cancellationSumAmount)
                        .cancellationVatSumAmount(cancellationVatSumAmount)
                        .cancellationResultDTOS(cacellationResultDTOs)
                        .balPayAmount(balPayAmount)
                        .balVatAmount(balVatAmount)
                        .build();

                msgCode = ResponseService.CommonResponse.SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            msgCode = ResponseService.CommonResponse.FAIL;
        } finally {
            return responseService.getSingleResult(resultDTO, msgCode);
        }

    }

    @ApiOperation(value = "결제API", notes = "카드결제정보를 전송한다.")
    @PostMapping(value = "/DoPayment")
    public SingleResult<PaymentResultDTO> save(
            @ApiParam(value = "카드번호", required = true) @RequestParam String cardNum,
            @ApiParam(value = "유효기간", required = true) @RequestParam String validPeriod,
            @ApiParam(value = "cvc", required = true) @RequestParam String cvc,
            @ApiParam(value = "할부개월수", required = true) @RequestParam Integer installment,
            @ApiParam(value = "결제금액", required = true) @RequestParam Integer paymentAmount,
            @ApiParam(value = "부가가치세", required = false) @RequestParam Integer vatAmount
    ) {
        String currentID = "";
        String encCardInfo = "";
        String transData = "";
        PaymentResultDTO paymentResultDTO = null;
        ResponseService.CommonResponse msgCode = ResponseService.CommonResponse.FAIL;

        try {
            //input check
            if( cardNum.length()<10 || cardNum.length()>16 ) {
                //카드번호(10~16자리 숫자)
                msgCode = ResponseService.CommonResponse.CHK_CARDNUM;
            } else if ( !isOnlyDigit(cardNum) ) {
                //카드번호(10~16자리 숫자)
                msgCode = ResponseService.CommonResponse.CHK_CARDNUM;
            } else if ( validPeriod.length() != 4 ) {
                //유효기간(4자리숫자,mmyy)
                msgCode = ResponseService.CommonResponse.CHK_VALIDPERIOD;
            } else if ( !isOnlyDigit(validPeriod) ) {
                //유효기간(4자리숫자,mmyy)
                msgCode = ResponseService.CommonResponse.CHK_VALIDPERIOD;
            }  else if ( Integer.parseInt(validPeriod.substring(0,2)) > 12 ) {
                //유효기간(4자리숫자,mmyy)
                msgCode = ResponseService.CommonResponse.CHK_VALIDPERIOD;
            } else if ( cvc.length() != 3 ) {
                //cvc(3자리숫자)
                msgCode = ResponseService.CommonResponse.CHK_CVC;
            } else if ( !isOnlyDigit(cvc) ) {
                //cvc(3자리숫자)
                msgCode = ResponseService.CommonResponse.CHK_CVC;
            } else if ( installment < 0 || installment > 12 ) {
                //할부개월수:0(일시불), 1~12
                msgCode = ResponseService.CommonResponse.CHK_INST;
            } else if ( paymentAmount < 100 || paymentAmount > 1000000000  ) {
                //결제금액(100원이상, 10억원 이하, 숫자)
                msgCode = ResponseService.CommonResponse.CHK_PAYAMT;
            } else {
                //부가세 계산
                if(vatAmount == null) {
                    vatAmount = Math.toIntExact(Math.round(paymentAmount / 11.0));
                }
                System.out.println(vatAmount);

                //필요 object 생성
                CardInfo cardInfo = CardInfo.builder().cardNum(cardNum).validPeriod(validPeriod).cvc(cvc).build();
                AmountInfo amountInfo = AmountInfo.builder().paymentAmount(paymentAmount).vatAmount(vatAmount).build();

                //카드정보 암호화
                encCardInfo = crypto.encryptAES256(cardNum+"|"+validPeriod+"|"+cvc, Key);
                System.out.println(encCardInfo);

                //unique ID 생성 ( payment uid 는 P로 시작 )
                currentID = "P"+commonUtils.getCurrentDateId();
                System.out.println(currentID);

                //카드사 전송데이터 생성
                //body정보생성
                transData = commonUtils.makeTransData(cardInfo, installment, amountInfo, currentID, encCardInfo);
                System.out.println(transData);
                //header정보추가
                transData = commonUtils.makeStringByType("446",1,4)
                        + commonUtils.makeStringByType("PAYMENT",4,10)
                        + commonUtils.makeStringByType(currentID,4,20)
                        + transData;
                System.out.println(transData);

                //결제정보저장
                LocalDateTime date = LocalDateTime.now();
                Payment payment = Payment.builder()
                        .uid(currentID)
                        .cardInfo(encCardInfo)
                        .installment(installment)
                        .paymentAmount(paymentAmount)
                        .vatAmount(vatAmount)
                        .transData(transData)
                        .createdAt(date)
                        .build();
                paymentJpaRepo.save(payment);

                //리턴할정보생성
                paymentResultDTO = PaymentResultDTO.builder()
                        .uId(currentID)
                        .transData(transData)
                        .build();
                msgCode = ResponseService.CommonResponse.SUCCESS;
            }



        } catch (Exception e) {
            e.printStackTrace();
            msgCode = ResponseService.CommonResponse.FAIL;
        } finally {
            return responseService.getSingleResult(paymentResultDTO, msgCode);
        }

    }



    @ApiOperation(value = "취소API", notes = "카드취소정보를 전송한다.")
    @PostMapping(value = "/CancelPayment")
    public SingleResult<PaymentResultDTO> cancel(
            @ApiParam(value = "관리번호uid", required = true) @RequestParam String paymentUid,
            @ApiParam(value = "취소금액", required = true) @RequestParam Integer cancellationAmount,
            @ApiParam(value = "부가가치세", required = false) @RequestParam Integer vatAmount
    ) {
        String currentID = "";
        String encCardInfo = "";
        String transData = "";
        String decCardInfo = "";
        String[] splitCardInfo = null;
        PaymentResultDTO paymentResultDTO = null;
        int cancellationSumAmount = 0;
        int cancellationVatSumAmount = 0;
        int balPayAmount = 0;
        int balVatAmount = 0;
        boolean calcVatYN = false;

        ResponseService.CommonResponse msgCode = ResponseService.CommonResponse.FAIL;
        try {
            //취소는 할부 0
            int installment = 0;

            // paymentUid 로 결제내역 조회
            Payment payment = paymentJpaRepo.findByUid(paymentUid);

            if (payment == null) {
                msgCode = ResponseService.CommonResponse.NO_EXIST;
            } else {
                //기취소금액 구하기
                if (payment.getCancellations() != null && payment.getCancellations().size() > 0) {
                    for (int i = 0; i < payment.getCancellations().size(); i++) {
                        cancellationSumAmount = cancellationSumAmount + payment.getCancellations().get(i).getCancellationAmount();
                        cancellationVatSumAmount = cancellationVatSumAmount + payment.getCancellations().get(i).getVatAmount();
                    }
                }
                //부가세 계산
                if(vatAmount == null) {
                    vatAmount = Math.toIntExact(Math.round(cancellationAmount / 11.0));
                    calcVatYN = true;
                }
                balPayAmount = payment.getPaymentAmount() - cancellationSumAmount;
                balVatAmount = payment.getVatAmount() - cancellationVatSumAmount;

                //부가세 짜투리 금액 조절
                //천원은 부가세가 없을 수 있으므로, 자동계산되는 부가세에 대해서는 천원에 대한 부가세 91원까지는 보정함
                //결제원금이 0원이 되고 계산되는 부가세이면서 0~91원 과다계산되는 부가세에 대해서 조절
                if ( balPayAmount - cancellationAmount == 0 && calcVatYN && balVatAmount-vatAmount >= -91 && balVatAmount-vatAmount < 0 ) {
                    vatAmount = balVatAmount;
                }

                //취소금액 확인
                if ( balPayAmount - cancellationAmount < 0 ) {
                    msgCode = ResponseService.CommonResponse.CHK_CANAMT;
                } else if ( balVatAmount - vatAmount < 0 ) {
                    msgCode = ResponseService.CommonResponse.CHK_CANVATAMT;
                } else if ( balPayAmount - cancellationAmount < balVatAmount - vatAmount ) {
                    msgCode = ResponseService.CommonResponse.CHK_CANBALVATAMT;
                } else {
                    decCardInfo = crypto.decryptAES256(payment.getCardInfo(), Key);

                    if (decCardInfo != null) {
                        splitCardInfo = decCardInfo.split("\\|");
                    }

                    //필요 object 생성
                    CardInfo cardInfo = CardInfo.builder().cardNum(splitCardInfo[0]).validPeriod(splitCardInfo[1]).cvc(splitCardInfo[2]).build();
                    AmountInfo cancelAmountInfo = AmountInfo.builder().paymentAmount(cancellationAmount).vatAmount(vatAmount).build();

                    //unique ID 생성 ( 취소 uid 는 C로 시작 )
                    currentID = "C"+commonUtils.getCurrentDateId();
                    System.out.println(currentID);

                    //카드사 전송데이터 생성
                    //body정보생성
                    transData = commonUtils.makeTransData(cardInfo, installment, cancelAmountInfo, currentID, payment.getCardInfo());
                    System.out.println(transData);
                    //header정보추가
                    transData = commonUtils.makeStringByType("446",1,4)
                            + commonUtils.makeStringByType("CANCEL",4,10)
                            + commonUtils.makeStringByType(currentID,4,20)
                            + transData;
                    System.out.println(transData);

                    //취소정보저장
                    LocalDateTime date = LocalDateTime.now();
                    Cancellation cancellationInsert = Cancellation.builder()
                            .uid(currentID)
                            .cancellationAmount(cancellationAmount)
                            .vatAmount(vatAmount)
                            .transData(transData)
                            .paymentUid(paymentUid)
                            .createdAt(date)
                            .build();
                    CancellationJpaRepo.save(cancellationInsert);

                    //리턴할정보생성(결제정보와 같은 response 사용)
                    paymentResultDTO = PaymentResultDTO.builder()
                            .uId(currentID)
                            .transData(transData)
                            .build();
                    msgCode = ResponseService.CommonResponse.SUCCESS;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            msgCode = ResponseService.CommonResponse.FAIL;
        } finally {
            return responseService.getSingleResult(paymentResultDTO, msgCode);
        }

    }

}

