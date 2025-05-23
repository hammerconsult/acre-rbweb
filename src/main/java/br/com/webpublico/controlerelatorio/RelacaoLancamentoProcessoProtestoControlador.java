package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoProcessoProtesto;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoProcessoProtestoDTO;
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
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = @URLMapping(id = "novaRelacaoLancamentoProcesoProtesto",
    pattern = "/tributario/processo-debito/relacao-lancamento-processo-protesto/",
    viewId = "/faces/tributario/contacorrente/relatorio/relacaolancamentoprocessoprotesto.xhtml"))
public class RelacaoLancamentoProcessoProtestoControlador implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(RelacaoLancamentoProcessoProtestoControlador.class);

    @EJB
    private CacheTributario cacheTributario;

    @EJB
    private ExercicioFacade exercicioFacade;

    private FiltroRelacaoProcessoProtesto filtro;

    @URLAction(mappingId = "novaRelacaoLancamentoProcesoProtesto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        this.filtro = new FiltroRelacaoProcessoProtesto();
        this.filtro.setDivida(new Divida());
        this.filtro.setExercicioInicial(exercicioFacade.getExercicioCorrente());
        this.filtro.setExercicioFinal(exercicioFacade.getExercicioCorrente());
        this.filtro.setTipoRelatorio(AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.DETALHADO);
    }

    public String getCaminhoPadrao() {
        return "/tributario/processo-debito/relacao-lancamento-processo-protesto/";
    }

    public List<SelectItem> montarTiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public List<SelectItem> recuperarDividas() {
        List<SelectItem> itens = Lists.newArrayList();
        if (filtro != null && filtro.getTipoCadastro() != null) {
            List<Divida> dividas = filtro.getTipoCadastro().isPessoa() ? cacheTributario.getDividas() : cacheTributario.getDividasPorTipoCadastro(filtro.getTipoCadastro());
            itens.add(new SelectItem(null, " "));
            for (Divida divida : dividas) {
                itens.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return itens;
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            Util.adicionarObjetoEmLista(filtro.getDividas(), filtro.getDivida());
            filtro.setDivida(new Divida());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerDivida(Divida divida) {
        filtro.getDividas().remove(divida);
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getDividas().contains(filtro.getDivida())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A dívida selecionada já foi adicionada.");
        }
        ve.lancarException();
    }

    public void limparFiltros() {
        filtro.setCadastro(null);
        filtro.setPessoa(null);
        filtro.setDivida(new Divida());
        filtro.setDividas(Lists.<Divida>newArrayList());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = instanciarRelatorioDto(tipoRelatorioExtensao);
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

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro != null) {
            if (filtro.getExercicioInicial() != null && filtro.getExercicioFinal() != null &&
                filtro.getExercicioFinal().compareTo(filtro.getExercicioInicial()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Exercício Final do Processo deve ser igual ou posterior ao Exercício Inicial.");
            }
            if (filtro.getDataLancamentoInicial() != null && filtro.getDataLancamentoFinal() != null &&
                filtro.getDataLancamentoFinal().compareTo(filtro.getDataLancamentoInicial()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final do Processo deve ser igual ou posterior a Data Inicial.");
            }

        }
        ve.lancarException();
    }

    private RelatorioDTO instanciarRelatorioDto(String tipoRelatorio) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("Relação de Lançamento de Processo de Protesto" + filtro.getTipoRelatorio().toString());
        dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
        dto.adicionarParametro("filtroDto", preencherFiltrosDto());
        dto.setApi("tributario/lancamento-processo-protesto/" +
            (TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio()) ? "excel/" : ""));
        return dto;
    }

    private FiltroRelacaoProcessoProtestoDTO preencherFiltrosDto() {
        FiltroRelacaoProcessoProtestoDTO filtroDto = new FiltroRelacaoProcessoProtestoDTO();
        filtroDto.setExercicioInicial(filtro.getExercicioInicial() != null ? filtro.getExercicioInicial().getAno() : null);
        filtroDto.setExercicioFinal(filtro.getExercicioFinal() != null ? filtro.getExercicioFinal().getAno() : null);
        filtroDto.setDataLancamentoInicial(filtro.getDataLancamentoInicial());
        filtroDto.setDataLancamentoFinal(filtro.getDataLancamentoFinal());
        filtroDto.setCadastro_id(filtro.getCadastro() != null ? filtro.getCadastro().getId() : null);
        filtroDto.setInscricaoCadastral(filtro.getCadastro() != null ? filtro.getCadastro().getNumeroCadastro() : null);
        filtroDto.setPessoa_id(filtro.getPessoa() != null ? filtro.getPessoa().getId() : null);
        filtroDto.setDadosPessoa(filtro.getPessoa() != null ? filtro.getPessoa().getNomeCpfCnpj() : null);
        filtroDto.setNumeroCDA(filtro.getNumeroCDA());
        filtroDto.setTipoCadastroTributario(filtro.getTipoCadastro() != null ? filtro.getTipoCadastro().toWebreport() : null);

        if (filtro.getDividas() != null && !filtro.getDividas().isEmpty()) {
            filtroDto.setIdsDivida(filtro.inDividas());
            filtroDto.setDividas(filtro.filtroDividas());
        }
        filtroDto.setTipoRelatorio(br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO.valueOf(filtro.getTipoRelatorio().name()));
        filtroDto.setSomenteTotalizador(filtro.getSomenteTotalizador());
        return filtroDto;
    }

    public FiltroRelacaoProcessoProtesto getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelacaoProcessoProtesto filtro) {
        this.filtro = filtro;
    }
}
