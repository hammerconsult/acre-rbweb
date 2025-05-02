package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoDividaAtiva;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoDividaAtivaDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoParcelaWebreportDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoProcessoJudicialDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    @URLMapping(id = "novaRelacaoLancamentoDA", pattern = "/tributario/divida-ativa/relacao-lancamento-divida-ativa/",
        viewId = "/faces/tributario/dividaativa/relatorio/relacaolancamentodividaativa.xhtml")})
public class RelacaoLancamentoDividaAtivaControlador {
    private final Logger logger = LoggerFactory.getLogger(RelacaoLancamentoDividaAtivaControlador.class);

    private FiltroRelacaoLancamentoDividaAtiva filtroRelacaoLancamentoDividaAtiva;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;

    public FiltroRelacaoLancamentoDividaAtiva getFiltroLancamento() {
        return filtroRelacaoLancamentoDividaAtiva;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    protected void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoDividaAtiva = new FiltroRelacaoLancamentoDividaAtiva();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoDividaAtiva.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoDividaAtiva.setExercicioDividaFinal(exercicioVigente.getAno());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
            dto.setNomeRelatorio(montarNomeRelatorio());
            dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            preencherFiltroDividaAtiva(dto);
            dto.setApi("tributario/lancamento-divida-ativa/" +
                (br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio()) ? "excel/" : ""));
            ReportService.getInstance().gerarRelatorio(SistemaService.getInstance().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            logger.error("Erro ao gerar relatorio. (WebReportRelatorioExistenteException) ", e);
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatorio. ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        if (filtroRelacaoLancamentoDividaAtiva.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoDividaAtiva.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaAtiva.getDataLancamentoInicial(),
            filtroRelacaoLancamentoDividaAtiva.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaAtiva.getDataVencimentoInicial(),
            filtroRelacaoLancamentoDividaAtiva.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaAtiva.getDataMovimentoInicial(),
            filtroRelacaoLancamentoDividaAtiva.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoDividaAtiva.getDataPagamentoInicial(),
            filtroRelacaoLancamentoDividaAtiva.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    protected String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoDividaAtiva.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de Dívida Ativa Resumido";
        }
        return "Relação de Lançamento de Dívida Ativa Detalhado";
    }

    private void preencherFiltroDividaAtiva(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoDividaAtivaDTO filtroDto = new FiltroRelacaoLancamentoDividaAtivaDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoDividaAtiva.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoDividaAtiva.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoDividaAtiva.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoDividaAtiva.getDataLancamentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoDividaAtiva.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoDividaAtiva.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoDividaAtiva.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoDividaAtiva.getDataMovimentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoDividaAtiva.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoDividaAtiva.getDataVencimentoFinal());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoDividaAtiva.inDividas());
        filtroDto.setExercicioCertidaoInicial(filtroRelacaoLancamentoDividaAtiva.getExercicioCertidaoInicial());
        filtroDto.setExercicioCertidaoFinal(filtroRelacaoLancamentoDividaAtiva.getExercicioCertidaoFinal());
        filtroDto.setNumeroCertidaoInicial(filtroRelacaoLancamentoDividaAtiva.getNumeroCertidaoInicial());
        filtroDto.setNumeroCertidaoFinal(filtroRelacaoLancamentoDividaAtiva.getNumeroCertidaoFinal());
        filtroDto.setAjuizado(filtroRelacaoLancamentoDividaAtiva.getAjuizado());
        filtroDto.setProtestado(filtroRelacaoLancamentoDividaAtiva.getProtestado());
        ProcessoJudicial.Situacao situacao = filtroRelacaoLancamentoDividaAtiva.getSituacaoProcessoJudicial();
        if (situacao != null) {
            filtroDto.setSituacaoProcessoJudicial(SituacaoProcessoJudicialDTO.valueOf(situacao.getDescricao()));
        }
        TipoCadastroTributario tipoCadastroTributario = filtroRelacaoLancamentoDividaAtiva.getTipoCadastroTributario();
        if (tipoCadastroTributario != null) {
            filtroDto.setTipoCadastroTributario(tipoCadastroTributario.toWebreport());
        }
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoDividaAtiva.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoDividaAtiva.getInscricaoFinal());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoDividaAtiva.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoDividaAtiva.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoDividaAtiva.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoDividaAtiva.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoDividaAtiva.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoDividaAtiva.getSomenteTotalizador());

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
        SituacaoParcela[] situacoesParcela = filtroRelacaoLancamentoDividaAtiva.getSituacoes();
        for (SituacaoParcela situacao : situacoesParcela) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SelectItem> montarDividaAtiva() {
        List<SelectItem> fiscalizacaoIssqn = Lists.newArrayList();
        fiscalizacaoIssqn.add(new SelectItem(null, "     "));
        List<Divida> dividas = dividaFacade.buscarDividasDeDividaAtiva("");
        for (Divida divida : dividas) {
            fiscalizacaoIssqn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return fiscalizacaoIssqn;
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }

    public List<SelectItem> montarTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public List<Divida> completaDividas(String parte) {
        return dividaFacade.listaDividasAtivas(parte.trim(), null);
    }

    public List<SelectItem> situacoesProcessoJudicial() {
        List<SelectItem> itens = Lists.newArrayList();
        itens.add(new SelectItem(null, "Todas"));
        for (ProcessoJudicial.Situacao situacao : ProcessoJudicial.Situacao.values()) {
            itens.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return itens;
    }
}
