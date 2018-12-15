package com.ecommerce.microcommerce.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class ProduitGratuitException extends RuntimeException {
    /**
     * Constructeur normal
     * @param nomProduit Nom du produit gratuit
     */
    public ProduitGratuitException(String nomProduit) {
        super("Le produit '" + nomProduit + "' que vous voulez ajouter est gratuit");
    }
}
