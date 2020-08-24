package courier.uy.core.utils.mapstruct;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Presentacion;
import courier.uy.core.entity.Producto;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.resources.dto.PalletResponse;
import courier.uy.core.resources.dto.ProductoAccionesResponse;
import courier.uy.core.utils.poiji.ExcelRNCliente;
import courier.uy.core.utils.poiji.ExcelRNProducto;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Pallet;

@Mapper
public interface Cloner {

    final static Cloner MAPPER = Mappers.getMapper(Cloner.class);

    ProductoAccionesResponse clone(Producto producto);

    Producto exactClone(Producto producto);

    List<ProductoAccionesResponse> clone(List<Producto> producto);

    Set<ProductoAccionesResponse> clone(Set<Producto> producto);

    PalletResponse clone(Pallet pallet);

    @Mapping(target = "gtin", source = "gtin13")
    @Mapping(target = "nivelMinimoVenta", source = "unidadesDeDespacho.unidXcaja")
    @Mapping(target = "pallet.unidadesVenta", source = "unidadesDeDespacho.unidXcaja")
    @Mapping(target = "pallet.cajas", source = "unidadesDeDespacho.unidXcaja")
    @Mapping(target = "pallet.camadas", source = "unidadesDeDespacho.camadasPorPallet")
    @Mapping(target = "paisOrigen", source = "pais")
    @Mapping(target = "descripcion", source = "descripción")
    Producto clone(ExcelRNProducto productoRN) throws ParseException;

    @Mapping(target = "gln", source = "eanCode")
    @Mapping(target = "nombre", source = "nombre")
    Company clone(ExcelRNCliente clienteRN);

    @AfterMapping
    default Producto mapearProductoRNAProducto(ExcelRNProducto productoRN, @MappingTarget Producto producto) throws ParseException {

        final String[] tiposPresentacion = { "AE-Aerosol", "PU-Bandeja", "BR-Barra", "BA-Barril", "BME-Blister",
                "BG-Bolsa", "CBL-Botella", "BX-Caja", "CT-Caja", "CR-Casillero", "X8-Dispensador", "DPE-Display",
                "SA-Funda", "CX-Lata", "MPE-Multipack", "X1-Pallet", "JR-Pote", "SH-Sachet", "NE-Sin Empaque",
                "STL-Stick", "DR-Tambor", "TU-Tubo" };

        Pallet pallet = producto.getPallet();
        Integer cajasXPallet = Integer.parseInt(productoRN.getUnidadesDeDespacho().getCajasPorCamada())
                * Integer.parseInt(productoRN.getUnidadesDeDespacho().getCamadasPorPallet());
        pallet.setCajas("" + cajasXPallet);
        pallet.setAlto("0");
        pallet.setAncho("0");
        pallet.setUnidadesVenta("" + cajasXPallet * Integer.parseInt(productoRN.getUnidadesDeDespacho().getUnidadesPorCaja()));
        producto.setPallet(pallet);
        producto.setMercadoObjetivo("UY");
        Optional<String> esPromo = Optional.ofNullable(productoRN.getEsPromo());
        producto.setEsPromo(esPromo.orElse("N").toUpperCase().equals("S"));

        BigDecimal contenidoNeto = productoRN.getPresentacion().getCantidad() != null
                && !productoRN.getPresentacion().getCantidad().isEmpty()
                ? new BigDecimal(productoRN.getPresentacion().getCantidad().replace(",", "."))
                : new BigDecimal("0");
        producto.setPesoBruto(new BigDecimal("0"));

        Categoria categoria = new Categoria();
        Categoria catPadre = new Categoria(productoRN.getDivision(), (long) 1);
        try {
            categoria.copyForExcel(productoRN.getLinea(), (long) 2, catPadre);
        } catch (ModelException e) {

        }

        producto.setCategoria(categoria);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/YY");
        if (!productoRN.getSuspendido().getSuspendidoDesde().isEmpty())
            producto.setSuspendidoDesde(dtf.parseDateTime(productoRN.getSuspendido().getSuspendidoDesde()));
        if (!productoRN.getSuspendido().getSuspendidoHasta().isEmpty())
            producto.setSuspendidoHasta(dtf.parseDateTime(productoRN.getSuspendido().getSuspendidoHasta()));
        final String presentacion = Arrays.asList(tiposPresentacion).stream()
                .filter(e -> e.split("-")[0].toUpperCase().equals(productoRN.getPresentacion().getTipo().trim()))
                .map(e -> e.split("-")[1]).findFirst().orElse("Sin Empaque");
        producto.setPresentacion(new Presentacion(presentacion));
        producto.setGtinPresentacion(productoRN.getUnidadesDeDespacho().getGtin14());
        producto.setContenidoNeto(contenidoNeto);
        producto.setNivelMinimoVenta(productoRN.getPideUnidad().equals("N") ? Integer.valueOf(productoRN.getUnidadesDeDespacho().getUnidadesPorCaja()) : 1);
        producto.setUnidadMedida(productoRN.getPresentacion().getUnidad().equals("uni") ? "EA"
                : productoRN.getPresentacion().getUnidad());
        return producto;
    }

}