/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
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
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "relatorioSituacaoDividaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoSituacaoDivida", pattern = "/tributario/relatoriosituacaodivida/", viewId = "/faces/tributario/dividaativa/relatorio/relatoriosituacaodivida.xhtml"),
})
public class RelatorioSituacaoDividaControlador extends AbstractReport implements Serializable {

    @EJB
    private ExercicioFacade exercicioFacade;
    private Converter converterExercicio;
    private List<TipoCadastroTributario> listaTipoCadastro;
    @Limpavel(Limpavel.NULO)
    private TipoCadastroTributario[] tiposCadastrosSelecionados;
    private List<Divida> listaDivida;
    @Limpavel(Limpavel.NULO)
    private Divida[] dividasSelecionadas;
    @Limpavel(Limpavel.NULO)
    private Date dataPosicao;
    @Limpavel(Limpavel.ZERO)
    private Exercicio exercicioInicial;
    @Limpavel(Limpavel.ZERO)
    private Exercicio exercicioFinal;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String situacao;
    @EJB
    private DividaFacade dividaFacade;
    private StringBuilder where;


    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public Date getDataPosicao() {
        return dataPosicao;
    }

    public void setDataPosicao(Date dataPosicao) {
        this.dataPosicao = dataPosicao;
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

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Divida[] getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(Divida[] dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    public List<Divida> getListaDivida() {
        return listaDivida;
    }

    public void setListaDivida(List<Divida> listaDivida) {
        this.listaDivida = listaDivida;
    }

    public RelatorioSituacaoDividaControlador() {
        listaTipoCadastro = new ArrayList<TipoCadastroTributario>();
        listaDivida = new ArrayList<Divida>();
    }

    private void populaListaTipoCadastro() {
    }

    public TipoCadastroTributario[] getTiposCadastrosSelecionados() {
        return tiposCadastrosSelecionados;
    }

    public void setTiposCadastrosSelecionados(TipoCadastroTributario[] tiposCadastrosSelecionados) {
        this.tiposCadastrosSelecionados = tiposCadastrosSelecionados;
    }

    public List<TipoCadastroTributario> getListaTipoCadastro() {
        return Arrays.asList(TipoCadastroTributario.values());
    }

    public void setListaTipoCadastro(List<TipoCadastroTributario> listaTipoCadastro) {
        this.listaTipoCadastro = listaTipoCadastro;
    }

    public void montaCondicao() {
        where = new StringBuilder();
        String juncao = " where ";

        //Filtro de tipos de cadastros
        if (tiposCadastrosSelecionados != null && tiposCadastrosSelecionados.length > 0) {
            StringBuffer idsTiposCadastros = new StringBuffer();
            for (int i = 0; i < tiposCadastrosSelecionados.length; i++) {
                idsTiposCadastros.append("'").append(tiposCadastrosSelecionados[i].name()).append("'");
                idsTiposCadastros.append(",");
            }
            where.append(juncao);
            where.append(" (div.tipocadastro in (");
            where.append(idsTiposCadastros.substring(0, idsTiposCadastros.length() - 1));
            where.append(")) ");
            juncao = " and ";
        }

        //Filtro de dividas
        if (dividasSelecionadas != null && dividasSelecionadas.length > 0) {
            StringBuffer idsDividas = new StringBuffer();
            for (int i = 0; i < dividasSelecionadas.length; i++) {
                idsDividas.append(dividasSelecionadas[i].getId().toString());
                idsDividas.append(",");
            }

            where.append(juncao);
            where.append(" (div.id in (");
            where.append(idsDividas.substring(0, idsDividas.length() - 1));
            where.append(")) ");
            juncao = " and ";
        }

        //Filtro de Exercicio Inicial
        if (exercicioInicial != null && exercicioInicial.getAno() != null) {
            //System.out.println(exercicioInicial.getAno());
            where.append(juncao);
            where.append(" (exerc.ano >= ");
            where.append(exercicioInicial.getAno().toString());
            where.append(") ");
            juncao = " and ";
        }

        //Filtro de Exercicio Final
        if (exercicioFinal != null && exercicioFinal.getAno() != null) {
            where.append(juncao);
            where.append(" (exerc.ano <= ");
            where.append(exercicioFinal.getAno().toString());
            where.append(") ");
            juncao = " and ";
        }

        //Filtro por Data de Posição
        if (dataPosicao != null) {
            where.append(juncao);
            where.append(" (vlrdiv.emissao <= '");
            where.append(new SimpleDateFormat("dd/MM/yyyy").format(dataPosicao));
            where.append("') ");
            juncao = " and ";
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        //Filtro de tipos de cadastros
        if (tiposCadastrosSelecionados == null || tiposCadastrosSelecionados.length <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Continuar. ", "Selecione pelo menos um tipo de cadastro!"));
            retorno = false;
        }

        //Filtro de dividas
        if (dividasSelecionadas == null || dividasSelecionadas.length <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Continuar. ", "Selecione pelo menos uma dívida!"));
            retorno = false;
        }

        //Filtro de Exercicio Inicial
        if (exercicioInicial == null || exercicioInicial.getAno() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Continuar. ", "Selecione o exercício inicial!"));
            retorno = false;
        }

        //Filtro de Exercicio Final
        if (exercicioFinal == null || exercicioFinal.getAno() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Continuar. ", "Selecione o exercício final!"));
            retorno = false;
        }

        if ((exercicioInicial != null && exercicioFinal != null) && (exercicioFinal.getAno() != null && exercicioInicial.getAno() != null) &&
                (exercicioFinal.getAno() < exercicioInicial.getAno())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Continuar. ", "Exercício final menor que o exercício inicial!"));
            retorno = false;
        }

        //Filtro por Data de Posição
        if (dataPosicao == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível Continuar. ", "Selecione a data de posição!"));
            retorno = false;
        }

        return retorno;
    }

    public void limparCampos() {
        Util.limparCampos(this);
    }

    @URLAction(mappingId = "novoSituacaoDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        tiposCadastrosSelecionados = null;
        listaTipoCadastro = Arrays.asList(TipoCadastroTributario.values());
        dividasSelecionadas = null;
        listaDivida = dividaFacade.lista();
        dataPosicao = new Date();
        exercicioInicial = new Exercicio();
        exercicioFinal = new Exercicio();
        situacao = "E";
    }

    public void montaRelatorio() throws JRException, IOException {
        if (!validaCampos()) {
            return;
        }

        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();

        String arquivoJasper = "RelatorioGerencialSituacaoDasDividas.jasper";

        HashMap parameters = new HashMap();
        montaCondicao();
        parameters.put("WHERE", where.toString());
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO_RIO_BRANCO", caminhoBrasao);
        parameters.put("USUARIO", this.usuarioLogado().getUsername());
        parameters.put("DIVIDAATIVA", situacao.equals("D"));
        parameters.put("AJUIZADA", situacao.equals("A"));
        parameters.put("SEMDADOS", "NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
        gerarRelatorio(arquivoJasper, parameters);
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();

        for (Exercicio ex : exercicioFacade.lista()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }
}
