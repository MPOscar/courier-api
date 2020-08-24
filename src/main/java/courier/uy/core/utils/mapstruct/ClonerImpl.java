package courier.uy.core.utils.mapstruct;

import courier.uy.core.entity.Presentacion;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.Grupo;
import courier.uy.core.resources.dto.PalletResponse;
import courier.uy.core.resources.dto.ProductoAccionesResponse;
import courier.uy.core.utils.poiji.ExcelRNCliente;
import courier.uy.core.utils.poiji.ExcelRNProducto;
import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Pallet;
import courier.uy.core.utils.poiji.ExcelRNCampoPresentacion;
import courier.uy.core.utils.poiji.ExcelRNCampoUnidadDespacho;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Generated;
import javax.ws.rs.core.Link;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2020-05-13T19:40:37-0700",
        comments = "version: 1.3.1.Final, compiler: Eclipse JDT (IDE) 3.21.50.v20200318-0850_BETA_JAVA14, environment: Java 1.8.0_241 (Oracle Corporation)"
)
public class ClonerImpl implements Cloner {

    @Override
    public ProductoAccionesResponse clone(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoAccionesResponse productoAccionesResponse = new ProductoAccionesResponse();

        //Set<Link> set = producto.getLinks();
        Set<Link> set = new HashSet<>();
        if ( set != null ) {
            productoAccionesResponse.links( new HashSet<Link>( set ) );
        }
        productoAccionesResponse.id( producto.getId() );
        productoAccionesResponse.fechaCreacion(producto.getFechaCreacion());
        productoAccionesResponse.fechaEdicion(producto.getFechaEdicion());
        productoAccionesResponse.eliminado( producto.getEliminado() );
        productoAccionesResponse.cpp( producto.getCpp() );
        productoAccionesResponse.pallet( producto.getPallet() );
        productoAccionesResponse.gtin( producto.getGtin() );
        productoAccionesResponse.gtinPresentacion( producto.getGtinPresentacion() );
        productoAccionesResponse.descripcion( producto.getDescripcion() );
        productoAccionesResponse.mercadoObjetivo( producto.getMercadoObjetivo() );
        productoAccionesResponse.unidadMedidaPesoBruto( producto.getUnidadMedidaPesoBruto() );
        productoAccionesResponse.nivelMinimoVenta( producto.getNivelMinimoVenta() );
        productoAccionesResponse.marca( producto.getMarca() );
        productoAccionesResponse.contenidoNeto( producto.getContenidoNeto() );
        productoAccionesResponse.unidadMedida( producto.getUnidadMedida() );
        productoAccionesResponse.paisOrigen( producto.getPaisOrigen() );
        productoAccionesResponse.foto( producto.getFoto() );
        productoAccionesResponse.alto( producto.getAlto() );
        productoAccionesResponse.ancho( producto.getAncho() );
        productoAccionesResponse.profundidad( producto.getProfundidad() );
        productoAccionesResponse.pesoBruto( producto.getPesoBruto() );
        productoAccionesResponse.esPromo( producto.getEsPromo() );
        productoAccionesResponse.esPrivado( producto.getEsPrivado() );
        productoAccionesResponse.setEsPublico( producto.getEsPublico() );
        productoAccionesResponse.suspendidoDesde( producto.getSuspendidoDesde() );
        productoAccionesResponse.suspendidoHasta( producto.getSuspendidoHasta() );
        productoAccionesResponse.empresa( producto.getEmpresa() );
        productoAccionesResponse.categoria( producto.getCategoria() );
        productoAccionesResponse.presentacion( producto.getPresentacion() );
        Set<Empaque> set1 = producto.getEmpaques();
        if ( set1 != null ) {
            productoAccionesResponse.empaques( new HashSet<Empaque>( set1 ) );
        }
        Set<Company> set2 = producto.getEmpresasConVisibilidad();
        if ( set2 != null ) {
            productoAccionesResponse.setEmpresasConVisibilidad( new HashSet<Company>( set2 ) );
        }
        Set<Grupo> set3 = producto.getGruposConVisibilidad();
        if ( set3 != null ) {
            productoAccionesResponse.setGruposConVisibilidad( new HashSet<Grupo>( set3 ) );
        }

        return productoAccionesResponse;
    }

