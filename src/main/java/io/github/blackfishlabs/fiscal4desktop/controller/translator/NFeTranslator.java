package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetornoDados;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import br.indie.fiscal4j.nfe400.classes.nota.NFNotaProcessada;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FiscalHelper;
import io.github.blackfishlabs.fiscal4desktop.domain.model.NFeEntity;

import java.util.Optional;
import java.util.UUID;

public class NFeTranslator implements TranslatorForDB<NFeEntity, NFLoteEnvioRetornoDados> {

    @Override
    public NFeEntity toEntity(NFLoteEnvioRetornoDados result) {
        NFeEntity entity = new NFeEntity();
        Optional<NFNota> document = result.getLoteAssinado().getNotas().stream().findFirst();

        final NFNotaProcessada nfProcessed = FiscalHelper.getNFProcessed(result, document.get());

        entity.setId(UUID.randomUUID().toString());
        document.ifPresent(d -> {
            entity.setEmitter(d.getInfo().getEmitente().getCnpj());
            entity.setEnvironment(d.getInfo().getIdentificacao().getAmbiente().toString());
            entity.setKey(d.getInfo().getChaveAcesso());
            entity.setProtocol(result.getRetorno().getProtocoloInfo().getNumeroProtocolo());
            entity.setProtocolCancel("");
            entity.setUf(d.getInfo().getEmitente().getEndereco().getUf());
            entity.setXml(nfProcessed.toString());
            entity.setXmlCancel("");
        });

        return entity;
    }

}
