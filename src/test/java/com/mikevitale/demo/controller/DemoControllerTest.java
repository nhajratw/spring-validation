package com.mikevitale.demo.controller;

import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DemoController.class)
@AutoConfigureMockMvc
public class DemoControllerTest {
	@Autowired
	private MockMvc mvc;

	Validator validator;

	@Test
	public void validStringPathVariable() throws Exception {
		MockHttpServletRequestBuilder request = givenARequestFor("/java/string/mike");
		ResultActions actions = whenTheRequestIsMade(request);
		thenExpect(actions,
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().bytes("Username is valid".getBytes()));
	}

	@Test
	public void shoutsWhenStringPathVariableIsTooLong() throws Exception {
		MockHttpServletRequestBuilder request = givenARequestFor("/java/string/wpeurhgiouwerhgoiuwerhgo");
		ResultActions actions = whenTheRequestIsMade(request);
		final var response = "{\n" +
		                     "    \"validationErrors\": [\n" +
		                     "        {\n" +
		                     "            \"fieldName\": \"validateStringPathVariable.username\",\n" +
		                     "            \"message\": \"Username Size Validation Message\"\n" +
		                     "        }\n" +
		                     "    ]\n" +
		                     "}";
		ContentResultMatchers content = MockMvcResultMatchers.content();

		thenExpect(actions,
				MockMvcResultMatchers.status().isBadRequest(),
				content.contentType(MediaType.APPLICATION_JSON),
				content.json(response));
	}

	@Test
	public void validIntegerPathVariable() throws Exception {
		MockHttpServletRequestBuilder request = givenARequestFor("/java/int/5");
		ResultActions actions = whenTheRequestIsMade(request);
		thenExpect(actions,
				MockMvcResultMatchers.status().isOk(),
				MockMvcResultMatchers.content().bytes("ID is valid".getBytes()));
	}

	@Test
	public void shoutsWhenIntegerPathVariableIsTooLow() throws Exception {
		final var request = givenARequestFor("/java/int/4");
		final ResultActions resultActions = whenTheRequestIsMade(request);
		final var response = "{\n" +
		                     "    \"validationErrors\": [\n" +
		                     "        {\n" +
		                     "            \"fieldName\": \"validateIntPathVariable.id\",\n" +
		                     "            \"message\": \"A minimum value of 5 is required\"\n" +
		                     "        }\n" +
		                     "    ]\n" +
		                     "}";
		final var content = MockMvcResultMatchers.content();
		thenExpect(resultActions,
				MockMvcResultMatchers.status().isBadRequest(),
				content.contentType(MediaType.APPLICATION_JSON),
				content.json(response));
	}

	@Test
	public void shoutsWhenIntegerPathVariableIsTooHigh() throws Exception {
		final var request = givenARequestFor("/java/int/10000");
		final ResultActions resultActions = whenTheRequestIsMade(request);
		final var response = "{\n" +
		                     "    \"validationErrors\": [\n" +
		                     "        {\n" +
		                     "            \"fieldName\": \"validateIntPathVariable.id\",\n" +
		                     "            \"message\": \"A maximum value of 9999 can be given\"\n" +
		                     "        }\n" +
		                     "    ]\n" +
		                     "}";
		final var content = MockMvcResultMatchers.content();
		thenExpect(resultActions,
				MockMvcResultMatchers.status().isBadRequest(),
				content.contentType(MediaType.APPLICATION_JSON),
				content.json(response));
	}

	@Test
	public void shoutsWhenStringIsPassedToIntegerPathVariable() throws Exception {
		final var request = givenARequestFor("/java/int/wegyrguywery");
		final ResultActions resultActions = whenTheRequestIsMade(request);
		final var response = "{\n" +
		                     "    \"validationErrors\": [\n" +
		                     "        {\n" +
		                     "            \"fieldName\": \"id\",\n" +
		                     "            \"message\": \"Failed to convert value of type 'java.lang.String' to required type 'int'; nested exception is java.lang.NumberFormatException: For input string: \\\"wegyrguywery\\\"\"\n" +
		                     "        }\n" +
		                     "    ]\n" +
		                     "}";
		final var content = MockMvcResultMatchers.content();
		thenExpect(resultActions,
				MockMvcResultMatchers.status().isBadRequest(),
				content.contentType(MediaType.APPLICATION_JSON),
				content.json(response));
	}

	private MockHttpServletRequestBuilder givenARequestFor(String url) {
		return MockMvcRequestBuilders.get(url)
		                             .characterEncoding("UTF-8");
	}

	private ResultActions whenTheRequestIsMade(MockHttpServletRequestBuilder request) throws Exception {
		return mvc.perform(request);
	}

	private void thenExpect(ResultActions resultActions, ResultMatcher... matchers) throws Exception {
		resultActions.andExpect(ResultMatcher.matchAll(matchers));
	}
}
