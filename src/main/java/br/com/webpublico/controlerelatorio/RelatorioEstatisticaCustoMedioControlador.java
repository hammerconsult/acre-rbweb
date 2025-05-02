package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidadesauxiliares.EstatisticaCustoMedio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.relatoriofacade.RelatorioEstatisticaCustoMedioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 26/05/2017.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-estatistica-custo-medio", pattern = "/relatorio/estatistica-custo-medio/", viewId = "/faces/administrativo/relatorios/relatorioestatisticacustomedio.xhtml")
})
@ManagedBean
public class RelatorioEstatisticaCustoMedioControlador extends AbstractReport {

    @EJB
    private RelatorioEstatisticaCustoMedioFacade facade;
    private Mes mesInicial;
    private Mes mesFinal;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Apresentacao apresentacao;
    private Material material;
    private String filtros;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @URLAction(mappingId = "relatorio-estatistica-custo-medio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mesInicial = Mes.JANEIRO;
        mesFinal = Mes.JANEIRO;
        exercicioInicial = getSistemaFacade().getExercicioCorrente();
        exercicioFinal = getSistemaFacade().getExercicioCorrente();
        apresentacao = Apresentacao.MENSAL;
        material = null;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes mes : Mes.values()) {
            toReturn.add(new SelectItem(mes, mes.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(Apresentacao.values());
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            String arquivoJasper = "RelatorioEstatisticaCustoMedio.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDados());
            HashMap parametros = Maps.newHashMap();
            parametros.put("FILTROS", filtros.trim());
            parametros.put("MODULO", "Materiais");
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("MATERIAL", material.getDescricao());
            parametros.put("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
            parametros.put("NOMERELATORIO", "Relatório de Estatística de Custo Médio");
            gerarRelatorioComDadosEmCollection(arquivoJasper, parametros, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (material == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Material deve ser informado.");
        }
        if (exercicioInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Inicial deve ser informado.");
        }
        if (exercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício Final deve ser informado.");
        }
        ve.lancarException();
        if (mesInicial.getNumeroMes() > mesFinal.getNumeroMes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O mês inicial deve ser anterior ou igual ao mês final.");
        }
        if (exercicioInicial.getAno() > exercicioFinal.getAno()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício inicial deve ser anterior ou igual ao exercício final.");
        }
        ve.lancarException();
    }

    private List<EstatisticaCustoMedio> buscarDados() {
        String mascara = Apresentacao.MENSAL.equals(apresentacao) ? "MM/yyyy" : "yyyy";
        String clausula = " where ";
        String filtro = clausula + " to_char(MO.DATAMOVIMENTO, '" + mascara + "') between '" + (Apresentacao.MENSAL.equals(apresentacao) ? mesInicial.getNumeroMesString() + "/" + exercicioInicial.getAno() : exercicioInicial.getAno()) + "' " +
            " and '" + (Apresentacao.MENSAL.equals(apresentacao) ? mesFinal.getNumeroMesString() + "/" + exercicioFinal.getAno() : exercicioFinal.getAno()) + "' ";
        filtros = "Data Inicial: " + mesInicial.getNumeroMesString() + "/" + exercicioInicial.getAno() + "; Data Final: " + mesFinal.getNumeroMesString() + "/" + exercicioFinal.getAno() + "; ";
        clausula = " and ";
        if (hierarquiaOrganizacional != null) {
            filtro += clausula + " vw.subordinada_id = " + hierarquiaOrganizacional.getSubordinada().getId();
            filtros += "Unidade Administrativa: " + hierarquiaOrganizacional.getDescricao() + "; ";
        }
        if (material != null) {
            filtro += clausula + " M.id = " + material.getId();
            filtros += "Material: " + material.getDescricao() + "; ";
        }
        return facade.buscarDados(filtro, mascara);
    }

    public enum Apresentacao {
        ANUAL("Anual"),
        MENSAL("Mensal");

        private String descricao;

        Apresentacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
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

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
