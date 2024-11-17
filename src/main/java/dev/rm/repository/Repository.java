package dev.rm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.model.Model;

public interface Repository extends JpaRepository<Model, Long> {

}
