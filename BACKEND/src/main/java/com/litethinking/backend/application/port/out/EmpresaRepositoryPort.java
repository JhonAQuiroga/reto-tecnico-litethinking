package com.litethinking.backend.application.port.out;

import com.litethinking.backend.domain.model.Empresa;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato de persistencia para la entidad {@link Empresa}.
 *
 * <p>Clean Architecture — Application Layer: esta interfaz define las
 * <strong>operaciones que la capa de aplicación necesita del mundo exterior</strong>
 * (base de datos) para cumplir los casos de uso de empresa. La implementación
 * concreta vive en la capa de infraestructura (adaptador JPA).</p>
 *
 * <p>Ningún detalle de JPA, SQL o Spring Data filtra hacia aquí: los parámetros
 * y tipos de retorno son exclusivamente objetos del dominio o tipos estándar de Java.</p>
 */
public interface EmpresaRepositoryPort {

    /**
     * Persiste una nueva empresa o actualiza una existente (upsert semántico).
     *
     * <p>Si la empresa ya existe con el mismo NIT, sus datos se actualizan.
     * Si no existe, se crea un nuevo registro.</p>
     *
     * @param empresa entidad de dominio a guardar; nunca {@code null}.
     * @return la instancia guardada con cualquier dato poblado por la persistencia
     *         (ej.: timestamps de auditoría), nunca {@code null}.
     */
    Empresa guardar(Empresa empresa);

    /**
     * Busca una empresa por su NIT como cadena de texto.
     *
     * @param nit representación textual del NIT (ej.: "900123456-1"); nunca {@code null}.
     * @return {@link Optional} con la empresa si existe, o vacío si no se encontró.
     */
    Optional<Empresa> buscarPorNit(String nit);

    /**
     * Verifica si ya existe una empresa con el NIT indicado.
     *
     * <p>Más eficiente que {@link #buscarPorNit(String)} cuando solo se necesita
     * la existencia (no los datos completos).</p>
     *
     * @param nit representación textual del NIT a verificar; nunca {@code null}.
     * @return {@code true} si existe al menos una empresa con ese NIT.
     */
    boolean existePorNit(String nit);

    /**
     * Recupera todas las empresas registradas en el sistema.
     *
     * @return lista de empresas; nunca {@code null}, puede estar vacía.
     */
    List<Empresa> listarTodas();

    /**
     * Recupera todas las empresas que se encuentran activas.
     *
     * @return lista de empresas activas; nunca {@code null}, puede estar vacía.
     */
    List<Empresa> listarActivas();

    /**
     * Elimina lógicamente una empresa marcándola como inactiva,
     * o la elimina físicamente según la estrategia de infraestructura.
     *
     * <p>No se realiza borrado físico por defecto para preservar la integridad
     * referencial con productos, inventarios y órdenes históricas.</p>
     *
     * @param nit representación textual del NIT de la empresa a eliminar; nunca {@code null}.
     */
    void eliminar(String nit);
}
