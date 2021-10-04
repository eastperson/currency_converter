package com.wirebarley.api.scheduler;

import com.wirebarley.api.service.CurrencyConvertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyScheduler {

    private final CurrencyConvertService currencyConvertService;

    /**
     * Daily Currency Polling
     */
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void dailySaveCurrentCurrency(){
        currencyConvertService.saveCurrentCurrency();
    }

    /**
     * 서버 실행시 최초 api 호출
     */
    @PostConstruct
    public void initMessage() {
        log.info("Init Api Call");
        currencyConvertService.saveCurrentCurrency();
    }

}
