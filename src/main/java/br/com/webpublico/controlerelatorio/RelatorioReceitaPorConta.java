/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.BancoContaConfTributario;
import br.com.webpublico.negocios.BancoFacade;
import br.com.webpublico.negocios.LoteBaixaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gustavo
 */
@ManagedBean(name = "relatorioReceitaPorConta")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoReceitaPorConta", pattern = "/faces/tributario/cobrancaadministrativa/arrecadacaocontareceita/", viewId = "/faces/tributario/arrecadacao/relatorios/receitaporconta.xhtml"),
})
public class RelatorioReceitaPorConta extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioReceitaPorConta.class);

    private Date dataInicialPagamento;
    private Date dataFinalPagamento;
    private Date dataInicialFinanceira;
    private Date dataFinalFinanceira;
    private String contaReceitaReduzidoInicio;
    private String contaReceitaReduzidoFinal;
    private String contaReceita;
    private Banco filtroBanco;
    private BancoContaConfTributario conta;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    private Converter converterBanco;
    private transient Converter converterConta;

    @URLAction(mappingId = "novoReceitaPorConta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        conta = null;
        filtroBanco = null;
        dataFinalFinanceira = null;
        dataInicialFinanceira = null;
        dataInicialPagamento = null;
        dataFinalPagamento = null;
        contaReceitaReduzidoInicio = null;
        contaReceitaReduzidoFinal = null;
        contaReceita = null;
    }

    public Converter getConverterBanco() {
        if (converterBanco == null) {
            converterBanco = new ConverterAutoComplete(Banco.class, bancoFacade);
        }
        return converterBanco;
    }

    public Converter getConverterConta() {
        if (converterConta == null) {
            converterConta = new Converter() {
                @Override
                public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                    if (string == null || string.isEmpty()) {
                        return null;
                    }
                    return bancoFacade.recuperar(BancoContaConfTributario.class, Long.parseLong(string));
                }

                @Override
                public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                    if (o == null) {
                        return null;
                    } else {
                        return String.valueOf(((BancoContaConfTributario) o).getId());
                    }
                }
            };
        }
        return converterConta;
    }

    public List<SelectItem> getContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<BancoContaConfTributario> lista = loteBaixaFacade.recuperaContasConfiguracao();
        Collections.sort(lista, new Comparator<BancoContaConfTributario>() {
            @Override
            public int compare(BancoContaConfTributario o1, BancoContaConfTributario o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (BancoContaConfTributario object : lista) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public BancoContaConfTributario getConta() {
        return conta;
    }

    public void setConta(BancoContaConfTributario conta) {
        this.conta = conta;
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

    public String getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(String contaReceita) {
        this.contaReceita = contaReceita;
    }

    public String getContaReceitaReduzidoFinal() {
        return contaReceitaReduzidoFinal;
    }

    public void setContaReceitaReduzidoFinal(String contaReceitaReduzidoFinal) {
        this.contaReceitaReduzidoFinal = contaReceitaReduzidoFinal;
    }

    public String getContaReceitaReduzidoInicio() {
        return contaReceitaReduzidoInicio;
    }

    public void setContaReceitaReduzidoInicio(String contaReceitaReduzidoInicio) {
        this.contaReceitaReduzidoInicio = contaReceitaReduzidoInicio;
    }

    public List<Banco> completaBanco(String parte) {
        return bancoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void imprime() {
        if (conta == null && filtroBanco == null
            && dataInicialFinanceira == null
            && dataFinalFinanceira == null
            && dataInicialPagamento == null
            && dataFinalPagamento == null
            && (contaReceita == null || contaReceita.trim().length() <= 0)
            && (contaReceitaReduzidoInicio == null || contaReceitaReduzidoInicio.trim().length() <= 0)
            && (contaReceitaReduzidoFinal == null || contaReceitaReduzidoFinal.trim().length() <= 0)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe ao menos um filtro."));

        } else {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            String juncao = " and ";
            StringBuilder where = new StringBuilder();
            StringBuilder whereSubReport = new StringBuilder();
            String caminhoBrasao = getCaminhoImagem();

            if (conta != null && conta.getId() != null) {
                where.append(juncao).append(" banco.id = ").append(conta.getSubConta().getContaBancariaEntidade().getAgencia().getBanco().getId());
                juncao = " and ";
            }
            if (dataInicialFinanceira != null) {
                where.append(juncao).append(" to_char(lote.datafinanciamento,'dd/MM/yyyy') >= ").append(formataData(dataInicialFinanceira));
                juncao = " and ";
            }
            if (dataFinalFinanceira != null) {
                where.append(juncao).append(" to_char(lote.datafinanciamento,'dd/MM/yyyy') <= ").append(formataData(dataFinalFinanceira));
                juncao = " and ";
            }
            if (dataInicialPagamento != null) {
                where.append(juncao).append(" to_char(lote.datapagamento,'dd/MM/yyyy')  >= ").append(formataData(dataInicialPagamento));
                juncao = " and ";
            }
            if (dataFinalPagamento != null) {
                where.append(juncao).append(" to_char(lote.datapagamento,'dd/MM/yyyy') <= ").append(formataData(dataFinalPagamento));
                juncao = " and ";
            }
            if (contaReceitaReduzidoInicio != null && !contaReceitaReduzidoInicio.trim().isEmpty()) {
                whereSubReport.append(juncao).append(" ( receitaexercicio.codigoreduzido >= ").append(contaReceitaReduzidoInicio);
                whereSubReport.append(" or receitadividaativa.codigoreduzido >= ").append(contaReceitaReduzidoInicio).append(" )");
            }
            if (contaReceitaReduzidoFinal != null && !contaReceitaReduzidoFinal.trim().isEmpty()) {
                whereSubReport.append(juncao).append(" (  receitaexercicio.codigoreduzido <= ").append(contaReceitaReduzidoFinal);
                whereSubReport.append(" or receitadividaativa.codigoreduzido <= ").append(contaReceitaReduzidoFinal).append(" )");
            }
            if (contaReceita != null && !contaReceita.trim().isEmpty()) {
                whereSubReport.append(juncao).append(" (  upper(contaExercicio.codigo) like '%").append(contaReceita).append("%'");
                whereSubReport.append(juncao).append(" upper(contadividaativa.codigo) like '% ").append(contaReceita).append("%' )");
            }

            HashMap parameters = new HashMap();
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("BRASAO", caminhoBrasao);
            parameters.put("WHERE", where.toString());
            parameters.put("WHERESUBREPORT", whereSubReport.toString());
            parameters.put("SUBREPORT_DIR", subReport);
            try {
                gerarRelatorio("ReceitaPorConta.jasper", parameters);
            } catch (JRException ex) {
                logger.error("Erro: ", ex);
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public String formataData(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return "'" + formatador.format(data) + "'";
    }
}
