/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TipoAfastamento;
import br.com.webpublico.enums.rh.StatusAfastamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.TipoAfastamentoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */
@ManagedBean(name = "relatorioAfastamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioAfastamento", pattern = "/relatorio/afastamento/", viewId = "/faces/rh/relatorios/relatorioafastamento.xhtml")
})
public class RelatorioAfastamentoControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ContratoFP contratoFP;

    private Date dataInicial;
    private Date dataFinal;

    private TipoAfastamento tipoAfastamentoSelecionado;
    @EJB
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    private String filtrosUtilizados;
    private boolean bloqueiaMatriculaFP;
    private boolean bloqueiaHierarquia;
    private StatusAfastamento statusAfastamento;
    private TipoPesquisa tipoPesquisa;
    private boolean apenasAfastamentosVencidos;

    public RelatorioAfastamentoControlador() {
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public TipoPesquisa getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(TipoPesquisa tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), "ADMINISTRATIVA", UtilRH.getDataOperacao());
    }

    public List<ContratoFP> completarContratosFP(String filtro) {
        return contratoFPFacade.buscaContratoFPFiltrandoAtributosVinculoMatriculaFP(filtro.trim());
    }

    public TipoAfastamento getTipoAfastamentoSelecionado() {
        return tipoAfastamentoSelecionado;
    }

    public void setTipoAfastamentoSelecionado(TipoAfastamento tipoAfastamentoSelecionado) {
        this.tipoAfastamentoSelecionado = tipoAfastamentoSelecionado;
    }

    public boolean isBloqueiaMatriculaFP() {
        return bloqueiaMatriculaFP;
    }

    public void setBloqueiaMatriculaFP(boolean bloqueiaMatriculaFP) {
        this.bloqueiaMatriculaFP = bloqueiaMatriculaFP;
    }

    public boolean isBloqueiaHierarquia() {
        return bloqueiaHierarquia;
    }

    public void setBloqueiaHierarquia(boolean bloqueiaHierarquia) {
        this.bloqueiaHierarquia = bloqueiaHierarquia;
    }

    public List<SelectItem> getTiposDeAfastamento() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoAfastamento tipo : tipoAfastamentoFacade.lista()) {
            retorno.add(new SelectItem(tipo, tipo.toString()));
        }
        return retorno;
    }

    public List<TipoPesquisa> getTiposDePesquisa() {
        List<TipoPesquisa> toReturn = Lists.newArrayList();
        for (TipoPesquisa tipoPesquisa : TipoPesquisa.values()) {
            toReturn.add(tipoPesquisa);
        }
        return toReturn;
    }

    @URLAction(mappingId = "gerarRelatorioAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacional = null;
        contratoFP = null;
        tipoAfastamentoSelecionado = null;
        tipoPesquisa = TipoPesquisa.UNIDADE_ORGANIZACIONAL;
        this.bloqueiaHierarquia = false;
        this.bloqueiaMatriculaFP = false;
        statusAfastamento = StatusAfastamento.SERVIDOR_CONTINUA_AFASTADO;
        dataInicial = null;
        dataFinal = null;
        apenasAfastamentosVencidos = false;
    }

    public void limparUnidadeAndServidor() {
        hierarquiaOrganizacional = null;
        contratoFP = null;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null && contratoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Unidade Organizacional ou o Servidor.");
        }

        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Período Inicial.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Período Final.");
        }
        if (dataInicial != null && dataFinal != null) {
            if (dataInicial.compareTo(dataFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Período Inicial deve ser anterior ao Período Final.");
            }
        }

        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE AFASTAMENTO");
            dto.adicionarParametro("condicao", buscarClausulaWhere());
            dto.adicionarParametro("FILTROS", filtrosUtilizados);
            dto.adicionarParametro("DATA", "to_date(" + DataUtil.getDataFormatada(UtilRH.getDataOperacao()) + ", dd/MM/yyyy)");
            dto.setNomeRelatorio("RELATÓRIO-DE-AFASTAMENTO");
            dto.setApi("rh/afastamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro ao gerar relatório de Afastamento: {} " + ex);
        }

    }

    private String buscarClausulaWhere() {
        filtrosUtilizados = "";
        String retorno = " where to_date('" + UtilRH.getDataOperacaoFormatada() + "' , 'dd/MM/yyyy') between ho.iniciovigencia " +
            "   and coalesce(ho.fimvigencia, to_date('" + UtilRH.getDataOperacaoFormatada() + "' , 'dd/MM/yyyy')) ";

        if (hierarquiaOrganizacional != null) {
            retorno += " and ho.codigo like '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%' ";
            filtrosUtilizados = "Unidade Organizacional: " + hierarquiaOrganizacional.getCodigo() + " - " + hierarquiaOrganizacional.getSubordinada().getDescricao();
        }

        if (contratoFP != null) {
            retorno += " and contrato.id =  " + contratoFP.getId();
            filtrosUtilizados += " Servidor: " + contratoFP.toString();
        }

        if (tipoAfastamentoSelecionado != null) {
            retorno += " and tipo.id = " + tipoAfastamentoSelecionado.getId();
            filtrosUtilizados += " Tipo de Afastamento: " + tipoAfastamentoSelecionado.getCodigo() + " - " + tipoAfastamentoSelecionado.getDescricao();
        }

        retorno += " and ((trunc(afastamento.inicio) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')"
            + "         or trunc(afastamento.termino) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') "
            + "         or to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') between trunc(afastamento.inicio) and trunc(afastamento.termino) "
            + "         or to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') between trunc(afastamento.inicio) and trunc(afastamento.termino)) or "
            + "         (afastamento.retornoinformado = 0 and trunc(AFASTAMENTO.INICIO) <= to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')))"
            + " and (trunc(vinculo.INICIOVIGENCIA) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') "
            + "         or coalesce(trunc(vinculo.FINALVIGENCIA), to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') "
            + "         or to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') between trunc(vinculo.INICIOVIGENCIA) and coalesce(trunc(vinculo.FINALVIGENCIA), to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')) "
            + "         or to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') between trunc(vinculo.INICIOVIGENCIA) and coalesce(trunc(vinculo.FINALVIGENCIA), to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')))";
        filtrosUtilizados += " - Período: " + DataUtil.getDataFormatada(dataInicial) + " Até " + DataUtil.getDataFormatada(dataFinal);

        if (StatusAfastamento.SERVIDOR_CONTINUA_AFASTADO.equals(statusAfastamento)) {
            retorno += apenasAfastamentosVencidos ? " and afastamento.retornoinformado = 0 and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') > trunc(afastamento.termino) " : " and (to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') <= trunc(afastamento.termino) or afastamento.retornoinformado = 0) ";
            filtrosUtilizados += " - Situação do Afastamento: " + StatusAfastamento.SERVIDOR_CONTINUA_AFASTADO.toString() + (apenasAfastamentosVencidos ? " - Apenas Afastamentos Vencidos: Sim " : "- Apenas Afastamentos Vencidos: Não");
        }

        if (StatusAfastamento.SERVIDOR_RETORNOU_AFASTAMENTO.equals(statusAfastamento)) {
            retorno += " and afastamento.retornoinformado = 1 and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') > trunc(afastamento.termino) ";
            filtrosUtilizados += " - Situação do Afastamento: " + StatusAfastamento.SERVIDOR_RETORNOU_AFASTAMENTO.toString();
        }

        retorno += " order by orgao, ho.codigo, servidor, matricula.matricula, vinculo.numero, afastamento.inicio ";
        return retorno;
    }

    public List<StatusAfastamento> getTipoStatusAfastamento() {
        return Arrays.asList(StatusAfastamento.values());
    }

    public StatusAfastamento getStatusAfastamento() {
        return statusAfastamento;
    }

    public void setStatusAfastamento(StatusAfastamento statusAfastamento) {
        this.statusAfastamento = statusAfastamento;
    }

    public enum TipoPesquisa {

        UNIDADE_ORGANIZACIONAL("Unidade Organizacional"),
        SERVIDOR("Servidor");

        TipoPesquisa(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public boolean isApenasAfastamentosVencidos() {
        return apenasAfastamentosVencidos;
    }

    public void setApenasAfastamentosVencidos(boolean apenasAfastamentosVencidos) {
        this.apenasAfastamentosVencidos = apenasAfastamentosVencidos;
    }
}

