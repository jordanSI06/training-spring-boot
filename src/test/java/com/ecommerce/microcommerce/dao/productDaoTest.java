package com.ecommerce.microcommerce.dao;

import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.controller.ProductController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.io.IOException;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class productDaoTest {
    @Autowired
    private ProductDao productDao;


    @Test
    public void findById() throws Exception{
        Product book = new Product();
        book.setNom("Livre");
        book.setPrix(20);
        book.setPrixAchat(12);
        productDao.save(book);

        System.out.println(book);

        Assert.assertEquals(book.getId(), productDao.findById(book.getId()).getId());

    }

    @Test
    public void findByPrixGreaterThan() throws Exception{
        Assert.assertEquals(2, productDao.findByPrixGreaterThan(490).size());
    }

    @Test
    public void findByNomLike() throws Exception{
        Assert.assertEquals(1, productDao.findByNomLike("Ordinateur portable").size());
    }

    @Test
    public void chercherUnProduitCher() throws Exception{
        Assert.assertEquals(2, productDao.chercherUnProduitCher(490).size());
    }

}
