/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidades.Lote;
import br.com.webpublico.entidades.Quadra;
import br.com.webpublico.entidades.Setor;
import br.com.webpublico.negocios.LogradouroFacade;
import br.com.webpublico.negocios.LoteFacade;
import br.com.webpublico.negocios.QuadraFacade;
import br.com.webpublico.negocios.SetorFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author magraowar
 */
@SessionScoped
@ManagedBean
public class RelatorioDeLogradouroControlador extends AbstractReport implements Serializable {

    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private LoteFacade loteFacade;
    private Logradouro logradouroInicial;
    private Logradouro logradouroFinal;
    private Quadra quadraInicial;
    private Quadra quadraFinal;
    private Setor setorInicial;
    private Setor setorFinal;
    private Lote loteInicial;
    private Lote loteFinal;
    private ConverterAutoComplete converterLogradouro;
    private ConverterAutoComplete converterQuadra;
    private ConverterAutoComplete converterSetor;
    private ConverterAutoComplete converterLote;
    private StringBuilder semDados;
    private StringBuilder filtros;
    private StringBuilder where;
    private String ordenacao;
    private String ordemSql;

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public Logradouro getLogradouroFinal() {
        return logradouroFinal;
    }

    public void setLogradouroFinal(Logradouro logradouroFinal) {
        this.logradouroFinal = logradouroFinal;
    }

    public Logradouro getLogradouroInicial() {
        return logradouroInicial;
    }

    public void setLogradouroInicial(Logradouro logradouroInicial) {
        this.logradouroInicial = logradouroInicial;
    }

    public Lote getLoteFinal() {
        return loteFinal;
    }

    public void setLoteFinal(Lote loteFinal) {
        this.loteFinal = loteFinal;
    }

    public Lote getLoteInicial() {
        return loteInicial;
    }

    public void setLoteInicial(Lote loteInicial) {
        this.loteInicial = loteInicial;
    }

