package com.developer.silverheavens;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.developer.silverheavens.repository.RateRepository;

@SpringBootTest
class SilverheavensApplicationTests {

	@MockBean
	public RateRepository rateRepo;
	
	@Test
	void contextLoads() {
	}

}
