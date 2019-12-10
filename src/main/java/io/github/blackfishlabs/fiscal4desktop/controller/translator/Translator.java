package io.github.blackfishlabs.fiscal4desktop.controller.translator;

public interface Translator<D, E, R> {

    E fromDTO(final D dto);

    String response(R result) throws Exception;
}
