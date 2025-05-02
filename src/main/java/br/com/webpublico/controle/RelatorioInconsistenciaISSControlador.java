package br.com.webpublico.controle;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Servico;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioInconsistenciaISS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ServicoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by William on 23/05/2017.
 */

@ManagedBean(name = "relatorioInconsistenciaISSControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioInconsistenciaISS", pattern = "/relatorio-inconsistencia-iss/",
        viewId = "/faces/tributario/fiscalizacao/relatorios/relatorioinconsistenciaiss.xhtml"),
})
public class RelatorioInconsistenciaISSControlador {
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioInconsistenciaISSControlador.class);
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<SelectItem> dividasSelectItem;
    private Divida divida;
    private List<SelectItem> servicoSelectItem;
    private Servico servico;
    private FiltroRelatorioInconsistenciaISS filtro;

    @URLAction(mappingId = "novoRelatorioInconsistenciaISS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioInconsistenciaISS();
    }

    public FiltroRelatorioInconsistenciaISS getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioInconsistenciaISS filtro) {
        this.filtro = filtro;
    }

    public Divida getDivida() {
        return divida;
    }

    public List<SelectItem> getServicoSelectItem() {
        return servicoSelectItem;
    }

    public void setServicoSelectItem(List<SelectItem> servicoSelectItem) {
        this.servicoSelectItem = servicoSelectItem;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    private void validarEmissaoRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Inicial.");
        }
        if (filtro.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Final.");
        }
        if ((filtro.getDataInicial() != null && filtro.getDataFinal() != null) && (filtro.getDataInicial().after(filtro.getDataFinal()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser anterior a Data Final.");
        }
        if (filtro.getPercentualMais() != null && filtro.getPercentualMais().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O % de Variação para mais não pode ser negativo.");
        }
        if (filtro.getPercentualMenos() != null && filtro.getPercentualMenos().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O % de Variação para menos não pode ser negativo.");
        }
        if (filtro.getPercentualMenos() == null && filtro.getPercentualMais() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um percentual de variação.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarEmissaoRelatorio();
            String nomeRelatorio = "Relatório de Possíveis Inconsistência do ISS";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Tributário");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
            dto.adicionarParametro("idsDividas", getIdsDividas(filtro.getDividas()));
            dto.adicionarParametro("idsServicos", getIdsServicos(filtro.getServicos()));
            dto.adicionarParametro("FILTROS", getFiltrosUtilizados(filtro));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("tributario/inconsistencia-iss/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getFiltrosUtilizados(FiltroRelatorioInconsistenciaISS filtro) {
        StringBuilder sb = new StringBuilder();
        sb.append("Data Inicial: ").append(DataUtil.getDataFormatada(filtro.getDataInicial())).append("; ");
        sb.append("Data Final: ").append(DataUtil.getDataFormatada(filtro.getDataFinal())).append("; ");
        if (filtro.getPercentualMenos() != null) {
            sb.append("% Variação para menos: ").append(filtro.getPercentualMenos()).append("; ");

        }
        if (filtro.getPercentualMais() != null) {
            sb.append("% Variação para mais: ").append(filtro.getPercentualMais()).append("; ");
        }
        if (filtro.getDividas() != null && !filtro.getDividas().isEmpty()) {
            sb.append("Dívida: ");
            for (Divida divida : filtro.getDividas()) {
                sb.append(" ").append(divida.getDescricao());
                sb.append(", ");
            }
        }
        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            sb.append("Serviço: ");
            for (Servico servico : filtro.getServicos()) {
                sb.append(" ").append(servico.getCodigo());
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    private List<Long> getIdsDividas(List<Divida> dividas) {
        List<Long> ids = Lists.newArrayList();
        if (dividas != null && !dividas.isEmpty()) {
            for (Divida divida : dividas) {
                ids.add(divida.getId());
            }
        }
        return ids;
    }

    private List<Long> getIdsServicos(List<Servico> servicos) {
        List<Long> ids = Lists.newArrayList();
        if (servicos != null && !servicos.isEmpty()) {
            for (Servico servico : servicos) {
                ids.add(servico.getId());
            }
        }
        return ids;
    }

    public List<SelectItem> getDividasSelecionadas() {
        if (dividasSelectItem == null) {
            dividasSelectItem = Lists.newArrayList();
            dividasSelectItem.add(new SelectItem(null, ""));
            for (Divida divida : dividaFacade.buscarDividasDeISSQNAndNFSE("")) {
                dividasSelectItem.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasSelectItem;
    }

    public void adicionarDivida() {
        Util.adicionarObjetoEmLista(filtro.getDividas(), divida);
        divida = null;
    }

    public void removerDivida(Divida divida) {
        filtro.getDividas().remove(divida);
    }

    public List<SelectItem> getServicosSelecionados() {
        if (servicoSelectItem == null) {
            servicoSelectItem = Lists.newArrayList();
            servicoSelectItem.add(new SelectItem(null, ""));
            for (Servico servico : servicoFacade.listarPorOrdemAlfabetica()) {
                servicoSelectItem.add(new SelectItem(servico, servico.getToStringAutoComplete()));
            }
        }
        return servicoSelectItem;
    }

    public void adicionarServico() {
        Util.adicionarObjetoEmLista(filtro.getServicos(), servico);
        servico = null;
    }

    public void removerServico(Servico servico) {
        filtro.getServicos().remove(servico);
    }

}
