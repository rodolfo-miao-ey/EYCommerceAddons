package br.com.braspag.service.impl;

import br.com.braspag.dao.BraspagSalesDao;
import br.com.braspag.exceptions.BraspagTimeoutException;
import br.com.braspag.model.OrderPaymentLogInfoModel;
import br.com.braspag.payment.data.*;
import br.com.braspag.service.BraspagSalesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;

public class DefaultBraspagSalesService implements BraspagSalesService {

    private static final Logger LOG = Logger.getLogger(DefaultBraspagSalesService.class);

    private static final String GET_CAPTURED_RESOURCE = "/v2/sales/%s";
    private static final String SALES_RESOURCE = "/v2/sales";
    private static final String CAPTURE_RESOURCE = "/v2/sales/%s/capture";
    private static final String VOID_RESOURCE = "/v2/sales/%s/void";

    @Resource
    private BraspagSalesDao braspagSalesDao;

    private final ObjectMapper objectMapper;
    private RestTemplate restTemplate;

    public DefaultBraspagSalesService(){
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     *
     * @param request Inform the request object to be included in the payload.
     * @param urlResource Inform the URL resource only. E.g., "/v2/sales", NOT the whole URL.
     * @param httpMethod Inform the required HttpMethod.
     * @param clazz Inform the class of the response.
     * @param <T> Response Entity Class
     * @return The response object.
     */
    private <T> T doRequest(final Serializable request, final String urlResource, final HttpMethod httpMethod,
                            final Class<T> clazz, final String apiKey, final String merchantId, final String endpoint) throws BraspagTimeoutException
    {
        try {
            final var httpHeaders = getHttpHeaders(apiKey, merchantId);

            final HttpEntity<Serializable> payload;
            if (request != null) {
                payload = new HttpEntity<>(request, httpHeaders);
            }
            else {
                payload = new HttpEntity<>(httpHeaders);
            }

            if (LOG.isDebugEnabled()){
                LOG.debug("[DEBUG] Braspag - endpoint: " + endpoint + urlResource);
                LOG.debug("[DEBUG] Braspag - body: " + print(payload.getBody()));
                LOG.debug("[DEBUG] Braspag - header: " + print(payload.getHeaders()));
            }

            final ResponseEntity<String> responseEntity = restTemplate.exchange(endpoint + urlResource, httpMethod, payload, String.class);

            if (LOG.isDebugEnabled()){
                LOG.debug("[DEBUG] Braspag | endpoint: " + urlResource + " | response: " + print(responseEntity.getBody()));
            }

            final var javaType = objectMapper.getTypeFactory().constructType(clazz);

            return readValue(responseEntity, javaType);
        }
        catch (final ResourceAccessException e)
        {
            if(e.getRootCause() instanceof SocketTimeoutException)
            {
                throw new BraspagTimeoutException(e.getMessage());
            }
        }
        catch (Exception ex){
            LOG.error("[Braspag] Erro de conexão ao Gateway.", ex);
        }

        return null;
    }


    @Override
    public AuthorizeResponseDTO authorizePayment(AuthorizeRequestDTO request, final AbstractOrderModel orderModel) throws BraspagTimeoutException
    {
        try {
            final BaseStoreModel currentBaseStore = orderModel.getStore();

            return doRequest(request, SALES_RESOURCE, HttpMethod.POST, AuthorizeResponseDTO.class,
                    currentBaseStore.getBraspagMerchantKey(), currentBaseStore.getBraspagMerchantId(), currentBaseStore.getBraspagAPIEndpoint());
        } catch (Exception ex){
            LOG.error("[Braspag] Erro ao autorizar pagamento. Request=" + print(request));
            if (ex instanceof BraspagTimeoutException)
                throw (BraspagTimeoutException) ex;
            return null;
        }
    }

    @Override
    public VoidResponseDTO voidPayment(final String paymentId, final BraspagAuthorizationPojo braspagAuthorizationPojo, final AbstractOrderModel cartModel) {
        try {
            final BaseStoreModel currentBaseStore = cartModel.getStore();

            return doRequest(null,  String.format(VOID_RESOURCE, paymentId), HttpMethod.PUT, VoidResponseDTO.class,
                    braspagAuthorizationPojo.getMerchantKey(), braspagAuthorizationPojo.getMerchantId(), currentBaseStore.getBraspagAPIEndpoint());
        } catch (Exception ex){
            LOG.error("[Braspag] Erro ao cancelar pagamento. {paymentId}=" + paymentId);
            return null;
        }
    }


    private HttpHeaders getHttpHeaders(String braspagAPIKey, String braspagMerchantId) {
        final var httpHeaders = new HttpHeaders();

        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("MerchantKey", braspagAPIKey);
        httpHeaders.set("MerchantId", braspagMerchantId);

        return httpHeaders;
    }

    private <T> T readValue(ResponseEntity<String> response, JavaType javaType) {

        T result = null;

        if (response.getStatusCode() == HttpStatus.OK ||
                response.getStatusCode() == HttpStatus.CREATED) {
            try {
                result = objectMapper.readValue(response.getBody(), javaType);
            } catch (IOException e) {
                LOG.info(e.getMessage());
            }
        } else {
            LOG.info(String.format("No data found %s", response.getStatusCode()));
        }
        return result;
    }

    private String print(Serializable payload)
    {
        try{
            return objectMapper.writeValueAsString(payload);
        }
        catch (JsonProcessingException e){
            return e.getMessage();
        }
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OrderPaymentLogInfoModel getLog(String orderCode){
        return braspagSalesDao.getLog(orderCode);
    }
}
