package com.pruebatecnica.tareasservices.controller;

import com.pruebatecnica.tareasservices.respository.TareaRepository;
import com.pruebatecnica.tareasservices.dto.UsuarioTareaDto;
import com.pruebatecnica.tareasservices.entity.Tarea;
import com.pruebatecnica.tareasservices.entity.Usuario;
import com.pruebatecnica.tareasservices.entity.Usuario_tarea;
import com.pruebatecnica.tareasservices.execption.ApiRequestExecption;
import com.pruebatecnica.tareasservices.respository.UsuarioRepository;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PlataformaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TareaRepository tareaRepository;



    @GetMapping("/get/usarios")
    public ResponseEntity<List<Usuario>>getUsuarios() {
        return ResponseEntity.ok().body(usuarioRepository.getUsuarios());
    }


    @GetMapping("/get/tareas")
    public ResponseEntity<List<Tarea>> getTareas() {
        return ResponseEntity.ok().body(tareaRepository.getTareas());
    }


    @GetMapping("/get/usuarioByUsername/{username}")
    public ResponseEntity<Usuario>getUsuarioByUsername(@PathVariable ("username") String username) {
            return ResponseEntity.ok(usuarioRepository.getUsuarioByUsername(username));
    }

    @GetMapping("/get/tareaByCodigo/{codigo}")
    public ResponseEntity<Tarea>getUsuarioByCodigo(@PathVariable ("codigo") String codigo) {
        return ResponseEntity.ok(tareaRepository.getTareaByCodigo(codigo));
    }

    @PostMapping("/save/usuario")
    public ResponseEntity<String> saveUsuario(@RequestBody Usuario usuario) throws UnsupportedEncodingException {
        if(usuarioRepository.checkUsuario(usuario)){
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            String hash = argon2.hash(1, 1024, 1, usuario.getContraseña());
            usuario.setContraseña(hash);
            usuarioRepository.create(usuario);
            return ResponseEntity.ok("Usuario creado");
        }else {
            throw new ApiRequestExecption("El Usuario ya se encuentra registrado");
        }
    }

    @PostMapping("/add/usuarioTarea")
    public ResponseEntity<String> addUsuarioTarea(@RequestBody UsuarioTareaDto reciver) throws UnsupportedEncodingException {
        Usuario usuario =  usuarioRepository.getUsuarioByUsername(reciver.getUsername());
        Tarea tarea = tareaRepository.getTareaByCodigo(reciver.getCodigo());
        if(usuario == null && tarea == null){
            throw new ApiRequestExecption("El Usuario o la tarea no existe");
        }else {
            Usuario_tarea usuario_tarea = new Usuario_tarea();
            Hibernate.initialize(usuario_tarea);
            usuario_tarea.setId_tarea(tarea.getId_tarea());
            usuario_tarea.setId_usuario(usuario.getId_usuario());
            usuarioRepository.create(usuario_tarea);
            return ResponseEntity.ok("El usuario "+ usuario.getUsername()+" a sido agregado a la tarea "+ tarea.getCodigo_tarea());
        }
    }

    @PostMapping("/terminarTarea")
    public ResponseEntity<String> saveUsuario(@RequestBody UsuarioTareaDto reciver) throws UnsupportedEncodingException {
        Usuario usuario =  usuarioRepository.getUsuarioByUsername(reciver.getUsername());
        Tarea tarea = tareaRepository.getTareaByCodigo(reciver.getCodigo());
        if(usuario == null && tarea == null){
            throw new ApiRequestExecption("El Usuario o la tarea no existen");
        }else {
            tareaRepository.terminarTarea(tarea.getCodigo_tarea());
            return ResponseEntity.ok("El usuario "+ usuario.getUsername()+" a sido agregado a la tarea "+ tarea.getCodigo_tarea());
        }
    }
}



