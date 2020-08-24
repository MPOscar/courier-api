package courier.uy.core.db;

import org.springframework.stereotype.Component;

/*import rondanet.catalogo.core.entity.GrupoYListaDeVenta;
import rondanet.catalogo.core.entity.ProductoYListaDeVenta;
import rondanet.catalogo.core.repository.IProductoYListaDeVentaRepository;*/

@Component
public class ProductoYListaDeVentaDAO {

    /*@Autowired
    IProductoYListaDeVentaRepository productoYListaDeVentaRepository;

    @PersistenceContext
    private EntityManager em;

    public ProductoYListaDeVenta insert(ProductoYListaDeVenta e) {
        e.setFechaCreacion();
        e.setFechaEdicion();
        return productoYListaDeVentaRepository.save(e);
    }

    public ProductoYListaDeVenta update(ProductoYListaDeVenta pls) {
        pls.setFechaEdicion();
        return productoYListaDeVentaRepository.save(pls);
    }

    public ProductoYListaDeVenta delete(ProductoYListaDeVenta e) {
        return delete(e);
    }

    public void insertForSaleList(ProductoYListaDeVenta productoYListaDeVenta) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ProductoYListaDeVenta> criteria = builder.createQuery(ProductoYListaDeVenta.class);
        Root<ProductoYListaDeVenta> i = criteria.from(ProductoYListaDeVenta.class);

        criteria.select(i).where(builder.equal(i.get("listaDeVentaId"), productoYListaDeVenta.getListaDeVentaId()),
                builder.equal(i.get("productoId"), productoYListaDeVenta.getProducto()));

        TypedQuery<ProductoYListaDeVenta> query = em.createQuery(criteria);
        List<ProductoYListaDeVenta> listaProductos = query.getResultList();
        if (listaProductos != null && !listaProductos.isEmpty()) {
            ProductoYListaDeVenta updateLV = listaProductos.get(0);
            updateLV.noEliminado();
            updateLV.setEsPublico(productoYListaDeVenta.getEsPublico());
            updateLV.setEsPrivado(productoYListaDeVenta.getEsPrivado());
            update(updateLV);
        } else {
            insert(productoYListaDeVenta);
        }
    }

    @Transactional
    public void deleteAllForSaleList(Long idListaDeVenta) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<ProductoYListaDeVenta> criteria = builder.createCriteriaUpdate(ProductoYListaDeVenta.class);
        Root<ProductoYListaDeVenta> i = criteria.from(ProductoYListaDeVenta.class);
        Predicate criterioSeleccion = builder.and(builder.equal(i.get("eliminado"), false),
                builder.equal(i.get("listaDeVentaId"), idListaDeVenta));
        criteria.where(criterioSeleccion);
        criteria.set("eliminado", true);
        em.createQuery(criteria).executeUpdate();
    }

    public List<ProductoYListaDeVenta> getAllProductsByListaDeVentaId(long listaDeVentaId){
        List<ProductoYListaDeVenta> productosListaDeVenta = productoYListaDeVentaRepository.findAllByListaDeVentaId(listaDeVentaId);
        return productosListaDeVenta;
    }*/
}