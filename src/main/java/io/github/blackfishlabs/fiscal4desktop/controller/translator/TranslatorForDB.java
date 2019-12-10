package io.github.blackfishlabs.fiscal4desktop.controller.translator;

public interface TranslatorForDB<E, R> {

    E toEntity(R result);
}
