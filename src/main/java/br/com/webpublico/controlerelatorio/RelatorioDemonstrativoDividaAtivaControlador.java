
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.MoedaFacade;
import br.com.webpublico.negocios.TributoFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.google.common.collect.Lists;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "relatorioDemonstrativoDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioDemonstrativoDividaAtiva", pattern = "/tributario/divida-ativa/demostrativo/",
                viewId = "/faces/tributario/dividaativa/relatorio/demonstrativodividaativa.xhtml")})
public class RelatorioDemonstrativoDividaAtivaControlador extends AbstractReport implements Serializable {

    BigDecimal ufmAno;
    BigDecimal ufmAnoAnterior;
    BigDecimal diferencaUFM;
    @EJB
    private ExercicioFacade exercicioFacade;
    private Converter converterExercicio;
    private Converter converterTributo;
    private List<Tributo> listaTributos;
    @Limpavel(Limpavel.NULO)
    private List<Tributo> tributosSelecionados;
    @Limpavel(Limpavel.ZERO)
    private Integer exercicio;
    private StringBuilder where;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    private StringBuilder filtro;
    private Tributo tributo;

    public RelatorioDemonstrativoDividaAtivaControlador() {
        listaTributos = new ArrayList<Tributo>();
    }

    public List<Tributo> getListaTributos() {
        return listaTributos;
    }

    public void setListaTributos(List<Tributo> listaTributos) {
        this.listaTributos = listaTributos;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public List<Tributo> getTributosSelecionados() {
        return tributosSelecionados;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public void montaCondicao() {
        where = new StringBuilder();
        String juncao = " where ";
        StringBuilder sb = new StringBuilder();
        filtro.append("Filtrado por Tributo: ");
        for (Tributo t : tributosSelecionados) {
            sb.append(t.getId()).append(", ");
            filtro.append(t.getDescricao()).append(", ");
        }
        // -2 para remover a 'virgula' e o 'espaço' do fim da string (", ")
        if (tributosSelecionados != null && !tributosSelecionados.isEmpty()) {
            this.where.append(juncao).append(" trib.id in (").append(sb.substring(0, sb.length() - 2)).append(") ");
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (exercicio == null) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "Informe um exercício."));
        }
        return retorno;
    }

    private boolean validaUFM() {
        boolean retorno = true;

        if (ufmAno == null || ufmAno.compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar!", "Não existe UFM parametrizado para o ano " + exercicio + "."));
        }

        if (ufmAnoAnterior == null || ufmAnoAnterior.compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar!", "Não existe UFM parametrizado para o ano " + (exercicio - 1) + "."));
        }

        return retorno;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public void limparCampos() {
        Util.limparCampos(this);
    }

    @URLAction(mappingId = "novoRelatorioDemonstrativoDividaAtiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        tributo = new Tributo();
        exercicio = Calendar.getInstance().get(Calendar.YEAR);
        listaTributos = tributoFacade.lista();
        tributosSelecionados = Lists.newLinkedList();
    }

    public void montaRelatorio() throws JRException, IOException {
        preenchePrimeiroTributo();

        if (validaCampos()) {
            ufmAno = moedaFacade.recuperaValorUFMPorExercicio(exercicio);
            ufmAnoAnterior = moedaFacade.recuperaValorUFMPorExercicio(exercicio - 1);
            diferencaUFM = ufmAno.subtract(ufmAnoAnterior);

            if (validaUFM()) {
                filtro = new StringBuilder();
                String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
                subReport += "/report/";
                String caminhoBrasao = getCaminhoImagem();
                String arquivoJasper = "RelatorioDemonstrativoDeDividaAtiva.jasper";
                HashMap parameters = new HashMap();
                montaCondicao();
                //System.out.println("montacondicao" + where.toString());
                parameters.put("WHERE", where.toString());
                parameters.put("SUBREPORT_DIR", subReport);
                parameters.put("BRASAO", caminhoBrasao);
                parameters.put("USUARIO", this.usuarioLogado().getUsername());
                parameters.put("ANO", exercicio);
                filtro.append("; Exercicio: ").append(exercicio).append("; ");
                parameters.put("FILTROS", filtro.toString());
                parameters.put("UFM_ANO", ufmAno);
                parameters.put("DIFERENCA_UFM", diferencaUFM);
                parameters.put("SEMDADOS", "NÃO FOI ENCONTRADO NENHUM REGISTRO PARA O FILTRO SOLICITADO!");
                gerarRelatorio(arquivoJasper, parameters);
            }
        }

    }

    private void preenchePrimeiroTributo() {
        if (tributo != null && tributo.getId() != null) {
            tributosSelecionados.add(tributo);
        }
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterGenerico(Tributo.class, tributoFacade);
        }
        return converterTributo;
    }

    public void addTributo() {
        if (validaTributo()) {
            tributosSelecionados.add(tributo);
            tributo = new Tributo();
        }
    }

    public void removeTributo(Tributo tributo) {
        if (tributosSelecionados.contains(tributo)) {
            tributosSelecionados.remove(tributo);
        }
    }

    private boolean validaTributo() {
        boolean valida = true;
        if (tributo == null) {
            valida = false;
            FacesUtil.addWarn("Atenção", "Selecione um tributo para adiciona-lo");
        } else if (tributosSelecionados.contains(tributo)) {
            valida = false;
            FacesUtil.addWarn("Atenção", "O Tributo " + tributo.getDescricao() + " já foi selecionado");
        }
        return valida;
    }
}
