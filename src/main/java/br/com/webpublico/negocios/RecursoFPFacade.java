/*
 * Codigo gerado automaticamente em Wed Jun 29 13:52:58 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class RecursoFPFacade extends AbstractFacade<RecursoFP> {

    private static final Logger logger = LoggerFactory.getLogger(RecursoFPFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public RecursoFPFacade() {
        super(RecursoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Conta recuperaProvisaoFonte(RecursoFP r) {
        Query q = em.createQuery("select c from Conta c inner join c.recursoFPs as r where r.id = :rp ");
        q.setParameter("rp", r.getId());

        return (Conta) q.getSingleResult();
    }

    public List<ProvisaoPPAFonte> listaFiltrandoPPAFonte(Exercicio exe, String s) {
        String hql = " select a from ProvisaoPPAFonte a, ProvisaoPPADespesa b, ProvisaoPPA ce";
        hql += " where ce.exercicio = :paramExer and b.provisaoPPA =ce and a.provisaoPPADespesa =b";
        hql += " and lower(b.contaDeDespesa.descricao) like :filtro or lower(b.provisaoPPA.acao.descricao) like :filtro ";
        Query q = getEntityManager().createQuery(hql);
        //System.out.println(hql + "    " + exe + "    " + s);
        q.setParameter("paramExer", exe);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoContaDespesa(Conta con, String s) {
        String hql = "from Conta co where co.superior=:paramCon";
        hql += " and lower(co.descricao) like :filtro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("paramCon", con);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Conta> listaFiltrandoContaDespesa1(Conta con, String s) {
        String hql = "from Conta c, PlanoDeContasExercicio p where PlanoDeContasExercicio. =:paramCon";
        hql += " and lower(co.descricao) like :filtro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("paramCon", con);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ContaBancariaEntidade> listaFiltrandoContaBancariaEntidade(Conta contaDestinacaoDeRecursos, String s) {
        List<ContaBancariaEntidade> listaFiltrandoContaBancariaEnt = new ArrayList<ContaBancariaEntidade>();
        ContaBancariaEntidade a;
        FonteDeRecursos fon; //FonteDeRecursosDestinacao dest;
        String hql = " select a from ContaBancariaEntidade a, FonteDeRecursos fon, FonteDeRecursosDestinacao dest";
        hql += " WHERE a.fonteDeRecursos = fon and dest.fonteDeRecursos=fon and dest.destinacaoDeRecursos=:paramContaDest";
        hql += " and lower(fon.descricao) like :filtro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("paramContaDest", contaDestinacaoDeRecursos);
        q.setMaxResults(50);
        listaFiltrandoContaBancariaEnt = q.getResultList();
        return listaFiltrandoContaBancariaEnt;
    }

    public List<ContaBancariaEntidade> listaFiltrandoContaBancariaEntidade1(Conta contaDestinacaoDeRecursos, String s) {
        List<ContaBancariaEntidade> listaFiltrandoContaBancariaEnt = new ArrayList<ContaBancariaEntidade>();
        ContaBancariaEntidade a;
        FonteDeRecursos fon; //FonteDeRecursosDestinacao dest;
        String hql = " select a from ContaBancariaEntidade a";
        hql += " WHERE a.fonteDeRecursos = :fon and ";
        hql += " lower(fon.descricao) like :filtro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("fon", listaFiltrandoContaDespesaFonte(contaDestinacaoDeRecursos).getFonteDeRecursos());

        q.setMaxResults(50);
        listaFiltrandoContaBancariaEnt = q.getResultList();
        return listaFiltrandoContaBancariaEnt;
    }

    public List<ContaBancariaEntidade> listaFiltrandoContaBancariaEntidade2(Conta contaDestinacaoDeRecursos) {

        ContaBancariaEntidade a;
        FonteDeRecursos fon; //FonteDeRecursosDestinacao dest;
        String hql = "select cbe from ContaBancariaEntidade cbe, ContaDeDestinacao "
            + "cde inner join cbe.fonteDeRecursos fr where cde.fonteDeRecursos = fr and cde = :conta";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("conta", contaDestinacaoDeRecursos);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ContratoFP> listaFiltrandoContratoServidorRecursoFP(String s) throws ParseException {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        String d = formatador.format(new Date());
        String hql = "select a from ContratoServidor a,PessoaFisica b where lower(b.nome) like :filtro";
        hql += "    and a.servidor=b and :paramData";
        hql += "    between to_char(a.inicioVigencia,'dd/MM/yyyy') and to_char(a.finalVigencia,'dd/MM/yyyy')";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("paramData", d);
        //System.out.println(s + "  ***   " + d);
        //System.out.println(q.getResultList());
        q.setMaxResults(50);
        return q.getResultList();
    }

    @Override
    public RecursoFP recuperar(Object id) {
        RecursoFP f = em.find(RecursoFP.class, id);
        f.getFontesRecursoFPs().size();
        for (FontesRecursoFP fonte : f.getFontesRecursoFPs()) {
            fonte.getFonteEventoFPs().size();
        }
        return f;
    }

    public List<AcaoPPA> listaFiltrandoAcao(String s) {
        String hql = " select a from AcaoPPA a where lower(a.descricao) like :filtro OR lower(a.programa.denominacao) like :filtro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();

    }

    //     public List<Conta> listaFiltrandoContaDespesa(Conta con, String s) {
//        String hql = "from ContaBancariaEntidade c where c.fonteDeRecursos =:paramCon";
//        hql += " and lower(co.descricao) like :filtro";
//        Query q = getEntityManager().createQuery(hql);
//        q.setParameter("paramCon", listaFiltrandoContaDespesaFonte(con).getFonteDeRecursos());
//        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
//        q.setMaxResults(50);
//        return q.getResultList();
//    }
    public ContaDeDestinacao listaFiltrandoContaDespesaFonte(Conta con) {
        String hql = "from ContaDeDestinacao c where c =:paramCon";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("paramCon", con);
        q.setMaxResults(1);
        return (ContaDeDestinacao) q.getSingleResult();
    }

    public boolean verificaUtilizacaoRurso(RecursoFP recurso) {
        Query q = em.createQuery("from FichaFinanceiraFP r where r.recursoFP= :recurso");
        q.setParameter("recurso", recurso);
        logger.debug("verificaUtilizacaoRurso.isEmpty: {}", q.getResultList().isEmpty());
        return q.getResultList().isEmpty();

    }

    public List<RecursoFP> recuperaRecursosPorNumeroOrgao(String numeroOrgao) {
        String hql = "from RecursoFP where codigo like :numeroOrgao " +
            //" and :vigencia between inicioVigencia and coalesce(finalVigencia, :vigencia)" +
            " order by codigo";
        Query q = em.createQuery(hql);
        q.setParameter("numeroOrgao", numeroOrgao + "%");
        //q.setParameter("vigencia", UtilRH.getDataOperacao());

        return q.getResultList();
    }


    public List<RecursoFP> retornaRecursoFP(String parte, HierarquiaOrganizacional ho, Date data) {
        String sql = "select distinct rec.* from vinculofp vinculo " +
            " inner join contratofp contrato on contrato.id = vinculo.id " +
            " inner join lotacaoFuncional lotacao on lotacao.vinculofp_id = contrato.id " +
            " inner join unidadeorganizacional un on un.id = lotacao.unidadeorganizacional_id " +
            " inner join hierarquiaorganizacional ho on ho.subordinada_id = un.id " +
            " inner join recursodovinculofp rec_vin on rec_vin.vinculofp_id = vinculo.id " +
            " inner join recursofp rec on rec.id = rec_vin.recursofp_id " +
            " where to_date(:data, 'dd/MM/yyyy') between ho.inicioVigencia  and coalesce(ho.fimVigencia, to_date(:data, 'dd/MM/yyyy'))" +
            " and to_date(:data, 'dd/MM/yyyy') between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia, to_date(:data, 'dd/MM/yyyy'))" +
            " and to_date(:data, 'dd/MM/yyyy') between rec_vin.inicioVigencia and coalesce(rec_vin.finalVigencia, to_date(:data, 'dd/MM/yyyy'))" +
            " and to_date(:data, 'dd/MM/yyyy') between rec.inicioVigencia and coalesce(rec.finalVigencia, to_date(:data, 'dd/MM/yyyy'))" +
            " and to_date(:data, 'dd/MM/yyyy') between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia, to_date(:data, 'dd/MM/yyyy'))" +
            " and ho.codigo like :ho" +
            " and lower(rec.descricao) like :filtro " +
            " order by rec.descricao asc ";

        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("ho", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(data));
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<RecursoFP> retornaRecursoFPDo2NivelDeHierarquia(String parte, HierarquiaOrganizacional ho, Date data) {
        String sql = "select rec.* from recursofp rec " +
            " where rec.codigo like :ho " +
            " and :data between rec.inicioVigencia and coalesce(rec.finalVigencia, :data) " +
            " and lower(rec.descricao) like :filtro " +
            " order by rec.descricao asc ";

        //System.out.println("Teste..: " + ho.getCodigoDo2NivelDeHierarquia());

        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("ho", ho.getCodigoDo2NivelDeHierarquia() + "%");
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("data", data);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<RecursoFP> listaRecursosFPVigente(String filtro, Date dataVigencia) {
        String hql = "select rec from RecursoFP rec " +
            " where (rec.codigo like :filtro " +
            " or lower(rec.descricao) like :filtro " +
            " or to_char(rec.codigoOrgao) like :codigoOrgao " +
            " or rec.descricaoReduzida like :filtro )" +
            " and :dataVigencia between rec.inicioVigencia and coalesce(rec.finalVigencia, :dataVigencia)";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", filtro.trim().toLowerCase() + "%");
        q.setParameter("codigoOrgao", filtro.trim());
        q.setParameter("dataVigencia", dataVigencia);
        q.setMaxResults(50);

        return q.getResultList();
    }

    public List<RecursoFP> listaRecursosFP(String filtro, Date dataVigencia) {
        String hql = "select rec from RecursoFP rec " +
            " where (rec.codigo like :filtro " +
            " or lower(rec.descricao) like :filtro " +
            " or lower(rec.descricaoReduzida) like :filtro )" +
            " and :dataVigencia between rec.inicioVigencia and coalesce(rec.finalVigencia, :dataVigencia)" +
            " order by rec.codigo ";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("dataVigencia", dataVigencia);
        q.setMaxResults(15);

        return q.getResultList();
    }

    public List<RecursoFP> retornaRecursoFPDo2NivelDeHierarquia(HierarquiaOrganizacional ho, Date data) {
        String sql = "select rec.* from recursofp rec " +
            " where rec.codigo like :ho " +
            " and :data between rec.inicioVigencia and coalesce(rec.finalVigencia, :data) " +
            " order by rec.codigo asc ";

        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("ho", ho.getCodigoDo2NivelDeHierarquia() + "%");
        q.setParameter("data", data);
        return q.getResultList();
    }

    public List<RecursoFP> buscarRecursoFPDo2NivelDeHierarquiaPorAno(HierarquiaOrganizacional ho, Exercicio exercicio) {
        String sql = "select rec.* from recursofp rec " +
            " where rec.codigo like :ho " +
            " and :ano between extract(YEAR from rec.inicioVigencia) and COALESCE(EXTRACT(year from rec.finalVigencia), :ano) " +
            " order by rec.codigo asc ";

        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("ho", ho.getCodigoDo2NivelDeHierarquia() + "%");
        q.setParameter("ano", exercicio.getAno());
        return q.getResultList();
    }

    public List<RecursoFP> buscarRecursosPorMesAndAnoFolha(Mes mes, Integer ano, Boolean canVerificarVigencia) {
        String sql = "select distinct rec.* from FichaFinanceiraFP ficha  " +
            "              inner join FolhaDePagamento folha on folha.ID = ficha.folhaDePagamento_ID  " +
            "              inner join VinculoFP vin on vin.ID = ficha.vinculoFP_ID  " +
            "              inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID  " +
            " where folha.ano = :ano" +
            " and folha.mes = :mes ";
        if (canVerificarVigencia) {
            sql += " and to_date(:ultimoDiaMesFolha, 'dd/MM/yyyy') between trunc(rec.iniciovigencia) " +
                " and coalesce(trunc(rec.finalvigencia), to_date(:ultimoDiaMesFolha, 'dd/MM/yyyy')) ";
        }
        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        if (canVerificarVigencia) {
            Date data = DataUtil.criarDataUltimoDiaMes(mes.getNumeroMes(), ano).toDate();
            q.setParameter("ultimoDiaMesFolha", DataUtil.getDataFormatada(data));
        }
        return q.getResultList();
    }

    public List<RecursoFP> buscarRecursosPorMesesAndAnosFolha(Mes mes1, Integer ano1, Mes mes2, Integer ano2, TipoFolhaDePagamento tipoFolhaDePagamento) {
        String sql = "select distinct rec.* from FichaFinanceiraFP ficha  " +
            "              inner join FolhaDePagamento folha on folha.ID = ficha.folhaDePagamento_ID  " +
            "              inner join VinculoFP vin on vin.ID = ficha.vinculoFP_ID  " +
            "              inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID  " +
            " where (folha.ano = :ano1 or folha.ano = :ano2)" +
            " and (folha.mes = :mes1 or folha.mes = :mes2)" +
            " and folha.TIPOFOLHADEPAGAMENTO  = :tipoFolha ";
        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("ano1", ano1);
        q.setParameter("mes1", mes1.getNumeroMesIniciandoEmZero());
        q.setParameter("ano2", ano2);
        q.setParameter("mes2", mes2.getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", tipoFolhaDePagamento.name());
        return q.getResultList();
    }

    public List<RecursoFP> buscarTodosRecusosVigentes(Date dataVigencia) {
        String sql = "select rec.* from RecursoFP rec " +
            " where :dataVigencia between rec.inicioVigencia and coalesce(rec.finalVigencia, :dataVigencia)" +
            " order by rec.codigo ";
        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("dataVigencia", dataVigencia);
        return q.getResultList();
    }


    public RecursoFP buscarRecursosPorCodigo(String codigo) {
        String sql = "select rec.* from RecursoFP rec " +
            " where :dataVigencia between rec.inicioVigencia and coalesce(rec.finalVigencia, :dataVigencia)" +
            " and rec.codigo like :codigo ";
        Query q = em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("dataVigencia", sistemaFacade.getDataOperacao());
        q.setParameter("codigo", codigo);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (RecursoFP) q.getResultList().get(0);
    }

    public List<RecursoFP> buscarRecursosFPPorFolha(FolhaDePagamento folha) {
        Query q = em.createNativeQuery("select distinct rec.* from FichaFinanceiraFP ficha  " +
                "              inner join FolhaDePagamento folha on folha.ID = ficha.folhaDePagamento_ID  " +
                "              inner join VinculoFP vin on vin.ID = ficha.vinculoFP_ID  " +
                "              inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID  " +
                " where folha.id = :id order by rec.codigo "
            , RecursoFP.class);
        q.setParameter("id", folha.getId());
        return q.getResultList();
    }

    public boolean recursoFPVinculadoVinculoVigente(RecursoFP recurso, Date dataReferencia) {
        String hql = "select vinc from RecursoDoVinculoFP recVinc " +
            " inner join recVinc.vinculoFP vinc " +
            " inner join recVinc.recursoFP rec " +
            " where rec = :recurso " +
            " and :dataOperacao between vinc.inicioVigencia and coalesce(vinc.finalVigencia, :dataOperacao) " +
            "   and (:dataReferencia between recVinc.inicioVigencia and coalesce(recVinc.finalVigencia, :dataReferencia) " +
            "           or :dataReferencia < recVinc.inicioVigencia) ";
        Query q = em.createQuery(hql);
        q.setParameter("recurso", recurso);
        q.setParameter("dataReferencia", dataReferencia);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        return !q.getResultList().isEmpty();
    }


    public FontesRecursoFP clonarFonteRecursoFP(FontesRecursoFP fonte, Date data) {
        FontesRecursoFP fonteNova = new FontesRecursoFP();
        fonteNova.setRecursoFP(fonte.getRecursoFP());

        List<FonteEventoFP> eventosClonado = Lists.newArrayList();
        return fonteNova;
    }

    public List<RecursoFP> buscarRecursosFPVigente(Mes mes, Exercicio exercicio, List<Long> idsRecursos, List<String> codigosEventos) {
        if (mes == null || exercicio == null) return Lists.newArrayList();
        String sql = "select distinct rec.* " +
            " from recursofp rec " +
            "  inner join fontesrecursofp frec on rec.id = frec.recursofp_id " +
            "  inner join fonteeventofp fevent on frec.id = fevent.fontesrecursofp_id " +
            "  inner join eventofp evento on evento.id = fevent.eventofp_id " +
            " where to_date(:dataoperacao, 'dd/mm/yyyy') between frec.iniciovigencia and coalesce(frec.finalvigencia, to_date(:dataoperacao, 'dd/mm/yyyy')) " +
            ((idsRecursos != null && !idsRecursos.isEmpty()) ? " and rec.id in :idsRecursos " : "") +
            ((codigosEventos != null && !codigosEventos.isEmpty()) ? " and evento.codigo in :codigosEventos " : "");
        Query q = this.em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("dataoperacao", DataUtil.getDataFormatada(DataUtil.criarDataComMesEAno(mes.getNumeroMes(), exercicio.getAno()).toDate()));
        if (idsRecursos != null && !idsRecursos.isEmpty()) {
            q.setParameter("idsRecursos", idsRecursos);
        }
        if (codigosEventos != null && !codigosEventos.isEmpty()) {
            q.setParameter("codigosEventos", codigosEventos);
        }
        return q.getResultList();
    }

    public List<RecursoFP> buscarRecursosFP(String parte, Mes mes, Exercicio exercicio) {
        if (mes == null || exercicio == null || parte == null) return Lists.newArrayList();
        String sql = "select distinct rec.* " +
            " from recursofp rec " +
            "  inner join fichafinanceirafp ficha on ficha.recursofp_id = rec.id " +
            "  inner join itemfichafinanceirafp item on ficha.id = item.fichafinanceirafp_id " +
            "  inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id " +
            "  inner join eventofp evento on evento.id = item.eventofp_id " +
            " where folha.mes = :mes " +
            "   and folha.ano = :ano " +
            "   and (rec.codigo like :parte or lower(rec.descricao) like :parte) " +
            " order by cast(rec.codigo as int) ";
        Query q = this.em.createNativeQuery(sql, RecursoFP.class);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public FontesRecursoFP recuperarOrCriarVigente(RecursoFP recursoFP, Mes mes, Integer ano) {
        String sql = "select * from fontesrecursofp " +
            "where recursofp_id = :recursofp and to_date(:vigencia, 'mm,yyyy') between iniciovigencia and finalvigencia ";
        Query q = em.createNativeQuery(sql, FontesRecursoFP.class);
        q.setParameter("recursofp", recursoFP.getId());
        q.setParameter("vigencia", mes.getNumeroMesString() + "/" + ano);
        try {
            return (FontesRecursoFP) q.getSingleResult();
        } catch (NoResultException e) {
            FontesRecursoFP fontesRecursoFP = new FontesRecursoFP();
            fontesRecursoFP.setRecursoFP(recursoFP);
            fontesRecursoFP.setInicioVigencia(DataUtil.primeiroDiaMes(mes));
            fontesRecursoFP.setFinalVigencia(DataUtil.ultimoDiaDoMes(mes));
            return em.merge(fontesRecursoFP);
        }
    }

    public void salvarFonteEventoFP(FonteEventoFP fonteEventoFP) {
        em.merge(fonteEventoFP);
    }

    public void removerFonteEventoFP(Long idFonteEvento) {
        em.remove(em.find(FonteEventoFP.class, idFonteEvento));
    }

}
