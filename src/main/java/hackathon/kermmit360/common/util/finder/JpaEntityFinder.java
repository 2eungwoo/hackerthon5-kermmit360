package hackathon.kermmit360.common.util.finder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
public class JpaEntityFinder<T, ID> implements EntityFinder<T, ID> {

    private final JpaRepository<T, ID> repository;
    private final String entityName;

    @Override
    public T findByIdOrThrow(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("404 not found : " + entityName));
    }
}