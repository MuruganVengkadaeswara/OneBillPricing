package com.onebill.pricing.servicetest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import com.onebill.pricing.dao.ProductServiceDao;
import com.onebill.pricing.dao.ServiceDao;
import com.onebill.pricing.dto.ServiceDto;
import com.onebill.pricing.entities.ProductService;
import com.onebill.pricing.entities.Service;
import com.onebill.pricing.exceptions.PricingConflictsException;
import com.onebill.pricing.exceptions.PricingException;
import com.onebill.pricing.exceptions.PricingNotFoundException;
import com.onebill.pricing.services.ServiceManagerService;
import com.onebill.pricing.services.ServiceManagerServiceImpl;
import com.sun.istack.logging.Logger;

import javassist.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class TestServiceManagerService {

	@Mock
	private ServiceDao servDao;

	@Mock
	private ProductServiceDao prodServDao;

	@InjectMocks
	private ServiceManagerService service = new ServiceManagerServiceImpl();

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	private Service serv;

	private ServiceDto servDto;

	private ModelMapper mapper;

	Logger log = Logger.getLogger(TestServiceManager.class);

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAddServiceWithNullServiceName() {
		expectedEx.expect(PricingConflictsException.class);
		expectedEx.expectMessage("Please Provide a service Name");
		ServiceDto dto = new ServiceDto();
		service.addService(dto);
	}

	@Test
	public void testAddServiceWithLessThan2Chars() {
		expectedEx.expect(PricingConflictsException.class);
		expectedEx.expectMessage(
				"The service Name Must contain only numbers,letters and spaces and must be within 2 and 25 characters");
		ServiceDto dto = new ServiceDto();
		dto.setServiceName("e");
		service.addService(dto);
	}

	@Test
	public void testAddDuplicateServiceName() {
		expectedEx.expect(PricingConflictsException.class);
		expectedEx.expectMessage("The service with name Hello already exists");
		ServiceDto dto = new ServiceDto();
		dto.setServiceName("Hello");

		Mockito.when(servDao.getServiceByName("Hello")).thenReturn(new Service());
		service.addService(dto);
	}

	@Test
	public void testAddInvalidServiceName() {
		expectedEx.expect(PricingConflictsException.class);
		expectedEx.expectMessage(
				"The service Name Must contain only numbers,letters and spaces and must be within 2 and 25 characters");
		servDto = new ServiceDto();
		servDto.setServiceName("OIT&(&($@&(@$($@");
		service.addService(servDto);
	}

	@Test
	public void testRemoveServiceInUse() throws NotFoundException {
		expectedEx.expect(PricingConflictsException.class);
		expectedEx.expectMessage("The service is used By one or more products ! please remove them before deleting");
		Integer id = 2;
		List<ProductService> list = new ArrayList<ProductService>();
		ProductService p = new ProductService();
		list.add(p);
		Mockito.when(prodServDao.getAllProductServicesByServiceId(2)).thenReturn(list);
		service.removeService(id);
	}

	@Test
	public void testRemoveServiceWithNegativeId() throws NotFoundException {
		expectedEx.expect(PricingException.class);
		expectedEx.expectMessage("Service Id must be greater than 0");
		service.removeService(-2);
	}

	@Test
	public void testUpdateServiceWithNegativeId() {
		expectedEx.expect(PricingException.class);
		expectedEx.expectMessage(
				"the service id must be greater than 0 and name must contain only spaces and numbers and be within 25 characters");
		ServiceDto dto = new ServiceDto();
		dto.setServiceId(-5);
		dto.setServiceName("Hello");
		service.updateService(dto);
	}

	@Test
	public void testUpdateServiceWithInvalidName() {
		expectedEx.expect(PricingException.class);
		expectedEx.expectMessage(
				"the service id must be greater than 0 and name must contain only spaces and numbers and be within 25 characters");
		servDto = new ServiceDto();
		servDto.setServiceId(5);
		servDto.setServiceName("MDUY**63974((*(Y^#");
		service.updateService(servDto);

	}

	@Test
	public void testGetServiceWithNegativeId() throws NotFoundException {
		expectedEx.expect(PricingException.class);
		expectedEx.expectMessage("The service id must be greater than 0");
		service.getService(-4);
	}

	@Test
	public void testGetAllServicesWithEmptyDb() {
		expectedEx.expect(PricingNotFoundException.class);
		expectedEx.expectMessage("There are no services");
		List<Service> list = new ArrayList<>();
		Mockito.when(servDao.getAllServices()).thenReturn(list);
		service.getAllServices();
	}

	// @Test
	// public void testGetAllServices() {
	// List<Service> list = new ArrayList<>();
	// list.add(makeService("dummy", 1));
	// list.add(makeService("dummy1", 2));
	// list.add(makeService("dummy2", 3));
	//
	// Mockito.when(servDao.getAllServices()).thenReturn(list);
	// List<ServiceDto> dtolist = service.getAllServices();
	// assertEquals(3, dtolist.size());
	//
	// }

	@Test
	public void testGetAllProductswithNegativeServiceId() {
		expectedEx.expect(PricingException.class);
		expectedEx.expectMessage("service Id must be greater than 0");
		service.getAllProductsOfService(-6);
	}

	@Test
	public void testGetNonExistingServiceByName() {
		expectedEx.expect(PricingNotFoundException.class);
		expectedEx.expectMessage("Service With Name Hello doesn't exist");
		Mockito.when(servDao.getServiceByName("Hello")).thenReturn(null);
		service.getServiceByName("Hello");
	}

	@Test(expected = PricingConflictsException.class)
	public void testAddServiceWithMoreThan25Characters() {

		ServiceDto serv = new ServiceDto();
		serv.setServiceName("Ajhsjahfdjhsdjhkshdkfhdslsasdasdgsg");
		service.addService(serv);

	}

	public Service makeService(String name, int id) {
		Service serv = new Service();
		serv.setServiceId(id);
		serv.setServiceName(name);
		return serv;
	}

	// @Test
	public void testGetServiceById() throws NotFoundException {

		serv = new Service();
		serv.setServiceId(1);
		serv.setServiceName("dummy");

		Mockito.when(servDao.getService(1)).thenReturn(serv);

		ServiceDto retrieved = service.getService(1);

		assertEquals(serv.getServiceId(), retrieved.getServiceId());
	}

}
