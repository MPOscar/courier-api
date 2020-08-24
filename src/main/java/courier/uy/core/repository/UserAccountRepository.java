package courier.uy.core.repository;

import courier.uy.core.entity.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserAccountRepository extends MongoRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
}
