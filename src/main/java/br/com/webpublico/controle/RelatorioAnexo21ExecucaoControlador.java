package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.TipoAdministracao;
import br.com.webpublico.enums.TipoEntidade;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 12/12/13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "relatorioAnexo21ExecucaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-lei4320-execucao-anexo21", pattern = "/relatorio/lei4320/execucao/anexo21/", viewId = "/faces/financeiro/relatorio/relatorioanexo21execucao.xhtml")
})
public class RelatorioAnexo21ExecucaoControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @Enumerated(EnumType.STRING)
    private EsferaDoPoder esferaDoPoder;
    @Enumerated(EnumType.STRING)
    private TipoAdministracao tipoAdministracao;
    @Enumerated(EnumType.STRING)
    private TipoEntidade natureza;
    private List<HierarquiaOrganizacional> listaUnidades;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private UnidadeGestora unidadeGestora;
    private String mesFinal;
    @Enumerated(EnumType.STRING)
    private Apresentacao apresentacao;

    private enum Apresentacao {

        ORGAO("Orgão"),
        UNIDADE("Orgão e Unidade");
        private String descricao;

        private Apresentacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public RelatorioAnexo21ExecucaoControlador() {
        super();
    }

    public void recuperaRelatorio() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 21", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.LEI_4320);
    }

    @URLAction(mappingId = "relatorio-lei4320-execucao-anexo21", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.mesFinal = "";
        this.esferaDoPoder = null;
        this.tipoAdministracao = null;
        this.natureza = null;
        this.listaUnidades = new ArrayList<>();
    }

    public List<SelectItem> getListaEsferaDoPoder() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Consolidado"));
        for (EsferaDoPoder edp : EsferaDoPoder.values()) {
            if (!edp.equals(EsferaDoPoder.JUDICIARIO)) {
                toReturn.add(new SelectItem(edp, edp.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getListaApresentacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Apresentacao ap : Apresentacao.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoAdministracao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdministracao ta : TipoAdministracao.values()) {
            toReturn.add(new SelectItem(ta, ta.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoEntidade() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEntidade te : TipoEntidade.values()) {
            toReturn.add(new SelectItem(te, te.getTipo()));
        }
        return toReturn;
    }

    public void gerarRelatorio(){
        try {
            Exercicio e = sistemaControlador.getExercicioCorrente();
            String arquivoJasper = "RelatorioAnexo21Execucao.jasper";
            HashMap parameters = new HashMap();
            parameters.put("MUNICIPIO", "Município de Rio Branco - AC ");
            parameters.put("ANO_EXERCICIO", e.getAno().toString());
            parameters.put("EXERCICIO_ID", e.getId());
            parameters.put("SUBREPORT_DIR", getCaminho());
            parameters.put("IMAGEM", getCaminhoImagem());
            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameters.put("USER", sistemaControlador.getUsuarioCorrente().getUsername());
            }
            if (relatoriosItemDemonst.getNotaExplicativa() != null) {
                if (!relatoriosItemDemonst.getNotaExplicativa().trim().equals("")) {
                    parameters.put("NOTA_EXPLICATIVA", "Nota Explicativa: " + relatoriosItemDemonst.getNotaExplicativa().trim());
                }
            }
            parameters.put("APRESENTACAO", apresentacao.name());
            String sql = gerarSql();
            if (!sql.equals("")) {
                parameters.put("SQL", sql);
            }
            if (!mesFinal.equals("EX")) {
                parameters.put("FILTRO_RELATORIO", retornaDescricaoMes(retornaMesAnterior(mesFinal)));
            } else {
                parameters.put("FILTRO_RELATORIO", "EXERCÍCIO");
            }
            parameters.put("DATAINICIAL", "01/01/" + sistemaControlador.getExercicioCorrente().getAno());

            if (!mesFinal.equals("01") && !mesFinal.equals("EX")) {
                parameters.put("DATAFINAL", "01/" + mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno());
            } else {
                parameters.put("DATAFINAL", "01/01/" + (sistemaControlador.getExercicioCorrente().getAno() + 1));
            }
            gerarRelatorio(arquivoJasper, parameters);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    private String
    gerarSql() {
        StringBuilder stb = new StringBuilder();
        String concat = " ";
        if (listaUnidades == null) {
            listaUnidades = new ArrayList<>();
        }
        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String concatUnd = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatUnd).append(lista.getId());
                concatUnd = ",";
            }
            stb.append(concat).append(" VW.ID IN (").append(idUnidades.toString()).append(")");
            concat = " and ";
        }
        if (tipoAdministracao != null) {
            stb.append(concat).append(" VW.TIPOADMINISTRACAO = \'").append(tipoAdministracao).append("\'");
            concat = " and ";
        }
        if (natureza != null) {
            stb.append(concat).append(" VW.TIPOUNIDADE = \'").append(natureza).append("\'");
            concat = " and ";
        }
        if (esferaDoPoder != null) {
            stb.append(concat).append(" VW.ESFERADOPODER = \'").append(esferaDoPoder).append("\'");
        }
        return stb.toString();
    }

    private String retornaDescricaoMes(String mes) {
        String toReturn = "";
        switch (mes) {
            case "01":
                toReturn = "JANEIRO";
                break;
            case "02":
                toReturn = "FEVEREIRO";
                break;
            case "03":
                toReturn = "MARÇO";
                break;
            case "04":
                toReturn = "ABRIL";
                break;
            case "05":
                toReturn = "MAIO";
                break;
            case "06":
                toReturn = "JUNHO";
                break;
            case "07":
                toReturn = "JULHO";
                break;
            case "08":
                toReturn = "AGOSTO";
                break;
            case "09":
                toReturn = "SETEMBRO";
                break;
            case "10":
                toReturn = "OUTUBRO";
                break;
            case "11":
                toReturn = "NOVEMBRO";
                break;
            case "12":
                toReturn = "DEZEMBRO";
                break;
        }
        return toReturn;
    }

    private String retornaMesAnterior(String mes) {
        String toReturn;
        Integer mesRet;

        if (mes.equals("01")) {
            mesRet = 12;
        } else {
            mesRet = Integer.parseInt(mes) - 1;
        }
        if (mesRet < 10) {
            toReturn = "0" + mesRet;
        } else {
            toReturn = "" + mesRet;
        }
        return toReturn;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public TipoEntidade getNatureza() {
        return natureza;
    }

    public void setNatureza(TipoEntidade natureza) {
        this.natureza = natureza;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }
}
