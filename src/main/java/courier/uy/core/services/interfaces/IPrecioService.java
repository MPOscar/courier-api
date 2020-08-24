package courier.uy.core.services.interfaces;

import courier.uy.core.entity.Precio;
import courier.uy.core.exceptions.ServiceException;

import java.io.InputStream;
import java.util.List;

public interface IPrecioService {
    public void actualizarPrecios(InputStream inputStream) throws ServiceException;
    public List<Precio> GetPrecioByCppAndGln(String prodcutoCpp, String glnListaVenta);
}
