package courier.uy.core.db;

import java.util.List;

import courier.uy.core.entity.Param;
import courier.uy.core.repository.IParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParamsDAO {

	@Autowired
	private IParamRepository paramRepository;

	public List<Param> getAll() {
		return paramRepository.findAll();
	}

	public Param findByNombre(String nombre) {
		return paramRepository.findFirstByNombre(nombre);
	}

}