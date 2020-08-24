package courier.uy.core.services.implementations;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.ArrayList;

import courier.uy.CourierConfiguration;
import courier.uy.core.db.ParamsDAO;
import courier.uy.core.db.ProductosDAO;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Param;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.services.interfaces.IFileService;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService implements IFileService {
	@Autowired
	private ParamsDAO paramsDAO;
	@Autowired
	private CourierConfiguration configuration;
	@Autowired
	private ProductosDAO productosDAO;
	@Autowired
	private IEmpresaRepository empresaRepository;

	public FileService(ParamsDAO paramsDAO, ProductosDAO productosDAO, CourierConfiguration configuration) {
		this.paramsDAO = paramsDAO;
		this.configuration = configuration;
		this.productosDAO = productosDAO;
	}

	@Override
	public String SaveFileForProduct(MultipartFile file, String productId) throws IOException {
		Producto producto = this.productosDAO.findById(productId);
		String location = "";
		if(producto != null){
			String rut = producto.getEmpresa().getRut().toString();
			location = rut + "/productos/img/" + producto.getCpp() + "_" + producto.getGtin()
					+ getFileName(file.getOriginalFilename());
			producto.setFoto(location);
			this.productosDAO.update(producto);
			InputStream uploadedInputStream = file.getInputStream();
			SaveFile(uploadedInputStream, file.getContentType(), location);
		}
		return location;
	}

	@Override
	public String SaveFileForBussisnes(MultipartFile file, UsuarioEmpresa usuarioEmpresa) throws IOException {
		Company empresa = usuarioEmpresa.getEmpresa();
		String rut = empresa.getRut().toString();
		String location = rut + "/perfil/img/" + rut + getFileName(file.getOriginalFilename());
		InputStream uploadedInputStream = file.getInputStream();
		SaveFile(uploadedInputStream, file.getContentType(), location);
		empresa.setFoto(location);
		empresaRepository.save(empresa);
		return location;
	}

	@Override
	public List<String> SaveFileByRutBussisnes(Long rut, MultipartFile[] files) throws IOException {
		List<String> result = new ArrayList<>();
		for (MultipartFile file: files) {
			InputStream inputStream = file.getInputStream();
			String fileName = file.getOriginalFilename();
			String cpp = fileName.substring(fileName.lastIndexOf("_") > -1 ? fileName.lastIndexOf("_") + 1 : 0,
					fileName.lastIndexOf("."));
			String mediaType = file.getContentType();
			Producto producto = this.productosDAO.getProductByBussisnesRutAndCpp(rut, Long.parseLong(cpp));
			if (producto != null) {
				String location = rut + "/productos/img/" + producto.getCpp() + "_" + producto.getGtin() + getFileName(file.getOriginalFilename());
				SaveFile(inputStream, mediaType, location);
				producto.setFoto(location);
				this.productosDAO.update(producto);
				result.add(location);
			}
		}
		return result;
	}

	private String SaveFile(InputStream uploadedInputStream, String mediaType, String location) {
		Param key = paramsDAO.findByNombre("s3_key");
		Param keyId = paramsDAO.findByNombre("s3_id");
		AWSCredentials credentials = new BasicAWSCredentials(keyId.getValor(), key.getValor());
		@SuppressWarnings("deprecation")
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		ObjectMetadata objectMetaData = new ObjectMetadata();
		objectMetaData.setContentType(mediaType);
		String bucketName = configuration.getConfiguracionDespliegue().getBucket();
		s3Client.putObject(new PutObjectRequest(bucketName, location, uploadedInputStream, objectMetaData)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		return location;
	}

	private String getFileName(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}
}