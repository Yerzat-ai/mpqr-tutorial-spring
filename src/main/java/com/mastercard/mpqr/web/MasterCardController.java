package com.mastercard.mpqr.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mastercard.mpqr.exception.MasterCardException;
import com.mastercard.mpqr.model.MerchantTransferFundingAndPaymentRequest;
import com.mastercard.mpqr.model.MerchantTransferPaymentRequest;
import com.mastercard.mpqr.service.MasterCardService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Rest endpoints to proxy MPQR API
 */
@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MasterCardController {

    MasterCardService masterCardService;

    ObjectMapper objectMapper;

    /**
     * Proxy for Merchant Transfer - Funding and Payment/create request
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/merchantTransferFundingAndPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> merchantTransferFundingAndPayment(@RequestBody MerchantTransferFundingAndPaymentRequest request) {
        return ResponseEntity.ok(masterCardService.merchantTransferFundingAndPayment(request));
    }

    /**
     * Proxy for Merchant Transfer - Payment/create request
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/merchantTransferPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> merchantTransferPayment(@RequestBody MerchantTransferPaymentRequest request) {
        return ResponseEntity.ok(masterCardService.merchantTransferPayment(request));
    }

    @GetMapping(value = "/getMerchantTransferPaymentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMerchantTransferPaymentList() {
        String byRef = masterCardService.getByRef("ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo", "4002731033061103775624344507490434951000");
        try {
            JsonNode jsonNode = objectMapper.readTree(byRef);
            ObjectNode merchantTransfers = (ObjectNode) jsonNode.findPath("merchant_transfers");
            ArrayNode data = (ArrayNode) merchantTransfers.findPath("data").withArray("merchant_transfer");

            Stream.of(
                    "4002731033061103775624344507490434951001",
                    "4002731033061103775624344507490434951002",
                    "4002731033061103775624344507490434951003",
                    "4002731033061103775624344507490434951004",
                    "4002731033061103775624344507490434951005")
                    .map(ref -> masterCardService.getByRef("ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo", ref))
                    .forEach(mtp -> {
                        try {
                            merchantTransfers.put("item_count", merchantTransfers.get("item_count").asInt() + 1);
                            data.add(objectMapper.readTree(mtp).findPath("merchant_transfers").findPath("data").withArray("merchant_transfer").get(0));
                        } catch (IOException e) {
                            throw new MasterCardException(e);
                        }
                    });
            return ResponseEntity.ok(jsonNode.toString());
        } catch (IOException e) {
            throw new MasterCardException(e);
        }
    }

    /**
     * Proxy for Merchant Retrieval/read by ID request
     *
     * @param partnerId  required
     * @param transferId required
     * @return
     */
    @GetMapping(value = "/byId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@RequestParam String partnerId, @RequestParam String transferId) {
        return ResponseEntity.ok(masterCardService.getById(partnerId, transferId));
    }

    /**
     * Proxy for Merchant Retrieval/read by reference request
     *
     * @param partnerId required
     * @param ref       required
     * @return
     */
    @GetMapping(value = "/byRef", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByRef(@RequestParam String partnerId, @RequestParam String ref) {
        return ResponseEntity.ok(masterCardService.getByRef(partnerId, ref));
    }
}
