package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.entidades.LoteBaixa;
import br.com.webpublico.enums.TipoIntegracao;
import br.com.webpublico.negocios.ArrecadacaoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IntegracaoTributabioContabilExecutor implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(IntegracaoTributabioContabilExecutor.class);
    private final ArrecadacaoFacade arrecadacaoFacade;
    private LoteBaixa loteBaixa;
    private TipoIntegracao tipoIntegracao;

    private IntegracaoTributabioContabilExecutor() {
        this.arrecadacaoFacade = (ArrecadacaoFacade) Util.getFacadeViaLookup("java:module/ArrecadacaoFacade");
    }

    public static IntegracaoTributabioContabilExecutor build(LoteBaixa loteBaixa, TipoIntegracao tipoIntegracao) {
        IntegracaoTributabioContabilExecutor build = new IntegracaoTributabioContabilExecutor();
        build.loteBaixa = loteBaixa;
        build.tipoIntegracao = tipoIntegracao;
        return build;
    }


    @Override
    public void run() {
        AssistenteBarraProgresso assistenteBarraProgresso = new AssistenteBarraProgresso("Integração Tributário Contábil do Lote " + loteBaixa.getCodigoLote(), 1);
        arrecadacaoFacade.gerarIntegracao(loteBaixa, tipoIntegracao);
        assistenteBarraProgresso.contar(1);
    }


}
