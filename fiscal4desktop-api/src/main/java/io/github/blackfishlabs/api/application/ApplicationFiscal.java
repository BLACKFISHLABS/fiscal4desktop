package io.github.blackfishlabs.api.application;

import io.github.blackfishlabs.api.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ApplicationFiscal extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(StatusSefazAPI.class);
        resources.add(CCeAPI.class);
        resources.add(NFeAPI.class);
        resources.add(NFCeAPI.class);
        resources.add(MDFeAPI.class);
        return resources;
    }

}
