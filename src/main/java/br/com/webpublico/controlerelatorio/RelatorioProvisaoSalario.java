/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoProvSalario;
import br.com.webpublico.enums.TipoProvisaoSalario;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-provisao-decimo-terceiro-salario-ferias-licenca-premio",
                pattern = "/relatorio/provisao-decimo-terceiro-salario-ferias-licenca-premio/",
                viewId = "/faces/financeiro/relatorio/relatorioprovisaosalario.xhtml")
})
public class RelatorioProvisaoSalario extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private String numero;
    private Date dataInicial;
    private Date dataFinal;
    private TipoProvisaoSalario tipoProvisaoSalario;
    private TipoOperacaoProvSalario tipoOperacaoProvSalario;
    private TipoLancamento tipoLancamento;
    private List<HierarquiaOrganizacional> listaUnidades;
    private String filtros;
    @Enumerated(EnumType.STRING)
    private Apresentacao apresentacao;
    private UnidadeGestora unidadeGestora;

    private enum Apresentacao {

        CONSOLIDADO("Consolidado"),
        ORGAO("Orgão"),
        UNIDADE("Unidade"),
        UNIDADE_GESTORA("Unidade Gestora");
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

    public List<SelectItem> getListaApresentacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Apresentacao ap : Apresentacao.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    private Boolean validaDatas() {
        if (this.dataInicial == null || this.dataFinal == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Campo Obrigatório!", "Favor informar um intervalo de datas"));
            return false;
        }
        if (this.dataInicial.after(this.dataFinal)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Validação!", "Data Inicial não pode ser maior que a Data Final"));
            return false;
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Validação!", "As datas estão com exercícios diferentes"));
            return false;
        }
        return true;
    }

    public void gerarRelatorioProvisaoSalario() {
        try {
            if (validaDatas()) {
                String arquivoJasper = "RelatorioProvisaoSalario.jasper";
                HashMap parameters = new HashMap();
                String where = gerarSql();
                if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
                } else {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getUsername());
                }
                parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
                parameters.put("IMAGEM", getCaminhoImagem());
                parameters.put("SQL", where);
                parameters.put("APRESENTACAO", apresentacao.name());
                parameters.put("EXERCICIO_ID", sistemaControlador.getExercicioCorrente().getId());
                parameters.put("FILTROS", filtros);
                gerarRelatorio(arquivoJasper, parameters);
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    public List<SelectItem> getListaTipoProvisaoSalario() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProvisaoSalario tps : TipoProvisaoSalario.values()) {
            toReturn.add(new SelectItem(tps, tps.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoOperacaoProvSalario() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoProvSalario tops : TipoOperacaoProvSalario.values()) {
            toReturn.add(new SelectItem(tops, tops.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoLancamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoLancamento tl : TipoLancamento.values()) {
            toReturn.add(new SelectItem(tl, tl.toString()));
        }
        return toReturn;
    }

    private String gerarSql() {
        String juncao = " AND ";
        StringBuilder where = new StringBuilder();

        where.append(juncao).append(" trunc(PS.DATAPROVISAO) BETWEEN TO_DATE(\'").append(formataData(dataInicial)).append("\', 'DD/MM/YYYY') AND TO_DATE(\'").append(formataData(dataFinal)).append("\', 'DD/MM/YYYY') ");
        filtros = " Período: " + formataData(dataInicial) + " a " + formataData(dataFinal) + " -";

        if (numero != null && !numero.isEmpty()) {
            where.append(juncao).append(" PS.NUMERO = '").append(numero).append("'");
            filtros += "Número: " + numero + " -";
        }
        if (tipoLancamento != null) {
            where.append(juncao).append(" PS.TIPOLANCAMENTO = '").append(tipoLancamento.name()).append("'");
            filtros += "Tipo Lançamento: " + tipoLancamento + " -";
        }
        if (tipoOperacaoProvSalario != null) {
            where.append(juncao).append(" PS.TIPOOPERACAOPROVSALARIO = '").append(tipoOperacaoProvSalario.name()).append("'");
            filtros += "Tipo Operação: " + tipoOperacaoProvSalario + " -";
        }
        if (tipoProvisaoSalario != null) {
            where.append(juncao).append(" PS.TIPOPROVISAOSALARIO = '").append(tipoProvisaoSalario.name()).append("'");
            filtros += "Tipo Provisão Salário: " + tipoProvisaoSalario + " -";
        }

        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String unidades = "";
            String concatena = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatena).append(lista.getSubordinada().getId());
                unidades += lista.getCodigo().substring(3, 10) + " -";
                concatena = ",";
            }
            filtros += "Unidade(s): " + unidades;
            where.append(juncao).append(" PS.UNIDADEORGANIZACIONAL_ID IN (").append(idUnidades).append(")");
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            StringBuilder idUnidades = new StringBuilder();
            String concat = "";
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                idUnidades.append(concat).append(lista.getSubordinada().getId());
                concat = ", ";
            }
            where.append(" and PS.UNIDADEORGANIZACIONAL_ID IN (").append(idUnidades).append(")");
        }

        if (unidadeGestora != null) {
            where.append(juncao).append(" ug.id = ").append(unidadeGestora.getId());
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return where.toString();
    }

    private String formataData(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(data);
    }

    @URLAction(mappingId = "relatorio-provisao-decimo-terceiro-salario-ferias-licenca-premio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.numero = null;
        this.dataFinal = new Date();
        this.dataInicial = new Date();
        this.listaUnidades = new ArrayList<>();
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TipoProvisaoSalario getTipoProvisaoSalario() {
        return tipoProvisaoSalario;
    }

    public void setTipoProvisaoSalario(TipoProvisaoSalario tipoProvisaoSalario) {
        this.tipoProvisaoSalario = tipoProvisaoSalario;
    }

    public TipoOperacaoProvSalario getTipoOperacaoProvSalario() {
        return tipoOperacaoProvSalario;
    }

    public void setTipoOperacaoProvSalario(TipoOperacaoProvSalario tipoOperacaoProvSalario) {
        this.tipoOperacaoProvSalario = tipoOperacaoProvSalario;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
