package com.juana.turnos_api.service;

import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente crear(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> actualizar(Long id, Cliente datosNuevos) {
        Optional<Cliente> encontrado = buscarPorId(id);
        if (encontrado.isPresent()) {
            Cliente cliente = encontrado.get();
            cliente.setNombre(datosNuevos.getNombre());
            cliente.setEmail(datosNuevos.getEmail());
            cliente.setTelefono(datosNuevos.getTelefono());
            clienteRepository.save(cliente);
        }
        return encontrado;
    }

    public boolean eliminar(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
