package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.DeclaracaoMensalServicoService;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GeracaoDeclaracaoMensalServicoJob extends WPJob {

    private static final Logger log = LoggerFactory.getLogger(GeracaoDeclaracaoMensalServicoJob.class);

    @Autowired
    private DeclaracaoMensalServicoService declaracaoMensalServicoService;



    @Override
    public void execute() {
        log.debug("Gerando DMS do Tipo Normal");
        declaracaoMensalServicoService.gerarDms(TipoMovimentoMensal.NORMAL);
        log.debug("Gerou DMS do Tipo Normal com sucesso!");

        log.debug("Gerando DMS do Tipo Retenção");
        declaracaoMensalServicoService.gerarDms(TipoMovimentoMensal.RETENCAO);
        log.debug("Gerou DMS do Tipo Rentenção com sucesso!");
    }


}
