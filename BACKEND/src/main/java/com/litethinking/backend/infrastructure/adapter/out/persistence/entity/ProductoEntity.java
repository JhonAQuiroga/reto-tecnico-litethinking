package com.litethinking.backend.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos", schema = "litethinking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {

    @Id
    @Column(length = 50)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    @Column(name = "precio_cop", nullable = false, precision = 18, scale = 2)
    private BigDecimal precioCop;

    @Column(name = "precio_usd", nullable = false, precision = 18, scale = 2)
    private BigDecimal precioUsd;

    @Column(name = "precio_eur", nullable = false, precision = 18, scale = 2)
    private BigDecimal precioEur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_nit", nullable = false)
    private EmpresaEntity empresa;

    @Column(nullable = false)
    private boolean activo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name            = "producto_categorias",
            schema          = "litethinking",
            joinColumns     = @JoinColumn(name = "producto_codigo"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private List<CategoriaEntity> categorias = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
