package com.example.module.mvctest.controller;

import com.example.module.mvctest.config.SecurityConfig;
import com.example.module.mvctest.dto.PersonResponse;
import com.example.module.mvctest.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateApiController.class)
@Import(SecurityConfig.class)
class PrivateApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestService testService;

    @Test
    void getPersonList_shouldReturnList() throws Exception {
        PersonResponse p1 = new PersonResponse(1, "John", LocalDate.of(1990, 1, 1));
        PersonResponse p2 = new PersonResponse(2, "Jane", LocalDate.of(1995, 10, 20));
        given(testService.getPersonList()).willReturn(Arrays.asList(p1, p2));

        mockMvc.perform(
                get("/api/private/people")
                        .with(httpBasic("admin", "password"))
        ).andExpect(status().isOk());
    }

    @Test
    void getPersonList_shouldReturnList_bypassSecurity() throws Exception {
        PersonResponse p1 = new PersonResponse(1, "John", LocalDate.of(1990, 1, 1));
        PersonResponse p2 = new PersonResponse(2, "Jane", LocalDate.of(1995, 10, 20));
        given(testService.getPersonList()).willReturn(Arrays.asList(p1, p2));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_admin"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(
                get("/api/private/people")
                        .with(httpBasic("admin", "password"))
        ).andExpect(status().isOk());

        SecurityContextHolder.clearContext();
    }

    @Test
    void getPersonList_shouldThrowError() throws Exception {
        PersonResponse p1 = new PersonResponse(1, "John", LocalDate.of(1990, 1, 1));
        PersonResponse p2 = new PersonResponse(2, "Jane", LocalDate.of(1995, 10, 20));
        given(testService.getPersonList()).willReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/private/people"))
                .andExpect(status().isUnauthorized());
    }
}
