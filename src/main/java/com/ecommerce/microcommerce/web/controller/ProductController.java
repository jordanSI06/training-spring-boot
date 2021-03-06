package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;


@Api( description="API pour es opérations CRUD sur les produits.")

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;


    /**
     * Calculer la marge d'un produit
     * @param id id du produit
     * @return marge du produit
     */
    @GetMapping(value="/AdminProduits/marge/{id}")
    @ApiOperation(value = "Calcule la marge du produit entre le prix de vente et le prix d'achat!")
    public ResponseEntity<HashMap<Product, Integer>> calculerMargeDuProduit(@PathVariable int id) {
        Product p = productDao.findById(id);

        if( p == null ) {
            throw new ProduitIntrouvableException("Le produit avec l'id " + id);
        }
        int marge = p.getPrix() - p.getPrixAchat();

        HashMap<Product, Integer> hmap = new HashMap<Product, Integer>();
        hmap.put(p, new Integer(marge));

        return new ResponseEntity<HashMap<Product, Integer>>(hmap, HttpStatus.OK);
    }

    /**
     * Trier les produits par ordre alphabetique
     * @return list triee des produits
     */
    @GetMapping(value="/Produits/trier")
    @ApiOperation(value = "Trie par ordre alphabétique l'ordre de la liste de produit!")
    public List<Product> trierProduitsParOrdreAlphabetique() {
        return productDao.findAll(new Sort(Sort.Direction.ASC, "nom"));
    }
    //Récupérer la liste des produits

    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    @ApiOperation(value = "Liste les produits disponibles!")
    public MappingJacksonValue listeProduits() {

        Iterable<Product> produits = productDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }


    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")

    public Product afficherUnProduit(@PathVariable int id) {

        Product produit = productDao.findById(id);

        if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");

        return produit;
    }

    //ajouter un produit
    @PostMapping(value = "/Produits")
    @ApiOperation(value = "Ajoute un produit dans la liste!")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {
        // Verifier si le produit est gratuit
        if(product.getPrix() <= 0) throw new ProduitGratuitException(product.getNom());

        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/Produits/{id}")
    @ApiOperation(value = "Supprime un produit disponible dans la liste!")
    public /*boolean*/ void supprimerProduit(@PathVariable int id) {

        //productDao.deleteById(id);

        //return (!productDao.existsById(id)) ? true : false;
    }

    @PutMapping (value = "/Produits")
    @ApiOperation(value = "Met à jour  un produit disponible sur la liste!")
    public void updateProduit(@RequestBody Product product) {
        // Verifier si le produit est gratuit
        if(product.getPrix() <= 0) throw new ProduitGratuitException(product.getNom());

        productDao.save(product);
    }


    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    @ApiOperation(value = "Test de requete afin de trouver des produits chers!")
    public List<Product>  testeDeRequetes(@PathVariable int prix) {

        return productDao.chercherUnProduitCher(prix);
    }



}
