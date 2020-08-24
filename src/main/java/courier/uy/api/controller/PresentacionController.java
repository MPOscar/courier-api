package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.List;

import courier.uy.core.db.PresentacionesDAO;
import courier.uy.core.entity.Presentacion;
import courier.uy.core.resources.dto.Representacion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/presentaciones")
public class PresentacionController {
	Logger logger = LogManager.getLogger(PresentacionController.class);
	private final PresentacionesDAO presentacionesDAO;

	public PresentacionController(PresentacionesDAO presentacionesDAO) {
		this.presentacionesDAO = presentacionesDAO;
	}

	@GetMapping("")
	public ResponseEntity getPresentaciones() {
		return ok(new Representacion<List<Presentacion>>(HttpStatus.OK.value(), presentacionesDAO.getVisible()));
	}

}