    @Override
    public Producto exactClone(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        Producto producto1 = new Producto();

        producto1.setFechaCreacion( producto.getFechaCreacion() );
        producto1.setFechaEdicion( producto.getFechaEdicion() );
        //Set<Link> set = producto.getLinks();
        Set<Link> set = new HashSet<>();
        if ( set != null ) {
            //producto1.setLinks( new HashSet<Link>( set ) );
        }
        producto1.setPallet( producto.getPallet() );
        producto1.setMercadoObjetivo( producto.getMercadoObjetivo() );
        producto1.setUnidadMedidaPesoBruto( producto.getUnidadMedidaPesoBruto() );
        producto1.setNivelMinimoVenta( producto.getNivelMinimoVenta() );
        producto1.setEsPromo( producto.getEsPromo() );
        producto1.setEsPrivado( producto.getEsPrivado() );
        producto1.setVisibilidadPorGrupo( producto.getVisibilidadPorGrupo() );
        producto1.setEsPublico( producto.getEsPublico() );
        Set<Company> set1 = producto.getEmpresasConVisibilidad();
        if ( set1 != null ) {
            producto1.setEmpresasConVisibilidad( new HashSet<Company>( set1 ) );
        }
        Set<Grupo> set2 = producto.getGruposConVisibilidad();
        if ( set2 != null ) {
            producto1.setGruposConVisibilidad( new HashSet<Grupo>( set2 ) );
        }
        producto1.setId( producto.getId() );
        producto1.setCpp( producto.getCpp() );
        producto1.setGtin( producto.getGtin() );
        producto1.setGtinPresentacion( producto.getGtinPresentacion() );
        producto1.setDescripcion( producto.getDescripcion() );
        producto1.setMarca( producto.getMarca() );
        producto1.setContenidoNeto( producto.getContenidoNeto() );
        producto1.setUnidadMedida( producto.getUnidadMedida() );
        producto1.setPaisOrigen( producto.getPaisOrigen() );
        producto1.setFoto( producto.getFoto() );
        producto1.setAlto( producto.getAlto() );
        producto1.setAncho( producto.getAncho() );
        producto1.setProfundidad( producto.getProfundidad() );
        producto1.setPesoBruto( producto.getPesoBruto() );
        producto1.setEmpresa( producto.getEmpresa() );
        producto1.setCategoria( producto.getCategoria() );
        producto1.setPresentacion( producto.getPresentacion() );
        Set<Empaque> set3 = producto.getEmpaques();
        if ( set3 != null ) {
            producto1.setEmpaques( new HashSet<Empaque>( set3 ) );
        }
        producto1.setSuspendidoDesde( producto.getSuspendidoDesde() );
        producto1.setSuspendidoHasta( producto.getSuspendidoHasta() );

        return producto1;
    }

    @Override
    public List<ProductoAccionesResponse> clone(List<Producto> producto) {
        if ( producto == null ) {
            return null;
        }

        List<ProductoAccionesResponse> list = new ArrayList<ProductoAccionesResponse>( producto.size() );
        for ( Producto producto1 : producto ) {
            list.add( clone( producto1 ) );
        }

        return list;
    }

    @Override
    public Set<ProductoAccionesResponse> clone(Set<Producto> producto) {
        if ( producto == null ) {
            return null;
        }

        Set<ProductoAccionesResponse> set = new HashSet<ProductoAccionesResponse>( Math.max( (int) ( producto.size() / .75f ) + 1, 16 ) );
        for ( Producto producto1 : producto ) {
            set.add( clone( producto1 ) );
        }

        return set;
    }

