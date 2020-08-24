package courier.uy.core.services.implementations;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.ws.rs.core.MultivaluedMap;

import courier.uy.CourierConfiguration;
import courier.uy.core.db.*;
import courier.uy.core.resources.dto.*;
import courier.uy.core.utils.ExcelUtility;
import courier.uy.core.utils.S3FileManager;
import courier.uy.core.utils.mapstruct.Cloner;
import courier.uy.core.utils.poiji.ExcelRNCliente;
import courier.uy.core.utils.poiji.ExcelRNProducto;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import courier.uy.core.entity.Grupo;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.entity.Pallet;
import courier.uy.core.entity.Param;
import courier.uy.core.entity.Presentacion;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.ProductoAccion;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.services.interfaces.IProductService;

@Service
public class ProductsService implements IProductService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductsService.class);
	private CourierConfiguration configuration;

	@Autowired
	private ProductosDAO productsRepository;
	@Autowired
	private EmpresasDAO businessesRepository;
	@Autowired
	private CategoriasDAO categoriesRepository;
	@Autowired
	private PresentacionesDAO presentationsRepository;
	@Autowired
	private EmpaquesDAO packsRepository;
	@Autowired
	private EmpresasDAO empresasDAO;
	@Autowired
	private GruposDAO groupsRepository;
	@Autowired
	private PalletsDAO palletsRepository;
	@Autowired
	private ProductosAccionesDAO productActionsRepository;
	@Autowired
	private ParamsDAO paramsDAO;
	@Autowired
	private ListasDeVentaDAO listasDeVentaDAO;

	private S3FileManager s3FileManager;

	public ProductsService(ProductosDAO productosDao, CategoriasDAO categoriasDAO, PresentacionesDAO presentacionesDAO,
						   EmpaquesDAO empaquesDAO, EmpresasDAO empresasDAO, GruposDAO gruposDAO, PalletsDAO palletsDAO,
						   ProductosAccionesDAO productosAccionesDAO, ParamsDAO paramsDAO, CourierConfiguration configuration, S3FileManager s3FileManager) {
		this.productsRepository = productosDao;
		this.categoriesRepository = categoriasDAO;
		this.presentationsRepository = presentacionesDAO;
		this.packsRepository = empaquesDAO;
		this.businessesRepository = empresasDAO;
		this.groupsRepository = gruposDAO;
		this.palletsRepository = palletsDAO;
		this.productActionsRepository = productosAccionesDAO;
		this.paramsDAO = paramsDAO;
		this.configuration = configuration;
		this.s3FileManager = s3FileManager;
	}

	@Override
	public void InsertExcel(List<ExcelProduct> products, UsuarioPrincipal user, Boolean updateExistent,
                            Boolean deleteExistent, Boolean deleteAll) throws ServiceException {
		try {
			if (deleteAll) {
				try {
					this.productsRepository.deleteAll(user.getUsuarioEmpresa().getEmpresa().getId());
				} catch (Exception ex) {
					throw new Exception(
							"No se pudieron eliminar los Productos asociados a la Empresa " + ex.getMessage());
				}
			}

			for (ExcelProduct excelProduct : products) {
				try {
					checkIfProductExists(excelProduct, updateExistent, deleteExistent);
					if (excelProduct.getWasCreated()) {
						Producto product = excelProduct.getProduct();
						addCategory(user.getUsuarioEmpresa().getEmpresa(), product);
						addPresentations(product);
						product.setEmpresa(user.getUsuarioEmpresa().getEmpresa());
						product.setSempresa(user.getUsuarioEmpresa().getEmpresa().getSId());
						addPacks(user.getUsuarioEmpresa().getEmpresa(), excelProduct);
					}

				} catch (Exception e) {
					excelProduct.setWasCreated(false);
					excelProduct.setHasErrors(true);
					excelProduct.bddErrors += "Error inesperado " + e.getMessage() + " ";
					LOGGER.error("PRODUCTOSSERVICE EXCEPTION: " + e.getMessage());
				}
			}
			this.productsRepository.insertBatch(products);
		} catch (Exception e) {
			throw new ServiceException("No se pudieron agregar los productos " + e.getMessage());
		}
	}

	@Override
	public void InsertExcelRN(List<ExcelProduct> products, Company empresa, Boolean updateExistent,
							  Boolean deleteExistent, Boolean deleteAll) throws ServiceException {
		try {
			if (deleteAll) {
				try {
					this.productsRepository.deleteAll(empresa.getId());
				} catch (Exception ex) {
					throw new Exception(
							"No se pudieron eliminar los Productos asociados a la Empresa " + ex.getMessage());
				}
			}

			for (ExcelProduct excelProduct : products) {
				try {
					checkIfProductExistsRN(excelProduct, updateExistent, deleteExistent);
					if (excelProduct.getWasCreated() && excelProduct.getProduct().getId() == null) {
						Producto product = excelProduct.getProduct();
						addCategory(empresa, product);
						addPresentations(product);
						product.setEmpresa(empresa);
						addPacks(empresa, excelProduct);
					}

				} catch (Exception e) {
					excelProduct.setWasCreated(false);
					excelProduct.setHasErrors(true);
					excelProduct.bddErrors += "Error inesperado " + e.getMessage() + " ";
					LOGGER.error("PRODUCTOSSERVICE EXCEPTION: " + e.getMessage());
				}
			}
			this.productsRepository.insertBatch(products);
		} catch (Exception e) {
			throw new ServiceException("No se pudieron agregar los productos " + e.getMessage());
		}
	}

	public void InsertClienteExcel(List<ExcelCliente> clientes, UsuarioPrincipal user, Boolean updateExistent,
                                   Boolean deleteExistent, Boolean deleteAll) throws ServiceException {
		try {

			for (ExcelCliente excelCliente : clientes) {
				try {
					Optional<Company> OptionalEmpresa = businessesRepository.findByKey("gln", excelCliente.getEmpresa().getGln());
					Company empresa = OptionalEmpresa.isPresent() ? OptionalEmpresa.get() : null;
					Set<Grupo> grupoRN = excelCliente.getEmpresa().getGrupos();

					if (empresa != null) {
						for (Grupo grupo : grupoRN)
							grupo.setEmpresa(empresa);
						empresa.setGrupos(grupoRN);
						businessesRepository.update(empresa);
						excelCliente.setHasErrors(false);
					}

				} catch (Exception e) {
					excelCliente.setWasCreated(false);
					excelCliente.setHasErrors(true);
					excelCliente.bddErrors += "Error inesperado " + e.getMessage() + " ";
					LOGGER.error("PRODUCTOSSERVICE EXCEPTION: " + e.getMessage());
				}
			}
			// this.productsRepository.insertBatch(products, user);
		} catch (Exception e) {
			throw new ServiceException("No se pudieron agregar los productos " + e.getMessage());
		}
	}

	/**
	 * 
	 * Verifica si existe un {@link Producto} a partir del GTIN/CPP y lanza una
	 * excepción en caso que esto se cumpla
	 * 
	 * @param product
	 * @throws ServiceException
	 */
	private void checkIfProductExists(Producto product) throws ServiceException {
		// Optional<Producto> existingCpp =
		// this.productsRepository.findByCpp(product.getCpp()); TODO
		Optional<Producto> existe = this.productsRepository.findByGtinAndCpp(product.getGtin(), product.getCpp());
		Optional<Producto> existeProductoCppMismaEmpresa = this.productsRepository
				.findByIdEmpresaAndCpp(product.getEmpresa().getId(), product.getCpp());
		if (existe.isPresent()) {
			throw new ServiceException("Ya existe un producto con este GTIN/CPP");
		}
		if (existeProductoCppMismaEmpresa.isPresent()) {
			throw new ServiceException("Esta empresa ya tiene otro producto con este cpp");
		}
	}

	/**
	 *
	 * Chequea si existe un {@link Producto} y de existir se actualizan sus Datos
	 * solo si se solicita mediante parámetros. Se registran errores en caso del
	 * {@link Producto} no pertenecer a la {@link Company} actualmente logueada. Se
	 * puede especificar además que se eliminen los {@link Producto} y se realizará
	 * siempre que no se especifique que se desea actualizar el {@link Producto}
	 *
	 * @param excelProduct
	 * @param actualizarExistentes
	 * @param eliminarExistentes
	 */
	private ExcelProduct checkIfProductExists(final ExcelProduct excelProduct, final Boolean actualizarExistentes,
											  final Boolean eliminarExistentes) {
		if (excelProduct.getWasCreated()) {
			Optional<Producto> producto = this.productsRepository.findByGtinAndCppAndEmpresaForExcelUpdate(
					excelProduct.getProduct().getGtin(), excelProduct.getProduct().getCpp(),
					excelProduct.getProduct().getEmpresa().getId());

			producto.ifPresent(e -> {
				if (e.getEmpresa().getId().equals(excelProduct.getProduct().getEmpresa().getId())) {
					if (actualizarExistentes || eliminarExistentes) {
						if (eliminarExistentes) {
							e.eliminar();
						}
						excelProduct.setWasCreated(true);
						Producto bddProd = e;
						bddProd.copy(excelProduct.getProduct());
						bddProd.noEliminado();
						excelProduct.setProduct(bddProd);
					} else {
						excelProduct.setWasCreated(false);
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += "Existe otro Producto con este GTIN/CPP y no pertenece a la Empresa actualmente logueada. ";
					}
				} else if (!actualizarExistentes) {
					if (eliminarExistentes) {
						e.eliminar();
					} else {
						excelProduct.setWasCreated(false);
						excelProduct.setHasErrors(true);
						excelProduct.bddErrors += "Existe otro Producto con este GTIN/CPP y no se solicitó actualizar Productos existentes. ";
					}
				}
			});
		}

		return excelProduct;

	}


	private ExcelProduct checkIfProductExistsRN(final ExcelProduct excelProduct, final Boolean actualizarExistentes,
			final Boolean eliminarExistentes) {
		if (excelProduct.getWasCreated()) {
			Optional<Producto> producto = this.productsRepository.findByGtinAndCpp(excelProduct.getProduct().getGtin(),
					excelProduct.getProduct().getCpp());

			producto.ifPresent(e -> {
				if (actualizarExistentes) {
					excelProduct.setWasCreated(true);
					Producto bddProd = e;
					bddProd.copy(excelProduct.getProduct());
					excelProduct.setProduct(bddProd);

				} else {
					excelProduct.setWasCreated(false);
					excelProduct.setHasErrors(true);
					excelProduct.bddErrors += "Existe otro Producto con este GTIN/CPP y no pertenece a la Empresa actualmente logueada. ";
				}
			});
		}

		return excelProduct;

	}

	private void addPacks(UsuarioPrincipal user, Producto product) throws ServiceException {
		Company business = user.getUsuarioEmpresa().getEmpresa();
		HashSet<Empaque> bddPacks = new HashSet<Empaque>();
		String id = business.getId();
		business.setId(id);
		if (product.getEmpaques() != null && product.getEmpaques().size() != 0) {

			for (Empaque emp : product.getEmpaques()) {
				Empaque pack = new Empaque();
				Empaque fatherPack = new Empaque();
				Empaque grandFatherPack = new Empaque();
				pack.setEmpresa(business);
				pack.setSempresa(business.getSId());
				fatherPack.setEmpresa(business);
				grandFatherPack.setEmpresa(business);
				grandFatherPack.setSempresa(business.getSId());
				emp.setEmpresa(business);
				if (emp.getPadre() != null) {
					emp.getPadre().setEmpresa(business);
					if (emp.getPadre().getPadre() != null)
						emp.getPadre().getPadre().setEmpresa(business);
				}
				Optional<Empaque> optPack = packsRepository.findByGtin("" + emp.getGtin());
				if (optPack.isPresent() && !optPack.get().getGtin().equals("")
						&& !(optPack.get().getEmpresa().getId().equals(business.getId())) ) {
					throw new ServiceException("Existe otro empaque con este gtin. ");
				} else {
					if (optPack.isPresent() && !optPack.get().getGtin().equals("")
							&& optPack.get().getEmpresa().getId().equals(business.getId()) ) {// si hay empaque de la misma
																						// empresa lo uso
						pack = optPack.get();
						pack.copyForExcel(emp.getGtin(), "" + emp.getNivel(), emp.getUnidadMedida(), "" + emp.getAlto(),
								"" + emp.getAncho(), "" + emp.getProfundidad(), "" + emp.getPesoBruto(),
								"" + emp.getCantidad(), emp.getCpp(), emp.getClasificacion());
						pack.setEmpresa(business);
						pack.setSempresa(business.getSId());
						pack.setPresentacion(emp.getPresentacion());
						this.packsRepository.save(pack);
					} else {
						pack = packsRepository.crear(emp, business);
					}
					if (emp.getPadre() != null && !emp.getPadre().getGtin().equals(emp.getGtin())) {
						emp.getPadre().setEmpresa(business);
						Optional<Empaque> optFather = packsRepository.findByGtin("" + emp.getPadre().getGtin());
						if (optFather.isPresent() && !optFather.get().getGtin().equals("")
								&& !(optFather.get().getEmpresa().getId().equals(business.getId()))) {
							throw new ServiceException("Existe otro empaque con este gtin. ");
						} else {
							if (optFather.isPresent() && !optFather.get().getGtin().equals("")
									&& optFather.get().getEmpresa().getId() == business.getId()) {
								fatherPack = optFather.get();
								fatherPack.copyForExcel(emp.getPadre().getGtin(), "" + emp.getPadre().getNivel(),
										emp.getPadre().getUnidadMedida(), "" + emp.getPadre().getAlto(),
										"" + emp.getPadre().getAncho(), "" + emp.getPadre().getProfundidad(),
										"" + emp.getPadre().getPesoBruto(), "" + emp.getPadre().getCantidad(),
										emp.getPadre().getCpp(), emp.getPadre().getClasificacion());
								fatherPack.setEmpresa(business);
								fatherPack.setPresentacion(emp.getPadre().getPresentacion());
								this.packsRepository.save(fatherPack);
							} else {
								fatherPack = packsRepository.crear(emp.getPadre(), business);
							}
							pack.setPadre(fatherPack);
						}
						if (emp.getPadre().getPadre() != null && !emp.getPadre().getPadre().getGtin().equals(emp.getGtin()) && !emp.getPadre().getPadre().getGtin().equals(emp.getPadre().getGtin())) {
							emp.getPadre().getPadre().setEmpresa(business);
							Optional<Empaque> optGrandFather = packsRepository
									.findByGtin("" + emp.getPadre().getPadre().getGtin());
							if (optGrandFather.isPresent() && !optGrandFather.get().getGtin().equals("")
									&& optGrandFather.get().getEmpresa().getId() != business.getId()) {
								throw new ServiceException("Existe otro empaque con este gtin. ");
							} else {
								if (optGrandFather.isPresent() && !optGrandFather.get().getGtin().equals("")
										&& optGrandFather.get().getEmpresa().getId().equals(business.getId())) {
									grandFatherPack = optGrandFather.get();
									grandFatherPack.copyForExcel(emp.getPadre().getPadre().getGtin(),
											"" + emp.getPadre().getPadre().getNivel(),
											emp.getPadre().getPadre().getUnidadMedida(),
											"" + emp.getPadre().getPadre().getAlto(),
											"" + emp.getPadre().getPadre().getAncho(),
											"" + emp.getPadre().getPadre().getProfundidad(),
											"" + emp.getPadre().getPadre().getPesoBruto(),
											"" + emp.getPadre().getPadre().getCantidad(),
											emp.getPadre().getPadre().getCpp(),
											emp.getPadre().getPadre().getClasificacion());
									grandFatherPack.setEmpresa(business);
									grandFatherPack.setPresentacion(emp.getPadre().getPadre().getPresentacion());
									this.packsRepository.save(grandFatherPack);
								} else {
									grandFatherPack = packsRepository.crear(emp.getPadre().getPadre(), business);
								}
								pack.getPadre().setPadre(grandFatherPack);
							}

						}
					}
					bddPacks.add(pack);
					pack = this.packsRepository.save(pack);
					product.getSempaques().add(pack.getSId());
				}

			}
		}
		product.setEmpaques(bddPacks);
	}

	private void addPacks(Company empresa, ExcelProduct excelProduct) {
		Producto product = excelProduct.getProduct();
		Company business = empresa;
		HashSet<Empaque> bddPacks = new HashSet<Empaque>();
		HashSet<String> sbddPacks = new HashSet<String>();
		if (product.getEmpaques() != null && product.getEmpaques().size() != 0) {

			for (Empaque emp : product.getEmpaques()) {
				Empaque pack = new Empaque();
				Empaque fatherPack = new Empaque();
				Empaque grandFatherPack = new Empaque();

				Optional<Empaque> optPack = packsRepository.findByGtin("" + emp.getGtin());
				if (optPack.isPresent() && !optPack.get().getGtin().equals("")
						&& !(optPack.get().getEmpresa().getId().equals(business.getId())) ) {
					excelProduct.setHasErrors(true);
					excelProduct.setWasCreated(false);
					excelProduct.bddErrors += "Existe otro empaque con este gtin. ";
					excelProduct.getProduct().setEmpaques(null);
				} else {
					if (optPack.isPresent() && !optPack.get().getGtin().equals("")
							&& optPack.get().getEmpresa().getId().equals(business.getId())) {// si hay empaque de la misma
																						// empresa lo uso
						pack = optPack.get();
						pack.copyForExcel(emp.getGtin(), "" + emp.getNivel(), emp.getUnidadMedida(), "" + emp.getAlto(),
								"" + emp.getAncho(), "" + emp.getProfundidad(), "" + emp.getPesoBruto(),
								"" + emp.getCantidad(), emp.getCpp(), emp.getClasificacion());
						pack.setEmpresa(business);
						pack.setPresentacion(emp.getPresentacion());
					}

					else {
						pack = packsRepository.crear(emp, empresa);
					}
					if (emp.getPadre() != null) {
						Optional<Empaque> optFather = packsRepository.findByGtin("" + emp.getPadre().getGtin());
						if (optFather.isPresent() && !optFather.get().getGtin().equals("")
								&& !(optFather.get().getEmpresa().getId().equals(business.getId())) ) {
							excelProduct.setHasErrors(true);
							excelProduct.setWasCreated(false);
							excelProduct.bddErrors += "Existe otro empaque con este gtin. ";
							pack.setPadre(null);
						} else {
							if (optFather.isPresent() && !optFather.get().getGtin().equals("")
									&& !(optFather.get().getEmpresa().getId().equals(business.getId()))) {
								fatherPack = optFather.get();
								fatherPack.copyForExcel(emp.getPadre().getGtin(), "" + emp.getPadre().getNivel(),
										emp.getPadre().getUnidadMedida(), "" + emp.getPadre().getAlto(),
										"" + emp.getPadre().getAncho(), "" + emp.getPadre().getProfundidad(),
										"" + emp.getPadre().getPesoBruto(), "" + emp.getPadre().getCantidad(),
										emp.getPadre().getCpp(), emp.getPadre().getClasificacion());
								fatherPack.setEmpresa(business);
								fatherPack.setPresentacion(emp.getPadre().getPresentacion());
							} else {
								fatherPack = packsRepository.crear(emp.getPadre(), empresa);
							}
							pack.setPadre(fatherPack);
						}

						if (emp.getPadre().getPadre() != null) {
							emp.getPadre().getPadre().setEmpresa(business);
							Optional<Empaque> optGrandFather = packsRepository
									.findByGtin("" + emp.getPadre().getPadre().getGtin());
							if (optGrandFather.isPresent() && !optGrandFather.get().getGtin().equals("")
									&& !(optGrandFather.get().getEmpresa().getId().equals(business.getId()))) {
								excelProduct.setHasErrors(true);
								excelProduct.setWasCreated(false);
								excelProduct.bddErrors += "Existe otro empaque con este gtin. ";
								pack.getPadre().setPadre(null);
							} else {
								if (optGrandFather.isPresent() && !optGrandFather.get().getGtin().equals("")
										&& optGrandFather.get().getEmpresa().getId() != business.getId()) {
									grandFatherPack = optGrandFather.get();
									grandFatherPack.copyForExcel(emp.getPadre().getPadre().getGtin(),
											"" + emp.getPadre().getPadre().getNivel(),
											emp.getPadre().getPadre().getUnidadMedida(),
											"" + emp.getPadre().getPadre().getAlto(),
											"" + emp.getPadre().getPadre().getAncho(),
											"" + emp.getPadre().getPadre().getProfundidad(),
											"" + emp.getPadre().getPadre().getPesoBruto(),
											"" + emp.getPadre().getPadre().getCantidad(),
											emp.getPadre().getPadre().getCpp(),
											emp.getPadre().getPadre().getClasificacion());
									grandFatherPack.setEmpresa(business);
									grandFatherPack.setPresentacion(emp.getPadre().getPadre().getPresentacion());
									this.packsRepository.save(grandFatherPack);
								} else {
									grandFatherPack = packsRepository.crear(emp.getPadre().getPadre(), business);
								}
								pack.getPadre().setPadre(grandFatherPack);
							}

						}
					}

					bddPacks.add(pack);
					pack = this.packsRepository.save(pack);
					product.getSempaques().add(pack.getSId());// si hay error no tiene que hacer esto
				}

			}
		}
		product.setEmpaques(bddPacks);
	}

	private void addPresentations(Producto product) {
		if (product.getPresentacion() != null && !product.getPresentacion().getNombre().trim().equals("")) {
			Presentacion presentation;
			Optional<Presentacion> optPresentation = presentationsRepository
					.findByName(product.getPresentacion().getNombre());
			if (optPresentation.isPresent())
				presentation = optPresentation.get();
			else
				presentation = presentationsRepository.crear(product.getPresentacion());
			product.setPresentacion(presentation);
		}
		if (product.getEmpaques() != null) {
			for (Empaque emp : product.getEmpaques()) {
				if (emp.getPadre() != null && emp.getPadre().getPresentacion() != null
						&& !emp.getPadre().getPresentacion().getNombre().trim().equals("")) {
					Presentacion fatherPresentation;
					Optional<Presentacion> optFatherPresentation = presentationsRepository
							.findByName(emp.getPadre().getPresentacion().getNombre());
					if (optFatherPresentation.isPresent())
						fatherPresentation = optFatherPresentation.get();
					else
						fatherPresentation = presentationsRepository.crear(emp.getPadre().getPresentacion());
					emp.getPadre().setPresentacion(fatherPresentation);
				}
				if (emp.getPresentacion() != null && !emp.getPresentacion().getNombre().trim().equals("")) {
					Presentacion packPresentation;
					Optional<Presentacion> optPackPresentation = presentationsRepository
							.findByName(emp.getPresentacion().getNombre());
					if (optPackPresentation.isPresent())
						packPresentation = optPackPresentation.get();
					else
						packPresentation = presentationsRepository.crear(emp.getPresentacion());
					emp.setPresentacion(packPresentation);
				}
			}
		}

	}

	@Override
	public Producto Modify(Producto p, UsuarioPrincipal ue) throws ServiceException {
		Producto product = this.productsRepository.findById(p.getId());

		if (product.getEmpresa().getId().equals(ue.getUsuarioEmpresa().getEmpresa().getId())) {
			if (!p.getDescripcion().equals(product.getDescripcion())) {
				product.setDescripcion(p.getDescripcion());
				descriptionIsNotRepeated(product);
			}
			product.setMarca(p.getMarca());
			product.setPaisOrigen(p.getPaisOrigen());
			product.setPesoBruto(p.getPesoBruto());
			product.setContenidoNeto(p.getContenidoNeto());
			if (p.getFoto() != null && !p.getFoto().trim().equals("")) {
				product.setFoto(p.getFoto());
			}

			product.setUnidadMedida(p.getUnidadMedida());
			product.setUnidadMedidaPesoBruto(p.getUnidadMedidaPesoBruto());
			product.setCpp(p.getCpp());
			product.setNivelMinimoVenta(p.getNivelMinimoVenta());
			product.setAlto(p.getAlto());
			product.setEsPromo(p.getEsPromo());
			product.setAncho(p.getAncho());
			product.setProfundidad(p.getProfundidad());
			if (p.getPresentacion() != null) {
				Optional<Presentacion> optPresentation = presentationsRepository
						.findByName(p.getPresentacion().getNombre());
				if (optPresentation.isPresent()) {
					product.setPresentacion(optPresentation.get());
				} else {
					Presentacion pres = new Presentacion();
					pres.setNombre(p.getPresentacion().getNombre());
					product.setPresentacion(pres);
					addPresentations(product);
				}
			}
			if (p.getCategoria() != null) {
				try {
					Categoria cat = p.getCategoria();
					Categoria catToAdd;
					Optional<Categoria> bddCat;
					if (cat.getPadre() != null) {
						bddCat = this.categoriesRepository.findByNameAndParent(cat.getNombre(),
								ue.getUsuarioEmpresa().getEmpresa(), cat.getPadre().getNombre());
					} else
						bddCat = this.categoriesRepository.findByName(cat.getNombre(),
								ue.getUsuarioEmpresa().getEmpresa());
					if (bddCat.isPresent()) {
						catToAdd = bddCat.get();
					} else {
						catToAdd = new Categoria();
						catToAdd.setDescripcion(cat.getDescripcion());
						catToAdd.setEmpresa(ue.getUsuarioEmpresa().getEmpresa());
						catToAdd.setSId(ue.getUsuarioEmpresa().getEmpresa().getSId());
						catToAdd.setNombre(cat.getNombre());
						catToAdd.setPosicion(cat.getPosicion());
					}
					catToAdd.setNivel((long) 2);
					if (cat.getPadre() != null) {
						Categoria fatherCatToAdd;
						Optional<Categoria> bddFatherCat = this.categoriesRepository
								.findByName(cat.getPadre().getNombre(), ue.getUsuarioEmpresa().getEmpresa());
						if (bddFatherCat.isPresent()) {
							fatherCatToAdd = bddFatherCat.get();
						} else {
							fatherCatToAdd = new Categoria();
							fatherCatToAdd.setDescripcion(cat.getDescripcion());
							fatherCatToAdd.setEmpresa(ue.getUsuarioEmpresa().getEmpresa());
							fatherCatToAdd.setSempresa(ue.getUsuarioEmpresa().getEmpresa().getSId());
							fatherCatToAdd.setNombre(cat.getPadre().getNombre());
							fatherCatToAdd.setPosicion(cat.getPadre().getPosicion());
						}
						fatherCatToAdd.setNivel((long) 1);
						catToAdd.checkForParentLoop(fatherCatToAdd);
						this.categoriesRepository.insert(fatherCatToAdd);
						product.setDivision(fatherCatToAdd.getNombre());
						catToAdd.setPadre(fatherCatToAdd);
					}
					this.categoriesRepository.insert(catToAdd);
					product.setLinea(catToAdd.getNombre());
					product.setCategoria(catToAdd);

					// addCategory(ue, product);
				} catch (ModelException ex) {
					throw new ServiceException("Error en la categoria del producto");
				}

			}

			product.setEmpaques(p.getEmpaques());
			addPacks(ue, product);

			if (p.getPallet() != null && !p.getPallet().getAlto().trim().equals("")) {
				Pallet existingPallet = p.getPallet();
				Pallet newPallet = new Pallet();
				newPallet.setAlto(existingPallet.getAlto());
				newPallet.setAncho(existingPallet.getAncho());
				newPallet.setProfundidad(existingPallet.getProfundidad());
				newPallet.setCajas(existingPallet.getCajas());
				newPallet.setCamadas(existingPallet.getCamadas());
				newPallet.setUnidadesVenta(existingPallet.getUnidadesVenta());
				product.setPallet(newPallet);
			}

			this.productsRepository.update(product);
			return product;
		} else
			throw new ServiceException("Solo puedes editar un grupo de la empresa activa");

	}

	private void descriptionIsNotRepeated(Producto p) throws ServiceException {
		List<Producto> productsWithDesc = this.productsRepository.findByKey("descripcion", p.getDescripcion());
		for (Producto product : productsWithDesc) {
			if (product.getId() != p.getId())
				throw new ServiceException("Esta ya tiene un producto con esta descripcion");
		}
	}

	private void addCategory(Company empresa, Producto product) {
		Categoria cat = product.getCategoria();
		Categoria father = null;
		if (cat.getPadre() != null) {
			Optional<Categoria> optFather = this.categoriesRepository.findByName(cat.getPadre().getNombre(), empresa);
			if (optFather.isPresent()) {
				father = optFather.get();
			} else
				father = this.categoriesRepository.crear(cat.getPadre(), empresa);
			cat.setPadre(father);
		}
		Optional<Categoria> optCat = this.categoriesRepository.findByName(cat.getNombre(), empresa);
		if (optCat.isPresent()
				&& ((optCat.get().getPadre() != null && optCat.get().getPadre().getNombre().equals(father.getNombre()))
				|| (father == null && optCat.get().getPadre() == null))) {
			cat = optCat.get();
		} else
			cat = this.categoriesRepository.crear(cat, empresa);
		product.setCategoria(cat);
		product.setLinea(cat.getNombre());
		product.setDivision(father.getNombre());
		product.setScategoria(cat.getSId());
	}

	@Override
	public Producto Insert(Producto p, UsuarioPrincipal user) throws ServiceException {
		try {
			p.setEmpresa(user.getUsuarioEmpresa().getEmpresa());
			checkIfProductExists(p);
			Producto product = new Producto();
			product.setDescripcion(p.getDescripcion());
			product.setCpp(p.getCpp());
			product.setGtin(p.getGtin());
			product.setPaisOrigen(p.getPaisOrigen());
			product.setMarca(p.getMarca());
			product.setContenidoNeto(p.getContenidoNeto());
			product.setPesoBruto(p.getPesoBruto());
			product.setEsPromo(p.getEsPromo());
			product.setUnidadMedida(p.getUnidadMedida());
			product.setUnidadMedidaPesoBruto(p.getUnidadMedidaPesoBruto());
			product.setAlto(p.getAlto());
			product.setAncho(p.getAncho());
			product.setProfundidad(p.getProfundidad());
			product.setFoto(p.getFoto());
			product.setNivelMinimoVenta(p.getNivelMinimoVenta());
			product.setEmpaques(p.getEmpaques());
			Optional<Presentacion> optPresentation = presentationsRepository
					.findByName(p.getPresentacion().getNombre());
			if (optPresentation.isPresent()) {
				product.setPresentacion(optPresentation.get());
			} else if (p.getPresentacion().getNombre() != null) {
				Presentacion pres = new Presentacion();
				pres.setNombre(p.getPresentacion().getNombre());
				product.setPresentacion(pres);
				addPresentations(product);
			}
			Categoria cat = p.getCategoria();
			cat.setNivel((long) 2);
			cat.getPadre().setNivel((long) 1);
			cat.checkForParentLoop(cat.getPadre());
			product.setCategoria(cat);
			addCategory(user.getUsuarioEmpresa().getEmpresa(), product);

			addPacks(user, product);

			product.setEmpresa(user.getUsuarioEmpresa().getEmpresa());
			product.setSempresa(user.getUsuarioEmpresa().getEmpresa().getSId());

			if (p.getPallet() != null && !p.getPallet().getAlto().trim().equals("")) {
				Pallet existingPallet = p.getPallet();
				Pallet newPallet = new Pallet();
				newPallet.setAlto(existingPallet.getAlto());
				newPallet.setAncho(existingPallet.getAncho());
				newPallet.setProfundidad(existingPallet.getProfundidad());
				newPallet.setCajas(existingPallet.getCajas());
				newPallet.setCamadas(existingPallet.getCamadas());
				newPallet.setUnidadesVenta(existingPallet.getUnidadesVenta());
				product.setPallet(newPallet);
			}
			this.productsRepository.save(product);

			return product;
		} catch (ModelException ex) {
			throw new ServiceException("Error al insertar Producto");
		}
	}

	@Override
	public List<Producto> GetAll(UsuarioEmpresa ue, MultivaluedMap<String, String> parametros) {
		// FIXME Revisar implementación de consulta a BD para evitar cargar todos los
		// productos
		Company empresa = ue.getEmpresa();
		Company bddEmp = this.businessesRepository.findById(empresa.getId());
		Set<Producto> toReturn = bddEmp.getProductosEmpresa();
		List<Producto> allProducts = new ArrayList<Producto>();
		allProducts.addAll(toReturn);
		allProducts.removeIf(p -> p.getEliminado() == true);
		return allProducts;
	}

	@Override
	public PaginadoResponse<List<Producto>> GetVisibleForBusiness(PaginadoRequest pr, String businessId,
                                                                  UsuarioEmpresa ue) throws ServiceException {
		try {
			return this.productsRepository.getVisibleForBussines(pr, businessId, ue);
		} catch (ModelException me) {
			throw new ServiceException(
					"Ocurrió un error al obtener el Productos visibles por la Empresa. Error: " + me.getMessage());
		} catch (Exception e) {
			throw new ServiceException(
					"Ocurrió un error al obtener el Productos visibles por la Empresa. Error: " + e.getMessage());
		}

	}

	@Override
	public Set<Producto> obtenerProductosVisiblesEmpresa(UsuarioEmpresa ue, String businessId) throws ServiceException {
		Company empresa = this.businessesRepository.findById(ue.getEmpresa().getId());
		Company proveedor = null;
		Optional<Company> bussiness = this.businessesRepository.findByKey("rut", businessId);
		proveedor = bussiness.orElse(this.businessesRepository.findById(businessId));
		if (proveedor != null) {
			Set<Producto> toReturn = new HashSet<Producto>();
			toReturn.addAll(this.obtenerProductosVisiblesPorEmpresa(empresa, proveedor));
			return toReturn;

		}
		throw new ServiceException("No existe este proveedor");
	}

	@Override
	public PaginadoResponse<List<Producto>> obtenerProductosVisiblesEmpresa(PaginadoRequest pr, UsuarioEmpresa ue,
			String businessId) throws ServiceException {
		try {
			return this.productsRepository.getVisibleByBussines(pr, businessId, ue);
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public PaginadoResponse<List<Producto>> obtenerProductosVisiblesParaEmpresaSeleccionada(PaginadoRequest pr,
			UsuarioEmpresa ue, String businessId) throws ServiceException {
		try {
			return this.productsRepository.getMyVisibleProductForSelectedBussines(pr, businessId, ue);
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public PaginadoResponse<List<Producto>> obtenerProductosEmpresa(PaginadoRequest pr, UsuarioEmpresa ue,
			String businessId) throws ServiceException {
		try {
			return this.productsRepository.obtenerProductosEmpresa(pr, businessId, ue);
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<String> obtenerProductosEmpresaMarcas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaMarcas(pr, businessId, empresa);
			List<String> marcas = new ArrayList<>();
			for (Producto producto : productos) {
				marcas.add(producto.getMarca());
			}
			return marcas;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<String> obtenerProductosEmpresaDivisiones(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaDivisiones(pr, businessId,
					empresa);
			List<String> divisiones = new ArrayList<>();
			for (Producto producto : productos) {
				divisiones.add(producto.getCategoria().getNombre());
			}
			return divisiones;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<String> obtenerProductosEmpresaLineas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaLineas(pr, businessId, empresa);
			List<String> lineas = new ArrayList<>();
			for (Producto producto : productos) {
				lineas.add(producto.getCategoria().getPadre().getNombre());
			}
			return lineas;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<String> obtenerProductosEmpresaProveedorMarcas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaProveedorMarcas(pr, businessId,
					empresa);
			List<String> marcas = new ArrayList<>();
			for (Producto producto : productos) {
				marcas.add(producto.getMarca());
			}
			return marcas;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<Set<String>> obtenerProductosEmpresaProveedorFiltros(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			return this.productsRepository.obtenerProductosVisiblesPorPorveedorFiltros(pr, businessId,
					empresa);
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<String> obtenerProductosEmpresaProveedorDivisiones(PaginadoRequest pr, UsuarioEmpresa ue,
			String businessId) throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaProveedorDivisiones(pr,
					businessId, empresa);
			List<String> divisiones = new ArrayList<>();
			for (Producto producto : productos) {
				divisiones.add(producto.getCategoria().getNombre());
			}
			return divisiones;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<String> obtenerProductosEmpresaProveedorLineas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaProveedorLineas(pr, businessId,
					empresa);
			List<String> lineas = new ArrayList<>();
			for (Producto producto : productos) {
				lineas.add(producto.getCategoria().getPadre().getNombre());
			}
			return lineas;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public String exportarExcel(PaginadoRequest pr, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		Company empresa = usuarioEmpresa.getEmpresa();
		try {			
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaParaExportar(pr, empresa);
			XSSFWorkbook xSSFWorkbook = ExcelUtility.exportProductList(productos, s3FileManager);
			String excelUrl = uploadErrorExcel(xSSFWorkbook, empresa.getRut(), usuarioEmpresa.getId(), true);
			return excelUrl;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ empresa.getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	private void InsertVisibilityActions(Producto newVisibilityProduct, Producto oldVisibilityProduct) {
		Set<Grupo> groupsToAdd = newVisibilityProduct.getGruposConVisibilidad();
		Set<Company> businessesFromGroupsToAdd = new HashSet<Company>(); // A
		for (Grupo g : groupsToAdd) {
			businessesFromGroupsToAdd.addAll(g.getEmpresas());
		}

		Set<Grupo> groupsThatAlreadyHad = oldVisibilityProduct.getGruposConVisibilidad();
		Set<Company> businessesFromGroupsThatAlreadyHad = new HashSet<Company>(); // B
		for (Grupo g : groupsThatAlreadyHad) {
			businessesFromGroupsThatAlreadyHad.addAll(g.getEmpresas());
		}

		Set<Company> businessesToAdd = newVisibilityProduct.getEmpresasConVisibilidad(); // C
		if (newVisibilityProduct.getEsPublico()) {
			businessesToAdd.addAll(this.businessesRepository.getAll());
		}
		if (newVisibilityProduct.getEsPrivado()) {
			businessesToAdd = new HashSet<Company>();
			businessesFromGroupsToAdd = new HashSet<Company>();
		}
		Set<Company> businessesThatAlreadyHad = oldVisibilityProduct.getEmpresasConVisibilidad(); // D
		if (oldVisibilityProduct.getEsPublico()) {
			businessesThatAlreadyHad.addAll(this.businessesRepository.getAll());
		}
		if (oldVisibilityProduct.getEsPrivado()) {
			businessesThatAlreadyHad = new HashSet<Company>();
			businessesFromGroupsThatAlreadyHad = new HashSet<Company>();
		}

		Set<Company> businessesThatNowHaveVisibility = new HashSet<Company>();
		businessesThatNowHaveVisibility.addAll(businessesToAdd);
		businessesThatNowHaveVisibility.addAll(businessesFromGroupsToAdd); // A + C

		Set<Company> businessesThatHadVisibility = new HashSet<Company>();
		businessesThatHadVisibility.addAll(businessesThatAlreadyHad);
		businessesThatHadVisibility.addAll(businessesFromGroupsThatAlreadyHad); // B + D

		// Final ahora tienen = (A + C) - (B + D)
		Set<Company> businessesThatWonVisibility = new HashSet<Company>();
		businessesThatWonVisibility.addAll(businessesThatNowHaveVisibility);
		businessesThatWonVisibility.removeAll(businessesThatHadVisibility);

		// for (Empresa emp : businessesThatWonVisibility) {
		// this.InsertProductAction(oldVisibilityProduct, emp, "POST");
		// }

		// Final dejan de tener = (B+D) - (A+C)
		Set<Company> businessesThatLostVisibility = new HashSet<Company>();
		businessesThatLostVisibility.addAll(businessesThatHadVisibility);
		businessesThatLostVisibility.removeAll(businessesThatNowHaveVisibility);

		/*
		 * for(int i = 0 ; i < businessesThatLostVisibility.size(); i++ ){
		 * businessesThatLostVisibility. }
		 */
		// for (Empresa emp : businessesThatLostVisibility) {
		// this.InsertProductAction(oldVisibilityProduct, emp, "DELETE");
		// }
	}

	@Override
	public void ChangeVisibility(Producto p, UsuarioEmpresa ue) throws ServiceException {

		this.businessesRepository.existeEmpresa(ue.getEmpresa().getId());
		Company e = this.businessesRepository.findById(ue.getEmpresa().getId());
		if (e.getValidado() && !e.getEliminado()) {
			Producto existingProduct = this.productsRepository.findById(p.getId());
			if (existingProduct != null) {
				if (p.getEsPrivado() || p.getEsPublico()) {
					existingProduct.setEsPublico(p.getEsPublico());
					existingProduct.setEsPrivado(p.getEsPrivado());
					this.productsRepository.update(existingProduct);
					this.InsertVisibilityActions(p, existingProduct);

				} else {
					existingProduct.setEsPrivado(p.getEsPrivado());
					existingProduct.setEsPublico(p.getEsPublico());
					this.InsertVisibilityActions(p, existingProduct);
					existingProduct.setGruposConVisibilidad(p.getGruposConVisibilidad());
					existingProduct.setEmpresasConVisibilidad(p.getEmpresasConVisibilidad());
					this.productsRepository.update(existingProduct);
				}

			} else
				throw new ServiceException("No existe este producto");
		} else {
			throw new ServiceException(
					"Antes de agregarle productos a una categoría un administrador debe validar esta empresa");
		}
	}

	@Override
	public void Delete(String id, UsuarioPrincipal user) throws ServiceException {
		Producto p = this.productsRepository.findById(id);
		if(p != null) {
			Company e = this.businessesRepository.findById(user.getUsuarioEmpresa().getEmpresa().getId());
			if (e.getValidado() && !e.getEliminado()) {
				if (p.getEmpresa().getId().equals(e.getId())) {

					Set<Grupo> groupsWithVisibility = p.getGruposConVisibilidad();
					Set<Company> businessesFromGroupsWithVisibility = new HashSet<Company>(); // B
					for (Grupo g : groupsWithVisibility) {
						businessesFromGroupsWithVisibility.addAll(g.getEmpresas());
					}
					p.getEmpresasConVisibilidad().addAll(businessesFromGroupsWithVisibility);
					if (p.getEsPrivado()) {
						p.setEmpresasConVisibilidad(new HashSet<Company>());
					}
					if (p.getEsPublico()) {
						Set<Company> allBusinesses = new HashSet<Company>();
						allBusinesses.addAll(this.businessesRepository.getAll());
						p.setEmpresasConVisibilidad(allBusinesses);
					}

					// for (Empresa lostVisibility : p.getEmpresasConVisibilidad()) {
					// this.InsertProductAction(p, lostVisibility, "DELETE");
					// }

					p.eliminar();
					this.productsRepository.update(p);
				} else
					throw new ServiceException("Solo se pueden eliminar productos de la propia empresa");
			} else {
				throw new ServiceException("Antes de eliminar un producto un administrador debe validar esta empresa");
			}
		}
	}

	public List<String> obtenerTodosLosProductos(UsuarioPrincipal user) {
		return this.productsRepository.obtenerProductosEmpresaArray(user.getUsuarioEmpresa());
	}

	public void deleteProductsArray(EliminarProductos eliminarProductos, UsuarioPrincipal user) throws ServiceException {
		if (eliminarProductos.getEliminarTodos()) {
			List<String> todosLosProductos = this.productsRepository
					.obtenerProductosEmpresaArray(user.getUsuarioEmpresa());
			for (String productoId : todosLosProductos) {
				boolean eliminar = true;
				if (eliminarProductos.getProductosNoEliminar() != null) {
					for (String noElminar : eliminarProductos.getProductosNoEliminar()) {
						if (productoId.equals(noElminar)) {
							eliminar = false;
						}
					}
				}
				if (eliminar) {
					Delete(productoId, user);
				}
			}
		} else {
			if (eliminarProductos.getProductosEliminar() != null) {
				for (String productoId : eliminarProductos.getProductosEliminar()) {
					Delete(productoId, user);
				}
			}
		}
	}

	@Override
	public void ChangeMassiveVisibility(VisibilityRequest visibilityRequest, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException {
        for (Producto p : visibilityRequest.getProductos()) {
            Producto existingProduct = this.productsRepository.findById(p.getId());
            if (existingProduct != null) {
                existingProduct.setEsPrivado(visibilityRequest.getEsPrivado());
                existingProduct.setEsPublico(visibilityRequest.getEsPublico());

                if(visibilityRequest.getEsPublico() || (!visibilityRequest.getEsPublico() && !visibilityRequest.getEsPrivado()) || !visibilityRequest.getIsMasive()){
					existingProduct.getEmpresasConVisibilidad().clear();
					existingProduct.getSempresasConVisibilidad().clear();
					existingProduct.getGruposConVisibilidad().clear();
					existingProduct.getSgruposConVisibilidad().clear();
				}

                if (visibilityRequest.getEsPrivado()) {
                    if (visibilityRequest.getEmpresas() != null) {
						for (Company visibilityRequestEmpresa: visibilityRequest.getEmpresas()) {
							Company empresa = empresasDAO.findById(visibilityRequestEmpresa.getId());
							existingProduct.getEmpresasConVisibilidad().add(empresa);
							existingProduct.getSempresasConVisibilidad().add(empresa.getId());
						}
                    }

                    if (visibilityRequest.getGrupos() != null) {
                      	for (Grupo visibilityRequestGrupo: visibilityRequest.getGrupos()) {
							Grupo grupo = groupsRepository.findById(visibilityRequestGrupo.getId());
							existingProduct.getGruposConVisibilidad().add(grupo);
							existingProduct.getSgruposConVisibilidad().add(grupo.getId());
						}
                    }
                }
                this.productsRepository.update(existingProduct);
			}
		}
	}

	@Override
	public void ChangeBusinessVisibility(VisibilityRequest visibilityRequest, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException {
		if (visibilityRequest.getEmpresas() != null && visibilityRequest.getEmpresas().size() != 0) {
			String empresaId = visibilityRequest.getEmpresas().get(0).getId();
			Company emp = this.businessesRepository.findById(empresaId);
			if (emp != null) {
				this.productsRepository.eliminarTodaVisibilidadEmpresasProveedor(empresaId,
						usuarioEmpresa.getEmpresa().getId());
				if (visibilityRequest.getEmpresas() != null && visibilityRequest.getEmpresas().size() > 0
						&& visibilityRequest.getProductos().size() > 0) {
					this.productsRepository.actualizarEmpresasConVisibilidad(visibilityRequest.getProductos(),
							empresaId);
				}

				if (visibilityRequest.getGrupos() != null && visibilityRequest.getGrupos().size() > 0
						&& visibilityRequest.getProductos().size() > 0) {
					this.productsRepository.actualizarGruposConVisibilidad(visibilityRequest.getProductos(),
							visibilityRequest.getGrupos().get(0).getId());
				}
			} else
				throw new ServiceException("No existe empresa con este id");
		} else
			throw new ServiceException("Debes enviar una empresa");

	}

	/*
	 * /* Esta acción devuelve una lista de acciones en formato JSON que hacen
	 * referencia a los productos que cambiaron junto con el tipo de cambio desde la
	 * última fecha de consulta. Se puede enviar como parámetro URI el id de la
	 * empresa a la cuál se le quieren actualizar los productos, si no se manda el
	 * API devuelve las acciones para todas las empresas. A su vez también se puede
	 * enviar el id del proveedor del cual se quieren consultar los productos, si no
	 * se envía ninguno el API devuelve las acciones de todos los proveedores.
	 */

	@Override
	public List<ProductoAccion> GetNotAcknowledgedActions(String businessId, String providerId, UsuarioEmpresa ue) {
		if (businessId.equals("0") && !ue.getUsuario().esAdministradorSistema())
			businessId = ue.getEmpresa().getId();
		Optional<Company> bussiness = this.businessesRepository.findByKey("rut", "" + businessId);
		if (bussiness.isPresent())
			businessId = bussiness.get().getId();
		Optional<Company> provider = this.businessesRepository.findByKey("rut", "" + providerId);
		if (provider.isPresent())
			providerId = provider.get().getId();
		if (businessId.equals("0") && providerId.equals("0")) {
			return this.productActionsRepository.getAllNotAcknowledged();
		} else if (businessId.equals("0")) {
			return this.productActionsRepository.getAllNotAcknowledgedFromProvider(providerId);
		} else if (providerId.equals("0")) {
			return this.productActionsRepository.getAllNotAcknowledgedFromBusiness(businessId);
		} else {
			return this.productActionsRepository.getAllNotAcknowledgedFromBusinessAndProvider(businessId, providerId);
		}
	}

	@Override
	public void SetAcknowledge(AcknowledgeRequest acknowledgeRequest, UsuarioEmpresa ue) throws ServiceException {
		Optional<Company> providerOptional = this.businessesRepository.findByKey("rut",
				"" + acknowledgeRequest.getIdEmpresa());
		Company provider = providerOptional
				.orElse(this.businessesRepository.findById(acknowledgeRequest.getIdEmpresa()));
		if (provider != null) {
			for (ProductoAccion toAcknowledge : acknowledgeRequest.getAcciones()) {
				ProductoAccion existingProductAction = this.productActionsRepository.findById(toAcknowledge.getId());
				if (existingProductAction != null) {
					Usuario usuario = ue.getUsuario();
					if (!usuario.esAdministradorSistema()
							&& !(existingProductAction.getEmpresaId() == ue.getEmpresa().getId())) {
						continue;
					}
					existingProductAction.setFechaRecibido();
					this.productActionsRepository.update(existingProductAction);
				}
			}
		} else {
			throw new ServiceException("No existe este proveedor");
		}

	}

	@Override
	public Producto GetSingleProduct(UsuarioEmpresa usuarioEmpresa, String businessId, String productId)
			throws ServiceException {
		Company bddEmp = this.businessesRepository.findById(usuarioEmpresa.getEmpresa().getId());
		Optional<Company> prov = this.businessesRepository.findByKey("rut", "" + businessId);
		Company bddProv = prov.orElse(this.businessesRepository.findById(businessId));
		if (bddEmp == null)
			throw new ServiceException("No existe una Empresa asociada a este Usuario con el Id solicitado");
		if (bddProv == null)
			throw new ServiceException("No existe Empresa Proveedora con el Id enviado por parámetros");
		Producto bddProd = this.productsRepository.findById(productId);
		if (bddProd == null)
			throw new ServiceException("No existe un Producto con el Id pasado parámetros");
		/*
		 * if (bddProd.getEmpresa().getId() != bddProv.getId()) throw new
		 * ServiceException("El Producto con el Id pasado por parámetros no pertenece al Proveedor"
		 * );
		 */

		Optional<Producto> productoOptional = this.esVisiblePorEmpresa(bddEmp, bddProd);
		if (productoOptional.isPresent()) {
			return productoOptional.get();
		}

		throw new ServiceException("La Empresa no tiene visibilidad asignada sobre este producto");
	}

	/**
	 * Método para determinar si un {@link Producto} es visible por una
	 * {@link Company}
	 * 
	 * @param empresa
	 * @param producto
	 * @return Producto pasado por parámetros si la {@link Company} tiene
	 *         visibilidad, de lo contrario devuelve null
	 */
	public Optional<Producto> esVisiblePorEmpresa(Company empresa, Producto producto) {
		return this.productsRepository.obtenerProductoVisiblePorEmpresa(empresa.getId(), producto.getId());
	}

	@Override
	public Set<Producto> GetAllVisible(UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		Company bddEmp = this.businessesRepository.findById(usuarioEmpresa.getEmpresa().getId());
		if (bddEmp != null) {
			Set<Producto> toReturn = new HashSet<Producto>();
			List<Company> allProviders = this.businessesRepository.getAll();
			for (Company business : allProviders) {
				toReturn.addAll(this.obtenerProductosVisiblesPorEmpresa(bddEmp, business));
			}
			return toReturn;
		} else
			throw new ServiceException("No existe esta empresa");
	}

	/**
	 * Devuelve el listado de los {@link Producto} que están visibles para la
	 * Empresa Cliente del Proveedor pasado por parámetros
	 * 
	 * @param empresa   : Empresa Cliente
	 * @param proveedor : Empresa Proveedor
	 * @return Set de Productos Visibles
	 */
	@Transactional
	Set<Producto> obtenerProductosVisiblesPorEmpresa(Company empresa, Company proveedor) {

		Set<Producto> productosVisibles = new HashSet<Producto>();
		if (proveedor.getId() != empresa.getId()) {
			Set<Producto> productos = this.productsRepository.getAll(proveedor);
			for (Producto p : productos) {
				boolean added = false;
				if (p.isEsPublico()) {
					productosVisibles.add(p);
					continue;
				}
				if (p.getGruposConVisibilidad() != null) {
					for (Grupo group : p.getGruposConVisibilidad()) {
						//Grupo bddGroup = this.groupsRepository.findById(group.getId());
						Set<Company> groupBusinesses = group.getEmpresas();
						if (groupBusinesses.contains(empresa)) {
							added = true;
							p.setVisibilidadPorGrupo(true);
							productosVisibles.add(p);
							break;
						}
					}
					if (added)
						continue;
				}

				if (p.getEmpresasConVisibilidad() != null) {
					if (p.getEmpresasConVisibilidad().contains(empresa)) {
						p.setVisibilidadPorGrupo(false);
						productosVisibles.add(p);
					}
				}
			}
		}
		return productosVisibles;
	}

	@Override
	public Set<Producto> GetVisibleByBussinesOnSaleList(ListaDeVenta lv, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException {
		return this.productsRepository.getVisibleByBussinesOnSaleList(usuarioEmpresa.getEmpresa(), lv);
	}

	@Override
	public Set<Producto> GetVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException {
		ListaDeVenta listaDeVenta = this.listasDeVentaDAO.findById(listaDeVentaId);
		return this.productsRepository.getVisibleByBussinesOnSaleList(usuarioEmpresa.getEmpresa(), listaDeVenta);
	}

	@Override
	public PaginadoResponse<Set<Company>> obtenerEmpresasConVisibilidad(PaginadoRequest pr, String idProducto,
																		UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		Producto producto = this.productsRepository.findById(idProducto);
		if (producto == null)
			throw new ServiceException("No existe un Producto con el Id pasado parámetros");
		if (!producto.getEmpresa().getId().equals(usuarioEmpresa.getEmpresa().getId())) {
			Set<Company> l = new HashSet<>();
			l.add(usuarioEmpresa.getEmpresa());
			return new PaginadoResponse<Set<Company>>(pr.getPagina(), 1L, 1L, l);
		}
        Set<Company> empresasConVisibilidad = producto.getEmpresasConVisibilidad();
        PaginadoResponse<Set<Company>> paginadoResponse = new PaginadoResponse<>();
        paginadoResponse.setElementos(empresasConVisibilidad);
        return paginadoResponse;

	}

	@Override
	public PaginadoResponse<List<Grupo>> obtenerGruposConVisibilidad(PaginadoRequest pr, String idProducto,
			UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		// TODO Iteracion 1: Implementar que devuelva todos cuando la Empresa es dueña
		// del Producto
		Producto producto = this.productsRepository.findById(idProducto);
		if (producto == null)
			throw new ServiceException("No existe un Producto con el Id pasado parámetros");
		try {
			return this.productsRepository.obtenerGruposConVisibilidadEnProducto(pr, producto.getId(),
					usuarioEmpresa.getEmpresa().getId());
		} catch (ModelException e) {
			throw new ServiceException("Error obteniendo Empresas con visibilidad." + e.getMessage());
		}
	}

	@Override
	public String actualizarURLImagenes(String bucket, final Company empresa) {
		Param amazonKey = paramsDAO.findByNombre("s3_key");
		Param keyId = paramsDAO.findByNombre("s3_id");
		AWSCredentials credentials = new BasicAWSCredentials(keyId.getValor(), amazonKey.getValor());
		@SuppressWarnings("deprecation")
		AmazonS3 s3Client = new AmazonS3Client(credentials);

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket)
				.withPrefix("assets/images/Sebamar/").withDelimiter("/");
		ObjectListing objects = s3Client.listObjects(listObjectsRequest);
		objects.getObjectSummaries()

				.stream()
				.map(e -> new AbstractMap.SimpleEntry<String, String>(e.getKey(),
						e.getKey().replace("assets/images/Sebamar/", "")))
				.filter(e -> e.getValue().toLowerCase().contains(".jpg")).forEach(e -> {
					String noExtension = e.getValue().replaceFirst(".jpg", "");
					try {
						long val = Long.parseLong(noExtension);

						Optional<Producto> optionalProduct = this.productsRepository.findByGtin("" + val,
								empresa.getId());
						if (optionalProduct.isPresent()) {
							Producto existingProduct = optionalProduct.get();
							existingProduct.setFoto(e.getKey());
							existingProduct.setGtin("" + val);
							this.productsRepository.update(existingProduct);
						}
					} catch (Exception ex) {
					}
				});

		return "ok";
	}

	@Override
	public void crearClientesExcelRN(List<ExcelRNCliente> listaClientes, UsuarioEmpresa ue) throws ServiceException {
		HashMap<String, List<String>> grupoEmpresas = new HashMap<String, List<String>>();
		for (ExcelRNCliente excelRNCliente : listaClientes) {

			for (String grupo : excelRNCliente.listadoGrupos()) {
				if (!grupoEmpresas.containsKey(grupo)) {
					grupoEmpresas.put(grupo, new ArrayList<String>());
				}
				List<String> listaEmpresas = grupoEmpresas.get(grupo);
				listaEmpresas.add("" + excelRNCliente.getEanCode());
				grupoEmpresas.put(grupo, listaEmpresas);
			}
		}

	}

	@Override
	public List<ExcelProduct> crearProductosRN(List<ExcelRNProducto> listaProductos, UsuarioPrincipal usuario)
			throws ServiceException {
		// Inserto los que no son marcados para eliminar
		List<ExcelProduct> productosAInsertar = listaProductos.stream()
				.map(producto -> {
					try {
						return this.mapearProductoRNAProducto(producto, usuario.getUsuarioEmpresa().getEmpresa());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return null;
				})
				.collect(Collectors.toList());

		this.InsertExcel(productosAInsertar, usuario, true, true, true);

		return productosAInsertar;
	}

	@Override
	public List<ExcelProduct> crearProductosRN(List<ExcelRNProducto> listaProductos, UsuarioPrincipal usuario,
			Company empresa) throws ServiceException {
		// Inserto los que no son marcados para eliminar
		List<ExcelProduct> productosAInsertar = listaProductos.stream()
				.map(producto -> {
					try {
						return this.mapearProductoRNAProducto(producto, empresa);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());

		this.InsertExcelRN(productosAInsertar, empresa, true, false, false);

		return productosAInsertar;
	}

	@Override
	public List<ExcelCliente> crearClientesRN(List<ExcelRNCliente> listaClientes, UsuarioPrincipal usuario, Company empresa)
			throws ServiceException {
		// Inserto los que no son marcados para eliminar
		List<ExcelCliente> clientesAInsertar = listaClientes.stream()
				.map(cliente -> this.mapearClienteRNAEmpresa(cliente, empresa)).collect(Collectors.toList());

		this.InsertClienteExcel(clientesAInsertar, usuario, true, true, true);

		return clientesAInsertar;
	}

	private ExcelProduct mapearProductoRNAProducto(ExcelRNProducto producto, Company empresa) throws ParseException {

        if(producto.getSuspendido() != null){
            if(producto.getSuspendido().getSuspendidoHasta() != null && !producto.getSuspendido().getSuspendidoHasta().equals("")){
                String desde = producto.getSuspendido().getSuspendidoHasta();
                String ddmm = desde.substring(0,desde.lastIndexOf("/"));
                String yy = desde.substring(desde.lastIndexOf("/") + 1);
                if(Integer.valueOf(yy) > 30)
                    producto.getSuspendido().setSuspendidoHasta(ddmm + "/30");
            }

        }
		ExcelProduct excelProducto = new ExcelProduct();
		excelProducto.setProduct(Cloner.MAPPER.clone(producto));
		excelProducto.setWasCreated(true);
		excelProducto.getProduct().setEmpresa(empresa);
		// Establezco la Empresa para evitar errores al insertar la Categoria. Tanto la
		// Hija como la Padre
		if (excelProducto.getProduct().getCategoria() != null)
			excelProducto.getProduct().getCategoria().setEmpresa(empresa);
		if (excelProducto.getProduct().getCategoria().getPadre() != null)
			excelProducto.getProduct().getCategoria().getPadre().setEmpresa(empresa);
		if (producto.getVisiblePor().getVisiblePorGrupos().trim().isEmpty()) {
			excelProducto.getProduct().setEsPublico(true);
		} else {
			producto.listadoGrupos().stream()

					.map(grupoNombre -> {
						Grupo grupo = this.groupsRepository.obtenerGrupo(grupoNombre, empresa.getId());
						if (grupo == null) {
							grupo = new Grupo(grupoNombre, "");
							grupo.setDescripcion("Grupo Sincronizado desde Rondanet");
							grupo.setEmpresa(empresa);
							this.groupsRepository.insert(grupo);
						}
						Producto prod = excelProducto.getProduct();
						prod.getGruposConVisibilidad().add(grupo);
						excelProducto.setProduct(prod);

						return grupo;
					}).peek(grupo -> excelProducto.getProduct().getGruposConVisibilidad().add(grupo))
					.collect(Collectors.toList());
		}

		Empaque emp = new Empaque();
		emp.setGtin(producto.getUnidadesDeDespacho().getGtin14());
		emp.setCantidad(new BigDecimal(producto.getUnidadesDeDespacho().getUnidadesPorCaja()));
		emp.setNivel(1);
		emp.setEmpresa(empresa);
		emp.setClasificacion("Caja");
		Set<Empaque> sEmpaques = new HashSet<Empaque>();
		sEmpaques.add(emp);
		Producto prod = excelProducto.getProduct();
		prod.setEmpaques(sEmpaques);
		if (prod.getGruposConVisibilidad() != null && !prod.getGruposConVisibilidad().isEmpty()) {
			prod.setEsPrivado(true);
		}
		excelProducto.setProduct(prod);

		return excelProducto;
	}

	private ExcelCliente mapearClienteRNAEmpresa(ExcelRNCliente cliente, Company empresa) {

		ExcelCliente excelCliente = new ExcelCliente();
		excelCliente.setEmpresa(Cloner.MAPPER.clone(cliente));
		excelCliente.setWasCreated(true);
		if (!cliente.getVisiblePor().isEmpty()) {
			cliente.listadoGrupos().stream()

					.map(grupoNombre -> {
						Grupo grupo = this.groupsRepository.obtenerGrupo(grupoNombre, empresa.getId());
						Company emp = excelCliente.getEmpresa();
						Optional<Company> emp2 = this.businessesRepository.findByKey("gln", emp.getGln());
						if (grupo == null) {
							grupo = new Grupo(grupoNombre, "");
							grupo.setDescripcion("Grupo Sincronizado desde Rondanet");
							grupo.setEmpresa(empresa);
							if(emp2.isPresent()){
								grupo.getEmpresas().add(emp2.get());
							}
							this.groupsRepository.insert(grupo);
						}else{
							if(emp2.isPresent()){
								grupo.getEmpresas().add(emp2.get());
							}
							this.groupsRepository.update(grupo);
						}
						excelCliente.grupos.add(grupo.getId());
						excelCliente.setEmpresa(emp);

						return grupo;
					}).collect(Collectors.toList());
		}

		return excelCliente;
	}

	@Override
	public Workbook getErrorExcel(List<ExcelProduct> productList) {
		return ExcelUtility.getErrorExcel(productList, s3FileManager);
	}

	@Override
	public String uploadErrorExcel(Workbook wb, Long empresaRut, String idUsuarioEmpresa, boolean isProducts) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			wb.write(byteArrayOutputStream);
			byteArrayOutputStream.close();
		} catch (IOException e) {
			// String message = e.getMessage();
		}

		ByteArrayInputStream bi = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

		// String clientRegion = "us-east-2";
		String bucketName = configuration.getConfiguracionDespliegue().getBucket();
		Param key = paramsDAO.findByNombre("s3_key");
		Param keyId = paramsDAO.findByNombre("s3_id");
		AWSCredentials credentials = new BasicAWSCredentials(keyId.getValor(), key.getValor());
		@SuppressWarnings("deprecation")
		AmazonS3 s3Client = new AmazonS3Client(credentials);

		ObjectMetadata objectMetaData = new ObjectMetadata();
		objectMetaData.setContentType("application/vnd.ms-excel");
		objectMetaData.setContentLength(byteArrayOutputStream.toByteArray().length);
		String nombreArchivo = "";
		String location = "";
		if (!isProducts) {
			nombreArchivo = "errores-" + idUsuarioEmpresa;
			location = empresaRut + "/errors/excelUploads/" + nombreArchivo + ".xls";
		}else{
			nombreArchivo = "productosExcel-" + idUsuarioEmpresa;
			location = empresaRut + "/productos/descargas/" + nombreArchivo + ".xlsx";			
		}
		try {				
			s3Client.deleteObject(new DeleteObjectRequest(bucketName, location));
		} catch (AmazonServiceException e) {			
			e.printStackTrace();
		} catch (SdkClientException e) {			
			e.printStackTrace();
		}		
		s3Client.putObject(new PutObjectRequest(bucketName, location, bi, objectMetaData)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		return bucketName + "/" + location;
	}

	@Override
	public boolean visibilidadPrivadaEnListaVenta(List<ExcelRNProducto> listaProductos, String cpp){
		ExcelRNProducto excelRNProductoToCheck= new ExcelRNProducto();
		for (ExcelRNProducto excelRNProducto: listaProductos) {
			excelRNProductoToCheck = excelRNProducto;
			if(excelRNProducto.getCpp().equals(cpp)){
				break;
			}
		}
		if(excelRNProductoToCheck != null && excelRNProductoToCheck.getVisiblePor().getVisiblePorGrupos().equals("")){
			return false;
		}
		return true;
	}

}
