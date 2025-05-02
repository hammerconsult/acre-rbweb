package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoTaxasDiversas;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
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
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoTaxasDiversasDTO;
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
 * Date: 05/05/17
 * Time: 09:00
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaRelacaoLancamentoTaxasDiversas", pattern = "/tributario/taxas-diversas/relacao-lancamento-taxas-diversas/",
        viewId = "/faces/tributario/taxasdividasdiversas/relatorios/relacaolancamentotaxasdiversas.xhtml")})
public class RelacaoLancamentoTaxasDiversasControlador {
    private final static Logger logger = LoggerFactory.getLogger(RelacaoLancamentoTaxasDiversasControlador.class);

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;
    private List<SelectItem> dividasTaxasDiversas;

    private FiltroRelacaoLancamentoTaxasDiversas filtroRelacaoLancamentoTaxasDiversas;

    public FiltroRelacaoLancamentoTaxasDiversas getFiltroLancamento() {
        return filtroRelacaoLancamentoTaxasDiversas;
    }

    @URLAction(mappingId = "novaRelacaoLancamentoTaxasDiversas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        instanciarFiltroLancamento();
    }

    private void instanciarFiltroLancamento() {
        filtroRelacaoLancamentoTaxasDiversas = new FiltroRelacaoLancamentoTaxasDiversas();
        Exercicio exercicioVigente = exercicioFacade.getExercicioCorrente();
        filtroRelacaoLancamentoTaxasDiversas.setExercicioDividaInicial(exercicioVigente.getAno());
        filtroRelacaoLancamentoTaxasDiversas.setExercicioDividaFinal(exercicioVigente.getAno());

    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
            dto.setNomeRelatorio(montarNomeRelatorio());
            dto.setNomeParametroBrasao("BRASAO");
            preencherFiltroTaxasDiversas(dto);
            dto.setApi("tributario/lancamento-taxas-diversas/");
            ReportService.getInstance().gerarRelatorio(SistemaService.getInstance().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        if (filtroRelacaoLancamentoTaxasDiversas.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial da Dívida deve ser informado.");
        }
        if (filtroRelacaoLancamentoTaxasDiversas.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final da Dívida deve ser informado.");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoTaxasDiversas.getDataLancamentoInicial(),
            filtroRelacaoLancamentoTaxasDiversas.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoTaxasDiversas.getDataVencimentoInicial(),
            filtroRelacaoLancamentoTaxasDiversas.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoTaxasDiversas.getDataMovimentoInicial(),
            filtroRelacaoLancamentoTaxasDiversas.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroRelacaoLancamentoTaxasDiversas.getDataPagamentoInicial(),
            filtroRelacaoLancamentoTaxasDiversas.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        ve.lancarException();
    }

    public List<Divida> completarDividas(String parte) {
        return dividaFacade.listaDividasAtivas(parte.trim(), null);
    }

    private String montarNomeRelatorio() {
        if (filtroRelacaoLancamentoTaxasDiversas.getTipoRelatorio().equals(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO)) {
            return "Relação de Lançamento de Taxas Diversas Resumido";
        }
        return "Relação de Lançamento de Taxas Diversas Detalhado";
    }

    private void preencherFiltroTaxasDiversas(RelatorioDTO relatorioDTO) {
        FiltroRelacaoLancamentoTaxasDiversasDTO filtroDto = new FiltroRelacaoLancamentoTaxasDiversasDTO();
        filtroDto.setExercicioDividaInicial(filtroRelacaoLancamentoTaxasDiversas.getExercicioDividaInicial());
        filtroDto.setExercicioDividaFinal(filtroRelacaoLancamentoTaxasDiversas.getExercicioDividaFinal());
        filtroDto.setDataLancamentoInicial(filtroRelacaoLancamentoTaxasDiversas.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtroRelacaoLancamentoTaxasDiversas.getDataLancamentoFinal());
        filtroDto.setDataPagamentoInicial(filtroRelacaoLancamentoTaxasDiversas.getDataPagamentoInicial());
        filtroDto.setDataPagamentoFinal(filtroRelacaoLancamentoTaxasDiversas.getDataPagamentoFinal());
        filtroDto.setDataMovimentoInicial(filtroRelacaoLancamentoTaxasDiversas.getDataMovimentoInicial());
        filtroDto.setDataMovimentoFinal(filtroRelacaoLancamentoTaxasDiversas.getDataMovimentoFinal());
        filtroDto.setDataVencimentoInicial(filtroRelacaoLancamentoTaxasDiversas.getDataVencimentoInicial());
        filtroDto.setDataVencimentoFinal(filtroRelacaoLancamentoTaxasDiversas.getDataVencimentoFinal());
        filtroDto.setIdsDivida(filtroRelacaoLancamentoTaxasDiversas.inDividas());
        filtroDto.setNumeroProcessoInicial(filtroRelacaoLancamentoTaxasDiversas.getNumeroProcessoInicial());
        filtroDto.setNumeroProcessoFinal(filtroRelacaoLancamentoTaxasDiversas.getNumeroProcessoFinal());
        Tributo tributo = filtroRelacaoLancamentoTaxasDiversas.getTributo();
        if (tributo != null) {
            filtroDto.setIdTributo(tributo.getId());
            filtroDto.setDescricaoTributo(tributo.getDescricao());
        }
        TipoCadastroTributario tipoCadastroTributario = filtroRelacaoLancamentoTaxasDiversas.getTipoCadastroTributario();
        if (tipoCadastroTributario != null) {
            filtroDto.setTipoCadastroTributarioDTO(tipoCadastroTributario.toWebreport());
        }
        filtroDto.setInscricaoInicial(filtroRelacaoLancamentoTaxasDiversas.getInscricaoInicial());
        filtroDto.setInscricaoFinal(filtroRelacaoLancamentoTaxasDiversas.getInscricaoFinal());
        filtroDto.setIdsContribuintes(Util.montarIdsList(filtroRelacaoLancamentoTaxasDiversas.getContribuintes()));
        filtroDto.setContribuintes(montarNomeContribuintes(filtroRelacaoLancamentoTaxasDiversas.getContribuintes()));
        filtroDto.setSituacoes(montarSituacoesParcelaWebreport());
        filtroDto.setTotalLancadoInicial(filtroRelacaoLancamentoTaxasDiversas.getTotalLancadoInicial());
        filtroDto.setTotalLancadoFinal(filtroRelacaoLancamentoTaxasDiversas.getTotalLancadoFinal());
        filtroDto.setTipoRelatorio(TipoRelatorioDTO.valueOf(filtroRelacaoLancamentoTaxasDiversas.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtroRelacaoLancamentoTaxasDiversas.getSomenteTotalizador());

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
        for (SituacaoParcela situacao : filtroRelacaoLancamentoTaxasDiversas.getSituacoes()) {
            list.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[list.size()];
        return list.toArray(situacoes);
    }

    public List<SelectItem> montarDividasTaxasDiversas() {
        if (dividasTaxasDiversas == null) {
            dividasTaxasDiversas = Lists.newArrayList();
            dividasTaxasDiversas.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeTaxasDiversas("")) {
                dividasTaxasDiversas.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasTaxasDiversas;
    }

    public List<SelectItem> montarTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public List<SituacaoParcela> montarSituacoesParcela() {
        return SituacaoParcela.getValues();
    }
}
