/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.entidades.comum.TipoTemplateEmail;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.DependentePortal;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.DependenteVinculoPortal;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.PessoaFisicaPortal;
import br.com.webpublico.entidadesauxiliares.SefipTrabalhadorRegistroTipo14;
import br.com.webpublico.entidadesauxiliares.rh.AverbacaoContratoPorServico;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.SituacaoPessoaPortal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.SolicitacaoCadastroPessoaFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.jdbc.AuditoriaJDBC;
import br.com.webpublico.negocios.rh.portal.PessoaFisicaPortalFacade;
import br.com.webpublico.negocios.tributario.AuditoriaFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO;
import br.com.webpublico.nfse.domain.dtos.ImagemUsuarioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import br.com.webpublico.nfse.domain.dtos.RegisterNfseDTO;
import br.com.webpublico.nfse.util.ArquivoUtil;
import br.com.webpublico.pessoa.dto.DependenteDTO;
import br.com.webpublico.pessoa.dto.DependenteVinculoFPDTO;
import br.com.webpublico.pessoa.dto.PessoaFisicaDTO;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.NotAudited;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Munif
 */
@Stateless
public class PessoaFacade extends AbstractFacade<Pessoa> {

    private static final Logger logger = LoggerFactory.getLogger(PessoaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private NacionalidadeFacade nacionalidadeFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private EscritorioContabilFacade escritorioContabilFacade;
    @EJB
    private CidadeFacade naturalidadeFacade;
    @EJB
    private NivelEscolaridadeFacade nivelEscolaridadeFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private CEPFacade cepFacade;
    @EJB
    private CEPLogradouroFacade cepLogradouroFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PortalContribunteFacade portalContribunteFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;
    @EJB
    private AuditoriaFacade auditoriaFacade;
    @EJB
    private PessoaFisicaPortalFacade pessoaFisicaPortalFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SolicitacaoCadastroPessoaFacade solicitacaoCadastroPessoaFacade;
    @EJB
    private ConfiguracaoPortalContribuinteFacade configuracaoPortalContribuinteFacade;

    public PessoaFacade() {
        super(Pessoa.class);
    }

    public AuditoriaFacade getAuditoriaFacade() {
        return auditoriaFacade;
    }

    public ConfiguracaoPortalContribuinteFacade getConfiguracaoPortalContribuinteFacade() {
        return configuracaoPortalContribuinteFacade;
    }

    public SolicitacaoCadastroPessoaFacade getSolicitacaoCadastroPessoaFacade() {
        return solicitacaoCadastroPessoaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    public Pessoa recuperarSemDependencias(Object id) {
        return em.find(Pessoa.class, id);
    }

    @Override
    public Pessoa recuperar(Object id) {
        Pessoa p = em.find(Pessoa.class, id);
        inicializarAtributosLazy(p);
        return p;
    }

    public void inicializarAtributosLazy(Pessoa p) {
        if (p != null) {
            Hibernate.initialize(p.getEnderecos());
            Hibernate.initialize(p.getTelefones());
            Hibernate.initialize(p.getPerfis());
            Hibernate.initialize(p.getClasseCredorPessoas());
            Hibernate.initialize(p.getContaCorrenteBancPessoas());
            Hibernate.initialize(p.getClassificacaoCredores());
            Hibernate.initialize(p.getUsuariosWeb());
            Hibernate.initialize(p.getHistoricoSituacoesPessoa());
            Hibernate.initialize(p.getHorariosFuncionamento());
            for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : p.getHorariosFuncionamento()) {
                Hibernate.initialize(pessoaHorarioFuncionamento.getHorarioFuncionamento().getItens());
            }
            if (p instanceof PessoaFisica) {
                Hibernate.initialize(((PessoaFisica) p).getDocumentosPessoais());
            }
            if (p instanceof PessoaJuridica) {
                Hibernate.initialize(((PessoaJuridica) p).getPessoaCNAE());
            }
        }
    }

    public Pessoa recuperarSimples(Object id) {
        Pessoa p = em.find(Pessoa.class, id);
        p.getEnderecos().size();
        return p;
    }

    public Pessoa recuperarComPerfil(Object id) {
        Pessoa p = em.find(Pessoa.class, id);
        Hibernate.initialize(p.getPerfis());
        return p;
    }

    /**
     * retorna todas as pessoas filtrando por nome, cpf, razão social, nome fantasia ou cnpj e TODAS situações
     *
     * @param filtro String like nome, cpf, razão social ou cnpj
     * @return List de Pessoa
     */

    public List<Pessoa> listaTodasPessoas(String filtro) {
        return listaTodasPessoas(filtro, SituacaoCadastralPessoa.values());
    }

    public List<Pessoa> listaTodasPessoasAtivas(String filtro) {
        return listaTodasPessoas(filtro, SituacaoCadastralPessoa.ATIVO);
    }


    public List<Pessoa> listaTodasPessoasInativasESuspensas(String filtro) {
        return listaTodasPessoas(filtro, SituacaoCadastralPessoa.INATIVO, SituacaoCadastralPessoa.SUSPENSO, SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
    }

    public List<Pessoa> listaTodasPessoasPorCPFCNPJOuEmail(String cpfCnpjEmail) {
        ArrayList<Pessoa> retorno = Lists.newArrayList();
        retorno.addAll(listaPessoasFisicasPorCpfEmail(cpfCnpjEmail));
        retorno.addAll(listaPessoasJuridicasPorCnpjEmail(cpfCnpjEmail));
        return retorno;
    }

    private List<PessoaFisica> listaPessoasFisicasPorCpfEmail(String cpfEmail) {
        String hql = "select pf from PessoaFisica pf " +
            " join pf.usuariosPortalWeb usu " +
            " where (REGEXP_REPLACE(pf.cpf, '[^0-9]') = REGEXP_REPLACE(:cpf, '[^0-9]') " +
            " or usu.email = :email)";
        Query q = em.createQuery(hql);
        q.setParameter("cpf", cpfEmail);
        q.setParameter("email", cpfEmail);
        q.setMaxResults(10);
        Set<PessoaFisica> pessoas = new HashSet<>(q.getResultList());
        for (Pessoa pessoa : pessoas) {
            Hibernate.initialize(pessoa.getUsuariosWeb());
        }
        return new ArrayList<>(pessoas);
    }

    private List<PessoaJuridica> listaPessoasJuridicasPorCnpjEmail(String cnpjEmail) {
        String hql = "select pj from PessoaJuridica pj " +
            " join pj.usuariosPortalWeb usu " +
            " where (REGEXP_REPLACE(pj.cnpj, '[^0-9]') = REGEXP_REPLACE(:cnpj, '[^0-9]') " +
            " or usu.email = :email)";
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", cnpjEmail);
        q.setParameter("email", cnpjEmail);
        q.setMaxResults(10);
        Set<PessoaJuridica> pessoas = new HashSet<>(q.getResultList());
        for (Pessoa pessoa : pessoas) {
            Hibernate.initialize(pessoa.getUsuariosWeb());
        }
        return new ArrayList<>(pessoas);
    }

    /**
     * retorna todas as pessoas filtrando por nome, cpf, razão social ou cnpj e situações
     *
     * @param filtro    String like nome, cpf, razão social ou cnpj
     * @param situacoes Array de SituacaoCadastralPessoa
     * @return List de Pessoa
     */
    public List<Pessoa> listaTodasPessoas(String filtro, SituacaoCadastralPessoa... situacoes) {
        ArrayList<Pessoa> retorno = Lists.newArrayList();
        retorno.addAll(listaPessoasFisicas(filtro, situacoes));
        retorno.addAll(listaPessoasJuridicas(filtro, situacoes));
        return retorno;
    }


    public List<Pessoa> listaTodasPessoasPorCPFCNPJ(String filtro) {
        return listaTodasPessoasPorCPFCNPJ(filtro, SituacaoCadastralPessoa.values());
    }

    public List<Pessoa> listaTodasPessoasPorCPFCNPJ(String filtro, SituacaoCadastralPessoa... situacoes) {
        ArrayList<Pessoa> retorno = Lists.newArrayList();
        retorno.addAll(listaPessoasFisicasPorCpf(filtro, situacoes));
        retorno.addAll(listaPessoasJuridicasPorCnpj(filtro, situacoes));
        return retorno;
    }


    public List<Pessoa> listaTodasPessoasPorId(String filtro) {
        Long id;
        try {
            id = Long.parseLong(filtro);
        } catch (Exception e) {
            id = 0l;
        }
        Query q;
        ArrayList<Pessoa> retorno = new ArrayList<>();
        q = getEntityManager().createQuery("from PessoaFisica obj where obj.id = :filtroId or lower(obj.nome) like :filtro or replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like replace(replace(replace(:filtro,'.',''),'-',''),'/','') ");
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());
        q = getEntityManager().createQuery("from PessoaJuridica obj where obj.id = :filtroId or lower(obj.razaoSocial) like :filtro or replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like replace(replace(replace(:filtro,'.',''),'-',''),'/','') or lower(obj.nomeFantasia) like :filtro");
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());
        return retorno;
    }

    /**
     * retorna todas as pessoas filtrando por nome, cpf TODAS situações
     *
     * @param filtro String like nome, cpf
     * @return List de Pessoa
     */

    public List<Pessoa> listaPessoasFisicas(String filtro) {
        return listaPessoasFisicas(filtro, SituacaoCadastralPessoa.values());
    }

    public List<Object[]> buscarTodasAsPessoasFisicasAtivas() {
        String sql = " select pf.cpf as cpfCnpj,  " +
            "       replace(coalesce(pf.nome,'NOME NÃO INFORMADO'),';',' ') as nome, " +
            "       p.id as crc, " +
            "       replace(replace(replace(replace(trim(coalesce(end.logradouro, '')),';',''),'=',''),'-',''),'  ',' ') as logradouro,  " +
            "       trim(coalesce(end.localidade, '')) as localidade, " +
            "        trim(coalesce(end.uf, '')) as uf, " +
            "       trim(coalesce(end.bairro, '')) as bairro, " +
            "       trim(REGEXP_REPLACE(COALESCE(end.complemento, ''), '( ){2,}', ' ')) as complemento," +
            "       REGEXP_REPLACE(coalesce(end.numero, ''), '[^0-9]', '') as numero  " +
            "  from pessoafisica pf " +
            " inner join pessoa p on pf.id = p.id " +
            "  left join enderecocorreio end on end.id = p.ENDERECOPRINCIPAL_ID " +
            " where p.situacaoCadastralPessoa = :situacao " +
            "   and pf.cpf is not null ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO.name());
        return q.getResultList();
    }

