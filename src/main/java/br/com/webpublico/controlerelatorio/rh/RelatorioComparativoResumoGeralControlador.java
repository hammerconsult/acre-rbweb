package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.rh.EventoRelatorioComparativoResumoGeralPorOrgao;
import br.com.webpublico.entidadesauxiliares.rh.OrgaoRelatorioComparativoResumoGeralPorOrgao;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.relatoriofacade.rh.RelatorioComparativoResumoGeralFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tharlyson on 03/04/19.
 */

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-comparativo-resumo-geral", pattern = "/relatorio/relatorio-comparativo-resumo-geral/", viewId = "/faces/rh/relatorios/relatorio-comparativo-resumo-geral.xhtml")
})
public class RelatorioComparativoResumoGeralControlador extends AbstractReport {

    @EJB
    private RelatorioComparativoResumoGeralFacade relatorioComparativoResumoGeralFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private String filtros;
    private Integer mesComparativoUm;
    private Integer anoComparativoUm;
    private Integer mesComparativoDois;
    private Integer anoComparativoDois;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<GrupoRecursoFP> grupos;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Opcao opcao;
    private String agrupadoPorOrgao;
    private Integer versao;

    public RelatorioComparativoResumoGeralControlador(){
        geraNoDialog = Boolean.TRUE;
    }

