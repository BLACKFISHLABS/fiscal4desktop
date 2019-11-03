package io.github.blackfishlabs.controller.translator;

public interface TranslatorForDB<E, R> {

    E toEntity(R result);
}
