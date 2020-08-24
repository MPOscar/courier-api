package courier.uy.core.repository;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IUsuarioEmpresaRepository extends MongoRepository<UsuarioEmpresa, String> {

    public UsuarioEmpresa findFirstByOldId(long oldId);

    public Optional<UsuarioEmpresa> findById(String id);

    public List<UsuarioEmpresa> findByUsuarioAndEmpresa(Usuario usuario, Company empresa);

    public List<UsuarioEmpresa> findByUsuario(Usuario usuario);

    public List<UsuarioEmpresa> findAllByEmpresa(Company empresa);

    public List<UsuarioEmpresa> findByUsuarioAndActivoAndValidado(Usuario usuario, boolean activo, boolean validado);

    public List<UsuarioEmpresa> findByUsuarioAndEmpresaAndActivoAndValidado(Usuario usuario, Company empresa, boolean activo, boolean validado);
}
