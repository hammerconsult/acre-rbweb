package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacadeOLD;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
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
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 26/05/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-boletim-frequencia", pattern = "/relatorio/boletim-frequencia-servidor/", viewId = "/faces/rh/relatorios/relatorioboletimfrequencia.xhtml")
})
public class RelatorioBoletimFrequenciaControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterHierarquia;
    private Mes mes;
    private Integer ano;
    private String filtros;

    public RelatorioBoletimFrequenciaControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public void gerarRelatorio() throws JRException, IOException {
        try {
            if(validaCampos()){
            String arquivoJasper = "RelatorioBoletimFrequencia.jasper";
            HashMap parameters = new HashMap();
            parameters.put("FILTROS", filtros);
            parameters.put("DATAOPERACAO", UtilRH.getDataOperacao());
            parameters.put("MES", (mes.getNumeroMesIniciandoEmZero()));
            parameters.put("MESANO", getMesAno());
            parameters.put("ANO", ano);
            parameters.put("CODIGOHO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
            parameters.put("MUNICIPIO", "Município de Rio Branco - AC");
            parameters.put("SUBREPORT_DIR", getCaminho());
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            parameters.put("NOMERELATORIO", "RELATÓRIO BOLETIM DE FREQUÊNCIA DO SERVIDOR");
            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
            }
            gerarRelatorio(arquivoJasper, parameters);
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    private String getMesAno() {
        return ((String.valueOf(mes.getNumeroMes()).length() <= 1 ? "0" + mes.getNumeroMes() : mes.getNumeroMes()) + "/" + ano);
    }

    public boolean validaCampos() {
        boolean toReturn = true;
        if (mes == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:mes", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "O campo Mês é obrigatório"));
            toReturn = false;
        }

        if (ano == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:ano", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "O campo Ano é obrigatório"));
            toReturn = false;
        }

        if (hierarquiaOrganizacionalSelecionada == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:orgao", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "O campo Órgão é obrigatório"));
            toReturn = false;
        }
        return toReturn;
    }

    @URLAction(mappingId = "relatorio-frequencias-por-periodo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.hierarquiaOrganizacionalSelecionada = null;
        this.mes = null;
        this.ano = null;
        filtros = "";
    }

    private String concatenaSQL() {
        StringBuilder stb = new StringBuilder();
        String concat = " AND ";


        return stb.toString();
    }

    private String formataData(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("MM/yyyy");
        return formato.format(data);
    }

    public ConverterAutoComplete getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
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

    public List<SelectItem> getMesesRelatorio(){
        return Util.getListSelectItem(Mes.values(), false);
    }
}
