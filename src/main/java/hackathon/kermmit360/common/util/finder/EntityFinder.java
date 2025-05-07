package hackathon.kermmit360.common.util.finder;

public interface EntityFinder<T, ID> {
    T findByIdOrThrow(ID id);
}