package courier.uy.core.services.implementations;

import java.util.*;

import courier.uy.core.db.CategoriasDAO;
import courier.uy.core.db.EmpresasDAO;
import courier.uy.core.db.ProductosDAO;
import courier.uy.core.db.UsuariosDAO;
import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;
import courier.uy.core.services.interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private final CategoriasDAO categoriesRepository;
    @Autowired
    private final ProductosDAO productsRepository;
    @Autowired
    private final UsuariosDAO usersRepository;
    @Autowired
    private final EmpresasDAO businessesRepository;

    public CategoryService(CategoriasDAO categoriesDAO, ProductosDAO productsDAO, EmpresasDAO businessesDAO,
            UsuariosDAO usersDAO) {
        this.categoriesRepository = categoriesDAO;
        this.productsRepository = productsDAO;
        this.usersRepository = usersDAO;
        this.businessesRepository = businessesDAO;
    }

    @Override
    public void Create(Categoria c, UsuarioEmpresa ue) throws ServiceException {
        this.usersRepository.existeUsuario(ue.getUsuario());
        this.businessesRepository.existeEmpresa(ue.getEmpresa().getId());
        this.nameIsNotRepeated(c, ue.getEmpresa());
        Company e = this.businessesRepository.findById(ue.getEmpresa().getId());
        if (e.getValidado() && !e.getEliminado()) {
            if (c.getPadre() != null) {
                if (c.getPadre().getEmpresa().getId() != e.getId()) {
                    c.setPadre(null);
                }
            }
            c.setEmpresa(ue.getEmpresa());
            this.categoriesRepository.insert(c);
            for (Producto p : c.getProductos()) {
                Producto existingProduct = this.productsRepository.findById(p.getId());
                if (existingProduct != null) {
                    existingProduct.setCategoria(c);
                    this.productsRepository.update(existingProduct);
                }

            }
        } else {
            throw new ServiceException("Antes de crear una categoría un administrador debe validar esta empresa");
        }

    }

    @Override
    public Categoria GetCategoryById(String id) throws ServiceException {
        return this.GetCategoryById(id, null);
    }

    @Override
    public Categoria GetCategoryById(String id, UsuarioEmpresa ue) throws ServiceException {
        Optional<Categoria> categoria = this.categoriesRepository.findById(id);
        return categoria.orElseThrow(() -> new ServiceException("No existe la Categoria solicitada"));
    }

    @Override
    public void Delete(Categoria c, UsuarioEmpresa ue) throws ServiceException {
        Optional<Categoria> categoryOptional = this.categoriesRepository.findById(c.getId());
        Categoria category = null;
        if (!categoryOptional.isPresent()) {
            throw new ServiceException("No existe la Categoria");
        }
        category = categoryOptional.get();
        if (category.getEmpresa().getId() == ue.getEmpresa().getId()) {
            category.eliminar();
            // Pongo category = null a todos los prod que la tengan?
            this.categoriesRepository.update(category);
        } else {
            throw new ServiceException("Solo puedes editar una categoria de la empresa activa");
        }
    }

    @Override
    public void ChangeProducts(Categoria c, UsuarioEmpresa ue) throws ServiceException {
        this.usersRepository.existeUsuario(ue.getUsuario());
        this.businessesRepository.existeEmpresa(ue.getEmpresa().getId());
        Company e = this.businessesRepository.findById(ue.getEmpresa().getId());
        if (e.getValidado() && !e.getEliminado()) {
            List<Producto> productsInCategory = this.getAllProductsFromCategory(c, e);
            for (Producto existingProduct : productsInCategory) {
                existingProduct.setCategoria(null);
                this.productsRepository.update(existingProduct);
            }
            Set<Producto> prods = new HashSet<>();
            for (Producto p : c.getProductos()) {
                Producto existingProduct = this.productsRepository.findById(p.getId());
                if (existingProduct != null) {
                    existingProduct.setCategoria(c);
                    this.productsRepository.update(existingProduct);
                    prods.add(existingProduct);
                }
            }
            c.setProductos(prods);
        } else {
            throw new ServiceException(
                    "Antes de agregarle productos a una categoría un administrador debe validar esta empresa");
        }
    }

    private List<Producto> getAllProductsFromCategory(Categoria c, Company e) {
        Set<Producto> allProd = this.productsRepository.getAll(e);
        List<Producto> allProducts = new ArrayList<Producto>(allProd);
        List<Producto> filteredProducts = new ArrayList<Producto>();
        for (Producto product : allProducts) {
            if (product.getCategoria().getId() == c.getId()) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    @Override
    public PaginadoResponse<List<Categoria>> GetAllFromBusiness(PaginadoRequest pr, UsuarioEmpresa ue)
            throws ServiceException {

        try {
            return this.categoriesRepository.obtenerCategoriasEmpresa(pr, ue.getEmpresa().getId());
        } catch (ModelException e) {
            throw new ServiceException(
                    "Ocurrió un error al obtener el listado de Categorías pertenecientes a la Empresa "
                            + ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

    @Override
    public PaginadoResponse<List<Categoria>> GetAllFromProvider(PaginadoRequest pr, String id) throws ServiceException {
        Optional<Company> optional = Optional.ofNullable(this.businessesRepository.findById(id));
        try {
            optional.orElseThrow(() -> new Exception("No existe la Empresa con Id: " + id));
            return this.categoriesRepository.obtenerCategoriasEmpresa(pr, id);
        } catch (ModelException e) {
            throw new ServiceException(
                    "Ocurrió un error al obtener el listado de Categorías pertenecientes a la Empresa "
                            + optional.get().getNombre() + ". Error: " + e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

    private void nameIsNotRepeated(Categoria c, Company e) throws ServiceException {
        List<Categoria> categoriesWithName = this.categoriesRepository.findByKey("nombre", c.getNombre());
        for (Categoria category : categoriesWithName) {
            if (category.getId() != c.getId() && category.getEmpresa().getId() == e.getId()) {
                throw new ServiceException("Esta empresa ya tiene una categoría con este nombre");
            }
        }
    }

}
