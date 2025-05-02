/*
 * Codigo gerado automaticamente em Tue Feb 15 11:18:55 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SiprevControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoConcurso;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoInscricao;
import br.com.webpublico.entidades.rh.concursos.InscricaoConcurso;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class PessoaFisicaFacade extends AbstractFacade<PessoaFisica> {

    private static final Logger logger = LoggerFactory.getLogger(PessoaFisicaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;
    @EJB
    private EsferaGovernoFacade esferaGovernoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    @EJB
    private SistemaFacade sistemaFacade;

    public PessoaFisicaFacade() {
        super(PessoaFisica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public void setArquivoFacade(ArquivoFacade arquivoFacade) {
        this.arquivoFacade = arquivoFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public void setCidadeFacade(CidadeFacade cidadeFacade) {
        this.cidadeFacade = cidadeFacade;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }

    public void setUfFacade(UFFacade ufFacade) {
        this.ufFacade = ufFacade;
    }

    public HierarquiaOrganizacionalFacadeOLD getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }

    public void setConsultaCepFacade(ConsultaCepFacade consultaCepFacade) {
        this.consultaCepFacade = consultaCepFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public void setClasseCredorFacade(ClasseCredorFacade classeCredorFacade) {
        this.classeCredorFacade = classeCredorFacade;
    }

    public UnidadeExternaFacade getUnidadeExternaFacade() {
        return unidadeExternaFacade;
    }

    public void setUnidadeExternaFacade(UnidadeExternaFacade unidadeExternaFacade) {
        this.unidadeExternaFacade = unidadeExternaFacade;
    }

    public EsferaGovernoFacade getEsferaGovernoFacade() {
        return esferaGovernoFacade;
    }

    public void setEsferaGovernoFacade(EsferaGovernoFacade esferaGovernoFacade) {
        this.esferaGovernoFacade = esferaGovernoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public void setPessoaFacade(PessoaFacade pessoaFacade) {
        this.pessoaFacade = pessoaFacade;
    }

    public List<PessoaFisica> listaProcurador(String filtro) {
        String sql;
        sql = "select  p from ContratoFP v "
            + "inner join v.matriculaFP  as m  "
            + "inner join m.pessoa as p "
            + "inner join v.cargo "
            + "where ((lower(p.nome) like :parte) or (p.cpf like :parte))  "
            + "and current_date between coalesce(v.inicioVigencia,current_date) and coalesce(v.finalVigencia,current_date) "
            + "and v.cargo = (select cc.cargo from ConfiguracaoProcDivida cc where current_date between coalesce(cc.inicioVigencia,current_date) and coalesce(cc.fimVigencia,current_date))";
        Query q = em.createQuery(sql);
        q.setParameter("parte", "%" + filtro.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<PessoaFisica> listaFiscalTributario(String parte) {
        String sql;
        sql = " SELECT p.*, pf.* " +
            " FROM USUARIOSISTEMA US " +
            "         INNER JOIN PESSOAFISICA PF ON US.PESSOAFISICA_ID = PF.ID " +
            "         INNER JOIN PESSOA P ON US.PESSOAFISICA_ID = P.ID " +
            "         INNER JOIN VIGENCIATRIBUSUARIO VTU ON US.ID = VTU.USUARIOSISTEMA_ID " +
            "         INNER JOIN TIPOUSUARIOTRIBUSUARIO TIPUSUTRIB ON VTU.ID = TIPUSUTRIB.VIGENCIATRIBUSUARIO_ID " +
            " WHERE (TRUNC(VTU.VIGENCIAFINAL) > TRUNC(SYSDATE) OR VTU.VIGENCIAFINAL IS NULL) " +
            " and (LOWER(PF.NOME) LIKE :parte OR LOWER(PF.CPF) LIKE :parte) " +
            "  AND TIPUSUTRIB.TIPOUSUARIOTRIBUTARIO = :tipoUsuario ";
        Query q = em.createNativeQuery(sql, PessoaFisica.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipoUsuario", TipoUsuarioTributario.FISCAL_TRIBUTARIO.name());
        return q.getResultList();
    }

    public List<PessoaFisica> listaAdvogado(String filtro) {
        String sql;
        sql = "SELECT pf.nome, classe.numerodocumento  "
            + " FROM vinculofp vp "
            + " INNER JOIN matriculafp mat ON mat.id = vp.matriculafp_id"
            + " INNER JOIN contratofp con ON con.id = vp.id "
            + " INNER JOIN conselhoclassecontratofp classe ON con.conselhoclassecontrato_id = classe.id"
            + " INNER JOIN pessoafisica pf ON pf.id = mat.pessoa_id"
            + " WHERE current_date BETWEEN coalesce(vp.iniciovigencia,current_date) AND coalesce(vp.finalvigencia,current_date)"
            + " AND ((lower (pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte))";
        Query q = em.createNativeQuery(sql, PessoaFisica.class);
        q.setParameter("parte", "%" + filtro.toLowerCase().trim() + "%");
        Util u = new Util();
        u.imprimeSQL(sql, q);
        return q.getResultList();
    }

    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public List<PessoaFisica> buscarNutricionistas(String parte) {
        String sql;
        sql = " SELECT DISTINCT PF.id, PF.NOME, CCO.SIGLA, CONT.NUMERODOCUMENTO  " +
            " FROM PESSOAFISICA PF " +
            "   INNER JOIN PESSOA P ON P.ID = PF.ID " +
            "         left join matriculafp mat on PF.ID = mat.PESSOA_ID " +
            "         left join VINCULOFP vfp on mat.ID = vfp.MATRICULAFP_ID " +
            "         left join CONSELHOCLASSECONTRATOFP cont on PF.ID = cont.PESSOAFISICA_ID " +
            "         left join CONSELHOCLASSEORDEM cco on cont.CONSELHOCLASSEORDEM_ID = cco.ID " +
            " where ((lower (pf.nome) LIKE :param) OR (lower(cont.numerodocumento) LIKE :param)) " +
            " AND (to_date(:dataOperacao, 'dd/MM/yyyy') between coalesce(trunc(vfp.iniciovigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    and coalesce(trunc(vfp.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    AND cco.SIGLA = 'CRN') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(10);
        List<Object[]> resultList = q.getResultList();
        return resultList.stream().map(obj -> new PessoaFisica(((Number) obj[0]).longValue(), (String) obj[1], (String) obj[2], (String) obj[3])).collect(Collectors.toList());
    }

    public String recuperarCRN(Long idNutricionista) {
        String sql = " SELECT DISTINCT CCO.SIGLA, CONT.NUMERODOCUMENTO " +
            " FROM PESSOAFISICA PF " +
            "         INNER JOIN PESSOA P ON P.ID = PF.ID " +
            "         left join matriculafp mat on PF.ID = mat.PESSOA_ID " +
            "         left join VINCULOFP vfp on mat.ID = vfp.MATRICULAFP_ID " +
            "         left join CONSELHOCLASSECONTRATOFP cont on PF.ID = cont.PESSOAFISICA_ID " +
            "         left join CONSELHOCLASSEORDEM cco on cont.CONSELHOCLASSEORDEM_ID = cco.ID " +
            " WHERE pf.id = :idNutricionista ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idNutricionista", idNutricionista);
        List<Object[]> resultado = q.getResultList();
        String retorno = "";
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                retorno += (String) obj[0];
                retorno += " ";
                retorno += (String) obj[1];
            }
        }
        return retorno;
    }

    @Override
    public PessoaFisica recuperar(Object id) {
        PessoaFisica pf = em.find(PessoaFisica.class, id);
        pf.getTelefones().size();
        pf.getDependentes().size();
        pf.getDocumentosPessoais().size();
        pf.getEnderecoscorreio().size();
        pf.getContaCorrenteBancPessoas().size();
        pf.getFormacoes().size();
        pf.getHabilidades().size();
        pf.getPerfis().size();
        pf.getItemTempoContratoFPPessoa().size();
        pf.getDependentes().size();
        for (Dependente dependente : pf.getDependentes()) {
            dependente.getDependentesVinculosFPs().size();
        }
        Hibernate.initialize(pf.getConselhoClasseContratos());
        return pf;
    }

    public PessoaFisica recuperarSimples(Object id) {
        return em.find(PessoaFisica.class, id);
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

    public List<PessoaFisica> listaFiltrandoPorMatricula(String s) {
        String hql = "SELECT DISTINCT obj.id FROM PessoaFisica obj INNER JOIN MatriculaFP x ON obj.id= x.PESSOA_ID WHERE lower(obj.nome) LIKE :filtro";
        Query q = getEntityManager().createNativeQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(20);
        List<BigDecimal> ids = q.getResultList();
        List<PessoaFisica> pessoas = new ArrayList<>();
        for (BigDecimal id : ids) {
            pessoas.add(em.find(PessoaFisica.class, id.longValue()));
        }
        return pessoas;
    }

    public void salvar(PessoaFisica entity, List<ContaCorrenteBancPessoa> listaExcluidos) {
        try {
            for (ContaCorrenteBancPessoa c : listaExcluidos) {
                c = em.find(ContaCorrenteBancPessoa.class, c.getId());
                em.remove(c);
            }
            getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    public List<PessoaFisica> listaPessoaPorMatriculaFP(String parte) {
//        String hql = "select p from PessoaFisica p, MatriculaFP m where m.pessoa = p "
//                + "and lower(p.nome) like :parte";
        String hql = " select pf from MatriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where lower(pf.nome) like :parte "
            + " or (replace(replace(pf.cpf,'.',''),'-','') like :cpfCnpj) ";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("cpfCnpj", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<DocumentoPessoal> recuperarDocumentosPessoais(PessoaFisica p) {
        p = recuperar(p.getId());
        p.getDocumentosPessoais().size();
        return p.getDocumentosPessoais();
    }

    public List<Telefone> recuperarTelefonesPessoa(Pessoa p) {
        p = recuperar(p.getId());
        p.getTelefones().size();
        return p.getTelefones();
    }

    public List<EnderecoCorreio> buscarEnderecoPessoa(Pessoa p) {
        p = recuperar(p.getId());
        p.getEnderecoscorreio().size();
        return p.getEnderecoscorreio();
    }

    public boolean isCPFValido(String s_aux) {
        if (s_aux.isEmpty()) {
            return true;
        } else {
            s_aux = s_aux.replace(".", "");
            s_aux = s_aux.replace("-", "");
            //Rotina para CPF
            if (s_aux.length() == 11) {
                int d1, d2;
                int digito1, digito2, resto;
                int digitoCPF;
                String nDigResult;
                d1 = d2 = 0;
                digito1 = digito2 = resto = 0;
                for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++) {
                    digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count)).intValue();
                    //Multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                    d1 = d1 + (11 - n_Count) * digitoCPF;
                    //Para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                    d2 = d2 + (12 - n_Count) * digitoCPF;
                }
                ;
                //Primeiro resto da divisão por 11.
                resto = (d1 % 11);
                //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito1 = 0;
                } else {
                    digito1 = 11 - resto;
                }
                d2 += 2 * digito1;
                //Segundo resto da divisão por 11.
                resto = (d2 % 11);
                //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito2 = 0;
                } else {
                    digito2 = 11 - resto;
                }
                //Digito verificador do CPF que está sendo validado.
                String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
                //Concatenando o primeiro resto com o segundo.
                nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
                //Comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
                return nDigVerific.equals(nDigResult);
            } //Rotina para CNPJ
            else {
                return false;
            }
        }
    }

    public List<PessoaFisica> listaFiltrandoPorTipoPerfil(String parte, PerfilEnum perfilEnum) {
        String hql = "select new PessoaFisica(p.id, p.nome, p.cpf) from PessoaFisica p join p.perfis per where :perfil IN(per) "
            + " and ((lower(p.nome) like :parte) "
            + " or (replace(replace(p.cpf,'.',''),'-','') like :filtroCpf))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("filtroCpf", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setParameter("perfil", perfilEnum.name());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PessoaFisica> buscarPessoasPorTipoPerfilAndAtiva(String parte, PerfilEnum perfilEnum) {
        String hql = "select new PessoaFisica(p.id, p.nome, p.cpf) from PessoaFisica p join p.perfis per where :perfil IN(per) "
            + " and p.situacaoCadastralPessoa = :situacao "
            + " and ((lower(p.nome) like :parte) "
            + " or (replace(replace(p.cpf,'.',''),'-','') like :filtroCpf))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("filtroCpf", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setParameter("perfil", perfilEnum.name());
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PessoaFisica> buscarPessoasAtivas(String parte) {
        String hql = "select new PessoaFisica(p.id, p.nome, p.cpf) from PessoaFisica p " +
            " where p.situacaoCadastralPessoa = :situacao "
            + " and ((lower(p.nome) like :parte) "
            + " or (replace(replace(p.cpf,'.',''),'-','') like :filtroCpf))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("filtroCpf", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PerfilEnum> listaPerfisPessoa(PessoaFisica pessoaFisica) {
        String hql = "SELECT p.* FROM pessoa_perfil p WHERE p.id_pessoa = :pessoa";
        Query q = em.createNativeQuery(hql);
        q.setParameter("pessoa", pessoaFisica.getId());
        List<PerfilEnum> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista;
        }
        return new ArrayList<>();
    }

    public List<PessoaFisica> listaFiltrandoTodasPessoasByNomeAndCpf(String s) {
        Query q = getEntityManager().createQuery("select obj from PessoaFisica obj "
            + " where lower(obj.nome) like :filtro "
            + " or lower(obj.cpf) like :filtro "
            + " or (replace(replace(obj.cpf,'.',''),'-','') like :filtro) ");
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public boolean isPessoaNoPerfil(PessoaFisica pessoaFisica, PerfilEnum perfilEnum) {
        String hql = "select new PessoaFisica(p.id, p.nome) from PessoaFisica p join p.perfis per where :perfil IN(per) "
            + " and p = :pessoaFisica";
        Query q = em.createQuery(hql);
        q.setParameter("perfil", perfilEnum.name());
        q.setParameter("pessoaFisica", pessoaFisica);

        if (!q.getResultList().isEmpty()) {
            return true;
        }

        return false;
    }

    public List<String> listaCEPString(String parte) {
        Query q = em.createQuery("select distinct replace(replace(replace(log.cep,'.',''),'-',''),'/','') from CEPLogradouro log where log.cep like :parte");
        q.setParameter("parte", "%" + parte + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PessoaFisica> listaPessoaFisica(String filtro) {
        Query q = em.createQuery("from PessoaFisica obj "
            + " where lower(obj.nome) like :filtro or lower(obj.cpf) like :filtro");
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public SiprevControlador.PessoaFisicaSiprev buscarPessoaFisicaSiprev(Long id) {
        String sql = " SELECT PF.NOME, PF.CPF, RG.NUMERO, PF.DATANASCIMENTO, PF.MAE, CARTEIRA.PISPASEP FROM PESSOAFISICA PF " +
            " INNER JOIN DOCUMENTOPESSOAL DOCRG ON DOCRG.PESSOAFISICA_ID = PF.ID " +
            " INNER JOIN DOCUMENTOPESSOAL DOCNIT ON DOCNIT.PESSOAFISICA_ID = PF.ID " +
            " LEFT JOIN RG ON RG.ID = DOCRG.ID " +
            " INNER JOIN CARTEIRATRABALHO CARTEIRA ON CARTEIRA.ID = DOCNIT.ID" +
            " where pf.id = :id";

        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        return retornoPessoaFisicaSiprev(q.getResultList());
    }

    private SiprevControlador.PessoaFisicaSiprev retornoPessoaFisicaSiprev(List<Object[]> objeto) {
        SiprevControlador.PessoaFisicaSiprev pessoaFisicaSiprev = new SiprevControlador.PessoaFisicaSiprev();
        for (Object[] obj : objeto) {
            pessoaFisicaSiprev.setNome((String) obj[0]);
            pessoaFisicaSiprev.setCpf(obj[1] != null ? (String) obj[1] : null);
            pessoaFisicaSiprev.setRg(obj[2] != null ? (String) obj[2] : null);
            pessoaFisicaSiprev.setDataNascimento(obj[3] != null ? (Date) obj[3] : null);
            pessoaFisicaSiprev.setNomeMae(obj[4] != null ? (String) obj[4] : null);
            pessoaFisicaSiprev.setpISPASEP(obj[5] != null ? (String) obj[5] : null);
        }
        return pessoaFisicaSiprev;
    }

    public Long buscarIdDePessoaPorCpf(String cpf) {
        String sql = "select id from pessoafisica pf where (replace(replace(pf.cpf,'.',''),'-','') like :cpf)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cpf", cpf.replace(".", "").replace("-", ""));
        q.setMaxResults(1);
        try {
            return Long.parseLong("" + q.getSingleResult());
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Long buscarPessoaPeloCPFComVinculoVigente(String cpf, Date dataOperacao) {
        String sql = " select obj.id " +
            " from VinculoFP obj " +
            " inner join matriculafp mat on obj.MATRICULAFP_ID = mat.ID " +
            " inner join pessoa p on p.id = mat.PESSOA_ID " +
            " inner join pessoaFisica pf on p.id = pf.id " +
            " where replace(replace(pf.CPF,'.',''),'-','') like :cpf ";
        if (dataOperacao != null) {
            sql += " and :dataOperacao between trunc(obj.inicioVigencia) and coalesce(trunc(obj.finalVigencia),:dataOperacao) ";
        }
        sql += " order by obj.inicioVigencia desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cpf", cpf.replace(".", "").replace("-", ""));
        if (dataOperacao != null) {
            q.setParameter("dataOperacao", dataOperacao);
        }
        q.setMaxResults(1);
        try {
            return Long.parseLong("" + q.getSingleResult());
        } catch (NoResultException nre) {
            logger.error("Erro ao recuperar a pessoa pelo CPF com Vinculo Vigente: " + nre.getMessage());
            return null;
        }
    }

    public PessoaFisica buscarPessoaPeloCPF(String cpf) {
        String sql = " select p.id, p.dataregistro, p.email, p.homepage, p.contacorrentecontribuinte_id, p.nacionalidade_id, " +
            "       p.situacaocadastralpessoa, p.migracaochave, p.unidadeorganizacional_id, p.motivo, p.bloqueado, " +
            "       p.classepessoa, p.unidadeexterna_id, p.observacao, p.enderecoprincipal_id, p.profissao_id, " +
            "       p.telefoneprincipal_id, p.contacorrenteprincipal_id, p.detentorarquivocomposicao_id, " +
            "       p.arquivo_id, p.codigocnaebi, p.receitabrutacprb, p.key, " +
            "       pf.cpf, pf.datanascimento, pf.deficientefisico, pf.doadorsangue, pf.estadocivil, pf.mae, pf.nome, pf.pai, " +
            "       pf.racacor, pf.sexo, pf.tiposanguineo, pf.naturalidade_id, pf.nivelescolaridade_id, pf.tipodeficiencia, " +
            "       pf.anochegada, pf.datainvalidez, pf.nomeabreviado, pf.nometratamento, pf.nacionalidadepai_id, " +
            "       pf.nacionalidademae_id, pf.esferagoverno_id, pf.viveuniaoestavel, pf.situacaoqualificacaocadastral, " +
            "       pf.tipocondicaoingresso, pf.casadobrasileiro, pf.filhobrasileiro, pf.cotadeficiencia, " +
            "       pf.observacaocotadeficiencia, pf.acumulacargo, pf.orgao, pf.localtrabalho " +
            "    from pessoa p " +
            "   inner join pessoafisica pf on p.id = pf.id " +
            " where replace(replace(replace(pf.cpf, '.'), '-'), '/') = replace(replace(replace(:cpf, '.'), '-'), '/') " +
            "  order by p.id desc ";
        Query q = em.createNativeQuery(sql, PessoaFisica.class);
        q.setParameter("cpf", cpf);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            PessoaFisica pessoaFisica = null;
            for (PessoaFisica pf : (List<PessoaFisica>) resultList) {
                pessoaFisica = pf;
                if (SituacaoCadastralPessoa.ATIVO.equals(pf.getSituacaoCadastralPessoa())) {
                    break;
                }
            }
            Hibernate.initialize(pessoaFisica.getTelefones());
            Hibernate.initialize(pessoaFisica.getEnderecoscorreio());
            return pessoaFisica;
        }
        return null;
    }

    public List<PessoaFisica> buscaPessoaFiltrandoAtributosVinculoMatriculaFP(String s) {
        String hql = " select distinct new PessoaFisica(pessoa.id, pessoa.nome)  " +
            " from VinculoFP obj " +
            " inner join obj.matriculaFP matricula " +
            " inner join matricula.pessoa pessoa " +
            " left join matricula.unidadeMatriculado unidade " +
            " where ((lower(pessoa.nome) like :filtro) " +
            " OR (lower(unidade.descricao) like :filtro) " +
            " OR (lower(obj.numero) like :filtro) " +
            " OR (lower(matricula.matricula) like :filtro) " +
            " OR (replace(replace(pessoa.cpf,'.',''),'-','') = :cpf)) " +
            " order by pessoa.nome ";
        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(s));
        return q.getResultList();

    }

    public List<PessoaFisica> buscaPessoaPrestadorServicosOrVinculoFiltrando(String s) {
        String hql = " select distinct new PessoaFisica(pf.id, pf.nome)  " +
            " from PessoaFisica pf " +
            " where (pf.id in (select prestador.prestador.id from PrestadorServicos prestador)" +
            " and ((lower(pf.nome) like :filtro) " +
            " OR (replace(replace(pf.cpf,'.',''),'-','') = :cpf))) " +
            " or (pf.id in (select mat.pessoa.id from VinculoFP vinc " +
            "                inner join vinc.matriculaFP mat " +
            "                   where mat.pessoa.id = pf.id " +
            "                       and ((lower(mat.pessoa.nome) like :filtro)" +
            "                           or (lower(vinc.numero) like :filtro)" +
            "                           or (lower(mat.matricula) like :filtro)" +
            "                           or (replace(replace(mat.pessoa.cpf,'.',''),'-','') = :cpf))))" +
            " order by pf.nome ";
        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(s));
        return q.getResultList();
    }

    public List<PessoaFisica> buscaPessoaPrestadorServicosFiltrandoCpfOrNome(String s) {
        String hql = " select distinct new PessoaFisica(pf.id, pf.nome)  " +
            " from PessoaFisica pf " +
            " where pf.id in (select prestador.prestador.id from PrestadorServicos prestador)" +
            " and ((lower(pf.nome) like :filtro) " +
            " OR (replace(replace(pf.cpf,'.',''),'-','') = :cpf)) " +
            " order by pf.nome ";
        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(s));
        return q.getResultList();
    }

    public List<PessoaFisica> buscarPessoasAtivasPorTiposPerfil(String parte, PerfilEnum... perfisEnum) {
        String hql = " select new PessoaFisica(p.id, p.nome, p.cpf) "
            + " from PessoaFisica p "
            + " inner join p.perfis per "
            + " where per in (:perfil) "
            + "     and p.situacaoCadastralPessoa = :situacao "
            + "     and ((lower(p.nome) like :parte) "
            + "         or (replace(replace(p.cpf,'.',''),'-','') like :filtroCpf))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("filtroCpf", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setParameter("perfil", Lists.newArrayList(perfisEnum));
        q.setParameter("situacao", SituacaoCadastralPessoa.ATIVO);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public PessoaFisica recuperarComHabilidades(Long id) {
        PessoaFisica pf = super.recuperar(id);
        if (!CollectionUtils.isEmpty(pf.getHabilidades())) {
            pf.getHabilidades().size();
        }
        return pf;
    }

    public PessoaFisica recuperarComFormacoes(Long id) {
        PessoaFisica pf = super.recuperar(id);
        if (!CollectionUtils.isEmpty(pf.getFormacoes())) {
            pf.getFormacoes().size();
        }
        return pf;
    }

    public List<ClassificacaoInscricao> atualizarDadosPessoaisViaConcurso(ClassificacaoConcurso selecionado) {
        List<ClassificacaoInscricao> naoAtualizados = Lists.newLinkedList();
        for (ClassificacaoInscricao classificacaoInscricao : selecionado.getInscricoes()) {
            InscricaoConcurso inscricaoConcurso = classificacaoInscricao.getInscricaoConcurso();
            Long idPessoa = buscarIdDePessoaPorCpf(inscricaoConcurso.getCpf());
            if (idPessoa != null) {
                //Se pessoa já existir, então não atualiza nada.
                naoAtualizados.add(classificacaoInscricao);
            } else {
                criarPessoaFisicaFromConcurso(inscricaoConcurso);
            }
        }
        return naoAtualizados;
    }

    public List<ClassificacaoInscricao> atualizarDadosPessoaisViaConcursoImportacaoSimples(List<ClassificacaoInscricao> classificacoes) {
        List<ClassificacaoInscricao> naoAtualizados = Lists.newLinkedList();
        for (ClassificacaoInscricao classificacaoInscricao : classificacoes) {
            InscricaoConcurso inscricaoConcurso = classificacaoInscricao.getInscricaoConcurso();
            Long idPessoa = buscarIdDePessoaPorCpf(inscricaoConcurso.getCpf());
            if (idPessoa != null) {
                //Se pessoa já existir, então não atualiza nada.
                naoAtualizados.add(classificacaoInscricao);
            } else {
                criarPessoaFisicaFromConcurso(inscricaoConcurso);
            }
        }
        return naoAtualizados;
    }

    private void criarPessoaFisicaFromConcurso(InscricaoConcurso inscricaoConcurso) {
        PessoaFisica pf = new PessoaFisica();
        pf.getPerfis().add(PerfilEnum.PERFIL_RH);
        PessoaFisica pessoaFisica = transferirDadosDoCandidatoParaPessoaFisica(pf, inscricaoConcurso);
        salvarNovo(pessoaFisica);

    }


    private PessoaFisica transferirDadosDoCandidatoParaPessoaFisica(PessoaFisica selecionado, InscricaoConcurso inscricao) {

        selecionado.setNome(inscricao.getNome());
        selecionado.setMae(inscricao.getNomeMae());
        selecionado.setDataNascimento(inscricao.getDataNascimento());
        selecionado.setSexo(inscricao.getSexo());
        selecionado.setEmail(inscricao.getEmail());
        selecionado.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
        selecionado.setCpf(inscricao.getCpf());
        if (inscricao.getDeficienteFisico()) {
            selecionado.setDeficienteFisico(inscricao.getDeficienteFisico());
            selecionado.setTipoDeficiencia(TipoDeficiencia.NAO_PORTADOR);
        }
        selecionado.setDeficienteFisico(inscricao.getDeficienteFisico());

        if (inscricao.getEnderecoCorreio() != null) {
            inscricao.getEnderecoCorreio().setPrincipal(Boolean.TRUE);
            selecionado.getEnderecos().add(inscricao.getEnderecoCorreio());
        }
        if (inscricao.getRg() != null) {
            selecionado.getDocumentosPessoais().add(inscricao.getRg());
        }
        Telefone fonePrincipial = inscricao.getTelefoneAsEntidadeTelefone(inscricao.getTelefone(), TipoTelefone.FIXO);
        Telefone celular = inscricao.getTelefoneAsEntidadeTelefone(inscricao.getCelular(), TipoTelefone.CELULAR);
        if (fonePrincipial != null) {
            fonePrincipial.setPessoa(selecionado);
            selecionado.setTelefones(Util.adicionarObjetoEmLista(selecionado.getTelefones(), fonePrincipial));
        }
        if (celular != null) {
            celular.setPessoa(selecionado);
            selecionado.setTelefones(Util.adicionarObjetoEmLista(selecionado.getTelefones(), celular));
        }
        return selecionado;
    }

    public PessoaFisica recuperarComDocumentos(Object id) {
        PessoaFisica pf = em.find(PessoaFisica.class, id);
        pf.getDocumentosPessoais().size();
        return pf;
    }

    public PessoaFisica recuperarComDocumentosAndDependentes(Object id) {
        PessoaFisica pessoaFisica = em.find(PessoaFisica.class, id);
        Hibernate.initialize(pessoaFisica.getDocumentosPessoais());
        Hibernate.initialize(pessoaFisica.getDependentesAtivos());

        for (Dependente dependente : pessoaFisica.getDependentesAtivos()) {
            Hibernate.initialize(dependente.getDependentesVinculosFPs());
        }
        return pessoaFisica;
    }

    public PessoaFisica buscarPessoaFisicaDoBeneficio(VinculoFP vinculo, BeneficioPensaoAlimenticia beneficioPensaoAlimenticia, CreditoSalario creditoSalario) {
        PessoaFisica pessoaFisica = null;
        try {
            if (buscarContaCorrenteDaPessoa(vinculo, beneficioPensaoAlimenticia.getPessoaFisicaBeneficiario(), creditoSalario) != null) {
                pessoaFisica = beneficioPensaoAlimenticia.getPessoaFisicaBeneficiario();
            } else if (buscarContaCorrenteDaPessoa(vinculo, beneficioPensaoAlimenticia.getPessoaFisicaResponsavel(), creditoSalario) != null) {
                pessoaFisica = beneficioPensaoAlimenticia.getPessoaFisicaResponsavel();
            }
        } catch (NullPointerException ex) {
        }
        return pessoaFisica;
    }

    public PessoaFisica recuperarComConta(Object id) {
        PessoaFisica pf = em.find(PessoaFisica.class, id);
        pf.getContaCorrenteBancPessoas().size();
        return pf;
    }

    public ContaCorrenteBancaria buscarContaCorrenteDaPessoa(VinculoFP vinculo, PessoaFisica pessoa, CreditoSalario selecionado) {
        pessoa = recuperarComConta(pessoa.getId());
        List<ContaCorrenteBancPessoa> contasDaPessoa = pessoa.getContaCorrenteBancPessoas();
        if (vinculo.getContaCorrente() != null && SituacaoConta.ATIVO.equals(vinculo.getContaCorrente().getSituacao())) {
            return vinculo.getContaCorrente();
        }
        if (contasDaPessoa == null) {
            return null;
        }
        ordenarContasPorId(contasDaPessoa);

        // Busca conta principal ativa como prioridade
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contasDaPessoa) {
            if (SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao()) && contaCorrenteBancPessoa.getPrincipal()) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
        }

        //Busca a conta ativa do mesmo banco
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contasDaPessoa) {
            if (contaCorrenteBancPessoa.getAgencia().getBanco().equals(selecionado.getContaBancariaEntidade().getAgencia().getBanco()) && SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
        }

        //Busca a conta ativa
        for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contasDaPessoa) {
            if (SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                return contaCorrenteBancPessoa.getContaCorrenteBancaria();
            }
        }

        return null;
    }

    private void ordenarContasPorId(List<ContaCorrenteBancPessoa> contasDaPessoa) {
        Collections.sort(contasDaPessoa, new Comparator<ContaCorrenteBancPessoa>() {
            @Override
            public int compare(ContaCorrenteBancPessoa o1, ContaCorrenteBancPessoa o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
    }
}
