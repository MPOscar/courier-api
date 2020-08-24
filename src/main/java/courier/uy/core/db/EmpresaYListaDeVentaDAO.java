package courier.uy.core.db;

import org.springframework.stereotype.Component;

/*import rondanet.catalogo.core.entity.Empresa;
import rondanet.catalogo.core.entity.EmpresaYListaDeVenta;
import rondanet.catalogo.core.repository.IEmpresaYListaDeVentaRepository;*/

@Component
public class EmpresaYListaDeVentaDAO {

    /*@PersistenceContext
    private EntityManager em;

    @Autowired
    IEmpresaYListaDeVentaRepository empresaYListaDeVentaRepository;

    public EmpresaYListaDeVenta insert(EmpresaYListaDeVenta e) {
        e.setFechaCreacion();
        e.setFechaEdicion();
        return empresaYListaDeVentaRepository.save(e);
    }

    public EmpresaYListaDeVenta update(EmpresaYListaDeVenta els) {
        els.setFechaEdicion();
        // els.setEliminado(false);
        return empresaYListaDeVentaRepository.save(els);
    }

    public EmpresaYListaDeVenta delete(EmpresaYListaDeVenta e) {
        return delete(e);
    }

    public void insertForSaleList(EmpresaYListaDeVenta empresaYListaDeVenta) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EmpresaYListaDeVenta> criteria = builder.createQuery(EmpresaYListaDeVenta.class);
        Root<EmpresaYListaDeVenta> i = criteria.from(EmpresaYListaDeVenta.class);

        criteria.select(i).where(builder.equal(i.get("listaDeVentaId"), empresaYListaDeVenta.getListaDeVentaId()),
                builder.equal(i.get("empresaId"), empresaYListaDeVenta.getEmpresa()));

        TypedQuery<EmpresaYListaDeVenta> query = em.createQuery(criteria);
        List<EmpresaYListaDeVenta> listaEmpresas = query.getResultList();
        if (listaEmpresas != null && !listaEmpresas.isEmpty()) {
            EmpresaYListaDeVenta updateLV = listaEmpresas.get(0);
            updateLV.noEliminado();
            update(updateLV);
        } else {
            insert(empresaYListaDeVenta);
        }
    }

    @Transactional
    public void deleteAllForSaleList(Long idListaDeVenta) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<EmpresaYListaDeVenta> criteria = builder.createCriteriaUpdate(EmpresaYListaDeVenta.class);
        Root<EmpresaYListaDeVenta> i = criteria.from(EmpresaYListaDeVenta.class);
        Predicate criterioSeleccion = builder.and(Utils.predicadoEliminado(builder, i),
                builder.equal(i.get("listaDeVentaId"), idListaDeVenta));
        criteria.where(criterioSeleccion);
        criteria.set("eliminado", true);
        em.createQuery(criteria).executeUpdate();
    }

    public List<EmpresaYListaDeVenta> getAllEmpresasByListaDeVentaId(long listaDeVentaId){
        List<EmpresaYListaDeVenta> empresasYListaDeVenta = empresaYListaDeVentaRepository.findAllByListaDeVentaId(listaDeVentaId);
        return empresasYListaDeVenta;
    }*/

}