package com.pvpin.pvpincore.demo;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.api.PVPINPersistence;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author William_Shi
 */
public class PersistenceDemo {
    static {
        var dataMap = PVPINPersistence.getDataMap();
        dataMap.put("JAVA","TEST");
        dataMap.put("LIST", List.of(1D,2D,3D));
        dataMap.put("MAP", Map.of("KEY",List.of("VALUE")));
        PVPINPersistence.saveToFile();
        PVPINPersistence.readFromFile();
        System.out.println(PVPINPersistence.getDataMap().toString());
    }
}
