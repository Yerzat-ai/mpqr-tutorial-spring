package com.mastercard.mpqr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

/**
 * Request object for Merchant Transfer - Payment/create request
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantTransferPaymentRequest {
    @JsonProperty("partnerId")
    String partnerId;
    MerchantPaymentTransfer merchantPaymentTransfer;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MerchantPaymentTransfer {
        String transferReference;
        String paymentType;
        String amount;
        String currency;
        String senderAccountUri;
        Sender sender;
        String additionalMessage;
        String fundingSource;
        String participationId;
        String recipientAccountUri;
        Recipient recipient;
        String paymentOriginationCountry;
        ReconciliationData reconciliationData;
        String channel;
        String deviceId;
        String location;
        String transactionLocalDateTime;
        Participant participant;
        FundingTransactionReference fundingTransactionReference;
        String mastercardAssignedId;
        String routingTransitNumber;
        String processorId;
        TokenCryptogram tokenCryptogram;
        String authenticationValue;

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class Person {
            String firstName;
            String middleName;
            String lastName;
            Address address;
            String phone;
            String email;
            String authenticationValue;

            @Data
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static class Address {
                String line1;
                String line2;
                String city;
                String countrySubdivision;
                String postalCode;
                String country;
            }
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @EqualsAndHashCode(callSuper = true)
        public static class Sender extends Person {
            String dateOfBirth;
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @EqualsAndHashCode(callSuper = true)
        public static class Recipient extends Person {
            String merchantCategoryCode;
        }


        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class ReconciliationData {
            CustomField[] customField;

            @Data
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static class CustomField {
                String name;
                String value;
            }
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class Participant {
            String cardAcceptorId;
            String cardAcceptorName;
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class FundingTransactionReference {
            String network;
            String referenceNumber;
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class TokenCryptogram {
            String type;
            String value;
            String panSequenceNumber;
        }
    }
}
