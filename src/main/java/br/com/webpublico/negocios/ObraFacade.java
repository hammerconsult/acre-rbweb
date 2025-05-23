package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.ObraPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.administrativo.AuxObraFontesDespesas;
import br.com.webpublico.enums.ObraAtribuicao;
import br.com.webpublico.enums.SituacaoSolicitacaoFiscal;
import br.com.webpublico.enums.TipoSituacaoObra;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author venom
 */
@Stateless
public class ObraFacade extends AbstractFacade<Obra> {

    private static final Logger logger = LoggerFactory.getLogger(ObraFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ServicoObraFacade servicoObraFacade;
    @EJB
    private ProfissionalConfeaFacade profissionalConfeaFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private ModeloDoctoFacade modeloDoctoFacade;
    @EJB
    private ResponsavelTecnicoFiscalFacade responsavelTecnicoFiscalFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private TipoDocumentoAnexoFacade tipoDocumentoAnexoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    private ResponsavelTecnicoFiscal responsavelTecnicoFiscal;

    public ObraFacade() {
        super(Obra.class);
    }

    @Override
    public void salvarNovo(Obra entity) {
        if (entity.getCadastroImobiliario() != null) {
            entity.setCadastroImobiliario(getCadastroImobiliarioFacade().recuperar(entity.getCadastroImobiliario().getId()));
        }
        entity.getSituacoes().add(montarSituacaoEmAndamento(entity));
        getEntityManager().persist(entity);
        salvarPortal(entity);
    }

    private void salvarPortal(Obra entity) {
        portalTransparenciaNovoFacade.salvarPortal(new ObraPortal(entity));
    }

    public void salvarNovoComOrdensServico(Obra obra, List<OrdemDeServicoContrato> ordens) {
        if (ordens != null && !ordens.isEmpty()) {
            for (OrdemDeServicoContrato ordenServico : ordens) {
                getEntityManager().merge(ordenServico);
            }
        }
        salvarNovo(obra);
    }

    @Override
    public void salvar(Obra entity) {
        if (entity.getCadastroImobiliario() != null) {
            entity.setCadastroImobiliario(getCadastroImobiliarioFacade().recuperar(entity.getCadastroImobiliario().getId()));
        }

        entity.setContrato(mergeContrato(entity.getContrato()));
        entity.setInicioExecucao(entity.getContrato().getInicioVigencia());
        entity.setPrazoEntrega(entity.getContrato().getPrazoEntrega());
        getEntityManager().merge(entity);
        salvarPortal(entity);
    }

    private ObraSituacao montarSituacaoEmAndamento(Obra selecionado) {
        ObraSituacao obraSituacao = new ObraSituacao();
        obraSituacao.setObra(selecionado);
        obraSituacao.setSituacao(TipoSituacaoObra.EM_ANDAMENTO);
        obraSituacao.setMotivo("Gerado automaticamente ao iniciar a obra.");
        obraSituacao.setPessoaFisica(sistemaFacade.getUsuarioCorrente().getPessoaFisica());
        obraSituacao.setDataSituacao(selecionado.getContrato() != null
            ? selecionado.getContrato().getInicioVigencia()
            : sistemaFacade.getDataOperacao());
        return obraSituacao;
    }

    public void salvarObra(Obra entity) {
        entityManager.merge(entity);
    }

    public void salvarComOrdensServico(Obra obra, List<OrdemDeServicoContrato> ordens) {
        if (ordens != null) {
            for (OrdemDeServicoContrato o : ordens) {
                o = getEntityManager().merge(o);
                obra.getContrato().setOrdensDeServico(Util.adicionarObjetoEmLista(obra.getContrato().getOrdensDeServico(), o));
            }
        }
        salvar(obra);
    }

    public ResponsavelTecnicoFiscalFacade getResponsavelTecnicoFiscalFacade() {
        return responsavelTecnicoFiscalFacade;
    }

    public Contrato mergeContrato(Contrato c) {
        try {
            return getEntityManager().merge(c);
        } catch (Exception ex) {
            logger.error("Erro ao atualizar contrato: " + ex.getMessage());
            return c;
        }
    }

    private ServicoObra mergeServico(ServicoObra servico) {
        return getEntityManager().merge(servico);
    }

    @Override
    public Obra recuperar(Object id) {
        Obra obra = super.recuperar(id);
        obra.getServicos().size();
        obra.getAnexos().size();
        obra.getAnotacoes().size();
        obra.setContrato(getContratoFacade().recuperar(obra.getContrato().getId()));
        if (obra.getCadastroImobiliario() != null) {
            obra.setCadastroImobiliario(getCadastroImobiliarioFacade().recuperar(obra.getCadastroImobiliario().getId()));
        }
        obra.getTecnicoFiscais().size();
        obra.getItemObraTermo().size();
        obra.getMedicoes().size();
        for (ObraMedicao om : obra.getMedicoesObra()) {
            om.getAnexos().size();
            om.getExecucoesMedicao().size();
        }
        obra.getServicos().size();
        for (ObraServico os : obra.getServicos()) {
            os.getFilhos().size();
        }
        obra.getSituacoes().size();
        return obra;
    }

    public Obra recuperarPortalTransparencia(Object id) {
        Obra obra = super.recuperar(id);
        obra.getServicos().size();
        obra.getAnexos().size();
        obra.getAnotacoes().size();
        obra.getItemObraTermo().size();
        obra.setContrato(getContratoFacade().recuperarPortalTransparencia(obra.getContrato().getId()));
        return obra;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public UnidadeMedidaFacade getUnidadeMedidaFacade() {
        return unidadeMedidaFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public ServicoObraFacade getServicoObraFacade() {
        return servicoObraFacade;
    }

    public ProfissionalConfeaFacade getProfissionalConfeaFacade() {
        return profissionalConfeaFacade;
    }

    public ModeloDoctoFacade getModeloDoctoFacade() {
        return modeloDoctoFacade;
    }

    public List<Obra> buscarObrasPorContratoExercicioContratado(String parte) {
        String sql = "SELECT o.*" +
            "   from OBRA o" +
            " INNER JOIN CONTRATO c on c.id = o.CONTRATO_ID" +
            " inner join exercicio e on c.exerciciocontrato_id = e.id" +
            " LEFT JOIN PESSOAFISICA pf on pf.id = c.CONTRATADO_ID" +
            " LEFT JOIN PESSOAJURIDICA pj on pj.id = c.CONTRATADO_ID" +
            " where " +
            "  (c.numeroTermo like :parte" +
            "   or e.ano like :parte" +
            "   or lower(o.NOME) like :parte" +
            "   or ((lower(pf.nome) like :parte) or (replace(replace(pf.cpf,'.',''),'-','') like :parte)) " +
            "   or (lower(pj.razaoSocial) like :parte) or (pj.cnpj like :parte) or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte))" +
            " ORDER BY c.numeroTermo, e.ano DESC  ";
        Query q = entityManager.createNativeQuery(sql, Obra.class);
        q.setMaxResults(10);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Obra> buscarTodasObrasPorNome(String filter) {
        String sql = " select ob.* from obra ob where lower(ob.nome) like :filter ";
        Query q = getEntityManager().createNativeQuery(sql, Obra.class);
        q.setParameter("filter", "%" + filter.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Obra> buscarTodasObras() {
        String sql = " select ob.* from obra ob";
        Query q = getEntityManager().createNativeQuery(sql, Obra.class);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public ObraTermo recuperarTermoDaObra(Obra obra, boolean definitivo) {
        String sql = " select obratermo.* from obratermo where obra_id = :obra and definitivo = :termoDefinitivo";
        Query q = entityManager.createNativeQuery(sql, ObraTermo.class);
        q.setParameter("obra", obra.getId());
        q.setParameter("termoDefinitivo", definitivo);
        if (!q.getResultList().isEmpty()) {
            return (ObraTermo) q.getResultList().get(0);
        }
        return null;
    }

    public String mesclarTagsModelo(ModeloDocto modeloDoDocumento, Obra selecionado, ResponsavelTecnicoFiscal responsavelTecnico) {
        responsavelTecnicoFiscal = responsavelTecnico;
        CharSequence string1 = "$NUMERO[if !mso]";
        CharSequence string2 = "$NUMERO";

        modeloDoDocumento.setModelo(modeloDoDocumento.getModelo().replace(string1, string2));

        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        VelocityEngine ve = new VelocityEngine();
        ve.init(p);

        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource("myTemplate", modeloDoDocumento.getModelo());
        repo.setEncoding("UTF-8");

        VelocityContext context = new VelocityContext();
        adicionarTagsNoContexto(context, selecionado);
        Template template = ve.getTemplate("myTemplate", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private void adicionarTagsNoContexto(VelocityContext context, Obra obra) {
        adicionarTags(context, obra);
    }

    private void adicionarTags(VelocityContext context, Obra selecionado) {
        if (selecionado.getCadastroImobiliario() != null) {
            CadastroImobiliario cadastroImobiliario = cadastroImobiliarioFacade.recuperarSemCalcular(selecionado.getCadastroImobiliario().getId());
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.ENDERECO_OBRA.name(), cadastroImobiliario.getLote().getEndereco());
        } else {
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.ENDERECO_OBRA.name(), "");
        }
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.DESCRICAO_SERVICO.name(), selecionado.getNome());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.EMPRESA_CONTRATADA.name(), selecionado.getContrato().getContratado().getNome());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.CNPJ_EMPRESA_CONTRATADA.name(), selecionado.getContrato().getContratado().getCpf_Cnpj());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.ENDERECO_EMPRESA_CONTRATADA.name(), selecionado.getContrato().getContratado().getEnderecoPrincipal() != null ?
            selecionado.getContrato().getContratado().getEnderecoPrincipal() : "");
        if (selecionado.getContrato().isDeDispensaDeLicitacao()) {
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.NUMERO_PROCESSO_ADM_CONTRATO.name(), selecionado.getContrato().getDispensaDeLicitacao().getProcessoDeCompra().getNumeroAndExercicio());
        } else {
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.NUMERO_PROCESSO_ADM_CONTRATO.name(), selecionado.getContrato().getLicitacao().getProcessoDeCompra().getNumeroAndExercicio());
        }
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.NUMERO_CONTRATO.name(), selecionado.getContrato().getNumeroAnoTermo());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.VALOR_CONTRATO.name(), Util.formataValor(selecionado.getContrato().getValorTotalExecucao()));

