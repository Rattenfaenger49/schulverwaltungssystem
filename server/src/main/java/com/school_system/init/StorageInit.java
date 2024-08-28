package com.school_system.init;

import com.school_system.service.StorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;

public class StorageInit implements CommandLineRunner{
    @Resource
    StorageService storageService;

    @Override
    public void run(String... arg)  {
        storageService.init();


    }
}
