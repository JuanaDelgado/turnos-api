package com.juana.turnos_api.service;

import com.juana.turnos_api.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final List<Cliente> clientes = new ArrayList<>(List.of(
            new Cliente(1L, "Noelia Martínez", "noelia@gmail.com", "1112345678"),
            new Cliente(2L, "Tomás López", "tomas1@gmail.com", "1112348765"),
            new Cliente(3L, "María García", "margarcia@gmail.com", "1187654321")
    ));

    private Long proximoId = 4L;

    public List<Cliente> listar() {
        return clientes;
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clientes.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Cliente crear(Cliente cliente) {
        cliente.setId(proximoId);
        proximoId++;
        clientes.add(cliente);
        return cliente;
    }

    public Optional<Cliente> actualizar(Long id, Cliente datosNuevos) {
        Optional<Cliente> encontrado = buscarPorId(id);
        if (encontrado.isPresent()) {
            Cliente cliente = encontrado.get();
            cliente.setNombre(datosNuevos.getNombre());
            cliente.setEmail(datosNuevos.getEmail());
            cliente.setTelefono(datosNuevos.getTelefono());
        }
        return encontrado;
    }

    public boolean eliminar(Long id) {
        return clientes.removeIf(p -> p.getId().equals(id));
    }

}
