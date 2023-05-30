package model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private double quantity;

    private Integer piece;
    private double kilograms;
    private String invoiceNumber;
    private String expirationDate;

    public Product(String name, double quantity, String invoiceNumber, String expirationDate, double kilograms, Integer piece) {
        this.name = name;
        this.quantity = quantity;
        this.invoiceNumber = invoiceNumber;
        this.expirationDate = expirationDate;
        this.kilograms = kilograms;
        this.piece = piece;
    }

    public Product() {

    }


    public Integer getPiece(){
        return piece;
    }
    public void setPiece(int piece){
        this.piece = piece;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public double getKilograms() {
        return  kilograms;
    }
    public void setKilograms(double kilograms) {
        this.kilograms = kilograms;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
