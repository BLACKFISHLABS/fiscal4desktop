package io.github.blackfishlabs.fiscal4desktop;

import br.indie.fiscal4j.DFModelo;
import br.indie.fiscal4j.danfe.DFParser;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvio;
import br.indie.fiscal4j.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import br.indie.fiscal4j.nfe400.classes.nota.NFNota;
import io.github.blackfishlabs.fiscal4desktop.common.helper.FileHelper;
import io.github.blackfishlabs.fiscal4desktop.common.properties.FiscalProperties;
import io.github.blackfishlabs.fiscal4desktop.controller.StatusWebServiceController;
import io.github.blackfishlabs.fiscal4desktop.controller.dto.FiscalStatusWebServiceDTO;
import io.github.blackfishlabs.fiscal4desktop.controller.translator.NFCeTranslator;
import io.github.blackfishlabs.fiscal4desktop.domain.model.ContingencyEntity;
import io.github.blackfishlabs.fiscal4desktop.domain.repository.ContingencyRepository;
import io.github.blackfishlabs.fiscal4desktop.domain.repository.NFCeRepository;
import io.github.blackfishlabs.fiscal4desktop.infra.NFeConfiguration;
import io.github.blackfishlabs.fiscal4desktop.service.NFeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

class ContingencyScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContingencyScheduler.class);

    @Autowired
    private ContingencyRepository contingencyRepository;
    @Autowired
    private NFCeRepository nfCeRepository;

    public void execute() {
        LOGGER.info(">> Thread de Verificação das notas em contingência iniciada");

        List<ContingencyEntity> filter = contingencyRepository.findAll();
        if (filter.isEmpty()) {
            LOGGER.info(">> Nenhuma nota em contingencia foi encontrada!");
            return;
        } else {
            LOGGER.info(">> Foram encontradas " + filter.size() + " notas em contingência.");
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

                    LOGGER.info("Enviando " + xml);
                    NFLoteEnvioRetorno send = service.send(new NFeConfiguration(f.getEmitter(), f.getKey()), xml);

                    if (send.getStatus().equals("539"))
                        contingencyRepository.delete(f);

                    checkArgument(send.getStatus().equals("104"),
                            send.getStatus().concat(" - ").
                                    concat(send.getMotivo()));

                    LOGGER.info("Nota autorizada pelo protocolo: ".concat(send.getProtocoloInfo().getNumeroProtocolo()));

                    contingencyRepository.delete(f);
                    FileHelper.saveFilesAndSendToEmailAttach(send, xml);
                    saveDocInDatabase(send, xml);

                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });
    }

    private void saveDocInDatabase(NFLoteEnvioRetorno send, String xml) {
        NFCeTranslator nfCeTranslator = new NFCeTranslator();

        NFLoteEnvio loteEnvio = new DFParser().loteParaObjeto(xml);
        NFNota nota = loteEnvio.getNotas().get(0);

        new Thread(() -> {
            try {
                LOGGER.info("Salvando documentos no Banco de Dados");
                nfCeRepository.save(nfCeTranslator.toEntity(nota, send, xml));

            } catch (Exception e) {
                LOGGER.error("Erro ao gravar no banco de dados!");
                LOGGER.error(e.getMessage());
            }
        }).start();
    }

}