    public Quadra getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(Quadra quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public Quadra getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(Quadra quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public Setor getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(Setor setorFinal) {
        this.setorFinal = setorFinal;
    }

    public Setor getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(Setor setorInicial) {
        this.setorInicial = setorInicial;
    }

    public ConverterAutoComplete getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, logradouroFacade);
        }
        return converterLogradouro;
    }

    public ConverterAutoComplete getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterAutoComplete(Lote.class, loteFacade);
        }
        return converterLote;
    }

    public ConverterAutoComplete getConverterQuadra() {
        if (converterQuadra == null) {
            converterQuadra = new ConverterAutoComplete(Quadra.class, loteFacade);
        }
        return converterQuadra;
    }

    public ConverterAutoComplete getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterAutoComplete(Setor.class, loteFacade);
        }
        return converterSetor;
    }

    public List<Logradouro> completaLogradouro(String parte) {
        return logradouroFacade.listaFiltrando(parte.trim(), "nome");
    }

    public List<Setor> completaSetor(String parte) {
        return setorFacade.listaFiltrando(parte.trim(), "nome");
    }

    public List<Quadra> completaQuadra(String parte) {
        return quadraFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<Lote> completaLote(String parte) {
        return loteFacade.listaFiltrando(parte.trim(), "codigoLote");
    }

    public RelatorioDeLogradouroControlador() {
        ordenacao = "S";
    }

    public void limpaCampos() {
        logradouroInicial = null;
        logradouroFinal = null;
        setorInicial = null;
        setorFinal = null;
        quadraInicial = null;
        quadraFinal = null;
        loteInicial = null;
        loteFinal = null;
        where = new StringBuilder();
        ordenacao = "S";
    }

    public void montaRelatorio() throws JRException, IOException {
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();
        semDados = new StringBuilder("NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
        filtros = new StringBuilder();

        String arquivoJasper = "RelatorioDeLogradouro.jasper";

        HashMap parameters = new HashMap();
        montaCondicao();
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTROS", filtros.toString());
        parameters.put("ORDER", ordemSql);
        gerarRelatorio(arquivoJasper, parameters);
    }

    public void montaCondicao() {

        String juncao = "where";
        ordemSql = "";

        if (logradouroInicial != null && logradouroFinal == null) {
            this.where.append(juncao).append(" log.id = ").append(logradouroInicial.getId());
            juncao = " and ";
            filtros.append(" Logradouro Inicial = ").append(logradouroInicial.getNome()).append("; ");
        } else if (logradouroFinal != null && logradouroInicial == null) {
            this.where.append(juncao).append(" log.id <= ").append(logradouroFinal.getId());
            juncao = " and ";
            filtros.append(" Logradouro Final = ").append(logradouroFinal.getNome()).append("; ");
        } else if (logradouroInicial != null && logradouroFinal != null) {
            this.where.append(juncao).append(" log.id between ").append(logradouroInicial.getId()).append(" and ").append(logradouroFinal.getId());
            juncao = " and ";
            filtros.append(" Logradouro entre ").append(logradouroInicial.getNome()).append(" e ").append(logradouroFinal.getNome()).append("; ");
        }

        if (setorInicial != null && setorFinal == null) {
            this.where.append(juncao).append(" setor.id = ").append(setorInicial.getId());
            juncao = " and ";
            filtros.append(" Setor Inicial = ").append(setorInicial.getNome()).append("; ");
        } else if (setorFinal != null && setorInicial == null) {
            this.where.append(juncao).append(" setor.id = ").append(setorFinal.getId());
            juncao = " and ";
            filtros.append(" Setor Final = ").append(setorFinal.getNome()).append("; ");
        } else if (setorInicial != null && setorFinal != null) {
            this.where.append(juncao).append(" setor.id between ").append(setorInicial.getId()).append(" and ").append(setorFinal.getId());
            juncao = " and ";
            filtros.append(" Setor entre ").append(setorInicial.getNome()).append(" e ").append(setorFinal.getNome()).append("; ");
        }

        if (quadraInicial != null && quadraFinal == null) {
            this.where.append(juncao).append(" quadra.id = ").append(quadraInicial.getId());
            juncao = " and ";
            filtros.append(" Quadra Inicial = ").append(quadraInicial.getDescricao()).append("; ");
        } else if (quadraFinal != null && quadraInicial == null) {
            this.where.append(juncao).append(" quadra.id = ").append(quadraFinal.getId());
            juncao = " and ";
            filtros.append(" Quadra Final = ").append(quadraFinal.getDescricao()).append("; ");
        } else if (quadraInicial != null && setorFinal != null) {
            this.where.append(juncao).append(" quadra.id between ").append(quadraInicial.getId()).append(" and ").append(quadraFinal.getId());
            juncao = " and ";
            filtros.append(" Quadra entre ").append(quadraInicial.getDescricao()).append(" e ").append(quadraFinal.getDescricao()).append("; ");
        }

        if (loteInicial != null && loteFinal == null) {
            this.where.append(juncao).append(" lote.id = ").append(loteInicial.getId());
            juncao = " and ";
            filtros.append(" Lote Inicial = ").append(loteInicial.getCodigoLote()).append("; ");
        } else if (loteFinal != null && loteInicial == null) {
            this.where.append(juncao).append(" lote.id = ").append(loteFinal.getId());
            juncao = " and ";
            filtros.append(" Lote Final = ").append(loteFinal.getCodigoLote()).append("; ");
        } else if (loteInicial != null && loteFinal != null) {
            this.where.append(juncao).append(" lote.id between ").append(loteInicial.getId()).append(" and ").append(loteFinal.getId());
            juncao = " and ";
            filtros.append(" Lote entre ").append(loteInicial.getCodigoLote()).append(" e ").append(loteFinal.getCodigoLote()).append("; ");
        }

        switch (ordenacao) {
            case "A":
                ordemSql = " log.nome";
                break;
            case "N":
                ordemSql = " lote.codigolote";
                break;
            default:
                break;
        }
        if (!ordemSql.equals("")) {
            ordemSql = " order by " + ordemSql;
        } else if (ordemSql.equals("")) {
            ordemSql = " order by log.nome ";
        }

    }
}