    @URLAction(mappingId = "relatorio-comparativo-resumo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtros = "";
        mesComparativoUm = null;
        anoComparativoUm = null;
        mesComparativoDois = null;
        anoComparativoDois = null;
        tipoFolhaDePagamento = TipoFolhaDePagamento.NORMAL;
        versao = null;
        anularHierarquiaOrganizacional();
        instanciarListaDeGrupos();
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getOpcoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(Opcao.GERAL, Opcao.GERAL.getDescricao()));
        return retorno;
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return relatorioComparativoResumoGeralFacade.getHierarquiaOrganizacionalFacade().filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getData());
    }

    public void carregarRecursosFPPorHierarquiaOrganizacional() {
        try {
            instanciarListaDeGrupos();
            validarCampos();
            List<GrupoRecursoFP> gruposPorOrgao = getGruposPorOrgao(hierarquiaOrganizacional);
            validaGrupoDeRecurso();
            for (GrupoRecursoFP grupoRecursoFP : gruposPorOrgao) {
                if (relatorioComparativoResumoGeralFacade.getGrupoRecursoFPFacade().existeEventoFPParaGrupoAndMesAndAnoAndTipoFolha(grupoRecursoFP, mesComparativoUm, anoComparativoUm, tipoFolhaDePagamento)) {
                    grupos.add(grupoRecursoFP);
                }
            }

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validaGrupoDeRecurso() {
        ValidacaoException valida = new ValidacaoException();
        if (mesComparativoUm == null || anoComparativoUm == null) {
            cancelarAgrupadorPorOrgaoAndHierarquiaOrganizacionalAndGrupos();
            valida.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório informar mês e ano para filtrar os grupos de recurso fp.");
        }
        valida.lancarException();

    }

    public List<GrupoRecursoFP> getGruposPorOrgao(HierarquiaOrganizacional hierarquiaOrganizacional) {

            return relatorioComparativoResumoGeralFacade.getGrupoRecursoFPFacade()
                .buscarGruposRecursoFPPorHierarquiaAndMesAndAnoAndVersaoAndTipoFolha(hierarquiaOrganizacional, mesComparativoUm, anoComparativoUm, tipoFolhaDePagamento, versao);
    }

    private void instanciarListaDeGrupos() {

        grupos = new ArrayList<>();
    }

    public void marcarGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        grupoRecursoFP.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        grupoRecursoFP.setSelecionado(Boolean.FALSE);
    }

    public void marcarTodosGrupos() {
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setSelecionado(Boolean.TRUE);
        }
    }
    public void desmarcarTodosGrupos() {
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setSelecionado(Boolean.FALSE);
        }
    }
    public boolean todosGruposEstaoMarcados() {
        for (GrupoRecursoFP grupo : grupos) {
            if (!grupo.getSelecionado()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMesValido() {
        if (temMesInformado()) {
            if (mesComparativoUm.compareTo(getValorMesJaneiro()) < 0 || mesComparativoUm.compareTo(getValorMesDezembro()) > 0
                && mesComparativoDois.compareTo(getValorMesJaneiro()) < 0 || mesComparativoDois.compareTo(getValorMesDezembro()) > 0) {
                return false;
            }
        }
        return true;
    }

    public void gerarRelatorioFiltro() {
        try {
            HashMap parameters = new HashMap();
            validarCampos();
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            parameters.put("RAIZHIERARQUIA", hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao()).toString());

            parameters.put("EXERCICIO", relatorioComparativoResumoGeralFacade.getSistemaFacade().getExercicioCorrente().getAno() + "");
            if (relatorioComparativoResumoGeralFacade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USUARIO", relatorioComparativoResumoGeralFacade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameters.put("USUARIO", relatorioComparativoResumoGeralFacade.getSistemaFacade().getUsuarioCorrente().getLogin());
            }
            String img = getCaminhoImagem() + "Brasao_de_Rio_Branco.gif";
            parameters.put("IMAGEM", img);
            parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            parameters.put("NOMERELATORIO", getNomeRelatorio());
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("MES", DataUtil.getDescricaoMes(mesComparativoUm));
            parameters.put("MESCOMPARATIVODOIS", DataUtil.getDescricaoMes(mesComparativoDois));
            parameters.put("ANO", anoComparativoUm);
            parameters.put("ANOCOMPARATIVODOIS", anoComparativoDois);
            parameters.put("OPCAO", opcao.getDescricao());
            parameters.put("VERSAO", getVersao() != null ? getVersao().toString() : "Todas as Versões");

            JRBeanCollectionDataSource ds = getJrBeanCollectionDataSource();
            parameters.put("FILTROS", filtros);
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorio(), getNomeDoArquivo(), parameters, ds);
        } catch (ValidacaoException valida) {
            FacesUtil.printAllFacesMessages(valida.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public JRBeanCollectionDataSource getJrBeanCollectionDataSource() {
        return isOpcaoGeral() && naoAgrupadoPorOrgao() ? new JRBeanCollectionDataSource(gerarRelatorioGeral()) : new JRBeanCollectionDataSource(gerarRelatorioPorSecretaria());
    }

    private List<OrgaoRelatorioComparativoResumoGeralPorOrgao> gerarRelatorioPorSecretaria() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        atribuirParametroMesComparativoDois(parametros);
        atribuirParametroAnoComparativoDois(parametros);
        atribuirParametroMesComparativoUm(parametros);
        atribuirParametroAnoComparativoUm(parametros);
        atribuirParametroTipoFolha(parametros);
        atribuirParametroOrgao(parametros);
        atribuirParametroRecursoFP(parametros);
        atribuirParametroVersaoFolha(parametros);
        if (temOpcaoInformada()) {

            filtros += " Opção: " + opcao.getDescricao();
        }
        return relatorioComparativoResumoGeralFacade.recuperarRelatorio(parametros, opcao);
    }

    private List<EventoRelatorioComparativoResumoGeralPorOrgao> gerarRelatorioGeral() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();

        atribuirParametroMesComparativoDois(parametros);
        atribuirParametroAnoComparativoDois(parametros);
        atribuirParametroMesComparativoUm(parametros);
        atribuirParametroAnoComparativoUm(parametros);
        atribuirParametroTipoFolha(parametros);
        atribuirParametroVersaoFolha(parametros);

        if (temOpcaoInformada()) {
            filtros += " Opção: " + opcao.getDescricao();
        }
        return relatorioComparativoResumoGeralFacade.buscarRelatorioGeral(parametros);
    }

    private void atribuirParametroRecursoFP(List<ParametrosRelatorios> parametros) {
        if (temGrupoRecursoFPMarcado()) {
            parametros.add(new ParametrosRelatorios(" gru.id ", ":idGrupo", null, OperacaoRelatorio.IN, getIdsGruposMarcados(), null, 2, false));
        }
    }

    private List<Long> getIdsGruposMarcados() {
        List<Long> ids = new ArrayList<>();

        for (GrupoRecursoFP grupo : grupos) {
            if (grupo.getSelecionado()) {
                ids.add(grupo.getId());
            }
        }
        return ids;
    }

    private void atribuirParametroOrgao(List<ParametrosRelatorios> parametros) {
        if (temHierarquiaOrganizacionalInformada()) {
            parametros.add(new ParametrosRelatorios(" vw.id ", ":idOrgao", null, OperacaoRelatorio.IGUAL, hierarquiaOrganizacional.getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" rec.codigoOrgao ", ":cod2nivel", null, OperacaoRelatorio.IGUAL, hierarquiaOrganizacional.getCodigoDo2NivelDeHierarquia(), null, 2, false));
            filtros += " Órgão: " + hierarquiaOrganizacional.toString();
        }
    }

    private void atribuirParametroTipoFolha(List<ParametrosRelatorios> parametros) {
        if (temTipoFolhaInformado()) {
            parametros.add(new ParametrosRelatorios(" folha.tipoFolhaDePagamento ", ":tipo", null, OperacaoRelatorio.IGUAL, tipoFolhaDePagamento.name(), null, 2, false));
            filtros = " Tipo Folha: " + tipoFolhaDePagamento.getDescricao();
        }
    }

    private void atribuirParametroVersaoFolha(List<ParametrosRelatorios> parametros) {
        if (temVersaoFolhaInformada()) {
            parametros.add(new ParametrosRelatorios(" folha.versao ", ":versao", null, OperacaoRelatorio.IGUAL, versao, null, 2, false));
            filtros = " Versão da Folha: " + versao;
        }
    }

    private void atribuirParametroAnoComparativoDois(List<ParametrosRelatorios> parametros) {
        if (temAnoInformado()) {

            parametros.add(new ParametrosRelatorios(" folha.ano ", ":anoComparativoDois", null, OperacaoRelatorio.IGUAL, anoComparativoDois, null, 2, false));
        }
    }

    private void atribuirParametroMesComparativoDois(List<ParametrosRelatorios> parametros) {
        if (temMesInformado()) {
            parametros.add(new ParametrosRelatorios(" folha.mes ", ":mesComparativoDois", null, OperacaoRelatorio.IGUAL, mesComparativoDois - 1, null, 2, false));
        }
    }

    private void atribuirParametroAnoComparativoUm(List<ParametrosRelatorios> parametros) {
        if (temAnoInformado()) {

            parametros.add(new ParametrosRelatorios(" folha.ano ", ":anoComparativoUm", null, OperacaoRelatorio.IGUAL, anoComparativoUm, null, 3, false));
        }
    }

    private void atribuirParametroMesComparativoUm(List<ParametrosRelatorios> parametros) {
        if (temMesInformado()) {
            parametros.add(new ParametrosRelatorios(" folha.mes ", ":mesComparativoUm", null, OperacaoRelatorio.IGUAL, mesComparativoUm - 1, null, 3, false));
        }
    }

    private void validarCampos() {

        ValidacaoException valida = new ValidacaoException();

        if (!temMesInformado()) {
            valida.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (!isMesValido()) {
            valida.adicionarMensagemDeOperacaoNaoPermitida("O campo Mês deve ser entre 1 e 12.");
        }
        if (!temAnoInformado()) {
            valida.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (!temTipoFolhaInformado()) {
            valida.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (!temOpcaoInformada()) {
            valida.adicionarMensagemDeCampoObrigatorio("O campo Opções deve ser informado.");
        }
        if (!validarAgrupadoPor()) {
            valida.adicionarMensagemDeCampoObrigatorio("Informe se o relatório será agrupado por Órgão ou não.");
        }
        if (!validarHierarquiaOrganizacional()) {
            valida.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (mesComparativoUm == mesComparativoDois){
            valida.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um mês diferente");
        }
        valida.lancarException();
    }

    private boolean validarAgrupadoPor() {
        if (isOpcaoGeral()) {
            if (agrupadoPorOrgao == null || agrupadoPorOrgao.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAgrupudoPorOrgao() {

        return "AGRUPADO_POR_ORGAO".equals(agrupadoPorOrgao);
    }

    public boolean naoAgrupadoPorOrgao() {

        return !isAgrupudoPorOrgao();
    }

    public boolean isOpcaoGeral() {
        return Opcao.GERAL.equals(opcao);
    }

    public boolean isOpcaoSecretaria() {
        return Opcao.SECRETARIA.equals(opcao);
    }

    public List<GrupoRecursoFP> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRecursoFP> grupos) {
        this.grupos = grupos;
    }

    public void cancelarAgrupadorPorOrgaoAndHierarquiaOrganizacionalAndGrupos() {
        agrupadoPorOrgao = null;
        setHierarquiaOrganizacional(null);
        instanciarListaDeGrupos();
    }

    public enum Opcao {
        GERAL("Geral"),
        SECRETARIA("Secretaria");
        private String descricao;

        Opcao(String descricao) { this.descricao = descricao; }

        public String getDescricao() { return descricao; }
    }

    private Date getData() {
        if (getMesComparativoUm() != null && getAnoComparativoUm() != null) {
            return DataUtil.montaData(1, getValorMesJaneiro(), getAnoComparativoUm()).getTime();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public void limparHierarquia() {
        setHierarquiaOrganizacional(null);
        grupos = Lists.newArrayList();
    }

    public List<SelectItem> getVersoes(){
        List<SelectItem> retorno = new ArrayList<>();
        if (mesComparativoUm != null && anoComparativoUm != null || mesComparativoDois != null || anoComparativoDois != null){
            retorno.add(new SelectItem(null, "Todas"));
            for (Integer versao: relatorioComparativoResumoGeralFacade.getFolhaDePagamentoFacade().recuperarVersao(Mes.getMesToInt(mesComparativoUm), anoComparativoUm, tipoFolhaDePagamento)){
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public Integer getValorMesJaneiro() { return 1; }

    public Integer getValorMesDezembro() { return 12; }

    private boolean temOpcaoInformada() {
        return opcao != null;
    }

    private boolean validarHierarquiaOrganizacional() {
        if (isOpcaoSecretaria()) {
            if (!temHierarquiaOrganizacionalInformada()) {
                return false;
            }
        }
        return true;
    }

    private boolean temTipoFolhaInformado() {
        return tipoFolhaDePagamento != null;

    }

    private boolean temVersaoFolhaInformada() {
        return versao != null;
    }

    private boolean temAnoInformado() {
        return anoComparativoUm != null && anoComparativoDois !=null;
    }

    private boolean temMesInformado() {
        return mesComparativoUm != null && mesComparativoDois != null;
    }

    private boolean temHierarquiaOrganizacionalInformada() {
        return hierarquiaOrganizacional != null;

    }

    private boolean temGrupoRecursoFPMarcado() {
        for (GrupoRecursoFP grupo : grupos) {
            if (grupo.getSelecionado()) {
                return true;
            }
        }
        return false;
    }

    public String getNomeRelatorio() {
        return isOpcaoGeral() && naoAgrupadoPorOrgao() ? "RELATÓRIO COMPARATIVO RESUMO GERAL" : "RELATÓRIO COMPARATIVO RESUMO POR ÓRGÃO";
    }

    public String getNomeDoArquivo() {

        return isOpcaoGeral() && naoAgrupadoPorOrgao() ? "RelatorioComparativoResumoGeral.jasper" : "RelatorioComparativoResumoPorOrgao.jasper";
    }

    public void anularHierarquiaOrganizacional() {
        hierarquiaOrganizacional = null;
    }

    public Integer getMesComparativoUm() {
        return mesComparativoUm;
    }

    public void setMesComparativoUm(Integer mesComparativoUm) {
        this.mesComparativoUm = mesComparativoUm;
    }

    public Integer getAnoComparativoUm() {

        return anoComparativoUm;
    }

    public void setAnoComparativoUm(Integer anoComparativoUm) {
        this.anoComparativoUm = anoComparativoUm;
    }

    public Integer getMesComparativoDois() {
        return mesComparativoDois;
    }

    public void setMesComparativoDois(Integer mesComparativoDois) {
        this.mesComparativoDois = mesComparativoDois;
    }

    public Integer getAnoComparativoDois() {
        return anoComparativoDois;
    }

    public void setAnoComparativoDois(Integer anoComparativoDois) {
        this.anoComparativoDois = anoComparativoDois;
    }

    public Opcao getOpcao() {
        return opcao;
    }

    public void setOpcao(Opcao opcao) {
        this.opcao = opcao;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public String getAgrupadoPorOrgao() {
        return agrupadoPorOrgao;
    }

    public void setAgrupadoPorOrgao(String agrupadoPorOrgao) {
        this.agrupadoPorOrgao = agrupadoPorOrgao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }


}
