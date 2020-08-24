package courier.uy.core.services.implementations;

import java.util.ArrayList;
import java.util.List;

import courier.uy.core.db.EmpresasDAO;
import courier.uy.core.db.ProductosDAO;
import courier.uy.core.db.WishlistDAO;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.entity.Wishlist;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.services.interfaces.IWishlistService;
import org.apache.poi.ss.usermodel.Workbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import courier.uy.core.excel.formats.ExportStrategies;
import courier.uy.core.excel.formats.IExcelFormat;
import courier.uy.core.excel.formats.TataLogistics;
import courier.uy.core.excel.formats.TataVolumetry;
import courier.uy.core.excel.formats.TiendaInglesa;

@Service
public class WishlistService implements IWishlistService {

	@Autowired
	private EmpresasDAO businessRepository;
	@Autowired
	private WishlistDAO wishlistRepository;
	@Autowired
	private ProductosDAO productsRepository;

	private IExcelFormat excelFormat;

	public WishlistService(WishlistDAO wishlistDAO, EmpresasDAO businessDAO, ProductosDAO productsDAO) {
		this.wishlistRepository = wishlistDAO;
		this.businessRepository = businessDAO;
		this.productsRepository = productsDAO;
	}

	@Override
	public Wishlist AddProductToWishlist(String productId, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		if (usuarioEmpresa.getEmpresa() != null) {
			Company business = usuarioEmpresa.getEmpresa();
			Producto product = this.productsRepository.findById(productId);
			if (product != null) {
				Wishlist wishlistExits = this.wishlistRepository.findByProductAndBusiness(business.getId(), productId);
				if (wishlistExits == null) {
					Wishlist wishlist = new Wishlist();
					wishlist.setEnWishlist(true);
					wishlist.setEmpresa(business);
					wishlist.setSempresa(business.getId());
					wishlist.getProductos().add(product);
					wishlist.getSproductos().add(product.getId());
					wishlist.setFechaCreacion();
					return this.wishlistRepository.save(wishlist);
				}else{
					wishlistExits.getProductos().add(product);
					wishlistExits.getSproductos().add(product.getId());
					return this.wishlistRepository.save(wishlistExits);
				}
			} else
				throw new ServiceException("Este producto no existe");

		} else
			throw new ServiceException("Esta empresa no existe");
	}

	@Override
	public List<Producto> GetAllProductsFromProviderInWishlist(String providerId, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException {
		List<Producto> productos = new ArrayList<>();
		if (usuarioEmpresa.getEmpresa() != null) {
			Company business = usuarioEmpresa.getEmpresa();
			Company provider = this.businessRepository.findById(providerId);
			if (provider != null) {
				Wishlist wishlist = this.wishlistRepository.findByProductBusiness(business.getId());
				productos.addAll(wishlist.getProductos());
			} else
				throw new ServiceException("Este proveedor no existe");
		} else
			throw new ServiceException("Esta empresa no existe");
		return productos;
	}

	@Override
	public void RemoveFromWishlist(String productId, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		if (usuarioEmpresa.getEmpresa() != null) {
			Company business = usuarioEmpresa.getEmpresa();
			Producto product = this.productsRepository.findById(productId);
			if (product != null) {
				Wishlist wishlist = this.wishlistRepository.findByProductAndBusiness(business.getId(), product.getId());
				if (wishlist != null) {
					wishlist.getProductos().remove(product);
					wishlist.getSproductos().remove(productId);
					this.wishlistRepository.save(wishlist);
				} else
					throw new ServiceException("Esta empresa no tiene este producto para exportar");
			} else
				throw new ServiceException("Este producto no existe");

		} else
			throw new ServiceException("Esta empresa no existe");
	}

	@Override
	public void ClearWishlist(String providerId, UsuarioEmpresa usuarioEmpresa) throws ServiceException {
		if (usuarioEmpresa.getEmpresa() != null) {
			Company business = usuarioEmpresa.getEmpresa();
			Company provider = this.businessRepository.findById(providerId);
			if (provider != null) {
				List<Wishlist> wishlist = this.wishlistRepository.findByKey("idEmpresa", "" + business.getId());
				for (Wishlist w : wishlist) {
					if (w.getEnWishlist()) {
						/*Producto product = this.productsRepository.findById(w.getIdProducto());
						if (product.getEmpresa() != null) {
							if (product.getEmpresa().getId() == Long.parseLong("" + providerId)) {
								w.setEnWishlist(false);
								this.wishlistRepository.update(w);
							}
						}*/
					}
				}
			} else
				throw new ServiceException("Este proveedor no existe");
		} else
			throw new ServiceException("Esta empresa no existe");
	}

	@Override
	public void SetStrategy(String formatBusinessId) throws ServiceException {
		switch (formatBusinessId) {
			case ExportStrategies.TATA_LOGISTICS:
				this.excelFormat = new TataLogistics();
				break;
			case ExportStrategies.TATA_VOLUMETRY:
				this.excelFormat = new TataVolumetry();
				break;
			case ExportStrategies.TIENDA_INGLESA:
				this.excelFormat = new TiendaInglesa();
				break;
			default:
				throw new ServiceException("Esta empresa no tiene un formato específico");
		}

	}

	@Override
	public Workbook GetMyProductsWithFormat(String myBusinessId) throws ServiceException {
		List<Producto> myProducts = new ArrayList<Producto>();
		Company myBusiness = this.businessRepository.findById(myBusinessId);
		myProducts.addAll(this.productsRepository.getAll(myBusiness));
		if (this.excelFormat != null)
			return this.excelFormat.GetWorkbook(myProducts);
		throw new ServiceException("No se pudo elegir el formato");
	}

}
