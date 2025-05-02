/*
 * Codigo gerado automaticamente em Tue Apr 12 17:35:39 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.UnidadePortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassificacaoUO;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.TipoEntidade;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import org.hibernate.Hibernate;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("ALL")
@Stateless
public class UnidadeOrganizacionalFacade extends AbstractFacade<UnidadeOrganizacional> {

    @EJB
    ConfiguracaoContabilFacade configuracaoContabilFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
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
    private NaturezaJuridicaEntidadeFacade naturezaJuridicaEntidadeFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ResponsavelUnidadeFacade responsavelUnidadeFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    private boolean enableCampos;

    public UnidadeOrganizacionalFacade() {
        super(UnidadeOrganizacional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean isDependenciaUnidadeOrganizacional(UnidadeOrganizacional unidade) {
        String hql = "from  HierarquiaOrganizacional h where h.subordinada=:unidadeParametro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("unidadeParametro", unidade);
        return !(q.getResultList().size() > 0);
    }

    public String dataAtualFormataDDMMYYYY() {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(new Date());
    }

    public UnidadeOrganizacional getRaiz(Exercicio exerc) {

        Query q = em.createQuery("from HierarquiaOrganizacional h where h.exercicio =:exerc and h.superior is null");
        q.setParameter("exerc", exerc);
        HierarquiaOrganizacional h;
        try {
            HierarquiaOrganizacional hierarquiaOrganizacional = (HierarquiaOrganizacional) q.getSingleResult();
            return hierarquiaOrganizacional.getSubordinada();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public UnidadeOrganizacional getUnidadePelaSubordinada(Long idSubordinada, Date dataInicial, Date dataFinal) {

        String sql = " select uo.* " +
            " from vwhierarquiaorcamentaria vw " +
            " inner join unidadeorganizacional uo on vw.subordinada_id = uo.id " +
            " where vw.subordinada_id = :id " +
            " and to_date(:dataInicial, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("id", idSubordinada);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        try {
            UnidadeOrganizacional unidadeOrganizacional = (UnidadeOrganizacional) q.getSingleResult();
            return unidadeOrganizacional;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verificaInativo(UnidadeOrganizacional unid) {
        Date dataHoje = new Date();

        Query q = em.createQuery("FROM HierarquiaOrganizacional h where h.subordinada = :unidade and (h.fimVigencia < :dataHoje or h.fimVigencia = null )");
        q.setParameter("unidade", unid);
        q.setParameter("dataHoje", dataHoje);
        return q.getResultList().isEmpty();
    }

    public List<UnidadeOrganizacional> getFilhosDe(UnidadeOrganizacional uo) {
        Query q = em.createQuery("select h.subordinada from HierarquiaOrganizacional h  "
            + "where to_char(h.inicioVigencia,'dd/MM/yyyy')<=:dataAtual and h.superior = :superior order by h.codigo");
        q.setParameter("superior", uo);
        q.setParameter("dataAtual", dataAtualFormataDDMMYYYY());
        return q.getResultList();
    }

    public List<HierarquiaOrganizacional> getHierarquiasFilhasDe(UnidadeOrganizacional uo) {
        Query q = em.createQuery("from HierarquiaOrganizacional h  "
            + " where to_char(h.inicioVigencia,'dd/MM/yyyy')<=:dataAtual  and h.superior = :superior order by h.codigo");
        q.setParameter("superior", uo);
        q.setParameter("dataAtual", dataAtualFormataDDMMYYYY());
        return q.getResultList();
    }

    public HierarquiaOrganizacional getPaiDe(UnidadeOrganizacional uo) {
        Query q = em.createQuery(" from HierarquiaOrganizacional h "
            + " where to_char(h.inicioVigencia,'dd/MM/yyyy')<=:dataAtual "
            + " and h.subordinada = :subordinada ");
        q.setParameter("subordinada", uo);
        q.setParameter("dataAtual", dataAtualFormataDDMMYYYY());
        HierarquiaOrganizacional hi = (HierarquiaOrganizacional) q.getSingleResult();
        return hi;
    }

    public int profundidadeSelecionada(HierarquiaOrganizacional hi) {
        UnidadeOrganizacional undSubordinado = hi.getSuperior();
        HierarquiaOrganizacional h = new HierarquiaOrganizacional();
        int total = 0;
        while (undSubordinado != null) {
            Query q = em.createQuery("from HierarquiaOrganizacional h where to_char(h.inicioVigencia,'dd/MM/yyyy')<=:dataAtual and h.subordinada = :sub");
            q.setParameter("sub", undSubordinado);
            q.setParameter("dataAtual", dataAtualFormataDDMMYYYY());
            if (!q.getResultList().isEmpty()) {
                h = (HierarquiaOrganizacional) q.getSingleResult();
                undSubordinado = h.getSuperior();
                total++;
            } else {
                undSubordinado = null;
            }
        }
        return total;
    }

    public void trataHierarquia(HierarquiaOrganizacional ho) throws ExcecaoNegocioGenerica {
        calculaNivelDentroNo(ho);
        atribuiMascara(ho, ho.getNivelNaEntidade());
    }

    public void calculaNivelDentroNo(HierarquiaOrganizacional hierarquia) {
        int nivelLocalizado = 0;
        List lista;
        if (hierarquia.getSuperior() != null) {
            String hql = "from HierarquiaOrganizacional h where h.superior=:sup";
            Query q = em.createQuery(hql);
            q.setParameter("sup", hierarquia.getSuperior());
            lista = q.getResultList();
            if (!lista.isEmpty()) {
                nivelLocalizado = lista.size();
            }
        }
        hierarquia.setNivelNaEntidade(nivelLocalizado + 1);
    }

    public HierarquiaOrganizacional iniciaHierarquia(UnidadeOrganizacional und) {

        HierarquiaOrganizacional ho = new HierarquiaOrganizacional();
        ho.setSubordinada(und);

        return ho;
    }

    public void atribuiMascara(HierarquiaOrganizacional hierarquia, int nivel) throws ExcecaoNegocioGenerica {
        String codigoMontado = "";
        String token = "\\.";
        ConfiguracaoContabil configuracao = configuracaoContabilFacade.configuracaoContabilVigente();
        Integer profundidadeSelcionada = profundidadeSelecionada(hierarquia);
        HierarquiaOrganizacional hierarquiPai = getPaiDe(hierarquia.getSuperior());
        String codigoPai = hierarquiPai.getCodigo();
        String mascara = configuracao.getMascaraUnidadeOrganizacional();
        String[] partesPai = codigoPai.split(token);
        partesPai = codigoPai.split(token);
        Integer profundidadePossiveil = partesPai.length;
        codigoMontado = codigoPai;

        if (profundidadeSelcionada > profundidadePossiveil) {
            String msgErro = "Não é possivel salvar a Unidade Organizacional no nivel selecionado\n";
            msgErro += "A mascara " + mascara + " possibilita apenas " + partesPai.length + " niveis";
            throw new ExcecaoNegocioGenerica(msgErro);
        }

        int x = 0;
        codigoMontado = "";
        String nvlEntidade = nivel + "";
        for (String s : partesPai) {
            if ((profundidadeSelcionada - 1) == x) {
                int numero = s.length() - nvlEntidade.length();
                if (numero == 0) {
                    s = nvlEntidade;
                } else {
                    s = "";
                    for (int i = 0; i < numero; i++) {
                        s = s + "0";
                    }
                    s = s + nvlEntidade;
                }
            }
            //System.out.println("mascara" + s);
            codigoMontado = codigoMontado + s + ".";
            x++;
        }
        codigoMontado = codigoMontado.substring(0, codigoMontado.length() - 1);
        hierarquia.setCodigo(codigoMontado);
    }

    //    public void recalculaCodigoFilhos(HierarquiaOrganizacional h) throws ExcecaoNegocioGenerica {
//
//        List<HierarquiaOrganizacional> listaFilhos = getHierarquiasFilhasDe(h.getSubordinada());
//        int tamanho = listaFilhos.size();
//        int novoNivel;
//        Collections.sort(listaFilhos);
//        for (int x = 0; x < tamanho; x++) {
//            HierarquiaOrganizacional hTemp = listaFilhos.get(x);
//            novoNivel = x + 1;
//            //System.out.println("reculculaFIlhos" + x);
//            hTemp.setNivelNaEntidade(novoNivel);
//            atribuiMascara(hTemp, novoNivel);
//            em.merge(hTemp);
//            recalculaCodigoFilhos(hTemp);
//        }
//        em.flush();
//    }
//
//    public void recalculaCodigoIrmaos(UnidadeOrganizacional superior) throws ExcecaoNegocioGenerica {
//        List<HierarquiaOrganizacional> listaIrmaos = new ArrayList<HierarquiaOrganizacional>();
//        listaIrmaos = getHierarquiasFilhasDe(superior);
//        Collections.sort(listaIrmaos);
//        int tamanho;
//        tamanho = listaIrmaos.size();
//        int novoNivel;
//        for (int x = 0; x < tamanho; x++) {
//            novoNivel = x + 1;
//            //System.out.println("recalculaIRmaos" + x);
//            HierarquiaOrganizacional hTemp = listaIrmaos.get(x);
//            hTemp.setNivelNaEntidade(novoNivel);
//            atribuiMascara(hTemp, novoNivel);
//            em.merge(hTemp);
//        }
//    }

    public void salvarNovaUnidade(UnidadeOrganizacional und, FileUploadEvent fileUpload, Arquivo arquivoNovo) {
        if (fileUpload != null) {
            und.getEntidade().setArquivo(arquivoNovo);
            und.setCodigo(retornaUltimoNumero() + 1);
            salvarArquivo(fileUpload, arquivoNovo);
        }
        und = getEntityManager().merge(und);
        portalTransparenciaNovoFacade.salvarPortal(new UnidadePortal(und));
    }

    public void salvarUnidade(UnidadeOrganizacional und, Arquivo arquivo, FileUploadEvent fileUpload) {
        if (fileUpload != null) {
            und.getEntidade().setArquivo(arquivo);
            salvarArquivo(fileUpload, arquivo);
        }
        getEntityManager().merge(und);
        portalTransparenciaNovoFacade.salvarPortal(new UnidadePortal(und));
    }

    public Integer retornaUltimoNumero() {
        String sql = "SELECT max(codigo) FROM unidadeorganizacional";
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
//
//    public void alteraHierarquia(HierarquiaOrganizacional hi, UnidadeOrganizacional pai, Arquivo arquivo, FileUploadEvent fileUpload) throws ExcecaoNegocioGenerica {
//        trataHierarquia(hi);
//        if (hi.getSubordinada().getEntidade() != null) {
//            if (hi.getSubordinada().getEntidade().getArquivo() == null) {
//                if (verificaSeExisteArquivo(arquivo)) {
//                    //System.out.println("removeu");
//                    arquivoFacade.removerArquivo(arquivo);
//                }
//            }
//        }
//        if (fileUpload != null) {
//            hi.getSubordinada().getEntidade().setArquivo(arquivo);
//            salvarArquivo(fileUpload, arquivo);
//
//        }
//        em.merge(hi.getSubordinada().getEndereco());
//        em.merge(hi.getSubordinada());
//        em.merge(hi);
//        recalculaCodigoFilhos(hi);
//        recalculaCodigoIrmaos(pai);
//        em.flush();
//    }

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

    public List<UnidadeOrganizacional> retornaEntidades() {
        String hql = "select a from UnidadeOrganizacional a where a.entidade is not null";
        Query q = em.createQuery(hql);
//        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> retornaEntidadesPorTipoEntidade(TipoEntidade tipo) {
        String hql = "select a from UnidadeOrganizacional a where a.entidade is not null and a.entidade.tipoUnidade = :tipo";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> retornaUnidadesOrcamentarias(String parte) {
        String hql = "select a from UnidadeOrganizacional a where a.entidade is not null and a.descricao like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setMaxResults(50);
        return q.getResultList();
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

    public List<UnidadeOrganizacional> filtrandoUnidadeorganizacionalEntidade(String parte) {
        String hql = "from UnidadeOrganizacional uo where uo.entidade is not null and lower(uo.descricao) like :filtro order by uo.descricao ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadeOrganizacionalPorEntidade(String parte, Entidade entidade) {
        String hql = "from UnidadeOrganizacional uo where uo.entidade = :ent and (lower(uo.descricao) like :filtro) order by uo.descricao";
        Query q = em.createQuery(hql);
        q.setParameter("ent", entidade);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public HierarquiaOrganizacional getHierarquiaPai(UnidadeOrganizacional uo, Exercicio exercicio) {
        Query q = getEntityManager().createQuery(" select hierarquia from HierarquiaOrganizacional hierarquia "
            + " inner join hierarquia.subordinada unidade "
            + " where unidade = :subordinada "
            + " and hierarquia.exercicio = :exercicio "
            + " and hierarquia.tipoHierarquiaOrganizacional = :tipo ");
        q.setParameter("subordinada", uo);
        q.setParameter("exercicio", exercicio);
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA);

        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (Exception e) {
            return null;

        }
    }

    public List<UnidadeOrganizacional> listaTodosPorFiltro(String parte) {
        String hql = "from UnidadeOrganizacional uo where lower(uo.descricao) like :parte";

        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");

        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadesUsuarioCorrente(UsuarioSistema usu) {
        String sql;
        sql = "SELECT uo.* FROM usuariosistema usuSis "
            + " INNER JOIN UsuarioUnidadeOrganizacio usuuni ON ususis.id = usuuni.usuariosistema_id"
            + " INNER JOIN unidadeorganizacional uo ON usuuni.unidadeorganizacional_id = uo.id"
            + " WHERE usuSis.id = :usuario_id";
        //+ " where (lower (ususis.login) like :param)";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("usuario_id", usu.getId());
        //q.setParameter("param", "%" + usu.getLogin().toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadesOrganizacionalUsuarioCorrente(UsuarioSistema usu, String parte) {
        String sql;
        sql = "SELECT uo.* FROM usuariosistema usuSis "
            + " INNER JOIN UsuarioUnidadeOrganizacio usuuni ON ususis.id = usuuni.usuariosistema_id"
            + " INNER JOIN unidadeorganizacional uo ON usuuni.unidadeorganizacional_id = uo.id"
            + " WHERE usuSis.id = :usuario_id "
            + " AND lower(uo.descricao) LIKE :descricao";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("usuario_id", usu.getId());
        q.setParameter("descricao", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> buscarUnidadesOrganizacionalGestorProtocoloUsuarioCorrente(UsuarioSistema usu, String parte) {
        String sql;
        sql = "SELECT uo.* FROM usuariosistema usuSis "
            + " INNER JOIN UsuarioUnidadeOrganizacio usuuni ON ususis.id = usuuni.usuariosistema_id"
            + " INNER JOIN unidadeorganizacional uo ON usuuni.unidadeorganizacional_id = uo.id"
            + " WHERE usuSis.id = :usuario_id "
            + " AND usuuni.gestorProtocolo = 1 "
            + " AND lower(uo.descricao) LIKE :descricao";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("usuario_id", usu.getId());
        q.setParameter("descricao", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }


    public List<HierarquiaOrganizacional> buscarUnidadesUsuarioCorrenteProtocolo(UsuarioSistema usu) {
        String sql;
        sql = "select hadm.* from usuariosistema usuSis "
            + " inner join usuariounidadeorganizacio usuuni on ususis.id = usuuni.usuariosistema_id "
            + " inner join unidadeorganizacional uo on usuuni.unidadeorganizacional_id = uo.id "
            + " inner join hierarquiaorganizacional hadm on hadm.subordinada_id = uo.id "
            + " where ususis.id = :usuario_id "
            + "  and usuuni.gestorProtocolo = 1"
            + "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(hadm.iniciovigencia) and coalesce(trunc(hadm.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))"
            + "  and hadm.tipohierarquiaorganizacional = :tipo "
            + " order by uo.descricao ";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("usuario_id", usu.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadesUsuarioCorrenteNivel3(UsuarioSistema usu, Date dataOperacao) {
        String sql;
        sql = "SELECT distinct u.* FROM vwhierarquiaadministrativa VW "
            + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID "
            + " inner join vwhierarquiaorcamentaria vworc on hadm.hierarquiaorc_id = vworc.id"
            + " inner join unidadeorganizacional u on u.id = vworc.subordinada_id"
            + " inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = vw.subordinada_id"
            + " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id"
            + " where to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and usu.id = :idUsu";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("idUsu", usu.getId());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataOperacao));
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadesUsuarioCorrenteNivel3(UsuarioSistema usu, String parte, Date data) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT distinct u.* FROM vwhierarquiaorcamentaria vw" +
            " inner join unidadeorganizacional u on u.id = vw.subordinada_id" +
            " inner join usuariounidadeorganizacorc usuund on usuund.unidadeorganizacional_id = vw.subordinada_id" +
            " INNER JOIN usuariosistema usu ON usuund.usuariosistema_id = usu.id"
            + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))");
        sql.append(" and (lower(vw.descricao) like :str  ");
        sql.append(" or vw.CODIGO like :str )");
        sql.append(" and usu.id = :usu ");


        Query q = getEntityManager().createNativeQuery(sql.toString(), UnidadeOrganizacional.class);
        q.setParameter("usu", usu.getId());
        q.setParameter("str", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));

        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadesUsuarioCorrenteNivel1(UsuarioSistema usu, String parte, Date data) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT distinct U.* FROM vwhierarquiaadministrativa VW  "
            + " inner join HIERARQUIAORGANIZACIONAL hadm on hadm.id=VW.ID  "
            + " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON HADM.HIERARQUIAORC_ID = VWORC.ID "
            + " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORC.SUPERIOR_ID = VWORG.SUBORDINADA_ID "
            + " INNER JOIN VWHIERARQUIAORCAMENTARIA VWENT ON VWORG.SUPERIOR_ID = VWENT.SUBORDINADA_ID "
            + " inner join unidadeorganizacional u on u.id = vwENT.subordinada_id "
            + " inner join usuariounidadeorganizacio usuund on usuund.unidadeorganizacional_id = vw.subordinada_id "
            + " inner join usuariosistema usu on usuund.usuariosistema_id = usu.id "
            + " where to_date(:data, 'dd/MM/yyyy') between vw.iniciovigencia  and coalesce(vw.fimvigencia, to_date(:data, 'dd/MM/yyyy'))"
            + " and to_date(:data, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:data, 'dd/MM/yyyy'))");
        sql.append(" and (vworc.descricao like :str  ");
        sql.append(" or vworc.CODIGO like :str )");
        sql.append(" and usu.id = :usu ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), UnidadeOrganizacional.class);
        q.setParameter("usu", usu.getId());
        q.setParameter("str", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaFiltrandoX(String s, int inicio, int max, Boolean somenteEntidades) {

        List<EsferaDoPoder> listaEsferaPoder = new ArrayList<EsferaDoPoder>();
        for (EsferaDoPoder esferaDoPoder : EsferaDoPoder.values()) {
            if (esferaDoPoder.getDescricao().equals(s)) {
                listaEsferaPoder.add(esferaDoPoder);
            }
        }

        List<ClassificacaoUO> listaClassificacao = new ArrayList<ClassificacaoUO>();
        for (ClassificacaoUO classificacaoUO : ClassificacaoUO.values()) {
            if (classificacaoUO.getDescricao().equals(s)) {
                listaClassificacao.add(classificacaoUO);
            }
        }

        if (listaEsferaPoder.isEmpty()) {
            listaEsferaPoder = null;
        }
        if (listaClassificacao.isEmpty()) {
            listaClassificacao = null;
        }

        StringBuilder hql = new StringBuilder();

        hql.append(" select obj from UnidadeOrganizacional obj ");

        if (somenteEntidades) {
            hql.append(" inner join obj.entidade entidade ");
        } else {
            hql.append(" left join obj.entidade entidade ");
        }

        hql.append(" where (lower(obj.descricao) like :filtro) ");
        hql.append(" or (lower(obj.endereco) like :filtro) ");
        hql.append(" or (lower(entidade.nome) like :filtro) ");
        hql.append(" or (obj.esferaDoPoder in (:listaEsferaPoder)) ");
        hql.append(" or (obj.classificacaoUO in (:listaClassificacao)) ");
        hql.append(" order by obj.descricao ");

        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("listaEsferaPoder", listaEsferaPoder);
        q.setParameter("listaClassificacao", listaClassificacao);

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> buscarUnidadesOrganizacionaisNaoVinculadasValidas(TipoHierarquiaOrganizacional tipo, Date fimVigencia, String parte) {

        String sql = " select * from unidadeorganizacional"
            + " where id not in"
            + " (SELECT "
            + "       subordinada_id "
            + " FROM "
            + "     hierarquiaorganizacional "
            + " WHERE "
            + "     tipohierarquiaorganizacional = :tipo "
            + "     AND trunc(:fim) BETWEEN trunc(INICIOVIGENCIA) AND coalesce(trunc(FIMVIGENCIA), trunc(SYSDATE)) " +
            "    )"
            + "     and coalesce(inativo,1) = :inativo"
            + "     AND lower(translate(descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')";
        Query q = getEntityManager().createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("tipo", tipo.name());
        q.setParameter("fim", fimVigencia);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("inativo", Boolean.FALSE);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public UnidadeOrganizacional unidadeOrganizacionalPorEntidade(Entidade entidade) {
        String sql = "select uo.* from UnidadeOrganizacional uo "
            + "inner join Entidade e on uo.entidade_id = e.id "
            + "inner join contabancariaentidade conta on conta.entidade_id = e.id "
            + "and e.id = :ent";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("ent", entidade.getId());

        List<UnidadeOrganizacional> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return new UnidadeOrganizacional();
    }

    public UnidadeOrganizacional merge(UnidadeOrganizacional uo) {
        return em.merge(uo);
    }

    public List<HierarquiaOrganizacional> listaHierarquiasVigentes(Date dataVigencia, String parte) {
        String sql = " SELECT uo.* FROM VWHIERARQUIAORCAMENTARIA VW " +
            " INNER JOIN HierarquiaOrganizacional UO ON UO.ID = VW.ID " +
            " WHERE to_date(:data, 'dd/MM/yyyy') BETWEEN VW.INICIOVIGENCIA  AND COALESCE(VW.FIMVIGENCIA, to_date(:data, 'dd/MM/yyyy')) " +
            " and (lower(vw.descricao) like :parte or replace(vw.codigo, '.','') like :parte)" +
            " order by vw.codigo asc";
        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<UnidadeOrganizacional> listaUnidadesVigentes(Date dataVigencia, String parte) {
        String sql = " SELECT uo.* FROM VWHIERARQUIAORCAMENTARIA VW " +
            " INNER JOIN UnidadeOrganizacional UO ON UO.ID = VW.subordinada_id " +
            " WHERE to_date(:data,'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA  AND COALESCE(VW.FIMVIGENCIA, to_date(:data,'dd/mm/yyyy')) " +
            " and (lower(vw.descricao) like :parteDescricao " +
            " or replace(vw.codigo, '.', '') like :parteCodigo) " +
            " order by vw.codigo desc";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("parteDescricao", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("parteCodigo", parte.trim() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    @Override
    public UnidadeOrganizacional recuperar(Object id) {
        UnidadeOrganizacional uo = super.recuperar(id);
        Hibernate.initialize(uo.getResponsaveisUnidades());
        Hibernate.initialize(uo.getUnidadeGestoraUnidadesOrganizacionais());
        Hibernate.initialize(uo.getAnexos());
        Hibernate.initialize(uo.getAtosNormativos());
        for (UnidadeOrganizacionalAnexo anexo : uo.getAnexos()) {
            Hibernate.initialize(anexo.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return uo;
    }

    public List<UnidadeOrganizacional> filtrarUnidadeOrganizacionaisAtivas(String parte) {
        String hql = "select uo from UnidadeOrganizacional uo where uo.inativo is false and uo.descricao like :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalPorEntidade(Entidade entidade) {
        String hql = "select uo from UnidadeOrganizacional uo "
            + "where uo.entidade = :entidade ";
        Query q = em.createQuery(hql);
        q.setParameter("entidade", entidade);

        List<UnidadeOrganizacional> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisPorTipoEntidade(TipoEntidade tipoEntidade) {
        String hql = "from UnidadeOrganizacional uo " +
            " inner join uo.entidade e " +
            " where e.tipoUnidade = :tipoEntidade";
        Query q = em.createQuery(hql);
        q.setParameter("tipoEntidade", tipoEntidade);
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> unidadesOrganizacionaisPensionista() {
        String hql = "select distinct lot.unidadeOrganizacional from VinculoFP v, Aposentadoria apo, Pensionista pen inner join v.lotacaoFuncionals lot " +
            " where (v.id= apo.id or v.id = pen.id) and :data between lot.inicioVigencia and coalesce(lot.finalVigencia,:data)";

        Query q = em.createQuery(hql);
        q.setParameter("data", UtilRH.getDataOperacao());
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> listaUnidadePorFolha(String parte, Integer mes, Integer ano) {
        String sql = " select unique uo.* "
            + " from vinculofp v inner join matriculafp mat on mat.id = v.matriculafp_id "
            + " inner join contratofp c on c.id = v.id "
            + " inner join pessoafisica pf on pf.id = mat.pessoa_id "
            + " inner join cargo cargo on cargo.id = c.cargo_id "
            + " inner join FichaFinanceiraFP ficha on ficha.vinculofp_id = v.id "
            + " inner join folhadepagamento folha on folha.id  = ficha.folhadepagamento_id "
            + " inner join vwrhcontrato vwrh on vwrh.ID_CONTRATOFP = c.id "
            + " inner join unidadeorganizacional uo on vwrh.ID_UNIDADE_LOTADA = uo.id "
            + " where folha.ano = :ano and folha.mes = :mes and uo.descricao like :parte "
            + " and folha.EFETIVADAEM BETWEEN vwrh.INICIOLOTACAO and coalesce(vwrh.FINALOTACAO, folha.EFETIVADAEM)"
            + " order by uo.descricao";

        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("parte", "%" + parte.trim() + "%");
        new Util().imprimeSQL(sql, q);
        return q.getResultList();
    }

    public List<UnidadeOrganizacional> buscarUnidadePorTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional, String filtro) {
        String sql = "SELECT DISTINCT UO.*, HO.CODIGO FROM UNIDADEORGANIZACIONAL UO " +
            "         INNER JOIN HIERARQUIAORGANIZACIONAL HO ON UO.ID = HO.SUBORDINADA_ID " +
            "         WHERE HO.TIPOHIERARQUIAORGANIZACIONAL = :tipoHierarquiaOrganizacional " +
            "         AND TO_DATE(:data, 'dd/mm/yyyy') BETWEEN HO.INICIOVIGENCIA AND COALESCE(HO.FIMVIGENCIA, TO_DATE(:data, 'dd/mm/yyyy')) " +
            "         AND TRIM(LOWER(UO.DESCRICAO)) LIKE :filtro " +
            "         ORDER BY HO.CODIGO";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("tipoHierarquiaOrganizacional", tipoHierarquiaOrganizacional.name());
        q.setParameter("data", UtilRH.getDataOperacaoFormatada());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public UnidadeOrganizacional buscarUnidadeOrganizacionalPorDescricao(String descricao) {
        String sql = " select uo.* from UnidadeOrganizacional uo where trim(upper(uo.descricao)) like :descricao ";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("descricao", descricao.toUpperCase().trim());
        List<UnidadeOrganizacional> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
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

    public NaturezaJuridicaEntidadeFacade getNaturezaJuridicaEntidadeFacade() {
        return naturezaJuridicaEntidadeFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<ResponsavelUnidade> buscaResponsaveisPorUnidade(UnidadeOrganizacional unidade) {
        return responsavelUnidadeFacade.responsaveisDaUnidade(unidade);
    }
}
