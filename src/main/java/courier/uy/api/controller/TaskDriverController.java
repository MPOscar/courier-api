package courier.uy.api.controller;

import courier.uy.core.entity.Task;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.ITaskDriverService;
import courier.uy.core.services.interfaces.ITaskService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.WebApplicationException;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/taskDriver")
public class TaskDriverController {

  Logger logger = LogManager.getLogger(TaskDriverController.class);

  @Autowired
  private IErrorService errorService;

  @Autowired
  ITaskService taskService;

  @Autowired
  ITaskDriverService taskDriverService;

  private final IAuthenticationFacade authenticationFacade;

  public TaskDriverController(IAuthenticationFacade authenticationFacade) {
    this.authenticationFacade = authenticationFacade;
  }

  @PostMapping("/")
  public ResponseEntity saveTask(@RequestBody Task task) {
    try {
      Task savedTask = taskService.save(task);
      return ok(savedTask);
    } catch (Exception ex) {
      logger.log(Level.ERROR, "precios controller @PostMapping(\"/excel/actualizar\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("AtributosLaboratorioController controller @PostMapping(\"/excel/actualizar\") Error: " + ex.getMessage(), " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error al actualizar los productos - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  @GetMapping("/")
  public ResponseEntity getAllTasks(@RequestBody Task task) {
    try {
      List<Task> taskList = taskDriverService.getAllTasks();
      return ok(taskList);
    } catch (Exception ex) {
      logger.log(Level.ERROR, "precios controller @PostMapping(\"/excel/actualizar\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("AtributosLaboratorioController controller @PostMapping(\"/excel/actualizar\") Error: " + ex.getMessage(), " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error al actualizar los productos - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

}