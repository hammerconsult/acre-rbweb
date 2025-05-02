/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.LiquidacaoEstornoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-nota-liquidacao-estorno-intervalo", pattern = "/relatorio/nota-liquidacao-estorno-intervalo/", viewId = "/faces/financeiro/relatorio/relatorionotaliquidacaoestorno.xhtml")
})
@ManagedBean
public class RelatorioNotaLiquidacaoEstornoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private LiquidacaoEstornoFacade liquidacaoEstornoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<HierarquiaOrganizacional> listaUnidades;
    private Date dataInicial;
    private Date dataFinal;
    private String numeroInicial;
    private String numeroFinal;

    private String concatenaParametros() {
        StringBuilder stb = new StringBuilder();
        stb.append(" and trunc(NOTA.dataestorno) between to_date('").append(DataUtil.getDataFormatada(dataInicial)).append("' ,'dd/MM/yyyy') and to_date('").append(DataUtil.getDataFormatada(dataFinal)).append("','dd/MM/yyyy')");
        if (!this.numeroInicial.equals("") && !this.numeroFinal.equals("")) {
            stb.append(" and NOTA.numero between '").append(this.numeroInicial).append("' and '").append(this.numeroFinal).append("'");
        }
        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                concatena = ",";
            }
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        } else {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            StringBuilder idUnidades = new StringBuilder();
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                concatena = ", ";
            }
            stb.append(" and VW.SUBORDINADA_ID IN (").append(idUnidades).append(")");
        }
        return stb.toString();
    }

    @URLAction(mappingId = "relatorio-nota-liquidacao-estorno-intervalo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dataInicial = DataUtil.montaData(1, 0, sistemaFacade.getExercicioCorrente().getAno()).getTime();
        this.dataFinal = new Date();
        this.numeroInicial = null;
        this.numeroFinal = null;
        this.listaUnidades = new ArrayList<>();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial é maior que a Data Final");
        }
        if (!"".equals(numeroInicial.trim()) && "".equals(numeroFinal.trim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Número final");
        }
        if ("".equals(numeroInicial.trim()) && !"".equals(numeroFinal.trim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Número inicial");
        }
        ve.lancarException();
    }

    public void gerarRelatorioFiltro() {
        try {
            validarCampos();
            List<NotaExecucaoOrcamentaria> notas = liquidacaoEstornoFacade.buscarNotaLiquidacaoEstorno(concatenaParametros(), CategoriaOrcamentaria.NORMAL);
            documentoOficialFacade.gerarRelatorioDasNotasOrcamentarias(notas, ModuloTipoDoctoOficial.NOTA_ESTORNO_LIQUIDACAO);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public LiquidacaoEstornoFacade getLiquidacaoEstornoFacade() {
        return liquidacaoEstornoFacade;
    }

    public void setLiquidacaoEstornoFacade(LiquidacaoEstornoFacade liquidacaoEstornoFacade) {
        this.liquidacaoEstornoFacade = liquidacaoEstornoFacade;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }
}
