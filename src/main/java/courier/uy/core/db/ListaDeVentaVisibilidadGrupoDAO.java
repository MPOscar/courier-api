package courier.uy.core.db;

import courier.uy.core.entity.ListaDeVentaVisibilidad;
import courier.uy.core.repository.IListaDeVentaVisibilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class ListaDeVentaVisibilidadGrupoDAO {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    IListaDeVentaVisibilidadRepository listaDeVentaVisibilidadGrupoRepository;

    public ListaDeVentaVisibilidad insert(ListaDeVentaVisibilidad listaDeVentaVisibilidadGrupo) {
        listaDeVentaVisibilidadGrupo.setFechaCreacion();
        listaDeVentaVisibilidadGrupo.setFechaEdicion();
        return listaDeVentaVisibilidadGrupoRepository.save(listaDeVentaVisibilidadGrupo);
    }

    public ListaDeVentaVisibilidad update(ListaDeVentaVisibilidad listaDeVentaVisibilidadGrupo) {
        listaDeVentaVisibilidadGrupo.setFechaEdicion();
        listaDeVentaVisibilidadGrupoRepository.save(listaDeVentaVisibilidadGrupo);
        return listaDeVentaVisibilidadGrupo;
    }

    public ListaDeVentaVisibilidad delete(ListaDeVentaVisibilidad listaDeVentaVisibilidadGrupoRepository) {
        return delete(listaDeVentaVisibilidadGrupoRepository);
    }

    @Transactional
    public void deleteAll(long listaDeVentaId, long productoId){
        String queryEliminarVisivilidad = "DELETE FROM lista_de_venta_visibilidad_grupo WHERE lista_de_venta_id = " + listaDeVentaId + " AND producto_id="
                + productoId;
        em.createNativeQuery(queryEliminarVisivilidad).executeUpdate();
    }
}