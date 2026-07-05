package com.minibank;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
@Disabled("Requiere contexto completo con BD activa")
class ApiApplicationTests {

	@Test
	void contextLoads() {
		// Test vacío para validar el arranque del contexto sin dependencias externas
	}

}