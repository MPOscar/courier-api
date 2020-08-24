package courier.uy.core.services.implementations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import courier.uy.core.services.interfaces.IGroupService;
import courier.uy.core.db.EmpresasDAO;
import courier.uy.core.db.GruposDAO;
import courier.uy.core.db.UsuariosDAO;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import org.apache.commons.lang3.NotImplementedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class GroupsService implements IGroupService {
	@Autowired
	private GruposDAO groupRepository;
	@Autowired
	private UsuariosDAO userRepository;
	@Autowired
	private EmpresasDAO businessRepository;

	public GroupsService(GruposDAO groupsDAO, UsuariosDAO usuariosDAO, EmpresasDAO businessDAO) {
		this.groupRepository = groupsDAO;
		this.userRepository = usuariosDAO;
		this.businessRepository = businessDAO;
	}

	@Override
	public Grupo Create(Grupo g, UsuarioEmpresa ue) throws ServiceException {
		this.userRepository.existeUsuario(ue.getUsuario());
		this.businessRepository.existeEmpresa(ue.getEmpresa().getId());
		Company e = this.businessRepository.findById(ue.getEmpresa().getId());
		g.setEmpresa(ue.getEmpresa());
		this.nameIsNotRepeated(g);
		if (e.getValidado() && !e.getEliminado()) {

			this.groupRepository.insert(g);
			if (g.getEmpresas() != null) {
				Grupo gr = this.groupRepository.findById(g.getId());
				Set<Company> groupBusinesses = new HashSet<Company>();
				for (Company b : g.getEmpresas()) {
					this.businessRepository.existeEmpresa(b.getId());
					Company business = this.businessRepository.findById(b.getId());
					groupBusinesses.add(business);
				}
				gr.setEmpresas(null);
				gr.setEmpresas(groupBusinesses);
				this.groupRepository.update(gr);
			}
			return g;

		} else {
			throw new ServiceException("Antes de crear un grupo un administrador debe validar esta empresa");
		}

	}

	@Override
	public Grupo Modify(Grupo g, UsuarioEmpresa ue) throws ServiceException {
		Grupo group = this.groupRepository.findById(g.getId());
		if (group.getEmpresa().getId() == ue.getEmpresa().getId()) {
			if (!g.getNombre().equals(group.getNombre())) {
				group.setNombre(g.getNombre());
				nameIsNotRepeated(group);
			}
			group.setDescripcion(g.getDescripcion());
			Set<Company> groupBusinesses = new HashSet<Company>();
			for (Company b : g.getEmpresas()) {
				this.businessRepository.existeEmpresa(b.getId());
				Company business = this.businessRepository.findById(b.getId());
				groupBusinesses.add(business);
			}
			group.setEmpresas(null);
			group.setEmpresas(groupBusinesses);
			this.groupRepository.update(group);
			return group;
		} else
			throw new ServiceException("Solo puedes editar un grupo de la empresa activa");

	}

	@Override
	public void Delete(Grupo g, UsuarioEmpresa ue) throws ServiceException {
		Grupo group = this.groupRepository.findById(g.getId());
		if (group.getEmpresa().getId() == ue.getEmpresa().getId()) {
			group.setEmpresas(null);
			group.eliminar();
			this.groupRepository.update(group);
		} else
			throw new ServiceException("Solo puedes editar un grupo de la empresa activa");
	}

	@Override
	public Grupo AddBusinesses(Grupo g, UsuarioEmpresa ue, List<Company> empresas) throws ServiceException {
		throw new NotImplementedException("no");
	}

	@Override
	public Grupo RemoveBusinesses(Grupo g, UsuarioEmpresa ue, List<Company> empresas) throws ServiceException {
		throw new NotImplementedException("no");
	}

	private void nameIsNotRepeated(Grupo g) throws ServiceException {
		List<Grupo> groupsWithName = this.groupRepository.findByKey("nombre", g.getNombre());
		for (Grupo group : groupsWithName) {
			if (group.getId() != g.getId() && group.getEmpresa().getId() == g.getEmpresa().getId())
				throw new ServiceException("Esta empresa ya tiene un grupo con este nombre");
		}
	}

	@Override
	public Grupo GetGroupById(String id) throws ServiceException {
		Grupo grupo = this.groupRepository.findById(id);
		return grupo;
	}

	@Transactional
	@Override
	public Set<Grupo> GetAllFromProvider(UsuarioPrincipal usuario) throws ServiceException, Exception {
		UsuarioEmpresa ue = usuario.getUsuarioEmpresa();
		if (ue != null) {
			Company e = ue.getEmpresa();
			if (e != null) {
				Company bddEmp = this.businessRepository.findById(e.getId());
				Set<Grupo> grupos = bddEmp.getGrupos();
				return grupos;
			} else {
				throw new ServiceException("No se encuentra esta empresa");
			}
		} else {
			throw new ServiceException("Este usuario no tiene acceso para esta empresa");
		}
	}
}
