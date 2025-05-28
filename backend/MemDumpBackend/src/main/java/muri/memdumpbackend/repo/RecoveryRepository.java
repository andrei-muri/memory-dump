package muri.memdumpbackend.repo;

import muri.memdumpbackend.model.Recovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecoveryRepository extends JpaRepository<Recovery, UUID> {
}
