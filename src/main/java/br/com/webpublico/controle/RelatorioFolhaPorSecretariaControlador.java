/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LoteProcessamento;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.LoteProcessamentoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author peixe
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "gerarRelatorioFolhaPorSecretariaApartirDoCalculoDaFolha", pattern = "/relatorio/folha-por-secretaria/gerar/", viewId = "/faces/rh/relatorios/relatoriofolhaporsecretaria.xhtml")
})
public class RelatorioFolhaPorSecretariaControlador extends AbstractReport implements Serializable {

    private Integer mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterHierarquia;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<VinculoFP> vinculos;
    private LoteProcessamento loteProcessamento;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;

    public RelatorioFolhaPorSecretariaControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    @URLAction(mappingId = "gerarRelatorioFolhaPorSecretariaApartirDoCalculoDaFolha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void gerarRelatorio() throws JRException, IOException {
        if (recuperarInformacoesUrl()) {
            String arquivoJasper = "RelatorioFolhaPorSecretaria.jasper";
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            String imagem = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/img");
            imagem += "/Brasao_de_Rio_Branco.gif";

            HashMap parameters = new HashMap();

            parameters.put("IMAGEM", imagem);
            parameters.put("MES", (mes - 1));
            parameters.put("ANO", ano);
            parameters.put("DESCRICAO_HO", hierarquiaOrganizacionalSelecionada.getCodigo() + " - " + hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            parameters.put("UNIDADE_ID", hierarquiaOrganizacionalSelecionada.getSubordinada().getId());
            parameters.put("LOTACAO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
            parameters.put("DESCRICAO_HO", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            parameters.put("SUB", subReport);
            parameters.put("TIPOFOLHA", tipoFolhaDePagamento.name());
            parameters.put("CONTRATOS", montaIdsContratos());
            parameters.put("USER", new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " - " + sistemaControlador.getUsuarioCorrente().getLogin());


            gerarRelatorio(arquivoJasper, parameters);
        }
    }

    private String montaIdsContratos() {
        String retorno = "";
        if (loteProcessamento != null){
            vinculos = new ArrayList<>();
            vinculos = loteProcessamentoFacade.findVinculosByLote(loteProcessamento);
            if (!vinculos.isEmpty()) {
                String ids = " and contrato.id in ( ";
                for (VinculoFP vinculo : vinculos) {
                    ids += vinculo.getId() + ",";
                }
                retorno = ids.substring(0, ids.length() - 1) + ")";
                ;
            }
        }

        return retorno;
    }


    public void gerarRelatorioFolhaDePagamento() {
        try {
            if (validaCampos()) {

                String arquivoJasper = "RelatorioFolhaPorSecretaria.jasper";
                String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
                subReport += "/report/";
                String imagem = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/img");
                imagem += "/Brasao_de_Rio_Branco.gif";

                HashMap parameters = new HashMap();

                parameters.put("IMAGEM", imagem);
                parameters.put("MES", (mes - 1));
                parameters.put("ANO", ano);
                parameters.put("DESCRICAO_HO", hierarquiaOrganizacionalSelecionada.getCodigo() + " - " + hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
                parameters.put("UNIDADE_ID", hierarquiaOrganizacionalSelecionada.getSubordinada().getId());
                parameters.put("LOTACAO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
                parameters.put("DESCRICAO_HO", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
                parameters.put("SUB", subReport);
                parameters.put("CONTRATOS", "");
                parameters.put("TIPOFOLHA", tipoFolhaDePagamento.name());
                parameters.put("USER", new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " - " + sistemaControlador.getUsuarioCorrente().getLogin());

                gerarRelatorio(arquivoJasper, parameters);
            }
        }catch (Exception ex){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório " ));
        }
    }

    private boolean recuperarInformacoesUrl() {
        String mes = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("mes");
        String ano = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ano");
        String tipofolha = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tipo");
        String orgao = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("orgao");
        String lote = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("lote");
        if (mes != null && ano != null && tipofolha != null && orgao != null && lote != null) {
            this.mes = Integer.parseInt(mes);
            this.ano = Integer.parseInt(ano);
            this.tipoFolhaDePagamento = TipoFolhaDePagamento.valueOf(tipofolha);
            hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(Long.parseLong(orgao));
            this.loteProcessamento = loteProcessamentoFacade.recuperar(Long.parseLong(lote));
            return true;
        }
        return false;
    }

    public boolean validaCampos() {
        boolean toReturn = true;
        if (mes == null) {
            FacesUtil.addCampoObrigatorio("O campo Mês é obrigatório");
            toReturn = false;
        }

        if (ano == null || (ano != null && ano == 0)) {
            FacesUtil.addCampoObrigatorio("O campo Ano é obrigatório");
            toReturn = false;
        }

        if (mes != null && (mes < 1 || mes > 12)) {
            FacesUtil.addCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
            toReturn = false;
        }

        if (hierarquiaOrganizacionalSelecionada == null) {
            FacesUtil.addCampoObrigatorio("O campo Órgão é obrigatório");
            toReturn = false;
        }
        return toReturn;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        //hos.add(hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao()));
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao()));
        return hos;
    }

    public void limpaCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        mes = null;
        ano = null;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }
}
