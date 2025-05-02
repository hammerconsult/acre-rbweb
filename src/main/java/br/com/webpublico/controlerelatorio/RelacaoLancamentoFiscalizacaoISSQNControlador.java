package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoFiscalizacaoISSQN;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoFiscalizacaoISSQNDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoParcelaWebreportDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 28/04/15
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoFiscalizacaoIssqn", pattern = "/tributario/fiscalizacao-issqn/relacao-lancamento-fiscalizacao-issqn/",
        viewId = "/faces/tributario/fiscalizacao/relatorios/relacaolancamentofiscalizacaoissqn.xhtml")})
public class RelacaoLancamentoFiscalizacaoISSQNControlador {

    private static Logger logger = LoggerFactory.getLogger(RelacaoLancamentoFiscalizacaoISSQNControlador.class);
    private FiltroRelacaoLancamentoFiscalizacaoISSQN filtroRelacaoLancamentoFiscalizacaoISSQN;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;

    public FiltroRelacaoLancamentoFiscalizacaoISSQN getFiltroLancamento() {
        return filtroRelacaoLancamentoFiscalizacaoISSQN;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoFiscalizacaoIssqn", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoFiscalizacaoISSQN = new FiltroRelacaoLancamentoFiscalizacaoISSQN();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoFiscalizacaoISSQN.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoFiscalizacaoISSQN.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioDto();
            dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(SistemaService.getInstance().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        if (filtroRelacaoLancamentoFiscalizacaoISSQN.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoFiscalizacaoISSQN.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataLancamentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoISSQN.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataVencimentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoISSQN.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataMovimentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoISSQN.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataPagamentoInicial(),
            filtroRelacaoLancamentoFiscalizacaoISSQN.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    private RelatorioDTO instanciarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeRelatorio(montarNomeRelatorio());
        dto.setNomeParametroBrasao("BRASAO");
        preencherFiltroFiscalizacaoIssqn(dto);
        dto.setApi("tributario/lancamento-fiscalizacao-issqn/");
        return dto;
    }

    protected String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoFiscalizacaoISSQN.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de ISSQN Resumido";
        }
        return "Relação de Lançamento de ISSQN Detalhado";
    }

    private void preencherFiltroFiscalizacaoIssqn(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoFiscalizacaoISSQNDTO filtroDto = new FiltroRelacaoLancamentoFiscalizacaoISSQNDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataLancamentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataMovimentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getDataVencimentoFinal());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoFiscalizacaoISSQN.inDividas());
        filtroDto.setNumeroProgramacaoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getNumeroProgramacaoInicial());
        filtroDto.setNumeroProgramacaoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getNumeroProgramacaoFinal());
        filtroDto.setNumeroOrdemServicoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getNumeroOrdemServicoInicial());
        filtroDto.setNumeroOrdemServicoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getNumeroOrdemServicoFinal());
        filtroDto.setNumeroAutoInfracaoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getNumeroAutoInfracaoInicial());
        filtroDto.setNumeroAutoInfracaoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getNumeroAutoInfracaoFinal());
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getInscricaoFinal());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoFiscalizacaoISSQN.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoFiscalizacaoISSQN.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoFiscalizacaoISSQN.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoFiscalizacaoISSQN.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoFiscalizacaoISSQN.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoFiscalizacaoISSQN.getSomenteTotalizador());

        relatorioDTO.adicionarParametro("filtroDto", filtroDto);
    }

    private String montarNomeContribuintes(List<Pessoa> contribuintes) {
        List<String> nomes = Lists.newArrayList();
        String nomesContribuintes = "";
        if (contribuintes != null) {
            for (Pessoa contribuinte : contribuintes) {
                nomes.add(contribuinte.getNomeCpfCnpj());
            }
            nomesContribuintes = Joiner.on(", ").join(nomes);
        }
        return nomesContribuintes;
    }

    private SituacaoParcelaWebreportDTO[] montarSituacoesParcelaWebreport() {
        List<SituacaoParcelaWebreportDTO> list = Lists.newArrayList();
        for (SituacaoParcela situacao : filtroRelacaoLancamentoFiscalizacaoISSQN.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SelectItem> montarFiscalizacaoIssqn() {
        List<SelectItem> fiscalizacaoIssqn = Lists.newArrayList();
        fiscalizacaoIssqn.add(new SelectItem(null, "     "));
        for (Divida divida : dividaFacade.buscarDividasDeFiscalizacaoISSQN("")) {
            fiscalizacaoIssqn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return fiscalizacaoIssqn;
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }
}
