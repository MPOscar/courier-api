package courier.uy.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.hash.Hashing;
import courier.uy.core.utils.serializer.CustomDateTimeDeserializer;
import courier.uy.core.utils.serializer.CustomDateTimeSerializer;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.ws.rs.core.Link;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "Driver")
public class Driver extends Entidad {

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String password;

    private Boolean isDeleted;

    private Boolean available;

    private String transportType;

    private String status;

    private String transportDescription;

    private String locationLat;

    private String locationLon;

    private String deviceId;

    private String devicePlatform;

    private String image;

    private String language;

    private String rating;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    protected DateTime lastLogin;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    protected DateTime lastOnline;

    @DBRef(lazy = true)
    private Company company;

    private String scompany;

    @DBRef(lazy = true)
    private Team team;

    private String steam;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Driver other = (Driver) obj;
        if (other.getId().equals(this.getId()))
            return true;
        return false;
    }
}
