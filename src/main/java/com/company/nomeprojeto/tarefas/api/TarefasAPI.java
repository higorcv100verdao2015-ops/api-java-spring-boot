package com.company.nomeprojeto.tarefas.api;

import com.company.nomeprojeto.tarefas.dto.TarefaDTO;
import com.company.nomeprojeto.tarefas.facade.TarefasFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefasAPI {

    private final TarefasFacade tarefasFacade;

    public TarefasAPI(TarefasFacade tarefasFacade) {
        this.tarefasFacade = tarefasFacade;
    }

    @PostMapping
    public ResponseEntity<TarefaDTO> criar(@RequestBody TarefaDTO tarefaDTO) {
        TarefaDTO tarefaCriada = tarefasFacade.criar(tarefaDTO);
        URI location = URI.create("/tarefas/" + tarefaCriada.getId());

        return ResponseEntity.created(location).body(tarefaCriada);
    }

    @PutMapping("/{tarefaId}")
    public ResponseEntity<TarefaDTO> atualizar(@PathVariable("tarefaId") Long tarefaId,
                                               @RequestBody TarefaDTO tarefaDTO) {
        return ResponseEntity.ok(tarefasFacade.atualizar(tarefaDTO, tarefaId));
    }

    @PatchMapping("/{tarefaId}")
    public ResponseEntity<TarefaDTO> atualizarParcial(@PathVariable("tarefaId") Long tarefaId,
                                                      @RequestBody TarefaDTO tarefaDTO) {
        return ResponseEntity.ok(tarefasFacade.atualizarParcial(tarefaDTO, tarefaId));
    }

    @GetMapping
    public ResponseEntity<List<TarefaDTO>> getAll() {
        return ResponseEntity.ok(tarefasFacade.getAll());
    }

    @GetMapping("/{tarefaId}")
    public ResponseEntity<TarefaDTO> getById(@PathVariable("tarefaId") Long tarefaId) {
        return ResponseEntity.ok(tarefasFacade.getById(tarefaId));
    }

    @DeleteMapping("/{tarefaId}")
    public ResponseEntity<Void> delete(@PathVariable("tarefaId") Long tarefaId) {
        tarefasFacade.delete(tarefaId);
        return ResponseEntity.noContent().build();
    }
}
