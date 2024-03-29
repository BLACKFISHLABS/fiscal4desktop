package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import io.github.blackfishlabs.fiscal4desktop.domain.model.ContingencyEntity;

import java.util.UUID;

public class ContingencyTranslator implements TranslatorForDB<ContingencyEntity, String> {

    private final String emitter;
    private final String key;

    public ContingencyTranslator(String emitter, String key) {
        this.emitter = emitter;
        this.key = key;
    }

    @Override
    public ContingencyEntity toEntity(String result) {

        ContingencyEntity entity = new ContingencyEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setEmitter(emitter);
        entity.setKey(key);
        entity.setXml(result);

        return entity;
    }


}
