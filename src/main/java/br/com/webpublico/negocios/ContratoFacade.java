/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.ContratoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.administrativo.FiscalContratoDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.singletons.SingletonContrato;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class ContratoFacade extends AbstractFacade<Contrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private ModeloDocumentoContratoFacade modeloDocumentoContratoFacade;
    @EJB
    private RegistroSolicitacaoMaterialExternoFacade registroSolicitacaoMaterialExternoFacade;
    @EJB
    private ItemContratoFacade itemContratoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private DocumentosNecessariosAnexoFacade documentosNecessariosAnexoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonContrato singletonContrato;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private AjusteContratoFacade ajusteContratoFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ParticipanteIRPFacade participanteIRPFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private SolicitacaoMaterialExternoFacade solicitacaoMaterialExternoFacade;
    @EJB
    private ExecucaoContratoEstornoFacade execucaoContratoEstornoFacade;
    @EJB
    private LancamentoDebitoContratoFacade lancamentoDebitoContratoFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private OrdemCompraFacade ordemCompraFacade;
    @EJB
    private SaldoItemContratoFacade saldoItemContratoFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;

    public ContratoFacade() {
        super(Contrato.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Contrato recuperar(Object id) {
        Contrato contrato = super.recuperar(id);
        Hibernate.initialize(contrato.getExecucoes());
        Hibernate.initialize(contrato.getPublicacoes());
        Hibernate.initialize(contrato.getFornecedores());
        for (ExecucaoContrato ec : contrato.getExecucoes()) {
            Hibernate.initialize(ec.getNotasFiscais());
            Hibernate.initialize(ec.getObras());
            Hibernate.initialize(ec.getItens());
            Hibernate.initialize(ec.getReservas());
            for (ExecucaoContratoTipo execucaoContratoTipo : ec.getReservas()) {
                Hibernate.initialize(execucaoContratoTipo.getFontes());
                for (ExecucaoContratoTipoFonte fonte : execucaoContratoTipo.getFontes()) {
                    Hibernate.initialize(fonte.getItens());
                }
            }
            Hibernate.initialize(ec.getEmpenhos());
            execucaoContratoFacade.recuperarEmpenhosExecucaoContrato(ec);
        }
        contrato.setAlteracoesContratuais(alteracaoContratualFacade.buscarAlteracoesContratuaisPorContrato(contrato));
        for (AlteracaoContratual ac : contrato.getAlteracoesContratuais()) {
            alteracaoContratualFacade.recuperar(ac.getId());
        }
        Hibernate.initialize(contrato.getAjustes());
        for (AjusteContrato ajuste : contrato.getAjustes()) {
            ajusteContratoFacade.recuperar(ajuste.getId());
        }
        Hibernate.initialize(contrato.getItens());
        Hibernate.initialize(contrato.getOrdensDeCompra());
        for (OrdemDeCompraContrato oc : contrato.getOrdensDeCompra()) {
            Hibernate.initialize(oc.getItens());
        }
        if (contrato.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(contrato.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        atribuirHierarquiaAdministrativaContrato(contrato);
        Hibernate.initialize(contrato.getUnidades());
        for (UnidadeContrato unidade : contrato.getUnidades()) {
            unidade.setHierarquiaAdm(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidade.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao()));
        }
        Hibernate.initialize(contrato.getCaucoes());
        Hibernate.initialize(contrato.getOrdensDeServico());
        Hibernate.initialize(contrato.getFiscais());
        Hibernate.initialize(contrato.getPenalidades());
        Hibernate.initialize(contrato.getContatos());
        Hibernate.initialize(contrato.getDocumentos());
        Hibernate.initialize(contrato.getGestores());
        Collections.sort(contrato.getFornecedores());
        return contrato;
    }

    public Contrato recuperarSomenteItens(Object id) {
        Contrato contrato = super.recuperar(id);
        contrato.getExecucoes().size();
        contrato.getOrdensDeServico().size();
        return contrato;
    }


    public Contrato recuperarSemDependencias(Object id) {
        return super.recuperar(id);
    }

    public void atribuirHierarquiaAdministrativaContrato(Contrato contrato) {
        HierarquiaOrganizacional unidadeAdministrativaVigente = buscarHierarquiaVigenteContrato(contrato, sistemaFacade.getDataOperacao());
        if (unidadeAdministrativaVigente != null) {
            contrato.setUnidadeAdministrativa(unidadeAdministrativaVigente);
        }
    }

    public Contrato recuperarContratoComDependenciasFornecedor(Object id) {
        Contrato contrato = em.find(Contrato.class, id);
        Hibernate.initialize(contrato.getFornecedores());
        atribuirHierarquiaAdministrativaContrato(contrato);
        Collections.sort(contrato.getFornecedores());
        return contrato;
    }

    public Contrato recuperarContratoComDependenciasFornecedores(Object id) {
        Contrato contrato = em.find(Contrato.class, id);
        Hibernate.initialize(contrato.getFornecedores());
        Collections.sort(contrato.getFornecedores());
        atribuirHierarquiaAdministrativaContrato(contrato);
        return contrato;
    }

    public Contrato recuperarContratoComDependenciasItens(Object id) {
        Contrato contrato = em.find(Contrato.class, id);
        Hibernate.initialize(contrato.getItens());
        return contrato;
    }

    public Contrato recuperarContratoComDependenciasEmpenho(Object id) {
        Contrato contrato = em.find(Contrato.class, id);
        Hibernate.initialize(contrato.getItens());
        Hibernate.initialize(contrato.getExecucoes());
        Hibernate.initialize(contrato.getFornecedores());
        if (contrato.getExecucoes() != null && !contrato.getExecucoes().isEmpty()) {
            for (ExecucaoContrato execucaoContrato : contrato.getExecucoes()) {
                Hibernate.initialize(execucaoContrato.getEmpenhos());
            }
        }
        return contrato;
    }

    public Contrato recuperarContratoComDependenciasExecucao(Object id) {
        Contrato contrato = em.find(Contrato.class, id);
        Hibernate.initialize(contrato.getItens());
        Hibernate.initialize(contrato.getFornecedores());
        Hibernate.initialize(contrato.getExecucoes());
        atribuirHierarquiaAdministrativaContrato(contrato);
        Hibernate.initialize(contrato.getAjustes());
        return contrato;
    }

    public void transferirUnidadeContrato(Contrato contrato, UnidadeOrganizacional unidadeAdministrativa, Date inicioVigencia) {
        UnidadeContrato unidadeVigenteContrato = buscarUnidadeVigenteContrato(contrato);
        unidadeVigenteContrato.setFimVigencia(DataUtil.getDataDiaAnterior(inicioVigencia));
        em.merge(unidadeVigenteContrato);

        UnidadeContrato novaUnidadeContrato = new UnidadeContrato();
        novaUnidadeContrato.setContrato(contrato);
        novaUnidadeContrato.setInicioVigencia(inicioVigencia);
        novaUnidadeContrato.setUnidadeAdministrativa(unidadeAdministrativa);
        em.merge(novaUnidadeContrato);
    }

    public void transferirFornecedorContrato(Contrato contrato, Pessoa fornecedor, PessoaFisica responsavel) {
        FornecedorContrato fc = new FornecedorContrato();
        fc.setOrdem(contrato.getFornecedores().size());
        fc.setContrato(contrato);
        fc.setFornecedor(fornecedor);
        fc.setResponsavelFornecedor(responsavel);
        em.merge(fc);

        contrato.setContratado(fornecedor);
        contrato.setResponsavelEmpresa(responsavel);
    }

    public Contrato recuperarPortalTransparencia(Object id) {
        Contrato contrato = em.find(Contrato.class, id);
        contrato.getItens().size();
        contrato.getOrdensDeServico().size();
        contrato.getOrdensDeCompra().size();
        contrato.getGestores().size();
        contrato.setAlteracoesContratuais(alteracaoContratualFacade.buscarAlteracoesContratuaisPorContrato(contrato));
        if (contrato.getDetentorDocumentoLicitacao() != null) {
            contrato.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList().size();
        }
        return contrato;
    }

    public List<Contrato> buscarContratoPorNumeroOrExercicio(String numeroOrExercicio) {
        String sql = "select c.*  " +
            "   from contrato c " +
            "  inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "where c.numerotermo like :numero_exercicio or e.ano like :numero_exercicio" +
            " order by e.ano desc, c.numerotermo desc";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("numero_exercicio", "%" + numeroOrExercicio.trim() + "%");
        q.setMaxResults(50);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarContratoPorNumeroAndExercicio(String numero, Exercicio exercicio) {
        String sql = "select c.*  " +
            "   from contrato c " +
            "  inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "where c.numerotermo = :numero and e.id =:exercicio" +
            " order by c.numerotermo desc";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("numero", numero.trim());
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(50);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarContratoPortalTransparencia(Exercicio exercicio) {
        String sql = "select c.*  " +
            "   from contrato c " +
            "  inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "where e.id = :exercicio";

        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("exercicio", exercicio.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<Contrato> buscarContratos(String parte) {
        String sql = "" +
            "   select c.*  " +
            "   from contrato c " +
            "     inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or e.ano like :parte " +
            "           or lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte " +
            "           or pj.cnpj like :parte" +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte) " +
            "   order by c.numerotermo desc ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setMaxResults(50);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            List resultList = q.getResultList();
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarContratosVigentesNosUltimosDozeMesesParaCotacao(String parte, List<Long> idsObjetosDeCompras) {
        String sql = "" +
            "   select DISTINCT c.*  " +
            "   from contrato c " +
            "     inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "     left join itemcontrato ic on c.id = ic.contrato_id" +
            "     left join itemcontratovigente icv on icv.itemcontrato_id = c.id " +
            "     left join itemcotacao icot on icot.id = icv.itemcotacao_id " +
            "     left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "     left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "     left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "     left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
            "     left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
            "     left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
            "     left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "     left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
            "     left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
            "     left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
            "     inner join objetocompra oc on oc.id = coalesce(itemsol.objetocompra_id, ise.objetocompra_id, icot.objetocompra_id) " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or e.ano like :parte " +
            "           or lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte " +
            "           or pj.cnpj like :parte" +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte) " +
            "           and (MONTHS_BETWEEN(SYSDATE,C.TERMINOVIGENCIAATUAL) <= 12)" +
            "           and oc.id in (:idsObjetosDeCompras)" +
            "   order by c.numerotermo desc ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idsObjetosDeCompras", idsObjetosDeCompras);
        q.setMaxResults(70);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            List resultList = q.getResultList();
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarContratosPorTipoContrato(String parte, TipoAquisicaoContrato tipoContrato) {
        String sql = "" +
            "   select c.*  " +
            "   from contrato c " +
            "     inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or e.ano like :parte " +
            "           or lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte " +
            "           or pj.cnpj like :parte" +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte)" +
            "     and c.tipoaquisicao = :tipoContrato " +
            "   order by c.numerotermo desc ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("tipoContrato", tipoContrato.name());
        q.setMaxResults(50);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarContratosConcessao(String parte, SituacaoContrato... situacoes) {
        String sql = "" +
            "   select c.*  " +
            "   from contrato c " +
            "     inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or e.ano like :parte " +
            "           or lower(pf.nome) like :parte " +
            "           or lower(pj.razaosocial) like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte " +
            "           or pj.cnpj like :parte) " +
            "     and c.situacaocontrato in (:situacoes) " +
            "     and c.contratoconcessao = :contratoConcessao" +
            "   order by c.numerotermo desc ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("situacoes", SituacaoContrato.getSituacoesAsString(Arrays.asList(situacoes)));
        q.setParameter("contratoConcessao", Boolean.TRUE);
        q.setMaxResults(50);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarFiltrandoPorSituacao(String parte, SituacaoContrato... situacoes) {
        String sql = "" +
            "   select c.*  " +
            "   from contrato c " +
            "     inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or lower(c.objeto) like :parte " +
            "           or e.ano like :parte " +
            "           or lower(pf.nome) like :parte " +
            "           or lower(pj.razaosocial) like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte " +
            "           or pj.cnpj like :parte) " +
            "     and c.situacaocontrato in (:situacoes) " +
            "     and c.contratoconcessao = :contratoConcessao" +
            "   order by c.numerotermo desc ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacoes", SituacaoContrato.getSituacoesAsString(Arrays.asList(situacoes)));
        q.setParameter("contratoConcessao", Boolean.FALSE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarContratoComExecucaoReprocessada(String parte) {
        String sql = "" +
            "   select c.*  " +
            "   from contrato c " +
            "     inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or lower(c.objeto) like :parte " +
            "           or e.ano like :parte " +
            "           or lower(pf.nome) like :parte " +
            "           or lower(pj.razaosocial) like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte " +
            "           or pj.cnpj like :parte) " +
            "     and c.situacaocontrato in (:situacoes) " +
            "     and c.contratoconcessao = :contratoConcessao" +
            "     and exists (select 1 from execucaocontrato ec " +
            "                         where ec.contrato_id = c.id " +
            "                              and ec.reprocessada = :reprocessada) " +
            "   order by c.numerotermo desc ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacoes", SituacaoContrato.getSituacoesAsString(Arrays.asList(SituacaoContrato.situacoesDiferenteEmElaboracao)));
        q.setParameter("contratoConcessao", Boolean.FALSE);
        q.setParameter("reprocessada", Boolean.TRUE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarContratoEmAndamento(String parte, Date dataReferencia) {
        String sql = " select c.* from contrato c " +
            "     inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (c.numerotermo like :parte " +
            "           or c.numerocontrato like :parte " +
            "           or e.ano like :parte " +
            "           or lower(pf.nome) like :parte " +
            "           or lower(pj.razaosocial) like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or pj.cnpj like :parte" +
            "           or to_char(c.numerotermo) || '/' || to_char(e.ano) like :numeroAno" +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :numeroAno) " +
            "     and trunc(c.inicioexecucao) <= to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "     and c.situacaocontrato in ('APROVADO', 'DEFERIDO') " +
            "     and c.saldoatualcontrato > 0 " +
            "     and c.contratoconcessao = :contratoConcessao" +
            "     order by e.ano desc, c.numerocontrato desc ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("numeroAno", parte.trim() + "%");
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        q.setParameter("contratoConcessao", Boolean.FALSE);
        q.setMaxResults(50);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Contrato> buscarFiltrandoOndeUsuarioGestorLicitacao(FiltroContratoRequisicaoCompra filtroVO, TipoObjetoCompra tipoObjetoCompra, Integer maxResult) {
        String sql = " " +
            "   select distinct con.* from contrato con " +
            "     inner join exercicio e on e.id = con.exerciciocontrato_id " +
            "     inner join execucaocontrato ex on ex.contrato_id = con.id " +
            "     inner join execucaocontratotipo exTipo on extipo.execucaocontrato_id = ex.id " +
            "     inner join unidadecontrato uc on uc.contrato_id = con.id  " +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia),to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "     inner join hierarquiaorganizacional ho on ho.subordinada_id = uc.unidadeadministrativa_id " +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia),to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "     left join pessoa pForn on con.contratado_id = pForn.id " +
            "     left join pessoafisica pfForn on pForn.id = pfForn.id " +
            "     left join pessoajuridica pjForn on pForn.id = pjForn.id " +
            "     left join pessoa pResp on con.responsavelempresa_id = pResp.id " +
            "     left join pessoafisica pfResp on pResp.id = pfResp.id " +
            "     left join pessoajuridica pjResp on pResp.id = pjResp.id " +
            "  where extipo.tipoobjetocompra = :tipoObjetoCompra " +
            "     and con.situacaoContrato = :situacaoContratoDeferido " +
            "     and con.contratoconcessao = :contratoConcessao" +
            "     and (lower(con.numeroprocesso) like :filtro " +
            "       or lower(pfResp.nome) like :filtro " +
            "       or lower(pjResp.razaosocial) like :filtro " +
            "       or replace(replace(pfResp.cpf, '.', ''), '-', '') like :filtro  " +
            "       or pfResp.cpf like :filtro  " +
            "       or replace(replace(replace(pjResp.cnpj, '.', ''), '-', ''), '/', '') like :filtro " +
            "       or pjResp.cnpj like :filtro " +
            "       or lower(con.numeroprocesso) like :filtro " +
            "       or lower(pfForn.nome) like :filtro " +
            "       or lower(pjForn.razaosocial) like :filtro " +
            "       or replace(replace(pfForn.cpf, '.', ''), '-', '') like :filtro  " +
            "       or pfForn.cpf like :filtro  " +
            "       or replace(replace(replace(pjForn.cnpj, '.', ''), '-', ''), '/', '') like :filtro " +
            "       or pjForn.cnpj like :filtro " +
            "       or to_char(con.numerotermo) || '/' || to_char(e.ano) like :filtro " +
            "       or lower(con.numerotermo) like :filtro  " +
            "       or lower(con.numerocontrato) like :filtro " +
            "       or to_char(con.numerocontrato) || '/' || to_char(extract (year from con.datalancamento)) like :filtro " +
            "       or ho.codigo like :filtro " +
            "       or replace(ho.codigo, '.', '') like :filtro " +
            "       or lower(ho.descricao) like :filtro " +
            "       ) " +
            "   and exists(select 1 " +
            "              from usuariounidadeorganizacio u_un " +
            "              where u_un.usuariosistema_id = :idUsuario " +
            "              and u_un.unidadeorganizacional_id = uc.unidadeadministrativa_id " +
            "              and u_un.gestorlicitacao = :gestoLicitacao ) ";
        if (filtroVO.getHierarquiaOrganizacional() != null && filtroVO.getHierarquiaOrganizacional().getSubordinada() != null) {
            sql += " and uc.unidadeadministrativa_id = :unidadeOrganizacionalId ";
        }
        if (filtroVO.getExercicio() != null) {
            sql += " and con.exerciciocontrato_id = :exercicioId ";
        }
        if (filtroVO.getLicitacao() != null) {
            sql += " and con.id in (select conlic.contrato_id from conlicitacao conlic where conlic.licitacao_id = :licitacaoId) ";
        }
        if (filtroVO.getFiscal() != null) {
            sql += " and con.id in (select f.contrato_id from fiscalcontrato f " +
                " left join fiscalexternocontrato fe on fe.id = f.id " +
                " left join contrato c on c.id = fe.contratofiscal_id " +
                " left join fiscalinternocontrato fi on fi.id = f.id " +
                " left join contratofp cfp on cfp.id = fi.servidor_id " +
                " left join pessoafisica pf on pf.id = fi.servidorpf_id " +
                " left join vinculofp v on v.id = cfp.id " +
                " left join matriculafp m on m.id = v.matriculafp_id " +
                " left join pessoa p on p.id = coalesce(c.contratado_id, m.pessoa_id, pf.id)" +
                " where p.id = :pessoaFiscalId) ";
        }
        if (filtroVO.getGestor() != null) {
            sql += " and con.id in (select g.contrato_id from gestorcontrato g " +
                " left join contratofp cfp on cfp.id = g.servidor_id " +
                " left join pessoafisica pf on pf.id = g.servidorpf_id " +
                " left join vinculofp v on v.id = cfp.id " +
                " left join matriculafp m on m.id = v.matriculafp_id " +
                " left join pessoa p on p.id = m.pessoa_id " +
                " where p.id = :pessoaGestorId) ";
        }
        if (filtroVO.getObjetoCompra() != null) {
            sql += " and con.id in (select ic.contrato_id from itemcontrato ic" +
                " left join itemcontratovigente icv on icv.itemcontrato_id = ic.id " +
                " left join itemcotacao icot on icot.id = icv.itemcotacao_id " +
                " left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                " left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                " left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                " left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                " left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                " left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
                " left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                " left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                " left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
                " left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
                " inner join objetocompra oc on oc.id = coalesce(itemsol.objetocompra_id, ise.objetocompra_id, icot.objetocompra_id) " +
                " where oc.id = :objetoCompraId)";
        }
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("filtro", "%" + filtroVO.getParte().trim().toLowerCase() + "%");
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestoLicitacao", Boolean.TRUE);
        q.setParameter("tipoObjetoCompra", tipoObjetoCompra.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("situacaoContratoDeferido", SituacaoContrato.DEFERIDO.name());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("contratoConcessao", Boolean.FALSE);
        if (filtroVO.getHierarquiaOrganizacional() != null && filtroVO.getHierarquiaOrganizacional().getSubordinada() != null) {
            q.setParameter("unidadeOrganizacionalId", filtroVO.getHierarquiaOrganizacional().getSubordinada().getId());
        }
        if (filtroVO.getExercicio() != null) {
            q.setParameter("exercicioId", filtroVO.getExercicio().getId());
        }
        if (filtroVO.getLicitacao() != null) {
            q.setParameter("licitacaoId", filtroVO.getLicitacao().getId());
        }
        if (filtroVO.getFiscal() != null) {
            q.setParameter("pessoaFiscalId", filtroVO.getFiscal().getId());
        }
        if (filtroVO.getGestor() != null) {
            q.setParameter("pessoaGestorId", filtroVO.getGestor().getId());
        }
        if (filtroVO.getObjetoCompra() != null) {
            q.setParameter("objetoCompraId", filtroVO.getObjetoCompra().getId());
        }
        if (maxResult != null) {
            q.setMaxResults(maxResult);
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            List<Contrato> contratos = q.getResultList();
            for (Contrato contrato : contratos) {
                contrato.setUnidadeAdministrativa(buscarHierarquiaVigenteContrato(contrato, sistemaFacade.getDataOperacao()));
            }
            return contratos;
        }
        return new ArrayList<>();
    }

    public List<Contrato> buscarFiltrandoContratoPorModalidadeAndUnidade(String parte, UnidadeOrganizacional unidadeOrganizacional, ModalidadeLicitacaoEmpenho modalidadeLicitacao) {
        String sql = " " +
            " select c.* from contrato c " +
            "   inner join unidadecontrato uc on uc.contrato_id = c.id " +
            "   inner join vwhierarquiaadministrativa vwAdm  on vwAdm.subordinada_id = uc.unidadeadministrativa_id " +
            " where c.tipoaquisicao in (:tiposAquisicao) " +
            "   and (lower(to_char(c.tipoaquisicao)) like :filtro " +
            "        or to_char(c.numeroProcesso) like :filtro " +
            "        or to_char(c.numerotermo) like :filtro) " +
            "   and vwAdm.entidade_id = :idEntidadeOrc " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between vwAdm.inicioVigencia and coalesce (vwAdm.fimVigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "   and c.contratoconcessao = :contratoConcessao ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("contratoConcessao", Boolean.FALSE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tiposAquisicao", TipoAquisicaoContrato.getTiposAquisicaoPorModalidade(modalidadeLicitacao));
        q.setParameter("idEntidadeOrc", entidadeFacade.recuperarEntidadePorUnidadeOrcamentaria(unidadeOrganizacional).getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<Contrato> listaFiltrandoContrato(String parte, UnidadeOrganizacional unidadeOrganizacional) {
        String hql = " select c.* from Contrato c " +
            "   inner join unidadecontrato uc on uc.contrato_id = c.id " +
            "           and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            " where (lower(to_char(c.tipoaquisicao)) like :filtro " +
            "    or to_char(c.numeroProcesso) like :filtro " +
            "    or to_char(c.numerotermo) like :filtro " +
            "    or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :filtro " +
            "    or lower(c.objeto) like :filtro) " +
            "   and uc.unidadeadministrativa_id = :unidade " +
            "   and c.contratoconcessao = :contratoConcessao ";
        Query q = em.createNativeQuery(hql, Contrato.class);
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("contratoConcessao", Boolean.FALSE);
        q.setMaxResults(10);
        try {
            return q.getResultList();
        } catch (NoResultException nr) {
            return Lists.newArrayList();
        }
    }

    public List<ItemContrato> buscarItensContrato(Contrato contrato) {
        String sql = "" +
            " select ic.* from itemcontrato ic where ic.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql, ItemContrato.class);
        q.setParameter("idContrato", contrato.getId());
        return q.getResultList();
    }

    public boolean isMesmoItemObjetoCompra(String especif1, ObjetoCompra objetoCompra1, Integer numeroLote1,
                                           String especif2, ObjetoCompra objetoCompra2, Integer numeroLote2) {
        if (!Strings.isNullOrEmpty(especif1) && !Strings.isNullOrEmpty(especif2)) {
            return objetoCompra1.equals(objetoCompra2)
                && especif1.trim().equals(especif2.trim())
                && numeroLote1.equals(numeroLote2);
        }
        return objetoCompra1.equals(objetoCompra2) && numeroLote1.equals(numeroLote2);
    }

    public List<ItemContrato> criarItensContratoIRP(Contrato contrato) {

        List<ItemPropostaFornecedor> itens = licitacaoFacade.getItensVencidosPeloFornecedorPorStatus(contrato.getLicitacao(),
            SituacaoItemProcessoDeCompra.HOMOLOGADO, contrato.getContratado(), TipoClassificacaoFornecedor.getHabilitados());

        List<ItemContrato> itensContrato = Lists.newArrayList();
        List<ItemParticipanteIRP> itensParticipantes = participanteIRPFacade.buscarItensUnidadeParticipante(contrato.getParticipanteIRP());

        for (ItemParticipanteIRP itemPart : itensParticipantes) {
            for (ItemPropostaFornecedor ipf : itens) {
                if (isMesmoItemObjetoCompra(
                    itemPart.getItemIntencaoRegistroPreco().getDescricaoComplementar(),
                    itemPart.getItemIntencaoRegistroPreco().getObjetoCompra(),
                    itemPart.getItemIntencaoRegistroPreco().getLoteIntencaoRegistroPreco().getNumero(),
                    ipf.getItemProcessoDeCompra().getDescricaoComplementar(),
                    ipf.getObjetoCompra(),
                    ipf.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero())) {

                    ItemContrato ic = new ItemContrato();
                    ic.setContrato(contrato);
                    ic.setItemContratoItemIRP(new ItemContratoItemIRP(ic, ipf, itemPart));
                    ic.setTipoControle(ic.getItemAdequado().getTipoControle());
                    alterarTipoControleItemParaValorQuandoUtilizadoEmOutroContratoPorValor(ic);
                    itensContrato.add(ic);
                }
            }
        }
        return itensContrato;
    }

    public List<ItemContrato> criarItensContratoSolicitacaoAdesaoAtaInterna(Contrato contrato) {

        List<ItemSolicitacaoExterno> itensSolicitacao = solicitacaoMaterialExternoFacade.buscarItens(contrato.getSolicitacaoAdesaoAtaInterna());
        List<ItemPropostaFornecedor> itensVencidos = licitacaoFacade.getItensVencidosPeloFornecedorPorStatus(contrato.getLicitacao(),
            SituacaoItemProcessoDeCompra.HOMOLOGADO, contrato.getContratado(), TipoClassificacaoFornecedor.getHabilitados());

        List<ItemContrato> itensContrato = new ArrayList<>();
        for (ItemSolicitacaoExterno itemSol : itensSolicitacao) {
            for (ItemPropostaFornecedor ipf : itensVencidos) {
                if (itemSol.getItemProcessoCompra() != null && itemSol.getItemProcessoCompra().equals(ipf.getItemProcessoDeCompra())) {
                    ItemContrato ic = new ItemContrato();
                    ic.setContrato(contrato);
                    ic.setValorUnitario(itemSol.getValorUnitario());
                    ic.setItemContratoAdesaoAtaInt(new ItemContratoAdesaoAtaInterna(ic, ipf, itemSol));
                    ic.setTipoControle(ic.getItemAdequado().getTipoControle());
                    alterarTipoControleItemParaValorQuandoUtilizadoEmOutroContratoPorValor(ic);
                    itensContrato.add(ic);
                }
            }
        }
        return itensContrato;
    }

    public List<ItemContrato> criarItensContratoLicitacao(Contrato c) {
        List<ItemPropostaFornecedor> itens = licitacaoFacade.getItensVencidosPeloFornecedorPorStatus(c.getLicitacao(),
            SituacaoItemProcessoDeCompra.HOMOLOGADO, c.getContratado(), TipoClassificacaoFornecedor.getHabilitados());
        List<ItemContrato> retorno = new ArrayList<>();
        for (ItemPropostaFornecedor ipf : itens) {
            ItemContrato ic = new ItemContrato();
            ic.setContrato(c);
            ic.setItemContratoItemPropostaFornecedor(new ItemContratoItemPropostaFornecedor(ic, ipf));
            ic.setTipoControle(ic.getItemAdequado().getTipoControle());
            alterarTipoControleItemParaValorQuandoUtilizadoEmOutroContratoPorValor(ic);
            retorno.add(ic);
        }
        return retorno;
    }

    public List<ItemContrato> criarItensContratoDeDispensaDeLicitacao(Contrato c) {
        List<ItemPropostaFornecedorDispensa> itens = dispensaDeLicitacaoFacade.getItensVencidosPeloFornecedorPorStatus(
            c.getContratoDispensaDeLicitacao().getDispensaDeLicitacao(),
            c.getContratado(),
            TipoClassificacaoFornecedor.getHabilitados());

        List<ItemContrato> retorno = new ArrayList<>();
        for (ItemPropostaFornecedorDispensa ifd : itens) {
            ItemContrato ic = new ItemContrato();
            ic.setContrato(c);
            ic.setItemContratoItemPropostaFornecedorDispensa(new ItemContratoItemPropostaFornecedorDispensa(ic, ifd));
            ic.setTipoControle(ic.getItemAdequado().getTipoControle());
            alterarTipoControleItemParaValorQuandoUtilizadoEmOutroContratoPorValor(ic);
            retorno.add(ic);
        }
        return retorno;
    }

    public List<ItemContrato> criarItensContratoDeRegistroDePrecoExterno(Contrato c) {
        List<ItemSolicitacaoExterno> itens = solicitacaoMaterialExternoFacade.getItensVencidosPeloFornecedorPorStatus(c.getContratoRegistroPrecoExterno().getRegistroSolicitacaoMaterialExterno(), c.getContratado(), TipoClassificacaoFornecedor.getHabilitados());
        List<ItemContrato> retorno = new ArrayList<>();
        for (ItemSolicitacaoExterno ise : itens) {
            ItemContrato ic = new ItemContrato();
            ic.setContrato(c);
            ic.setItemContratoItemSolicitacaoExterno(new ItemContratoItemSolicitacaoExterno(ic, ise));
            ic.setTipoControle(ic.getItemAdequado().getTipoControle());
            alterarTipoControleItemParaValorQuandoUtilizadoEmOutroContratoPorValor(ic);
            retorno.add(ic);
        }
        return retorno;
    }

    public List<ItemContrato> criarItensContratoDe(Contrato contrato) {
        if (contrato.isDeLicitacao() || contrato.isDeProcedimentosAuxiliares()) {
            return criarItensContratoLicitacao(contrato);
        }
        if (contrato.isDeIrp()) {
            return criarItensContratoIRP(contrato);
        }
        if (contrato.isDeAdesaoAtaRegistroPrecoInterna()) {
            return criarItensContratoSolicitacaoAdesaoAtaInterna(contrato);
        }
        if (contrato.isDeDispensaDeLicitacao()) {
            return criarItensContratoDeDispensaDeLicitacao(contrato);
        }
        if (contrato.isDeRegistroDePrecoExterno()) {
            return criarItensContratoDeRegistroDePrecoExterno(contrato);
        }
        if (contrato.isContratoVigente()) {
            return contrato.getItens();
        }
        return null;
    }

    private void alterarTipoControleItemParaValorQuandoUtilizadoEmOutroContratoPorValor(ItemContrato ic) {
        if (ic.getTipoControle().isTipoControlePorQuantidade() && itemContratoFacade.isUtilizadoEmOutroContratoPorValor(ic)) {
            ic.setTipoControle(TipoControleLicitacao.VALOR);
        }
    }

    public void atribuirGrupoContaDespesa(ItemContrato item) {
        if (item.getItemAdequado().getObjetoCompra() != null) {
            item.getItemAdequado().getObjetoCompra().setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(
                item.getItemAdequado().getObjetoCompra().getTipoObjetoCompra(),
                item.getItemAdequado().getObjetoCompra().getGrupoObjetoCompra()));
        }
    }

    public List<Contrato> buscarContratoDisponivelNovaObra(String parte) {
        String sql = " " +
            " select contrato.* from  " +
            " (select c.*   " +
            "   from contrato c  " +
            "  inner join exercicio e on c.exerciciocontrato_id = e.id  " +
            "  inner join conlicitacao cl on cl.contrato_id = c.id  " +
            "  inner join licitacao l on l.id = cl.licitacao_id  " +
            "  inner join processodecompra pc on pc.id = l.processodecompra_id  " +
            "  inner join solicitacaomaterial sm on sm.id = pc.solicitacaomaterial_id  " +
            " where sm.tiposolicitacao = :obra_servicoengenharia " +
            "   and c.situacaocontrato <> :contratoEmElaboracao " +
            "   and c.contratoconcessao = :contratoConcessao " +
            "   and (c.numerotermo like :parte or c.numerocontrato like :parte or e.ano like :parte or lower(trim(c.objeto)) like :parte) " +
            " " +
            "union all " +
            "select c.*   " +
            "   from contrato c  " +
            "  inner join exercicio e on c.exerciciocontrato_id = e.id  " +
            "  inner join condispensalicitacao cdl on cdl.contrato_id = c.id  " +
            "  inner join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id  " +
            "  inner join processodecompra pc on pc.id = dl.processodecompra_id  " +
            "  inner join solicitacaomaterial sm on sm.id = pc.solicitacaomaterial_id  " +
            " where sm.tiposolicitacao = :obra_servicoengenharia " +
            "   and c.situacaocontrato <> :contratoEmElaboracao " +
            "   and c.contratoconcessao = :contratoConcessao " +
            "   and (c.numerotermo like :parte or c.numerocontrato like :parte or e.ano like :parte or lower(trim(c.objeto)) like :parte) " +
            " )contrato " +
            "where not exists (select 1 from obra obra where obra.contrato_id = contrato.id) ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("obra_servicoengenharia", TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA.name());
        q.setParameter("contratoEmElaboracao", SituacaoContrato.EM_ELABORACAO.name());
        q.setParameter("contratoConcessao", Boolean.FALSE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Pessoa> buscarFornecedoresVencedoresComItensHomologados(Contrato contrato) {
        List<LicitacaoFornecedor> licForns;
        if (contrato.isDeProcedimentosAuxiliares()) {
            licForns = licitacaoFacade.buscarFornecedoresLicitacao(contrato.getLicitacao());
        } else {
            licForns = licitacaoFacade.buscarFornecedoresVencedoresComItensHomologados(contrato.getLicitacao());
        }
        List<Pessoa> retorno = new ArrayList<>();
        for (LicitacaoFornecedor licForn : licForns) {
            retorno.add(licForn.getEmpresa());
        }
        return retorno;
    }

    public List<Pessoa> buscarFornecedoresProcesso(Contrato contrato) {
        List<Pessoa> fornecedores = Lists.newArrayList();
        switch (contrato.getTipoAquisicao()) {
            case LICITACAO:
            case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
            case INTENCAO_REGISTRO_PRECO:
            case PROCEDIMENTO_AUXILIARES:
                List<LicitacaoFornecedor> list = licitacaoFacade.buscarFornecedoresLicitacao(contrato.getLicitacao());
                for (LicitacaoFornecedor lf : list) {
                    fornecedores.add(lf.getEmpresa());
                }
                break;
            case DISPENSA_DE_LICITACAO:
                fornecedores = dispensaDeLicitacaoFacade.recuperarFornecedoresDispensaDeLicitacao(contrato.getDispensaDeLicitacao());
                break;
            case REGISTRO_DE_PRECO_EXTERNO:
                fornecedores = registroSolicitacaoMaterialExternoFacade.recuperarFornecedoresRegistroPrecoExterno(contrato.getRegistroSolicitacaoMaterialExterno());
                break;
        }
        return fornecedores;
    }

    public List<ExecucaoContratoTipoFonte> buscarFonteDespesaORCOfContrato(Contrato contrato) {
        String sql = " select ectf.* " +
            "    from contrato c " +
            "   inner join execucaocontrato ec on ec.contrato_id = c.id " +
            "   inner join execucaocontratotipo ect on ect.execucaocontrato_id = ec.id " +
            "   inner join execucaocontratotipofonte ectf on ectf.execucaocontratotipo_id = ect.id " +
            " Where c.id = :id_contrato ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoTipoFonte.class);
        q.setParameter("id_contrato", contrato.getId());
        return q.getResultList();
    }

    public HierarquiaOrganizacional buscarHierarquiaVigenteContrato(Contrato contrato, Date dataOperacao) {
        String sql = " select ho.* from unidadecontrato uc" +
            "           inner join contrato c on c.id = uc.contrato_id " +
            "           inner join hierarquiaorganizacional ho on ho.subordinada_id = uc.unidadeAdministrativa_id" +
            "           where uc.contrato_id = :idContrato " +
            "               and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "               and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "               and ho.tipohierarquiaorganizacional = :tipoHo ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("tipoHo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setMaxResults(1);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getResultList().get(0);
        }
        return null;
    }

    public UnidadeContrato buscarUnidadeVigenteContrato(Contrato contrato) {
        String sql = " select uc.* from unidadecontrato uc " +
            "   where uc.contrato_id = :idContrato " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, UnidadeContrato.class);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (UnidadeContrato) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Unidade vigente não encontrada para o contrato : " + contrato + ".");
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Retornou mais de uma unidade vigente para o contrato: " + contrato + ".");
        }
    }

    public UnidadeContrato buscarUnidadeVigenteContrato(Contrato contrato, UnidadeOrganizacional unidade) {
        String sql = " select uc.* from unidadecontrato uc " +
            "   where uc.contrato_id = :idContrato " +
            "   and uc.unidadeadministrativa_id = :idUnidade " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, UnidadeContrato.class);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (UnidadeContrato) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Unidade vigente não encontrada para o contrato : " + contrato + ".");
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Retornou mais de uma unidade vigente para o contrato: " + contrato + ".");
        }
    }

    public List<UnidadeContrato> buscarUnidadeContrato(Contrato contrato) {
        String sql = " select uc.* from unidadecontrato uc where uc.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql, UnidadeContrato.class);
        q.setParameter("idContrato", contrato.getId());
        return q.getResultList();
    }


    public void validarNumeroTermoExistente(ContratoValidacao contrato) throws ValidacaoException {
        String sql = "" +
            " select c.* " +
            "  from Contrato c " +
            "  inner join unidadecontrato uc on uc.contrato_id = c.id " +
            "  inner join hierarquiaorganizacional ho on ho.subordinada_id = uc.unidadeadministrativa_id " +
            "       and ho.tipohierarquiaorganizacional = '" + TipoHierarquiaOrganizacional.ADMINISTRATIVA.name() + "'" +
            "       and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where c.numerotermo = :numero " +
            "  and c.exerciciocontrato_id = :exercicio_id " +
            "  and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "  and substr(ho.codigo,0,6) = :codigoOrgao ";
        if (contrato.getId() != null) {
            sql += " and c.id <> :id ";
        }
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("codigoOrgao", contrato.getUnidadeAdministrativa().getCodigo().substring(0, 6));
        q.setParameter("numero", contrato.getNumeroTermo());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("exercicio_id", contrato.getExercicioContrato().getId());
        if (contrato.getId() != null) {
            q.setParameter("id", contrato.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoPermitida("O número do termo gerado encontra-se registrado para outro contrato.");
            throw ve;
        }
    }

    public Contrato salvarRescisaoContrato(Contrato entity, List<MovimentoItemContrato> movimentosItensRescicao) {
        if (entity.getRescisaoContrato().getId() == null) {
            entity.setSituacaoContrato(SituacaoContrato.RESCINDIDO);

            movimentosItensRescicao.forEach(movExecucao -> {
                saldoItemContratoFacade.gerarSaldoPorMovimento(movExecucao);

                MovimentoItemContrato movVariacao = (MovimentoItemContrato) Util.clonarObjeto(movExecucao);
                movVariacao.setId(null);
                movVariacao.setSubTipo(SubTipoSaldoItemContrato.VARIACAO);
                saldoItemContratoFacade.gerarSaldoPorMovimento(movVariacao);

                entity.setSaldoAtualContrato(entity.getSaldoAtualContrato().subtract(movExecucao.getValorTotal()));
            });
        }
        return em.merge(entity);
    }

    public Contrato salvarNovoContrato(Contrato contrato) {
        gerarNumeroContrato(contrato);
        if (contrato.isContratoEmElaboracao()) {
            criarUnidadeContrato(contrato);
            criarFornecedorContrato(contrato);
        }
        contrato = em.merge(contrato);
        if (contrato.isContratoAprovado()) {
            contrato.getItens().stream().filter(item -> !itemContratoFacade.getUtilizadoProcesso(item)).forEach(item -> {
                saldoItemContratoFacade.excluirSaldoAndMovimento(item);
                saldoItemContratoFacade.gerarSaldoInicialContrato(item);
            });
        }
        return contrato;
    }

    public void bloquearUnidadeSingleton(Contrato entity) {
        singletonContrato.bloquearUnidade(entity.getUnidadeAdministrativa().getSubordinada());
    }

    public void desbloquearUnidadeSingleton(Contrato entity) {
        if (entity.getUnidadeAdministrativa() == null) {
            entity.setUnidadeAdministrativa(buscarHierarquiaVigenteContrato(entity, sistemaFacade.getDataOperacao()));
        }
        singletonContrato.desbloquearUnidade(entity.getUnidadeAdministrativa().getSubordinada());
    }

    public void validarUnidadeSingleton(Contrato entity) {
        ValidacaoException ve = new ValidacaoException();
        if (singletonContrato.isBloqueado(entity.getUnidadeAdministrativa().getSubordinada())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe outro(s) contrato(s) sendo movimentado nesta mesma unidade. " +
                "Para garantir a integridade do número do termo, reinicie o processo novamente.");
        }
        ve.lancarException();
    }

    public Contrato aprovarContrato(Contrato contrato) {
        bloquearUnidadeSingleton(contrato);
        contrato.setSituacaoContrato(SituacaoContrato.APROVADO);
        contrato.setSaldoAtualContrato(contrato.getValorTotal());
        contrato.setValorAtualContrato(contrato.getValorTotal());
        for (ItemContrato item : contrato.getItens()) {
            saldoItemContratoFacade.gerarSaldoInicialContrato(item);
        }
        contrato = em.merge(contrato);
        salvarPortal(contrato);
        desbloquearUnidadeSingleton(contrato);
        return contrato;
    }

    public ContratoAprovacaoVO simularAprovacaoContratoVO(Contrato contrato) {
        ContratoAprovacaoVO contAprovVO = new ContratoAprovacaoVO();
        contAprovVO.setValorAtualContrato(contrato.getValorTotal());
        contAprovVO.setSaldoAtualContrato(contrato.getValorAtualContrato());
        contAprovVO.setTerminoVigencia(contrato.getTerminoVigencia());
        contAprovVO.setTerminoExecucao(contrato.getTerminoExecucao());

        Collections.sort(contrato.getItens());
        for (ItemContrato item : contrato.getItens()) {
            ItemContratoAprovacaoVO movoItemVO = new ItemContratoAprovacaoVO();
            movoItemVO.setItemContrato(item);

            MovimentoItemContrato movExecucao = saldoItemContratoFacade.novoMovimentoInicialItemContrato(item, SubTipoSaldoItemContrato.EXECUCAO);
            movoItemVO.setMovimentoItemExecucao(movExecucao);

            MovimentoItemContrato movVariacao = saldoItemContratoFacade.novoMovimentoInicialItemContrato(item, SubTipoSaldoItemContrato.VARIACAO);
            movoItemVO.setMovimentoItemVariacao(movVariacao);

            movoItemVO.setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(item.getItemAdequado().getObjetoCompra().getTipoObjetoCompra(), item.getItemAdequado().getObjetoCompra().getGrupoObjetoCompra()));
            contAprovVO.getItensAprovacao().add(movoItemVO);
        }
        return contAprovVO;
    }

    private void criarFornecedorContrato(Contrato contrato) {
        contrato.setFornecedores(Lists.<FornecedorContrato>newArrayList());
        FornecedorContrato novoFornecedorCont = new FornecedorContrato();
        novoFornecedorCont.setContrato(contrato);
        novoFornecedorCont.setFornecedor(contrato.getContratado());
        novoFornecedorCont.setResponsavelFornecedor(contrato.getResponsavelEmpresa());
        Util.adicionarObjetoEmLista(contrato.getFornecedores(), novoFornecedorCont);
    }

    private void criarUnidadeContrato(Contrato contrato) {
        contrato.setUnidades(Lists.<UnidadeContrato>newArrayList());
        UnidadeContrato unidadeContrato = new UnidadeContrato();
        unidadeContrato.setContrato(contrato);
        unidadeContrato.setUnidadeAdministrativa(contrato.getUnidadeAdministrativa().getSubordinada());
        unidadeContrato.setInicioVigencia(DataUtil.dataSemHorario(contrato.getInicioVigencia()).compareTo(DataUtil.dataSemHorario(contrato.getDataLancamento())) > 0
            ? contrato.getDataLancamento() : contrato.getInicioVigencia());
        Util.adicionarObjetoEmLista(contrato.getUnidades(), unidadeContrato);
    }

    public Contrato deferirContrato(Contrato contrato) {
        contrato.setSituacaoContrato(SituacaoContrato.DEFERIDO);
        contrato = em.merge(contrato);
        salvarPortal(contrato);
        return contrato;
    }

    private void salvarPortal(Contrato entity) {
        portalTransparenciaNovoFacade.salvarPortal(new ContratoPortal(entity));
    }

    private void gerarNumeroContrato(Contrato contrato) {
        if (contrato.getNumeroContrato() == null) {
            int ano = DataUtil.getAno(contrato.getDataLancamento());
            Exercicio exercicio = exercicioFacade.getExercicioPorAno(ano);
            contrato.setNumeroContrato(singletonContrato.getProximoNumeroContrato(exercicio));
        }
    }

    public String gerarNumeroTermoContrato(Contrato contrato) {
        String numeroTermo = "";
        if (contrato.getDataAprovacao() != null) {
            int ano = DataUtil.getAno(contrato.getDataAprovacao());
            Exercicio exercicio = exercicioFacade.getExercicioPorAno(ano);
            contrato.setExercicioContrato(exercicio);
            if (contrato.isContratoAprovadoAte2020()) {
                numeroTermo = singletonContrato.getProximoNumeroTermoAte2020(contrato);
            } else {
                numeroTermo = singletonContrato.getProximoNumeroTermo(contrato);
            }
        }
        ContratoValidacao contratoValid = new ContratoValidacao(contrato);
        validarNumeroTermoExistente(contratoValid);
        return numeroTermo;
    }

    public boolean hasSolicitacaoEmpenhoEmpenhada(Contrato contrato) {
        String sql = " select e.* " +
            "   from execucaocontrato ec " +
            "  inner join execucaocontratoempenho ece on ece.execucaocontrato_id = ec.id " +
            "  inner join solicitacaoempenho se on se.id = ece.solicitacaoempenho_id " +
            "  inner join empenho e on e.id = se.empenho_id " +
            "  left join empenhoestorno es on es.empenho_id = e.id " +
            "  where ec.contrato_id = :id_contrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_contrato", contrato.getId());
        return !q.getResultList().isEmpty();
    }

    @Override
    public void remover(Contrato entity) {
        validarExclusaoContrato(entity);
        List<AlteracaoContratual> alteracoesCont = alteracaoContratualFacade.buscarAlteracoesContratuaisPorContrato(entity);
        alteracoesCont.forEach(alt ->em.remove(em.find(AlteracaoContratual.class, alt.getId())));
        super.remover(entity);
    }

    public void validarExclusaoContrato(Contrato contrato) {
        ValidacaoException ve = new ValidacaoException();
        if (hasSolicitacaoEmpenhoEmpenhada(contrato)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe solicitações de empenho já empenhadas para este contrato.");
        }
        ve.lancarException();
    }

    public void validarDataAprovacao(ContratoValidacao contratoValidacao) {
        ValidacaoException ve = new ValidacaoException();
        if (contratoValidacao.getDataAprovacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de aprovação deve ser informado.");
        }
        if (!contratoValidacao.getContrato().isContratoAprovadoAte2020(contratoValidacao.getDataAprovacao()) && Strings.isNullOrEmpty(contratoValidacao.getContrato().getNumeroAnoTermo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo número do termo deve ser informado.");
        }
        ve.lancarException();

        Integer anoAprovacao = DataUtil.getAno(contratoValidacao.getDataAprovacao());
        if (!contratoValidacao.getContrato().isContratoAprovadoAte2020(contratoValidacao.getDataAprovacao())) {
            if (!contratoValidacao.getExercicioContrato().getAno().equals(anoAprovacao)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano do Termo deve ser igual ao Ano da Data de Aprovação.");
            }
            if (contratoValidacao.getDataAssinatura() != null) {
                if (DataUtil.dataSemHorario(contratoValidacao.getDataAprovacao()).after(DataUtil.dataSemHorario(contratoValidacao.getDataAssinatura()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data de aprovação do contrato (" + DataUtil.getDataFormatada(contratoValidacao.getDataAprovacao()) + ") " +
                        " deve ser menor ou igual a data de assinatura do contrato (" + DataUtil.getDataFormatada(contratoValidacao.getDataAssinatura()) + ").");
                }
                Integer anoAssinatura = DataUtil.getAno(contratoValidacao.getDataAssinatura());
                if (!anoAprovacao.equals(anoAssinatura)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano da Data de Aprovação do contrato deve ser igual ao Ano da Data de Assinatura.");
                }
                if (!contratoValidacao.getExercicioContrato().getAno().equals(anoAssinatura)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano da Data de Assinatura do contrato deve ser igual ao ano do termo.");
                }
            }
        }
        if (!contratoValidacao.getContrato().isContratoDeferido()) {
            Date dataPrimeiraExecucao = execucaoContratoFacade.buscarDataPrimeiraExecucaoContrato(contratoValidacao.getContrato());
            if (dataPrimeiraExecucao != null && DataUtil.dataSemHorario(contratoValidacao.getDataAprovacao()).after(DataUtil.dataSemHorario(dataPrimeiraExecucao))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de aprovação " + DataUtil.getDataFormatada(contratoValidacao.getDataAprovacao()) + " é posterior a data da primeira execução do contrato: " + DataUtil.getDataFormatada(dataPrimeiraExecucao) + ".");
                ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, ". Caso essa informação não estiver correta,  solicite a correção das datas.");
            }
        }
        ve.lancarException();
    }

    public void validarDataDeferimentoContrato(ContratoValidacao contratoValidacao) {
        ValidacaoException ve = new ValidacaoException();
        if (contratoValidacao.getDataAssinatura() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de assinatura deve ser informado.");
        }
        if (contratoValidacao.getDataAssinatura() != null && DataUtil.isSabadoDomingo(contratoValidacao.getDataAssinatura())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de assinatura do contrato não pode ser realizada em um Sábado ou Domingo. Por favor, mude-a para um dia de semana.");
        }
        ve.lancarException();

        Integer anoAprovacao = DataUtil.getAno(contratoValidacao.getDataAprovacao());
        if (!contratoValidacao.getContrato().isContratoAprovadoAte2020(contratoValidacao.getDataAprovacao())) {
            if (!contratoValidacao.getExercicioContrato().getAno().equals(anoAprovacao)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano do Termo do contrato deve ser igual ao Ano da Data de Aprovação.");
            }
            if (contratoValidacao.getDataAssinatura() != null) {
                if (DataUtil.dataSemHorario(contratoValidacao.getDataAprovacao()).after(DataUtil.dataSemHorario(contratoValidacao.getDataAssinatura()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data de aprovação do contrato deve ser menor ou igual a data de assinatura.");
                }
                Integer anoAssinatura = DataUtil.getAno(contratoValidacao.getDataAssinatura());
                if (!anoAprovacao.equals(anoAssinatura)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano da Data de Aprovação do contrato deve ser igual ao Ano da Data de Assinatura.");
                }
                if (!contratoValidacao.getExercicioContrato().getAno().equals(anoAssinatura)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano do Termo do contrato deve ser igual ao Ano da Data de Assinatura.");
                }
            }
        }

        if (contratoValidacao.getDataAssinatura().before(DataUtil.dataSemHorario(contratoValidacao.getDataAprovacao()))
            || contratoValidacao.getDataAssinatura().after(DataUtil.dataSemHorario(contratoValidacao.getDataDeferimento()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de assinatura do contrato deve estar entre a aprovação e o deferimento do contrato.");
        }
        ve.lancarException();
    }

    public void diminuirSaldoAtualContrato(BigDecimal valor, Contrato contrato) {
        contrato.setSaldoAtualContrato(contrato.getSaldoAtualContrato().subtract(valor));
        em.merge(contrato);
    }

    public void aumentarSaldoAtualContrato(BigDecimal valor, Contrato contrato) {
        contrato.setSaldoAtualContrato(contrato.getSaldoAtualContrato().add(valor));
        em.merge(contrato);
    }

    public List<Contrato> buscarContratoPorAtaAndUnidade(UnidadeOrganizacional unidade, AtaRegistroPreco ataRegistroPreco) {
        String sql = " select distinct c.* from contrato c " +
            " inner join unidadecontrato uc on uc.contrato_id = c.id " +
            " inner join conlicitacao cc on cc.contrato_id = c.id " +
            " inner join licitacao lic on lic.id = cc.licitacao_id " +
            " inner join ataregistropreco ata on ata.licitacao_id  = lic.id " +
            " inner join solicitacaomaterialext sol on sol.ataregistropreco_id = ata.id " +
            " where ata.id = :idAta " +
            " and uc.unidadeadministrativa_id = :idUnidade ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("idAta", ataRegistroPreco.getId());
        q.setParameter("idUnidade", unidade.getId());
        return q.getResultList();
    }

    public List<Object[]> buscarContratos(Exercicio exercicio) {
        String sql = " select cont.numeroTermo, " +
            "       cont.objeto as descricaoContrato, " +
            "       p.id " +
            "  from contrato cont " +
            " inner join pessoa p on cont.contratado_id = p.id " +
            "  left join pessoafisica pf on p.id = pf.id " +
            "  left join pessoajuridica pj on p.id = pj.id " +
            " where cont.EXERCICIOCONTRATO_ID = :exercicio " +
            " order by cont.numeroTermo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<Contrato> buscarContratoPorPeriodo(Date dataInicial, Date dataFinal) {
        String sql = " select c.* from contrato c " +
            "  where trunc(c.datalancamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy')" +
            "  order by c.datalancamento ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        return q.getResultList();
    }

    public List<Object[]> buscarFornecedores(Exercicio exercicio) {
        String sql = " select distinct id, " +
            "        pessoa, " +
            "        cpfCnpj, " +
            "        endereco " +
            "   from ( " +
            " select distinct p.id, " +
            "       coalesce(pf.nome, pj.razaosocial) as pessoa, " +
            "       coalesce(pf.cpf, pj.cnpj) as cpfCnpj, " +
            "       coalesce(end.logradouro, '') || ', nº ' || " +
            "       coalesce(end.numero, '') || ', ' || " +
            "       coalesce(end.complemento, '')  || ', ' || " +
            "       coalesce(end.bairro, '') || ', ' || " +
            "       coalesce(end.localidade, '')  || ', ' || " +
            "       coalesce(end.uf, '')   as endereco  " +
            "  from contrato cont " +
            " inner join pessoa p on cont.contratado_id = p.id " +
            "  left join pessoafisica pf on p.id = pf.id " +
            "  left join pessoajuridica pj on p.id = pj.id " +
            "  left join enderecocorreio end on end.id = p.ENDERECOPRINCIPAL_ID " +
            " where cont.exercicioContrato_id = :exercicio " +
            " union all " +
            "select distinct p.id, " +
            "       coalesce(pf.nome, pj.razaosocial) as pessoa, " +
            "       coalesce(pf.cpf, pj.cnpj) as cpfCnpj, " +
            "       coalesce(end.logradouro, '') || ', nº ' || " +
            "       coalesce(end.numero, '') || ', ' || " +
            "       coalesce(end.complemento, '')  || ', ' || " +
            "       coalesce(end.bairro, '') || ', ' || " +
            "       coalesce(end.localidade, '')  || ', ' || " +
            "       coalesce(end.uf, '')   as endereco  " +
            "  from empenho emp " +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            "  left join pessoafisica pf on p.id = pf.id " +
            "  left join pessoajuridica pj on p.id = pj.id " +
            "  left join enderecocorreio end on end.id = p.ENDERECOPRINCIPAL_ID " +
            " where emp.EXERCICIO_ID = :exercicio " +
            "   and emp.CATEGORIAORCAMENTARIA = :categoria " +
            " union all " +
            "select distinct p.id, " +
            "       coalesce(pf.nome, pj.razaosocial) as pessoa, " +
            "       coalesce(pf.cpf, pj.cnpj) as cpfCnpj, " +
            "       coalesce(end.logradouro, '') || ', nº ' || " +
            "       coalesce(end.numero, '') || ', ' || " +
            "       coalesce(end.complemento, '')  || ', ' || " +
            "       coalesce(end.bairro, '') || ', ' || " +
            "       coalesce(end.localidade, '')  || ', ' || " +
            "       coalesce(end.uf, '')   as endereco  " +
            "  from empenhoestorno est " +
            " inner join empenho emp on est.empenho_id = emp.id" +
            " inner join pessoa p on emp.fornecedor_id = p.id " +
            "  left join pessoafisica pf on p.id = pf.id " +
            "  left join pessoajuridica pj on p.id = pj.id " +
            "  left join enderecocorreio end on end.id = p.ENDERECOPRINCIPAL_ID " +
            " where est.EXERCICIO_ID = :exercicio " +
            "   and est.CATEGORIAORCAMENTARIA = :categoria)  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL.name());
        return q.getResultList();
    }

    public List<Contrato> buscarContratoLicitacao(Licitacao licitacao) {
        String sql = " select c.* from contrato c " +
            "          inner join conlicitacao con on con.contrato_id = c.id " +
            "          where con.licitacao_id = :idLicitacsao ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("idLicitacsao", licitacao.getId());
        return q.getResultList();
    }

    public List<Contrato> buscarContratoDispensa(DispensaDeLicitacao dispensa) {
        String sql = " select c.* from contrato c " +
            "          inner join condispensalicitacao con on con.contrato_id = c.id " +
            "          where con.dispensadelicitacao_id = :idDispensa ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("idDispensa", dispensa.getId());
        return q.getResultList();
    }

    public List<Contrato> buscarContratoRegistroPrecoExterno(SolicitacaoMaterialExterno solMatExterna) {
        String sql = "select c.* from contrato c " +
            "         inner join conregprecoexterno con on con.contrato_id = c.id " +
            "         inner join registrosolmatext reg on reg.id = con.regsolmatext_id " +
            "         inner join solicitacaomaterialext sol on sol.id = reg.solicitacaomaterialexterno_id " +
            "where sol.id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("idSolicitacao", solMatExterna.getId());
        return q.getResultList();
    }

    public List<Contrato> buscarContratoPorLicitacaoAndUnidade(Licitacao licitacao, UnidadeOrganizacional unidadeOrganizacional) {
        String sql;
        sql = " " +
            " select c.* from contrato c " +
            "   inner join conlicitacao conlic on conlic.contrato_id = c.id " +
            "   inner join licitacao lic on lic.id = conlic.licitacao_id " +
            "   inner join unidadecontrato uc on uc.contrato_id = c.id  " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia),to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and lic.id = :idLicitacsao " +
            "   and uc.unidadeadministrativa_id = :idUnidade";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("idLicitacsao", licitacao.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public Long recuperarIdRevisaoAuditoriaContrato(Long idContrato) {
        String sql = " select rev.id from contrato_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from contrato_aud caud where id = :idContrato and caud.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", idContrato);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Pessoa> completarFiscalContrato(String parte) {
        String sql = " select distinct p.id, coalesce(pf.cpf || ' - ' || pf.nome, pj.cnpj || ' - '|| pj.razaosocial), case when pf.id is not null then 'FISICA' else 'JURIDICA' end as tipoPessoa from fiscalcontrato f " +
            " left join fiscalexternocontrato fe on fe.id = f.id " +
            " left join contrato c on c.id = fe.contratofiscal_id " +
            " left join fiscalinternocontrato fi on fi.id = f.id " +
            " left join contratofp cfp on cfp.id = fi.servidor_id " +
            " left join pessoafisica pfis on pfis.id = fi.servidorpf_id " +
            " left join vinculofp v on v.id = cfp.id " +
            " left join matriculafp m on m.id = v.matriculafp_id " +
            " inner join pessoa p on p.id = coalesce(c.contratado_id, m.pessoa_id, pfis.id) " +
            " left join pessoafisica pf on pf.id = p.id " +
            " left join pessoajuridica pj on pj.id = p.id " +
            " where (lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or lower(translate(pj.razaosocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :parte " +
            "           or pf.cpf like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte " +
            "           or pj.cnpj like :parte) " +
            " and :dataOperacao between coalesce(v.iniciovigencia, :dataOperacao) and coalesce(v.finalvigencia, :dataOperacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("dataOperacao", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List<Object[]> retorno = q.getResultList();
        if (Objects.isNull(retorno)) {
            return Lists.newArrayList();
        }
        List<Pessoa> pessoas = Lists.newArrayList();
        retorno.forEach(pessoaComTipo -> {
            BigDecimal id = (BigDecimal) pessoaComTipo[0];
            if ("FISICA".equals(pessoaComTipo[2])) {
                pessoas.add(new PessoaFisica(id.longValue(), (String) pessoaComTipo[1]));
            } else {
                pessoas.add(new PessoaJuridica(id.longValue(), (String) pessoaComTipo[1]));
            }
        });
        return pessoas;
    }

    public List<Pessoa> completarGestorContrato(String parte) {
        String sql = " select distinct p.id, pf.cpf || ' - ' || pf.nome from gestorcontrato g " +
            "left join contratofp c on c.id = g.servidor_id " +
            "left join pessoafisica pf on pf.id = g.servidorpf_id " +
            "left join vinculofp v on v.id = c.id " +
            "left join matriculafp m on m.id = v.matriculafp_id " +
            "left join pessoa p on p.id = m.pessoa_id " +
            "left join pessoafisica pf on pf.id = p.id" +
            " where (lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :parte " +
            "           or pf.cpf like :parte) " +
            " and :dataOperacao between coalesce(v.iniciovigencia, :dataOperacao) and coalesce(v.finalvigencia, :dataOperacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("dataOperacao", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List<Object[]> retorno = q.getResultList();
        if (Objects.isNull(retorno)) {
            return Lists.newArrayList();
        }
        List<Pessoa> pessoas = Lists.newArrayList();
        retorno.forEach(pessoa -> {
            BigDecimal id = (BigDecimal) pessoa[0];
            pessoas.add(new PessoaFisica(id.longValue(), (String) pessoa[1]));
        });
        return pessoas;
    }

    public List<FiscalContratoDTO> buscarFiscaisContrato(Long contratoId) {
        if (Objects.isNull(contratoId)) {
            return Lists.newArrayList();
        }
        String sql = " select distinct f.id, coalesce(pf.cpf || ' - ' || pf.nome, pj.cnpj || ' - '|| pj.razaosocial), " +
            " case when fe.id is not null then 'EXTERNO' else 'INTERNO' end as tipoFiscal " +
            " from fiscalcontrato f " +
            " left join fiscalexternocontrato fe on fe.id = f.id " +
            " left join contrato c on c.id = fe.contratofiscal_id " +
            " left join fiscalinternocontrato fi on fi.id = f.id " +
            " left join contratofp cfp on cfp.id = fi.servidor_id " +
            " left join pessoafisica pfis on pfis.id = fi.servidorpf_id" +
            " left join vinculofp v on v.id = cfp.id " +
            " left join matriculafp m on m.id = v.matriculafp_id " +
            " inner join pessoa p on p.id = coalesce(c.contratado_id, m.pessoa_id, pfis.id) " +
            " left join pessoafisica pf on pf.id = p.id " +
            " left join pessoajuridica pj on pj.id = p.id " +
            " where f.contrato_id = :contratoId ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoId", contratoId);
        List<Object[]> retorno = q.getResultList();
        if (Objects.isNull(retorno)) {
            return Lists.newArrayList();
        }
        List<FiscalContratoDTO> fiscais = Lists.newArrayList();
        retorno.forEach(fiscalComTipo -> {
            BigDecimal id = (BigDecimal) fiscalComTipo[0];
            if ("EXTERNO".equals(fiscalComTipo[2])) {
                fiscais.add(new FiscalContratoDTO(id.longValue(), (String) fiscalComTipo[1], TipoFiscalContrato.EXTERNO));
            } else {
                fiscais.add(new FiscalContratoDTO(id.longValue(), (String) fiscalComTipo[1], TipoFiscalContrato.INTERNO));
            }
        });
        return fiscais;
    }

    public List<GestorContrato> buscarGestoresContrato(Long contratoId) {
        if (Objects.isNull(contratoId)) {
            return Lists.newArrayList();
        }
        String sql = " select * from gestorcontrato g where g.contrato_id = :contratoId";
        Query q = em.createNativeQuery(sql, GestorContrato.class);
        q.setParameter("contratoId", contratoId);
        List<GestorContrato> retorno = q.getResultList();
        if (Objects.isNull(retorno)) {
            return Lists.newArrayList();
        }
        return retorno;
    }

    public String getOrigemContrato(Contrato contrato) {
        if (contrato.isProcessoLicitatorio()) {
            String ataRegistroPreco = "";
            if (contrato.isRegistroDePrecos()) {
                AtaRegistroPreco ata = getAtaRegistroPrecoFacade()
                    .recuperarAtaRegistroPrecoPorUnidade(
                        contrato.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional(),
                        contrato.getLicitacao(),
                        null
                    );

                ataRegistroPreco = (ata != null) ? "Ata Registro de Preço Nº " + ata.getNumero() + " - " : "";
            }
            return ataRegistroPreco + contrato.getLicitacao().getModalidadeLicitacao() + " " + contrato.getLicitacao().getNaturezaDoProcedimento() + " " + contrato.getLicitacao().toStringNumeroExercicio();
        }
        if (contrato.isDeDispensaDeLicitacao()) {
            return contrato.getDispensaDeLicitacao().getModalidade() + " " + contrato.getDispensaDeLicitacao();
        }
        if (contrato.isDeRegistroDePrecoExterno()) {
            return contrato.getRegistroSolicitacaoMaterialExterno().getModalidadeCarona() + " " + contrato.getRegistroSolicitacaoMaterialExterno().getTipoModalidade() + " " + contrato.getRegistroSolicitacaoMaterialExterno();
        }
        if (contrato.isDeProcedimentosAuxiliares()) {
            return "Credenciamento " + contrato.getLicitacao();
        }
        return "";
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public AgenciaFacade getAgenciaFacade() {
        return agenciaFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public ModeloDocumentoContratoFacade getModeloDocumentoContratoFacade() {
        return modeloDocumentoContratoFacade;
    }

    public RegistroSolicitacaoMaterialExternoFacade getRegistroSolicitacaoMaterialExternoFacade() {
        return registroSolicitacaoMaterialExternoFacade;
    }

    public ItemContratoFacade getItemContratoFacade() {
        return itemContratoFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public DocumentosNecessariosAnexoFacade getDocumentosNecessariosAnexoFacade() {
        return documentosNecessariosAnexoFacade;
    }


    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public ParticipanteIRPFacade getParticipanteIRPFacade() {
        return participanteIRPFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public SolicitacaoMaterialExternoFacade getSolicitacaoMaterialExternoFacade() {
        return solicitacaoMaterialExternoFacade;
    }

    public ExecucaoContratoEstornoFacade getExecucaoContratoEstornoFacade() {
        return execucaoContratoEstornoFacade;
    }

    public LancamentoDebitoContratoFacade getLancamentoDebitoContratoFacade() {
        return lancamentoDebitoContratoFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public OrdemCompraFacade getOrdemCompraFacade() {
        return ordemCompraFacade;
    }

    public AjusteContratoFacade getAjusteContratoFacade() {
        return ajusteContratoFacade;
    }

    public SaldoItemContratoFacade getSaldoItemContratoFacade() {
        return saldoItemContratoFacade;
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoSolicitacaoMaterialFacade() {
        return dotacaoSolicitacaoMaterialFacade;
    }
}

