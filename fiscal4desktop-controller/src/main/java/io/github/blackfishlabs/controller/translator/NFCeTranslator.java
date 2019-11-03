package io.github.blackfishlabs.controller.translator;

import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import br.indie.fiscal4j.nfe400.classes.nota.NFNotaProcessada;
import io.github.blackfishlabs.domain.model.NFCeEntity;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;

import java.util.Optional;
import java.util.UUID;

public class NFCeTranslator implements TranslatorForDB<NFCeEntity, NFLoteEnvioRetornoDados> {

    @Override
    public NFCeEntity toEntity(NFLoteEnvioRetornoDados result) {
        NFCeEntity entity = new NFCeEntity();
        Optional<NFNota> document = result.getLoteAssinado().getNotas().stream().findFirst();

        final NFNotaProcessada nfProcessed = FiscalHelper.getNFProcessed(result, document.get());

        entity.setId(UUID.randomUUID().toString());
        document.ifPresent(d -> {
            entity.setEmitter(d.getInfo().getEmitente().getCnpj());
            entity.setEnvironment(d.getInfo().getIdentificacao().getAmbiente().toString());
            entity.setKey(d.getInfo().getChaveAcesso());
            entity.setProtocol(result.getRetorno().getProtocoloInfo().getNumeroProtocolo());
            entity.setProtocolCancel("");
            entity.setQrCode(d.getInfoSuplementar().getQrCode());
            entity.setUf(d.getInfo().getEmitente().getEndereco().getUf());
            entity.setXml(nfProcessed.toString());
            entity.setXmlCancel("");
        });

        return entity;
    }

    public NFCeEntity toEntity(NFNota document, NFLoteEnvioRetorno retorno, String xml) {
        NFCeEntity entity = new NFCeEntity();

        entity.setId(UUID.randomUUID().toString());

        entity.setEmitter(document.getInfo().getEmitente().getCnpj());
        entity.setEnvironment(document.getInfo().getIdentificacao().getAmbiente().toString());
        entity.setKey(document.getInfo().getChaveAcesso());
        entity.setProtocol(retorno.getProtocoloInfo().getNumeroProtocolo());
        entity.setProtocolCancel("");
        entity.setQrCode(document.getInfoSuplementar().getQrCode());
        entity.setUf(document.getInfo().getEmitente().getEndereco().getUf());
        entity.setXml(xml);
        entity.setXmlCancel("");

        return entity;
    }
}
