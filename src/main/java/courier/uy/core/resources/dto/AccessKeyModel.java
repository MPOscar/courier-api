package courier.uy.core.resources.dto;

public class AccessKeyModel {
	
	private String key;
	private String keyId;
	
	public AccessKeyModel(String key, String keyId) {
		this.key = key;
		this.keyId = keyId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getKeyId() {
		return keyId;
	}
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	
}
