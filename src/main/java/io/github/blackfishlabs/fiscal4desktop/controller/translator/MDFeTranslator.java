package io.github.blackfishlabs.fiscal4desktop.controller.translator;

import br.indie.fiscal4j.mdfe3.classes.consultaRecibo.MDFeConsultaReciboRetorno;
import br.indie.fiscal4j.mdfe3.classes.lote.envio.MDFEnvioLoteRetornoDados;
import br.indie.fiscal4j.mdfe3.classes.nota.MDFe;
import io.github.blackfishlabs.fiscal4desktop.domain.model.MDFeEntity;

import java.util.UUID;

public class MDFeTranslator {

    public MDFeEntity toEntity(MDFeConsultaReciboRetorno status, MDFEnvioLoteRetornoDados result) {
        MDFeEntity entity = new MDFeEntity();
        MDFe mdfe = result.getLoteAssinado().getMdfe();

        entity.setId(UUID.randomUUID().toString());
        entity.setEmitter(mdfe.getInfo().getEmitente().getCnpj());
        entity.setEnvironment(mdfe.getInfo().getIdentificacao().getAmbiente().toString());
        entity.setKey(status.getMdfProtocolo().getProtocoloInfo().getChave());
        entity.setProtocol(status.getMdfProtocolo().getProtocoloInfo().getNumeroProtocolo());
        entity.setProtocolCancel("");
        entity.setUf(mdfe.getInfo().getEmitente().getEndereco().getSiglaUF());
        entity.setXml(result.getLoteAssinado().toString());
        entity.setXmlCancel("");

        return entity;
    }
}
