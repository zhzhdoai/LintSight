package org.joychou.omni.checkmark.validation.checkfield;


import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
public class Input {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private int numberBetweenOneAndTen;

    @IpAddress
    @Column(nullable = false)
    private String ipAddress;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberBetweenOneAndTen() {
        return numberBetweenOneAndTen;
    }

    public void setNumberBetweenOneAndTen(int numberBetweenOneAndTen) {
        this.numberBetweenOneAndTen = numberBetweenOneAndTen;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}