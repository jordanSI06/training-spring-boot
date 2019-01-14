package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.xml.ws.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductDao productDao;

    private ProductController productController;

    protected String mapToJson(Object o) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(o);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    /*@Before
    public void setUp() {
        Product computer = new Product(1, "Ordinateur", 350, 200);
        Product robot = new Product(2, "Robot", 500, 200);

        productDao.save(computer);
        productDao.save(robot);
    }*/

    @Test
    public void getProductList() throws Exception {
        Product car = new Product(6, "Voiture", 9000, 6000);

        // List we always return for the test
        List<Product> allProducts = Arrays.asList(car);

        // every time "findAll()" is called, we return "allProducts"
        given(productDao.findAll()).willReturn(allProducts);

        // use productController method
        mockMvc.perform(get("/Produits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is(car.getNom())));
    }

    @Test
    public void insertProduct() throws Exception {
        Product car = new Product(6, "Voiture", 9000, 6000);

        // if we want to insert car, the mock will always return the car
        given(productDao.save(car)).willReturn(car);

        String inputJson = this.mapToJson(car);

        mockMvc.perform(post("/Produits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void deleteProduct() throws Exception {
        Product car = new Product(6, "Voiture", 9000, 6000);

        productDao.save(car);

        mockMvc.perform(delete("/Produits/{id}", car.getId()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void afficherProduit() throws Exception {
        Product car = new Product(6, "Voiture", 9000, 6000);

        given(productDao.findById(car.getId())).willReturn(car);

        // use productController method
        mockMvc.perform(get("/Produits/{id}", car.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is(car.getNom())));
    }

    @Test
    public void updateProduit() throws Exception {
        Product book = new Product(1, "Livre Harry Potter", 25, 20);

        given(productDao.save(book)).willReturn(book);

        String inputJson = this.mapToJson(book);

        mockMvc.perform(put("/Produits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().is2xxSuccessful());
    }
}
