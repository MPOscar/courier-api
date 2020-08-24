package courier.uy.api.controller;
import static org.springframework.http.ResponseEntity.ok;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import courier.uy.core.db.ParamsDAO;
import courier.uy.core.entity.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalogo")
public class CatalogoController {
	Logger logger = LogManager.getLogger(LoginController.class);
	@Autowired
	private final ParamsDAO paramsDAO;

	public CatalogoController(ParamsDAO paramsDAO) {
		this.paramsDAO = paramsDAO;
	}

	@GetMapping("")
	public ResponseEntity catalogo() {
		Map<String, Object> result = new HashMap<>();
		List<Param> people = paramsDAO.getAll();
		for (Param p : people) {
			result.put(p.getNombre(), p.getValor());
		}
		return ok(result);
	}

}
