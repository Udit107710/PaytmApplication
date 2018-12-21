package com.example.udit.paytmapplication;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Paytm {

        @SerializedName("MID")
        private String mId;

        @SerializedName("ORDER_ID")
        private String orderId;

        @SerializedName("CUST_ID")
        private String custId;

        @SerializedName("CHANNEL_ID")
        private String channelId;

        @SerializedName("TXN_AMOUNT")
        private String txnAmount;

        @SerializedName("WEBSITE")
        private String website;

        @SerializedName("CALLBACK_URL")
        private String callBackUrl;

        @SerializedName("INDUSTRY_TYPE_ID")
        private String industryTypeId;

        public Paytm(String mId, String channelId, String txnAmount, String website, String callBackUrl, String industryTypeId) {
            this.mId = mId;
            this.orderId = generateString();
            this.custId = generateString();
            this.channelId = channelId;
            this.txnAmount = txnAmount;
            this.website = website;
            this.callBackUrl = callBackUrl + orderId;
            this.industryTypeId = industryTypeId;

            // Log.d("orderId", orderId);
            // Log.d("customerId", custId);
        }

        public String getmId() {
            return mId;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getCustId() {
            return custId;
        }

        public String getChannelId() {
            return channelId;
        }

        public String getTxnAmount() {
            return txnAmount;
        }

        public String getWebsite() {
            return website;
        }

        public String getCallBackUrl() {
            return callBackUrl;
        }

        public String getIndustryTypeId() {
            return industryTypeId;
        }

        private String generateString() {
            String uuid = UUID.randomUUID().toString();
            return uuid.replaceAll("-", "");
        }

}
