package com.school_system.config.properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class StorageProperties{
    // could be ad to MyAppProperties or used in the Service directly
    @Value("${paths.storage}")
    private String location;

}
