package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.GrupoOrcamentario;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 10/01/14
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-validacao-cota", pattern = "/relatorio/validacao-cota-orcamentaria/", viewId = "/faces/financeiro/relatorio/relatoriovalidacaocotaorc.xhtml")
})
@ManagedBean
public class RelatorioValidacaoCotaOrcControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private FonteDeRecursos fonteDeRecursos;
    private GrupoOrcamentario grupoOrcamentario;
    protected List<HierarquiaOrganizacional> listaUnidades;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    private String filtros;

    @URLAction(mappingId = "relatorio-validacao-cota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        fonteDeRecursos = null;
        mes = null;
        listaUnidades = new ArrayList<>();
    }

    public String getDataReferenciaFinal() {
        if (mes != null) {
            if (!mes.equals(Mes.DEZEMBRO)
                    && !mes.equals(Mes.NOVEMBRO)
                    && !mes.equals(Mes.OUTUBRO)) {
                return Util.getDiasMes(mes.getNumeroMes(), getSistemaControlador().getExercicioCorrente().getAno()) + "/0" + mes.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
            } else {
                return Util.getDiasMes(mes.getNumeroMes(), getSistemaControlador().getExercicioCorrente().getAno()) + "/" + mes.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
            }
        } else {
            return sistemaControlador.getDataOperacaoFormatada();
        }
    }

    public String getDataReferenciaInicial() {
        if (mes != null) {
            if (!mes.equals(Mes.DEZEMBRO)
                    && !mes.equals(Mes.NOVEMBRO)
                    && !mes.equals(Mes.OUTUBRO)) {
                return "01/0" + mes.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
            } else {
                return "01/" + mes.getNumeroMes() + "/" + getSistemaControlador().getExercicioCorrente().getAno();
            }
        } else {
            return "01/01/" + getSistemaControlador().getExercicioCorrente().getAno();
        }
    }

    public void gerarRelatorio() {
        try {
            String arquivoJasper = "RelatorioValidaCotaOrcamentaria.jasper";
            HashMap parametros = new HashMap();
            parametros.put("USUARIO", getNomeUsuarioLogado());
            parametros.put("MUNICIPIO", "Município de Rio Branco - AC");
            parametros.put("SUBREPORT_DIR", getCaminho());
            parametros.put("IMAGEM", getCaminhoImagem());
            parametros.put("SQL", gerarSql());
            parametros.put("SQL_EMPENHOS", gerarSqlEmpenhos());
            parametros.put("DATAINICIAL", getDataReferenciaInicial());
            parametros.put("DATAFINAL", getDataReferenciaFinal());
            parametros.put("FILTROS", filtros);
            parametros.put("EXERCICIO_ID", sistemaControlador.getExercicioCorrente().getId());
            gerarRelatorio(arquivoJasper, parametros);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getNomeUsuarioLogado() {
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            return sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome();
        } else {
            return sistemaControlador.getUsuarioCorrente().getUsername();
        }
    }

    private String gerarSql() {
        StringBuilder stb = new StringBuilder();
        filtros = "";
        if (mes != null) {
            stb.append(" and cota.mes like '").append(mes.name()).append("'");
            filtros += " Mês de Referência: " + mes.getDescricao() + " -";
        }
        if (grupoOrcamentario != null) {
            stb.append(" and go.id = ").append(grupoOrcamentario.getId());
            filtros += " Grupo orçamentário: " + grupoOrcamentario.toString().trim() + " -";
        }
        if (fonteDeRecursos != null) {
            stb.append(" and fonte.id = ").append(fonteDeRecursos.getId());
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString().trim() + " -";
        }
        if (filtros.length() > 0) {
            filtros = filtros.substring(0, filtros.length() - 1);
        }
        return stb.toString();
    }

    private String gerarSqlEmpenhos() {
        StringBuilder stb = new StringBuilder();
        if (grupoOrcamentario != null) {
            stb.append(" and go.id = ").append(grupoOrcamentario.getId());
        }
        if (fonteDeRecursos != null) {
            stb.append(" and fonte.id = ").append(fonteDeRecursos.getId());
        }
        if (listaUnidades.size() > 0) {
            StringBuilder idUnidades = new StringBuilder();
            String concatUnd = "";
            StringBuilder unds = new StringBuilder();
            for (HierarquiaOrganizacional lista : listaUnidades) {
                idUnidades.append(concatUnd).append(lista.getSubordinada().getId());
                unds.append(lista.getCodigo()).append(" -");
                concatUnd = ", ";
            }
            stb.append(" and VW.SUBORDINADA_ID IN ( ").append(idUnidades.toString()).append(")");
            filtros += " Unidade(s): " + unds.toString();
            if (filtros.length() > 0) {
                filtros = filtros.substring(0, filtros.length() - 1);
            }
        } else {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            StringBuilder idUnidades = new StringBuilder();
            String concat = "";
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                idUnidades.append(concat).append(lista.getSubordinada().getId());
                concat = ", ";
            }
            stb.append(" and VW.SUBORDINADA_ID IN ( ").append(idUnidades).append(")");
        }
        return stb.toString();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes obj : Mes.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
    }
}
