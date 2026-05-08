package com.company.nomeprojeto.clientes.repository;

import com.company.nomeprojeto.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("""
            select cliente from Cliente cliente
            where (:termo is null or :termo = ''
                or lower(cliente.nome) like lower(concat('%', :termo, '%'))
                or lower(cliente.email) like lower(concat('%', :termo, '%'))
                or lower(cliente.telefone) like lower(concat('%', :termo, '%'))
                or lower(cliente.cpf) like lower(concat('%', :termo, '%')))
            and (:ativo is null
                or (:ativo = true and (cliente.ativo = true or cliente.ativo is null))
                or (:ativo = false and cliente.ativo = false))
            order by cliente.nome
            """)
    List<Cliente> buscar(@Param("termo") String termo, @Param("ativo") Boolean ativo);

    long countByAtivoTrue();

    long countByAtivoFalse();
}
