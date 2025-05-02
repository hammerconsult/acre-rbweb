package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.GrauDetalhamento;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ProjetoAtividadeFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.relatoriofacade.RelatorioContabilSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 26/11/13
 * Time: 08:33
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-quadro-detalhamento-despesa-execucao-orc", pattern = "/relatorio/qdd/execucao-orcamentaria/", viewId = "/faces/financeiro/relatorio/relatorioqddexecucaoorcamentaria.xhtml")
})
@ManagedBean
public class RelatorioQDDExecucaoOrcamentariaControlador implements Serializable {

    @EJB
    private RelatorioContabilSuperFacade relatorioContabilSuperFacade;
    @EJB
    private ProjetoAtividadeFacade acaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private Conta conta;
    private AcaoPPA acaoPPA;
    private FonteDeRecursos fonteDeRecursos;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private List<HierarquiaOrganizacional> unidades;
    private GrauDetalhamento grau;
    private TipoRelatorioApresentacao tipoRelatorio;
    private Boolean analitica;
    private Date inicio;
    private Date fim;
    private String filtros;


    @URLAction(mappingId = "relatorio-quadro-detalhamento-despesa-execucao-orc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        unidades = Lists.newArrayList();
        conta = null;
        acaoPPA = null;
        fonteDeRecursos = null;
        funcao = null;
        subFuncao = null;
        grau = GrauDetalhamento.QUATRO;
        analitica = Boolean.FALSE;
        tipoRelatorio = TipoRelatorioApresentacao.RESUMIDO;
        inicio = buscarPrimeiroDiaAno();
        fim = new Date();
        filtros = "";
    }

    public Date buscarPrimeiroDiaAno() {
        return DataUtil.getPrimeiroDiaAno(Util.getSistemaControlador().getExercicioCorrente().getAno());
    }

    public Date buscarUltimoDiaAno() {
        return DataUtil.getUltimoDiaAno(Util.getSistemaControlador().getExercicioCorrente().getAno());
    }

    public void definirGrauDetalhamentoAnalitico() {
        if (analitica) {
            setGrau(GrauDetalhamento.QUATRO);
        }
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("contabil/qdd-execucao-orcamentaria/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("EXERCICIO", sistemaFacade.getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("TIPO_RELATORIO", tipoRelatorio.name());
        dto.adicionarParametro("DATA_INICIAL", DataUtil.getDataFormatada(inicio));
        dto.adicionarParametro("DATA_FINAL", DataUtil.getDataFormatada(fim));
        dto.adicionarParametro("ANALITICA", analitica);
        dto.adicionarParametro("FILTROS", filtros);
        dto.setNomeRelatorio(getNomeRelatorioAoImprimir());
        return dto;
    }

    public StreamedContent fileDownload() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("contabil/qdd-execucao-orcamentaria/excel/");
            dto.adicionarParametro("NOME_ARQUIVO", getNomeRelatorioAoImprimir());
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xlsx", getNomeRelatorioAoImprimir() + ".xlsx");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    private Exercicio buscarExercicioPelaData() {
        return relatorioContabilSuperFacade.getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(inicio));
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        definirParametrosUnidade(parametros);
        definirParametrosGerais(parametros);
        return parametros;
    }

    private void validarCampos() {
        ValidacaoException ex = new ValidacaoException();
        if (inicio == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Por favor informe a data inicial ");
        }
        if (fim == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Por favor informe a data final");
        }
        ex.lancarException();
        if (inicio.after(fim)) {
            ex.adicionarMensagemDeOperacaoNaoPermitida("A data inicial inicial deve ser inferior à data final ");
        }
        ex.lancarException();
    }

    private String getNomeRelatorioAoImprimir() {
        return "QDD-EXECUÇÃO-ORÇAMENTÁRIA";
    }

    private void definirParametrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATA", null, null, DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, buscarExercicioPelaData().getId(), null, 0, false));

        if (acaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" ac.id ", ":idAcao", null, OperacaoRelatorio.IGUAL, acaoPPA.getId(), null, 1, false));
            filtros += " Projeto Atividade: " + acaoPPA.getDescricao() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":idConta", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta: " + conta.getDescricao().toLowerCase() + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" func.id ", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao.getDescricao() + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sub.id ", ":idSubacao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " Sub Ação: " + subFuncao.getDescricao() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fte.id ", ":idFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 2, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getDescricao() + " -";
        }
        if (!filtros.isEmpty()) {
            filtros = filtros.substring(0, filtros.length() - 1);
        }
        parametros.add(new ParametrosRelatorios(" trunc(alt.dataEfetivacao) ", ":INICIAL", ":FINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(inicio), DataUtil.getDataFormatada(fim), 4, true));
        parametros.add(new ParametrosRelatorios(" trunc(est.dataEstorno) ", ":INICIAL", ":FINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(inicio), DataUtil.getDataFormatada(fim), 5, true));
        parametros.add(new ParametrosRelatorios(" trunc(emp.dataEmpenho) ", ":INICIAL", ":FINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(inicio), DataUtil.getDataFormatada(fim), 6, true));
        parametros.add(new ParametrosRelatorios(" trunc(li.dataLiquidacao) ", ":INICIAL", ":FINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(inicio), DataUtil.getDataFormatada(fim), 7, true));
        parametros.add(new ParametrosRelatorios(" trunc(pag.dataPagamento) ", ":INICIAL", ":FINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(inicio), DataUtil.getDataFormatada(fim), 8, true));

        parametros.add(new ParametrosRelatorios(" nivelestrutura(codigo_conta,'.') ", ":grau", null, OperacaoRelatorio.IGUAL, grau.getGrau(), null, 3, false));
    }

    private void definirParametrosUnidade(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!unidades.isEmpty()) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":unds", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":unds", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
    }

    public List<SelectItem> getTiposRelatorio() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioApresentacao.values());
    }

    public List<SelectItem> getGraus() {
        return Util.getListSelectItemSemCampoVazio(GrauDetalhamento.values());
    }

    public List<AcaoPPA> buscarProjetoAtividade(String parte) {
        return acaoFacade.buscarAcoesPPAPorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<Conta> buscarContaDespesa(String parte) {
        if (analitica) {
            return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoDespesaCriteria(parte, sistemaFacade.getExercicioCorrente());
        }
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte, sistemaFacade.getExercicioCorrente());
    }

    public List<FonteDeRecursos> buscarFonteDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<Funcao> buscarFuncao(String parte) {
        return relatorioContabilSuperFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
    }

    public List<SubFuncao> buscarSubFuncao(String parte) {
        return relatorioContabilSuperFacade.getSubFuncaoFacade().listaFiltrandoSubFuncao(parte.trim());
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public GrauDetalhamento getGrau() {
        return grau;
    }

    public void setGrau(GrauDetalhamento grau) {
        this.grau = grau;
    }

    public Boolean getAnalitica() {
        return analitica;
    }

    public void setAnalitica(Boolean analitica) {
        this.analitica = analitica;
    }

    public TipoRelatorioApresentacao getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioApresentacao tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }
}
