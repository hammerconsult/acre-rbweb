package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.contabil.financeiro.ConfigDespesaFolhaEncargos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.RelatorioDespesaFolhaEncargosFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-despesa-folha-encargos", pattern = "/relatorio/despesa-folha-encargos/", viewId = "/faces/financeiro/relatorio/relatoriodespesafolhaencargos.xhtml")
})
public class RelatorioDespesaFolhaEncargosControlador implements Serializable {

    @EJB
    private RelatorioDespesaFolhaEncargosFacade facade;
    private Date dataInicial;
    private Date dataFinal;
    private String filtro;
    private ConfigDespesaFolhaEncargos configDespesaFolhaEncargos;
    private List<ConfigDespesaFolhaEncargos> configuracoes;
    private List<HierarquiaOrganizacional> hierarquias;
    private OpcaoDespesaFolhaEncargos opcaoDespesaFolhaEncargos;
    private ApresentacaoDespesaFolhaEncargos apresentacaoDespesaFolhaEncargos;
    private FonteDeRecursos fonteDeRecursos;
    private EsferaDoPoder esferaDoPoder;

    @URLAction(mappingId = "relatorio-despesa-folha-encargos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = facade.getSistemaFacade().getDataOperacao();
        dataFinal = facade.getSistemaFacade().getDataOperacao();
        filtro = "";
        atualizarConfiguracoes();
        hierarquias = Lists.newArrayList();
        configDespesaFolhaEncargos = null;
    }

    private void atualizarConfiguracoes() {
        configuracoes = facade.buscarConfiguracoes(buscarExercicioPelaDataFinal());
        ordenarConfiguracoes();
    }

