package com.rootcode.backend.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

//I didn't use lombok. my IDE has an issue with the annotation process. so i did it manually


@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public void setBorrowRecords(Set<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }

    private String name = "";

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<BorrowRecord> borrowRecords = new HashSet<>();

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }



}
