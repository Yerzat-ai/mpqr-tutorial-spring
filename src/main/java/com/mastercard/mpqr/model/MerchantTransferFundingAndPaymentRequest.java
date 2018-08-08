package com.mastercard.mpqr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

/**
 * Request object for Merchant Transfer - Funding and Payment/create
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantTransferFundingAndPaymentRequest {
    @JsonProperty("partnerId")
    String partnerId;
    MerchantTransfer merchantTransfer;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MerchantTransfer {
        String transferReference;
        String paymentType;
        String paymentOriginationCountry;
        String transactionLocalDateTime;
        TransferAmount transferAmount;
        String senderAccountUri;
        Sender sender;
        String recipientAccountUri;
        Recipient recipient;
        ReconciliationData reconciliationData;
        String mastercardAssignedId;
        String routingTransitNumber;
        String processorId;
        String additionalMessage;
        String participationId;
        Participant participant;

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class TransferAmount {
            String value;
            String currency;
        }

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
            TokenCryptogram tokenCryptogram;

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

            @Data
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static class TokenCryptogram {
                String type;
                String value;
                String panSequenceNumber;
            }
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @EqualsAndHashCode(callSuper = true)
        public static class Sender extends Person {
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
    }
}
