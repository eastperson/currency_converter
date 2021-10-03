package com.wirebarley.api.scheduler;

import com.wirebarley.api.service.CurrencyConvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyScheduler {

    private final CurrencyConvertService currencyConvertService;

    /**
     * Daily Currency Polling
     */
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void dailySaveCurrentCurrency(){
        currencyConvertService.saveCurrentCurrency();
    }

}
