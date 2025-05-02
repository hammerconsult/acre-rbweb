package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
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
import java.math.BigDecimal;
import java.util.*;

@ViewScoped
@ManagedBean(name = "relatorioDemonstrativoEncargosPatronaisRetencoesControlador")
@URLMappings(mappings = {
    @URLMapping(id = "relatorioDemonstrativoEncargosPatronaisRetencoes", pattern = "/relatorio/demonstrativo-encargos-patronais-retencoes/", viewId = "/faces/rh/relatorios/demonstrativoencargospatronaisretencoes.xhtml")
})
public class RelatorioDemonstrativoEncargosPatronaisRetencoesControlador extends AbstractReport implements Serializable {
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private Integer mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private Integer versao;
    private Entidade entidade;
    private List<Entidade> entidades;
    private List<GrupoRecursoFP> grupoRecursoFPs;
    private TipoEncargo tipoEncargo;
    private Boolean analitico;
    private BigDecimal aliquotaSuplementar;
    private BigDecimal multaJuros;
    private GrupoRecursoFP[] recursosSelecionados;

    @URLAction(mappingId = "relatorioDemonstrativoEncargosPatronaisRetencoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        versao = null;
        entidade = null;
        entidades = Lists.newArrayList();
        grupoRecursoFPs = Lists.newArrayList();
        tipoEncargo = null;
        analitico = Boolean.FALSE;
        recursosSelecionados = new GrupoRecursoFP[]{};
        aliquotaSuplementar = BigDecimal.ZERO;
        multaJuros = BigDecimal.ZERO;
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    public StreamedContent gerarExcel() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.setApi("rh/demonstrativo-encargos-patronais-retencoes/excel/");
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", "RELATORIO_DEMONSTRATIVO_DE_ENCARGOS_PATRONAIS_E_RETENCOES.xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/demonstrativo-encargos-patronais-retencoes/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("NOMEENCARGOS", getNomeEncargos());
        dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.adicionarParametro("MES", (Mes.getMesToInt(getMes()).getNumeroMesIniciandoEmZero()));
        dto.adicionarParametro("ANO", getAno());
        dto.adicionarParametro("DATAREFERENCIA", DataUtil.criarDataUltimoDiaMes(Mes.getMesToInt(mes).getNumeroMes(), ano).toDate());
        dto.adicionarParametro("TIPOFOLHA", getTipoFolhaDePagamento().getDescricao());
        dto.adicionarParametro("VERSAO", versao == null ? "" : versao.toString());
        dto.adicionarParametro("ANALITICO", analitico);
        dto.adicionarParametro("ALIQ_SUPLEMENTAR", aliquotaSuplementar != null ? aliquotaSuplementar.toString() : BigDecimal.ZERO.toString());
        dto.adicionarParametro("MULTA_JUROS", multaJuros != null ? multaJuros.toString() : BigDecimal.ZERO.toString());
        dto.adicionarParametro("WHERE", getComplementoQuery());
        dto.adicionarParametro("VALORSEGURADO", getValorSegurado());
        dto.adicionarParametro("VALORSEGURADOSINTETICO", getValorSeguradoSintetico());
        dto.adicionarParametro("TIPOENCARGO", tipoEncargo.codigo);
        return dto;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é deve ser informado.");
        }
        if (!isMesValido()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Mês deve ser entre 1 e 12.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (entidades.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Estabelecimento deve ser informado.");
        }
        if (tipoEncargo == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Encargo deve ser informado.");
        }
        if (recursosSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione O(s) Grupo(s) de Recurso Folha de Pagamento.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private boolean isMesValido() {
        if (mes != null) {
            if (mes.compareTo(1) < 0 || mes.compareTo(12) > 0) {
                return false;
            }
        }
        return true;
    }

    private String getNomeRelatorio() {
        return "Relatório Demonstrativo de Encargos Patronais e Retenções";
    }

    private String getNomeEncargos() {
        if (TipoEncargo.AC_PREVIDENCIA.equals(tipoEncargo)) {
            return "Encargos do AC Previdência";
        } else if (TipoEncargo.RBPREV.equals(tipoEncargo)) {
            return "Encargos do RBPrev";
        } else {
            return " Encargos do INSS";
        }
    }

    private String getValorSegurado() {
        if (TipoEncargo.RBPREV.equals(tipoEncargo)) {
            return " ficha.baserpps as basePrevidencia, ficha.rpps as valorSegurado,  ficha.referenciarpps as referenciaSegurado, ";
        } else if (TipoEncargo.AC_PREVIDENCIA.equals(tipoEncargo)) {
            return " ficha.baseoutrasprevidencias as basePrevidencia, ficha.valoroutrasprevidencias as valorSegurado,  ficha.referenciaoutrasprevidencias as referenciaSegurado, ";
        }
        return " ";
    }

    private String getValorSeguradoSintetico() {
        if (TipoEncargo.RBPREV.equals(tipoEncargo)) {
            return " sum(ficha.BASERPPS) as basePrevidencia,  sum(ficha.rpps) as valorSegurado, ";
        } else if (TipoEncargo.AC_PREVIDENCIA.equals(tipoEncargo)) {
            return " sum(ficha.baseoutrasprevidencias) as basePrevidencia,  sum(ficha.valoroutrasprevidencias) as valorSegurado, ";
        }
        return " ";
    }

    public String getComplementoQuery() {
        StringBuilder filtros = new StringBuilder();
        filtros.append(" where trunc(previdencia.inicioVigencia) = (select max(trunc(inicioVigencia)) from previdenciavinculofp prev where prev.CONTRATOFP_ID = v.id) ");
        if (mes != null) {
            filtros.append(" and ficha.mes = ").append(Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        }
        if (ano != null) {
            filtros.append(" and ficha.ano = ").append(ano);
        }
        if (tipoFolhaDePagamento != null) {
            filtros.append(" and ficha.tipofolhadepagamento = '").append(tipoFolhaDePagamento.name()).append("'");
        }
        if (versao != null) {
            filtros.append(" and folha.versao = ").append(versao);
        }
        if (recursosSelecionados.length != 0) {
            String idsGrupoRecurso = "";
            for (GrupoRecursoFP grupoRecursoFP : recursosSelecionados) {
                idsGrupoRecurso += grupoRecursoFP.getId() + ",";
            }
            filtros.append(" and grupo.id in (").append(idsGrupoRecurso.substring(0, idsGrupoRecurso.length() - 1)).append(")");
        }
        if (tipoEncargo != null) {
            filtros.append(" and tipoprev.CODIGO = ").append(tipoEncargo.codigo);
        }
        return filtros.toString();
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public List<GrupoRecursoFP> getGrupoRecursoFPs() {
        return grupoRecursoFPs;
    }

    public void setGrupoRecursoFPs(List<GrupoRecursoFP> grupoRecursoFPs) {
        this.grupoRecursoFPs = grupoRecursoFPs;
    }

    public TipoEncargo getTipoEncargo() {
        return tipoEncargo;
    }

    public void setTipoEncargo(TipoEncargo tipoEncargo) {
        this.tipoEncargo = tipoEncargo;
    }

    public Boolean getAnalitico() {
        return analitico;
    }

    public void setAnalitico(Boolean analitico) {
        this.analitico = analitico;
    }

    public BigDecimal getAliquotaSuplementar() {
        return aliquotaSuplementar != null ? aliquotaSuplementar : BigDecimal.ZERO;
    }

    public void setAliquotaSuplementar(BigDecimal aliquotaSuplementar) {
        this.aliquotaSuplementar = aliquotaSuplementar;
    }

    public BigDecimal getMultaJuros() {
        return multaJuros != null ? multaJuros : BigDecimal.ZERO;
    }

    public void setMultaJuros(BigDecimal multaJuros) {
        this.multaJuros = multaJuros;
    }

    public GrupoRecursoFP[] getRecursosSelecionados() {
        return recursosSelecionados;
    }

    public void setRecursosSelecionados(GrupoRecursoFP[] recursosSelecionados) {
        this.recursosSelecionados = recursosSelecionados;
    }

    public List<Entidade> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<Entidade> entidades) {
        this.entidades = entidades;
    }

    public List<SelectItem> getTiposFolhasPagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<SelectItem> getVersoes() {
        if (mes != null && getAno() != null && getTipoFolhaDePagamento() != null) {
            return Util.getListSelectItem(folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes), getAno(), getTipoFolhaDePagamento()), false);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> buscarEntidades() {
        return Util.getListSelectItem(entidadeFacade.buscarEntidadesDeclarantesVigentePorCategoria(CategoriaDeclaracaoPrestacaoContas.RELATORIOS), false);
    }

    public void carregarGrupoRecursosFPPorEntidade() {
        if (buscarEntidades() != null) {
            List<GrupoRecursoFP> grupoRecursoFPs = Lists.newLinkedList();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
                for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional)) {
                    if (!grupoRecursoFPs.contains(grupoRecursoFP)) {
                        grupoRecursoFPs.add(grupoRecursoFP);
                    }
                }
            }
            setGrupoRecursoFPs(grupoRecursoFPs);
            RequestContext.getCurrentInstance().update("Formulario:tab-view-geral:grupo-recurso");
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasDaEntidade() {
        List<HierarquiaOrganizacional> hierarquias = new ArrayList<>();
        for (Entidade ent : entidades) {
            hierarquias.addAll(entidadeFacade.buscarHierarquiasDaEntidade(ent, CategoriaDeclaracaoPrestacaoContas.RELATORIOS, DataUtil.primeiroDiaMes(DataUtil.criarDataComMesEAno(mes, ano).toDate()).getTime(), DataUtil.ultimoDiaMes(DataUtil.criarDataComMesEAno(mes, ano).toDate()).getTime()));
        }
        Collections.sort(hierarquias);
        return hierarquias;
    }

    public void adicionarEntidade() {
        entidades.add(entidade);
        carregarGrupoRecursosFPPorEntidade();
        entidade = null;
    }

    public void removerEntidade(Entidade e) {
        entidades.remove(e);
        carregarGrupoRecursosFPPorEntidade();
    }

    public List<SelectItem> getTiposEncargos() {
        return Util.getListSelectItem(TipoEncargo.values(), false);
    }

    public enum TipoEncargo {
        INSS("INSS", "1"),
        RBPREV("RBPrev", "3"),
        AC_PREVIDENCIA("AC Previdência", "4");
        private String descricao;
        private String codigo;

        TipoEncargo(String descricao, String codigo) {
            this.descricao = descricao;
            this.codigo = codigo;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getCodigo() {
            return codigo;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
