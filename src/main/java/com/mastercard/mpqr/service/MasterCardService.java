package com.mastercard.mpqr.service;


import com.mastercard.mpqr.model.MerchantTransferFundingAndPaymentRequest;
import com.mastercard.mpqr.model.MerchantTransferPaymentRequest;

public interface MasterCardService {
    String merchantTransferFundingAndPayment(MerchantTransferFundingAndPaymentRequest request);

    String merchantTransferPayment(MerchantTransferPaymentRequest request);

    String getById(String partnerId, String transferId);

    String getByRef(String partnerId, String ref);
}
