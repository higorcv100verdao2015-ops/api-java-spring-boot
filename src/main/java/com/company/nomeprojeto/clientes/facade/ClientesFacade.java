package com.company.nomeprojeto.clientes.facade;

import com.company.nomeprojeto.clientes.dto.ClienteDTO;
import com.company.nomeprojeto.clientes.dto.ClientesResumoDTO;
import com.company.nomeprojeto.clientes.model.Cliente;
import com.company.nomeprojeto.clientes.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
public class ClientesFacade {

    private final ClienteRepository clienteRepository;

    public ClientesFacade(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteDTO criar(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();

        if (clienteDTO.getAtivo() == null) {
            clienteDTO.setAtivo(true);
        }

        atualizarDados(cliente, clienteDTO);
        return toDTO(clienteRepository.save(cliente));
    }

    public ClienteDTO atualizar(ClienteDTO clienteDTO, Long clienteId) {
        Cliente clienteEncontrado = buscarEntidadePorId(clienteId);

        atualizarDados(clienteEncontrado, clienteDTO);
        return toDTO(clienteRepository.save(clienteEncontrado));
    }

    public ClienteDTO atualizarParcial(ClienteDTO clienteDTO, Long clienteId) {
        Cliente clienteEncontrado = buscarEntidadePorId(clienteId);

        atualizarDadosParciais(clienteEncontrado, clienteDTO);
        return toDTO(clienteRepository.save(clienteEncontrado));
    }

    public List<ClienteDTO> getAll(String termo, Boolean ativo) {
        List<Cliente> clientes = clienteRepository.buscar(termo, ativo);

        return clientes.stream()
                .map(this::toDTO)
                .toList();
    }

    public ClientesResumoDTO getResumo() {
        long total = clienteRepository.count();
        long inativos = clienteRepository.countByAtivoFalse();
        long ativos = total - inativos;

        return new ClientesResumoDTO(total, ativos, inativos);
    }

    public ClienteDTO getById(Long clienteId) {
        return toDTO(buscarEntidadePorId(clienteId));
    }

    public void delete(Long clienteId) {
        clienteRepository.delete(buscarEntidadePorId(clienteId));
    }

    public ClienteDTO ativar(Long clienteId) {
        Cliente cliente = buscarEntidadePorId(clienteId);

        cliente.setAtivo(true);
        return toDTO(clienteRepository.save(cliente));
    }

    public ClienteDTO desativar(Long clienteId) {
        Cliente cliente = buscarEntidadePorId(clienteId);

        cliente.setAtivo(false);
        return toDTO(clienteRepository.save(cliente));
    }

    private Cliente buscarEntidadePorId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
    }

    private void atualizarDados(Cliente cliente, ClienteDTO clienteDTO) {
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setEndereco(clienteDTO.getEndereco());
        cliente.setObservacoes(clienteDTO.getObservacoes());
        cliente.setAtivo(clienteDTO.getAtivo());
    }

    private void atualizarDadosParciais(Cliente cliente, ClienteDTO clienteDTO) {
        if (clienteDTO.getNome() != null) {
            cliente.setNome(clienteDTO.getNome());
        }

        if (clienteDTO.getEmail() != null) {
            cliente.setEmail(clienteDTO.getEmail());
        }

        if (clienteDTO.getTelefone() != null) {
            cliente.setTelefone(clienteDTO.getTelefone());
        }

        if (clienteDTO.getCpf() != null) {
            cliente.setCpf(clienteDTO.getCpf());
        }

        if (clienteDTO.getEndereco() != null) {
            cliente.setEndereco(clienteDTO.getEndereco());
        }

        if (clienteDTO.getObservacoes() != null) {
            cliente.setObservacoes(clienteDTO.getObservacoes());
        }

        if (clienteDTO.getAtivo() != null) {
            cliente.setAtivo(clienteDTO.getAtivo());
        }
    }

    private ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCpf(),
                cliente.getEndereco(),
                cliente.getObservacoes(),
                cliente.getAtivo() == null || cliente.getAtivo(),
                cliente.getCriadoEm(),
                cliente.getAtualizadoEm()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class ClienteNaoEncontradoException extends RuntimeException {

        ClienteNaoEncontradoException(Long clienteId) {
            super("Cliente " + clienteId + " nao encontrado.");
        }
    }
}