    private void ordenarConfiguracoes() {
        Collections.sort(configuracoes, new Comparator<ConfigDespesaFolhaEncargos>() {
            @Override
            public int compare(ConfigDespesaFolhaEncargos o1, ConfigDespesaFolhaEncargos o2) {
                int compare = o2.getTipoDespesaFolhaEncargos().compareTo(o1.getTipoDespesaFolhaEncargos());
                return compare == 0 ? o1.getContaDespesa().getCodigo().compareTo(o2.getContaDespesa().getCodigo()) : compare;
            }
        });
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        verificarDataEmExercicioDiferente(ve, dataInicial, dataFinal, " As datas estão com exercícios diferentes.");
        ve.lancarException();
        if (configuracoes.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma configuração cadastrada para o exercício de " + buscarExercicioPelaDataFinal().getAno() + ".");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
                dto.adicionarParametro("MUNICIPIO", "CÂMARA MUNICIPAL DE RIO BRANCO");
                dto.adicionarParametro("isCamara", Boolean.TRUE);
            } else {
                dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
                dto.adicionarParametro("isCamara", Boolean.FALSE);
            }
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Planejamento");
            dto.adicionarParametro("DIRETORIA", "Diretoria de Orçamento Municipal");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("RELATORIO-DESPESA-FOLHA-E-ENCARGOS");
            dto.adicionarParametro("USER", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("opcaoDespesaFolhaEncargos", opcaoDespesaFolhaEncargos != null ? opcaoDespesaFolhaEncargos.getToDto() : null);
            dto.adicionarParametro("APRESENTACAODESPESAFOLHAENCARGOS", apresentacaoDespesaFolhaEncargos.getToDto());
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/relatorio-despesa-folha-e-encargos/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " até " + DataUtil.getDataFormatada(dataFinal) + " -";
        if (opcaoDespesaFolhaEncargos != null) {
            filtro += " Opção: " + opcaoDespesaFolhaEncargos.getDescricao() + " -";
        }
        parametros.add(new ParametrosRelatorios(null, ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.id ", ":fonteId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtro += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        List<Long> idsUnidades = Lists.newArrayList();
        if (!hierarquias.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else {
            List<HierarquiaOrganizacional> unidadesDoUsuario = facade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", facade.getSistemaFacade().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        if (esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios(" vw.ESFERADOPODER ", ":esferaPoder", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, 1, false));
            filtro += " Poder: " + esferaDoPoder.getDescricao() + " -";
        }
        atualizarFiltrosGerais();
        return parametros;
    }

    private void atualizarFiltrosGerais() {
        filtro = filtro.length() == 0 ? " " : filtro.substring(0, filtro.length() - 1);
    }

    private String getNomeRelatorio() {
        return "RELATORIO-DESPESA-FOLHA-E-ENCARGOS-" + DataUtil.getDataFormatada(dataInicial, "ddMMyyyy") + "A" + DataUtil.getDataFormatada(dataFinal, "ddMMyyyy");
    }

    public List<SelectItem> getOpcoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        for (OpcaoDespesaFolhaEncargos opcao : OpcaoDespesaFolhaEncargos.values()) {
            retorno.add(new SelectItem(opcao, opcao.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposDeDespesa() {
        return Util.getListSelectItemSemCampoVazio(TipoDespesaFolhaEncargos.values());
    }

    public List<SelectItem> getTiposDeApresentacao() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoDespesaFolhaEncargos.values());
    }

    public List<Conta> completarContasDeDespesa(String filtro) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return facade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(filtro.trim(), buscarExercicioPelaDataFinal());
    }

    private Exercicio buscarExercicioPelaDataFinal() {
        return facade.getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataFinal));
    }

    public void limparContaAndUnidade() {
        hierarquias = Lists.newArrayList();
        if (configDespesaFolhaEncargos != null) {
            configDespesaFolhaEncargos.setContaDespesa(null);
        }
        atualizarConfiguracoes();
    }

    public void cancelarConfiguracao() {
        configDespesaFolhaEncargos = null;
    }

    public void instanciarConfiguracao() {
        configDespesaFolhaEncargos = new ConfigDespesaFolhaEncargos();
        configDespesaFolhaEncargos.setExercicio(buscarExercicioPelaDataFinal());
    }

    public void editarConfiguracao(ConfigDespesaFolhaEncargos config) {
        configDespesaFolhaEncargos = (ConfigDespesaFolhaEncargos) Util.clonarObjeto(config);
    }

    public void removerConfiguracao(ConfigDespesaFolhaEncargos config) {
        facade.removerConfiguracao(config);
        configuracoes.remove(config);
    }

    public void adicionarConfiguracao() {
        try {
            validarConfiguracao();
            configDespesaFolhaEncargos.setExercicio(buscarExercicioPelaDataFinal());
            Util.adicionarObjetoEmLista(configuracoes, facade.salvarConfiguracao(configDespesaFolhaEncargos));
            ordenarConfiguracoes();
            cancelarConfiguracao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarConfiguracao() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, configDespesaFolhaEncargos.getContaDespesa(), "O campo Conta de Despesa deve ser informado.");
        verificarCampoNull(ve, configDespesaFolhaEncargos.getTipoDespesaFolhaEncargos(), "O campo Tipo de Despesa deve ser informado.");
        ve.lancarException();
        if (facade.hasConfiguracao(configDespesaFolhaEncargos)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta de Despesa e o Tipo de Despesa selecionada já está adicionada.");
        }
        ve.lancarException();
    }

    public void verificarCampoNull(ValidacaoException ve, Object campo, String mensagemValidacao) {
        if (campo == null) {
            ve.adicionarMensagemDeCampoObrigatorio(mensagemValidacao);
        }
    }

    private void verificarDataEmExercicioDiferente(ValidacaoException ve, Date data, Object campoExercicioStringOuData, String mensagemValidacao) {
        SimpleDateFormat formata = new SimpleDateFormat("yyyy");
        if (campoExercicioStringOuData instanceof Date) {
            campoExercicioStringOuData = formata.format(campoExercicioStringOuData);
        }
        if (!formata.format(data).equals(campoExercicioStringOuData)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao);
        }
    }

    public List<SelectItem> getEsferasDoPoder() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Consolidado"));
        for (EsferaDoPoder edp : EsferaDoPoder.values()) {
            if (!edp.equals(EsferaDoPoder.JUDICIARIO)) {
                toReturn.add(new SelectItem(edp, edp.getDescricao()));
            }
        }
        return toReturn;
    }

    public ConfigDespesaFolhaEncargos getConfigDespesaFolhaEncargos() {
        return configDespesaFolhaEncargos;
    }

    public void setConfigDespesaFolhaEncargos(ConfigDespesaFolhaEncargos configDespesaFolhaEncargos) {
        this.configDespesaFolhaEncargos = configDespesaFolhaEncargos;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<ConfigDespesaFolhaEncargos> getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(List<ConfigDespesaFolhaEncargos> configuracoes) {
        this.configuracoes = configuracoes;
    }

    public OpcaoDespesaFolhaEncargos getOpcaoDespesaFolhaEncargos() {
        return opcaoDespesaFolhaEncargos;
    }

    public void setOpcaoDespesaFolhaEncargos(OpcaoDespesaFolhaEncargos opcaoDespesaFolhaEncargos) {
        this.opcaoDespesaFolhaEncargos = opcaoDespesaFolhaEncargos;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public ApresentacaoDespesaFolhaEncargos getApresentacaoDespesaFolhaEncargos() {
        return apresentacaoDespesaFolhaEncargos;
    }

    public void setApresentacaoDespesaFolhaEncargos(ApresentacaoDespesaFolhaEncargos apresentacaoDespesaFolhaEncargos) {
        this.apresentacaoDespesaFolhaEncargos = apresentacaoDespesaFolhaEncargos;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }
}
