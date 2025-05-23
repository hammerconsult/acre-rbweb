/*
 * Codigo gerado automaticamente em Mon Feb 28 11:53:23 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.TipoEntidade;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class EntidadeFacade extends AbstractFacade<Entidade> {

    @EJB
    ArquivoFacade arquivoFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EsferaGovernoFacade esferaGovernoFacade;
    @EJB
    private CBOFacade CBOFacade;
    @EJB
    private CNAEFacade CNAEFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PagamentoDaGPSFacade pagamentoDaGPSFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private NaturezaJuridicaEntidadeFacade naturezaJuridicaEntidadeFacade;

    public EntidadeFacade() {
        super(Entidade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Entidade recuperar(Object id) {
        Entidade ent = em.find(Entidade.class, id);
        Hibernate.initialize(ent.getPrevidenciaEntidades());
        try {
            ent.getArquivo().getPartes().size();
        } catch (NullPointerException npe) {
        }
        return ent;
    }

    public Entidade recuperarSimples(Object id) {
        return em.find(Entidade.class, id);
    }

    public List<Entidade> listaEntidades(String parte) {
        Query q = em.createQuery("from Entidade obj where lower(obj.nome) like :filtro ");

        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Entidade> listaEntidadesAtivas(String parte) {
        Query q = em.createQuery("from Entidade obj where lower(obj.nome) like :filtro and obj.ativa = 1 order by obj.nome");

        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Entidade> listaEntidadesPorPessoaJuridicaeContratoFP(PessoaJuridica pessoa, ContratoFP contratoFP) {
        Query q = em.createQuery("select cfp from ContratoFP cfp "
            + " inner join cfp.unidadeOrganizacional uo "
            + " inner join uo.entidade ent "
            + " inner join ent.pessoaJuridica pessoa "
            + " where cfp.unidadeOrganizacional.id = uo.id "
            + " and uo.entidade.id = ent.id "
            + " and ent.pessoaJuridica.id = pessoa.id"
            + " and pessoa = :pessoa and cfp = :contrato ");
        q.setParameter("pessoa", pessoa);
        q.setParameter("contrato", contratoFP);
        q.setMaxResults(50);

        //Query q = em.createQuery("from Entidade obj where obj.pessoaJuridica = :pessoa ");
        return q.getResultList();
    }

    public Entidade recuperaEntidadePorCNPJ(String cnpj) {
        String hql = "select e from Entidade e "
            + "inner join e.pessoaJuridica p "
            + "where p.cnpj = :paramCNPJ";
        Query q = em.createQuery(hql);
        q.setParameter("paramCNPJ", cnpj);
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (Entidade) q.getSingleResult();
        }
    }

    public Integer retornaUltimoNumero() {
        String sql = "SELECT max(codigo) FROM entidade";
        Query q = getEntityManager().createNativeQuery(sql);
        BigDecimal num = (BigDecimal) q.getSingleResult();
        if (num != null) {
            return num.intValue();
        } else {
            return 0;
        }
    }

    public boolean verificaSeExisteArquivo(Arquivo arq) {
        Arquivo ar = null;
        if (arq == null) {
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

    public void salvarArquivo(FileUploadEvent fileUploadEvent, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            //arq.setMimeType(arquivoFacade.getMimeType(arquivoRecebido.getFileName()));
            arq.setTamanho(arquivoRecebido.getSize());
            //System.out.println(arq.getDescricao());
            //System.out.println(arq.getMimeType());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            //System.out.println("passou novoARquivo");
            arquivoRecebido.getInputstream().close();
            //System.out.println("passou metodo");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void salvarUnidade(Entidade ent, Arquivo arquivo, FileUploadEvent fileUpload) {
        if (ent.getArquivo() == null) {
            if (verificaSeExisteArquivo(arquivo)) {
                //System.out.println("removeu");
                arquivoFacade.removerArquivo(arquivo);
            }
        }

        if (fileUpload != null) {
            ent.setArquivo(arquivo);
            salvarArquivo(fileUpload, arquivo);
        }
        getEntityManager().merge(ent);
    }


    public boolean valida_CpfCnpj(String s_aux) {
        s_aux = s_aux.replace(".", "");
        s_aux = s_aux.replace("-", "");
        s_aux = s_aux.replace("/", "");

        if (s_aux.length() == 14) {
            int soma = 0, aux, dig;
            String cnpj_calc = s_aux.substring(0, 12);
            char[] chr_cnpj = s_aux.toCharArray();
//--------- Primeira parte
            for (int i = 0; i < 4; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                    soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11)
                ? "0" : Integer.toString(dig);
//--------- Segunda parte
            soma = 0;
            for (int i = 0; i < 5; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                }
            }
            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                    soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                }
            }
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11)
                ? "0" : Integer.toString(dig);
            return s_aux.equals(cnpj_calc);
        } else {
            return false;
        }
    }

    public List<Entidade> buscarEntidadesParaDeclaracoes(CategoriaDeclaracaoPrestacaoContas cat, Date periodoInicial, Date periodoFinal) {
        String hql = " select e from DeclaracaoPrestacaoContas dpc" +
            " inner join dpc.entidadesDPContas ents" +
            " inner join ents.itensEntidaDPContas iecs" +
            " inner join iecs.entidade e" +
            " where dpc.categoriaDeclaracaoPrestacaoContas = :cat" +
            "   and ((trunc(ents.inicioVigencia) >= :inicio and trunc(ents.inicioVigencia) <= :fim)" +
            "    or  (trunc(ents.finalVigencia) >= :inicio and trunc(ents.finalVigencia) <= :fim)" +
            "    or  (trunc(ents.inicioVigencia) <= :inicio and trunc(ents.finalVigencia) >= :fim)) ";
        Query q = em.createQuery(hql);
        q.setParameter("cat", cat);
        q.setParameter("inicio", periodoInicial, TemporalType.DATE);
        q.setParameter("fim", periodoFinal, TemporalType.DATE);
        return q.getResultList();
    }

    public Boolean entidadePossuiUnidadeParaDeclaracao(HierarquiaOrganizacional ho, Entidade e, CategoriaDeclaracaoPrestacaoContas cat, Date inicio, Date fim) {
        String hql = " select e from DeclaracaoPrestacaoContas dpc" +
            " inner join dpc.entidadesDPContas ents" +
            " inner join ents.itensEntidaDPContas iecs" +
            " inner join iecs.itemEntidadeUnidadeOrganizacional ieuo" +
            " inner join iecs.entidade e" +
            " where dpc.categoriaDeclaracaoPrestacaoContas = :cat" +
            "   and ((trunc(ents.inicioVigencia) >= :inicio and trunc(ents.inicioVigencia) <= :fim)" +
            "         or  (trunc(ents.finalVigencia)  >= :inicio and trunc(ents.finalVigencia)  <= :fim)" +
            "         or  (trunc(ents.inicioVigencia) <= :inicio and trunc(ents.finalVigencia)  >= :fim)) " +
            "   and e = :entidade" +
            "   and ieuo.hierarquiaOrganizacional = :ho";
        Query q = em.createQuery(hql);
        q.setParameter("cat", cat);
        q.setParameter("entidade", e);
        q.setParameter("ho", ho);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasDaEntidade(Entidade e, CategoriaDeclaracaoPrestacaoContas cat, Date inicio, Date fim) {
        String hql = " select distinct ho from DeclaracaoPrestacaoContas dpc" +
            " inner join dpc.entidadesDPContas ents" +
            " inner join ents.itensEntidaDPContas iecs" +
            " inner join iecs.itemEntidadeUnidadeOrganizacional ieuo" +
            " inner join iecs.entidade e" +
            " inner join ieuo.hierarquiaOrganizacional ho" +
            " where ((trunc(ents.inicioVigencia)  >= :inicio and trunc(ents.inicioVigencia) <= :fim) " +
            "     or (trunc(ents.finalVigencia )  >= :inicio and trunc(ents.finalVigencia ) <= :fim) " +
            "     or (trunc(ents.inicioVigencia)  <= :inicio and trunc(ents.finalVigencia ) >= :fim))" +
            "   and dpc.categoriaDeclaracaoPrestacaoContas = :cat" +
            "   and e = :entidade";
        Query q = em.createQuery(hql);
        q.setParameter("cat", cat);
        q.setParameter("entidade", e);
        q.setParameter("inicio", inicio, TemporalType.DATE);
        q.setParameter("fim", fim, TemporalType.DATE);
        return q.getResultList();
    }

    public List<Entidade> listaEntidadesPorTipo(TipoEntidade tipoEntidade) {
        String hql = "from Entidade entidade " +
            " where tipoUnidade = :tipoEntidade";
        Query q = em.createQuery(hql);
        q.setParameter("tipoEntidade", tipoEntidade);
        return q.getResultList();
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public EsferaGovernoFacade getEsferaGovernoFacade() {
        return esferaGovernoFacade;
    }

    public CBOFacade getCBOFacade() {
        return CBOFacade;
    }

    public CNAEFacade getCNAEFacade() {
        return CNAEFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PagamentoDaGPSFacade getPagamentoDaGPSFacade() {
        return pagamentoDaGPSFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public NaturezaJuridicaEntidadeFacade getNaturezaJuridicaEntidadeFacade() {
        return naturezaJuridicaEntidadeFacade;
    }

    public Entidade recuperarEntidadePorUnidadeOrganizacional(Long idUnidade) {
        try {
            Query query = em.createNativeQuery(" select ENTIDADE_ID " +
                "                               from VWHIERARQUIAADMINISTRATIVA vw " +
                "                            where VW.SUBORDINADA_ID = :und " +
                "                                  and SYSDATE BETWEEN  VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,SYSDATE)");
            query.setParameter("und", idUnidade);
            return em.find(Entidade.class, ((BigDecimal) query.getSingleResult()).longValue());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    public Entidade recuperarEntidadePorUnidadeOrganizacional(UnidadeOrganizacional unidade) {
        return recuperarEntidadePorUnidadeOrganizacional(unidade.getId());
    }

    public Entidade buscarEntidadePorUnidadeOrganizacionalSemFiltrarVigencia(UnidadeOrganizacional unidade) {
        Query query = em.createNativeQuery(" select DISTINCT ENTIDADE_ID " +
            "                               from VWHIERARQUIAADMINISTRATIVA vw " +
            "                            where VW.SUBORDINADA_ID = :und ");
        query.setParameter("und", unidade.getId());

        if (query.getResultList().isEmpty()) {
            return null;
        }

        return em.find(Entidade.class, ((BigDecimal) query.getSingleResult()).longValue());
    }

    public Entidade recuperarEntidadeDeclarantePorUnidadeOrganizacional(VinculoFP vinculoFP, CategoriaDeclaracaoPrestacaoContas cat) {
        String sql = " select distinct e.* from DeclaracaoPrestacaoContas dpc " +
            " inner join EntidadeDPContas ents on dpc.ID = ents.declaracaoPrestacaoContas_id " +
            " inner join ItemEntidadeDPContas iecs on ents.id = iecs.ENTIDADEDPCONTAS_ID " +
            " inner join entidade e on iecs.ENTIDADE_ID = e.id " +
            " inner join ITEMENTIDADEUNIDADEORG itemUnidade on iecs.id = itemUnidade.ITEMENTIDADEDPCONTAS_ID " +
            " inner join HIERARQUIAORGANIZACIONAL ho on itemUnidade.HIERARQUIAORGANIZACIONAL_ID = ho.id " +
            " inner join UNIDADEORGANIZACIONAL uo on ho.SUBORDINADA_ID = uo.id " +
            " inner join vinculofp vinculo on vinculo.unidadeorganizacional_id = uo.id " +
            " inner join matriculafp matricula on matricula.id = vinculo.matriculafp_id " +
            " where dpc.CATEGORIADECLARACAO = :cat " +
            " and matricula.id = :matricula " +
            " and vinculo.numero = :numero ";
        Query q = em.createNativeQuery(sql, Entidade.class);
        q.setParameter("cat", cat.name());
        q.setParameter("matricula", vinculoFP.getMatriculaFP().getId());
        q.setParameter("numero", vinculoFP.getNumero());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (Entidade) q.getResultList().get(0);
    }

    public Entidade recuperarEntidadePorUnidadeOrcamentaria(UnidadeOrganizacional orcamentaria) {
        Query query = em.createNativeQuery(" select ENTIDADE_ID " +
            "                               from VWHIERARQUIAORCAMENTARIA vw " +
            "                            where VW.SUBORDINADA_ID = :und " +
            "                                  and SYSDATE BETWEEN  VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,SYSDATE)");
        query.setParameter("und", orcamentaria.getId());
        return em.find(Entidade.class, ((BigDecimal) query.getSingleResult()).longValue());
    }

    public Entidade buscarEntidadePorUnidadeOrcamentaria(UnidadeOrganizacional orcamentaria) {
        Query query = em.createNativeQuery(" select ENTIDADE_ID " +
            "                               from VWHIERARQUIAORCAMENTARIA vw " +
            "                            where VW.SUBORDINADA_ID = :und " +
            "                                  and SYSDATE BETWEEN  VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,SYSDATE)");
        query.setParameter("und", orcamentaria.getId());
        try {
            return em.find(Entidade.class, ((BigDecimal) query.getSingleResult()).longValue());
        } catch (Exception ex) {
            return null;
        }
    }

    public Entidade recuperarEntidadeOrcamentaria(Long idUnidade, Date dataOperacao) {
        String sql = " " +
            " select " +
            "   ENTIDADE_ID " +
            " from VWHIERARQUIAORCAMENTARIA vw " +
            " where VW.SUBORDINADA_ID = :und " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN  trunc(VW.INICIOVIGENCIA) AND COALESCE(trunc(VW.FIMVIGENCIA),  to_date(:dataOperacao, 'dd/MM/yyyy'))";
        Query query = em.createNativeQuery(sql);
        query.setParameter("und", idUnidade);
        query.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        try {
            return em.find(Entidade.class, ((BigDecimal) query.getSingleResult()).longValue());
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Entidade> getEntidadeOndeUsuarioEhGestorPatrimonio(String parte, Date data, UsuarioSistema usuarioCorrente) {
        String sql = "   SELECT DISTINCT ET.*  " +
            "     FROM VWHIERARQUIAADMINISTRATIVA VW  " +
            "INNER JOIN ENTIDADE ET ON ET.id = VW.ENTIDADE_ID  " +
            "INNER JOIN unidadeorganizacional u on u.id = VW.subordinada_id  " +
            "INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = u.id  " +
            "    WHERE :DATA BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, :DATA) " +
            "      AND (UPPER(ET.NOME) LIKE :STR  " +
            "       OR ET.SIGLA LIKE :STR)  " +
            "        AND uu.gestorpatrimonio = 1 " +
            "        AND uu.usuariosistema_id = :USUARIO_ID" +

            " ORDER BY ET.NOME";

        Query q = getEntityManager().createNativeQuery(sql.toString(), Entidade.class);

        q.setParameter("STR", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("DATA", data, TemporalType.DATE);
        q.setParameter("USUARIO_ID", usuarioCorrente.getId());

        return q.getResultList();
    }

    /**
     * Buscar a entidade por profundidade, caso a primeira unidade não tenha uma entidade associada,
     * o sistema irá buscar pela ordem da hierarquia até chegar na Raiz da estrutura
     *
     * @param unidade
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Entidade buscarEntidadePorUnidade(UnidadeOrganizacional unidade, Date data) {
        Entidade entidade = buscarEntidadePelaUnidadeOrganizacional(unidade, data);
        if (entidade != null) {
            return entidade;
        }
        return buscarProximaEntidadePorOrdem(unidade, data);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private Entidade buscarProximaEntidadePorOrdem(UnidadeOrganizacional unidade, Date data) {
        HierarquiaOrganizacional hoUnidade = hierarquiaOrganizacionalFacade.buscarOrgaoAdministrativoPorUnidadeAndVigencia(unidade, data);

        String sql = "SELECT DISTINCT ho.* FROM VWhierarquiaAdministrativa vw "
            + " INNER JOIN HierarquiaOrganizacional ho ON ho.id = vw.id "
            + " INNER JOIN UnidadeOrganizacional un ON un.id = vw.subordinada_id "
            + " WHERE to_date(:data,'dd/MM/yyyy') BETWEEN vw.INICIOVIGENCIA AND coalesce(vw.FIMVIGENCIA, to_date(:data,'dd/MM/yyyy')) "
            + " AND vw.codigo like :codigo order by ho.codigo desc ";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("codigo", hoUnidade.getCodigoSemZerosFinais() + "%");
        List<HierarquiaOrganizacional> hos = Lists.newLinkedList(q.getResultList());
        for (HierarquiaOrganizacional ho : hos) {
            Entidade entidade = buscarEntidadePelaUnidadeOrganizacional(ho.getSubordinada(), data);
            if (entidade != null) {
                return entidade;
            }
        }
        HierarquiaOrganizacional rais = hierarquiaOrganizacionalFacade.getRaizHierarquia(data);
        return buscarEntidadePelaUnidadeOrganizacional(rais.getSubordinada(), data);
    }

    private Entidade buscarEntidadePelaUnidadeOrganizacional(UnidadeOrganizacional unidade, Date data) {
        String sql = "SELECT DISTINCT ent.* FROM VWhierarquiaAdministrativa vw "
            + " INNER JOIN HierarquiaOrganizacional ho ON ho.id = vw.id "
            + " INNER JOIN UnidadeOrganizacional un ON un.id = vw.subordinada_id "
            + " INNER JOIN Entidade ent ON ent.id = un.entidade_id "
            + " WHERE to_date(:data,'dd/MM/yyyy') BETWEEN vw.INICIOVIGENCIA AND coalesce(vw.FIMVIGENCIA, to_date(:data,'dd/MM/yyyy')) "
            + " AND ho.subordinada_id = :id ";
        Query q = em.createNativeQuery(sql, Entidade.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("id", unidade.getId());
        List<Entidade> entidades = q.getResultList();
        if (!entidades.isEmpty()) {
            return entidades.get(0);
        }
        return null;
    }

    public List<Entidade> buscarEntidadesDeclarantesVigentePorCategoria(CategoriaDeclaracaoPrestacaoContas categoria) {
        String sql = " SELECT entidade.* " +
            "FROM ENTIDADEDPCONTAS entidadeDP " +
            "  INNER JOIN DECLARACAOPRESTACAOCONTAS declaracao ON entidadeDP.DECLARACAOPRESTACAOCONTAS_ID = declaracao.ID " +
            "  INNER JOIN ITEMENTIDADEDPCONTAS itemEntidadedp ON entidadeDP.ID = itemEntidadedp.ENTIDADEDPCONTAS_ID " +
            "  INNER JOIN ENTIDADE entidade ON itemEntidadedp.ENTIDADE_ID = entidade.ID " +
            "WHERE :dataAtual BETWEEN trunc(entidadeDP.INICIOVIGENCIA) AND coalesce(trunc(entidadeDP.FINALVIGENCIA), :dataAtual) " +
            "      AND declaracao.CATEGORIADECLARACAO = :categoria and entidade.ATIVA = :ativo ";

        Query q = em.createNativeQuery(sql, Entidade.class);
        q.setParameter("dataAtual", UtilRH.getDataOperacao());
        q.setParameter("categoria", categoria.name());
        q.setParameter("ativo", Boolean.TRUE);

        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<Entidade> buscarTodasEntidadesAtivas() {
        String sql = "select entidade.* from Entidade entidade where entidade.ativa = 1 order by entidade.nome";
        Query q = em.createNativeQuery(sql, Entidade.class);
        return q.getResultList();
    }

    public Entidade buscarEntidadePorUnidadeAdministrativa(Long idUnidade, Date data) {
        try {
            Query query = em.createNativeQuery(" select ENTIDADE_ID " +
                "                               from VWHIERARQUIAADMINISTRATIVA vw " +
                "                            where VW.SUBORDINADA_ID = :und " +
                "                                  and to_date(:data,'dd/MM/yyyy')  BETWEEN  VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:data,'dd/MM/yyyy') )");
            query.setParameter("und", idUnidade);
            query.setParameter("data", DataUtil.getDataFormatada(data));
            return em.find(Entidade.class, ((BigDecimal) query.getSingleResult()).longValue());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    public Entidade completarEntidadePelaUnidadeOrganizacional(String parte, UnidadeOrganizacional unidade, Date data) {
        String sql = "SELECT DISTINCT ent.* FROM VWhierarquiaAdministrativa vw "
            + " INNER JOIN UnidadeOrganizacional un ON un.id = vw.subordinada_id "
            + " INNER JOIN Entidade ent ON ent.id = un.entidade_id "
            + " WHERE lower(ent.nome) like :filtro and to_date(:data,'dd/MM/yyyy') BETWEEN vw.INICIOVIGENCIA AND coalesce(vw.FIMVIGENCIA, to_date(:data,'dd/MM/yyyy')) "
            + " AND vw.subordinada_id = :id ";
        Query q = em.createNativeQuery(sql, Entidade.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("id", unidade.getId());
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        List<Entidade> entidades = q.getResultList();
        if (!entidades.isEmpty()) {
            return entidades.get(0);
        }
        return null;
    }

    public Long recuperarEntidadeDoRegistroEsocialPorVinculoFPEAnoETipo(TipoClasseESocial tipoArquivo, Integer ano, Long idVinculoFP) {
        String sql = " select distinct dadosRegistro.entidade_id  as entidade_id " +
            "from vinculofp vinc " +
            "         inner join FICHAFINANCEIRAFP ff on vinc.ID = ff.VINCULOFP_ID " +
            "         inner join MATRICULAFP mat on vinc.MATRICULAFP_ID = mat.ID " +
            "         inner join PESSOAFISICA pf on mat.PESSOA_ID = pf.ID " +
            "         inner join (select ENTIDADE.id               as entidade_id, " +
            "                            registro.DATAREGISTRO       as data,  " +
            "                            registro.IDENTIFICADORWP       identificador " +
            "                    from REGISTROESOCIAL registro " +
            "                             inner join CONFIGEMPREGADORESOCIAL conf on registro.EMPREGADOR_ID = conf.ID " +
            "                             inner join entidade on conf.ENTIDADE_ID = ENTIDADE.ID " +
            "                    where registro.ANOAPURACAO = :ano " +
            "                      and extract(year from registro.DATAREGISTRO) = :ano " +
            "                      and TIPOARQUIVOESOCIAL = :tipoArquivo) dadosRegistro " +
            "                   on dadosRegistro.identificador like '%' || ff.id || '%' " +
            "  and vinc.id = :idVinculofp ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoArquivo", tipoArquivo.name());
        q.setParameter("idVinculofp", idVinculoFP);
        q.setParameter("ano", ano);

        try {
            Object result = q.getSingleResult();
            return result != null ? ((BigDecimal) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long recuperarEntidadeDoRegistroEsocialPorPrestadorServicoEAnoETipo(TipoClasseESocial tipoArquivo, Integer ano, Long idPrestadorServicos) {
        String sql = "  select distinct dadosregistro.entidade_id  as entidade_id " +
            " from ficharpa ficha  " +
            "         inner join prestadorservicos prestador on ficha.prestadorservicos_id = prestador.id  " +
            "         inner join tomadordeservico tomador on ficha.tomador_id = tomador.id  " +
            "         inner join pessoafisica pf on prestador.prestador_id = pf.id " +
            "         inner join (select entidade.id as entidade_id, " +
            "                           registro.identificadorwp       identificador  " +
            "                    from registroesocial registro  " +
            "                             inner join configempregadoresocial conf on registro.empregador_id = conf.id  " +
            "                             inner join entidade on conf.entidade_id = entidade.id  " +
            "                    where registro.anoapuracao = :ano  " +
            "                      and extract(year from registro.dataregistro) = :ano " +
            "                      and tipoarquivoesocial = :tipoarquivo) dadosregistro  " +
            "                   on dadosregistro.identificador like '%' || ficha.id || '%'  " +
            " where extract(year from ficha.dataservico) = :ano " +
            " and prestador.id = :idPrestadorServicos  ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoarquivo", tipoArquivo.name());
        q.setParameter("idPrestadorServicos", idPrestadorServicos);
        q.setParameter("ano", ano);

        try {
            Object result = q.getSingleResult();
            return result != null ? ((BigDecimal) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }
}
