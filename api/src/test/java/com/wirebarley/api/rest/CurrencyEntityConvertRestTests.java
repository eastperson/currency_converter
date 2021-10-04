package com.wirebarley.api.rest;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.wirebarley.api.ApiApplication;
import com.wirebarley.core.component.currency_layer.domain.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
public class CurrencyEntityConvertRestTests {


    @Autowired MockMvc mockMvc;
    @Autowired Gson gson;
    @Autowired WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @DisplayName("현재 환율 정보")
    @Test
    void currentCurrency() throws Exception {
        MvcResult result = mockMvc.perform(get("/currency/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.terms").exists())
                .andExpect(jsonPath("$.privacy").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.source").exists())
                .andExpect(jsonPath("$.quotes").exists())
                .andExpect(jsonPath("$.quotes.USDKRW").exists())
                .andExpect(jsonPath("$.quotes.USDPHP").exists())
                .andExpect(jsonPath("$.quotes.USDJPY").exists())
                .andReturn();

        Map<String,Object> map = gson.fromJson(result.getResponse().getContentAsString(), HashMap.class);
        assertThat(map.get("success")).isEqualTo("true");
        assertThat(map.get("terms")).isEqualTo("https://currencylayer.com/terms");
        assertThat(map.get("privacy")).isEqualTo("https://currencylayer.com/privacy");
        assertThat(map.get("source")).isEqualTo("USD");
        Map<String,Object> resultQuotes = (LinkedTreeMap) map.get("quotes");
        assertFalse(Double.isNaN(Double.parseDouble((String) resultQuotes.get("USDKRW"))));
        assertFalse(Double.isNaN(Double.parseDouble((String) resultQuotes.get("USDPHP"))));
        assertFalse(Double.isNaN(Double.parseDouble((String) resultQuotes.get("USDJPY"))));
    }

    @DisplayName("환율 계산 - 성공")
    @Test
    void currencyConvertTest_success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("amount","3000");
        params.add("type",CurrencyType.KRW.name());

        MvcResult result = mockMvc.perform(get("/currency/convert")
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formattedConvertedAmount").exists())
                .andExpect(jsonPath("$.convertedAmount").exists())
                .andReturn();

        Map<String,Object> map = gson.fromJson(result.getResponse().getContentAsString(), HashMap.class);
        assertFalse(Double.isNaN(Double.parseDouble(map.get("convertedAmount").toString())));
    }

    @DisplayName("환율 계산 - 에러1 : 송금 금액이 음수인 경우")
    @Test
    void currencyConvertTest_error1() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("amount","-100");
        params.add("type",CurrencyType.KRW.name());

        MvcResult result = mockMvc.perform(get("/currency/convert")
                .params(params)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.msg").exists())
                .andReturn();

        Map<String,Object> map = gson.fromJson(result.getResponse().getContentAsString(), HashMap.class);
        assertThat(map.get("code")).isEqualTo("__BAD_REQUEST__");
        assertThat(map.get("msg")).isEqualTo("송금액이 바르지 않습니다");
    }

    @DisplayName("환율 계산 - 에러2 : 송금 금액이 10000을 초과한 경우")
    @Test
    void currencyConvertTest_error2() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("amount","10001");
        params.add("type",CurrencyType.KRW.name());

        MvcResult result = mockMvc.perform(get("/currency/convert")
                .params(params)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.msg").exists())
                .andReturn();

        Map<String,Object> map = gson.fromJson(result.getResponse().getContentAsString(), HashMap.class);
        assertThat(map.get("code")).isEqualTo("__BAD_REQUEST__");
        assertThat(map.get("msg")).isEqualTo("송금액이 바르지 않습니다");
    }

    @DisplayName("환율 계산 - 에러3 : 통화 코드가 올바르지 않을 경우")
    @Test
    void currencyConvertTest_error3() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("amount","3000");
        params.add("type","AAA");

        MvcResult result = mockMvc.perform(get("/currency/convert")
                .params(params)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.msg").exists())
                .andReturn();

        Map<String,Object> map = gson.fromJson(result.getResponse().getContentAsString(), HashMap.class);
        assertThat(map.get("code")).isEqualTo("__BAD_REQUEST__");
        assertThat(map.get("msg")).isEqualTo("통화 코드가 바르지 않습니다");
    }
}
