package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioContrato;
import br.com.webpublico.entidadesauxiliares.RelatorioContrato;
import br.com.webpublico.enums.TipoAquisicaoContrato;
import br.com.webpublico.enums.TipoContrato;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.RelatorioContratoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relContrato", pattern = "/relatorio-contrato/", viewId = "/faces/administrativo/relatorios/relatorio-contrato.xhtml")
})
public class RelatorioContratoControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioContratoControlador.class);
    @EJB
    private RelatorioContratoFacade facade;
    private FiltroRelatorioContrato filtroRelatorio;
    private List<RelatorioContrato> contratos;
    private RelatorioContrato contrato;
    private String filtros;

    @URLAction(mappingId = "relContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        limparDadosConsulta();
        novoFiltro();
    }

    public void novoFiltro() {
        filtroRelatorio = new FiltroRelatorioContrato();
        filtroRelatorio.setTipoAquisicaoContrato(TipoAquisicaoContrato.LICITACAO);
    }

    public void selecionarTodos() {
        for (RelatorioContrato contrato : contratos) {
            contrato.setSelecionado(filtroRelatorio.getSelecionarTodos());
        }
    }

    public List<Contrato> completarContrato(String parte) {
        return facade.getContratoFacade().buscarContratos(parte.trim());
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacoesComItensHomologadosPorUnidadesUsuario(parte.trim());
    }

    public List<DispensaDeLicitacao> completarDispensaLicitacao(String parte) {
        return facade.getDispensaDeLicitacaoFacade().buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumoAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), Boolean.TRUE);
    }

    public List<RegistroSolicitacaoMaterialExterno> completarRegistroDePrecoExterno(String parte) {
        return facade.getRegistroSolicitacaoMaterialExternoFacade().buscarRegistroSolicitacaoMaterialExternoPorNumeroOrExercicioAndUsuarioAndGestor(parte.trim(), facade.getSistemaFacade().getUsuarioCorrente(), Boolean.TRUE);
    }

    public List<HierarquiaOrganizacional> completarUnidadeAdministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public List<SelectItem> getTiposAquisicaoContrato() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoAquisicaoContrato object : TipoAquisicaoContrato.values()) {
            if (!TipoAquisicaoContrato.COMPRA_DIRETA.equals(object) && !TipoAquisicaoContrato.CONTRATOS_VIGENTE.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposContrato() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoContrato object : TipoContrato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void limparDadosPorTipoAquisicao() {
        filtroRelatorio.setLicitacao(null);
        filtroRelatorio.setDispensaLicitacao(null);
        filtroRelatorio.setRegistroPrecoExterno(null);
    }

    public void limparDadosConsulta() {
        contratos = Lists.newArrayList();
        novoFiltro();
    }

    public void voltar() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(FacesUtil.getRequestContextPath() + "/home");
        } catch (IOException e) {
            logger.error("Erro ao voltar home{}", e);
        }
    }

    public void redirecionarParaContrato(Long idContrato) {
        FacesUtil.redirecionamentoInterno("/contrato-adm/ver/" + idContrato + "/");
    }

    public void redirecionarParaAditivo(Long idAditivo) {
        FacesUtil.redirecionamentoInterno("/aditivo-contrato/ver/" + idAditivo + "/");
    }

    public void redirecionarParaExecucao(Long idExecucao) {
        FacesUtil.redirecionamentoInterno("/execucao-contrato-adm/ver/" + idExecucao + "/");
    }

    private Date getDataReferencia() {
        if (filtroRelatorio.getInicioVigencia() != null) {
            return montarDataFinal(filtroRelatorio.getInicioVigencia());
        }
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void buscarContratos() {
        try {
            validarCamposObrigatorio();
            contratos = facade.buscarContratos(montarClausulaWhere(), DataUtil.getDataFormatada(getDataReferencia()));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public boolean hasContratos() {
        return contratos != null && !contratos.isEmpty();
    }

    public BigDecimal getValorTotalContratos() {
        BigDecimal total = BigDecimal.ZERO;
        for (RelatorioContrato item : contratos) {
            total = total.add(item.getValorContrato());
        }
        return total;
    }

    private void validarRelatorio() {
        validarCamposObrigatorio();
        ValidacaoException ve = new ValidacaoException();
        if (!hasContratos()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para gerar o relatório é necessário buscar os contratos antes.");
        }
        ve.lancarException();
        boolean selecionado = false;
        for (RelatorioContrato contrato : contratos) {
            if (contrato.getSelecionado()) {
                selecionado = true;
            }
        }
        if (!selecionado) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para gerar o relatório é obrigatório selecionar ao menos um contrato.");
        }
        ve.lancarException();
    }

    private void validarCamposObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getUnidadeAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade administrativa deve ser informado.");
        }
        ve.lancarException();
    }

    private String montarClausulaWhere() {
        String retorno = " and uc.unidadeadministrativa_id = " + filtroRelatorio.getUnidadeAdministrativa().getSubordinada().getId();
        filtros = "Unidade Administrativa: " + filtroRelatorio.getUnidadeAdministrativa() + ";";
        if (filtroRelatorio.getTipoContrato() != null) {
            retorno += " and cont.tipocontrato = '" + filtroRelatorio.getTipoContrato().name() + "'";
            filtros += "Tipo de Contrato: " + filtroRelatorio.getTipoContrato().getDescricao() + "; ";
        }
        if (filtroRelatorio.getTipoAquisicaoContrato() != null) {
            retorno += " and cont.tipoaquisicao = '" + filtroRelatorio.getTipoAquisicaoContrato().name() + "'";
        }
        if (filtroRelatorio.getLicitacao() != null) {
            retorno += " and lic.id = " + filtroRelatorio.getLicitacao().getId();
            filtros += "Licitação: " + filtroRelatorio.getLicitacao().getNumeroAnoLicitacao() + "; ";
        }
        if (filtroRelatorio.getDispensaLicitacao() != null) {
            retorno += " and disp.id = " + filtroRelatorio.getDispensaLicitacao().getId();
            filtros += "Dispensa de Licitação: " + filtroRelatorio.getDispensaLicitacao().getNumeroDaDispensa() + "; ";
        }
        if (filtroRelatorio.getRegistroPrecoExterno() != null) {
            retorno += " and reg.id = " + filtroRelatorio.getRegistroPrecoExterno().getId();
            filtros += "Registro de Preço Externo: " + filtroRelatorio.getRegistroPrecoExterno().getNumeroRegistro() + "; ";
        }
        if (filtroRelatorio.getContrato() != null) {
            retorno += " and cont.id = " + filtroRelatorio.getContrato().getId();
            filtros += "Contrato: " + filtroRelatorio.getContrato().getNumeroAnoTermo() + "; ";
        }
        if (filtroRelatorio.getResponsavelUnidade() != null) {
            retorno += " and cfp.id = " + filtroRelatorio.getResponsavelUnidade().getId();
            filtros += "Responsável pela Unidade: " + filtroRelatorio.getResponsavelUnidade().getContratoFP().getMatriculaFP().getPessoa() + "; ";
        }
        if (filtroRelatorio.getInicioVigencia() != null && filtroRelatorio.getFimVigencia() != null) {
            Date dataFinal = montarDataFinal(filtroRelatorio.getFimVigencia());
            retorno += " and cont.iniciovigencia between to_date('" + DataUtil.getDataFormatada(filtroRelatorio.getInicioVigencia(), "MM/yyyy") + "', 'MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal, "MM/yyyy") + "', 'MM/yyyy') ";
            filtros += "Período Vigência: " + DataUtil.getDataFormatada(filtroRelatorio.getInicioVigencia()) + " até " + DataUtil.getDataFormatada(dataFinal) + "; ";
        }
        if (filtroRelatorio.getInicioExecucao() != null && filtroRelatorio.getFimExecucao() != null) {
            Date dataFinal = montarDataFinal(filtroRelatorio.getFimExecucao());
            retorno += " and cont.inicioexecucao between to_date('" + DataUtil.getDataFormatada(filtroRelatorio.getInicioExecucao(), "MM/yyyy") + "', 'MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal, "MM/yyyy") + "', 'MM/yyyy') ";
            filtros += "Período Execução: " + DataUtil.getDataFormatada(filtroRelatorio.getInicioExecucao()) + " até " + DataUtil.getDataFormatada(dataFinal) + "; ";
        }
        filtros = filtros.substring(0, filtros.length() - 2);
        return retorno;
    }

    private Date montarDataFinal(Date data) {
        String mesDaData = Util.getMesDaData(data);
        int ano = DataUtil.getAno(data);
        int mes = Integer.valueOf(mesDaData);
        int dia = DataUtil.ultimoDiaDoMes(mes);
        return Util.montarData(dia, mes - 1, ano);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE CONTRATO");
            dto.adicionarParametro("contratos", RelatorioContrato.contratosToDto(getContratosSelecionados()));
            dto.setNomeRelatorio("RELATÓRIO DE CONTRATO");
            dto.setApi("administrativo/contrato/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<RelatorioContrato> getContratosSelecionados() {
        List<RelatorioContrato> list = Lists.newArrayList();
        for (RelatorioContrato contrato : contratos) {
            if (contrato.getSelecionado()) {
                list.add(contrato);
            }
        }
        return list;
    }

    public BigDecimal getValorTotalAtualContrato() {
        BigDecimal total = BigDecimal.ZERO;
        for (RelatorioContrato contrato : contratos) {
            total = total.add(contrato.getValorAtualContrato());
        }
        return total;
    }

    public BigDecimal getValorTotalEmpenhado() {
        BigDecimal total = BigDecimal.ZERO;
        for (RelatorioContrato contrato : contratos) {
            total = total.add(contrato.getValorEmpenhado());
        }
        return total;
    }


    public void selecionarContrato(RelatorioContrato contrato) {
        this.contrato = contrato;
    }

    public FiltroRelatorioContrato getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioContrato filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }

    public List<RelatorioContrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<RelatorioContrato> contratos) {
        this.contratos = contratos;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public RelatorioContrato getContrato() {
        return contrato;
    }

    public void setContrato(RelatorioContrato contrato) {
        this.contrato = contrato;
    }
}
