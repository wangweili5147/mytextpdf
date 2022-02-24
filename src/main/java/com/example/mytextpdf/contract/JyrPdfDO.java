package com.example.mytextpdf.contract;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwl
 * @create 2022-02-17 15:32
 */
@NoArgsConstructor
@Data
public class JyrPdfDO {

    /**
     * data : {"rzCompanyName":"X公司","rzLegalPersonName":"张三","rzRegisteredAddress":"河南济源","rzBankUserName":"李四","rzBankAccount":"6221578458715477854","rzBankName":"工商银行","rzFax":"cz8744785478","rzSignDate":"2022年2月17日","rzLegalPersonPhone":"15287685478","zjCompanyName":"建设银行","zjLegalPersonName":"王五","zjRegisteredAddress":"河南郑州","zjLegalPersonPhone":"15236878987","zjFax":"cz785471258","zjSignDate":"2022年2月17日","upperFinanceAmount":"十六万","financeDateBegin":"2022年2月20日","financeDateEnd":"2024年2月20日","financeAmount":"160000","contractNo":"HT12541255788"}
     * title : 金易融-应收账款转让融资合同
     */

    private DataBean data;
    private String title;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * rzCompanyName : X公司
         * rzLegalPersonName : 张三
         * rzRegisteredAddress : 河南济源
         * rzBankUserName : 李四
         * rzBankAccount : 6221578458715477854
         * rzBankName : 工商银行
         * rzFax : cz8744785478
         * rzSignDate : 2022年2月17日
         * rzLegalPersonPhone : 15287685478
         * zjCompanyName : 建设银行
         * zjLegalPersonName : 王五
         * zjRegisteredAddress : 河南郑州
         * zjLegalPersonPhone : 15236878987
         * zjFax : cz785471258
         * zjSignDate : 2022年2月17日
         * upperFinanceAmount : 十六万
         * financeDateBegin : 2022年2月20日
         * financeDateEnd : 2024年2月20日
         * financeAmount : 160000
         * contractNo : HT12541255788
         */

        private String rzCompanyName;
        private String rzLegalPersonName;
        private String rzRegisteredAddress;
        private String rzBankUserName;
        private String rzBankAccount;
        private String rzBankName;
        private String rzFax;
        private String rzSignDate;
        private String rzLegalPersonPhone;
        private String zjCompanyName;
        private String zjLegalPersonName;
        private String zjRegisteredAddress;
        private String zjLegalPersonPhone;
        private String zjFax;
        private String zjSignDate;
        private String upperFinanceAmount;
        private String financeDateBegin;
        private String financeDateEnd;
        private String financeAmount;
        private String contractNo;
    }
}
