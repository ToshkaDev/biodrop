package biojobs;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TabDao extends JpaRepository<Tab, Integer> {
    Tab findByTabName(String tabName);
}

