package com.litethinking.backend.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orden_productos", schema = "litethinking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenProductoEntity {

    @EmbeddedId
    private OrdenProductoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ordenId")
    @JoinColumn(name = "orden_id")
    private OrdenEntity orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoCodigo")
    @JoinColumn(name = "producto_codigo")
    private ProductoEntity producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 18, scale = 2)
    private BigDecimal precioUnitario;
}
