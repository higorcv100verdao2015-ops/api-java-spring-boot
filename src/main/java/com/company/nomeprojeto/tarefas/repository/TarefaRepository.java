package com.company.nomeprojeto.tarefas.repository;

import com.company.nomeprojeto.tarefas.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}
