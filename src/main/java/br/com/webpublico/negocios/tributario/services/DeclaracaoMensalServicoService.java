package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.nfse.domain.LoteDeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.dtos.DeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DeclaracaoMensalServicoService {

    private NotaFiscalFacade notaFiscalFacade;
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    private ExercicioFacade exercicioFacade;

    @PostConstruct
    private void init() {
        notaFiscalFacade = (NotaFiscalFacade) Util.getFacadeViaLookup("java:module/NotaFiscalFacade");
        declaracaoMensalServicoFacade = (DeclaracaoMensalServicoFacade) Util.getFacadeViaLookup("java:module/DeclaracaoMensalServicoFacade");
        exercicioFacade = (ExercicioFacade) Util.getFacadeViaLookup("java:module/ExercicioFacade");
    }

    public void gerarDms(TipoMovimentoMensal tipoMovimentoMensal) {
        LoteDeclaracaoMensalServico lancamentoGeralMensal = criarLancamentoGeralMensal(tipoMovimentoMensal);
        List<NotaFiscalSearchDTO> notasDTO = notaFiscalFacade.buscarNotasParaLancamentoGeralPorFiltro(lancamentoGeralMensal);
        List<DeclaracaoMensalServicoNfseDTO> declaracoesDTO = declaracaoMensalServicoFacade.criarDeclaracoesMensalServico(lancamentoGeralMensal, notasDTO);
        declaracaoMensalServicoFacade.declarar(declaracoesDTO);
    }

    public Exercicio getExercicioCorrente() {
        Calendar calendar = Calendar.getInstance();
        Integer ano = calendar.get(Calendar.YEAR);
        return exercicioFacade.getExercicioPorAno(ano);
    }

    public Date getCompetenciaInicial() {
        Calendar calendar = Calendar.getInstance();
        return DataUtil.getDateParse("01/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
    }

    public Date getCompetenciaFinal() {
        Calendar calendar = Calendar.getInstance();
        return DataUtil.getDateParse(DataUtil.ultimoDiaDoMes(calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
    }

    public LoteDeclaracaoMensalServico criarLancamentoGeralMensal(TipoMovimentoMensal tipoMovimentoMensal) {
        LoteDeclaracaoMensalServico lancamentoGeralMensal = new LoteDeclaracaoMensalServico();
        lancamentoGeralMensal.setExercicio(getExercicioCorrente());
        lancamentoGeralMensal.setTipoMovimento(tipoMovimentoMensal);
        lancamentoGeralMensal.setMes(Mes.getMesToInt(DataUtil.getMes(new Date())));
        lancamentoGeralMensal.setCmcInicial("0");
        lancamentoGeralMensal.setCmcFinal("99999999999");
        return lancamentoGeralMensal;
    }

}
