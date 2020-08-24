package courier.uy.core.services.interfaces;

import java.util.List;

import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.entity.Wishlist;
import courier.uy.core.exceptions.ServiceException;
import org.apache.poi.ss.usermodel.Workbook;

public interface IWishlistService {
	void SetStrategy(String formatBusinessId) throws ServiceException;
	Workbook GetMyProductsWithFormat(String myBusinessId) throws ServiceException;
	Wishlist AddProductToWishlist(String productId, UsuarioEmpresa usuarioEmpresa) throws ServiceException;
	List<Producto> GetAllProductsFromProviderInWishlist(String providerId, UsuarioEmpresa usuarioEmpresa) throws ServiceException;
	void RemoveFromWishlist(String productId, UsuarioEmpresa usuarioEmpresa) throws ServiceException;
	void ClearWishlist(String providerId, UsuarioEmpresa usuarioEmpresa) throws ServiceException;
}
