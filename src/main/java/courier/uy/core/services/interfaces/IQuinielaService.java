package courier.uy.core.services.interfaces;

import courier.uy.core.entity.Quiniela;

import java.util.Set;

public interface IQuinielaService {
	public Quiniela saveTirada(String tirada);
	public Set<Integer> getJugada();
	public Set<Integer> getNumerosSinSalir(String fecha);
}
