package courier.uy.core.services.implementations;

import courier.uy.CourierConfiguration;
import courier.uy.core.entity.Precio;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.repository.IPrecioRepository;
import courier.uy.core.services.interfaces.IPrecioService;
import courier.uy.core.utils.ExcelUtilityPrecios;
import courier.uy.core.utils.S3FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.List;

@Service
public class PrecioService implements IPrecioService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PrecioService.class);
	private CourierConfiguration configuration;

	@Autowired
    IPrecioRepository precioRepository;

	private S3FileManager s3FileManager;

	public PrecioService() {
	}

	@Transactional
	public void actualizarPrecios(InputStream inputStream) throws ServiceException {
		List<Precio> precios = ExcelUtilityPrecios.excelToProductPrecioList(inputStream);
		if(precios.size() > 0){
			String gln = precios.get(0).getGlnListaVenta();
			eliminarPreciosDeListaVenta(gln);
			for (Precio precio: precios) {
				if(!precio.getProductoCpp().equals(""))
					precioRepository.save(precio);
			}
		}
	}

	@Transactional
	public void eliminarPreciosDeListaVenta(String glnListaVenta){
		precioRepository.deleteAllByGlnListaVenta(glnListaVenta);
	}

	public List<Precio> GetPrecioByCppAndGln(String prodcutoCpp, String glnListaVenta){
		return precioRepository.findAllByProductoCppAndGlnListaVenta(prodcutoCpp, glnListaVenta);
	}
}
