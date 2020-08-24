package courier.uy.core.db;

import org.springframework.stereotype.Component;

/*import rondanet.catalogo.core.entity.EmpresaYListaDeVenta;
import rondanet.catalogo.core.entity.GrupoYListaDeVenta;
import rondanet.catalogo.core.repository.IGrupoYListaDeVentaRepository;*/

@Component
public class GrupoYListaDeVentaDAO {

   /* @PersistenceContext
    private EntityManager em;

    @Autowired
    IGrupoYListaDeVentaRepository grupoYListaDeVentaRepository;

    public GrupoYListaDeVenta insert(GrupoYListaDeVenta grupoYListaDeVenta) {
        grupoYListaDeVenta.setFechaCreacion();
        grupoYListaDeVenta.setFechaEdicion();
        return grupoYListaDeVentaRepository.save(grupoYListaDeVenta);
    }

    public GrupoYListaDeVenta update(GrupoYListaDeVenta grupoYListaDeVenta) {
        grupoYListaDeVenta.setFechaEdicion();
        grupoYListaDeVentaRepository.save(grupoYListaDeVenta);
        return grupoYListaDeVenta;
    }

    public GrupoYListaDeVenta delete(GrupoYListaDeVenta grupoYListaDeVenta) {
        return delete(grupoYListaDeVenta);
    }

    public void insertForSaleList(GrupoYListaDeVenta grupoYListaDeVenta) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<GrupoYListaDeVenta> criteria = builder.createQuery(GrupoYListaDeVenta.class);
        Root<GrupoYListaDeVenta> i = criteria.from(GrupoYListaDeVenta.class);

        criteria.select(i).where(builder.equal(i.get("listaDeVentaId"), grupoYListaDeVenta.getListaDeVentaId()),
                builder.equal(i.get("grupoId"), grupoYListaDeVenta.getGrupoId()));

        TypedQuery<GrupoYListaDeVenta> query = em.createQuery(criteria);
        List<GrupoYListaDeVenta> listaGrupos = query.getResultList();
        if (listaGrupos != null && !listaGrupos.isEmpty()) {
            GrupoYListaDeVenta updateLV = listaGrupos.get(0);
            updateLV.noEliminado();
            update(updateLV);
        } else {
            insert(grupoYListaDeVenta);
        }
    }

    public void deleteAllForSaleList(Long idListaDeVenta) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<GrupoYListaDeVenta> criteria = builder.createCriteriaUpdate(GrupoYListaDeVenta.class);
        Root<GrupoYListaDeVenta> i = criteria.from(GrupoYListaDeVenta.class);
        Predicate criterioSeleccion = builder.and(Utils.predicadoEliminado(builder, i),
                builder.equal(i.get("listaDeVentaId"), idListaDeVenta));
        criteria.where(criterioSeleccion);
        criteria.set("eliminado", true);
        em.createQuery(criteria).executeUpdate();
    }

    public List<GrupoYListaDeVenta> getAllGruposByListaDeVentaId(long listaDeVentaId){
        List<GrupoYListaDeVenta> gruposYListaDeVenta = grupoYListaDeVentaRepository.findAllByListaDeVentaId(listaDeVentaId);
        return gruposYListaDeVenta;
    }
*/
}