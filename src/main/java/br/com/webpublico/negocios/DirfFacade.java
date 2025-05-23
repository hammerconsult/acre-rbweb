/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * @Author - Felipe Marinzeck
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.dirf.DirfInfoComplementares;
import br.com.webpublico.entidades.rh.dirf.DirfPessoa;
import br.com.webpublico.entidades.rh.dirf.DirfVinculoFP;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.entidadesauxiliares.rh.BeneficiarioPensaoAlimenticiaMesValor;
import br.com.webpublico.entidadesauxiliares.rh.BeneficioPensaoAlimenticiaEventoFP;
import br.com.webpublico.entidadesauxiliares.rh.PessoaDirf;
import br.com.webpublico.entidadesauxiliares.rh.PessoaInfo;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.dirf.DirfReg316;
import br.com.webpublico.enums.rh.dirf.DirfReg35;
import br.com.webpublico.enums.rh.dirf.LoteDirf;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class DirfFacade extends AbstractFacade<Dirf> {

    private static final int SESSENTAECINCOANOS = 65;
    private static final Logger logger = LoggerFactory.getLogger(DirfFacade.class);
    private static final String DESCRICAO_MODULO_EXPORTACAO = "IRRF";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hoFacade;
    private DependenciasDirf dependenciasDirf;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private MiddleRHFacade middleRHFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private DirfAcompanhamentoFacade dirfAcompanhamentoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoExportacaoFacade grupoExportacaoFacade;
    @EJB
    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;

    public DirfFacade() {
        super(Dirf.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DependenciasDirf getDependenciasDirf() {
        return dependenciasDirf;
    }

    public void setDependenciasDirf(DependenciasDirf dependenciasDirf) {
        this.dependenciasDirf = dependenciasDirf;
    }

    private List<HierarquiaOrganizacional> buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado(Dirf d) {
        List<HierarquiaOrganizacional> hierarquiasRecuperadas = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : entidadeFacade.buscarHierarquiasDaEntidade(d.getEntidade(), CategoriaDeclaracaoPrestacaoContas.DIRF, d.getPrimeiroDia(), d.getUltimoDia())) {
            hierarquiasRecuperadas.add(hierarquiaOrganizacionalFacade.recuperar(ho));
        }
        return hierarquiasRecuperadas;
    }

    private List<RecursoFP> buscarRecurosPorDirf(Dirf d) {
        Set<RecursoFP> recursoFPS = Sets.newHashSet();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : d.getHierarquiasOrganizacionais()) {
            hierarquiaOrganizacional.getCodigoDo2NivelDeHierarquia();
            recursoFPS.addAll(recursoFPFacade.buscarRecursoFPDo2NivelDeHierarquiaPorAno(hierarquiaOrganizacional, d.getExercicio()));
        }

        return Lists.newArrayList(recursoFPS);
    }

    private void addLog(String valor, String abre, String fecha) {
        addLog(abre + valor + fecha);
    }

    private void addLog(String valor) {
        try {
            String agora = Util.dateHourToString(new Date());
            dependenciasDirf.getLog().add(agora + " - " + valor.concat("<br/>"));
        } catch (NullPointerException npe) {
            if (dependenciasDirf == null) {
                dependenciasDirf = new DependenciasDirf();
            }
            dependenciasDirf.setLog(new ArrayList<String>());
            addLog(valor);
        }
    }

    public void jaExisteArquivoGeradoParaExercicio(Dirf d) {
        String hql = "select d from Dirf d where d.exercicio = :exercicio and d.entidade = :entidade and d.tipoDirf = '" + d.getTipoDirf().name() + "'";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", d.getExercicio());
        q.setParameter("entidade", d.getEntidade());
        q.setMaxResults(1);
        try {
            Dirf resultado = (Dirf) q.getSingleResult();
            throw new ExcecaoNegocioGenerica("Já existe um arquivo gerado para '" + d.getEntidade() + "' no exercício de " + d.getExercicio());
        } catch (NoResultException npe) {
        }
    }


    public void validarArquivoContabil(Dirf d) {

        String sql = "select dir.* from dirf dir " +
            "inner join exercicio ex on dir.EXERCICIO_ID = ex.ID " +
            "inner join entidade en on dir.ENTIDADE_ID = en.ID " +
            "where en.id = :entidade and ex.ID = :exercicio " +
            " and dir.TIPODIRF = :tipoDirf and  dir.tipoemissaodirf = :tipoEmissaoDirf ";

        Query q = em.createNativeQuery(sql, Dirf.class);
        q.setParameter("exercicio", d.getExercicio().getId());
        q.setParameter("entidade", d.getEntidade().getId());
        q.setParameter("tipoDirf", d.getTipoDirf().name());
        q.setParameter("tipoEmissaoDirf", d.getTipoEmissaoDirf().name());
        q.setMaxResults(1);
        try {
            Dirf resultado = (Dirf) q.getSingleResult();
            throw new ExcecaoNegocioGenerica("Já existe um arquivo gerado para '"
                + d.getEntidade() + "' no exercício de "
                + d.getExercicio() + " para o tipo de emissão da dirf "
                + d.getTipoEmissaoDirf().getDescricao());
        } catch (NoResultException npe) {
        }
    }

    @Override
    public List<Dirf> listaDecrescente() {
        String hql = "select d from Dirf d inner join d.exercicio e order by e.ano desc";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    private void removerRelacionamentoDirfArquivo(Dirf d) {
        try {
            String sql = "UPDATE Dirf SET arquivo_id = null WHERE id = :param";
            Query q = em.createNativeQuery(sql);
            q.setParameter("param", d.getId());
            q.executeUpdate();
        } catch (Exception e) {
        }
    }

    public Dirf getDirf(Exercicio e, Entidade ent) {
        String hql = "select d from Dirf d where d.exercicio = :exercicio and d.entidade = :ent and d.tipoDirf = :tipoDirf";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", e);
        q.setParameter("ent", ent);
        q.setParameter("tipoDirf", TipoDirf.RH);
        q.setMaxResults(1);

        try {
            return (Dirf) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    private List<DirfVinculoFP> getDirfVinculoFP(Dirf dirf) {
        String hql = "select dirfVinculo from DirfVinculoFP dirfVinculo where dirf = :dirf ";
        Query q = em.createQuery(hql);
        q.setParameter("dirf", dirf);
        try {
            return q.getResultList();
        } catch (NoResultException npe) {
            return Lists.newArrayList();
        }
    }

    private List<DirfInfoComplementares> getDirfInformacoesComplementares(DirfVinculoFP dirfVinculoFP) {
        String hql = "select info from DirfInfoComplementares info where dirfVinculoFP = :dirfVinculo ";
        Query q = em.createQuery(hql);
        q.setParameter("dirfVinculo", dirfVinculoFP);

        try {
            return q.getResultList();
        } catch (NoResultException npe) {
            return Lists.newArrayList();
        }
    }

    private List<DirfPessoa> getDirfPessoa(DirfVinculoFP dirfVinculoFP) {
        String hql = "select dirfPessoa from DirfPessoa dirfPessoa where dirfPessoa = :dirfVinculo ";
        Query q = em.createQuery(hql);
        q.setParameter("dirfVinculo", dirfVinculoFP);
        try {
            return q.getResultList();
        } catch (NoResultException npe) {
            return Lists.newArrayList();
        }
    }

    public Dirf buscarDirf(Dirf dirf) {
        String hql = "select d from Dirf d " +
            " where d.exercicio = :exercicio " +
            " and d.entidade = :ent and d.tipoDirf = :tipoDirf and d.tipoEmissaoDirf = :tipoEmissaoDirf";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", dirf.getExercicio());
        q.setParameter("ent", dirf.getEntidade());
        q.setParameter("tipoDirf", dirf.getTipoDirf());
        q.setParameter("tipoEmissaoDirf", dirf.getTipoEmissaoDirf());
        q.setMaxResults(1);

        try {
            return (Dirf) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    private Dirf gerarEntidadeArquivo(String conteudo, Dirf d) {
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());
        Arquivo a = new Arquivo();
        int ano = d.getExercicio().getAno() + 1;
        a.setNome("DIRF-" + ano + "-" + d.getEntidade().getSigla() + ".txt");
        a.setMimeType("text/plain");
        a.setInputStream(is);
        try {
            arquivoFacade.novoArquivo(a, a.getInputStream());
        } catch (Exception ex) {
            logger.info("Erro:", ex);
        }

        d.setArquivo(a);
        d.atualizarDataProcessamento();
        return d;
    }

    @Override
    public void remover(Dirf entity) {
        removerArquivoDirf(entity);
        super.remover(entity);
    }


    public void removerDirf(Dirf entity) {
        super.remover(entity);
    }

    public void removerArquivoDirf(Dirf d) {
        if (d != null && d.getArquivo() != null) {
            removerRelacionamentoDirfArquivo(d);
            arquivoFacade.removerArquivo(d.getArquivo());
        }
    }

    private void carregarTotalDeRegistros() {
        dependenciasDirf.setTotalCadastros(1);
    }

    public Dirf buscarDirfAnoEntidade(Dirf d) {
        Query q = em.createQuery(" from Dirf d where d.exercicio = :exercicio and d.entidade = :entidade");
        q.setParameter("exercicio", d.getExercicio());
        q.setParameter("entidade", d.getEntidade());
        List result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            return (Dirf) result.get(0);
        }
        return null;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarArquivo(Operacoes operacao, Dirf d, DependenciasDirf dep, Boolean dirfContabil, TipoEmissaoDirf tipoEmissaoDirf) throws IOException {
        dependenciasDirf = dep;
        try {
            if (d.getTipoDirf().equals(TipoDirf.RH)) {
                jaExisteArquivoGeradoParaExercicio(d);
            } else if (d.getTipoDirf().equals(TipoDirf.CONTABIL)) {
                validarArquivoContabil(d);
            }
        } catch (RuntimeException re) {
            addLog("REMOVENDO ARQUIVO ANTERIOR...");
            if (d.getTipoDirf().equals(TipoDirf.CONTABIL)) {
                dirfAcompanhamentoFacade.excluirDirfContabil(buscarDirf(d));
            }
        }

        addLog("PREPARANDO ARQUIVO PARA GERAÇÃO DOS DADOS...");

        carregarTotalDeRegistros();

        String conteudo = getConteudoArquivo(d, dirfContabil, tipoEmissaoDirf);
        d = gerarEntidadeArquivo(conteudo, d);

        try {
            addLog("SALVANDO ARQUIVO GERADO...");
            addLog("ARQUIVO GERADO E SALVO COM SUCESSO");

            if (operacao.equals(Operacoes.NOVO)) {
                middleRHFacade.salvarNovoCalculo(d);
            } else {
                middleRHFacade.salvar(d);
            }
        } catch (Exception e) {
            addLog("NÃO FOI POSSÍVEL SALVAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES. DETALHES DO ERRO: " + e);
        }

        dependenciasDirf.pararProcessamento();
    }

    private String getConteudoArquivo(Dirf d, Boolean dirfContabil, TipoEmissaoDirf tipoEmissaoDirf) {
        StringBuilder conteudo = new StringBuilder();
        try {
            addLog("GERANDO CABEÇALHO DO ARQUIVO...");
            conteudo.append(processarRegistro31(d));
            conteudo.append(processarRegistro32(d));
            conteudo.append(processarRegistro33(d));
            conteudo.append(processarRegistro34(d));
            if (dirfContabil) {
                adicionarQuantidadeDePessoasNoContador(d, tipoEmissaoDirf);
                if (tipoEmissaoDirf.isPessoaFisicaOuAmbos()) {
                    for (String codigo : getCodigosItemGrupoExportacaoContabil()) {
                        conteudo.append(processarRegistro35Contabil(codigo));
                        conteudo.append(processarRegistro36Contabil(d, codigo));
                    }
                }
                if (tipoEmissaoDirf.isPessoaJuridicaOuAmbos()) {
                    for (String codigoPj : getCodigosPjItemGrupoExportacaoContabil()) {
                        conteudo.append(processarRegistro35Contabil(codigoPj));
                        conteudo.append(processarRegistro37Contabil(d, codigoPj));
                    }
                }
            } else {
                List<LoteDirf> loteDirfs = Lists.newLinkedList();
                buscarPessoasParaDirf(d, loteDirfs);
                for (LoteDirf loteDirf : loteDirfs) {
                    conteudo.append(processarRegistro35(d, loteDirf));
                    conteudo.append(processarRegistro36DirfRH(d, loteDirf));

                }
            }
            conteudo.append(processarRegistro328());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conteudo.toString();
    }

    private void buscarPessoasParaDirf(Dirf d, List<LoteDirf> loteDirfs) {

        List<PessoaDirf> pessoas = new ArrayList<>();
        List<PessoaInfo> pessoasAgrupadas = new ArrayList<>();
        List<PessoaInfo> aposentadosPensionistas = new ArrayList<>();

        d.setHierarquiasOrganizacionais(buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado(d));
        d.setRecursos(buscarRecurosPorDirf(d));

        for (HierarquiaOrganizacional ho : d.getHierarquiasOrganizacionais()) {
            pessoas.addAll(getPessoasComRendimentosEm(d.getExercicio().getAno(), ho, d));
        }
        pessoas = new ArrayList(new HashSet(pessoas));
        montarAgrupamento(pessoas, pessoasAgrupadas, aposentadosPensionistas);

        dependenciasDirf.setTotalCadastros(pessoasAgrupadas.size()+aposentadosPensionistas.size());


        if (pessoas == null) {
            addLog("NÃO FORAM LOCALIZADOS SERVIDORES COM RENDIMENTOS NO ANO INFORMADO.");
            throw new ExcecaoNegocioGenerica("Não foram localizados servidores com rendimentos no ano informado.");
        }

        if (!pessoasAgrupadas.isEmpty()) {
            loteDirfs.add(new LoteDirf(DirfReg35.ASSALARIADO, pessoasAgrupadas));
        }
        if (!aposentadosPensionistas.isEmpty()) {
            loteDirfs.add(new LoteDirf(DirfReg35.APOSENTADO_PENSIONIATA, aposentadosPensionistas));
        }
    }

    private boolean isAposentadoOuPensionista(PessoaInfo pessoasAgrupada) {
        for (VinculoFP vinculoFP : pessoasAgrupada.getVinculos()) {
            if (vinculoFP instanceof Aposentadoria || vinculoFP instanceof Pensionista) {
                return true;
            }
        }
        return false;
    }

    private void adicionarQuantidadeDePessoasNoContador(Dirf d, TipoEmissaoDirf tipoEmissaoDirf) {
        List<Pessoa> pessoas = Lists.newArrayList();
        List<HierarquiaOrganizacional> hierarquias = buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado(d);
        if (tipoEmissaoDirf.isPessoaFisicaOuAmbos()) {
            for (String codigo : getCodigosItemGrupoExportacaoContabil()) {
                pessoas.addAll(getQuantidadePessoasFisicasComPagamentos(hierarquias, codigo, d.getExercicio().getAno()));
            }
        }
        if (tipoEmissaoDirf.isPessoaJuridicaOuAmbos()) {
            for (String codigo : getCodigosPjItemGrupoExportacaoContabil()) {
                pessoas.addAll(getQuantidadePessoasJuridicasComPagamentos(hierarquias, codigo, d.getExercicio().getAno()));
            }
        }
        dependenciasDirf.setTotalCadastros(pessoas.size());

    }


    private BigDecimal getSomaDosEventosMensalImpostoRendaRendimentoInsento(Pessoa p, VinculoFP vinculoFP, Mes mes, Integer ano, List<TipoFolhaDePagamento> tiposFolha, Integer evento) {
        String folhas = "and (";

        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";
        String sql = " SELECT coalesce(sum(ITEM.VALORBASEDECALCULO),0) FROM ITEMFICHAFINANCEIRAFP ITEM  " +
            " INNER JOIN FICHAFINANCEIRAFP FICHA ON ITEM.FICHAFINANCEIRAFP_ID = FICHA.ID " +
            " INNER JOIN folhadepagamento fp ON fp.id = ficha.folhadepagamento_id                              " +
            " inner join eventofp evento on ITEM.EVENTOFP_ID = evento.id " +
            " WHERE FICHA.VINCULOFP_ID = :vinculo_id ";
        if (mes != null) {
            sql += " and ITEM.MES = :mes ";
        }
        sql += " and ITEM.ANO = :ano " +
            " and evento.codigo = :evento " + folhas;
        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        if (mes != null) {
            q.setParameter("mes", mes.getNumeroMes());
        }
        q.setParameter("vinculo_id", vinculoFP.getId());
        q.setParameter("evento", evento);
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private List<ItemGrupoExportacaoContabil> recuperarItemGruposExportacaoContabilComClasseCredorPorModuloENomeReduzidoECodigo(DirfReg316 nomeReduzido, String codigo) {
        String sql = " SELECT igec.* FROM itemgrupoexportacaocont igec              " +
            "  INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "  INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "  where igec.classecredor_id is not null " +
            "  AND me.descricao = :descricaoModulo " +
            "  AND igec.codigo = :codigo ";
        if (nomeReduzido != null) {
            sql += "  AND ge.nomereduzido = :nomeReduzido ";
        }
        Query q = em.createNativeQuery(sql, ItemGrupoExportacaoContabil.class);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("codigo", codigo);
        if (nomeReduzido != null) {
            q.setParameter("nomeReduzido", nomeReduzido.toString());
        }
        return (List<ItemGrupoExportacaoContabil>) q.getResultList();
    }

    private List<ItemGrupoExportacaoContabil> buscarItensGruposExportacaoContabilComClasseCredorPorModuloECodigoPJ(String codigoPj) {
        String sql = " SELECT igec.* FROM itemgrupoexportacaocont igec              " +
            "  INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "  INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "  where igec.classecredor_id is not null " +
            "  AND me.descricao = :descricaoModulo " +
            "  AND igec.codigopj = :codigoPj ";
        Query q = em.createNativeQuery(sql, ItemGrupoExportacaoContabil.class);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("codigoPj", codigoPj);
        return (List<ItemGrupoExportacaoContabil>) q.getResultList();
    }

    private GrupoExportacao recuperarGruposExportacaoComClasseCredorPorModuloENomeReduzido(DirfReg316 nomeReduzido, String codigo) {
        String sql = " SELECT distinct ge.* FROM itemgrupoexportacaocont igec              " +
            "  INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "  INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "  where igec.classecredor_id is not null " +
            "  AND me.descricao = :descricaoModulo " +
            "  AND igec.codigo = :codigo ";
        if (nomeReduzido != null) {
            sql += "  AND ge.nomereduzido = :nomeReduzido ";
        }
        Query q = em.createNativeQuery(sql, GrupoExportacao.class);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("codigo", codigo);
        if (nomeReduzido != null) {
            q.setParameter("nomeReduzido", nomeReduzido.toString());
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (GrupoExportacao) q.getSingleResult();
        }
        return null;
    }

    private GrupoExportacao buscarGrupoExportacaoComClasseCredorPorModuloNomeReduzidoECodigoPj(DirfReg316 nomeReduzido, String codigoPj) {
        String sql = " SELECT distinct ge.* FROM itemgrupoexportacaocont igec              " +
            "  INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "  INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "  where igec.classecredor_id is not null " +
            "  AND me.descricao = :descricaoModulo " +
            "  AND igec.codigopj = :codigoPj ";
        if (nomeReduzido != null) {
            sql += "  AND ge.nomereduzido = :nomeReduzido ";
        }
        Query q = em.createNativeQuery(sql, GrupoExportacao.class);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("codigoPj", codigoPj);
        if (nomeReduzido != null) {
            q.setParameter("nomeReduzido", nomeReduzido.toString());
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (GrupoExportacao) q.getSingleResult();
        }
        return null;
    }

    private GrupoExportacao recuperarGruposExportacaoComContaExtraPorModuloENomeReduzido(DirfReg316 nomeReduzido) {
        String sql = " SELECT distinct ge.* FROM itemgrupoexportacaocont igec              " +
            "  INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "  INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "  where igec.contaextraorcamentaria_id is not null " +
            "  AND me.descricao = :descricaoModulo ";
        if (nomeReduzido != null) {
            sql += "  AND ge.nomereduzido = :nomeReduzido ";
        }
        Query q = em.createNativeQuery(sql, GrupoExportacao.class);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        if (nomeReduzido != null) {
            q.setParameter("nomeReduzido", nomeReduzido.toString());
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (GrupoExportacao) q.getSingleResult();
        }
        return null;
    }

    private List<ItemGrupoExportacaoContabil> recuperaGruposExportacaoContabilComContaExtraPorModuloENomeReduzido(DirfReg316 nomeReduzido) {
        String sql = " SELECT igec.* FROM itemgrupoexportacaocont igec              " +
            "  INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "  INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "  where igec.contaextraorcamentaria_id is not null " +
            "  AND me.descricao = :descricaoModulo " +
            "  AND ge.nomereduzido = :nomeReduzido ";
        Query q = em.createNativeQuery(sql, ItemGrupoExportacaoContabil.class);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        return (List<ItemGrupoExportacaoContabil>) q.getResultList();
    }

    private BigDecimal getSomaDosPagamentosMensal(Pessoa p, Mes mes, Integer ano, DirfReg316 nomeReduzido, List<Long> subordinadas, ClasseCredor classeCredor) {
        String sql = " select sum(valor) from (select coalesce(sum(pag.valor), 0) as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from pag.datapagamento) = :ano " +
            " and extract(month from pag.datapagamento) = :mes " +
            " and p.id = :pessoa_id " +
            " and vw.subordinada_id in :subordinadas " +
            " and trunc(pag.datapagamento) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(pag.datapagamento)) " +
            "       AND cc.id IN (SELECT igec.classecredor_id FROM itemgrupoexportacaocont igec              " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               where igec.classecredor_id = :classeCredor                       " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido)                           " +
            " union all " +
            " select coalesce(sum(est.valor), 0) * - 1 as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pagamentoestorno est on pag.id = est.pagamento_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from est.dataestorno) = :ano " +
            " and extract(month from est.dataestorno) = :mes " +
            " and  p.id = :pessoa_id " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND cc.id IN (SELECT igec.classecredor_id FROM itemgrupoexportacaocont igec              " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               where igec.classecredor_id = :classeCredor                       " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido)                           " +
            " and trunc(est.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(est.dataestorno))) ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("classeCredor", classeCredor.getId());
        q.setParameter("subordinadas", subordinadas);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getSomaDasReceitasExtrasMensal(Pessoa p, Mes mes, Integer ano, DirfReg316 nomeReduzido, List<Long> subordinadas, ContaExtraorcamentaria contaExtraorcamentaria) {
        String sql = " select sum(valor) from (select coalesce(sum(rec.valor), 0) as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " inner join RetencaoPgto ret on ret.pagamento_id = pag.id " +
            " inner join receitaextra rec on rec.retencaopgto_id = ret.id " +
            " inner join conta contaextra on rec.contaextraorcamentaria_id = contaextra.id " +
            " where extract(year from rec.datareceita) = :ano " +
            " and extract(month from rec.datareceita) = :mes " +
            " and p.id = :pessoa_id " +
            " and vw.subordinada_id in :subordinadas " +
            " and trunc(rec.datareceita) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(rec.datareceita)) " +
            "       AND contaextra.codigo IN (SELECT contaextraconfig.codigo FROM itemgrupoexportacaocont igec              " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               inner join conta contaextraconfig on igec.contaextraorcamentaria_id = contaextraconfig.id " +
            "                               where contaextraconfig.codigo = :contaExtra                      " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido)                           " +
            " union all " +
            " select coalesce(sum(recest.valor), 0) * - 1 as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " inner join RetencaoPgto ret on ret.pagamento_id = pag.id " +
            " inner join receitaextra rec on rec.retencaopgto_id = ret.id " +
            " inner join receitaextraestorno recest on recest.receitaextra_id = rec.id " +
            " inner join conta contaextra on rec.contaextraorcamentaria_id = contaextra.id " +
            " where extract(year from recest.dataestorno) = :ano " +
            " and extract(month from recest.dataestorno) = :mes " +
            " and  p.id = :pessoa_id " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND contaextra.codigo IN (SELECT contaextraconfig.codigo FROM itemgrupoexportacaocont igec " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               inner join conta contaextraconfig on igec.contaextraorcamentaria_id = contaextraconfig.id " +
            "                               where contaextraconfig.codigo = :contaExtra                      " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido)                           " +
            " and trunc(recest.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(recest.dataestorno))) ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("subordinadas", subordinadas);
        q.setParameter("contaExtra", contaExtraorcamentaria.getCodigo());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getSomaDosEventosMensal(Pessoa p, VinculoFP vinculoFP, Mes mes, Integer ano, DirfReg316 nomeReduzido, List<TipoFolhaDePagamento> tiposFolha, EventoFP verba, Boolean temDeducaoDeValor) {
        String folhas = "and (";
        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";
        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                    "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                      "
            + "INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                     "
            + "INNER JOIN eventofp      evento ON  evento.id = iff.eventofp_id "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND fp.mes = :mes                                                                        "
            + "       AND iff.tipoeventofp = evento.tipoeventofp                                                                        "
            + "       AND m.pessoa_id = :pessoa_id                                                             ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }
        sql += "       AND v.id = :vinculo_id                                                                   "
            + folhas
            + "                  and iff.TIPOEVENTOFP = :vantagem " +
            "                   AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ge.nomereduzido = :nomeReduzido)                        ";
        if (!DirfReg316.RTRT.equals(nomeReduzido) || temDeducaoDeValor) {
            sql += ")  -                                                                                              "
                + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
                + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
                + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
                + " INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                     "
                + " INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                    "
                + "     WHERE fp.ano = :ano                                                                       "
                + "        AND fp.mes = :mes                                                                       "
                + "        AND m.pessoa_id = :pessoa_id                                                            ";
            if (verba != null) {
                sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
            }
            sql += " AND v.id = :vinculo_id                                                                  "
                + folhas
                + "       and iff.TIPOEVENTOFP = :desconto" +
                "         AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
                + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
                + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
                + "                                 WHERE me.descricao = :descricaoModulo                          "
                + "                                   AND ge.nomereduzido = :nomeReduzido)";
        }
        sql += " ) AS resultado FROM dual";
        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("vinculo_id", vinculoFP.getId());
        if (verba != null) {
            q.setParameter("eventofp_id", verba.getId());
        }
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());
        if (!DirfReg316.RTRT.equals(nomeReduzido) || temDeducaoDeValor) {
            q.setParameter("desconto", TipoEventoFP.DESCONTO.name());
        }
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getSomaDosEventosAnual(Pessoa p, VinculoFP vinculoFP, Integer ano, DirfReg316 nomeReduzido, List<TipoFolhaDePagamento> tiposFolha, boolean is13Salario, EventoFP verba, Boolean temDeducaoDeValor) {
        String folhas = "and (";

        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";

        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                      "
            + "INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                     "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND v.id = :vinculo_id                                                                   "
            + "       AND m.pessoa_id = :pessoa_id                                                             ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }
        sql += folhas
            + "       and iff.TIPOEVENTOFP = :vantagem              "
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ge.nomereduzido = :nomeReduzido)                         ";
        if (temDeducaoDeValor) {
            sql += " )-                                                                                              "
                + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
                + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
                + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
                + " INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                     "
                + " INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                    "
                + "      WHERE fp.ano = :ano                                                                       "
                + "       AND v.id = :vinculo_id                                                                   "
                + "        AND m.pessoa_id = :pessoa_id                                                            ";

            if (verba != null) {
                sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
            }
            sql += folhas
                + "        AND iff.TIPOEVENTOFP = :desconto             "
                + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
                + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
                + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
                + "                                 WHERE me.descricao = :descricaoModulo                          "
                + "                                   AND ge.nomereduzido = :nomeReduzido)";
        }
        sql += ") AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("vinculo_id", vinculoFP.getId());
        if (verba != null) {
            q.setParameter("eventofp_id", verba.getId());
        }
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", is13Salario ? nomeReduzido.toString() + "_13" : nomeReduzido.toString());
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());
        if (temDeducaoDeValor) {
            q.setParameter("desconto", TipoEventoFP.DESCONTO.name());
        }

        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private String getDescricaoDosEventosAnual(List<VinculoFP> vinculosFP, Integer ano, DirfReg316 nomeReduzido) {

        String sql = " select distinct e.descricaoreduzida " +
            "FROM itemfichafinanceirafp iff " +
            "         INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id " +
            "         INNER JOIN EVENTOFP e on e.id = iff.eventofp_id " +
            "         INNER JOIN folhadepagamento fp ON fp.id = ff.folhadepagamento_id " +
            "         INNER JOIN vinculofp v ON v.id = ff.vinculofp_id " +
            "         INNER JOIN matriculafp m ON m.id = v.matriculafp_id " +
            " WHERE fp.ano = :ano " +
            "  AND v.id in (:vinculo_id) " +
            "  and iff.TIPOEVENTOFP = :vantagem " +
                    "  AND iff.eventofp_id IN (SELECT ige.eventofp_id " +
            "                          FROM itemgrupoexportacao ige " +
            "                                   INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id " +
            "                                   INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                          WHERE me.descricao = :descricaoModulo " +
            "                            AND ge.nomereduzido in (:nomeReduzido)) ";

        Query q = em.createNativeQuery(sql);

        List<Long> idVinculos = Lists.newArrayList();
        for (VinculoFP vinculoFP : vinculosFP) {
            idVinculos.add(vinculoFP.getId());
        }

        q.setParameter("ano", ano);
        q.setParameter("vinculo_id", idVinculos);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", Lists.newArrayList(nomeReduzido.toString(),(nomeReduzido.toString() + "_13")));
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());

     List<String> resultList = q.getResultList();
        if(resultList !=  null && !resultList.isEmpty()){
            String retorno = "";
            for (String descricao : resultList) {
                retorno += descricao + ", ";
            }
            retorno = retorno.substring(0, retorno.length() - 2);
            return retorno;
        }
        return "";
    }

    public Boolean existeValorParaOAno(VinculoFP v, Integer ano) {
        String sql = " select  COALESCE(sum(item.valor),0) from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on folha.ID = ficha.FOLHADEPAGAMENTO_ID " +
            " inner join ITEMFICHAFINANCEIRAFP item on ficha.ID = item.FICHAFINANCEIRAFP_ID " +

            " where ficha.VINCULOFP_ID = :vinculoId " +
            " and folha.ANO = :ano ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculoId", v.getId());
        q.setParameter("ano", ano);

        if (!q.getResultList().isEmpty()) {
            BigDecimal singleResult = (BigDecimal) q.getSingleResult();
            return singleResult.compareTo(BigDecimal.ZERO) > 0;
        }
        return Boolean.FALSE;
    }

    private Boolean existeLancamentoParaRegistroNoAno(Pessoa p, List<VinculoFP> vinculoFPs, Integer ano, DirfReg316 nomeReduzido) {
        if (DirfReg316.RIP65.equals(nomeReduzido)) {
            return fichaFinanceiraFPFacade.buscarValorParcelaIsentaAno(ano, vinculoFPs).compareTo(BigDecimal.ZERO) > 0;
        }

        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                    "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                      "
            + "INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                     "
            //                + "INNER JOIN vwhierarquiaadministrativa vw ON  vw.subordinada_id = m.unidadematriculado_id        "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND m.pessoa_id = :pessoa_id                                                             "
            + "       AND v.id in :vinculo_id                                                                   "
            //                + "       AND vw.codigo like :unidade_id                                                           "
//                + "       AND fp.tipoFolhaDePagamento = :tipo_folha                                                "
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ige.operacaoformula = :adicao                             "
            + "                                  AND ge.nomereduzido = :nomeReduzido))                         "
            + " -                                                                                              "
            + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
            + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
            + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
            + " INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                     "
            + " INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                    "
            //                + " INNER JOIN vwhierarquiaadministrativa vw ON  vw.subordinada_id = m.unidadematriculado_id       "
            + "      WHERE fp.ano = :ano                                                                       "
            + "       AND m.pessoa_id = :pessoa_id           "
            + "       AND v.id in :vinculo_id   "
            //                + "        AND vw.codigo like :unidade_id                                           "
//                + "        AND fp.tipoFolhaDePagamento = :tipo_folha                                               "
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
            + "                                 WHERE me.descricao = :descricaoModulo                          "
            + "                                   AND ige.operacaoformula = :subtracao                         "
            + "                                   AND ge.nomereduzido = :nomeReduzido)) AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("vinculo_id", getIdsVinculos(vinculoFPs));
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        q.setParameter("nomeReduzido", nomeReduzido.toString());
//        q.setParameter("tipo_folha", tipoFolha.name());
//        q.setParameter("unidade_id", hierarquia.getCodigoSemZerosFinais() + "%");

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null || resultado.equals(BigDecimal.ZERO)) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (NoResultException nre) {
            nre.printStackTrace();
            return Boolean.FALSE;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    public List<Long> getIdsVinculos(List<VinculoFP> vinculoFPs) {
        List<Long> ids = Lists.newArrayList();
        for (VinculoFP vinculoFP : vinculoFPs) {
            ids.add(vinculoFP.getId());
        }
        return ids;
    }

    private BigDecimal buscarTotalPagamentoParaRegistroNoAno(Pessoa p, Integer ano, DirfReg316 nomeReduzido, List<Long> subordinadas, ClasseCredor classeCredor, String codigo, boolean isPessoaFisica) {
        String campoCodigo = isPessoaFisica ? "igec.codigo" : "igec.codigopj";
        String sql = " select sum(valor) from (select coalesce(sum(pag.valor), 0) as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from pag.datapagamento) = :ano " +
            " and trunc(pag.datapagamento) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(pag.datapagamento)) " +
            " and p.id = :pessoa_id " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND cc.id IN (SELECT igec.classecredor_id FROM itemgrupoexportacaocont igec              " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               where igec.classecredor_id = :classeCredor                       " +
            "                                 AND " + campoCodigo + " = :codigo                              " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido)                           " +
            " union all " +
            " select coalesce(sum(est.valor), 0) * - 1 as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pagamentoestorno est on pag.id = est.pagamento_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from est.dataestorno) = :ano " +
            " and  p.id = :pessoa_id " +
            " and trunc(est.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(est.dataestorno)) " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND cc.id IN (SELECT igec.classecredor_id FROM itemgrupoexportacaocont igec              " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               where igec.classecredor_id = :classeCredor                       " +
            "                                 AND " + campoCodigo + " = :codigo                              " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido)) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("subordinadas", subordinadas);
        q.setParameter("classeCredor", classeCredor.getId());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("codigo", codigo);
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null || resultado.equals(BigDecimal.ZERO)) {
                return BigDecimal.ZERO;
            }
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            nre.printStackTrace();
            return BigDecimal.ZERO;
        } catch (Exception ex) {
            ex.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    private Boolean existePagamentoParaRegistroNoAno(Pessoa p, Integer ano, DirfReg316 nomeReduzido, List<Long> subordinadas, String codigo, boolean isPessoaFisica) {
        String campoCodigo = isPessoaFisica ? "igec.codigo" : "igec.codigopj";
        String sql = " select sum(valor) from (select coalesce(sum(pag.valor), 0) as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from pag.datapagamento) = :ano " +
            " and trunc(pag.datapagamento) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(pag.datapagamento)) " +
            " and p.id = :pessoa_id " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND cc.id IN (SELECT igec.classecredor_id FROM itemgrupoexportacaocont igec              " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               where igec.classecredor_id is not null                           " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND " + campoCodigo + " = :codigo                              " +
            "                                 AND ge.nomereduzido = :nomeReduzido)                          " +
            " union all " +
            " select coalesce(sum(est.valor), 0) * - 1 as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pagamentoestorno est on pag.id = est.pagamento_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from est.dataestorno) = :ano " +
            " and  p.id = :pessoa_id " +
            " and trunc(est.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(est.dataestorno)) " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND cc.id IN (SELECT igec.classecredor_id FROM itemgrupoexportacaocont igec              " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               where igec.classecredor_id is not null                           " +
            "                                 AND " + campoCodigo + " = :codigo                              " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido)) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("subordinadas", subordinadas);
        q.setParameter("codigo", codigo);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null || resultado.equals(BigDecimal.ZERO)) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (NoResultException nre) {
            nre.printStackTrace();
            return Boolean.FALSE;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private Boolean existeReceitaExtraParaRegistroNoAno(Pessoa p, Integer ano, DirfReg316 nomeReduzido, List<Long> subordinadas) {
        String sql = " select sum(valor) from (select coalesce(sum(rec.valor), 0) as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " inner join RetencaoPgto ret on ret.pagamento_id = pag.id " +
            " inner join receitaextra rec on rec.retencaopgto_id = ret.id " +
            " inner join conta contaextra on rec.contaextraorcamentaria_id = contaextra.id " +
            " where extract(year from rec.datareceita) = :ano " +
            " and trunc(rec.datareceita) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(rec.datareceita)) " +
            " and p.id = :pessoa_id " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND contaextra.codigo IN (SELECT contaextraconfig.codigo FROM itemgrupoexportacaocont igec " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id      " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id      " +
            "                               inner join conta contaextraconfig on igec.contaextraorcamentaria_id = contaextraconfig.id " +
            "                               where igec.contaextraorcamentaria_id is not null                      " +
            "                                 AND me.descricao = :descricaoModulo                                 " +
            "                                 AND ge.nomereduzido = :nomeReduzido)                                " +
            " union all " +
            " select coalesce(sum(recest.valor), 0) * - 1 as valor from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " inner join RetencaoPgto ret on ret.pagamento_id = pag.id " +
            " inner join receitaextra rec on rec.retencaopgto_id = ret.id " +
            " inner join receitaextraestorno recest on recest.receitaextra_id = rec.id " +
            " inner join conta contaextra on rec.contaextraorcamentaria_id = contaextra.id " +
            " where extract(year from recest.dataestorno) = :ano " +
            " and  p.id = :pessoa_id " +
            " and trunc(recest.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(recest.dataestorno)) " +
            " and vw.subordinada_id in :subordinadas " +
            "       AND contaextra.codigo IN (SELECT contaextraconfig.codigo FROM itemgrupoexportacaocont igec " +
            "                               INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "                               INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                               inner join conta contaextraconfig on igec.contaextraorcamentaria_id = contaextraconfig.id " +
            "                               where igec.contaextraorcamentaria_id is not null                 " +
            "                                 AND me.descricao = :descricaoModulo                            " +
            "                                 AND ge.nomereduzido = :nomeReduzido))                          ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("subordinadas", subordinadas);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null || resultado.equals(BigDecimal.ZERO)) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } catch (NoResultException nre) {
            nre.printStackTrace();
            return Boolean.FALSE;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private List<ContratoFP> getContratosDaPessoaComRendimentoEm(PessoaFisica p, Integer ano, UnidadeOrganizacional unidade) {
        String hql = "select distinct c from ContratoFP c, FichaFinanceiraFP ff"
            + " inner join c.matriculaFP m"
            + " inner join ff.folhaDePagamento fp"
            + " where m.unidadeMatriculado = :unidade"
            + "   and ff.vinculoFP = c"
            + "   and m.pessoa = :pessoaFisica"
            + "   and fp.ano = :ano";

        Query q = em.createQuery(hql);
        q.setParameter("ano", ano);
        q.setParameter("pessoaFisica", p);
        q.setParameter("unidade", unidade);
        return q.getResultList();
    }

    private List<PessoaDirf> getPessoasComRendimentosEm(Integer ano, HierarquiaOrganizacional hierarquia, Dirf d) {
        String hql = "  select new br.com.webpublico.entidadesauxiliares.rh.PessoaDirf(pf, c) from VinculoFP c, FichaFinanceiraFP ff"
            + " inner join c.recursosDoVinculoFP rv"
            + " inner join rv.recursoFP rfp"
            + " inner join ff.folhaDePagamento fp"
            + " inner join c.matriculaFP m"
            + " inner join m.pessoa pf"
            + " where (:inicio between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + " or :fim between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + " or (rv.inicioVigencia between :inicio and :fim and rv.finalVigencia between :inicio and :fim))"
            + "        and fp.ano = :ano"
            + "        and rfp.codigoOrgao = :unidade "
            + "        and ff.vinculoFP = c";

        if (d.getItemVinculoFP() != null && !d.getItemVinculoFP().isEmpty()) {
            hql += " and c in :vinculos";
        }
        Query q = em.createQuery(hql);
        q.setParameter("ano", ano);
        q.setParameter("inicio", d.getPrimeiroDia());
        q.setParameter("fim", d.getUltimoDia());
        q.setParameter("unidade", Integer.parseInt(hierarquia.getCodigoDo2NivelDeHierarquia()));
        if (d.getItemVinculoFP() != null && !d.getItemVinculoFP().isEmpty()) {
            q.setParameter("vinculos", getVinculosDirf(d.getItemVinculoFP()));
        }
        return q.getResultList();
    }

    private List<VinculoFP> getVinculosDirf(List<DirfVinculoFP> dirfVinculoFP) {
        List<VinculoFP> vinculos = Lists.newArrayList();
        for (DirfVinculoFP vinculosDIrf : dirfVinculoFP) {
            vinculos.add(vinculosDIrf.getVinculoFP());
        }
        return vinculos;
    }

    private List<PessoaFisica> getPessoasFisicasComPagamentosEm(Integer ano, HierarquiaOrganizacional hierarquia, String codigo) {
        List<Long> idsClasseCredor = new ArrayList<>();
        for (ItemGrupoExportacaoContabil itens : recuperarItemGruposExportacaoContabilComClasseCredorPorModuloENomeReduzidoECodigo(null, codigo)) {
            idsClasseCredor.add(itens.getClasseCredor().getId());
        }
        String sql = " select pf.*, p.* " +
            " from (" +
            " select pessoa_id, sum(valor) as valor from (" +
            " select p.id as pessoa_id, coalesce(sum(pag.valor), 0) as valor " +
            " from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join pessoafisica pf on p.id = pf.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from pag.datapagamento) = :ano " +
            " and vw.subordinada_id = :vwSubordinada " +
            " and trunc(pag.datapagamento) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(pag.datapagamento)) ";
        if (!idsClasseCredor.isEmpty()) {
            sql += " and cc.id in :idsClasseCredor ";
        }
        sql += " group by p.id " +
            " union all " +
            " select p.id as pessoa_id, coalesce(sum(est.valor) * - 1, 0) as valor " +
            " from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pagamentoestorno est on pag.id = est.pagamento_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join pessoafisica pf on p.id = pf.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from est.dataestorno) = :ano " +
            " and vw.subordinada_id = :vwSubordinada " +
            " and trunc(est.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(est.dataestorno)) ";
        if (!idsClasseCredor.isEmpty()) {
            sql += " and cc.id in :idsClasseCredor ";
        }
        sql += " group by p.id ) reg " +
            " group by pessoa_id ) dados " +
            " inner join pessoa p on dados.pessoa_id = p.id " +
            " inner join pessoafisica pf on p.id = pf.id " +
            " where valor <> 0 ";
        Query q = em.createNativeQuery(sql, PessoaFisica.class);
        q.setParameter("ano", ano);
        q.setParameter("vwSubordinada", hierarquia.getSubordinada().getId());
        if (!idsClasseCredor.isEmpty()) {
            q.setParameter("idsClasseCredor", idsClasseCredor);
        }
        return (List<PessoaFisica>) q.getResultList();
    }

    private List<PessoaJuridica> getPessoasJuridicasComPagamentosEm(Integer ano, HierarquiaOrganizacional hierarquia, String codigoPj) {
        List<Long> idsClasseCredor = new ArrayList<>();
        for (ItemGrupoExportacaoContabil itens : buscarItensGruposExportacaoContabilComClasseCredorPorModuloECodigoPJ(codigoPj)) {
            idsClasseCredor.add(itens.getClasseCredor().getId());
        }
        String sql = " select pj.*, p.* " +
            " from (" +
            " select pessoa_id, sum(valor) as valor from (" +
            " select p.id as pessoa_id, coalesce(sum(pag.valor), 0) as valor " +
            " from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join pessoajuridica pj on p.id = pj.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from pag.datapagamento) = :ano " +
            " and vw.subordinada_id = :vwSubordinada " +
            " and trunc(pag.datapagamento) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(pag.datapagamento)) ";
        if (!idsClasseCredor.isEmpty()) {
            sql += " and cc.id in :idsClasseCredor ";
        }
        sql += " group by p.id " +
            " union all " +
            " select p.id as pessoa_id, coalesce(sum(est.valor) * - 1, 0) as valor " +
            " from empenho emp " +
            " inner join liquidacao liq on emp.id = liq.empenho_id " +
            " inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " inner join pagamentoestorno est on pag.id = est.pagamento_id " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            " inner join pessoajuridica pj on p.id = pj.id " +
            " inner join classecredor cc on emp.classecredor_id  = cc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " +
            " where extract(year from est.dataestorno) = :ano " +
            " and vw.subordinada_id = :vwSubordinada " +
            " and trunc(est.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(est.dataestorno)) ";
        if (!idsClasseCredor.isEmpty()) {
            sql += " and cc.id in :idsClasseCredor ";
        }
        sql += " group by p.id ) reg " +
            " group by pessoa_id ) dados " +
            " inner join pessoa p on dados.pessoa_id = p.id " +
            " inner join pessoajuridica pj on p.id = pj.id " +
            " where valor <> 0 ";
        Query q = em.createNativeQuery(sql, PessoaJuridica.class);
        q.setParameter("ano", ano);
        q.setParameter("vwSubordinada", hierarquia.getSubordinada().getId());
        if (!idsClasseCredor.isEmpty()) {
            q.setParameter("idsClasseCredor", idsClasseCredor);
        }
        return (List<PessoaJuridica>) q.getResultList();
    }

    private List<PessoaFisica> ordenarPessoasPorCpf(List<PessoaFisica> pessoas) {
        Collections.sort(pessoas, new Comparator<PessoaFisica>() {
            @Override
            public int compare(PessoaFisica p1, PessoaFisica p2) {
                String cpf1 = "";
                String cpf2 = "";

                try {
                    cpf1 = p1.getCpf();
                } catch (NullPointerException npe) {
                    return -1;
                }

                try {
                    cpf2 = p2.getCpf();
                } catch (NullPointerException npe) {
                    return 1;
                }

                return cpf1.compareTo(cpf2);
            }
        });
        return pessoas;
    }

    private List<BeneficioPensaoAlimenticia> ordenarPensoesAlimenticias(List<BeneficioPensaoAlimenticia> pessoas) {
        Collections.sort(pessoas, new Comparator<BeneficioPensaoAlimenticia>() {
            @Override
            public int compare(BeneficioPensaoAlimenticia p1, BeneficioPensaoAlimenticia p2) {
                String cpf1 = null;
                String cpf2 = null;
                String nome1 = "";
                String nome2 = "";

                try {
                    cpf1 = p1.getPessoaFisicaBeneficiario().getCpf().trim();
                } catch (NullPointerException npe) {
                    cpf1 = null;
                }

                try {
                    cpf2 = p2.getPessoaFisicaBeneficiario().getCpf().trim();
                } catch (NullPointerException npe) {
                    cpf2 = null;
                }

                // Se ambos não têm CPF, ordena pelo nome
                if (cpf1 == null && cpf2 == null) {
                    try {
                        nome1 = p1.getPessoaFisicaBeneficiario().getNome().trim();
                    } catch (NullPointerException npe) {
                        return -1;
                    }

                    try {
                        nome2 = p2.getPessoaFisicaBeneficiario().getNome().trim();
                    } catch (NullPointerException npe) {
                        return 1;
                    }

                    return nome1.compareTo(nome2);
                }

                // Se apenas um tem CPF, quem NÃO tem CPF vem primeiro
                if (cpf1 == null) return -1;
                if (cpf2 == null) return 1;

                return cpf1.compareTo(cpf2);
            }
        });
        return pessoas;
    }

    private List<PessoaInfo> ordenarPessoasDirfPorCpf(List<PessoaInfo> pessoas) {
        Collections.sort(pessoas, new Comparator<PessoaInfo>() {
            @Override
            public int compare(PessoaInfo p1, PessoaInfo p2) {
                String cpf1 = "";
                String cpf2 = "";

                try {
                    cpf1 = p1.getPessoa().getCpf();
                } catch (NullPointerException npe) {
                    return -1;
                }

                try {
                    cpf2 = p2.getPessoa().getCpf();
                } catch (NullPointerException npe) {
                    return 1;
                }
                try {
                    return cpf1.compareTo(cpf2);
                } catch (Exception e) {
                    return 0;
                }

            }
        });
        return pessoas;
    }

    private List<PessoaJuridica> ordenarPessoasPorCnpj(List<PessoaJuridica> pessoas) {
        Collections.sort(pessoas, new Comparator<PessoaJuridica>() {
            @Override
            public int compare(PessoaJuridica p1, PessoaJuridica p2) {
                String cnpj1 = "";
                String cnpj2 = "";

                try {
                    cnpj1 = p1.getCnpj();
                } catch (NullPointerException npe) {
                    return -1;
                }

                try {
                    cnpj2 = p2.getCnpj();
                } catch (NullPointerException npe) {
                    return 1;
                }

                return cnpj1.compareTo(cnpj2);
            }
        });
        return pessoas;
    }

    private String getLayoutDeAcordoComAnoReferencia(Integer ano) {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigenteDataAtual();
        return configuracaoRHFacade.getCodigoDirfPorAno(ano + 1, configuracaoRH);
    }

    /**
     * 3.1 - Registro de identificação da declaração (Identificador Dirf)
     *
     * @return linha referênte ao registro 3.1
     */
    private String processarRegistro31(Dirf d) {
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance();
        c.setTime(d.getDataOperacao());
        sb.append("DIRF").append("|");
        Integer anoReferencia = d.getExercicio().getAno() + 1;
        sb.append(anoReferencia).append("|");
        sb.append(d.getExercicio().getAno().toString()).append("|");
        sb.append("N").append("|");
        sb.append("").append("|");
        sb.append(getLayoutDeAcordoComAnoReferencia(d.getExercicio().getAno())).append("|");
        sb.append("\r\n");

        return sb.toString();
    }

    /**
     * 3.2 - Registro de identificação da declaração (Identificador Dirf)
     *
     * @return linha referênte ao registro 3.2
     */
    private String processarRegistro32(Dirf d) {
        StringBuilder sb = new StringBuilder();

//        Pessoa p = d.getResponsavel().getProvimentoFP().getVinculoFP().getMatriculaFP().getPessoa();
        Pessoa p = d.getResponsavel().getMatriculaFP().getPessoa();
        p = pessoaFacade.recuperar(p.getId());
        Telefone fonePrincipal = p.getTelefonePrincipal() != null ? p.getTelefonePrincipal() : p.getTelefones().get(0);
        String ddd = fonePrincipal.getDDD();
        Telefone fax = p.getFax() != null ? p.getFax() : new Telefone();
        if (fax.getTelefone() == null) {
            fax.setTelefone("");
        }
        if (ddd.equals("")) {
            ddd += "00";
        }

        if (p.getEmail() == null) {
            p.setEmail("");
        }

        sb.append("RESPO").append("|");
        sb.append(p.getCpf_Cnpj().replaceAll("[^0-9]", "").trim()).append("|");
        sb.append(getStringParaDeclaracao(p.getNome())).append("|");
        sb.append(ddd).append("|"); //campo 4
        sb.append(StringUtil.cortaEsquerda(8, fonePrincipal.getSomenteTelefone())).append("|"); //campo 5
        sb.append("").append("|"); // Ramal
        sb.append(fax.getTelefone()).append("|"); // Fax
        sb.append(p.getEmail()).append("|"); // E-mail
        sb.append("\r\n");

        return sb.toString();
    }

    /**
     * 3.3 - Registro de identificação do declarante pessoa física
     * (Identificador DECPF)
     *
     * @return linha referênte ao registro 3.3
     */
    public String processarRegistro33(Dirf d) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    /**
     * 3.4 - Registro de identificação do declarante pessoa
     * jurídica(Identificador DECPJ)
     *
     * @return linha referênte ao registro 3.4
     */
    private String processarRegistro34(Dirf d) {
        StringBuilder sb = new StringBuilder();

//        Pessoa p = d.getResponsavel().getProvimentoFP().getVinculoFP().getMatriculaFP().getPessoa();
        Pessoa p = d.getResponsavel().getMatriculaFP().getPessoa();
        p = pessoaFacade.recuperar(p.getId());

        sb.append("DECPJ").append("|");
        sb.append(d.getEntidade().getPessoaJuridica().getCnpj().replaceAll("[^0-9]", "").trim()).append("|");
        sb.append(StringUtil.removeCaracteresEspeciais(d.getEntidade().getPessoaJuridica().getNome())).append("|");
        sb.append(d.getEntidade().getTipoUnidade().getCodigoDirf()).append("|");
        sb.append(p.getCpf_Cnpj().replaceAll("[^0-9]", "").trim()).append("|");
        sb.append("N").append("|"); // 6
        sb.append("N").append("|"); // 7
        sb.append("N").append("|"); // 8
        sb.append("N").append("|"); // 9
        sb.append("N").append("|"); // 10
        sb.append("N").append("|"); // 11
        sb.append("N").append("|"); // 12
        sb.append(getSituacaoEspecialDeAcordoComAno(d.getExercicio().getAno())).append("|"); // 13
//        sb.append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append("|");
        sb.append("").append("|"); // 14
        sb.append("\r\n");

        return sb.toString();
    }

    private String getSituacaoEspecialDeAcordoComAno(Integer ano) {
        switch (ano) {
            case 2013:
                return "S";
            case 2014:
                return "S";
            default:
                return "N";
        }
    }

    /**
     * 3.5 - Registro de identificação do declarante pessoa
     * jurídica(Identificador DECPJ)
     *
     * @return linha referênte ao registro 3.5
     */
    private String processarRegistro35(Dirf d, LoteDirf loteDirf) {
        StringBuilder sb = new StringBuilder();

        sb.append("IDREC").append("|");
        /**
         * Ver detalhes em
         * http://www.forumrm.com.br/index.php?/topic/11091-parametrizacao-para-gerar-dirf-informe-de-rendimentos/
         * Acesso em 23/01/2013
         */
        sb.append(loteDirf.getDirfReg35().getCodigo()).append("|");
        sb.append("\r\n");

        return sb.toString();
    }

    /**
     * 3.5 - Registro de identificação do declarante pessoa
     * jurídica(Identificador DECPJ)
     *
     * @return linha referênte ao registro 3.5
     */
    private String processarRegistro35Contabil(String codigo) {
        StringBuilder sb = new StringBuilder();

        sb.append("IDREC").append("|");
        sb.append(codigo).append("|");
        sb.append("\r\n");

        return sb.toString();
    }

    /**
     * 3.6 - Registro de beneficiário pessoa física do declarante(Identificador
     * BPFDEC)
     *
     * @return linha referênte ao registro 3.6
     */
    private String processarRegistro36DirfRH(Dirf d, LoteDirf loteDirf) throws IOException {

        List<PessoaInfo> pessoasAgrupadas = loteDirf.getPessoas();


        pessoasAgrupadas = ordenarPessoasDirfPorCpf(pessoasAgrupadas);

        StringBuilder sb = new StringBuilder();

        for (PessoaInfo p : pessoasAgrupadas) {
            DirfVinculoFP dirfVinculoFP = new DirfVinculoFP();
            dirfVinculoFP.setFontePagadora(d.getEntidade().getPessoaJuridica());
            if (p.getVinculos().get(0).getNaturezaRendimento() != null) {
                dirfVinculoFP.setNaturezaDoRendimento(p.getVinculos().get(0).getNaturezaRendimento().getDescricao());
            } else {
                dirfVinculoFP.setNaturezaDoRendimento("NATUREZA DE RENDIMENTO NÃO ENCONTRADA");
            }

            dirfVinculoFP.setDirf(d);
            d.getItemDirfVinculoFP().add(dirfVinculoFP);

            if (dependenciasDirf.getParado()) {
                return "";
            }
            if (naoPossuiRendimento(p, d)) {
                dependenciasDirf.incrementarContador();
                continue;
            }

            try {
                StringBuilder sbDaVez = new StringBuilder();
                sbDaVez.append("BPFDEC").append("|");
                sbDaVez.append(p.getPessoa().getCpf_Cnpj().replaceAll("[^0-9]", "").trim()).append("|");
                sbDaVez.append(getStringParaDeclaracao(p.getPessoa().getNome().trim())).append("|");

                sbDaVez.append(definirCamposBPFDEC(p, d));
                sbDaVez.append("\r\n");
                addLog(dependenciasDirf.getContadorProcessos() + 1 + " - GERANDO REGISTROS DE: " + p.getPessoa().getNome().toUpperCase(), "<b>", "</b>");
                sbDaVez.append(processarRegistro316(p, d, loteDirf, dirfVinculoFP));
                sb.append(sbDaVez);
                addLog("&nbsp;&bull; GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
            } catch (Exception e) {
                String complemento = p.getPessoa().getNome() == null ? "PESSOA ID = " + p.getPessoa().getId() : p.getPessoa().getNome();

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                logger.info("Erro: ", e);
                pw.close();
                sw.close();
                String detalhes = sw.getBuffer().toString();

                addLog("&nbsp;&bull; NÃO FOI POSSÍVEL GERAR O REGISTRO DE " + complemento + " DETALHES : " + detalhes, "<font style='color : red;'><i>", "</i></font>");
            }

            dependenciasDirf.incrementarContador();
        }

        return sb.toString();
    }

    private boolean naoPossuiRendimento(PessoaInfo p, Dirf d) {
        for (VinculoFP vinculoFP : p.getVinculos()) {
            if (existeValorParaOAno(vinculoFP, d.getExercicio().getAno())) {
                return false;
            }
        }

        return true;
    }

    private InvalidezAposentado buscarInvalidezAposentado(VinculoFP vinculoFP, Dirf dirf) {
        String sql = " select invalidez.* from InvalidezAposentado invalidez where APOSENTADORIA_ID = :aposentado " +
            " and :ano between extract (year from INICIOVIGENCIA) and extract (year from coalesce(FINALVIGENCIA, current_date))";
        Query q = em.createNativeQuery(sql, InvalidezAposentado.class);
        q.setParameter("aposentado", vinculoFP.getId());
        q.setParameter("ano", dirf.getExercicio().getAno());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (InvalidezAposentado) q.getSingleResult();
        }
        return null;
    }

    private InvalidezAposentado buscarInvalidezAposentadoNoMes(VinculoFP vinculoFP, Dirf dirf, Mes mes) {
        String sql = " select invalidez.* from InvalidezAposentado invalidez where APOSENTADORIA_ID = :aposentado " +
            " and :ano between extract (year from INICIOVIGENCIA) and extract (year from coalesce(FINALVIGENCIA, current_date)) ";
        if (mes != null) {
            sql += " and :mes > extract (month from INICIOVIGENCIA)";
        }
        Query q = em.createNativeQuery(sql, InvalidezAposentado.class);
        q.setParameter("aposentado", vinculoFP.getId());
        q.setParameter("ano", dirf.getExercicio().getAno());
        if (mes != null) {
            q.setParameter("mes", mes.getNumeroMes());
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (InvalidezAposentado) q.getSingleResult();
        }
        return null;
    }

    private boolean hasServidorEmMolestiaGrave(List<VinculoFP> vinculoFP, Dirf dirf, Integer mes) {
        String sql = " select invalidez.* from InvalidezAposentado invalidez where APOSENTADORIA_ID in :aposentado " +
            " and  to_date(to_char(:mesAno, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(iniciovigencia, 'mm/yyyy'), 'mm/yyyy') " +
            " and coalesce(to_date(to_char(finalvigencia, 'mm/yyyy'), 'mm/yyyy'),  to_date(to_char(:mesAno, 'mm/yyyy'), 'mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, InvalidezAposentado.class);
        q.setParameter("aposentado", getIdsVinculos(vinculoFP));
        q.setParameter("mesAno", DataUtil.montaData(1, mes - 1, dirf.getExercicio().getAno()).getTime());
        return q.getResultList().isEmpty();
    }


    private StringBuilder definirCamposBPFDEC(PessoaInfo p, Dirf d) {
        StringBuilder sbDaVez = new StringBuilder();
        String dataMolestia = "";
        for (VinculoFP vinculoFP : p.getVinculos()) {
            if (vinculoFP instanceof Aposentadoria || vinculoFP instanceof Pensionista) {
                InvalidezAposentado invalidez = buscarInvalidezAposentado(vinculoFP, d);
                if (invalidez != null) {
                    dataMolestia = DataUtil.converterAnoMesDia(invalidez.getInicioVigencia());
                }
                break;
            }
        }
        sbDaVez.append(dataMolestia).append("|");
        List<BeneficioPensaoAlimenticia> filhos = pensaoAlimenticiaFacade.buscarBeneficiariosPensaoAlimenticiaPorAnoAndPessoa(p.getPessoa(), d.getExercicio());
        if (filhos == null || filhos.isEmpty()) {
            sbDaVez.append("N").append("|");//5 Indicador de identificação do alimentando
        } else {
            sbDaVez.append("S").append("|");//5 Indicador de identificação do alimentando
        }

        sbDaVez.append("N").append("|");//6 Indicador de identificação da previdência complementar
        return sbDaVez;
    }

    private void montarAgrupamento(List<PessoaDirf> pessoas, List<PessoaInfo> pessoasAgrupadas, List<PessoaInfo> aposentadosPensionistas) {
        for (PessoaDirf pessoa : pessoas) {

            if (pessoa.getVinculoFP() instanceof ContratoFP) {
                PessoaInfo info = hasPessoaAgrupadas(pessoasAgrupadas, pessoa.getPessoaFisica());
                if (info != null) {
                    int i = pessoasAgrupadas.indexOf(info);
                    pessoasAgrupadas.get(i).getVinculos().add(pessoa.getVinculoFP());
                } else {
                    PessoaInfo pessoaInfo = new PessoaInfo();
                    pessoaInfo.setPessoa(pessoa.getPessoaFisica());
                    pessoaInfo.getVinculos().add(pessoa.getVinculoFP());
                    pessoasAgrupadas.add(pessoaInfo);
                }
            }

            if (pessoa.getVinculoFP() instanceof Aposentadoria || pessoa.getVinculoFP() instanceof Pensionista) {

                PessoaInfo info2 = hasPessoaAgrupadas(aposentadosPensionistas, pessoa.getPessoaFisica());
                if (info2 != null) {
                    int i = aposentadosPensionistas.indexOf(info2);
                    aposentadosPensionistas.get(i).getVinculos().add(pessoa.getVinculoFP());
                } else {
                    PessoaInfo pessoaInfo2 = new PessoaInfo();
                    pessoaInfo2.setPessoa(pessoa.getPessoaFisica());
                    pessoaInfo2.getVinculos().add(pessoa.getVinculoFP());
                    aposentadosPensionistas.add(pessoaInfo2);
                }
            }
        }

    }

    private PessoaInfo hasPessoaAgrupadas(List<PessoaInfo> pessoasAgrupadas, PessoaFisica pessoaFisica) {
        for (PessoaInfo pessoasAgrupada : pessoasAgrupadas) {
            if (pessoasAgrupada.getPessoa().equals(pessoaFisica)) return pessoasAgrupada;
        }
        return null;
    }

    private List<Pessoa> getQuantidadePessoasFisicasComPagamentos(List<HierarquiaOrganizacional> hierarquias, String codigo, Integer ano) {
        List<Pessoa> pessoas = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hierarquias) {
            for (HierarquiaOrganizacionalResponsavel responsavel : ho.getHierarquiaOrganizacionalResponsavels()) {
                pessoas.addAll(getPessoasFisicasComPagamentosEm(ano, responsavel.getHierarquiaOrganizacionalOrc(), codigo));
            }
        }
        return new ArrayList(new HashSet(pessoas));
    }

    private List<Pessoa> getQuantidadePessoasJuridicasComPagamentos(List<HierarquiaOrganizacional> hierarquias, String codigo, Integer ano) {
        List<Pessoa> pessoas = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hierarquias) {
            for (HierarquiaOrganizacionalResponsavel responsavel : ho.getHierarquiaOrganizacionalResponsavels()) {
                pessoas.addAll(getPessoasJuridicasComPagamentosEm(ano, responsavel.getHierarquiaOrganizacionalOrc(), codigo));
            }
        }
        return new ArrayList(new HashSet(pessoas));
    }

    /**
     * 3.6 - Registro de beneficiário pessoa física do declarante(Identificador
     * BPFDEC)
     *
     * @return linha referênte ao registro 3.6 Contábil
     */
    private String processarRegistro36Contabil(Dirf d, String codigo) throws IOException {

        List<PessoaFisica> pessoas = new ArrayList<>();

        d.setHierarquiasOrganizacionais(buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado(d));
        List<Long> subordinadas = new ArrayList<>();
        for (HierarquiaOrganizacional ho : d.getHierarquiasOrganizacionais()) {
            for (HierarquiaOrganizacionalResponsavel responsavel : ho.getHierarquiaOrganizacionalResponsavels()) {
                subordinadas.add(responsavel.getHierarquiaOrganizacionalOrc().getSubordinada().getId());
                pessoas.addAll(getPessoasFisicasComPagamentosEm(d.getExercicio().getAno(), responsavel.getHierarquiaOrganizacionalOrc(), codigo));
            }
        }

        pessoas = new ArrayList(new HashSet(pessoas));
        pessoas = ordenarPessoasPorCpf(pessoas);
        StringBuilder sb = new StringBuilder();

        if (pessoas == null) {
            addLog("NÃO FORAM LOCALIZADOS SERVIDORES COM RENDIMENTOS NO ANO INFORMADO.");
            throw new ExcecaoNegocioGenerica("Não foram localizados servidores com rendimentos no ano informado.");
        }

        for (PessoaFisica p : pessoas) {
            if (dependenciasDirf.getParado()) {
                return "";
            }

            try {
                StringBuilder sbDaVez = new StringBuilder();
                sbDaVez.append("BPFDEC").append("|");
                sbDaVez.append(p.getCpf_Cnpj().replaceAll("[^0-9]", "").trim()).append("|");
                sbDaVez.append(getStringParaDeclaracao(p.getNome())).append("|");
                sbDaVez.append("").append("|");//TODO verificar moléstia grave
                sbDaVez.append("N").append("|");//5 Indicador de identificação do alimentando
                sbDaVez.append("N").append("|");//6 Indicador de identificação da previdência complementar
                sbDaVez.append("\r\n");
                addLog(dependenciasDirf.getContadorProcessos() + 1 + " - GERANDO REGISTROS DE: " + p.getNome().toUpperCase(), "<b>", "</b>");
                sbDaVez.append(processarRegistro316Contabil(p, d, subordinadas, codigo));
                sb.append(sbDaVez);
                addLog("&nbsp;&bull; GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
            } catch (Exception e) {
                String complemento = p.getNome() == null ? "PESSOA ID = " + p.getId() : p.getNome();

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.close();
                sw.close();
                String detalhes = sw.getBuffer().toString();

                addLog("&nbsp;&bull; NÃO FOI POSSÍVEL GERAR O REGISTRO DE " + complemento + " DETALHES : " + detalhes, "<font style='color : red;'><i>", "</i></font>");
            }

            dependenciasDirf.incrementarContador();
        }

        return sb.toString();
    }

    /**
     * 3.7 - Registro de beneficiário pessoa juridica do declarante(Identificador
     * BPJDEC)
     *
     * @return linha referênte ao registro 3.7 Contábil
     */
    private String processarRegistro37Contabil(Dirf d, String codigoPj) throws IOException {

        List<PessoaJuridica> pessoas = new ArrayList<>();

        d.setHierarquiasOrganizacionais(buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado(d));
        List<Long> subordinadas = new ArrayList<>();
        for (HierarquiaOrganizacional ho : d.getHierarquiasOrganizacionais()) {
            for (HierarquiaOrganizacionalResponsavel responsavel : ho.getHierarquiaOrganizacionalResponsavels()) {
                subordinadas.add(responsavel.getHierarquiaOrganizacionalOrc().getSubordinada().getId());
                pessoas.addAll(getPessoasJuridicasComPagamentosEm(d.getExercicio().getAno(), responsavel.getHierarquiaOrganizacionalOrc(), codigoPj));
            }
        }

        pessoas = new ArrayList(new HashSet(pessoas));
        pessoas = ordenarPessoasPorCnpj(pessoas);
        StringBuilder sb = new StringBuilder();

        if (pessoas == null) {
            addLog("NÃO FORAM LOCALIZADOS SERVIDORES COM RENDIMENTOS NO ANO INFORMADO.");
            throw new ExcecaoNegocioGenerica("Não foram localizados servidores com rendimentos no ano informado.");
        }

        for (PessoaJuridica p : pessoas) {
            if (dependenciasDirf.getParado()) {
                return "";
            }

            try {
                StringBuilder sbDaVez = new StringBuilder();
                sbDaVez.append("BPJDEC").append("|");
                sbDaVez.append(p.getCpf_Cnpj().replaceAll("[^0-9]", "").trim()).append("|");
                sbDaVez.append(getStringParaDeclaracao(p.getNome())).append("|");
                sbDaVez.append("\r\n");
                addLog(dependenciasDirf.getContadorProcessos() + 1 + " - GERANDO REGISTROS DE: " + p.getNome().toUpperCase(), "<b>", "</b>");
                sbDaVez.append(processarRegistro316Contabil(p, d, subordinadas, codigoPj));
                sb.append(sbDaVez);
                addLog("&nbsp;&bull; GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
            } catch (Exception e) {
                String complemento = p.getRazaoSocial() == null ? "PESSOA ID = " + p.getId() : p.getRazaoSocial();

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.close();
                sw.close();
                String detalhes = sw.getBuffer().toString();

                addLog("&nbsp;&bull; NÃO FOI POSSÍVEL GERAR O REGISTRO DE " + complemento + " DETALHES : " + detalhes, "<font style='color : red;'><i>", "</i></font>");
            }

            dependenciasDirf.incrementarContador();
        }

        return sb.toString();
    }

    private List<String> getCodigosItemGrupoExportacaoContabil() {
        String sql = " SELECT distinct igec.codigo FROM itemgrupoexportacaocont igec     " +
            "  INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id   " +
            "  INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id   " +
            "  where me.descricao = :descricaoModulo and igec.codigo is not null  " +
            "  order by to_number(igec.codigo)                                    ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        return (List<String>) q.getResultList();
    }

    private List<String> getCodigosPjItemGrupoExportacaoContabil() {
        String sql = " SELECT distinct igec.codigopj " +
            "  FROM itemgrupoexportacaocont igec " +
            "     INNER JOIN grupoexportacao ge ON igec.grupoexportacao_id = ge.id " +
            "     INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "  where me.descricao = :descricaoModulo " +
            "    and igec.codigopj is not null " +
            "  order by to_number(igec.codigopj) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        return (List<String>) q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiasFilhasDe(UnidadeOrganizacional uo, Date data, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        String sql = "SELECT h FROM HierarquiaOrganizacional h  "
            + " WHERE :dataAtual between h.inicioVigencia and coalesce(h.fimVigencia, :dataAtual) "
            + " AND h.superior = :superior "
            + " AND h.tipoHierarquiaOrganizacional = :tipo "
            + " ORDER BY h.codigo ";
        Query q = em.createQuery(sql);
        q.setParameter("superior", uo);
        q.setParameter("dataAtual", data, TemporalType.DATE);
        q.setParameter("tipo", tipoHierarquiaOrganizacional);
        return (List<HierarquiaOrganizacional>) q.getResultList();
    }

    private String getStringParaDeclaracao(String nome) {
        nome = StringUtil.removeCaracteresEspeciaisENumeros(nome);
        nome = nome.replace("\"", "");
        nome = nome.replace("'", " ");
        nome = nome.replace("´", " ");
        nome = nome.replace("`", " ");
        nome = StringUtil.removeEspacos(nome);
        nome = nome.toUpperCase();
        return nome;
    }

    private String getValorFormatadoParaArquivo(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        String r = nf.format(valor);
        r = StringUtil.retornaApenasNumeros(r);
        return r;
    }

    private boolean hasFichaFinanceiraParaOMes(VinculoFP vinculoFP, Dirf d, Mes mes, List<TipoFolhaDePagamento> tipos) {
        String sql = "select distinct iff.mes from ITEMFICHAFINANCEIRAFP iff " +
            " inner join FICHAFINANCEIRAFP ficha on iff.FICHAFINANCEIRAFP_ID = ficha.ID " +
            " inner join FolhaDePagamento folha on folha.id = ficha.folhaDePagamento_id " +
            " inner join vinculofp v on ficha.vinculofp_id = v.id  " +
            " inner join matriculafp mat on v.MATRICULAFP_ID = mat.id " +
            " where iff.ano = :ano and v.id = :vinculo  and iff.mes = :mes " +
            " and  folha.tipoFolhaDePagamento in(:tipos) " +
            " and not EXISTS(select aposentado.id from Aposentadoria aposentado where aposentado.id = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", d.getExercicio().getAno());
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("tipos", getTipoFolhaPagamento(tipos));
        return !q.getResultList().isEmpty();
    }

    public boolean hasFichaFinanceiraParaOMes(VinculoFP vinculoFP, Integer ano, Mes mes, List<TipoFolhaDePagamento> tipos) {
        String sql = "select distinct iff.mes from ITEMFICHAFINANCEIRAFP iff " +
            " inner join FICHAFINANCEIRAFP ficha on iff.FICHAFINANCEIRAFP_ID = ficha.ID " +
            " inner join FolhaDePagamento folha on folha.id = ficha.folhaDePagamento_id " +
            " inner join vinculofp v on ficha.vinculofp_id = v.id  " +
            " inner join matriculafp mat on v.MATRICULAFP_ID = mat.id " +
            " where iff.ano = :ano and v.id = :vinculo  and iff.mes = :mes " +
            " and  folha.tipoFolhaDePagamento in(:tipos) " +
            " and not EXISTS(select aposentado.id from Aposentadoria aposentado where aposentado.id = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("tipos", getTipoFolhaPagamento(tipos));
        return !q.getResultList().isEmpty();
    }

    private List<String> getTipoFolhaPagamento(List<TipoFolhaDePagamento> tipos) {
        List<String> folhas = Lists.newArrayList();
        for (TipoFolhaDePagamento tipo : tipos) {
            folhas.add(tipo.name());
        }
        return folhas;
    }


    private String getRegistroRTDP(DirfReg316 dr, PessoaInfo p, Dirf d, PessoaFisica pessoFisica, DirfVinculoFP dirfVinculoFP) {
        StringBuilder sb = new StringBuilder();
        BigDecimal soma = BigDecimal.ZERO;
        if (dr.equals(DirfReg316.RTDP)) {
            sb.append(dr.toString()).append("|");
            List<VinculoFP> vinculos;
            if (p != null) {
                vinculos = p.getVinculos();
            } else {
                vinculos = vinculoFPFacade.listaTodosVinculosPorPessoa(pessoFisica);
            }
            for (Mes mes : Mes.values()) {
                BigDecimal resultado = BigDecimal.ZERO;
                if (vinculos != null) {
                    for (VinculoFP vinculoFP : vinculos) {
                        if (hasFichaFinanceiraParaOMes(vinculoFP, d, mes, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro())) {
                            resultado = resultado.add(getValorRTDP((p != null ? p.getPessoa() : pessoFisica), mes, d));
                            soma = soma.add(resultado);
                        }
                    }
                }
                String valor = getValorFormatadoParaArquivo(resultado);
                sb.append(valor).append("|");
            }

            //Décimo Terceiro
            BigDecimal resultado = BigDecimal.ZERO;
            for (VinculoFP vinculoFP : vinculos) {
                if (hasFichaFinanceiraParaOMes(vinculoFP, d, Mes.DEZEMBRO, TipoFolhaDePagamento.getFolhasDePagamentoDecimoTerceiro())) {
                    resultado = resultado.add(getValorRTDP((p != null ? p.getPessoa() : pessoFisica), Mes.DEZEMBRO, d));
                    soma = soma.add(resultado);
                }
            }
            String valor = getValorFormatadoParaArquivo(resultado);

            if (dirfVinculoFP != null) {
                gravarValorDirf(dr, resultado, dirfVinculoFP, false);
            }

            sb.append(valor).append("|");
            sb.append("\r\n");
        }
        return soma.compareTo(BigDecimal.ZERO) == 0 ? "" : sb.toString();
    }

    private String getRegistroINFPA(PessoaInfo p, Dirf d, DirfVinculoFP dirfVinculoFP) {
        StringBuilder sb = new StringBuilder();
        List<BeneficioPensaoAlimenticia> filhos = pensaoAlimenticiaFacade.buscarBeneficiariosPensaoAlimenticiaPorAnoAndPessoa(p.getPessoa(), d.getExercicio());
        if (filhos == null || filhos.isEmpty()) {
            return "";
        }

        filhos = new ArrayList(new HashSet(filhos));
        filhos = ordenarPensoesAlimenticias(filhos);
        List<BeneficioPensaoAlimenticiaEventoFP> filhosEventos = agruparVerbasPorBeneficiario(filhos);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        LinkedHashMap<PessoaFisica, BeneficiarioPensaoAlimenticiaMesValor> beneficiarioMesValor = new LinkedHashMap<>();
        for (BeneficioPensaoAlimenticiaEventoFP filho : filhosEventos) {
            DirfPessoa dirfPessoa = new DirfPessoa();
            dirfPessoa.setDirfVinculoFP(dirfVinculoFP);
            dirfPessoa.setPessoaFisica(filho.getBeneficiario().getPessoaFisicaBeneficiario());
            dirfPessoa.setFontePagadora(d.getEntidade().getPessoaJuridica());
            dirfPessoa.setNaturezaDoRendimento(p.getVinculos().get(0).getNaturezaRendimento() != null ?
                p.getVinculos().get(0).getNaturezaRendimento().getDescricao() : "");
            if(!beneficiarioMesValor.containsKey(filho.getBeneficiario().getPessoaFisicaBeneficiario())) {
                BeneficiarioPensaoAlimenticiaMesValor novoBeneficiarioMesValor = new BeneficiarioPensaoAlimenticiaMesValor();
                novoBeneficiarioMesValor.setCpf(filho.getBeneficiario().getPessoaFisicaBeneficiario().getCpf_Cnpj().replaceAll("[^0-9]", "").trim());
                novoBeneficiarioMesValor.setDataNascimento(filho.getBeneficiario().getPessoaFisicaBeneficiario().getDataNascimento() != null ? format.format(filho.getBeneficiario().getPessoaFisicaBeneficiario().getDataNascimento()) : "");
                novoBeneficiarioMesValor.setNome(StringUtil.removeCaracteresEspeciais(filho.getBeneficiario().getPessoaFisicaBeneficiario().getNome()));
                beneficiarioMesValor.put(filho.getBeneficiario().getPessoaFisicaBeneficiario(), novoBeneficiarioMesValor);
            }
            if (filho.getBeneficiario().getGrauParentesco() != null) {
                beneficiarioMesValor.get(filho.getBeneficiario().getPessoaFisicaBeneficiario()).setGrauParentesco(filho.getBeneficiario().getGrauParentesco().getCodigo());
            }

            for (DirfReg316 dr : DirfReg316.getRegistrosINFPA()) {
                buscarValoresDePensaoAlimenticia(p, filho, d, sb, dr, dirfVinculoFP, beneficiarioMesValor);
            }
            //popular valores dirfpessoa
            for (DirfInfoComplementares dirfInfoComplementares : dirfVinculoFP.getInformacoesComplementares()) {
                if (dirfInfoComplementares.getPessoaFisica().equals(dirfPessoa.getPessoaFisica())) {
                    dirfPessoa.setTotalRendimentos(dirfInfoComplementares.getValor());
                    dirfPessoa.setDecimoTerceiroSalario(dirfInfoComplementares.getValorDecimo());
                }
            }
            dirfVinculoFP.getPessoas().add(dirfPessoa);
        }

        montarLinhasPensaoAlimenticia(beneficiarioMesValor, sb);
        return sb.toString();
    }

    private static void montarLinhasPensaoAlimenticia(LinkedHashMap<PessoaFisica, BeneficiarioPensaoAlimenticiaMesValor> beneficiarioMesValor, StringBuilder sb) {
        beneficiarioMesValor.values().forEach(beneficionMesValor -> {
            sb.append(DirfReg316.INFPA).append("|");
            sb.append(beneficionMesValor.getCpf()).append("|");
            sb.append(beneficionMesValor.getDataNascimento()).append("|");
            sb.append(beneficionMesValor.getNome()).append("|");
            sb.append(beneficionMesValor.getGrauParentesco() != null? beneficionMesValor.getGrauParentesco() : "");

            sb.append("|");
            sb.append("\r\n");
            if (!beneficionMesValor.getValoresMesRTPA().isEmpty()) {
                sb.append(DirfReg316.RTPA).append("|");
                for(Mes mes : Mes.values()) {
                    String valorMesRTPA = beneficionMesValor.getValoresMesRTPA().get(mes);
                    sb.append(valorMesRTPA != null ? valorMesRTPA : "").append("|");
                }
                sb.append(beneficionMesValor.getDecimoTerceiroRTPA() != null ? beneficionMesValor.getDecimoTerceiroRTPA() : "").append("|");
                sb.append("\r\n");
            }
            if (!beneficionMesValor.getValoresMesESPA().isEmpty()) {
                sb.append(DirfReg316.ESPA).append("|");
                for(Mes mes : Mes.values()) {
                    String valorMesESPA = beneficionMesValor.getValoresMesESPA().get(mes);
                    sb.append(valorMesESPA != null ? valorMesESPA : "").append("|");
                }
                sb.append(beneficionMesValor.getDecimoTerceiroESPA()).append("|");
                sb.append("\r\n");
            }
        });
    }

    private List<BeneficioPensaoAlimenticiaEventoFP> agruparVerbasPorBeneficiario(List<BeneficioPensaoAlimenticia> filhos) {
        List<BeneficioPensaoAlimenticiaEventoFP> filhosEvento = Lists.newLinkedList();
        for (BeneficioPensaoAlimenticia filho : filhos) {
            BeneficioPensaoAlimenticiaEventoFP beneficiario = buscarBeneficiario(filhosEvento, filho);
            if (beneficiario == null) {
                beneficiario = new BeneficioPensaoAlimenticiaEventoFP(filho, Lists.newArrayList(filho.getEventoFP()));
            } else {
                beneficiario.adicionarEvento(filho.getEventoFP());
            }
            if (!filhosEvento.contains(beneficiario)) {
                filhosEvento.add(beneficiario);
            }
        }

        return filhosEvento;
    }

    private BeneficioPensaoAlimenticiaEventoFP buscarBeneficiario(List<BeneficioPensaoAlimenticiaEventoFP> filhosEvento, BeneficioPensaoAlimenticia filho) {
        for (BeneficioPensaoAlimenticiaEventoFP beneficioPensaoAlimenticiaEventoFP : filhosEvento) {
            if (beneficioPensaoAlimenticiaEventoFP.getBeneficiario().equals(filho.getPessoaFisicaBeneficiario()))
                return beneficioPensaoAlimenticiaEventoFP;
        }
        return null;
    }

    private String getTipoDependencia(PessoaFisica filho) {
        List<Dependente> dependentes = dependenteFacade.buscarDependentesPorDependente(filho);
        for (Dependente dependente : dependentes) {
            if (dependente.getGrauDeParentesco() != null) {
                return realizarDeParaCodigoDirf(dependente.getGrauDeParentesco());
            }
        }
        return "";
    }

    private String realizarDeParaCodigoDirf(GrauDeParentesco grauDeParentesco) {
        if (grauDeParentesco.getCodigoRais() == null) {
            return "";
        }
        switch (grauDeParentesco.getCodigoRais()) {
            case 0:
                return "";
            case 1:
                return "03";
            case 2:
                return "04";
            case 3:
                return "03";
            case 4:
                return "08";
            case 5:
                return "08";
            case 6:
                return "10";
            case 7:
                return "10";
            case 8:
                return "10";
            case 9:
                return "10";
            case 10:
                return "06";
            default:
                return "";
        }
    }

//    private String getRegistroRIIRP(DirfReg316 dr, PessoaDirf p, Dirf d) {
//        StringBuilder sb = new StringBuilder();
//        if (dr.equals(DirfReg316.RIIRP)) {
//            sb.append(dr.toString()).append("|");
//            for (Mes mes : Mes.values()) {
//                BigDecimal resultado = getSomaDosEventosMensal(p.getPessoaFisica(), p.getVinculoFP(), mes, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro(), null);
//                String valor = getValorFormatadoParaArquivo(resultado);
//                if (valor.replace("0", "").trim().length() == 0) {
//                    valor = "";
//                }
//                sb.append(valor).append("|");
//            }
//
//            //Décimo Terceiro
//            BigDecimal resultado = getSomaDosEventosAnual(p.getPessoaFisica(), p.getVinculoFP(), d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoRescisaoDecimoTerceiro(), true, null);
//            String valor = getValorFormatadoParaArquivo(resultado);
//            if (valor.replace("0", "").trim().length() == 0) {
//                valor = "";
//            }
//            sb.append(valor).append("|");
//
//            sb.append("\r\n");
//        }
//        return sb.toString();
//    }

    /**
     * \\\\\\
     * 3.6 - Registro de valores mensais. Ver identificadores no ENUM DirfReg316
     *
     * @return linha referênte ao registro 3.16
     */
    private String processarRegistro316(PessoaInfo p, Dirf d, LoteDirf loteDirf, DirfVinculoFP dirfVinculoFP) {
        StringBuilder sb = new StringBuilder();

        List<DirfReg316> registrosBPFDEC = DirfReg316.getRegistrosBPFDEC();

        sb.append(getRegistroINFPA(p, d, dirfVinculoFP));

        if (loteDirf.getDirfReg35() == DirfReg35.APOSENTADO_PENSIONIATA) {
            registrosBPFDEC.remove(DirfReg316.RIDAC);
        }
        for (DirfReg316 dr : registrosBPFDEC) {
            sb.append(getRegistroRTDP(dr, p, d, null, dirfVinculoFP));
            buscarValoresDe(p, d, sb, dr, loteDirf, dirfVinculoFP);
        }
        return sb.toString();
    }

    private void buscarValoresDe(PessoaInfo p, Dirf d, StringBuilder arquivo, DirfReg316 dr, LoteDirf loteDirf, DirfVinculoFP dirfVinculoFP) {
        BigDecimal valorTag = BigDecimal.ZERO;
        StringBuilder linha = new StringBuilder();
        BigDecimal valorDia = buscarValoresPagosEmDiaria(p.getPessoa().getId(), null, d.getExercicio());

        boolean temExoneracao = false;

        if (Integer.parseInt(DataUtil.getIdade(p.getPessoa().getDataNascimento())) < SESSENTAECINCOANOS && DirfReg316.RIP65.equals(dr)) {
            return;
        }
        if (!DirfReg35.APOSENTADO_PENSIONIATA.equals(loteDirf.getDirfReg35()) && DirfReg316.RIP65.equals(dr)) {
            return;
        }

        for (VinculoFP vinculoFP : p.getVinculos()) {
            if (DirfReg316.RIIRP.equals(dr) && exoneracaoRescisaoFacade.existeExoneracaoRescisaoPorVinculoFP(vinculoFP)) {
                temExoneracao = true;
            }
        }
        if (DirfReg316.RIIRP.equals(dr) && !temExoneracao) {
            return;
        }

        boolean mudarTag = true;

        if ((existeLancamentoParaRegistroNoAno(p.getPessoa(), p.getVinculos(), d.getExercicio().getAno(), dr)) || ((valorDia.compareTo(BigDecimal.ZERO) > 0) && DirfReg316.RIDAC.equals(dr)) || DirfReg316.RIP65.equals(dr)) {
            linha = new StringBuilder();
            InvalidezAposentado invalidez = buscarInvalidezAposentado(p.getVinculos().get(0), d);
            LocalDate dataInicialMolestiaGrave = null;
            if (invalidez != null) {
                dataInicialMolestiaGrave = LocalDate.fromDateFields(invalidez.getInicioVigencia());
            }
            if (DirfReg35.APOSENTADO_PENSIONIATA.equals(loteDirf.getDirfReg35()) &&
                invalidez != null &&
                DirfReg316.RTRT.equals(dr) &&
                (dataInicialMolestiaGrave.getMonthOfYear() <= Mes.JANEIRO.getNumeroMes() && dataInicialMolestiaGrave.getYear() < d.getExercicio().getAno())) {
                linha.append(DirfReg316.RIMOG).append("|");
                mudarTag = false;
            } else {
                linha.append(dr.toString()).append("|");
            }

            BigDecimal valorRio = BigDecimal.ZERO;

            for (Mes mes : Mes.values()) {
                Boolean existeInsencaoDeImpostoRenda = existeInsencaoDeImpostoRenda(p.getVinculos(), d, mes);
                if ((dataInicialMolestiaGrave != null && !hasServidorEmMolestiaGrave(p.getVinculos(), d, mes.getNumeroMes()) &&
                    DirfReg316.RTRT.equals(dr) && mudarTag) && !existeInsencaoDeImpostoRenda) {
                    int mesCorrente = mes.getNumeroMes();
                    while (mesCorrente <= 13) {
                        linha.append("|");
                        mesCorrente++;
                    }
                    linha.append("\r\n");
                    linha.append(DirfReg316.RIMOG).append("|");
                    int mesInicial = 1;
                    while (mesInicial < mes.getNumeroMes()) {
                        linha.append("|");
                        mesInicial++;
                    }
                    mudarTag = false;
                }

                BigDecimal resultado = BigDecimal.ZERO;
                for (VinculoFP vinculoFP : p.getVinculos()) {
                    dirfVinculoFP.setVinculoFP(vinculoFP);
                    if (podeBuscarValor(vinculoFP, mes, d)) {
                        if (DirfReg35.APOSENTADO_PENSIONIATA.equals(loteDirf.getDirfReg35()) && Integer.parseInt(DataUtil.getIdade(p.getPessoa().getDataNascimento())) >= SESSENTAECINCOANOS && DirfReg316.RTRT.equals(dr) && (hasServidorEmMolestiaGrave(Lists.newArrayList(vinculoFP), d, mes.getNumeroMes()) || existeInsencaoDeImpostoRenda)) {
                            BigDecimal valorBruto = resultado.add(getSomaDosEventosMensal(p.getPessoa(), vinculoFP, mes, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro(), null, false));
                            BigDecimal valorDeducao = resultado.add(fichaFinanceiraFPFacade.buscarValorDeducaoAposentadoAndPensionista65Anos());
                            if (valorBruto.compareTo(valorDeducao) > 0) {
                                resultado = valorBruto.subtract(valorDeducao);
                            }
                        } else if (!DirfReg316.RIP65.equals(dr)) {
                            resultado = resultado.add(getSomaDosEventosMensal(p.getPessoa(), vinculoFP, mes, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro(), null, hasServidorEmMolestiaGrave(Lists.newArrayList(vinculoFP), d, mes.getNumeroMes())));
                        }
                        if (DirfReg35.APOSENTADO_PENSIONIATA.equals(loteDirf.getDirfReg35()) && DirfReg316.RIP65.equals(dr) && Integer.parseInt(DataUtil.getIdade(p.getPessoa().getDataNascimento())) >= SESSENTAECINCOANOS && (hasServidorEmMolestiaGrave(Lists.newArrayList(vinculoFP), d, mes.getNumeroMes()) || existeInsencaoDeImpostoRenda)) {
                            BigDecimal valorFichaFinanceira = resultado.add(getSomaDosEventosMensal(p.getPessoa(), vinculoFP, mes, d.getExercicio().getAno(), DirfReg316.RTRT, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro(), null, hasServidorEmMolestiaGrave(Lists.newArrayList(vinculoFP), d, mes.getNumeroMes())));
                            BigDecimal valorDeducao = resultado.add(fichaFinanceiraFPFacade.buscarValorDeducaoAposentadoAndPensionista65Anos());
                            if (valorDeducao.compareTo(valorFichaFinanceira) > 0) {
                                resultado = valorFichaFinanceira;
                            } else {
                                resultado = valorDeducao;
                            }
                        }
                    }
                }
                if (DirfReg316.RIDAC.equals(dr)) {
                    resultado = resultado.add(buscarValoresPagosEmDiaria(p.getPessoa().getId(), mes, d.getExercicio()));
                }

                if (DirfReg316.RTPO.equals(dr) || DirfReg316.RTIRF.equals(dr)) {
                    resultado = resultado.abs();
                }

                if (resultado.compareTo(BigDecimal.ZERO) < 0) {
                    resultado = BigDecimal.ZERO;
                }

                gravarValorDirf(dr, resultado, dirfVinculoFP, false);

                valorTag = valorTag.add(resultado);

                if (!DirfReg316.RIO.equals(dr)) {


                    String valor = getValorFormatadoParaArquivo(resultado);
                    if (valor.replace("0", "").trim().length() == 0) {
                        valor = "";
                    }
                    linha.append(valor).append("|");
                } else {
                    valorRio = valorRio.add(resultado);
                }
            }


            // Décimo Terceiro
            BigDecimal resultado = BigDecimal.ZERO;
            //    segundo o chamado #1623 , os valores de previdência de folha rescisória não devem ser emitidos na dirf.
            for (VinculoFP vinculoFP : p.getVinculos()) {
                logger.info("preencher 13º vinculo ...... " + vinculoFP.getId());
                if (Integer.parseInt(DataUtil.getIdade(p.getPessoa().getDataNascimento())) >= SESSENTAECINCOANOS && DirfReg316.RTRT.equals(dr) && buscarInvalidezAposentado(vinculoFP, d) == null) {
                    BigDecimal valorBruto = resultado.add(getSomaDosEventosMensal(p.getPessoa(), vinculoFP, Mes.DEZEMBRO, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro(), null, false));
                    BigDecimal valorDeducao = resultado.add(fichaFinanceiraFPFacade.buscarValorDeducaoAposentadoAndPensionista65Anos());
                    if (valorBruto.compareTo(valorDeducao) > 0) {
                        resultado = valorBruto.subtract(valorDeducao);
                        logger.info("13º BRUTO - DEDUCAO " + resultado);
                    }

                } else if (!DirfReg316.RIP65.equals(dr)) {
                    resultado = resultado.add(getSomaDosEventosAnual(p.getPessoa(), vinculoFP, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoRescisaoDecimoTerceiro(), true, null, hasDeducaoValor(dr, vinculoFP, d, null)));
                    logger.info("valor dirf 13º " + resultado);
                    if (DirfReg316.RTRT.equals(dr)) {
                        gravarValorDirf(dr, resultado, dirfVinculoFP, true);
                    }
                }

                if (DirfReg35.APOSENTADO_PENSIONIATA.equals(loteDirf.getDirfReg35()) && DirfReg316.RIP65.equals(dr) && Integer.parseInt(DataUtil.getIdade(p.getPessoa().getDataNascimento())) >= SESSENTAECINCOANOS && hasServidorEmMolestiaGrave(Lists.newArrayList(vinculoFP), d, Mes.DEZEMBRO.getNumeroMes())) {
                    BigDecimal valorFichaFinanceira = resultado.add(getSomaDosEventosMensal(p.getPessoa(), vinculoFP, Mes.DEZEMBRO, d.getExercicio().getAno(), DirfReg316.RTRT, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro(), null, hasServidorEmMolestiaGrave(Lists.newArrayList(vinculoFP), d, Mes.DEZEMBRO.getNumeroMes())));
                    BigDecimal valorDeducao = resultado.add(fichaFinanceiraFPFacade.buscarValorDeducaoAposentadoAndPensionista65Anos());
                    if (valorDeducao.compareTo(valorFichaFinanceira) > 0) {
                        resultado = valorFichaFinanceira;
                        logger.info("valor dirf 13º com deducao" + resultado);
                    } else {
                        resultado = valorDeducao;
                        logger.info("valor dirf 13º valor deducao" + resultado);
                    }
                }

                if (resultado.compareTo(BigDecimal.ZERO) == 0) {
                    if (DirfReg316.RTRT.equals(dr)) {
                        resultado = resultado.add(getSomaDosEventosAnual(p.getPessoa(), vinculoFP, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoDecimoTerceiro(), true, null, hasDeducaoValor(dr, vinculoFP, d, null)));
                        logger.info("valor 13º zero getSomaDosEventosAnual " + resultado);
                        gravarValorDirf(dr, resultado, dirfVinculoFP, true);
                    }
                } else {
                    BigDecimal impostoRetidoFonteDecimo = getSomaDosEventosAnual(p.getPessoa(), vinculoFP, d.getExercicio().getAno(), DirfReg316.RTIRF, TipoFolhaDePagamento.getFolhasDePagamentoDecimoTerceiro(), true, null, hasDeducaoValor(dr, vinculoFP, d, null));
                    if (impostoRetidoFonteDecimo.compareTo(BigDecimal.ZERO) > 0) {
                        dirfVinculoFP.setImpostoSobreARendaRetidoNo13(impostoRetidoFonteDecimo.abs());
                    }
                }
            }

            if (!DirfReg316.RIO.equals(dr)) {

                String valor = getValorFormatadoParaArquivo(resultado);
                logger.info("valor arquivo " + valor);
                if (valor.replace("0", "").trim().length() == 0) {
                    valor = "";
                }

                gravarValorDirf(dr, resultado, dirfVinculoFP, DirfReg316.RTRT.equals(dr));

                linha.append(valor).append("|");
            } else{
                valorRio = valorRio.add(resultado);
                String valor = getValorFormatadoParaArquivo(valorRio);
                if (valor.replace("0", "").trim().length() == 0) {
                    valor = "";
                }

                linha.append(valor).append("|");

                linha.append(getDescricaoDosEventosAnual(p.getVinculos(), d.getExercicio().getAno() ,dr)).append("|");

            }

            linha.append("\r\n");

            if (DirfReg316.RIIRP.equals(dr) && valorTag.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            if (DirfReg316.RTRT.equals(dr) && valorTag.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            arquivo.append(linha);
            buscarValorParaInformeDeRendimentos(dirfVinculoFP, p, d.getExercicio().getAno(), d);
        }

    }

    private void buscarValorParaInformeDeRendimentos(DirfVinculoFP dirfVinculoFP, PessoaInfo p, Integer ano, Dirf dirf) {
        Set<VinculoFP> vinculos = new HashSet<>(p.getVinculos());
        for (VinculoFP vinculo : vinculos) {
            BigDecimal totalRendimentos = getTotalRendimentosInforme(p.getPessoa(), vinculos, ano);
            logger.info("CAMPO 3.1 - Total dos rendimentos (inclusive férias) ");
            logger.info("Total dos Rendimentos: " + totalRendimentos);
            dirfVinculoFP.setTotalRendimentos(totalRendimentos.abs());

            logger.info("------------------------------------------------------------ ");

            BigDecimal contribuicaoPrevidenciaria = getTotaDeducoesPorCodigoGrupoExportacaoInforme(p.getPessoa(), DirfReg316.RTPO, vinculos, ano);
            logger.info("CAMPO 3.2 - Contribuição previdenciária oficial ");
            logger.info("Total da Contribuição previdenciária: " + contribuicaoPrevidenciaria);
            dirfVinculoFP.setContribuicaoPrevidenciaria(contribuicaoPrevidenciaria.abs());

            logger.info("------------------------------------------------------------ ");

            BigDecimal impostoRendaRetidoFonte = getTotaDeducoesPorCodigoGrupoExportacaoInforme(p.getPessoa(), DirfReg316.RTIRF, vinculos, ano);
            logger.info("CAMPO 3.5 - Imposto sobre a renda retido na fonte ");
            logger.info("Total do Imposto sobre a renda retido na fonte: " + impostoRendaRetidoFonte);
            dirfVinculoFP.setImpostoDeRendaRetido(impostoRendaRetidoFonte.abs());

            logger.info("Valores do 13º ");
            BigDecimal valor13 = BigDecimal.ZERO;
            BigDecimal valorINSS13 = BigDecimal.ZERO;
            BigDecimal valorIRRF13 = BigDecimal.ZERO;
            BigDecimal valorPensaoAlimenticia = BigDecimal.ZERO;
            BigDecimal deducaoDependentes13 = BigDecimal.ZERO;
            BigDecimal deducaoAposentadoPensionista13 = BigDecimal.ZERO;
            valor13 = valor13.add(getSomaDosEventosAnualInformeRendimentos(p.getPessoa(), vinculos, ano, DirfReg316.RTRT, TipoFolhaDePagamento.getFolhasDePagamentoRescisaoDecimoTerceiro(), true, null, true));
            valorINSS13 = valorINSS13.add(getSomaDosEventosAnualInformeRendimentos(p.getPessoa(), vinculos, ano, DirfReg316.RTPO, TipoFolhaDePagamento.getFolhasDePagamentoRescisaoDecimoTerceiro(), true, null, true));
            valorIRRF13 = valorIRRF13.add(getSomaDosEventosAnualInformeRendimentos(p.getPessoa(), vinculos, ano, DirfReg316.RTIRF, TipoFolhaDePagamento.getFolhasDePagamentoRescisaoDecimoTerceiro(), true, null, true));
            valorPensaoAlimenticia = valorPensaoAlimenticia.add(getSomaDosEventosAnualInformeRendimentos(p.getPessoa(), vinculos, ano, DirfReg316.RTPA, TipoFolhaDePagamento.getFolhasDePagamentoRescisaoDecimoTerceiro(), true, null, true));
            deducaoDependentes13 = getValorRTDP(p.getPessoa(), Mes.DEZEMBRO, dirf);


            if (isAposentadoOuPensionista(p)) {
                if (Integer.parseInt(DataUtil.getIdade(p.getPessoa().getDataNascimento())) >= SESSENTAECINCOANOS) {
                    deducaoAposentadoPensionista13 = fichaFinanceiraFPFacade.buscarValorDeducaoAposentadoAndPensionista65Anos();
                }
            }

            BigDecimal decimo = valor13.add(valorINSS13).add(valorIRRF13).subtract(deducaoDependentes13).add(valorPensaoAlimenticia).subtract(deducaoAposentadoPensionista13);

            if (decimo.compareTo(BigDecimal.ZERO) < 0) {
                dirfVinculoFP.setDecimoTerceiroSalario(BigDecimal.ZERO);
            } else {
                dirfVinculoFP.setDecimoTerceiroSalario(decimo);
            }
            dirfVinculoFP.setImpostoSobreARendaRetidoNo13(valorIRRF13.abs());

        }
    }


    private void gravarValorDirf(DirfReg316 dr, BigDecimal resultado, DirfVinculoFP dirfVinculoFP, Boolean isDecimoTerceiro) {
        if (DirfReg316.RIP65.equals(dr)) {
            dirfVinculoFP.setParcelaIsenta(dirfVinculoFP.getParcelaIsenta().add(resultado.abs()));
        }
        if (DirfReg316.RIIRP.equals(dr)) {
            dirfVinculoFP.setIndenizacoesPorRescisao(dirfVinculoFP.getIndenizacoesPorRescisao().add(resultado.abs()));
        }
        if (DirfReg316.RIDAC.equals(dr)) {
            dirfVinculoFP.setDiariasAjudasDeCusto(dirfVinculoFP.getDiariasAjudasDeCusto().add(resultado.abs()));
        }
        if (DirfReg316.RIMOG.equals(dr)) {
            dirfVinculoFP.setPensaoProventosPorAcidente(dirfVinculoFP.getPensaoProventosPorAcidente().add(resultado.abs()));
        }

    }

    private boolean existeInsencaoDeImpostoRenda(List<VinculoFP> vinculoFP, Dirf dirf, Mes mes) {
        String sql = " select evento.* from fichafinanceirafp ficha " +
            " inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.id " +
            " inner join eventofp evento on item.EVENTOFP_ID = evento.id " +
            " where VINCULOFP_ID in :vinculo and item.ANO = :ano and item.MES = :mes and codigo = '901'";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculo", getIdsVinculos(vinculoFP));
        q.setParameter("ano", dirf.getExercicio().getAno());
        q.setParameter("mes", mes.getNumeroMes());
        return !q.getResultList().isEmpty();
    }

    private boolean hasDeducaoValor(DirfReg316 dr, VinculoFP vinculoFP, Dirf d, Mes mes) {
        if ((DirfReg316.RTRT.equals(dr) || DirfReg316.RIMOG.equals(dr)) && buscarInvalidezAposentadoNoMes(vinculoFP, d, mes) != null) {
            return false;
        }
        return true;

    }

    private boolean podeBuscarValor(VinculoFP vinculoFP, Mes mes, Dirf d) {
        for (RecursoFP recursoFP : fichaFinanceiraFPFacade.buscarRecursosFPPorServidor(vinculoFP, mes, d.getExercicio())) {
            if (d.getRecursos().contains(recursoFP)) {
                return true;
            }
        }
        return false;
    }

    private void buscarValoresDePensaoAlimenticia(PessoaInfo p, BeneficioPensaoAlimenticiaEventoFP filho, Dirf d, StringBuilder sb, DirfReg316 dr, DirfVinculoFP dirfVinculoFP, HashMap<PessoaFisica, BeneficiarioPensaoAlimenticiaMesValor> beneficiarioMesValor) {
        DirfInfoComplementares dirfInfoComplementares = new DirfInfoComplementares();
        if (existeLancamentoParaRegistroNoAno(p.getPessoa(), p.getVinculos(), d.getExercicio().getAno(), dr)) {
            for (Mes mes : Mes.values()) {
                BigDecimal resultado = BigDecimal.ZERO;
                for (VinculoFP vinculoFP : p.getVinculos()) {
                    for (EventoFP eventoFP : filho.getEventos()) {
                        resultado = resultado.add(getSomaDosEventosMensal(p.getPessoa(), vinculoFP, mes, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro(), eventoFP, false));
                    }
                }

                String valor = getValorFormatadoParaArquivo(resultado);

                dirfVinculoFP.setPensaoAlimenticia(dirfVinculoFP.getPensaoAlimenticia().add(resultado.abs()));

                dirfInfoComplementares.setPessoaFisica(filho.getBeneficiario().getPessoaFisicaBeneficiario());
                dirfInfoComplementares.setValor(dirfInfoComplementares.getValor().add(resultado.abs()));
                dirfInfoComplementares.setDirfVinculoFP(dirfVinculoFP);
                dirfVinculoFP.getInformacoesComplementares().add(dirfInfoComplementares);
                somarValorMes(filho, dr, beneficiarioMesValor, mes, valor);
            }

            // Décimo Terceiro
            BigDecimal resultado = BigDecimal.ZERO;
            for (VinculoFP vinculoFP : p.getVinculos()) {
                for (EventoFP eventoFP : filho.getEventos()) {
                    resultado = resultado.add(getSomaDosEventosAnual(p.getPessoa(), vinculoFP, d.getExercicio().getAno(), dr, TipoFolhaDePagamento.getFolhasDePagamentoRescisaoDecimoTerceiro(), false, eventoFP, hasDeducaoValor(dr, vinculoFP, d, null)));
                }
            }

            dirfVinculoFP.setDecimoTerceiroSalarioPensao(dirfVinculoFP.getDecimoTerceiroSalarioPensao().add(resultado.abs()));
            dirfInfoComplementares.setValorDecimo(resultado.abs());
            String valor = getValorFormatadoParaArquivo(resultado);

            somarValorDecimoTerceiro(filho, dr, beneficiarioMesValor, valor);
        }
    }

    private static void somarValorMes(BeneficioPensaoAlimenticiaEventoFP filho, DirfReg316 dr, HashMap<PessoaFisica, BeneficiarioPensaoAlimenticiaMesValor> beneficiarioMesValor, Mes mes, String valor) {
        if (!valor.replace("0", "").trim().isEmpty()) {
            BeneficiarioPensaoAlimenticiaMesValor beneficiarioPensaoAlimenticiaMesValor = beneficiarioMesValor.get(filho.getBeneficiario().getPessoaFisicaBeneficiario());
            if (DirfReg316.RTPA.equals(dr)) {
                String valorAnterior = beneficiarioPensaoAlimenticiaMesValor.getValoresMesRTPA().get(mes);
                int soma = valorAnterior != null ? Integer.parseInt(valor) + Integer.parseInt(valorAnterior) : Integer.parseInt(valor);
                beneficiarioPensaoAlimenticiaMesValor.getValoresMesRTPA().put(mes, Integer.toString(soma));
            }
            if (DirfReg316.ESPA.equals(dr)) {
                String valorAnterior = beneficiarioPensaoAlimenticiaMesValor.getValoresMesESPA().put(mes, valor);
                int soma = valorAnterior != null ? Integer.parseInt(valor) + Integer.parseInt(valorAnterior) : Integer.parseInt(valor);
                beneficiarioPensaoAlimenticiaMesValor.getValoresMesESPA().put(mes, Integer.toString(soma));
            }
        }
    }

    private static void somarValorDecimoTerceiro(BeneficioPensaoAlimenticiaEventoFP filho, DirfReg316 dr, HashMap<PessoaFisica, BeneficiarioPensaoAlimenticiaMesValor> beneficiarioMesValor, String valor) {
        if (!valor.replace("0", "").trim().isEmpty()) {
            BeneficiarioPensaoAlimenticiaMesValor beneficiarioPensaoAlimenticiaMesValor = beneficiarioMesValor.get(filho.getBeneficiario().getPessoaFisicaBeneficiario());
            if (DirfReg316.RTPA.equals(dr)) {
                String valorAnterior = beneficiarioPensaoAlimenticiaMesValor.getDecimoTerceiroRTPA();
                int soma = valorAnterior != null ? Integer.parseInt(valor) + Integer.parseInt(valorAnterior) : Integer.parseInt(valor);
                beneficiarioPensaoAlimenticiaMesValor.setDecimoTerceiroRTPA(Integer.toString(soma));
            }
            if (DirfReg316.ESPA.equals(dr)) {
                String valorAnterior = beneficiarioPensaoAlimenticiaMesValor.getDecimoTerceiroESPA();
                int soma = valorAnterior != null ? Integer.parseInt(valor) + Integer.parseInt(valorAnterior) : Integer.parseInt(valor);
                beneficiarioPensaoAlimenticiaMesValor.setDecimoTerceiroESPA(Integer.toString(soma));
            }
        }
    }

    /**
     * 3.6 - Registro de valores mensais. Ver identificadores no ENUM DirfReg316
     *
     * @return linha referênte ao registro 3.16 contábil
     */
    private String processarRegistro316Contabil(Pessoa p, Dirf d, List<Long> subordinadas, String codigo) {
        StringBuilder sb = new StringBuilder();
        boolean isPessoaFisica = p instanceof PessoaFisica;
        for (DirfReg316 dr : DirfReg316.values()) {
            if (isPessoaFisica) {
                sb.append(getRegistroRTDP(dr, null, d, (PessoaFisica) p, null));
            }
            GrupoExportacao grupoClasseCredor = isPessoaFisica
                ? recuperarGruposExportacaoComClasseCredorPorModuloENomeReduzido(dr, codigo)
                : buscarGrupoExportacaoComClasseCredorPorModuloNomeReduzidoECodigoPj(dr, codigo);

            if (grupoClasseCredor != null) {
                grupoClasseCredor = grupoExportacaoFacade.recuperar(grupoClasseCredor.getId());
                if (existePagamentoParaRegistroNoAno(p, d.getExercicio().getAno(), dr, subordinadas, codigo, isPessoaFisica)) {
                    if (grupoClasseCredor.getTipoGrupoExportacao().equals(TipoGrupoExportacao.MENSAL)) {
                        HashMap valorPorMes = atualizarValoresDePagamentoNoMes(p, d, subordinadas, grupoClasseCredor, dr);
                        sb.append(dr.toString()).append("|");
                        sb.append(adicionarZerosNosValoresPorMes(valorPorMes));
                        sb.append(adicionar13Zerado());
                    } else {
                        BigDecimal valorTotal = BigDecimal.ZERO;
                        for (ItemGrupoExportacaoContabil item : grupoClasseCredor.getItensGrupoExportacaoContabil()) {
                            BigDecimal valor = buscarTotalPagamentoParaRegistroNoAno(p, d.getExercicio().getAno(), dr, subordinadas, item.getClasseCredor(), codigo, isPessoaFisica);
                            if (valor != null && valor.compareTo(BigDecimal.ZERO) != 0) {
                                BigDecimal resultado = valor.multiply(item.getPercentual()).divide(new BigDecimal(100));
                                valorTotal = valorTotal.add(resultado);
                            }
                        }
                        sb.append(dr.toString()).append("|");

                        String valor = getValorFormatadoParaArquivo(valorTotal);
                        if (valor.replace("0", "").trim().length() == 0) {
                            valor = "";
                        }
                        sb.append(valor).append("|");
                        sb.append("Outros").append("|");
                        sb.append("\r\n");
                    }
                }
            }
            GrupoExportacao grupoContaExtra = recuperarGruposExportacaoComContaExtraPorModuloENomeReduzido(dr);
            if (grupoContaExtra != null) {
                grupoContaExtra = grupoExportacaoFacade.recuperar(grupoContaExtra.getId());
                if (existeReceitaExtraParaRegistroNoAno(p, d.getExercicio().getAno(), dr, subordinadas)) {
                    HashMap valorPorMes = atualizarValoresDeReceitaExtraNoMes(p, d, subordinadas, grupoContaExtra, dr);
                    sb.append(dr.toString()).append("|");
                    sb.append(adicionarZerosNosValoresPorMes(valorPorMes));
                    sb.append(adicionar13Zerado());
                }
            }
        }
        return sb.toString();
    }

    private HashMap atualizarValoresDePagamentoNoMes(Pessoa p, Dirf d, List<Long> subordinadas, GrupoExportacao grupo, DirfReg316 dr) {
        HashMap valorPorMes = new HashMap();
        for (ItemGrupoExportacaoContabil item : grupo.getItensGrupoExportacaoContabil()) {
            for (Mes mes : Mes.values()) {
                BigDecimal resultado = (getSomaDosPagamentosMensal(p, mes, d.getExercicio().getAno(), dr, subordinadas, item.getClasseCredor()).multiply(item.getPercentual())).divide(new BigDecimal(100));
                if (valorPorMes.get(mes.getNumeroMes()) != null) {
                    valorPorMes.put(mes.getNumeroMes(), ((BigDecimal) valorPorMes.get(mes.getNumeroMes())).add(resultado));
                } else {
                    valorPorMes.put(mes.getNumeroMes(), resultado);
                }
            }
        }
        return valorPorMes;
    }

    private HashMap atualizarValoresDeReceitaExtraNoMes(Pessoa p, Dirf d, List<Long> subordinadas, GrupoExportacao grupo, DirfReg316 dr) {
        HashMap valorPorMes = new HashMap();
        for (ItemGrupoExportacaoContabil item : grupo.getItensGrupoExportacaoContabil()) {
            for (Mes mes : Mes.values()) {
                BigDecimal resultado = (getSomaDasReceitasExtrasMensal(p, mes, d.getExercicio().getAno(), dr, subordinadas, item.getContaExtraorcamentaria()).multiply(item.getPercentual())).divide(new BigDecimal(100));
                if (valorPorMes.get(mes.getNumeroMes()) != null) {
                    valorPorMes.put(mes.getNumeroMes(), ((BigDecimal) valorPorMes.get(mes.getNumeroMes())).add(resultado));
                } else {
                    valorPorMes.put(mes.getNumeroMes(), resultado);
                }
            }
        }
        return valorPorMes;
    }

    private StringBuilder adicionarZerosNosValoresPorMes(HashMap valorPorMes) {
        StringBuilder sb = new StringBuilder();
        for (Mes mes : Mes.values()) {
            BigDecimal resultado = (BigDecimal) valorPorMes.get(mes.getNumeroMes());
            String valor = getValorFormatadoParaArquivo(resultado);
            if (valor.replace("0", "").trim().length() == 0) {
                valor = "";
            }
            sb.append(valor).append("|");
        }
        return sb;
    }

    private StringBuilder adicionar13Zerado() {
        StringBuilder sb = new StringBuilder();
        String valor = getValorFormatadoParaArquivo(BigDecimal.ZERO);
        if (valor.replace("0", "").trim().length() == 0) {
            valor = "";
        }
        sb.append(valor).append("|");

        sb.append("\r\n");
        return sb;
    }

    /**
     * 3.28 - Registro identificador do término da declaração.
     *
     * @return linha referênte ao registro 3.28
     */
    private String processarRegistro328() {
        StringBuilder sb = new StringBuilder();

        sb.append("FIMDIRF").append("|");

        return sb.toString();
    }

    /**
     * @param dia referente ao dia
     * @param mes referente ao mês iniciado em 0 (Janeiro = 0)
     * @param ano referente ao ano
     * @return data conforme parâmetros
     */
    public Date montarData(Integer dia, Integer mes, Integer ano) {
        Calendar dataRetorno = Calendar.getInstance();
        dataRetorno.set(Calendar.DAY_OF_MONTH, dia);
        dataRetorno.set(Calendar.MONTH, mes);
        dataRetorno.set(Calendar.YEAR, ano);
        dataRetorno.setTime(Util.getDataHoraMinutoSegundoZerado(dataRetorno.getTime()));
        return dataRetorno.getTime();
    }

    private BigDecimal getValorRTDP(Pessoa p, Mes mes, Dirf d) {
        Date dataReferencia = montarData(1, mes.getNumeroMesIniciandoEmZero(), d.getExercicio().getAno());
        Integer numeroDependentes = dependenteFacade.getNumeroDeDependentesDaDirf(p, dataReferencia);
        BigDecimal valorBaseFP = referenciaFPFacade.getValorReferenciaFPPorCodigoVigente("3", dataReferencia);

        BigDecimal dependentes = new BigDecimal(numeroDependentes);

        BigDecimal retorno = valorBaseFP.multiply(dependentes);

        return retorno;
    }

    public BigDecimal buscarValoresPagosEmDiaria(Long pessoaId, Mes mes, Exercicio ano) {

        String query = " select coalesce(sum(coalesce(pag.valor,0)),0) from PROPOSTACONCESSAODIARIA diaria " +
            "               inner join empenho emp on diaria.id = emp.PROPOSTACONCESSAODIARIA_ID " +
            "               inner join pessoa p on emp.FORNECEDOR_ID = p.id " +
            "               inner join liquidacao liq on emp.id = liq.EMPENHO_ID " +
            "               inner join pagamento pag on liq.id = pag.LIQUIDACAO_ID " +
            "               inner join exercicio ex on pag.EXERCICIO_ID = ex.id" +
            " where extract(year from pag.dataPagamento) = :ano ";
        if (mes != null) {
            query += "       and extract(month from pag.dataPagamento) = :mes ";
        }
        query += "       and emp.fornecedor_id = :pessoa " +
            "            and pag.STATUS = :pago" +
            "            and diaria.tipoproposta <> :suprimento ";
        Query q = em.createNativeQuery(query);
        q.setParameter("pessoa", pessoaId);
        q.setParameter("ano", ano.getAno());
        q.setParameter("suprimento", TipoProposta.SUPRIMENTO_FUNDO.name());
        q.setParameter("pago", StatusPagamento.PAGO.name());
        if (mes != null) {
            q.setParameter("mes", mes.getNumeroMes());
        }
        List<BigDecimal> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }


    private BigDecimal getTotalRendimentosInforme(Pessoa pessoa, Set<VinculoFP> vinculos, Integer ano) {
        String sql = " SELECT (SELECT coalesce(sum(iff.valor), 0)" +
            "        FROM itemfichafinanceirafp iff" +
            "                 INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id" +
            "                 INNER JOIN folhadepagamento fp ON fp.id = ff.folhadepagamento_id" +
            "                 INNER JOIN vinculofp v ON v.id = ff.vinculofp_id" +
            "                 INNER JOIN matriculafp m ON m.id = v.matriculafp_id" +
            "        WHERE fp.ano = :ano" +
            "        AND v.id in :vinculo_id" +
            "        AND m.pessoa_id = :pessoa_id " +
            "          AND iff.TIPOEVENTOFP = :vantagem " +
            "          and fp.tipoFolhaDePagamento <> :tipoFolhaDecimo " +
            "          AND iff.eventofp_id IN (SELECT ige.eventofp_id" +
            "                                  FROM itemgrupoexportacao ige" +
            "                                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id" +
            "                                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id" +
            "                                  WHERE me.descricao = :descricaoModulo" +
            "                                    AND ge.nomereduzido = :nomeReduzido )) - " +
            "                                            (SELECT coalesce(sum(iff.valor), 0) " +
            "                                                                      FROM itemfichafinanceirafp iff " +
            "                                                                               INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id " +
            "                                                                               INNER JOIN folhadepagamento fp ON fp.id = ff.folhadepagamento_id " +
            "                                                                               INNER JOIN vinculofp v ON v.id = ff.vinculofp_id " +
            "                                                                               INNER JOIN matriculafp m ON m.id = v.matriculafp_id " +
            "                                                                      WHERE fp.ano = :ano " +
            "                                                                        AND v.id in :vinculo_id" +
            "                                                                        AND m.pessoa_id = :pessoa_id " +
            "                                                                        and fp.tipoFolhaDePagamento <> :tipoFolhaDecimo " +
            "                                                                        and iff.TIPOEVENTOFP = :desconto " +
            "                                                                        AND iff.eventofp_id IN " +
            "                                                                            (SELECT ige.eventofp_id " +
            "                                                                             FROM itemgrupoexportacao ige " +
            "                                                                                      INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id " +
            "                                                                                      INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id " +
            "                                                                             WHERE me.descricao = :descricaoModulo " +
            "                                                                               AND ge.nomereduzido = :nomeReduzido ))  AS resultado" +
            " FROM dual";
        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("vinculo_id", getIdsVinculos(Lists.newArrayList(vinculos)));
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", DirfReg316.RTRT.name());
        q.setParameter("tipoFolhaDecimo", TipoFolhaDePagamento.SALARIO_13.name());
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());
        q.setParameter("desconto", TipoEventoFP.DESCONTO.name());
        q.setParameter("pessoa_id", pessoa.getId());

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }


    private BigDecimal getTotaDeducoesPorCodigoGrupoExportacaoInforme(Pessoa pessoa, DirfReg316 nomeReduzido, Set<VinculoFP> vinculos, Integer ano) {
        String sql = " SELECT (SELECT coalesce(sum(iff.valor), 0)" +
            "        FROM itemfichafinanceirafp iff" +
            "                 INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id" +
            "                 INNER JOIN folhadepagamento fp ON fp.id = ff.folhadepagamento_id" +
            "                 INNER JOIN vinculofp v ON v.id = ff.vinculofp_id" +
            "                 INNER JOIN matriculafp m ON m.id = v.matriculafp_id" +
            "        WHERE fp.ano = :ano" +
            "          AND v.id in :vinculo_id" +
            "          AND m.pessoa_id in :pessoa_id " +
            "          AND iff.TIPOEVENTOFP = :tipoEventoFP " +
            "          and fp.tipoFolhaDePagamento <> :tipoFolhaDecimo " +
            "          AND iff.eventofp_id IN (SELECT ige.eventofp_id" +
            "                                  FROM itemgrupoexportacao ige" +
            "                                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id" +
            "                                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id" +
            "                                  WHERE me.descricao = :descricaoModulo" +
            "                                    AND ge.nomereduzido = :nomeReduzido ))  AS resultado" +
            " FROM dual";
        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("vinculo_id", getIdsVinculos(Lists.newArrayList(vinculos)));
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", nomeReduzido.name());
        q.setParameter("tipoFolhaDecimo", TipoFolhaDePagamento.SALARIO_13.name());
        q.setParameter("tipoEventoFP", TipoEventoFP.DESCONTO.name());
        q.setParameter("pessoa_id", pessoa.getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getSomaDosEventosAnualInformeRendimentos(Pessoa p, Set<VinculoFP> vinculos, Integer ano, DirfReg316 nomeReduzido, List<TipoFolhaDePagamento> tiposFolha, boolean is13Salario, EventoFP verba, Boolean temDeducaoDeValor) {
        String folhas = "and (";

        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";

        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                      "
            + "INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                     "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND v.id in :vinculo_id                                                                   "
            + "       AND m.pessoa_id = :pessoa_id                                                             ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }
        sql += folhas
            + "       and iff.TIPOEVENTOFP = :vantagem              "
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ge.nomereduzido = :nomeReduzido)                         ";
        if (temDeducaoDeValor) {
            sql += " )-                                                                                              "
                + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
                + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
                + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
                + " INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                     "
                + " INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                    "
                + "      WHERE fp.ano = :ano                                                                       "
                + "       AND v.id in :vinculo_id                                                                   "
                + "        AND m.pessoa_id = :pessoa_id                                                            ";

            if (verba != null) {
                sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
            }
            sql += folhas
                + "        AND iff.TIPOEVENTOFP = :desconto             "
                + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
                + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
                + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
                + "                                 WHERE me.descricao = :descricaoModulo                          "
                + "                                   AND ge.nomereduzido = :nomeReduzido)";
        }
        sql += ") AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("vinculo_id", getIdsVinculos(Lists.newArrayList(vinculos)));
        if (verba != null) {
            q.setParameter("eventofp_id", verba.getId());
        }
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", is13Salario ? nomeReduzido.toString() + "_13" : nomeReduzido.toString());
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());
        q.setParameter("desconto", TipoEventoFP.DESCONTO.name());

        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }


        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public List<Dirf> buscarDirfPorResponsavel(ContratoFP contrato) {
        String sql = " select dirf.* from dirf " +
            " where dirf.RESPONSAVEL_ID = :contrato ";
        Query q = em.createNativeQuery(sql, Dirf.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<Dirf> buscarDirfPorItemVinculo(VinculoFP vinculoFP) {
        String sql = " select distinct dirf.* " +
            " from DIRFVINCULOFP dirfVinc " +
            "         inner join dirf on dirfVinc.DIRF_ID = dirf.ID " +
            " where dirfVinc.VINCULOFP_ID = :vinculo ";
        Query q = em.createNativeQuery(sql, Dirf.class);
        q.setParameter("vinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

}
