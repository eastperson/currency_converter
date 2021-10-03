package com.wirebarley.core.repository;

import com.wirebarley.core.entity.CurrencyEntity;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<CurrencyEntity,String> {
}
