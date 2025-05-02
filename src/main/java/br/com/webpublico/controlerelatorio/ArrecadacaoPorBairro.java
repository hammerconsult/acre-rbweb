/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.negocios.BairroFacade;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author heinz
 */

@ManagedBean(name = "arrecadacaoPorBairro")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoArrecadacaoPorBairro", pattern = "/tributario/cadastromunicipal/relatorio/arrecadacaoporbairro/", viewId = "/faces/tributario/cadastromunicipal/relatorio/arrecadacaoporbairro.xhtml"),
})

public class ArrecadacaoPorBairro extends AbstractReport {

    @EJB
    private BairroFacade bairroFacade;
    private Bairro bairroInicial;
    private Bairro bairroFinal;
    private ConverterAutoComplete converteBairro;
    @EJB
    private DividaFacade dividaFacade;
    private ConverterAutoComplete converteDivida;
    private StringBuilder where;
    private StringBuilder semDados;
    private StringBuilder filtros;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Divida[] dividasSelecionada;

    public Divida[] getDividasSelecionada() {
        return dividasSelecionada;
    }

    public void setDividasSelecionada(Divida[] dividasSelecionada) {
        this.dividasSelecionada = dividasSelecionada;
    }

    public Bairro getBairroFinal() {
        return bairroFinal;
    }

    public void setBairroFinal(Bairro bairroFinal) {
        this.bairroFinal = bairroFinal;
    }

    public Bairro getBairroInicial() {
        return bairroInicial;
    }

    public void setBairroInicial(Bairro bairroInicial) {
        this.bairroInicial = bairroInicial;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public ConverterAutoComplete getConverterBairro() {
        if (converteBairro == null) {
            converteBairro = new ConverterAutoComplete(Bairro.class, bairroFacade);
        }
        return converteBairro;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converteDivida == null) {
            converteDivida = new ConverterAutoComplete(Divida.class, dividaFacade);
        }
        return converteBairro;
    }

    public List<Bairro> completaBairro(String parte) {
        return bairroFacade.listaPorCodigo(parte.trim());
    }

    public List<Divida> dividas() {
        return dividaFacade.lista();
    }

    public ArrecadacaoPorBairro() {
    }

    @URLAction(mappingId = "novoArrecadacaoPorBairro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        bairroInicial = null;
        bairroFinal = null;
        dividasSelecionada = null;
        exercicioInicial = null;
        exercicioFinal = null;
        where = new StringBuilder();
    }

    public void montaRelatorio() throws JRException, IOException {
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();
        semDados = new StringBuilder("NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
        filtros = new StringBuilder();
        String arquivoJasper = "ArrecadacaoPorBairro.jasper";
        HashMap parameters = new HashMap();
        montaCondicao();
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("FILTROS", filtros.toString());
        gerarRelatorio(arquivoJasper, parameters);
    }

    private void montaCondicao() {

        where = new StringBuilder();
        filtros = new StringBuilder();
        String juncao = " where ";

        if (exercicioInicial != null && exercicioInicial.getId() != null) {
            this.where.append(juncao).append(" exerc.ano >= ").append(exercicioInicial.getAno());
            filtros.append(" Exécicio Inicial: ").append(exercicioInicial.getAno());
            juncao = " and ";
        }

        if (exercicioFinal != null && exercicioFinal.getId() != null) {
            this.where.append(juncao).append(" exerc.ano <= ").append(exercicioFinal.getAno());
            filtros.append(" Exécicio Final: ").append(exercicioInicial.getAno());
            juncao = " and ";
        }

        if (bairroInicial != null && bairroInicial.getId() != null) {
            where.append(juncao).append(" bairro.codigo >= ").append(bairroInicial.getCodigo());
            filtros.append(" Bairro Inicial: ").append(bairroInicial.getCodigo()).append(" - ").append(bairroInicial.getDescricao());
            juncao = " and ";
        }

        if (bairroFinal != null && bairroFinal.getId() != null) {
            where.append(juncao).append(" bairro.codigo <= ").append(bairroFinal.getCodigo());
            filtros.append(" Bairro Final = ").append(bairroFinal.getCodigo()).append(" - ").append(bairroFinal.getDescricao());
            juncao = " and ";
        }

        String dividas_in = "";
        for (int i = 0; i < dividasSelecionada.length; i++) {
            dividas_in += dividasSelecionada[i].getId();
            dividas_in += ",";
        }
        if (!dividas_in.isEmpty()) {
            dividas_in = dividas_in.substring(0, dividas_in.length() - 1);
            dividas_in = "(" + dividas_in + ")";
            where.append(juncao).append(" div.id in ").append(dividas_in);
            juncao = " and ";
        }
    }
}
