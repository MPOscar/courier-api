package courier.uy.core.db;

import org.springframework.stereotype.Component;
/*import rondanet.catalogo.core.entity.ListaDeVentaVisibilidadEmpresa;
import rondanet.catalogo.core.repository.IListaDeVentaVisibilidadEmpresaRepository;*/

@Component
public class ListaDeVentaVisibilidadEmpresaDAO {

    /*@PersistenceContext
    private EntityManager em;

    @Autowired
    IListaDeVentaVisibilidadEmpresaRepository listaDeVentaVisibilidadEmpresaRepository;

    public ListaDeVentaVisibilidadEmpresa insert(ListaDeVentaVisibilidadEmpresa listaDeVentaVisibilidadEmpresa) {
        listaDeVentaVisibilidadEmpresa.setFechaCreacion();
        listaDeVentaVisibilidadEmpresa.setFechaEdicion();
        return listaDeVentaVisibilidadEmpresaRepository.save(listaDeVentaVisibilidadEmpresa);
    }

    public ListaDeVentaVisibilidadEmpresa update(ListaDeVentaVisibilidadEmpresa listaDeVentaVisibilidadEmpresa) {
        listaDeVentaVisibilidadEmpresa.setFechaEdicion();
        listaDeVentaVisibilidadEmpresaRepository.save(listaDeVentaVisibilidadEmpresa);
        return listaDeVentaVisibilidadEmpresa;
    }

    public ListaDeVentaVisibilidadEmpresa delete(ListaDeVentaVisibilidadEmpresa listaDeVentaVisibilidadEmpresaRepository) {
        return delete(listaDeVentaVisibilidadEmpresaRepository);
    }

    @Transactional
    public void deleteAll(long listaDeVentaId, long productoId){
        String queryEliminarVisivilidad = "DELETE FROM lista_de_venta_visibilidad_empresa WHERE lista_de_venta_id = " + listaDeVentaId + " AND producto_id="
                + productoId;
        em.createNativeQuery(queryEliminarVisivilidad).executeUpdate();
    }

    public List<ListaDeVentaVisibilidadEmpresa> getAllByProductoIdAndListaDeVentaId(long listaDeVentaId, long productoId){
        List<ListaDeVentaVisibilidadEmpresa> listaDeVentaVisibilidadEmpresas = this.listaDeVentaVisibilidadEmpresaRepository.findAllByListaDeVentaIdAndProductoId(listaDeVentaId, productoId);
        return listaDeVentaVisibilidadEmpresas;
    }*/

}