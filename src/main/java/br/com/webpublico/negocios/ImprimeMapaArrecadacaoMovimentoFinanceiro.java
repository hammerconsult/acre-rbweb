/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.SubConta;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;

/**
 * @author magraowar
 */
public class ImprimeMapaArrecadacaoMovimentoFinanceiro extends AbstractReport {

    private StringBuilder semDados;
    private StringBuilder filtros;
    private StringBuilder where;
    private Banco filtroBanco;
    private Date dataInicialPagamento;
    private Date dataFinalPagamento;
    private String contaReceitaInicial;
    private String contaReceitaFinal;
    private String codigoReduzidoInicial;
    private String codigoReduzidoFinal;
    private String ordenacao;
    private String ordemSql;
    private String login;
    private SubConta subConta;

    public void imprime() throws JRException, IOException {
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();
        semDados = new StringBuilder("NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
        filtros = new StringBuilder();
        String arquivoJasper = "MapaArrecadacaoDoMovimentoFinanceiro.jasper";
        HashMap parameters = new HashMap();
        montaCondicao();
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("USUARIO", login);
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTROS", filtros.toString());
        parameters.put("ORDER", ordemSql);
        gerarRelatorio(arquivoJasper, parameters);
    }

    public ImprimeMapaArrecadacaoMovimentoFinanceiro(Banco filtroBanco,
            SubConta subConta,
            Date dataInicialPagamento,
            Date dataFinalPagamento,
            String contaReceitaInicial,
            String contaReceitaFinal,
            String codigoReduzidoInicial,
            String codigoReduzidoFinal,
            String ordenacao,
            String ordemSql,
            String login) {
        this.filtroBanco = filtroBanco;
        this.dataInicialPagamento = dataInicialPagamento;
        this.dataFinalPagamento = dataFinalPagamento;
        this.contaReceitaInicial = contaReceitaInicial;
        this.contaReceitaFinal = contaReceitaFinal;
        this.codigoReduzidoInicial = codigoReduzidoInicial;
        this.codigoReduzidoFinal = codigoReduzidoFinal;
        this.ordenacao = ordenacao;
        this.ordemSql = ordemSql;
        this.login = login;
        this.subConta = subConta;
        where = new StringBuilder();
    }

    public ImprimeMapaArrecadacaoMovimentoFinanceiro(Banco filtroBanco, Date dataInicialPagamento, Date dataFinalPagamento, String login) {
        this.filtroBanco = filtroBanco;
        this.dataInicialPagamento = dataInicialPagamento;
        this.dataFinalPagamento = dataFinalPagamento;
        contaReceitaInicial = new String();
        contaReceitaFinal = new String();
        codigoReduzidoInicial = new String();
        codigoReduzidoFinal = new String();
        this.login = login;
        where = new StringBuilder();
        ordenacao = "D";
    }

    public String formataData(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return "'" + formatador.format(data) + "'";
    }

    private void montaCondicao() {

        String juncao = " where ";
        ordemSql = "";

        if (filtroBanco != null && filtroBanco.getId() != null) {
            this.where.append(juncao).append(" banco_id = ").append(filtroBanco.getId());
            juncao = " and ";
            filtros.append(" Banco = ").append(filtroBanco.getDescricao()).append("; ");
        }
        if (subConta != null && subConta.getId() != null) {
            this.where.append(juncao).append(" idSubconta = ").append(subConta.getId());
            juncao = " and ";
            filtros.append(" Conta = ").append(subConta.getContaBancariaEntidade().getNumeroConta()).append("; ");
        }
        if (dataInicialPagamento != null) {
            this.where.append(juncao).append(" datapagamento >= to_date(").append(formataData(dataInicialPagamento)).append(",'dd/MM/yyyy')");
            juncao = " and ";
        }
        if (dataFinalPagamento != null) {
            this.where.append(juncao).append(" datapagamento <= to_date(").append(formataData(dataFinalPagamento)).append(",'dd/MM/yyyy')");
            juncao = " and ";
            filtros.append(" Data de Pagamento entre ").append(formataData(dataInicialPagamento)).append(" e ").append(formataData(dataFinalPagamento)).append("; ");
        }
        if (!contaReceitaInicial.isEmpty()) {
            this.where.append(juncao).append(" contareceita >= ").append(contaReceitaInicial);
            juncao = " and ";
        }
        if (!contaReceitaFinal.isEmpty()) {
            this.where.append(juncao).append(" contareceita <= ").append(contaReceitaFinal);
            juncao = " and ";
            filtros.append(" Conta de Receita entre ").append(contaReceitaInicial).append(" e ").append(contaReceitaFinal).append("; ");
        }
        if (!codigoReduzidoInicial.isEmpty()) {
            this.where.append(juncao).append(" codigotributo >= ").append(codigoReduzidoInicial);
            juncao = " and ";
        }
        if (!codigoReduzidoFinal.isEmpty()) {
            this.where.append(juncao).append(" codigotributo <= ").append(codigoReduzidoFinal);
            juncao = " and ";
            filtros.append(" Código Reduzido entre ").append(codigoReduzidoInicial).append(" e ").append(codigoReduzidoFinal).append("; ");
        }

        switch (ordenacao) {
            case "B":
                ordemSql = " banco ";
                break;
            case "D":
                ordemSql = " datapagamento";
                break;
            case "C":
                ordemSql = " contareceita";
                break;
            case "R":
                ordemSql = " codigotributo";
                break;
            default:
                break;
        }
        if (!ordemSql.equals("")) {
            ordemSql = " order by " + ordemSql;
        } else if (ordemSql.equals("")) {
            ordemSql = " order by codigotributo ";
        }
    }
}
