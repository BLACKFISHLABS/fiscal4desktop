package io.github.blackfishlabs.starter;

import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.danfe.DFParser;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvio;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import io.github.blackfishlabs.controller.StatusWebServiceController;
import io.github.blackfishlabs.controller.dto.FiscalStatusWebServiceDTO;
import io.github.blackfishlabs.controller.translator.NFCeTranslator;
import io.github.blackfishlabs.domain.model.ContingencyEntity;
import io.github.blackfishlabs.domain.repository.ContigencyDAO;
import io.github.blackfishlabs.domain.repository.NFCeDAO;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FileHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;
import io.github.blackfishlabs.service.NFeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

class ContingencyScheduler {

    private static final Logger logger = LogManager.getLogger(ContingencyScheduler.class);

    static void execute() {
        logger.info(">> Thread de Verificação das notas em contingência iniciado");

        List<ContingencyEntity> filter = new ContigencyDAO().filter("from ContingencyEntity");
        if (filter.isEmpty()) {
            logger.info(">> Nenhuma nota em contingencia foi encontrada!");
            return;
        } else {
            logger.info(">> Foram encontradas " + filter.size() + " notas em contingência.");
        }

        filter.forEach(f -> {
            StatusWebServiceController statusController = new StatusWebServiceController();
            FiscalStatusWebServiceDTO status = new FiscalStatusWebServiceDTO();
            status.setEmitter(f.getEmitter());
            status.setPassword(f.getKey());
            status.setUf(FiscalProperties.getInstance().getUF());
            status.setModel(DFModelo.NFCE.getCodigo());

            NFeService service = new NFeService();
            if (statusController.getStatusWebServiceCode(status)) {
                try {
                    final String xml = f.getXml();

                    logger.info("Enviando " + xml);
                    NFLoteEnvioRetorno send = service.send(new NFeConfiguration(f.getEmitter(), f.getKey()), xml);

                    if (send.getStatus().equals("539"))
                        new ContigencyDAO().delete(f);

                    checkArgument(send.getStatus().equals("104"),
                            send.getStatus().concat(" - ").
                                    concat(send.getMotivo()));

                    logger.info("Nota autorizada pelo protocolo: ".concat(send.getProtocoloInfo().getNumeroProtocolo()));

                    new ContigencyDAO().delete(f);
                    FileHelper.saveFilesAndSendToEmailAttach(send, xml);
                    saveDocInDatabase(send, xml);

                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }

    private static void saveDocInDatabase(NFLoteEnvioRetorno send, String xml) {
        NFCeTranslator nfCeTranslator = new NFCeTranslator();

        NFLoteEnvio loteEnvio = new DFParser().loteParaObjeto(xml);
        NFNota nota = loteEnvio.getNotas().get(0);

        new Thread(() -> {
            try {
                logger.info("Salvando documentos no Banco de Dados");
                new NFCeDAO().save(nfCeTranslator.toEntity(nota, send, xml));

            } catch (Exception e) {
                logger.error("Erro ao gravar no banco de dados!");
                logger.error(e.getMessage());
            }
        }).start();
    }

}