    @Override
    public PalletResponse clone(Pallet pallet) {
        if ( pallet == null ) {
            return null;
        }

        PalletResponse palletResponse = new PalletResponse();

        palletResponse.setAlto( pallet.getAlto() );
        palletResponse.setAncho( pallet.getAncho() );
        palletResponse.setCajas( pallet.getCajas() );
        palletResponse.setCamadas( pallet.getCamadas() );
        palletResponse.setFechaCreacion( pallet.getFechaCreacion() );
        palletResponse.setFechaEdicion( pallet.getFechaEdicion() );
        palletResponse.setId( pallet.getId() );
        //Set<Link> set = pallet.getLinks();
        Set<Link> set = new HashSet<>();
        if ( set != null ) {
            palletResponse.setLinks( new HashSet<Link>( set ) );
        }
        palletResponse.setProfundidad( pallet.getProfundidad() );
        palletResponse.unidadesVenta( pallet.getUnidadesVenta() );

        return palletResponse;
    }

    @Override
    public Producto clone(ExcelRNProducto productoRN) throws ParseException {
        if ( productoRN == null ) {
            return null;
        }

        Producto producto = new Producto();

        producto.setPallet( excelRNCampoUnidadDespachoToPallet( productoRN.getUnidadesDeDespacho() ) );
        producto.setDescripcion( productoRN.getDescripcion() );
        producto.setGtin( productoRN.getGtin13() );
        String unidXcaja = productoRNUnidadesDeDespachoUnidXcaja( productoRN );
        if ( unidXcaja != null ) {
            producto.setNivelMinimoVenta( Integer.parseInt( unidXcaja ) );
        }
        producto.setUnidadMedidaPesoBruto( productoRNPresentacionUnidad( productoRN ) );
        producto.setPaisOrigen( productoRN.getPais() );
        if ( productoRN.getEsPromo() != null ) {
            producto.setEsPromo( Boolean.parseBoolean( productoRN.getEsPromo() ) );
        }
        producto.setCpp( productoRN.getCpp() );
        producto.setMarca( productoRN.getMarca() );
        producto.setPresentacion( excelRNCampoPresentacionToPresentacion( productoRN.getPresentacion() ) );

        Producto target = mapearProductoRNAProducto( productoRN, producto );
        if ( target != null ) {
            return target;
        }

        return producto;
    }

    @Override
    public Company clone(ExcelRNCliente clienteRN) {
        if ( clienteRN == null ) {
            return null;
        }

        Company empresa = new Company();

        if ( clienteRN.getEanCode() != null ) {
            empresa.setGln( clienteRN.getEanCode().toString() );
        }
        empresa.setNombre( clienteRN.getNombre() );

        return empresa;
    }

    protected Pallet excelRNCampoUnidadDespachoToPallet(ExcelRNCampoUnidadDespacho excelRNCampoUnidadDespacho) {
        if ( excelRNCampoUnidadDespacho == null ) {
            return null;
        }

        Pallet pallet = new Pallet();

        pallet.setUnidadesVenta( excelRNCampoUnidadDespacho.getUnidXcaja() );
        pallet.setCajas( excelRNCampoUnidadDespacho.getUnidXcaja() );
        pallet.setCamadas( excelRNCampoUnidadDespacho.getCamadasPorPallet() );

        return pallet;
    }

    private String productoRNUnidadesDeDespachoUnidXcaja(ExcelRNProducto excelRNProducto) {
        if ( excelRNProducto == null ) {
            return null;
        }
        ExcelRNCampoUnidadDespacho unidadesDeDespacho = excelRNProducto.getUnidadesDeDespacho();
        if ( unidadesDeDespacho == null ) {
            return null;
        }
        String unidXcaja = unidadesDeDespacho.getUnidXcaja();
        if ( unidXcaja == null ) {
            return null;
        }
        return unidXcaja;
    }

    private String productoRNPresentacionUnidad(ExcelRNProducto excelRNProducto) {
        if ( excelRNProducto == null ) {
            return null;
        }
        ExcelRNCampoPresentacion presentacion = excelRNProducto.getPresentacion();
        if ( presentacion == null ) {
            return null;
        }
        String unidad = presentacion.getUnidad();
        if ( unidad == null ) {
            return null;
        }
        return unidad;
    }

    protected Presentacion excelRNCampoPresentacionToPresentacion(ExcelRNCampoPresentacion excelRNCampoPresentacion) {
        if ( excelRNCampoPresentacion == null ) {
            return null;
        }

        Presentacion presentacion = new Presentacion();

        return presentacion;
    }
}
