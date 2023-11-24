package ru.practicum.ewmmain.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByLatAndLon(Float lat, Float lon);
}
