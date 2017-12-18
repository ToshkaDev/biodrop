package biojobs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TabDao extends JpaRepository<Tab, Integer> {
    Tab findByTabName(String tabName);

    List<Tab> findAll();


}

