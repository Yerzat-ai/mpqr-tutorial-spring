package com.mastercard.mpqr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mastercard.mpqr.model.MerchantTransferFundingAndPaymentRequest;
import com.mastercard.mpqr.model.MerchantTransferPaymentRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpqrApplicationTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @PostConstruct
    public void init() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Test
    public void testMerchantTransferFundingAndPaymentMissedPartnerId() throws Exception {
        MerchantTransferFundingAndPaymentRequest request = getMerchantTransferFundingAndPaymentRequest();
        request.setPartnerId(null);
        mvc.perform(post("/merchantTransferFundingAndPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMerchantTransferFundingAndPaymentWrongPartnerId() throws Exception {
        MerchantTransferFundingAndPaymentRequest request = getMerchantTransferFundingAndPaymentRequest();
        request.setPartnerId("test");
        mvc.perform(post("/merchantTransferFundingAndPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.source", is("partnerId")))
                .andExpect(jsonPath("$.reason_code", is("INVALID_INPUT_LENGTH")))
                .andExpect(jsonPath("$.message", is("Invalid length")));
    }

    @Test
    public void testMerchantTransferFundingAndPaymentOk() throws Exception {
        MerchantTransferFundingAndPaymentRequest request = getMerchantTransferFundingAndPaymentRequest();
        String ref = RandomStringUtils.randomNumeric(40);
        request.getMerchantTransfer().setTransferReference(ref);
        mvc.perform(post("/merchantTransferFundingAndPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.merchant_transfer.transfer_reference", is(ref)));
    }

    @Test
    public void testMerchantTransferFundingAndPaymentDuplicate() throws Exception {
        MerchantTransferFundingAndPaymentRequest request = getMerchantTransferFundingAndPaymentRequest();
        String ref = RandomStringUtils.randomNumeric(40);
        request.getMerchantTransfer().setTransferReference(ref);
        //first call should succeed
        mvc.perform(post("/merchantTransferFundingAndPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.merchant_transfer.transfer_reference", is(ref)));

        //second will fail
        mvc.perform(post("/merchantTransferFundingAndPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.source", is("transfer_reference")))
                .andExpect(jsonPath("$.reason_code", is("RESOURCE_ERROR")))
                .andExpect(jsonPath("$.message", is("Duplicate value")));
    }

    @Test
    public void testMerchantTransferPaymentMissedPartnerId() throws Exception {
        MerchantTransferPaymentRequest request = getMerchantTransferPaymentRequest();
        request.setPartnerId(null);
        mvc.perform(post("/merchantTransferPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMerchantTransferPaymentWrongPartnerId() throws Exception {
        MerchantTransferPaymentRequest request = getMerchantTransferPaymentRequest();
        request.setPartnerId("test");
        mvc.perform(post("/merchantTransferPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.source", is("partnerId")))
                .andExpect(jsonPath("$.reason_code", is("INVALID_INPUT_LENGTH")))
                .andExpect(jsonPath("$.message", is("Invalid length")));
    }

    @Test
    public void testMerchantTransferPaymentOk() throws Exception {
        MerchantTransferPaymentRequest request = getMerchantTransferPaymentRequest();
        String ref = RandomStringUtils.randomNumeric(40);
        request.getMerchantPaymentTransfer().setTransferReference(ref);
        mvc.perform(post("/merchantTransferPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.merchant_transfer.transfer_reference", is(ref)));
    }

    @Test
    public void testMerchantTransferPaymentDuplicate() throws Exception {
        MerchantTransferPaymentRequest request = getMerchantTransferPaymentRequest();
        String ref = RandomStringUtils.randomNumeric(40);
        request.getMerchantPaymentTransfer().setTransferReference(ref);
        //first call should succeed
        mvc.perform(post("/merchantTransferPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.merchant_transfer.transfer_reference", is(ref)));

        //second will fail
        mvc.perform(post("/merchantTransferPayment")
                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.source", is("transfer_reference")))
                .andExpect(jsonPath("$.reason_code", is("RESOURCE_ERROR")))
                .andExpect(jsonPath("$.message", is("Duplicate value")));
    }

    @Test
    public void testGetByRefMissedPartnerId() throws Exception {
        mvc.perform(get("/byRef"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetByRefMissedRef() throws Exception {
        mvc.perform(get("/byRef")
                .param("partnerId", "ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetByRefInvalidRef() throws Exception {
        mvc.perform(get("/byRef")
                .param("partnerId", "ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo")
                .param("ref", "xxx"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.source", is("transfer_reference")))
                .andExpect(jsonPath("$.reason_code", is("RESOURCE_UNKNOWN")))
                .andExpect(jsonPath("$.message", is("Record not found")));
    }

    @Test
    public void testGetByIdMissedPartnerId() throws Exception {
        mvc.perform(get("/byId"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetByIdMissedId() throws Exception {
        mvc.perform(get("/byId")
                .param("partnerId", "ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testGetByIdInvalidId() throws Exception {
        mvc.perform(get("/byId")
                .param("partnerId", "ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo")
                .param("transferId", "xxx"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.source", is("transfer_id")))
                .andExpect(jsonPath("$.reason_code", is("RESOURCE_UNKNOWN")))
                .andExpect(jsonPath("$.message", is("Record not found")));
    }


    private MerchantTransferFundingAndPaymentRequest getMerchantTransferFundingAndPaymentRequest() throws IOException {
        return mapper.readValue(getFile("/MerchantTransferFundingAndPaymentRequest.json"), MerchantTransferFundingAndPaymentRequest.class);
    }

    private MerchantTransferPaymentRequest getMerchantTransferPaymentRequest() throws IOException {
        return mapper.readValue(getFile("/MerchantTransferPaymentRequest.json"), MerchantTransferPaymentRequest.class);
    }

    private File getFile(String resourceName) throws FileNotFoundException {
        return ResourceUtils.getFile(this.getClass().getResource(resourceName));
    }

}
