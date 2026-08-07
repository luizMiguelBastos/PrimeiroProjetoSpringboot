package br.com.LuizMiguel.firstProject.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ITaskReposity extends JpaRepository<TaskModel, UUID> {
  List<TaskModel> findByIdUser (UUID iduser);

}
