package com.bci.usersapp.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;

@Setter
@Getter
@Builder
@Entity
@Table(name = "PHONES")
@AllArgsConstructor
@NoArgsConstructor
public class Phone {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NUMBER")
    private Integer number;

    @Column(name = "CITY_CODE")
    private Integer cityCode;

    @Column(name = "COUNTRY_CODE")
    private Integer countryCode;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
