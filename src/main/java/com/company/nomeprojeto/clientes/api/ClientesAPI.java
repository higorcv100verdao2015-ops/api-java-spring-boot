package com.company.nomeprojeto.clientes.api;

import com.company.nomeprojeto.clientes.dto.ClienteDTO;
import com.company.nomeprojeto.clientes.dto.ClientesResumoDTO;
import com.company.nomeprojeto.clientes.facade.ClientesFacade;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClientesAPI {

    private final ClientesFacade clientesFacade;

    public ClientesAPI(ClientesFacade clientesFacade) {
        this.clientesFacade = clientesFacade;
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteCriado = clientesFacade.criar(clienteDTO);
        URI location = URI.create("/clientes/" + clienteCriado.getId());

        return ResponseEntity.created(location).body(clienteCriado);
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable("clienteId") Long clienteId,
                                                @Valid @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok(clientesFacade.atualizar(clienteDTO, clienteId));
    }

    @PatchMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> atualizarParcial(@PathVariable("clienteId") Long clienteId,
                                                       @RequestBody ClienteDTO clienteDTO) {
        return ResponseEntity.ok(clientesFacade.atualizarParcial(clienteDTO, clienteId));
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAll(@RequestParam(value = "termo", required = false) String termo,
                                                   @RequestParam(value = "ativo", required = false) Boolean ativo) {
        return ResponseEntity.ok(clientesFacade.getAll(termo, ativo));
    }

    @GetMapping("/resumo")
    public ResponseEntity<ClientesResumoDTO> getResumo() {
        return ResponseEntity.ok(clientesFacade.getResumo());
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> getById(@PathVariable("clienteId") Long clienteId) {
        return ResponseEntity.ok(clientesFacade.getById(clienteId));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> delete(@PathVariable("clienteId") Long clienteId) {
        clientesFacade.delete(clienteId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{clienteId}/ativar")
    public ResponseEntity<ClienteDTO> ativar(@PathVariable("clienteId") Long clienteId) {
        return ResponseEntity.ok(clientesFacade.ativar(clienteId));
    }

    @PatchMapping("/{clienteId}/desativar")
    public ResponseEntity<ClienteDTO> desativar(@PathVariable("clienteId") Long clienteId) {
        return ResponseEntity.ok(clientesFacade.desativar(clienteId));
    }
}
