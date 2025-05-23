package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractRelatorioAssincronoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.relatoriofacade.AbstractRelatorioAssincronoFacade;
import br.com.webpublico.relatoriofacade.RelatorioPatrimonioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.*;

@Deprecated// utilizar RelatorioPatrimonioSuperControlador
@ManagedBean(name = "relatorioPatrimonioControlador")
@ViewScoped
public class RelatorioPatrimonioControlador extends AbstractRelatorioAssincronoControlador {

    @EJB
    protected RelatorioPatrimonioFacade relatorioPatrimonioFacade;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioPatrimonioControlador.class);
    protected Bem bem;
    protected HierarquiaOrganizacional hierarquiaOrganizacional;
    protected HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    protected Exercicio exercicioInicial;
    protected Exercicio exercicioFinal;
    protected Integer anoInicial;
    protected Integer anoFinal;
    protected Date dtLiquidacao;
    protected Date dtinicial;
    protected Date dtFinal;
    protected Date dtReferencia = new Date();
    protected GrupoBem grupoBem;
    protected GrupoObjetoCompra grupoObjetoCompra;
    protected EstadoConservacaoBem estadoConservacaoBem;
    protected SituacaoConservacaoBem situacaoConservacaoBem;
    protected UsuarioSistema usuarioSistema;
    protected TipoGrupo tipoGrupo;
    protected TipoAquisicaoBem tipoAquisicaoBem;
    protected TipoInventarioBemMovel tipoInventarioBemMovel;
    protected Date dtNota;
    protected String numeroNotaFiscal;
    protected Date dtEmpenho;
    protected String numeroEmpenho;
    protected String numeroLiquidacao;
    protected String localizacao;
    protected String marca;
    protected String modelo;
    protected ApresentacaoRelatorio apresentacaoRelatorio;
    protected List<Object[]> ordenacaoDisponivel;
    protected Object[][] ordenacaoSelecionada;
    protected StringBuilder where;
    protected StringBuilder filtro;
    protected RequisicaoDeCompra requisicaoDeCompra;
    protected Contrato contrato;
    protected Integer tipoUnidade;
    protected Integer exercicio;
    protected Integer anoEmpenho;
    protected Date dataBaixa;
    protected String observacao;
    protected String numeroNota;
    protected Exercicio exercicioEmpenho;
    protected Boolean detalhar = Boolean.FALSE;
    protected TipoBem tipoBem;

    private FiltrosRelatorioPatrimonio filtrosRelatorioPatrimonio;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private BemFacade bemFacade;

    public RelatorioPatrimonioControlador() {
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
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

    public Integer getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(Integer anoInicial) {
        this.anoInicial = anoInicial;
    }

    public Integer getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(Integer anoFinal) {
        this.anoFinal = anoFinal;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public Boolean getDetalhar() {
        return detalhar;
    }

    public void setDetalhar(Boolean detalhar) {
        this.detalhar = detalhar;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public Date getDtinicial() {
        return dtinicial;
    }

    public void setDtinicial(Date dtinicial) {
        this.dtinicial = dtinicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public TipoInventarioBemMovel getTipoInventarioBemMovel() {
        return tipoInventarioBemMovel;
    }

    public void setTipoInventarioBemMovel(TipoInventarioBemMovel tipoInventarioBemMovel) {
        this.tipoInventarioBemMovel = tipoInventarioBemMovel;
    }

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public ApresentacaoRelatorio getApresentacaoRelatorio() {
        return apresentacaoRelatorio;
    }

    public void setApresentacaoRelatorio(ApresentacaoRelatorio apresentacaoRelatorio) {
        this.apresentacaoRelatorio = apresentacaoRelatorio;
    }

    public List<Object[]> getOrdenacaoDisponivel() {
        return ordenacaoDisponivel;
    }

    public void setOrdenacaoDisponivel(List<Object[]> ordenacaoDisponivel) {
        this.ordenacaoDisponivel = ordenacaoDisponivel;
    }

    public Object[][] getOrdenacaoSelecionada() {
        return ordenacaoSelecionada;
    }

    public void setOrdenacaoSelecionada(Object[][] ordenacaoSelecionada) {
        this.ordenacaoSelecionada = ordenacaoSelecionada;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public Date getDtReferencia() {
        return dtReferencia;
    }

    public void setDtReferencia(Date dtReferencia) {
        this.dtReferencia = dtReferencia;
    }

    public Integer getAnoEmpenho() {
        return anoEmpenho;
    }

    public void setAnoEmpenho(Integer anoEmpenho) {
        this.anoEmpenho = anoEmpenho;
    }

    public Date getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(Date dataBaixa) {
        this.dataBaixa = dataBaixa;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public void limparCampos() {
        dtReferencia = getSistemaFacade().getDataOperacao();
        hierarquiaOrganizacional = null;
        hierarquiaOrganizacionalOrcamentaria = null;
        grupoObjetoCompra = null;
        grupoBem = null;
        tipoGrupo = null;
        situacaoConservacaoBem = null;
        estadoConservacaoBem = null;
        exercicioInicial = null;
        exercicioFinal = null;
        anoInicial = null;
        anoFinal = null;
        dtinicial = null;
        dtFinal = null;
        ordenacaoSelecionada = null;
        tipoAquisicaoBem = null;
        tipoInventarioBemMovel = null;
        numeroNotaFiscal = "";
        numeroEmpenho = null;
        localizacao = "";
        marca = "";
        modelo = "";
        apresentacaoRelatorio = ApresentacaoRelatorio.UNIDADE;
        numeroLiquidacao = null;
        dtLiquidacao = null;
        dtEmpenho = null;
        dtNota = null;
        requisicaoDeCompra = null;
        contrato = null;
        tipoUnidade = 0;
        exercicio = 0;
        anoEmpenho = null;
        dataBaixa = null;
        observacao = null;
        numeroNota = null;
        exercicioEmpenho = null;
        bem = null;
    }

    public void montaWhereRelatorioHistoricoBem() {
        where = new StringBuilder();
        where.append("WHERE DADOS.ID_BEM = ").append(bem.getId());
        filtro = new StringBuilder();
        filtro.append("Critérios Utilizados: ").append(" Bem: ").append(bem.toString());
        if (hierarquiaOrganizacional != null) {
            where.append(" AND DADOS.ID_VWADM = ").append(hierarquiaOrganizacional.getId());
            filtro.append(" Unidade Administrativa: ").append(hierarquiaOrganizacional.getSubordinada()).append("; ");
        }
        if (hierarquiaOrganizacionalOrcamentaria != null) {
            where.append(" AND DADOS.ID_VWORC = ").append(hierarquiaOrganizacionalOrcamentaria.getId());
            filtro.append(" Unidade Orçamentária: ").append(hierarquiaOrganizacionalOrcamentaria.getSubordinada()).append("; ");
        }
        if (dtinicial != null) {
            where.append(" AND TO_DATE(TO_CHAR(DADOS.DATALANCAMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') >= to_date('").append(Util.dateToString(dtinicial)).append("', 'dd/MM/yyyy') ");
            filtro.append(" Data de Operação Inicial: ").append(Util.dateToString(dtinicial)).append("; ");
        }
        if (dtFinal != null) {
            where.append(" AND TO_DATE(TO_CHAR(DADOS.DATALANCAMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') <= to_date('").append(Util.dateToString(dtFinal)).append("', 'dd/MM/yyyy') ");
            filtro.append(" Data de Operação Final: ").append(Util.dateToString(dtFinal)).append("; ");
        }
    }

    public void gerarRelatorioHistoricoBem(TipoBem tipoBem, String tipoRelatorioExtensao) {
        try {
            validarFiltroHistoricoBem();
            montaWhereRelatorioHistoricoBem();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("HISTÓRICO DO BEM " + tipoBem.getSingular().toUpperCase());
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", "SETOR PATRIMONIAL");
            dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("ID_USUARIOSISTEMA", getSistemaFacade().getUsuarioCorrente().getId());
            dto.adicionarParametro("WHERE", where.toString());
            dto.adicionarParametro("FILTROS", filtro.toString());
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("APRESENTACAO", apresentacaoRelatorio.getToDto());
            dto.setApi("administrativo/historico-bem/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarFiltroHistoricoBem() {
        ValidacaoException ve = new ValidacaoException();
        if (this.bem == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Bem deve ser informado.");
        }
        if (dtinicial != null && dtFinal != null) {
            if (dtinicial.after(dtFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser anterior a data final.");
            }
        }
        ve.lancarException();
    }

    public String montaNomeSecretaria() {
        String nome = "";
        if (hierarquiaOrganizacional != null) {
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(this.hierarquiaOrganizacional, getSistemaFacade().getDataOperacao());
            if (secretaria == null) {
                throw new ExcecaoNegocioGenerica("Secretaria não encontrada para hierarquia administrativa " + hierarquiaOrganizacional + ".");
            }
            nome = secretaria.getDescricao().toUpperCase();
        }
        if (hierarquiaOrganizacional == null && hierarquiaOrganizacionalOrcamentaria != null) {
            HierarquiaOrganizacional hierararAdm = hierarquiaOrganizacionalFacade.recuperarAdministrativaDaOrcamentariaVigente(this.hierarquiaOrganizacionalOrcamentaria, getSistemaFacade().getDataOperacao());
            if (hierararAdm == null) {
                throw new ExcecaoNegocioGenerica("Hierarquia Administrativa não encontrada para hierarquia orçamentária " + hierarquiaOrganizacionalOrcamentaria + ".");
            }
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierararAdm, getSistemaFacade().getDataOperacao());
            if (secretaria == null) {
                throw new ExcecaoNegocioGenerica("Secretaria não encontrada para hierarquia administrativa " + hierararAdm + ".");
            }
            nome = secretaria.getDescricao().toUpperCase();
        }
        return nome;
    }

    @Override
    public String montarNomeDoMunicipio() {
        return "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase();
    }

    public String montarNomeDaEntidade() {
        String nome = "";
        if (hierarquiaOrganizacional != null) {
            nome = entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada()).getNome().toUpperCase();
        }

        if (hierarquiaOrganizacional == null && hierarquiaOrganizacionalOrcamentaria != null) {
            nome = entidadeFacade.recuperarEntidadePorUnidadeOrcamentaria(hierarquiaOrganizacionalOrcamentaria.getSubordinada()).getNome().toUpperCase();
        }

        return nome;
    }

    public String montaNomeOrgao() {
        String nome = "";
        if (hierarquiaOrganizacional != null) {
            nome = hierarquiaOrganizacional.toString();
        }

        if (hierarquiaOrganizacional == null && hierarquiaOrganizacionalOrcamentaria != null) {
            nome = hierarquiaOrganizacionalOrcamentaria.toString();
        }

        return nome;
    }

    public String montaNomeOrgaoSemCodigo() {
        String nome = "";
        if (hierarquiaOrganizacional != null) {
            nome = hierarquiaOrganizacional.getSubordinada().getDescricao();
        }

        if (hierarquiaOrganizacional == null && hierarquiaOrganizacionalOrcamentaria != null) {
            nome = hierarquiaOrganizacionalOrcamentaria.getSubordinada().getDescricao();
        }

        return nome;
    }

    public void gerarRelatorioLevantamentosEfetivadosPorLoteEfetivacao(LoteEfetivacaoLevantamentoBem loteEfetivacaoLevantamentoBem) {
        try {
            if (loteEfetivacaoLevantamentoBem != null) {
                RelatorioDTO dto = new RelatorioDTO();
                StringBuffer filtros = new StringBuffer();
                dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
                dto.adicionarParametro("MODULO", "PATRIMÔNIO");
                dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
                dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
                dto.adicionarParametro("NOMERELATORIO", "EFETIVAÇÃO DE LEVANTAMENTOS DE BENS MÓVEIS N°:" + loteEfetivacaoLevantamentoBem.getCodigo());
                dto.adicionarParametro("BRASAO", getCaminhoImagem());
                dto.adicionarParametro("CONDICAO", " AND EF.LOTE_ID = " + loteEfetivacaoLevantamentoBem.getId());
                dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(loteEfetivacaoLevantamentoBem.getDataEfetivacao()));
                dto.adicionarParametro("FILTROS", filtros.append("Unidade Orçametária: ").append(getDescricaoHierarquia(loteEfetivacaoLevantamentoBem)).append(". ").toString());
                dto.setNomeRelatorio("RELATÓRIO DE LEVANTAMENTOS DE BENS MÓVEIS EFETIVADOS");
                dto.setApi("administrativo/levantamento-bem-movel-efetivado/");
                ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private String getDescricaoHierarquia(LoteEfetivacaoLevantamentoBem loteEfetivacao) {
        return hierarquiaOrganizacionalFacade.buscarCodigoDescricaoHierarquia(
            TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
            loteEfetivacao.getUnidadeOrcamentaria(),
            loteEfetivacao.getDataEfetivacao());
    }

    public void termoDeResponsabilidade(EfetivacaoSolicitacaoIncorporacaoMovel incorporacaoBem) {
        try {
            String nomeArquivo = "TermoDeResponsabilidade.jasper";
            HashMap parameters = new HashMap();
            parameters.put("MODULO", "Patrimônio");
            parameters.put("SECRETARIA", "SETOR PATRIMONIAL");
            parameters.put("NOMERELATORIO", "TERMO DE RESPONSABILIDADE");
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("WHERE", " WHERE EV.ID = " + incorporacaoBem.getId());
            atribuirUsuarioLogado(parameters);
            gerarRelatorio(nomeArquivo, parameters);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    public void termoDeResponsabilidadeAquisicao(Aquisicao aquisicao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "TERMO DE RESPONSABILIDADE";
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", "SETOR PATRIMONIAL");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", " WHERE AQUISICAO.ID = " + aquisicao.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/termo-responsabilidade-aquisicao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hierarquiaOrganizacional != null && hierarquiaOrganizacional.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacional.getSubordinada(), getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }

        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaSelectItemEstadosDeConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }

    public List<SelectItem> getTiposGrupo() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoGrupo tipo : TipoGrupo.values()) {
            if (tipo.getClasseDeUtilizacao().equals(BensMoveis.class))
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getListaSelectItemSituacaoConservacaoBem() {
        if (estadoConservacaoBem != null) {
            return Util.getListSelectItem(estadoConservacaoBem.getSituacoes());
        }
        return null;
    }

    public List<SelectItem> getItensSelectTodosTipoDeAquisicaoBem() {
        return Util.getListSelectItem(TipoAquisicaoBem.values());
    }

    public List<SelectItem> getTiposInventarioBemMovel() {
        return Util.getListSelectItem(TipoInventarioBemMovel.values());
    }

    public List<Bem> completarBemMovel(String parte) {
        return bemFacade.completarBem(parte.trim(), TipoBem.MOVEIS, hierarquiaOrganizacional, hierarquiaOrganizacionalOrcamentaria);
    }

    public List<Bem> completarBemMovelComEstornados(String parte) {
        return bemFacade.completarBem(parte.trim(), TipoBem.MOVEIS, hierarquiaOrganizacional, hierarquiaOrganizacionalOrcamentaria, false);
    }


    public List<Bem> completarBemImovel(String parte) {
        return bemFacade.completaBemImovel(parte);
    }

    public List<Bem> completarBemIntangivel(String parte) {
        return bemFacade.completaBemIntangivel(parte);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalAdministrativaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario(parte, null,
            getSistemaFacade().getDataOperacao(), getSistemaFacade().getUsuarioCorrente(), hierarquiaOrganizacionalOrcamentaria, Boolean.TRUE);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalOrcamentariaOndeUsuarioLogadoEhGestorPatrimonio(String parte) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null,
            getSistemaFacade().getDataOperacao(), getSistemaFacade().getUsuarioCorrente(), hierarquiaOrganizacional);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (hierarquiaOrganizacional != null && hierarquiaOrganizacional.getSubordinada() != null) {
            return getUnidadeOrganizacionalFacade().getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacional.getSubordinada(), getSistemaFacade().getDataOperacao());
        }
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(parte, null,
            getSistemaFacade().getDataOperacao(), getSistemaFacade().getUsuarioCorrente(), hierarquiaOrganizacional);
    }

    public void atualizarCamposUnidade() {
        hierarquiaOrganizacional = null;
        hierarquiaOrganizacionalOrcamentaria = null;
    }

    public void atualizarCamposExercicio() {
        exercicioInicial = null;
        exercicioFinal = null;
    }

    public List<SelectItem> buscarApresentacoesDeRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            if (!ap.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
                toReturn.add(new SelectItem(ap, ap.getDescricao()));
            }
        }
        return toReturn;
    }

    public Date getDtLiquidacao() {
        return dtLiquidacao;
    }

    public void setDtLiquidacao(Date dtLiquidacao) {
        this.dtLiquidacao = dtLiquidacao;
    }

    public Date getDtNota() {
        return dtNota;
    }

    public void setDtNota(Date dtNota) {
        this.dtNota = dtNota;
    }

    public Date getDtEmpenho() {
        return dtEmpenho;
    }

    public void setDtEmpenho(Date dtEmpenho) {
        this.dtEmpenho = dtEmpenho;
    }

    public String getNumeroLiquidacao() {
        return numeroLiquidacao;
    }

    public void setNumeroLiquidacao(String numeroLiquidacao) {
        this.numeroLiquidacao = numeroLiquidacao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    public Integer getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(Integer tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Exercicio getExercicioEmpenho() {
        return exercicioEmpenho;
    }

    public void setExercicioEmpenho(Exercicio exercicioEmpenho) {
        this.exercicioEmpenho = exercicioEmpenho;
    }

    public UnidadeOrganizacional getUnidadeAdministrativaLogada() {
        return getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public StringBuilder getFiltro() {
        return filtro;
    }

    public void atribuirUsuarioLogado(HashMap parameters) {
        parameters.put("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
    }

    public void gerarRelatorio(LoteReducaoValorBem loteReducaoValorBem) {
        filtrosRelatorioPatrimonio = new FiltrosRelatorioPatrimonio(loteReducaoValorBem, relatorioPatrimonioFacade.getSistemaFacade().getDataOperacao());
        super.gerarRelatorio();
    }

    @Override
    protected AbstractRelatorioAssincronoFacade getFacade() {
        return relatorioPatrimonioFacade;
    }

    @Override
    protected Object clausulasConsulta() {
        return filtrosRelatorioPatrimonio;
    }

    @Override
    protected String arquivoJasper() {
        return "RelatorioDeConferenciaDeReducaoDeValorDoBem.jasper";
    }

    @Override
    protected void validarCampos() {
    }

    @Override
    protected HashMap getParameters() {
        HashMap parameters = new HashMap();
        parameters.put("MODULO", "PATRIMÔNIO");
        parameters.put("MUNICIPIO", montarNomeDoMunicipio());
        parameters.put("NOMERELATORIO", "RELATÓRIO DE CONFERÊNCIA DE  " + filtrosRelatorioPatrimonio.getLoteReducaoValorBem().getTipoReducao().getDescricao().toUpperCase()
            + " DE BEM " + filtrosRelatorioPatrimonio.getLoteReducaoValorBem().getTipoBem().getDescricao().toUpperCase());
        parameters.put("BRASAO", getCaminhoImagem());
        atribuirUsuarioLogado(parameters);
        return parameters;
    }

    public void imprimirRelatorio() {
        super.imprimir("RelatorioDeConferenciaDeReducaoDeValorDoBem");
    }

    public class FiltrosRelatorioPatrimonio {
        private LoteReducaoValorBem loteReducaoValorBem;
        private Date dataOperacao;

        public FiltrosRelatorioPatrimonio(LoteReducaoValorBem loteReducaoValorBem, Date dataOperacao) {
            this.loteReducaoValorBem = loteReducaoValorBem;
            this.dataOperacao = dataOperacao;
        }

        public LoteReducaoValorBem getLoteReducaoValorBem() {
            return loteReducaoValorBem;
        }

        public void setLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
            this.loteReducaoValorBem = loteReducaoValorBem;
        }

        public Date getDataOperacao() {
            return dataOperacao;
        }

        public void setDataOperacao(Date dataOperacao) {
            this.dataOperacao = dataOperacao;
        }
    }
}