    public List<Object[]> buscarTodasAsPessoasJuridicasAtivas() {
        String sql = " select pj.cnpj as cpfCnpj,  " +
            "       replace(coalesce(pj.razaosocial,'NOME NÃO INFORMADO'),';',' ') as nome, " +
            "       p.id as crc, " +
            "       replace(replace(replace(replace(trim(coalesce(end.logradouro, '')),';',''),'=',''),'-',''),'  ',' ') as logradouro,  " +
            "       trim(coalesce(end.localidade, '')) as localidade, " +
            "       trim(coalesce(end.uf, '')) as uf, " +
            "       trim(coalesce(end.bairro, '')) as bairro, " +
            "       trim(REGEXP_REPLACE(COALESCE(end.complemento, ''), '( ){2,}', ' ')) as complemento," +
            "       trim(REGEXP_REPLACE(coalesce(end.numero, ''), '[^0-9]', '')) as numero  " +
            "  from PessoaJuridica pj " +
            " inner join pessoa p on pj.id = p.id " +
            "  left join enderecocorreio end on end.id = p.ENDERECOPRINCIPAL_ID " +
            " where p.situacaoCadastralPessoa = :situacao " +
            "   and pj.cnpj is not null ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO.name());
        return q.getResultList();
    }

    /**
     * retorna todas as pessoas filtrando por nome, cpf e situações
     *
     * @param filtro    String like nome, cpf
     * @param situacoes Array de SituacaoCadastralPessoa
     * @return List de Pessoa
     */
    public List<Pessoa> listaPessoasFisicas(String filtro, SituacaoCadastralPessoa... situacoes) {
        List<Pessoa> lista = Lists.newArrayList();
        if (situacoes == null) {
            situacoes = SituacaoCadastralPessoa.values();
        }
        List<String> filtros = Lists.newArrayList(filtro.split("\\s+"));
        String sql = "select obj from PessoaFisica obj where ";
        sql += " (lower(translate(obj.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ";
        sql += " or replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :cpfCnpj)";
        if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
            sql += " and obj.situacaoCadastralPessoa in (:situacoes)";
        }
        Query q = em.createQuery(sql);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("cpfCnpj", "%" + filtro.toLowerCase().replace(".", "").replace("-", "") + "%");
        if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
            q.setParameter("situacoes", Lists.newArrayList(situacoes));
        }
        lista.addAll(q.setMaxResults(10).getResultList());
        if (lista.isEmpty() || lista.size() < 10) {
            sql = "select obj from PessoaFisica obj where ";
            for (String f : filtros) {
                sql += "  lower(translate(obj.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro" + filtros.indexOf(f) + ",'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') and ";
            }
            sql = sql.substring(0, sql.length() - 4);
            if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
                sql += " and obj.situacaoCadastralPessoa in (:situacoes)";
            }
            if (!lista.isEmpty()) {
                sql += " and obj not in (:pessoas)";
            }
            q = em.createQuery(sql);
            for (String f : filtros) {
                q.setParameter("filtro" + filtros.indexOf(f), "%" + f.toLowerCase() + "%");
            }
            if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
                q.setParameter("situacoes", Lists.newArrayList(situacoes));
            }
            if (!lista.isEmpty()) {
                q.setParameter("pessoas", lista);
            }
            lista.addAll(q.setMaxResults(10 - lista.size()).getResultList());
        }
        return lista;
    }


    public List<PessoaFisica> retornaPessoasFisicas(String where) {
        List<PessoaFisica> retorno = new ArrayList<>();
        Query q = getEntityManager().createQuery(" from PessoaFisica obj " + where.trim());
        q.setMaxResults(50);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }
        return retorno;
    }

    /**
     * retorna todas as pessoas filtrando por nome, cpf TODAS situações
     *
     * @param filtro String like razaoSocial, CNPJ, nomeFantasia
     * @return List de Pessoa
     */

    public List<Pessoa> listaPessoasJuridicas(String filtro) {
        return listaPessoasJuridicas(filtro, SituacaoCadastralPessoa.values());
    }

    /**
     * retorna todas as pessoas filtrando por nome, cpf e situações
     *
     * @param filtro    String like razaoSocial, CNPJ, nomeFantasia
     * @param situacoes Array de SituacaoCadastralPessoa
     * @return List de Pessoa
     */
    public List<Pessoa> listaPessoasJuridicas(String filtro, SituacaoCadastralPessoa... situacoes) {
        List<Pessoa> lista = Lists.newArrayList();
        if (situacoes == null) {
            situacoes = SituacaoCadastralPessoa.values();
        }
        List<String> filtros = Lists.newArrayList(filtro.split("\\s+"));
        String sql = "select obj from PessoaJuridica obj where ";
        sql += " (lower(translate(obj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ";
        sql += " or lower(translate(obj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') ";
        sql += " or replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cpfCnpj)";
        if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
            sql += " and obj.situacaoCadastralPessoa in (:situacoes)";
        }
        Query q = em.createQuery(sql);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("cpfCnpj", "%" + filtro.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
            q.setParameter("situacoes", Lists.newArrayList(situacoes));
        }
        lista.addAll(q.setMaxResults(10).getResultList());
        if (lista.isEmpty() || lista.size() < 10) {
            sql = "select obj from PessoaJuridica obj where ";
            for (String f : filtros) {
                sql += "  (lower(translate(obj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro" + filtros.indexOf(f) + ",'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') or ";
                sql += "  lower(translate(obj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro" + filtros.indexOf(f) + ",'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) and ";
            }
            sql = sql.substring(0, sql.length() - 4);
            if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
                sql += " and obj.situacaoCadastralPessoa in (:situacoes)";
            }
            if (!lista.isEmpty()) {
                sql += " and obj not in (:pessoas)";
            }
            q = em.createQuery(sql);
            for (String f : filtros) {
                q.setParameter("filtro" + filtros.indexOf(f), "%" + f.toLowerCase() + "%");
            }
            if (situacoes != null && situacoes.length > 0 && situacoes[0] != null) {
                q.setParameter("situacoes", Lists.newArrayList(situacoes));
            }
            if (!lista.isEmpty()) {
                q.setParameter("pessoas", lista);
            }
            lista.addAll(q.setMaxResults(10 - lista.size()).getResultList());
        }
        return lista;
    }

    public List<PessoaJuridica> retornaPessoasJuridicas(String where) {
        List<PessoaJuridica> retorno = new ArrayList<>();
        Query q = getEntityManager().createQuery("from PessoaJuridica obj " + where.trim());
        q.setMaxResults(50);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }
        return retorno;
    }

    //Pessoa Fisica
    public Pessoa recuperarPF(Object id) {
        PessoaFisica pf = em.find(PessoaFisica.class, id);
        Hibernate.initialize(pf.getTelefones());
        Hibernate.initialize(pf.getDocumentosPessoais());
        Hibernate.initialize(pf.getEnderecos());
        Hibernate.initialize(pf.getDependentes());
        Hibernate.initialize(pf.getEnderecoscorreio());
        Hibernate.initialize(pf.getContaCorrenteBancPessoas());
        Hibernate.initialize(pf.getPerfis());
        Hibernate.initialize(pf.getRepresentantesLegal());
        Hibernate.initialize(pf.getClasseCredorPessoas());
        Hibernate.initialize(pf.getClassificacaoCredores());
        Hibernate.initialize(pf.getConselhoClasseContratos());
        Hibernate.initialize(pf.getHabilidades());
        Hibernate.initialize(pf.getFormacoes());
        Hibernate.initialize(pf.getUsuariosWeb());
        Hibernate.initialize(pf.getHistoricoSituacoesPessoa());
        Hibernate.initialize(pf.getPessoaCNAE());
        Hibernate.initialize(pf.getHorariosFuncionamento());
        Hibernate.initialize(pf.getItemTempoContratoFPPessoa());
        for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : pf.getHorariosFuncionamento()) {
            Hibernate.initialize(pessoaHorarioFuncionamento.getHorarioFuncionamento().getItens());
        }
        Hibernate.initialize(pf.getItemTempoContratoFPPessoa());
        if (pf.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(pf.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return pf;
    }

    @Override
    public Pessoa recarregar(Pessoa entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Object obj = new Object();
        if (entity instanceof PessoaFisica) {
            obj = recuperarPF(chave);
        } else if (entity instanceof PessoaJuridica) {
            obj = recuperarPJ(chave);
        }

        return (Pessoa) obj;
    }

    public boolean hasOutraPessoaComMesmoCpf(PessoaFisica p, boolean somenteAtiva) {
        String hql = " from PessoaFisica p " +
            " where REGEXP_REPLACE(p.cpf, '[^0-9]+', '') = REGEXP_REPLACE(:cpf, '[^0-9]+', '') ";
        if (p.getId() != null) {
            hql += " and p != :pessoa ";
        }
        if (somenteAtiva) {
            hql += " and p.situacaoCadastralPessoa in (:ativo, :aguardando) ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("cpf", p.getCpf());
        if (p.getId() != null) {
            q.setParameter("pessoa", p);
        }
        if (somenteAtiva) {
            q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
            q.setParameter("aguardando", SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
        }
        return !q.getResultList().isEmpty();
    }


    public boolean hasOutraPessoaComMesmoCnpj(PessoaJuridica p, boolean somenteAtiva) {
        String hql = " from PessoaJuridica p " +
            " where REGEXP_REPLACE(p.cnpj, '[^0-9]+', '') = REGEXP_REPLACE(:cnpj, '[^0-9]+', '') ";
        if (p.getId() != null) {
            hql += " and p != :pessoa ";
        }
        if (somenteAtiva) {
            hql += " and p.situacaoCadastralPessoa in (:ativo, :aguardando) ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", p.getCnpj());
        if (p.getId() != null) {
            q.setParameter("pessoa", p);
        }
        if (somenteAtiva) {
            q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
            q.setParameter("aguardando", SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean hasMaisDeUmaPessoaComCnpj(String cnpj) {
        String hql = "from PessoaJuridica pj " +
            " where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cnpj " +
            " and pj.situacaoCadastralPessoa = :ativo ";
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", cnpj.replace(".", "").replaceAll("-", "").replace("/", ""));
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        return q.getResultList().size() > 1;
    }

    public boolean existeMaisPessoaComCnpjAtiva(PessoaJuridica p) {
        String hql = "";
        if (p.getId() != null) {
            hql = "from PessoaJuridica p where replace(replace(replace(p.cnpj,'.',''),'-',''), '/', '') = :cnpj and p.id <> :pessoa and p.situacaoCadastralPessoa in (:ativo, :aguardando) ";
        } else {
            hql = "from PessoaJuridica p where replace(replace(replace(p.cnpj,'.',''),'-',''), '/', '') = :cnpj and p.situacaoCadastralPessoa in (:ativo, :aguardando)";
        }
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", p.getCnpj().replace(".", "").replace("-", "").replace("/", ""));
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        q.setParameter("aguardando", SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
        if (p.getId() != null) {
            q.setParameter("pessoa", p.getId());
        }
        return !q.getResultList().isEmpty();
    }


    public boolean existeMaisPessoaComCpfAtiva(PessoaFisica p) {
        String hql = "";
        if (p.getId() != null) {
            hql = "from PessoaFisica p where replace(replace(p.cpf,'.',''),'-','') = :cpf and p.id <> :pessoa and p.situacaoCadastralPessoa in (:ativo, :aguardando)";
        } else {
            hql = "from PessoaFisica p where replace(replace(p.cpf,'.',''),'-','') = :cpf and p.situacaoCadastralPessoa in (:ativo, :aguardando)";
        }
        Query q = em.createQuery(hql);
        q.setParameter("cpf", p.getCpf().replace(".", "").replaceAll("-", ""));
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        q.setParameter("aguardando", SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
        if (p.getId() != null) {
            q.setParameter("pessoa", p.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<PessoaFisica> getPessoasComMesmoCPF(PessoaFisica p) {
        String hql = "";
        if (p.getId() != null) {
            hql = "from PessoaFisica p left join fetch p.perfis pe where replace(replace(p.cpf,'.',''),'-','') = :cpf and p != :pessoa";
        } else {
            hql = "from PessoaFisica p left join fetch p.perfis pe where replace(replace(p.cpf,'.',''),'-','') = :cpf";
        }
        Query q = em.createQuery(hql);
        q.setParameter("cpf", p.getCpf().replace(".", "").replaceAll("-", ""));
        if (p.getId() != null) {
            q.setParameter("pessoa", p);
        }
        return q.getResultList();
    }

    public ContaCorrenteBancaria recuperarConta(ContaCorrenteBancaria contaCorrente) {
        Query q = em.createQuery("from ContaCorrenteBancaria c where c.numeroConta = :numero and c.digitoVerificador = :digito and c.agencia = :agencia");
        q.setParameter("numero", contaCorrente.getNumeroConta());
        q.setParameter("digito", contaCorrente.getDigitoVerificador());
        q.setParameter("agencia", contaCorrente.getAgencia());
        try {
            return (ContaCorrenteBancaria) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    //Pessoa Juridica
    public Pessoa recuperarPJ(Object id) {
        PessoaJuridica pj = em.find(PessoaJuridica.class, id);
        Hibernate.initialize(pj.getTelefones());
        Hibernate.initialize(pj.getEnderecos());
        Hibernate.initialize(pj.getPerfis());
        Hibernate.initialize(pj.getContaCorrenteBancPessoas());
        Hibernate.initialize(pj.getPerfis());
        Hibernate.initialize(pj.getClasseCredorPessoas());
        Hibernate.initialize(pj.getClassificacaoCredores());
        Hibernate.initialize(pj.getPessoaCNAE());
        Hibernate.initialize(pj.getSociedadePessoaJuridica());
        Hibernate.initialize(pj.getRepresentantesLegal());
        Hibernate.initialize(pj.getJuntaComercial());
        for (JuntaComercialPessoaJuridica junta : pj.getJuntaComercial()) {
            Hibernate.initialize(junta.getEventosRedeSim());
        }
        Hibernate.initialize(pj.getFiliais());
        Hibernate.initialize(pj.getHistoricosIntegracaoRedeSim());
        Hibernate.initialize(pj.getUsuariosWeb());
        Hibernate.initialize(pj.getHistoricoSituacoesPessoa());
        Hibernate.initialize(pj.getHorariosFuncionamento());
        for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : pj.getHorariosFuncionamento()) {
            Hibernate.initialize(pessoaHorarioFuncionamento.getHorarioFuncionamento().getItens());
        }
        if (pj.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(pj.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return pj;
    }

    public List<Telefone> telefonePorPessoa(Pessoa pessoa) {
        if (pessoa == null || pessoa.getId() == null) {
            return new ArrayList<>();
        }
        String hql = "select t from Telefone t where t.pessoa = :pessoa";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public Telefone recuperaTelefonePorTipo(Pessoa pessoa, TipoTelefone tipo) {
        if (pessoa == null || pessoa.getId() == null) {
            return new Telefone();
        }
        String hql = "select t from Telefone t where t.pessoa = :pessoa and t.tipoFone = :tipoFone";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setParameter("tipoFone", tipo);
        return (Telefone) q.getResultList().get(0);
    }

    public List<EnderecoCorreio> enderecoPorPessoa(Pessoa pessoa) {
        if (pessoa == null || pessoa.getId() == null) {
            return new ArrayList<>();
        }
        String hql = "select e from Pessoa p join p.enderecoscorreio e where p = :pessoa";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public List<ContaCorrenteBancPessoa> contasPorPessoa(Pessoa pessoa) {
        if (pessoa == null || pessoa.getId() == null) {
            return new ArrayList<>();
        }
        String hql = "select c from Pessoa p left join p.contaCorrenteBancPessoas c where p = :pessoa";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    /*
     * Metodo que retorna todas as pessoa que possuem um vinculo impregaticio
     * com a empresa. se o final data data vigente for null essa data assumira a
     * data corrente do servidor
     *
     */
    public List<PessoaFisica> listaPessoaComVinculoVigente(String parte) {
        String hql = " select  p from VinculoFP v ";
        hql += " inner join v.matriculaFP  as m ";
        hql += " inner join m.pessoa as p ";
        hql += " where ((lower(p.nome) like :parte) or (p.cpf like :parte)) ";
        hql += " and current_date between coalesce(v.inicioVigencia,current_date) and coalesce(v.finalVigencia,current_date) ";
        parte = "%" + parte.toLowerCase() + "%";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parte", parte.trim());
        q.setMaxResults(10);
        return q.getResultList();
    }    /*

     * Metodo que retorna todas as pessoa que possuem um vinculo impregaticio
     * com a empresa de acordo com as situações cadastrais.
     * Se o final data data vigente for null essa data assumira a
     * data corrente do servidor
     *
     */

    public List<Pessoa> listaPessoaComVinculoVigente(String parte, SituacaoCadastralPessoa... situacoes) {
        if (situacoes == null) {
            situacoes = SituacaoCadastralPessoa.values();
        }
        String hql = " select  p from VinculoFP v ";
        hql += " inner join v.matriculaFP  as m ";
        hql += " inner join m.pessoa as p ";
        hql += " where ((lower(p.nome) like :parte) or (p.cpf like :parte)) ";
        hql += " and current_date between coalesce(v.inicioVigencia,current_date) and coalesce(v.finalVigencia,current_date)";
        hql += " and p.situacaoCadastralPessoa in (:situacoes) ";
        parte = "%" + parte.toLowerCase() + "%";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parte", parte.trim());
        q.setParameter("situacoes", Lists.newArrayList(situacoes));
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PessoaFisica> listaFiltrandoPessoaComVinculoVigenteEPorTipoClasse(String parte, TipoClasseCredor tcc) {
        String sql = " SELECT * FROM ( " +
            " SELECT DISTINCT PF.* " +
            " FROM PESSOA P " +
            " INNER JOIN PESSOAFISICA PF ON P.ID = PF.ID " +
            " INNER JOIN CLASSECREDORPESSOA CCP ON CCP.PESSOA_ID = P.ID " +
            " INNER JOIN CLASSECREDOR CC ON CCP.CLASSECREDOR_ID = CC.ID " +
            " WHERE ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR REPLACE(PF.CPF, '.', '')  LIKE :parte) " +
            " AND CC.TIPOCLASSECREDOR = :tipoClasse AND ROWNUM <=10 " +
            " ) PESSOAFISICA " +
            " INNER JOIN PESSOA P ON P.ID = PESSOAFISICA.ID and p.situacaoCadastralPessoa = :ativo ";
        Query q = getEntityManager().createNativeQuery(sql, PessoaFisica.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipoClasse", tcc.name());
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO.name());
        return q.getResultList();
    }

    public List<Pessoa> listaFiltrandoPessoaPorTipoClasseCredor(String parte, TipoClasseCredor tcc) {
        Query pj;
        Query pf;
        ArrayList<Pessoa> retorno = new ArrayList<>();
        pf = em.createNativeQuery(" SELECT PF.*"
            + " FROM PESSOA P "
            + " INNER JOIN PESSOAFISICA PF ON P.ID = PF.ID "
            + " INNER JOIN CLASSECREDORPESSOA CCP ON CCP.PESSOA_ID = P.ID "
            + " INNER JOIN CLASSECREDOR CC ON CCP.CLASSECREDOR_ID = CC.ID "
            + " WHERE ((LOWER(PF.NOME) LIKE :parte) "
            + " OR (PF.CPF like :parte)) "
            + " AND CC.TIPOCLASSECREDOR = :tcc AND ROWNUM <=10 ", PessoaFisica.class);
        pf.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        pf.setParameter("tcc", tcc.name());
        pf.setMaxResults(10);
        retorno.addAll(pf.getResultList());

        pj = em.createNativeQuery(" SELECT PJ.* "
            + " FROM PESSOA P "
            + " INNER JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID "
            + " INNER JOIN CLASSECREDORPESSOA CCP ON CCP.PESSOA_ID = P.ID "
            + " INNER JOIN CLASSECREDOR CC ON CCP.CLASSECREDOR_ID = CC.ID "
            + " WHERE ((LOWER(PJ.NOMEFANTASIA) LIKE :parte) "
            + " OR (pj.cnpj like :parte)) "
            + " AND CC.TIPOCLASSECREDOR = :tcc AND ROWNUM <=10 ", PessoaJuridica.class);
        pj.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        pj.setParameter("tcc", tcc.name());
        pj.setMaxResults(10);
        retorno.addAll(pj.getResultList());
        return retorno;
    }

    public Pessoa salvar(Pessoa entity, Arquivo arquivo, FileUploadEvent fileUploadEvent, List<ContaCorrenteBancPessoa> listaExcluidos) {
        if (verificaSeExisteArquivo(arquivo)) {
            if (entity.getArquivo() == null) {
                arquivoFacade.removerArquivo(arquivo);
            }
        }
        if (fileUploadEvent != null) {
            arquivo = new Arquivo();
            entity.setArquivo(arquivo);
            salvarArquivo(fileUploadEvent, arquivo);
        }

        return salvarRetornando(entity);
    }

    public void salvarCadastroEconomico(Pessoa pessoa) {
        try {
            List<CadastroEconomico> cadastros = cadastroEconomicoFacade.recuperarCadastrosSomenteDaPessoa(pessoa);
            for (CadastroEconomico cadastro : cadastros) {
                cadastroEconomicoFacade.removerNaNota(sistemaFacade.getUsuarioCorrente(), cadastro);
            }
            usuarioWebFacade.removerRegistroMongodbNfse(pessoa);
        } catch (Exception e) {
            logger.error("Não foi possivel salvar o cadastro economico da pessoa {}", pessoa);
        }
    }

    public Pessoa salvarNovo(Pessoa entity, Arquivo arquivo, FileUploadEvent fileUploadEvent) {
        try {
            if (fileUploadEvent != null) {
                entity.setArquivo(arquivo);
                salvarArquivo(fileUploadEvent, arquivo);
            }

            if (entity.getContaCorrenteBancPessoas() != null) {
                for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : entity.getContaCorrenteBancPessoas()) {
                    contaCorrenteBancPessoa.setContaCorrenteBancaria(em.merge(contaCorrenteBancPessoa.getContaCorrenteBancaria()));
                }
            }

            entity = em.merge(entity);

            for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : entity.getContaCorrenteBancPessoas()) {
                contaCorrenteBancPessoa.setPessoa(entity);
                em.merge(contaCorrenteBancPessoa);
            }

            entity.setTelefonePrincipal(entity.getTelefonePrincipalAdicionado());
            entity.setEnderecoPrincipal(entity.getEnderecoCorrespondencia());
            entity.setContaCorrentePrincipal(entity.getContaBancariaPrincipalAdicionada());

            entity = em.merge(entity);
            return entity;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void salvarArquivo(FileUploadEvent fileUploadEvent, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public boolean verificaSeExisteArquivo(Arquivo arq) {
        Arquivo ar = null;
        if (arq == null || arq.getId() == null) {
            return false;
        }
        try {
            ar = arquivoFacade.recupera(arq.getId());
        } catch (Exception e) {
            return false;
        }
        if (arq.equals(ar)) {
            return true;
        }
        return false;
    }

    public Pessoa salvar(Pessoa entity, List<ContaCorrenteBancPessoa> listaExcluidos) {
        try {
            if (listaExcluidos != null) {
                for (ContaCorrenteBancPessoa c : listaExcluidos) {
                    c = em.find(ContaCorrenteBancPessoa.class, c.getId());
                    em.remove(c);
                }
            }
            return em.merge(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public Long recuperarIdPessoaFisicaPorCPF(String cpf) {
        String hql = "select p.id from PessoaFisica p where replace(replace(p.cpf,'.',''),'-','') = :cpf order by p.id desc";
        Query q = getEntityManager().createNativeQuery(hql);
        q.setParameter("cpf", limpaCpf(cpf));
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return Long.parseLong(String.valueOf(q.getResultList().get(0)));
        }
        return null;
    }

    public PessoaFisica recuperaPessoaFisicaPorCPF(String cpf) {
        return buscarPessoaFisicaPorCPF(cpf, false);
    }

    public PessoaFisica buscarPessoaFisicaPorCPF(String cpf, boolean somenteAtivas) {
        String hql = "from PessoaFisica p where replace(replace(p.cpf,'.',''),'-','') = :cpf";
        if (somenteAtivas) {
            hql += " and p.situacaoCadastralPessoa = :ativo ";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("cpf", limpaCpf(cpf));
        if (somenteAtivas) {
            q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        }
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            PessoaFisica pf = (PessoaFisica) q.getSingleResult();
            Hibernate.initialize(pf.getPerfis());
            Hibernate.initialize(pf.getDocumentosPessoais());
            Hibernate.initialize(pf.getTelefones());
            Hibernate.initialize(pf.getEnderecos());
            Hibernate.initialize(pf.getDependentes());
            Hibernate.initialize(pf.getHistoricoSituacoesPessoa());
            if (pf.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(pf.getDetentorArquivoComposicao().getArquivosComposicao());
            }
            return pf;
        }
    }

    public List<Pessoa> buscarPessoasPorCPFCNPJ(String cpf, boolean somenteAtivos) {
        List<Pessoa> pessoas = Lists.newArrayList();
        String hql = "from PessoaFisica p where replace(replace(replace(p.cpf,'.',''),'-',''), '/', '') = :cpfCnpj " + (somenteAtivos ? "and situacaoCadastralPessoa = :ativo" : "") + " order by situacaoCadastralPessoa";
        String hql2 = "from PessoaJuridica p where replace(replace(replace(p.cnpj,'.',''),'-',''), '/', '') = :cpfCnpj " + (somenteAtivos ? "and situacaoCadastralPessoa = :ativo" : "") + " order by situacaoCadastralPessoa";
        Query q = em.createQuery(hql);
        Query q2 = em.createQuery(hql2);
        q.setParameter("cpfCnpj", cpf.toLowerCase().replace(".", "").replace("-", "").replace("/", ""));
        q2.setParameter("cpfCnpj", cpf.toLowerCase().replace(".", "").replace("-", "").replace("/", ""));
        if (somenteAtivos) {
            q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
            q2.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        }
        pessoas.addAll(q.getResultList());
        pessoas.addAll(q2.getResultList());
        return pessoas;

    }

    public PessoaFisica buscarPessoaFisicaPorNome(String nome) {
        String hql = " from PessoaFisica p where upper(p.nome) = :nome " +
            "           and p.situacaoCadastralPessoa = :ativo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("nome", nome.toUpperCase());
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (PessoaFisica) q.getResultList().get(0);
    }

    public PessoaJuridica buscarPessoaJurificaPorRazaoSocial(String nome) {
        String hql = " from PessoaJuridica p where upper(p.razaoSocial) = :nome " +
            "           and p.situacaoCadastralPessoa = :ativo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("nome", nome.toUpperCase());
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (PessoaJuridica) q.getResultList().get(0);
    }

    public List<Pessoa> listaFiltrandoX(String s, int inicio, int max) {
        Long id;
        try {
            id = Long.parseLong(s);
        } catch (Exception e) {
            id = 0l;
        }
        Query q;
        ArrayList<Pessoa> retorno = new ArrayList<>();
        q = getEntityManager().createQuery("from PessoaFisica obj "
            + " where obj.id = :filtroId or lower(obj.nome) like :filtro "
            + " or lower(obj.cpf) like :filtro "
            + " or (replace(replace(obj.cpf,'.',''),'-','') like :filtro) ");
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());
        q = getEntityManager().createQuery("from PessoaJuridica obj "
            + " where obj.id = :filtroId or lower(obj.razaoSocial) like :filtro "
            + " or lower(obj.cnpj) like :filtro or lower(obj.nomeFantasia) like :filtro "
            + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :filtro)");
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return retorno;
    }

    public List<Pessoa> listaFiltrandoXPerfil(String s, int inicio, int max, String perfil) {
        Long id;
        try {
            id = Long.parseLong(s);
        } catch (Exception e) {
            id = 0l;
        }
        Query q;
        ArrayList<Pessoa> retorno = new ArrayList<>();
        q = getEntityManager().createQuery("from PessoaFisica obj "
            + " where lower(coalesce(obj.migracaoChave, 'rh')) like :perfil and  (obj.id = :filtroId or lower(obj.nome) like :filtro "
            + " or lower(obj.cpf) like :filtro "
            + " or (replace(replace(obj.cpf,'.',''),'-','') like :filtro)) ");
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("perfil", perfil + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());
        q = getEntityManager().createQuery("from PessoaJuridica obj "
            + " where lower(coalesce(obj.migracaoChave, 'rh')) like :perfil and obj.id = :filtroId or lower(obj.razaoSocial) like :filtro "
            + " or lower(obj.cnpj) like :filtro or lower(obj.nomeFantasia) like :filtro "
            + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :filtro)");
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("perfil", perfil + "%");
        q.setMaxResults(10);
        retorno.addAll(q.getResultList());

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return retorno;
    }

    public Arquivo recuperaFotoPessoaFisica(PessoaFisica pessoaFisica) {
        return recuperaFotoPessoaFisica(pessoaFisica.getId());
    }

    public Arquivo recuperaFotoPessoaFisica(Long id) {
        Query q = em.createQuery(" select pf.arquivo from PessoaFisica pf where pf.id = :parametro ");
        q.setParameter("parametro", id);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (Arquivo) q.getSingleResult();
        }
    }

    public Pessoa buscarPessoaPorCpfOrCnpj(String cpfOrCnpj) {
        if (Strings.isNullOrEmpty(cpfOrCnpj)) return null;
        Pessoa pessoa = recuperaPessoaFisicaPorCPF(cpfOrCnpj);
        if (pessoa == null) {
            pessoa = recuperaPessoaJuridicaPorCNPJ(cpfOrCnpj);
        }
        return pessoa;
    }

    public Pessoa buscarPessoaAtivasPorCpfOrCnpj(String cpfOrCnpj) {
        Pessoa pessoa = buscarPessoaFisicaPorCPF(cpfOrCnpj, true);
        if (pessoa == null) {
            pessoa = buscarPessoaJuridicaPorCNPJ(cpfOrCnpj, true, true);
        }
        return pessoa;
    }

    public Pessoa buscarPessoaAtivasPorNomeOrRazaoSocial(String nomeOrRazao) {
        Pessoa pessoa = buscarPessoaFisicaPorNome(nomeOrRazao);
        if (pessoa == null) {
            pessoa = buscarPessoaJurificaPorRazaoSocial(nomeOrRazao);
        }
        return pessoa;
    }

    public PessoaJuridica recuperaPessoaJuridicaPorCNPJ(String cnpj, boolean initializeLists) {
        return buscarPessoaJuridicaPorCNPJ(cnpj, false, initializeLists);
    }

    public PessoaJuridica recuperaPessoaJuridicaPorCNPJ(String cnpj) {
        return buscarPessoaJuridicaPorCNPJ(cnpj, false, true);
    }

    public PessoaJuridica buscarPessoaJuridicaPorCNPJ(String cnpj, boolean somenteAtivas) {
        return buscarPessoaJuridicaPorCNPJ(cnpj, somenteAtivas, true);
    }

    public PessoaJuridica buscarPessoaJuridicaPorCNPJ(String cnpj, boolean somenteAtivas, boolean initializeLists) {
        String hql = "from PessoaJuridica pj where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cnpj";
        if (somenteAtivas) {
            hql += " and pj.situacaoCadastralPessoa = :ativo ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", cnpj.replace(".", "").replace("-", "").replace("/", ""));
        if (somenteAtivas) {
            q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            PessoaJuridica pj = (PessoaJuridica) q.getSingleResult();
            if (initializeLists) {
                Hibernate.initialize(pj.getPerfis());
                Hibernate.initialize(pj.getTelefones());
                Hibernate.initialize(pj.getEnderecos());
                Hibernate.initialize(pj.getPessoaCNAE());
                Hibernate.initialize(pj.getSociedadePessoaJuridica());
                Hibernate.initialize(pj.getRepresentantesLegal());
                Hibernate.initialize(pj.getFiliais());
                Hibernate.initialize(pj.getUsuariosWeb());
                Hibernate.initialize(pj.getHistoricoSituacoesPessoa());
                Hibernate.initialize(pj.getHorariosFuncionamento());
                for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : pj.getHorariosFuncionamento()) {
                    Hibernate.initialize(pessoaHorarioFuncionamento.getHorarioFuncionamento().getItens());
                }
            }
            return pj;
        } else {
            return null;
        }
    }

    public PessoaFisica salvaRetornaPF(PessoaFisica p) {
        return em.merge(p);
    }

    public List<SefipTrabalhadorRegistroTipo14> listaTrabalhadoresSefip(MesCalendarioPagamento mesCalendarioPagamento, Integer ano, UnidadeOrganizacional unidadeOrganizacional) {
        Query q = em.createQuery("select new " + new SefipTrabalhadorRegistroTipo14().getClass().getCanonicalName()
            + " (contrato.categoriaSEFIP,contrato.matriculaFP.pessoa,contrato.dataAdmissao)"
            + " from ContratoFP contrato, FichaFinanceiraFP ficha "
            + " inner join fetch contrato.previdenciaVinculoFPs previdencia "
            + " inner join previdencia.tipoPrevidenciaFP tipoPrevidencia"
            + " where tipoPrevidencia.codigo <> :codigo "
            + " and ficha.vinculoFP.id = contrato.id "
            + " and ficha.folhaDePagamento.mes = :mes "
            + " and ficha.folhaDePagamento.ano = :ano "
            + " and ficha.folhaDePagamento.unidadeOrganizacional = :unidade "
            + " and ficha.folhaDePagamento.calculadaEm >= previdencia.inicioVigencia"
            + " and ficha.folhaDePagamento.calculadaEm <= coalesce(previdencia.finalVigencia, ficha.folhaDePagamento.calculadaEm)");

        q.setParameter("codigo", 3l);
        q.setParameter("mes", mesCalendarioPagamento);
        q.setParameter("ano", ano);
        q.setParameter("unidade", unidadeOrganizacional);

        return q.getResultList();
    }

    public List<Pessoa> recuperaPessoasDoCadastro(Cadastro cadastro) {
        List<Pessoa> pessoas = new ArrayList<>();
        if (cadastro instanceof CadastroImobiliario) {
            Query q = em.createQuery("select p.pessoa from "
                + " Propriedade p where p.imovel = "
                + " :cadastro and (p.finalVigencia is null or p.finalVigencia > :hoje)");
            q.setParameter("cadastro", cadastro);
            q.setParameter("hoje", new Date());
            pessoas = q.getResultList();
        }
        if (cadastro instanceof CadastroEconomico) {
            pessoas.add(((CadastroEconomico) cadastro).getPessoa());
        }
        if (cadastro instanceof CadastroRural) {
            Query q = em.createQuery("select p.pessoa from "
                + " PropriedadeRural p where p.imovel = :cadastro "
                + " and p.finalVigencia is null or p.finalVigencia >= :hoje");
            q.setParameter("cadastro", cadastro);
            q.setParameter("hoje", new Date());
            pessoas = q.getResultList();
        }
        return pessoas;
    }

    public Pessoa recuperaPessoaDaDividaAtivaPeloCadastro(Cadastro cadastro) {
        String sql = "SELECT max(coalesce(propriedade.pessoa_id, propriedaderural.pessoa_id, cmc.pessoa_id, contrato.locatario_id)) "
            + " FROM iteminscricaodividaativa iteminscricao "
            + " INNER JOIN Calculo cal on cal.id = iteminscricao.id "
            + " LEFT JOIN cadastroimobiliario bci ON bci.id = cal.cadastro_id"
            + " LEFT JOIN cadastroeconomico cmc ON cmc.id = cal.cadastro_id"
            + " LEFT JOIN cadastrorural bcr ON bcr.id = cal.cadastro_id"
            + " LEFT JOIN contratorendaspatrimoniais contrato ON contrato.id = cal.cadastro_id"
            + " LEFT JOIN propriedaderural ON propriedaderural.imovel_id = bcr.id"
            + " LEFT JOIN propriedade ON propriedade.imovel_id = bci.id"
            + " WHERE cal.cadastro_id = :cadastro";
        Query q = em.createNativeQuery(sql, Pessoa.class);
        q.setParameter("cadastro", cadastro.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (Pessoa) resultList.get(0);
    }

    public Pessoa salvarPessoaContabil(Pessoa entity) {
        try {
            entity.setContaCorrentePrincipal(null);
            entity = em.merge(entity);
            return entity;
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public boolean validaPisPasep(PessoaFisica ct, String pis) {
        Query q = em.createQuery("select c from DocumentoPessoal dp, CarteiraTrabalho c where c.pisPasep = :pis and dp.id = c.id and dp.pessoaFisica.id != :pessoa " +
            " and dp.pessoaFisica.situacaoCadastralPessoa = :ativo");
        q.setParameter("pis", pis.trim().replace(".", "").replace("-", ""));
        q.setParameter("pessoa", ct.getId() != null ? ct.getId() : -100l);
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        return q.getResultList().isEmpty();
    }

    public void excluir(Pessoa pessoa) {
        em.remove(pessoa);
    }

    public PessoaJuridica recuperaPessoaJuridicaPorEntidade(Entidade entidade) {
        Query q = em.createQuery("select p from Entidade e join e.pessoaJuridica p where e = :e");
        q.setParameter("e", entidade);
        if (!q.getResultList().isEmpty()) {
            return (PessoaJuridica) q.getResultList().get(0);
        } else {
            return null;
        }
    }

    public CarteiraTrabalho recuperarCarteiraTrabalhoPelaPessoa(PessoaFisica pessoa) {
        Query q = em.createNativeQuery("SELECT * FROM CarteiraTrabalho ct JOIN DocumentoPessoal dp ON ct.id = dp.id WHERE dp.pessoafisica_id = :pessoa", CarteiraTrabalho.class);
        q.setParameter("pessoa", pessoa.getId());
        if (!q.getResultList().isEmpty()) {
            return (CarteiraTrabalho) q.getResultList().get(0);
        }
        return null;
    }

    public List<CarteiraTrabalho> recuperarTodasCarteiraTrabalhoPessoa(PessoaFisica pessoa) {
        Query q = em.createNativeQuery("SELECT * FROM CarteiraTrabalho ct JOIN DocumentoPessoal dp ON ct.id = dp.id WHERE dp.pessoafisica_id = :pessoa", CarteiraTrabalho.class);
        q.setParameter("pessoa", pessoa.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public PessoaFisica recuperarPessoaPeloContratoFP(ContratoFP contrato) {
        Query q = em.createQuery("select p from VinculoFP vinculo join vinculo.matriculaFP matricula join matricula.pessoa p where vinculo.id = :id");
        q.setParameter("id", contrato.getId());
        if (!q.getResultList().isEmpty()) {
            return (PessoaFisica) q.getResultList().get(0);
        }
        return null;
    }

    public List<Pessoa> listaFiltrandoPerfilRH(String s, int inicio, int max, String perfil) {
        Long id;
        try {
            id = Long.parseLong(s);
        } catch (Exception e) {
            id = 0l;
        }
        Query q = getEntityManager().createQuery("from PessoaFisica obj "
            + " where lower(coalesce(obj.migracaoChave, 'rh')) like :perfil and  (obj.id = :filtroId or lower(obj.nome) like :filtro "
            + " or lower(obj.cpf) like :filtro "
            + " or (replace(replace(obj.cpf,'.',''),'-','') like :filtro)) ");
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("perfil", perfil + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void enviarEmailParaPessoa(Pessoa p, String corpoEmail, String assunto) {
        if (p.getEmail() != null) {
            EmailService.getInstance().enviarEmail(p.getEmail(), assunto, corpoEmail);
        }
    }


    public Pessoa recuperaPessoaDaEntidade(UnidadeOrganizacional unidade, Date dataOperacao) {

        String sql = " select e.* from vwhierarquiaorcamentaria vw "
            + " inner join entidade e on vw.entidade_id = e.id "
            + " where vw.subordinada_id = :unidade "
            + " and to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))";

        Query q = em.createNativeQuery(sql, Entidade.class);
        q.setParameter("unidade", unidade.getId());
        q.setParameter("data", DataUtil.getDataFormatada(dataOperacao));
        try {
            return ((Entidade) q.getSingleResult()).getPessoaJuridica();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Pessoa> listaPessoasAtivasPorCpfCnpj(String cpfCnpj) {
        List<Pessoa> pessoas = Lists.newArrayList();
        pessoas.addAll(listaPessoasFisicasPorCpf(cpfCnpj, SituacaoCadastralPessoa.ATIVO));
        pessoas.addAll(listaPessoasJuridicasPorCnpj(cpfCnpj, SituacaoCadastralPessoa.ATIVO));
        return pessoas;
    }

    public List<PessoaFisica> listaPessoasFisicasPorCpf(String cpf, SituacaoCadastralPessoa... situacaoCadastralPessoa) {
        String hql = " from PessoaFisica pf " +
            " where REGEXP_REPLACE(pf.cpf, '[^0-9]') = REGEXP_REPLACE(:cpf, '[^0-9]') " +
            "   and pf.situacaoCadastralPessoa in (:situacoes) ";
        Query q = em.createQuery(hql);
        q.setParameter("cpf", cpf);
        q.setParameter("situacoes", Lists.newArrayList(situacaoCadastralPessoa));
        q.setMaxResults(10);
        q.setLockMode(LockModeType.NONE);
        return q.getResultList();

    }

    public List<PessoaJuridica> listaPessoasJuridicasPorCnpj(String cnpj, SituacaoCadastralPessoa... situacaoCadastralPessoa) {
        String hql = " from PessoaJuridica pj " +
            " where REGEXP_REPLACE(pj.cnpj, '[^0-9]') = REGEXP_REPLACE(:cnpj, '[^0-9]') " +
            "   and pj.situacaoCadastralPessoa in (:situacoes) ";
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", cnpj);
        q.setParameter("situacoes", Lists.newArrayList(situacaoCadastralPessoa));
        q.setMaxResults(10);
        q.setLockMode(LockModeType.NONE);
        return q.getResultList();
    }

    public boolean validarDependenteEmOutraPessoa(Pessoa responsavel, Dependente dependente) {
        String hql = "select d from Dependente d where d.dependente = :dependente ";
        if (responsavel.getId() != null) {
            hql += " and d.responsavel <> :responsavel";
        }
        Query q = em.createQuery(hql);
        if (responsavel.getId() != null) {
            q.setParameter("responsavel", responsavel);
        }
        q.setParameter("dependente", dependente.getDependente());
        return q.getResultList().isEmpty();
    }

    public void inativarPessoa(Pessoa pessoa) {
        pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.INATIVO);
        pessoa.adicionarHistoricoSituacaoCadastral();
        em.merge(pessoa);
    }

    public PessoaFisica buscarPessoaPorCpfAndMatricula(String cpf, String matricula) {
        String hql = "select vinculo.matriculaFP.pessoa from VinculoFP vinculo "
            + " where vinculo.matriculaFP.matricula = :numeroMatricula "
            + "  and replace(replace(replace(vinculo.matriculaFP.pessoa.cpf,'.',''),'-',''),'/','') = :numeroCpf";
        Query q = em.createQuery(hql);
        q.setParameter("numeroMatricula", matricula.trim());
        q.setParameter("numeroCpf", cpf.trim().replace(".", "").replace("-", ""));
        if (!q.getResultList().isEmpty()) {
            PessoaFisica pessoaFisica = (PessoaFisica) q.getResultList().get(0);

            return pessoaFisica;
        }
        return null;
    }

    public List<PessoaJuridica> buscarPessoasJuridicasPorCnpj(String cnpj, SituacaoCadastralPessoa... situacaoCadastralPessoa) {
        String hql = " from PessoaJuridica pj " +
            " where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cnpj " +
            "   and pj.situacaoCadastralPessoa in (:situacoes) ";
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", cnpj.trim().replace(".", "").replace("-", "").replace("/", ""));
        q.setParameter("situacoes", Lists.newArrayList(situacaoCadastralPessoa));
        q.setMaxResults(10);
        return q.getResultList();
    }

    public String limpaCpf(String sCpf) {
        return sCpf.replace(".", "").replace("-", "");
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public EscritorioContabilFacade getEscritorioContabilFacade() {
        return escritorioContabilFacade;
    }

    public NivelEscolaridadeFacade getNivelEscolaridadeFacade() {
        return nivelEscolaridadeFacade;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public NacionalidadeFacade getNacionalidadeFacade() {
        return nacionalidadeFacade;
    }

    public AgenciaFacade getAgenciaFacade() {
        return agenciaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public CidadeFacade getNaturalidadeFacade() {
        return naturalidadeFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public CEPFacade getCepFacade() {
        return cepFacade;
    }

    public CEPLogradouroFacade getCepLogradouroFacade() {
        return cepLogradouroFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public PortalContribunteFacade getPortalContribunteFacade() {
        return portalContribunteFacade;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public GrauDeParentescoFacade getGrauDeParentescoFacade() {
        return grauDeParentescoFacade;
    }

    public PessoaJuridica recuperarPessoaJuridicaPorRevisao(Long id, Long revisao) {
        AuditReader reader = AuditReaderFactory.get(Util.criarEntityManagerOpenViewReadOnly());
        PessoaJuridica pessoa = reader.find(PessoaJuridica.class, id, revisao);
        return pessoa;
    }

    public PessoaFisica recuperarPessoaFisicaPorRevisao(Long id, Long revisao) {
        PessoaFisica pessoa;
        AuditReader reader = AuditReaderFactory.get(Util.criarEntityManagerOpenViewReadOnly());
        pessoa = reader.find(PessoaFisica.class, id, revisao);
        return pessoa;
    }

    public void inicializarListaNoHibernate(List lista) {
        for (Object o : lista) {
            inicializarObjetoNoHibernate(o);
        }
    }

    public void inicializarObjetoNoHibernate(Object objeto) {
        Hibernate.initialize(objeto);
    }

    public List<PessoaFisica> buscarPessoasFisicasPorCpf(String cpf) {
        String hql = " from PessoaFisica pf " +
            " where REGEXP_REPLACE(pf.cpf, '[^0-9]') = REGEXP_REPLACE(:cpf, '[^0-9]') " +
            " and pf.situacaoCadastralPessoa = :ativo ";
        Query q = em.createQuery(hql);
        q.setParameter("cpf", cpf);
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public boolean hasVinculoAtivo(PessoaFisica pf) {
        String hql = " from PessoaFisica pf " +
            " where pf.id = :pfId " +
            "   and pf in (select mat.pessoa from VinculoFP v join v.matriculaFP mat where " +
            " :dataOperacao between v.inicioVigencia and coalesce(v.finalVigencia, :dataOperacao) )";
        Query q = em.createQuery(hql);
        q.setParameter("pfId", pf.getId());
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        return !q.getResultList().isEmpty();
    }

    public void salvarAtualizacaoCadastral(PessoaFisicaDTO pessoaFisicaDTO) {
        try {
            PessoaFisicaPortal pessoaPortal = PessoaFisicaPortal.toPessoFisicaPortal(pessoaFisicaDTO);
            adicionarDependenteVinculoPortal(pessoaFisicaDTO, pessoaPortal);
            if (pessoaFisicaDTO.getConfirmacaoCadastro()) {
                pessoaPortal.setStatus(SituacaoPessoaPortal.AGUARDANDO_LIBERACAO);
                pessoaPortal.setLiberadoEm(null);
            }
            PessoaFisicaPortal pessoaPortalSalva = pessoaFisicaPortalFacade.buscarPessoaPortalPorPessoaId(pessoaFisicaDTO.getId());
            if (pessoaPortalSalva != null && !SituacaoPessoaPortal.LIBERADO.equals(pessoaPortalSalva.getStatus())) {
                pessoaPortal.setId(pessoaPortalSalva.getId());
            }
            pessoaPortal.getItemTempoContratoFPPessoaPortal().size();
            PessoaFisica pf = PessoaFisica.dtoToPessoaFisica(pessoaFisicaDTO);
            pessoaPortal.setPessoaFisica(pessoaFisicaFacade.recuperar(pf.getId()));
            em.merge(pessoaPortal);
        } catch (Exception e) {
            logger.error("Error: ", e);
            throw e;
        }
    }

    private void adicionarDependenteVinculoPortal(PessoaFisicaDTO pessoaFisicaDTO, PessoaFisicaPortal pessoaPortal) {
        if (pessoaFisicaDTO.getDependentes() != null && !pessoaFisicaDTO.getDependentes().isEmpty()) {
            for (DependenteDTO dependenteDTO : pessoaFisicaDTO.getDependentes()) {
                DependentePortal dependentePortal = DependentePortal.dtoToDependentePortal(dependenteDTO, pessoaPortal);
                if (dependenteDTO.getId() != null) {
                    Dependente dep = em.find(Dependente.class, dependenteDTO.getId());
                    if (dep != null) {
                        dependentePortal.setDependente(dep);
                    }
                }
                atribuirItemDependenciaDTO(dependenteDTO, dependentePortal);
                if (dependenteDTO.getGrauDeParentescoDTO() != null) {
                    GrauDeParentesco grauParentesco = grauDeParentescoFacade.recuperar(dependenteDTO.getGrauDeParentescoDTO().getId());
                    if (grauParentesco != null) {
                        dependentePortal.setGrauDeParentesco(grauParentesco);
                    }
                }
                pessoaPortal.getDependentes().add(dependentePortal);
            }
        }
    }

    private void atribuirItemDependenciaDTO(DependenteDTO dependenteDTO, DependentePortal dependentePortal) {
        for (DependenteVinculoFPDTO itemDependenciaDTO : dependenteDTO.getDependentesVinculoPortalDto()) {
            DependenteVinculoPortal dependenteVinculoPortal = DependenteVinculoPortal.dtoToDependenteVinculoPortal(itemDependenciaDTO);
            dependenteVinculoPortal.setDependentePortal(dependentePortal);
            if (itemDependenciaDTO.getTipoDependenteDTO() != null && itemDependenciaDTO.getTipoDependenteDTO().getIdTipoDependente() != null) {
                TipoDependente tipoDependente = tipoDependenteFacade.recuperarSemDependencias(itemDependenciaDTO.getTipoDependenteDTO().getIdTipoDependente());
                if (tipoDependente != null) {
                    dependenteVinculoPortal.setTipoDependente(tipoDependente);
                }
            }
            dependentePortal.getDependentesVinculos().add(dependenteVinculoPortal);
        }
    }

    @Override
    public Pessoa salvarRetornando(Pessoa pessoa) {
        Pessoa merge = em.merge(pessoa);
        salvarCadastroEconomico(merge);
        return merge;
    }

    public ImagemUsuarioNfseDTO buscarImagemDaPessoa(Long id) {
        Pessoa pessoa = recuperar(id);
        if (pessoa == null || pessoa.getArquivo() == null) {
            return null;
        }
        ImagemUsuarioNfseDTO imagem = new ImagemUsuarioNfseDTO(pessoa.getArquivo().toArquivoDTO().getConteudo(),
            pessoa.toNfseDto(), pessoa.getArquivo().getId());
        return imagem;

    }

    public void atualizarImagemPessoa(ImagemUsuarioNfseDTO imagemDto) {
        Pessoa pessoa = recuperar(imagemDto.getPessoa().getId());
        Arquivo arquivo = ArquivoUtil.base64ToArquivo(imagemDto.getConteudo(), "Imagem do Usuário");

        try {
            arquivo = arquivoFacade.novoArquivoMemoria(arquivo);
            arquivo = em.merge(arquivo);
            pessoa.setArquivo(arquivo);
            salvarRetornando(pessoa);
        } catch (Exception e) {
            logger.error("Não foi possível salvar a imagem da pessoa");
        }
    }

    public Pessoa createOrSave(PessoaNfseDTO dto) {
        return createOrSave(dto, SituacaoCadastralPessoa.ATIVO);
    }

    public Pessoa createOrSave(PessoaNfseDTO dto, SituacaoCadastralPessoa situacaoPessoa) {
        DadosPessoaisNfseDTO dadosPessoais = dto.getDadosPessoais();
        if (!Strings.isNullOrEmpty(dadosPessoais.getCpfCnpj())) {
            Pessoa pessoa;
            if (dadosPessoais.getIdPessoa() != null) {
                pessoa = em.find(Pessoa.class, dadosPessoais.getIdPessoa());
            } else {
                pessoa = buscarPessoaAtivasPorCpfOrCnpj(dadosPessoais.getCpfCnpj());
                if (pessoa == null) {
                    if (dadosPessoais.getCpfCnpj().length() >= 14) {
                        pessoa = new PessoaJuridica();
                    } else {
                        pessoa = new PessoaFisica();
                    }
                }
                pessoa = aplicarDadosPessoais(dadosPessoais, pessoa, situacaoPessoa);
            }
            return pessoa;
        }
        return null;
    }

    public boolean hasOutraPessoaComMesmoEmail(Pessoa pessoa, String email) {
        String sql = "select id from UsuarioWeb where email = :email ";
        if (pessoa.getId() != null) {
            sql += " and pessoa_id <> :idPessoa";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("email", email);
        if (pessoa.getId() != null) {
            q.setParameter("idPessoa", pessoa.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public Pessoa aplicarDadosPessoais(DadosPessoaisNfseDTO dadosPessoais, Pessoa pessoa) throws ValidacaoException {
        return aplicarDadosPessoais(dadosPessoais, pessoa, SituacaoCadastralPessoa.ATIVO);
    }

    public Pessoa aplicarDadosPessoais(DadosPessoaisNfseDTO dadosPessoais, Pessoa pessoa, SituacaoCadastralPessoa situacaoPessoa) throws ValidacaoException {
        if (pessoa instanceof PessoaFisica) {
            ((PessoaFisica) pessoa).setCpf(dadosPessoais.getCpfCnpj());
            ((PessoaFisica) pessoa).setNome(dadosPessoais.getNomeRazaoSocial());
            RG rg = ((PessoaFisica) pessoa).getRg();
            if (rg == null) rg = new RG();
            rg.setNumero(dadosPessoais.getInscricaoEstadualRg());
            rg.setPessoaFisica(((PessoaFisica) pessoa));
            ((PessoaFisica) pessoa).getDocumentosPessoais().add(rg);
        } else {
            ((PessoaJuridica) pessoa).setCnpj(dadosPessoais.getCpfCnpj());
            ((PessoaJuridica) pessoa).setRazaoSocial(dadosPessoais.getNomeRazaoSocial());
            ((PessoaJuridica) pessoa).setNomeFantasia(dadosPessoais.getNomeRazaoSocial());
            ((PessoaJuridica) pessoa).setNomeReduzido(dadosPessoais.getNomeRazaoSocial());
            ((PessoaJuridica) pessoa).setInscricaoEstadual(dadosPessoais.getInscricaoEstadualRg());
            ((PessoaJuridica) pessoa).setInscricaoMunicipal(dadosPessoais.getInscricaoMunicipal());
        }
        pessoa.setEmail(dadosPessoais.getEmail());
        pessoa.setSituacaoCadastralPessoa(situacaoPessoa);
        pessoa = em.merge(pessoa);

        atualizaEndereco(dadosPessoais, pessoa);
        atualizaTelefones(dadosPessoais, pessoa);

        if (!hasHistoricoSituacaoAdicionadoNaData(pessoa, new Date())) {
            pessoa.adicionarHistoricoSituacaoCadastral();
        }
        if (pessoa instanceof PessoaJuridica) {
            if (!hasHistoricoRedeSimAdicionadoNaData((PessoaJuridica) pessoa, new Date())) {
                ((PessoaJuridica) pessoa).adicionarHistoricoRedeSimPessoa();
            }
        }
        return em.merge(pessoa); // salva antes de depois de adicionar o endereco e telefone para funcionar o 'principal'
    }

    private void atualizaTelefones(DadosPessoaisNfseDTO dadosPessoais, Pessoa pessoa) {
        if (dadosPessoais.getTelefone() != null) {
            Telefone foneFixo = pessoa.getTelefonePorTipo(TipoTelefone.FIXO);
            if (foneFixo == null) {
                foneFixo = new Telefone(pessoa, dadosPessoais.getTelefone(), true, TipoTelefone.FIXO);
                pessoa.getTelefones().add(foneFixo);
            } else {
                foneFixo.setTelefone(dadosPessoais.getTelefone());
            }
        }
        if (dadosPessoais.getCelular() != null) {
            Telefone foneCelular = pessoa.getTelefonePorTipo(TipoTelefone.CELULAR);
            if (foneCelular == null) {
                foneCelular = new Telefone(pessoa, dadosPessoais.getCelular(), true, TipoTelefone.CELULAR);
                pessoa.getTelefones().add(foneCelular);
            } else {
                foneCelular.setTelefone(dadosPessoais.getCelular());
            }
        }
    }

    private void atualizaEndereco(DadosPessoaisNfseDTO dadosPessoais, Pessoa pessoa) {
        EnderecoCorreio endereco = pessoa.getEnderecoPrincipal();
        if (endereco == null) {
            endereco = new EnderecoCorreio();
        }
        endereco.setCep(dadosPessoais.getCep());
        endereco.setLogradouro(dadosPessoais.getLogradouro());
        endereco.setNumero(dadosPessoais.getNumero());
        endereco.setComplemento(dadosPessoais.getComplemento());
        endereco.setBairro(dadosPessoais.getBairro());
        if (dadosPessoais.getMunicipio() != null) {
            endereco.setLocalidade(dadosPessoais.getMunicipio().getNome());
            endereco.setUf(dadosPessoais.getMunicipio().getEstado());

        }
        endereco.setPrincipal(true);

        if (endereco.getId() == null) {
            pessoa.getEnderecos().add(endereco);
            pessoa.setEnderecoPrincipal(endereco);
        }
    }

    private boolean hasHistoricoSituacaoAdicionadoNaData(Pessoa pessoa, Date dataHistorico) {
        for (HistoricoSituacaoPessoa historico : pessoa.getHistoricoSituacoesPessoa()) {
            if (DataUtil.dataSemHorario(historico.getDataSituacao()).equals(DataUtil.dataSemHorario(dataHistorico))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasHistoricoRedeSimAdicionadoNaData(PessoaJuridica pessoa, Date dataHistorico) {
        for (HistoricoAlteracaoRedeSim historico : pessoa.getHistoricosIntegracaoRedeSim()) {
            if (DataUtil.dataSemHorario(historico.getDataAlteracao()).equals(DataUtil.dataSemHorario(dataHistorico))) {
                return true;
            }
        }
        return false;
    }

    public PessoaFisica criarPessoaDoUsuario(RegisterNfseDTO registerDTO) throws ValidacaoException {
        DadosPessoaisNfseDTO dadosPessoais = registerDTO.getDadosPessoais();
        dadosPessoais.setEmail(registerDTO.getEmail());
        PessoaFisica pf = (PessoaFisica) buscarPessoaPorCpfOrCnpj(registerDTO.getDadosPessoais().getCpfCnpj());
        if (pf == null) {
            pf = new PessoaFisica();
        }
        pf = (PessoaFisica) aplicarDadosPessoais(dadosPessoais, pf);
        return pf;
    }

    public Pessoa buscarPessoaComDepedenciasPorCpfOrCnpj(String cpfOrCnpj) {
        Pessoa pessoa = recuperaPessoaFisicaPorCPF(cpfOrCnpj);
        if (pessoa == null) {
            pessoa = recuperaPessoaJuridicaPorCNPJ(cpfOrCnpj);
        }
        if (pessoa != null) {
            Hibernate.initialize(pessoa.getEnderecos());
            Hibernate.initialize(pessoa.getTelefones());
        }
        return pessoa;
    }

    public List<AverbacaoContratoPorServico> buscarAverbacoesETempoContratoPorIDPessoa(Long ID) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DADOS.ID, DADOS.EMPREGADO, DADOS.INICIOVIGENCIA, DADOS.FINALVIGENCIA ")
            .append(" FROM (SELECT DISTINCT averbacoes.* FROM PESSOA p INNER JOIN MATRICULAFP mat ON p.ID = mat.PESSOA_ID ")
            .append(" INNER JOIN VINCULOFP v ON mat.ID = v.MATRICULAFP_ID ")
            .append(" INNER JOIN CONTRATOFP contrato ON v.ID = contrato.ID ")
            .append(" INNER JOIN AVERBACAOTEMPOSERVICO averbacoes ON contrato.ID = averbacoes.CONTRATOFP_ID ")
            .append(" WHERE p.ID = :ID ) DADOS ")
            .append(" UNION ")
            .append(" SELECT tempoContrato.ID ,tempoContrato.LOCALTRABALHO,tempoContrato.INICIO,tempoContrato.FIM ")
            .append(" FROM TEMPOCONTRATOFPPESSOA tempoContrato INNER JOIN PESSOA p ON tempoContrato.PESSOAFISICA_ID = p.ID ")
            .append(" WHERE p.ID = :ID ORDER BY INICIOVIGENCIA DESC ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ID", ID);
        try {
            List<AverbacaoContratoPorServico> averbacaoContratoPorServicos = Lists.newArrayList();
            List<Object[]> lista = q.getResultList();
            for (Object[] objeto : lista) {
                AverbacaoContratoPorServico averbacaoContratoPorServico = new AverbacaoContratoPorServico();

                averbacaoContratoPorServico.setLocalTrabalho((String) objeto[1]);
                averbacaoContratoPorServico.setId(Long.parseLong(objeto[0].toString()));
                averbacaoContratoPorServico.setInicioVigencia((Date) objeto[2]);
                averbacaoContratoPorServico.setFinalVigencia((Date) objeto[3]);

                averbacaoContratoPorServicos.add(averbacaoContratoPorServico);
            }
            if (averbacaoContratoPorServicos.isEmpty()) {
                return Lists.newArrayList();
            }
            return averbacaoContratoPorServicos;

        } catch (Exception e) {
            logger.error("Erro ao tentar buscar averbações e tempo de contrato ", e);
            return null;
        }
    }

    public PessoaNfseDTO atualizarDadosPessoa(PessoaNfseDTO pessoaDTO) {
        Pessoa pessoa = recuperar(pessoaDTO.getId());
        pessoa = aplicarDadosPessoais(pessoaDTO.getDadosPessoais(), pessoa);
        return pessoa.toNfseDto();
    }

    public Long recuperarIdRevisaoPessoaJuridicaHistoricoRedeSim(HistoricoAlteracaoRedeSim historico) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select rev.id from pessoajuridica_aud aud ")
            .append(" inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append(" where rev.id = (select rev from historicoredesim_aud paud where id = :idHistorico and paud.revtype = 0) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idHistorico", historico.getId());
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public Pessoa criarOrAtualizarPessoa(PessoaNfseDTO pessoaDTO) {
        if (pessoaDTO.getId() != null) {
            Pessoa pessoa = recuperar(pessoaDTO.getId());
            atualizaEndereco(pessoaDTO.getDadosPessoais(), pessoa);
            atualizaTelefones(pessoaDTO.getDadosPessoais(), pessoa);
            pessoa = em.merge(pessoa);
            return pessoa;
        } else {
            Pessoa pessoa;
            if (pessoaDTO.getDadosPessoais().getCpfCnpj().length() >= 14) {
                pessoa = new PessoaJuridica();
            } else {
                pessoa = new PessoaFisica();
            }
            return aplicarDadosPessoais(pessoaDTO.getDadosPessoais(), pessoa);
        }
    }

    public Long buscarPessoaFisicaPorVinculoFP(Long idVinculo) {
        String sql = "select mat.pessoa_id from vinculofp vinculo " +
            "       inner join matriculafp mat on vinculo.matriculafp_id = mat.id" +
            "       where vinculo.id = :vinculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculo", idVinculo);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return Long.parseLong(resultList.get(0).toString());
        }
        return null;
    }

    public Pessoa recuperarHorariosDeFuncionamento(Long id) {
        Pessoa pessoa = em.find(Pessoa.class, id);
        Hibernate.initialize(pessoa.getHorariosFuncionamento());
        return pessoa;
    }

    public List<PessoaJuridica> buscarPessoasJuridicasComMesmoCnpj(PessoaJuridica p) {
        String hql = " from PessoaJuridica p where replace(replace(replace(p.cnpj,'.',''),'-',''),'/','') = :cnpj";
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", p.getCnpj().replace(".", "").replaceAll("-", "").replace("/", ""));
        return q.getResultList();
    }

    public boolean hasPermissaoEditar() {
        return usuarioSistemaFacade.validaAutorizacaoUsuario(sistemaFacade.getUsuarioCorrente(),
            AutorizacaoTributario.PERMITIR_EDITAR_CADASTRO_PESSOA_JURIDICA_PERFIL_TRIBUTARIO);
    }

    public boolean hasPermissaoAprovarRejeitar() {
        return usuarioSistemaFacade.validaAutorizacaoUsuario(sistemaFacade.getUsuarioCorrente(),
            AutorizacaoTributario.PERMITIR_APROVAR_REJEITAR_CADASTRO_PESSOA_PERFIL_TRIBUTARIO);
    }

    public List<VinculoFP> buscarVinculoFPPessoa(PessoaFisica pf) {
        String sql = "select vinculo from VinculoFP vinculo " +
            "inner join vinculo.matriculaFP mat " +
            " where mat.pessoa  = :pessoa";
        Query q = em.createQuery(sql);
        q.setParameter("pessoa", pf);
        return q.getResultList();
    }


    public SolicitacaoCadastroPessoa buscarPorKeyAndTipo(String key, TipoSolicitacaoCadastroPessoa tipo) {
        if (key != null) {
            return solicitacaoCadastroPessoaFacade.buscarPorKeyAndTipo(key, tipo);
        }
        return null;
    }

    public void confirmarAprovacaoSolicitacaoCadastroPessoa(Pessoa pessoa) {
        pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        pessoa.adicionarHistoricoSituacaoCadastral();
        pessoa = salvarRetornando(pessoa);
        if (pessoa.hasEmail()) {
            getPortalContribunteFacade().getUsuarioWebFacade().criarLoginPortalWebSeNaoExistir(pessoa.getEmail(), pessoa);
        }
    }

    public void confirmarRejeicaoSolicitacaoCadastroPessoa(Pessoa pessoa, SolicitacaoCadastroPessoa solicitacaoCadastroPessoa, TipoTemplateEmail tipoTemplateEmail) {
        pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.REJEITADO);
        pessoa.adicionarHistoricoSituacaoCadastral();
        salvarRetornando(pessoa);
        solicitacaoCadastroPessoaFacade.confirmarRejeicaoSolicitacaoCadastroPessoa(solicitacaoCadastroPessoa, tipoTemplateEmail);
    }

    public String buscarNomePessoa(Long idPessoa) {
        String sql = "select coalesce(pf.nome, pj.razaoSocial) as nome from Pessoa pes " +
            " left join pessoafisica pf on pf.id = pes.id " +
            " left join pessoajuridica pj on pj.id = pes.id " +
            " where pes.id = :idPessoa";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", idPessoa);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (String) resultList.get(0);
        }
        return "";
    }

    public PessoaFisica validarPisExistentePorPessoa(PessoaFisica ct, String pis) {
        String hql = " select c.pessoaFisica from DocumentoPessoal dp, CarteiraTrabalho c where c.pisPasep = :pis " +
            "             and dp.id = c.id " +
            "             and dp.pessoaFisica.situacaoCadastralPessoa = :ativo ";
        if (ct != null && ct.getId() != null) {
            hql += " and dp.pessoaFisica.id != :pessoa ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("pis", pis.trim().replace(".", "").replace("-", ""));
        q.setParameter("ativo", SituacaoCadastralPessoa.ATIVO);
        if (ct != null && ct.getId() != null) {
            q.setParameter("pessoa", ct.getId());
        }
        List result = q.getResultList();
        if (!result.isEmpty()) {
            return (PessoaFisica) result.get(0);
        }
        return null;
    }

    public SituacaoCadastralPessoa buscarSituacaoCadastralPessoaPorCpfCnpj(String cpfCnpj) {
        String sql = "select pes.situacaoCadastralPessoa as situacao " +
            " from Pessoa pes " +
            "   left join pessoafisica pf on pf.id = pes.id " +
            "   left join pessoajuridica pj on pj.id = pes.id " +
            " where REGEXP_REPLACE(coalesce(pf.cpf, pj.cnpj), '[^0-9]', '') = REGEXP_REPLACE(:cpfCnpj, '[^0-9]', '') " +
            " order by pes.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cpfCnpj", cpfCnpj);
        List<String> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return SituacaoCadastralPessoa.valueOf(resultado.get(0));
        }
        return null;
    }

    public boolean cadastroAtualizado(PessoaFisica pessoaFisica) {
        int periodoEmMesesParaSolicitarAtualizacao = 6;
        Integer diferencaMesesEntidadePai = null;
        Integer diferencaMesesEntidadeFilho = null;
        pessoaFisica = pessoaFisicaFacade.recuperar(pessoaFisica.getId());
        try {
            ValidaPessoa.validarPerfilTributario(pessoaFisica);
        } catch (Exception ex) {
            return false;
        }
        String sqlAud = "select rev.datahora " +
            " from pessoa_aud aud " +
            "         left join revisaoauditoria rev on aud.rev = rev.id " +
            "where aud.id = :idPessoa " +
            "order by rev.datahora desc fetch first 1 row only";
        Query query = em.createNativeQuery(sqlAud);
        query.setParameter("idPessoa", pessoaFisica.getId());
        List<Date> resultado = query.getResultList();
        if (!resultado.isEmpty()) {
            diferencaMesesEntidadePai = DataUtil.diferencaMesesInteira(resultado.get(0), new Date());
        }

        Map<String, List<Long>> mapIdFilhosAud = buscarIdAudFilhos(PessoaFisica.class, pessoaFisica.getId());
        for (Map.Entry<String, List<Long>> entry : mapIdFilhosAud.entrySet()) {
            String sqlAudFilhos = " select rev.datahora " +
                " from " + entry.getKey() + " aud " +
                "         left join revisaoauditoria rev on aud.rev = rev.id " +
                " where aud.id in :idFilhos " +
                " order by rev.datahora desc fetch first 1 row only ";
            Query queryFilhos = em.createNativeQuery(sqlAudFilhos);
            queryFilhos.setParameter("idFilhos", entry.getValue());
            List<Date> resultadoFilhos = queryFilhos.getResultList();
            if (!resultadoFilhos.isEmpty()) {
                diferencaMesesEntidadeFilho = DataUtil.diferencaMesesInteira(resultadoFilhos.get(0), new Date());
                if (diferencaMesesEntidadeFilho <= periodoEmMesesParaSolicitarAtualizacao) break;
            }
        }

        return (diferencaMesesEntidadePai != null && diferencaMesesEntidadePai <= periodoEmMesesParaSolicitarAtualizacao) ||
            (diferencaMesesEntidadeFilho != null && diferencaMesesEntidadeFilho <= periodoEmMesesParaSolicitarAtualizacao);
    }

    private Map<String, List<Long>> buscarIdAudFilhos(Class classe, Long id) {
        Map<String, List<Long>> retorno = Maps.newHashMap();
        for (Field field : Persistencia.getAtributos(classe)) {
            if (field.isAnnotationPresent(OneToMany.class) && !Strings.isNullOrEmpty(field.getAnnotation(OneToMany.class).mappedBy()) && !field.isAnnotationPresent(NotAudited.class)) {
                String mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
                Class genericTypeFromCollection = AuditoriaJDBC.getGenericTypeFromCollection(field, classe);
                String referenciaTabela = Util.getNomeTabela(genericTypeFromCollection) + "_aud";
                String select = " select distinct id from " + referenciaTabela + " where " + mappedBy + "_id = " + id;
                Query q = em.createNativeQuery(select);
                List<BigDecimal> resultList = q.getResultList();
                for (BigDecimal idAudFilho : resultList) {
                    if (retorno.containsKey(referenciaTabela)) {
                        retorno.get(referenciaTabela).add(idAudFilho.longValue());
                    } else {
                        retorno.put(referenciaTabela, Lists.newArrayList(idAudFilho.longValue()));
                    }
                }
            }
        }
        return retorno;
    }
}
