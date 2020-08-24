package courier.uy.core.services.implementations;

import courier.uy.core.db.ErrorsDAO;
import courier.uy.core.entity.Usuario;
import courier.uy.core.services.interfaces.IErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ErrorService implements IErrorService {

	@Autowired
	private ErrorsDAO errorsRepository;

	public ErrorService (ErrorsDAO errorsDAO) {
		errorsRepository = errorsDAO;
	}

	@Override
	public void Log(String message, Usuario usuario) {
		this.errorsRepository.insert(message, usuario);
	}
	@Override
	public void Log(String message) {
		this.errorsRepository.insert(message);
	}
	@Override
	public void Log(String message, String stackTrace) {
		this.errorsRepository.insert(message, stackTrace);
	}
}
