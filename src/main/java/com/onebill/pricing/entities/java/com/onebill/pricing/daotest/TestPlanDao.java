package com.onebill.pricing.daotest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.onebill.pricing.PricingAppConfiguration;
import com.onebill.pricing.dao.PlanDao;
import com.onebill.pricing.dao.ProductDao;
import com.onebill.pricing.entities.Plan;
import com.onebill.pricing.entities.Product;
import com.sun.istack.logging.Logger;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PricingAppConfiguration.class)
@WebAppConfiguration
public class TestPlanDao {

	@Autowired
	PlanDao planDao;

	@Autowired
	ProductDao prodDao;

	@Autowired
	ApplicationContext context;

	Logger logger = Logger.getLogger(TestPlanDao.class);

	@Test
	public void testAddPlan() {

		Product p = addDummyProduct("dummy product");

		Plan plan = addDummyPlan(p.getProductId(), 30);

		plan = planDao.addPlan(plan);
		logger.info(plan.toString());

		assertTrue(plan.getPlanId() > 0);
		assertEquals(plan.getProductId(), p.getProductId());
		assertEquals(30, plan.getValidityDays());

	}

	@Test(expected = PersistenceException.class)
	public void testAddPlanWithoutProduct() {

		Plan plan = new Plan();
		plan.setValidityDays(45);
		planDao.addPlan(plan);
	}

	@Test
	public void testUpdatePlan() {

		Product p = addDummyProduct("dummy product");

		Plan plan = addDummyPlan(p.getProductId(), 30);

		plan.setValidityDays(45);

		Plan p1 = planDao.updatePlan(plan);

		assertEquals(45, p1.getValidityDays());
		assertEquals(plan.getPlanId(), p1.getPlanId());
	}

	@Test
	public void testRemovePlan() {

		Product p = addDummyProduct("dummy product");

		Plan plan = addDummyPlan(p.getProductId(), 30);

		plan = planDao.removePlanbyId(plan.getPlanId());

		assertEquals(plan.getProductId(), p.getProductId());
		assertEquals(30, plan.getValidityDays());

	}

	@Test
	public void testGetPlanById() {

		Product p = addDummyProduct("dummy product");

		Plan plan = addDummyPlan(p.getProductId(), 30);

		plan = planDao.getPlanById(plan.getPlanId());

		assertEquals(plan.getProductId(), p.getProductId());
		assertEquals(30, plan.getValidityDays());
	}

	@Test
	public void testGetAllPlans() {

		Product p = addDummyProduct("dummy product");
		Product p1 = addDummyProduct("dummy product1");
		Product p2 = addDummyProduct("dummy product2");

		addDummyPlan(p.getProductId(), 30);
		addDummyPlan(p1.getProductId(), 40);
		addDummyPlan(p2.getProductId(), 70);

		List<Plan> list = planDao.getAllPlans();

		assertTrue(!list.isEmpty());

	}

	@Test(expected = PersistenceException.class)
	public void addDuplicateProductToPlan() {

		Product p = addDummyProduct("dummy product");

		addDummyPlan(p.getProductId(), 30);
		addDummyPlan(p.getProductId(), 40);

	}

	public Product addDummyProduct(String name) {
		Product product = new Product();
		product.setProductName(name);
		return prodDao.addProduct(product);
	}

	public Plan addDummyPlan(int productId, int days) {
		Plan p = new Plan();
		p.setProductId(productId);
		p.setValidityDays(days);
		return planDao.addPlan(p);
	}

}
