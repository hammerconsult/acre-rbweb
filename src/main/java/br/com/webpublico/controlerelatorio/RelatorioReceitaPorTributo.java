/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.negocios.BancoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author gustavo
 */
@ManagedBean(name = "relatorioReceitaPorTributo")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReceitaPorTributo", pattern = "/tributario/cobrancaadministrativa/arrecadacaotributo/", viewId = "/faces/tributario/arrecadacao/relatorios/receitaportributo.xhtml"),
})
public class RelatorioReceitaPorTributo extends AbstractReport {

    private Banco filtroBanco;
    private Date dataInicialPagamento;
    private Date dataFinalPagamento;
    private Date dataInicialFinanceira;
    private Date dataFinalFinanceira;
    @EJB
    private BancoFacade bancoFacade;
    private Converter converterBanco;
    private Integer grupo;

    @URLAction(mappingId = "novoReceitaPorTributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroBanco = null;
        dataFinalFinanceira = null;
        dataInicialFinanceira = null;
        dataInicialPagamento = null;
        dataFinalPagamento = null;
    }

    public Converter getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public Date getDataFinalFinanceira() {
        return dataFinalFinanceira;
    }

    public void setDataFinalFinanceira(Date dataFinalFinanceira) {
        this.dataFinalFinanceira = dataFinalFinanceira;
    }

    public Date getDataFinalPagamento() {
        return dataFinalPagamento;
    }

    public void setDataFinalPagamento(Date dataFinalPagamento) {
        this.dataFinalPagamento = dataFinalPagamento;
    }

    public Date getDataInicialFinanceira() {
        return dataInicialFinanceira;
    }

    public void setDataInicialFinanceira(Date dataInicialFinanceira) {
        this.dataInicialFinanceira = dataInicialFinanceira;
    }

    public Date getDataInicialPagamento() {
        return dataInicialPagamento;
    }

    public void setDataInicialPagamento(Date dataInicialPagamento) {
        this.dataInicialPagamento = dataInicialPagamento;
    }

    public Banco getFiltroBanco() {
        return filtroBanco;
    }

    public void setFiltroBanco(Banco filtroBanco) {
        this.filtroBanco = filtroBanco;
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public Integer getGrupo() {
        return grupo;
    }

    public void setGrupo(Integer grupo) {
        this.grupo = grupo;
    }

    public void imprime() {
        if (filtroBanco == null
            && dataInicialFinanceira == null
            && dataFinalFinanceira == null
            && dataInicialPagamento == null
            && dataFinalPagamento == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe ao menos um filtro."));

        } else if (dataInicialFinanceira == null && dataFinalFinanceira != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe a data Inicial"));
        } else {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            String juncao = " and ";
            StringBuilder where = new StringBuilder();
            String caminhoBrasao = getCaminhoImagem();

            if (filtroBanco != null && filtroBanco.getId() != null) {
                where.append(juncao).append(" banco_id = ").append(filtroBanco.getId());
                juncao = " and ";
            }
            if (dataInicialFinanceira != null) {
                where.append(juncao).append(" lote.datafinanciamento >= to_date('").append(formataData(dataInicialFinanceira)).append("','dd/MM/yyyy')");
                juncao = " and ";
            }
            if (dataFinalFinanceira != null) {
                where.append(juncao).append(" lote.datafinanciamento <= to_date('").append(formataData(dataFinalFinanceira)).append("','dd/MM/yyyy')");
                juncao = " and ";
            }
            if (dataInicialPagamento != null) {
                where.append(juncao).append(" lote.datapagamento >= to_date(").append(formataData(dataInicialPagamento)).append(",'dd/MM/yyyy')");
                juncao = " and ";
            }
            if (dataFinalPagamento != null) {
                where.append(juncao).append(" lote.datapagamento <= to_date(").append(formataData(dataFinalPagamento)).append(",'dd/MM/yyyy')");
                juncao = " and ";
            }

            SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
            HashMap parameters = new HashMap();
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("BRASAO", caminhoBrasao);
            //System.out.println("condicao...: " + where.toString());
            parameters.put("WHERE", where.toString());
            parameters.put("SUBREPORT_DIR", subReport);
            if (dataInicialFinanceira != null) {
                parameters.put("DATAINICIALDESC", "e data inicial");
                parameters.put("DATAINICIAL", formataData.format(dataInicialFinanceira));
            } else {
                parameters.put("DATAINICIALDESC", "");
                parameters.put("DATAINICIAL", "");
            }

            if (dataFinalFinanceira != null) {
                parameters.put("DATAFINALDESC", "à");
                parameters.put("DATAFINAL", formataData.format(dataFinalFinanceira));
            } else {
                parameters.put("DATAFINALDESC", "");
                parameters.put("DATAFINAL", "");
            }
            //System.out.println("grupo" + grupo);
            try {
                gerarRelatorio("ReceitaPorTributo.jasper", parameters);
            } catch (JRException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }

    public String formataData(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(data);
    }
}
