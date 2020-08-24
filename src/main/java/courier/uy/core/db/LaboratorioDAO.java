package courier.uy.core.db;

import courier.uy.core.entity.Producto;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.repository.IProductoRepository;
import courier.uy.core.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import courier.uy.api.dto.ProductoLaboratorioDTO;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Component
public class LaboratorioDAO {
	private static final Logger LOGGER = LoggerFactory.getLogger(LaboratorioDAO.class);

	@Autowired
    IProductoRepository productoRepository;

	@Autowired
    IEmpresaRepository empresaRepository;

	@Autowired
    IUserRepository userRepository;

	private final MongoTemplate mongoTemplate;

	private final MongoOperations mongoOperations;

	public LaboratorioDAO(MongoTemplate mongoTemplate, MongoOperations mongoOperations) {
		this.mongoTemplate = mongoTemplate;
		this.mongoOperations = mongoOperations;
	}


	public Optional<Producto> findByGtinAndEmpresa(String empresaId, String gtin) {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresaId).andOperator(Criteria.where("gtin").is(gtin), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if (productos.size() != 0)
			return Optional.of(productos.get(0));
		return Optional.ofNullable(null);
	}


	public ProductoLaboratorioDTO findByGtin(String empresaId, String gtin) {
		Query query = new Query();
		ProductoLaboratorioDTO productoLaboratorioDTO = new ProductoLaboratorioDTO();
		query.addCriteria(Criteria.where("sempresa").is(empresaId).andOperator(Criteria.where("gtin").is(gtin), Criteria.where("eliminado").is(false)));
		Producto producto = mongoOperations.findOne(query, Producto.class);
		if (producto != null){
			productoLaboratorioDTO = new ProductoLaboratorioDTO(producto);
		}
		return productoLaboratorioDTO;
	}

	public List<Producto> findAllByGtin(String gtin) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gtin").is(gtin).andOperator(Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		return productos;
	}

	public List<Producto> findAllByCpp(String cpp, String empresa) {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresa).andOperator(Criteria.where("cpp").is(cpp), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		return productos;
	}

	public List<ProductoLaboratorioDTO> getAllProductoLaboratorioByEmpresa(String empresaId) {
		Query query = new Query();
		List<ProductoLaboratorioDTO> productosLaboratorioDTO = new ArrayList<>();

		query.addCriteria(Criteria.where("sempresa").is(empresaId).andOperator(Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		for (Producto producto: productos) {
			ProductoLaboratorioDTO productoLaboratorioDTO = new ProductoLaboratorioDTO(producto);
			productosLaboratorioDTO.add(productoLaboratorioDTO);
		}
		return productosLaboratorioDTO;
	}


}