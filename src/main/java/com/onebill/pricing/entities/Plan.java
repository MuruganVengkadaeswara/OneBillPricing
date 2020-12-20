package com.onebill.pricing.entities;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "plans")
public class Plan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plan_id")
	private int planId;

	@OneToOne
	@JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
	private Product product;

	@Column(name = "product_id", unique = true)
	private int productId;


	@Column(name = "validity_days")
	private int validityDays;

}
