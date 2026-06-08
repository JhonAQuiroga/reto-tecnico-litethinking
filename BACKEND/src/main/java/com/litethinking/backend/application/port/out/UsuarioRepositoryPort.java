package com.litethinking.backend.application.port.out;

import com.litethinking.backend.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato de persistencia para la entidad {@link Usuario}.
 *
 * <p>Clean Architecture — Application Layer: define las operaciones que los casos de uso
 * de autenticación y gestión de usuarios requieren de la infraestructura.
 * La implementación concreta vive en la capa de infraestructura (adaptador JPA
 * integrado con Spring Security).</p>
 *
 * <p><strong>Nota de seguridad:</strong> el campo {@code passwordHash} que expone
 * {@link Usuario#getPasswordHash()} nunca debe ser incluido en respuestas de API.
 * Su uso está restringido a la autenticación en la capa de infraestructura.</p>
 */
public interface UsuarioRepositoryPort {

    /**
     * Persiste un nuevo usuario o actualiza uno existente.
     *
     * <p>La contraseña ya debe llegar <em>hasheada</em> con BCrypt antes de
     * invocar este método; la responsabilidad del hashing es del caso de uso.</p>
     *
     * @param usuario entidad de dominio a guardar; nunca {@code null}.
     * @return la instancia guardada con timestamps de auditoría poblados; nunca {@code null}.
     */
    Usuario guardar(Usuario usuario);

    /**
     * Busca un usuario por su dirección de correo electrónico (clave natural de negocio).
     *
     * <p>Se usa principalmente durante el proceso de autenticación para recuperar
     * el hash BCrypt y verificar las credenciales.</p>
     *
     * @param email dirección de correo del usuario (en minúsculas); nunca {@code null}.
     * @return {@link Optional} con el usuario si existe, o vacío si no se encontró.
     */
    Optional<Usuario> buscarPorEmail(String email);

    /**
     * Busca un usuario por su identificador de base de datos.
     *
     * @param id identificador numérico del usuario; nunca {@code null}.
     * @return {@link Optional} con el usuario si existe, o vacío si no se encontró.
     */
    Optional<Usuario> buscarPorId(Long id);

    /**
     * Verifica si ya existe un usuario con el email indicado.
     *
     * <p>Más eficiente que {@link #buscarPorEmail(String)} cuando solo se necesita
     * la existencia (no los datos completos). Se usa en el registro de nuevos usuarios.</p>
     *
     * @param email dirección de correo a verificar; nunca {@code null}.
     * @return {@code true} si existe al menos un usuario con ese email.
     */
    boolean existePorEmail(String email);

    /**
     * Recupera todos los usuarios registrados en el sistema.
     *
     * @return lista de usuarios; nunca {@code null}, puede estar vacía.
     */
    List<Usuario> listarTodos();
}
