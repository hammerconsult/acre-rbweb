/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.InscricaoDividaAtiva;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.InscricaoDividaAtivaFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author magraowar
 */
@ManagedBean(name = "mapaResumodeInscricaonoLivroControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMapaResumoInscricaonoLivro", pattern = "/relatorios/mapa-resumo-inscricao-divida-ativa", viewId = "/faces/tributario/dividaativa/livrodividaativa/maparesumodeinscricaonolivro.xhtml"),
})
public class MapaResumodeInscricaonoLivroControlador extends AbstractReport implements Serializable {

    @Limpavel(Limpavel.NULO)
    private Exercicio exercicioInicial;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicioFinal;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterGenerico converterExercicio;
    @Limpavel(Limpavel.NULO)
    private Divida divida;
    @EJB
    private DividaFacade dividaFacade;
    private ConverterGenerico converterDivida;
    @Limpavel(Limpavel.NULO)
    private Integer numeroInicial;
    @Limpavel(Limpavel.NULO)
    private Integer numeroFinal;
    @EJB
    private InscricaoDividaAtivaFacade numeroFacade;
    private ConverterGenerico converterNumero;
    @Limpavel(Limpavel.NULO)
    private Date dataInscricaoInicial;
    @Limpavel(Limpavel.NULO)
    private Date dataInscricaoFinal;
    private StringBuilder where;
    private StringBuilder filtro;
    private StringBuilder semDados;
    private String ordenacao;
    private String ordemSql;

    public MapaResumodeInscricaonoLivroControlador() {
        ordenacao = "S";
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Date getDataInscricaoFinal() {
        return dataInscricaoFinal;
    }

    public void setDataInscricaoFinal(Date dataInscricaoFinal) {
        this.dataInscricaoFinal = dataInscricaoFinal;
    }

    public Date getDataInscricaoInicial() {
        return dataInscricaoInicial;
    }

    public void setDataInscricaoInicial(Date dataInscricaoInicial) {
        this.dataInscricaoInicial = dataInscricaoInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(Integer numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public Integer getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(Integer numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getOrdemSql() {
        return ordemSql;
    }

    public void setOrdemSql(String ordemSql) {
        this.ordemSql = ordemSql;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public void montaCondicao() {

        String juncao = " where ";
        ordemSql = "";

        if (exercicioInicial != null) {
            this.where.append(juncao).append(" ano >= ").append(exercicioInicial.getAno());
            juncao = " and ";
            filtro.append("Exercício maior ou igual a ").append(exercicioInicial.getAno()).append("; ");
        }

        if (exercicioFinal != null) {
            this.where.append(juncao).append(" ano <= ").append(exercicioFinal.getAno());
            juncao = " and ";
            filtro.append("Exercício menor ou igual a ").append(exercicioFinal.getAno()).append("; ");
        }

        if (divida != null) {
            this.where.append(juncao).append(" id = ").append(divida.getId());
            juncao = " and ";
            filtro.append("Dívida = ").append(divida.getDescricao()).append("; ");
        }

        if (numeroInicial != null) {
            this.where.append(juncao).append("numero >= ").append(numeroInicial);
            juncao = " and ";
            filtro.append("Número Livro maior ou igual a ").append(numeroInicial).append("; ");
        }

        if (numeroFinal != null) {
            this.where.append(juncao).append(" numero <= ").append(numeroFinal);
            juncao = " and ";
            filtro.append("Número Livro menor ou igual a ").append(numeroFinal).append("; ");
        }

        if (dataInscricaoInicial != null) {
            this.where.append(juncao).append(" datainscricao >= to_date (").append(formataData(dataInscricaoInicial)).append(",'dd/MM/yyyy')");
            juncao = " and ";
            filtro.append("Data de Inscrição maior ou igual a ").append(formataData(dataInscricaoInicial)).append("; ");
        }

        if (dataInscricaoFinal != null) {
            this.where.append(juncao).append(" datainscricao <= to_date(").append(formataData(dataInscricaoFinal)).append(",'dd/MM/yyyy')");
            juncao = " and ";
            filtro.append("Data de Inscrição menor ou igual a ").append(formataData(dataInscricaoFinal)).append("; ");
        }

        switch (ordenacao) {
            case "L":
                ordemSql = " numero ";
                break;
            case "D":
                ordemSql = " descricao ";
                break;
            case "I":
                ordemSql = " datainscricao ";
                break;
            default:
                break;
        }

        if (!ordemSql.equals("")) {
            ordemSql = " order by " + ordemSql;
        } else if (ordemSql.equals("")) {
            ordemSql = " order by ano ";
        }
    }

    @URLAction(mappingId = "novoMapaResumoInscricaonoLivro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        ordenacao = "S";
        Util.limparCampos(this);
    }

    public void gerarRelatorio() throws JRException, IOException {
        super.geraNoDialog = true;
        where = new StringBuilder();
        semDados = new StringBuilder("Não foram encontrados registros");
        filtro = new StringBuilder();

        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();

        String arquivoJasper = "MapaResumodeInscricaonoLivro.jasper";

        HashMap parameters = new HashMap();
        montaCondicao();
        //System.out.println("montacondicao" + where.toString());
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTRO", filtro.toString());
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("ORDER", ordemSql.toString());
        gerarRelatorio(arquivoJasper, parameters);
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public Converter getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

    public Converter getConverterInscricaoDividaAtiva() {
        if (converterNumero == null) {
            converterNumero = new ConverterGenerico(InscricaoDividaAtiva.class, numeroFacade);
        }
        return converterNumero;
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> lista = new java.util.ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (Divida div : dividaFacade.lista()) {
            lista.add(new SelectItem(div, div.getDescricao().toString()));
        }
        return lista;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new java.util.ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (Exercicio ex : exercicioFacade.lista()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }

    public List<SelectItem> getInscricaoDividaAtiva() {
        List<SelectItem> lista = new java.util.ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (InscricaoDividaAtiva ida : numeroFacade.lista()) {
            lista.add(new SelectItem(ida, ida.getNumero().toString()));
        }
        return lista;
    }

    private String formataData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "'" + sdf.format(data) + "'";
    }
}
