package com.mastercard.mpqr.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.mastercard.api.core.ApiConfig;
import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.oauth.OAuthAuthentication;
import com.mastercard.api.p2m.MerchantRetrieval;
import com.mastercard.api.p2m.MerchantTransferFundingAndPayment;
import com.mastercard.api.p2m.MerchantTransferPayment;
import com.mastercard.mpqr.config.MasterCardConfiguration;
import com.mastercard.mpqr.exception.MasterCardException;
import com.mastercard.mpqr.model.MerchantTransferFundingAndPaymentRequest;
import com.mastercard.mpqr.model.MerchantTransferPaymentRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service communicating with MPQR API via QR client
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class MasterCardServiceImpl implements MasterCardService {

    MasterCardConfiguration masterCardConfiguration;

    ObjectMapper objectMapper;

    /**
     * Configures MPQR API client and jackson Object mapper
     */
    @PostConstruct
    public void init() throws IOException {
        ApiConfig.setEnvironment(masterCardConfiguration.getEnvironment());
        ApiConfig.setDebug(masterCardConfiguration.isDebug());
        InputStream is = new FileInputStream(masterCardConfiguration.getPrivateKey());
        OAuthAuthentication authentication = new OAuthAuthentication(masterCardConfiguration.getConsumerKey(), is, masterCardConfiguration.getKeyAlias(),
                masterCardConfiguration.getKeyPassword());
        ApiConfig.setAuthentication(authentication);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    /**
     * Initiates a MPQR purchase transaction by securing funds from a consumer’s account
     * with a Funding Transaction and pushing funds to a merchant account with a Payment Transaction.
     *
     * @param request
     * @return json response from MPQR API
     */
    @Override
    public String merchantTransferFundingAndPayment(MerchantTransferFundingAndPaymentRequest request) {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            RequestMap requestMap = new RequestMap();
            String content = objectMapper.writeValueAsString(request);
            addKeys("", objectMapper.readTree(content), map);
            map.forEach(requestMap::set);
            MerchantTransferFundingAndPayment apiResponse = MerchantTransferFundingAndPayment.create(requestMap);
            log.debug(apiResponse.toString());
            return JSONObject.toJSONString(apiResponse);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        }
    }

    /**
     * Initiates a MPQR purchase transaction by pushing funds to a merchant account.
     *
     * @param request
     * @return json response from MPQR API
     */
    @Override
    public String merchantTransferPayment(MerchantTransferPaymentRequest request) {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            RequestMap requestMap = new RequestMap();
            String content = objectMapper.writeValueAsString(request);
            addKeys("", objectMapper.readTree(content), map);
            map.forEach(requestMap::set);
            MerchantTransferPayment apiResponse = MerchantTransferPayment.create(requestMap);
            log.debug(apiResponse.toString());
            return JSONObject.toJSONString(apiResponse);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        }
    }

    /**
     * Retrieves the Transfer resource associated with the specified transfer-id.
     *
     * @param partnerId
     * @param transferId
     * @return json response from MPQR API
     */
    @Override
    public String getById(String partnerId, String transferId) {
        try {
            RequestMap map = new RequestMap();
            map.set("partnerId", partnerId);
            map.set("transferId", transferId);
            MerchantRetrieval apiResponse = MerchantRetrieval.readByID(null, map);
            log.debug(apiResponse.toString());
            return JSONObject.toJSONString(apiResponse);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        }
    }

    /**
     * Retrieves the Transfer resource associated with a specified transfer_reference value
     *
     * @param partnerId
     * @param ref
     * @return json response from MPQR API
     */
    @Override
    public String getByRef(String partnerId, String ref) {
        try {
            RequestMap map = new RequestMap();
            map.set("partnerId", partnerId);
            map.set("ref", ref);
            MerchantRetrieval apiResponse = MerchantRetrieval.readByReference(map);
            log.debug(apiResponse.toString());
            return JSONObject.toJSONString(apiResponse);
        } catch (ApiException e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MasterCardException(e);
        }
    }

    /**
     * Generates flat map from json request object
     *
     * @param currentPath
     * @param jsonNode
     * @param map
     */
    private void addKeys(String currentPath, JsonNode jsonNode, Map<String, Object> map) {
        if (jsonNode.isObject()) {
            com.fasterxml.jackson.databind.node.ObjectNode objectNode = (com.fasterxml.jackson.databind.node.ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                addKeys(currentPath + "[" + i + "]", arrayNode.get(i), map);
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            String value = valueNode.asText();
            if (value != null) {
                map.put(currentPath, value);
            }
        }
    }
}
