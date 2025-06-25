package com.formadoresit.camel;

import com.formadoresit.camel.domain.User;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@CamelSpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MySpringBootApplicationTest {

	@Autowired
	private CamelContext camelContext;

	@Autowired
	private ProducerTemplate producerTemplate;

	@BeforeEach
	public void setup() throws Exception {
		// Modificamos create-user
		AdviceWith.adviceWith(camelContext, "create-user", route -> {
			route.replaceFromWith("direct:testCreateUser");
		});

		// Modificamos insert-user-jpa
		AdviceWith.adviceWith(camelContext, "insert-user-jpa", route -> {
			route.weaveByToUri("jpa:*").replace().to("mock:jpa");
		});

		// Modificamos notify-new-user
		AdviceWith.adviceWith(camelContext, "notify-new-user", route -> {
			route.weaveByToUri("activemq:*").replace().to("mock:activemq");
		});
	}

	@Test
	public void creationUserOk() throws Exception {
		// Get mocks
		MockEndpoint mockJpa = camelContext.getEndpoint("mock:jpa", MockEndpoint.class);
		MockEndpoint mockActivemq = camelContext.getEndpoint("mock:activemq", MockEndpoint.class);

		// Expectations
		mockJpa.expectedMessageCount(1);
		mockActivemq.expectedMessageCount(1);

		// Create a User to send
		User user = new User();
		user.setId(1);
		user.setName("John");

		// Send message to route
		producerTemplate.sendBody("direct:testCreateUser", user);

		// Assert expectations
		MockEndpoint.assertIsSatisfied(camelContext);
	}

	@Test
	void testCreateUserWithEvenId_shouldFail() throws InterruptedException {
		MockEndpoint mockJpa = camelContext.getEndpoint("mock:jpa", MockEndpoint.class);
		MockEndpoint mockActivemq = camelContext.getEndpoint("mock:activemq", MockEndpoint.class);

		mockJpa.expectedMessageCount(1);
		mockActivemq.expectedMessageCount(0); // No debería llegar a ActiveMQ

		User user = new User();
		user.setId(2);
		user.setName("John");

		assertThrows(RuntimeException.class, () -> {
			producerTemplate.sendBody("direct:testCreateUser", user);
		});

		MockEndpoint.assertIsSatisfied(camelContext);
	}
}
