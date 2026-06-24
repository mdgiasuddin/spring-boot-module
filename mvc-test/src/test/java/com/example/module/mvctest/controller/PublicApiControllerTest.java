package com.example.module.mvctest.controller;

import com.example.module.mvctest.config.SecurityConfig;
import com.example.module.mvctest.dto.Person;
import com.example.module.mvctest.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@WebMvcTest(PublicApiController.class)
@AutoConfigureRestTestClient
@Import(SecurityConfig.class)
class PublicApiControllerTest {

    @Autowired
    private RestTestClient client;

    @MockitoBean
    private TestService testService;

    @Autowired
    private MockMvcTester mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getPersonList_shouldReturnList_mockMvc() throws UnsupportedEncodingException {
        Person p1 = new Person(1, "John", LocalDate.of(1990, 1, 1));
        Person p2 = new Person(2, "Jane", LocalDate.of(1995, 10, 20));
        given(testService.getPersonList()).willReturn(Arrays.asList(p1, p2));

        MvcTestResult result = mockMvc.get()
                .uri("/api/public/people")
                .exchange();

        assertThat(result).hasStatusOk();

        String jsonResponse = result.getResponse().getContentAsString();
        Person[] products = objectMapper.readValue(jsonResponse, Person[].class);

        assertThat(products).hasSize(2);
        assertThat(products[0].getName()).isEqualTo("John");
    }


    @Test
    void getPersonList_shouldReturnList_restClient() {
        Person p1 = new Person(1, "John", LocalDate.of(1990, 1, 1));
        Person p2 = new Person(2, "Jane", LocalDate.of(1995, 10, 20));
        given(testService.getPersonList()).willReturn(Arrays.asList(p1, p2));

        client.get()
                .uri("/api/public/people")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Person[].class)
                .value(products -> {
                    assertThat(products).hasSize(2);
                    assertThat(products[0].getName()).isEqualTo("John");
                });
    }

    @Test
    void getPersonById_shouldReturnPerson_restClient() {
        Person p1 = new Person(1, "John", LocalDate.of(1990, 1, 1));
        given(testService.getPersonById(anyInt())).willReturn(p1);

        var responseBody = client.get()
                .uri("/api/public/people/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Person.class)
                .getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("John");
    }
}