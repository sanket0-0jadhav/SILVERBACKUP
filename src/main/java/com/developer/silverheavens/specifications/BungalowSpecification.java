package com.developer.silverheavens.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.developer.silverheavens.entities.Bungalow;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface BungalowSpecification {
	
	@SuppressWarnings("serial")
	public static Specification<Bungalow> withId(int id){
		return new Specification<Bungalow>() {
			@Override
			public Predicate toPredicate(Root<Bungalow> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				return criteriaBuilder.equal(root.get("id"), id);
			}
		};
	}
	

	
	
	
}
