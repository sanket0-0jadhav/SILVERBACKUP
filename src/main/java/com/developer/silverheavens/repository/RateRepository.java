package com.developer.silverheavens.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.developer.silverheavens.entities.Rate;

@Repository
public interface RateRepository extends JpaRepository<Rate, Integer>,JpaSpecificationExecutor<Rate>{

//	//select all with closed_date as null
//	public List<Rate> findByClosedDateNull();
//	
//	//select all with given bungalow id
//	public List<Rate> findByBungalowId(int bungalowId);
//	
//	//select all with given bungalow id
//	public List<Rate> findByBungalowIdAndClosedDateNull(int bungalowId);

}
