package com.company.nomeprojeto.tarefas.facade;

import com.company.nomeprojeto.tarefas.dto.TarefaDTO;
import com.company.nomeprojeto.tarefas.model.Tarefa;
import com.company.nomeprojeto.tarefas.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
public class TarefasFacade {

    private final TarefaRepository tarefaRepository;

    public TarefasFacade(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public TarefaDTO criar(TarefaDTO tarefaDTO) {
        Tarefa tarefa = new Tarefa();

        if (tarefaDTO.getConcluida() == null) {
            tarefaDTO.setConcluida(false);
        }

        atualizarDados(tarefa, tarefaDTO);
        return toDTO(tarefaRepository.save(tarefa));
    }

    public TarefaDTO atualizar(TarefaDTO tarefaDTO, Long tarefaId) {
        Tarefa tarefaEncontrada = buscarEntidadePorId(tarefaId);

        atualizarDados(tarefaEncontrada, tarefaDTO);
        return toDTO(tarefaRepository.save(tarefaEncontrada));
    }

    public TarefaDTO atualizarParcial(TarefaDTO tarefaDTO, Long tarefaId) {
        Tarefa tarefaEncontrada = buscarEntidadePorId(tarefaId);

        atualizarDadosParciais(tarefaEncontrada, tarefaDTO);
        return toDTO(tarefaRepository.save(tarefaEncontrada));
    }

    public List<TarefaDTO> getAll() {
        return tarefaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public TarefaDTO getById(Long tarefaId) {
        return toDTO(buscarEntidadePorId(tarefaId));
    }

    public void delete(Long tarefaId) {
        tarefaRepository.delete(buscarEntidadePorId(tarefaId));
    }

    private Tarefa buscarEntidadePorId(Long tarefaId) {
        return tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new TarefaNaoEncontradaException(tarefaId));
    }

    private void atualizarDados(Tarefa tarefa, TarefaDTO tarefaDTO) {
        tarefa.setTitulo(tarefaDTO.getTitulo());
        tarefa.setDescricao(tarefaDTO.getDescricao());
        tarefa.setConcluida(tarefaDTO.getConcluida());
    }

    private void atualizarDadosParciais(Tarefa tarefa, TarefaDTO tarefaDTO) {
        if (tarefaDTO.getTitulo() != null) {
            tarefa.setTitulo(tarefaDTO.getTitulo());
        }

        if (tarefaDTO.getDescricao() != null) {
            tarefa.setDescricao(tarefaDTO.getDescricao());
        }

        if (tarefaDTO.getConcluida() != null) {
            tarefa.setConcluida(tarefaDTO.getConcluida());
        }
    }

    private TarefaDTO toDTO(Tarefa tarefa) {
        return new TarefaDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getConcluida()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class TarefaNaoEncontradaException extends RuntimeException {

        TarefaNaoEncontradaException(Long tarefaId) {
            super("Tarefa " + tarefaId + " nao encontrada.");
        }
    }
}