        HierarquiaOrganizacional hoAdmContrato = contratoFacade.buscarHierarquiaVigenteContrato(selecionado.getContrato(), selecionado.getContrato().getTerminoVigencia());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.SECRETARIA_DECLARANTE.name(), hoAdmContrato.getDescricao());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.DATA_TERMINO_RECIBIMENTO.name(), DataUtil.getDataFormatada(new Date()));
        if (responsavelTecnicoFiscal != null) {
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.FISCAL_OBRA.name(), responsavelTecnicoFiscal.getResponsavel());
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.PROFISSAO_FISCAL_OBRA.name(), responsavelTecnicoFiscal.getProfissionalConfea().getModalidadeConfea().getDescricao());
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.CREA_FISCAL_OBRA.name(), responsavelTecnicoFiscal.getCreaCau());
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.CPF_FISCAL_OBRA.name(), responsavelTecnicoFiscal.getContratoFP().getMatriculaFP().getPessoa().getCpf());
        } else {
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.FISCAL_OBRA.name(), "");
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.PROFISSAO_FISCAL_OBRA.name(), "");
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.CREA_FISCAL_OBRA.name(), "");
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.CPF_FISCAL_OBRA.name(), "");
        }
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.NOME_SECRETARIO.name(), selecionado.getContrato().getResponsavelPrefeitura().getMatriculaFP().getPessoa().getNome());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.RESPONSAVEL_EMPRESA.name(), selecionado.getContrato().getResponsavelEmpresa().getNome());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.CPF_RESPONSAVEL_EMPRESA.name(), selecionado.getContrato().getResponsavelEmpresa().getCpf());
        context.put(TipoModeloDocto.TagRecebimentoTermoObra.NOME_EMPRESA.name(), selecionado.getContrato().getContratado().getNome());
        if (selecionado.getContrato().isDeDispensaDeLicitacao()) {
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.NUMERO_LICITACAO.name(), selecionado.getContrato().getDispensaDeLicitacao());
        } else {
            context.put(TipoModeloDocto.TagRecebimentoTermoObra.NUMERO_LICITACAO.name(), selecionado.getContrato().getLicitacao().getNumeroLicitacao() + "/" + selecionado.getContrato().getLicitacao().getExercicio().getAno());
        }
    }


    public List<ResponsavelTecnicoFiscal> recuperarFiscaisObra(Obra obra) {
        String sql = "select responsavel.* from ObraTecnicoFiscal obratec" +
            " inner join ResponsavelTecnicoFiscal responsavel on obratec.TECNICOFISCAL_ID = responsavel.id " +
            " where obra_id = :idObra";
        Query q = entityManager.createNativeQuery(sql, ResponsavelTecnicoFiscal.class);
        q.setParameter("idObra", obra.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public ResponsavelTecnicoFiscal recuperarFiscalObraVigente(Obra obra, Date dataOperacao) {
        String sql = "select responsavel.* from ObraTecnicoFiscal obratec" +
            " inner join ResponsavelTecnicoFiscal responsavel on obratec.TECNICOFISCAL_ID = responsavel.id " +
            " where obra_id = :idObra" +
            " and to_date(:dataOperacao, 'dd/mm/yyyy') between obratec.inicioVigencia and coalesce(obratec.fimVigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))";
        Query q = entityManager.createNativeQuery(sql, ResponsavelTecnicoFiscal.class);
        q.setParameter("idObra", obra.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ResponsavelTecnicoFiscal) q.getSingleResult();
        }
        return null;
    }


    public DotacaoSolicitacaoMaterial buscarDotacaoSolicitacaoContrato(Contrato contrato) {
        String sql = "select dotacao.*\n" +
            "from contrato cont\n" +
            " left join conlicitacao conlic on conlic.contrato_id = cont.id " +
            " left join licitacao lic on lic.id = conlic.licitacao_id " +
            "inner join ProcessoDeCompra proc on proc.id = lic.PROCESSODECOMPRA_ID\n" +
            "inner join SolicitacaoMaterial solic on solic.id = proc.SOLICITACAOMATERIAL_ID\n" +
            "inner join DOTSOLMAT dotacao on dotacao.SOLICITACAOMATERIAL_ID = solic.ID\n" +
            "where cont.id = :contrato_id";
        Query q = entityManager.createNativeQuery(sql, DotacaoSolicitacaoMaterial.class);
        q.setParameter("contrato_id", contrato.getId());
        return !q.getResultList().isEmpty() ? (DotacaoSolicitacaoMaterial) q.getSingleResult() : null;
    }

    public List<AuxObraFontesDespesas> buscarFonteDespesaValorAgrupados(Contrato contrato) {
        String sql = " select  despesa.id as despesa_id, fonte.id fonte_id, sum(execucaoTipoFonte.VALOR) total_agrupado " +
            "      from contrato " +
            "      inner join ExecucaoContrato execucao on execucao.CONTRATO_ID = contrato.id " +
            "      inner join ExecucaoContratoTipo execucaoTipo on execucaoTipo.EXECUCAOCONTRATO_ID = execucao.id " +
            "      inner join ExecucaoContratoTipoFonte execucaoTipoFonte on execucaoTipoFonte.EXECUCAOCONTRATOTIPO_ID = execucaoTipo.ID " +
            "      inner join fonteDespesaORC fonte on fonte.id = execucaoTipoFonte.FONTEDESPESAORC_ID " +
            "      inner join despesaORC despesa on despesa.id = fonte.DESPESAORC_ID " +
            "      where contrato.id = :contrato_id" +
            "      group by despesa.id,fonte.id ";
        Query q = entityManager.createNativeQuery(sql);
        q.setParameter("contrato_id", contrato.getId());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            List<Object[]> lista = q.getResultList();
            List<AuxObraFontesDespesas> obraSolicitacaoEmpenhos = new ArrayList<>();
            for (Object[] objects : lista) {
                AuxObraFontesDespesas obraSolicitacao = new AuxObraFontesDespesas();
                obraSolicitacao.setDespesaORC(despesaORCFacade.recuperar(((BigDecimal) objects[0]).longValue()));
                obraSolicitacao.setFonteDespesaORC(fonteDespesaORCFacade.recuperar(((BigDecimal) objects[1]).longValue()));
                obraSolicitacao.setValorReservado((BigDecimal) objects[2]);
                obraSolicitacaoEmpenhos.add(obraSolicitacao);
            }
            return obraSolicitacaoEmpenhos;
        }
        return new ArrayList<>();
    }

    public List<Empenho> buscarEmpenhosObraMedicao(ObraMedicao obraMedicao) {

        String sql = " select em.* " +
            "      from obraMedicao om " +
            "      inner join obramedicaoexeccontrato omExec on omExec.obramedicao_id = om.id " +
            "      inner join execucaocontrato exec on exec.id = omExec.execucaocontrato_id " +
            "      inner join execucaocontratoempenho execEmp on execEmp.execucaocontrato_id = exec.id " +
            "      inner join empenho em on em.id = execEmp.empenho_id " +
            "      where om.id = :obraMedicao_id ";
        Query q = entityManager.createNativeQuery(sql, Empenho.class);
        q.setParameter("obraMedicao_id", obraMedicao.getId());
        return q.getResultList().isEmpty() ? new ArrayList<Empenho>() : q.getResultList();
    }

    public ObraMedicao salvarObraMedicaoSolicitacaoEmpenho(ObraMedicao obraMedicao) {
        return entityManager.merge(obraMedicao);
    }

    public void atualizarObraSolicitacaoEmpenhoWithSolicitacaoEmpenho(ObraMedicaoSolicitacaoEmpenho obraMedicaoSolicitacaoEmpenho) {
        entityManager.merge(obraMedicaoSolicitacaoEmpenho);
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoFacade() {
        return dotacaoFacade;
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }

    public List<Obra> buscarObrasPorUsuarioSistema(String parte, UsuarioSistema usuarioSistema) {
        String sql = " SELECT distinct o.* " +
            " FROM obra o " +
            "  INNER JOIN CONTRATO c ON c.id = o.CONTRATO_ID " +
            "  inner join unidadecontrato uc on uc.contrato_id = c.id " +
            "  INNER JOIN USUARIOUNIDADEORGANIZACIO USUUND ON USUUND.UNIDADEORGANIZACIONAL_ID = uc.unidadeadministrativa_id " +
            " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id " +
            " where lower(o.NOME) like :parte " +
            " and usu.id = :idUsuario " +
            " and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            " ORDER BY o.nome ";
        Query q = entityManager.createNativeQuery(sql, Obra.class);
        q.setMaxResults(10);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idUsuario", usuarioSistema.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public List<ResponsavelTecnicoFiscal> buscarResponsaveisFiscaisDaObraPorSituacao(Obra obra, SituacaoSolicitacaoFiscal situacaoSolicitacao) {
        String sql = "SELECT responsavel.* " +
            "FROM obra " +
            "INNER JOIN SOLICRESPONSATECNICOFISCAL aprovacao on aprovacao.OBRA_ID = obra.id " +
            "INNER JOIN  ResponsavelTecnicoFiscal responsavel on responsavel.id = aprovacao.RESPONSAVELTECNICOFISCAL_ID " +
            "WHERE aprovacao.SITUACAOSOLICITACAO = :situacao " +
            "AND obra.id = :obra_id";
        Query q = entityManager.createNativeQuery(sql, ResponsavelTecnicoFiscal.class);
        q.setParameter("situacao", situacaoSolicitacao.name());
        q.setParameter("obra_id", obra.getId());
        return q.getResultList().isEmpty() ? Lists.<ResponsavelTecnicoFiscal>newArrayList() : q.getResultList();
    }

    public List<ResponsavelTecnicoFiscal> buscarResponsaveisFiscaisDaObraPorSituacao(Obra obra, SituacaoSolicitacaoFiscal situacaoSolicitacao, String filtro) {
        String sql = "SELECT distinct responsavel.* " +
            "  FROM obra obra " +
            " INNER JOIN SOLICRESPONSATECNICOFISCAL aprovacao on aprovacao.OBRA_ID = obra.id " +
            " INNER JOIN  ResponsavelTecnicoFiscal responsavel on responsavel.id = aprovacao.RESPONSAVELTECNICOFISCAL_ID " +
            "  left join contrato c on c.id = responsavel.contrato_id" +
            "  left join exercicio ex on ex.id = c.exerciciocontrato_id" +
            "  left join pessoafisica pf on pf.id = c.contratado_id" +
            "  left join pessoajuridica pj on pj.id = c.contratado_id" +
            "  left join contratofp servidor on servidor.id = responsavel.contratofp_id" +
            "  left join VinculoFP v on v.id = servidor.id  " +
            "  left join MatriculaFP mtr on mtr.id = v.matriculafp_id" +
            "  left join pessoafisica pf_servidor on pf_servidor.id = mtr.pessoa_id" +
            " where  (((lower(pf.nome) like :parte) or (replace(replace(pf.cpf,'.',''),'-','') like :parte))" +
            "    or ((lower(pf_servidor.nome) like :parte) or (replace(replace(pf_servidor.cpf,'.',''),'-','') like :parte))" +
            "    or (lower(pj.razaoSocial) like :parte) or (pj.cnpj like :parte) or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte))" +
            "   and aprovacao.SITUACAOSOLICITACAO = :situacao " +
            "   and responsavel.atribuicao = :atribuicao " +
            "   AND obra.id = :obra_id ";
        Query q = entityManager.createNativeQuery(sql, ResponsavelTecnicoFiscal.class);
        q.setParameter("situacao", situacaoSolicitacao.name());
        q.setParameter("obra_id", obra.getId());
        q.setParameter("parte", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("atribuicao", ObraAtribuicao.FISCAL.name());
        return q.getResultList().isEmpty() ? Lists.<ResponsavelTecnicoFiscal>newArrayList() : q.getResultList();
    }

    public SolicitacaoResponsaveltecnicoFiscal buscarSolicitacaoTecnicoFiscal(Obra obra, SituacaoSolicitacaoFiscal situacaoSolicitacao, ResponsavelTecnicoFiscal responsavelTecnicoFiscal) {
        String sql = "SELECT distinct aprovacao.* " +
            "  FROM obra obra " +
            " INNER JOIN SOLICRESPONSATECNICOFISCAL aprovacao on aprovacao.OBRA_ID = obra.id " +
            " INNER JOIN ResponsavelTecnicoFiscal responsavel on responsavel.id = aprovacao.RESPONSAVELTECNICOFISCAL_ID " +
            " where responsavel.id = :responsavel_id " +
            "   and aprovacao.SITUACAOSOLICITACAO = :situacao " +
            "   and responsavel.atribuicao = :atribuicao " +
            "   AND obra.id = :obra_id ";
        Query q = entityManager.createNativeQuery(sql, SolicitacaoResponsaveltecnicoFiscal.class);
        q.setParameter("situacao", situacaoSolicitacao.name());
        q.setParameter("obra_id", obra.getId());
        q.setParameter("responsavel_id", responsavelTecnicoFiscal.getId());
        q.setParameter("atribuicao", ObraAtribuicao.FISCAL.name());
        q.setMaxResults(1);
        return q.getResultList().isEmpty() ? null : (SolicitacaoResponsaveltecnicoFiscal) q.getSingleResult();
    }

    public List<Obra> buscarObrasComMedicaoVinculadasAoEmpenho() {
        String sql = " select distinct obra.* from obra obra " +
            " inner join obramedicao med on med.obra_id = obra.id " +
            " inner join obramedsolicitacaoempenho oMed on omed.obramedicao_id = med.id ";
        Query q = getEntityManager().createNativeQuery(sql, Obra.class);
        return q.getResultList();
    }

    public List<Obra> buscarTodasObrasComInscricaoCadastral() {
        String sql = " select ob.* from obra ob where ob.CADASTROIMOBILIARIO_ID is not null";
        Query q = getEntityManager().createNativeQuery(sql, Obra.class);
        List<Obra> resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (Obra o : resultList) {
                o.getMedicoes().size();
                o.getCadastroImobiliario().getLote().getTestadas().size();
            }

            return resultList;
        }
        return Lists.newArrayList();
    }


    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public TipoDocumentoAnexoFacade getTipoDocumentoAnexoFacade() {
        return tipoDocumentoAnexoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
