package com.litethinking.backend.infrastructure.adapter.out.persistence.entity;

import com.litethinking.backend.domain.enums.EstadoOrden;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes", schema = "litethinking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden", nullable = false, unique = true, length = 50)
    private String numeroOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Column(name = "fecha_orden", nullable = false)
    private ZonedDateTime fechaOrden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoOrden estado;

    @Column(name = "total_cop", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCop;

    @OneToMany(
            mappedBy = "orden",
            cascade  = CascadeType.ALL,
            orphanRemoval = true,
            fetch    = FetchType.LAZY
    )
    @Builder.Default
    private List<OrdenProductoEntity> productos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
