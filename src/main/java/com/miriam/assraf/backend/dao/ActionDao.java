package com.miriam.assraf.backend.dao;

import com.miriam.assraf.backend.data.ActionEntity;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActionDao extends PagingAndSortingRepository<ActionEntity, Long> {

}