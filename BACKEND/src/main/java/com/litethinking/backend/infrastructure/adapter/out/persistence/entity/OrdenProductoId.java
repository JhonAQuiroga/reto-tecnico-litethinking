package com.litethinking.backend.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrdenProductoId implements Serializable {

    @Column(name = "orden_id")
    private Long ordenId;

    @Column(name = "producto_codigo")
    private String productoCodigo;
}
