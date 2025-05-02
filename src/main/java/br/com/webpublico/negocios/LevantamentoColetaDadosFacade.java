package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteLevantamentoBemImovel;
import br.com.webpublico.entidadesauxiliares.LevantamentoColetaDadosItemVo;
import br.com.webpublico.enums.SituacaoLevantamentoImovel;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class LevantamentoColetaDadosFacade extends AbstractFacade<LevantamentoColetaDados> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private PropriedadeFacade propriedadeFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public LevantamentoColetaDadosFacade() {
        super(LevantamentoColetaDados.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteLevantamentoBemImovel> salvarColetaDados(AssistenteLevantamentoBemImovel assistenteLev, List<LevantamentoColetaDadosItemVo> itens) {
        LevantamentoColetaDados entity = assistenteLev.getLevantameno();
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LevantamentoColetaDados.class, "codigo"));
        }
        entity = em.merge(entity);

        criarItensLevantamento(assistenteLev, itens, entity);
        assistenteLev.setLevantameno(entity);
        return new AsyncResult<>(assistenteLev);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteLevantamentoBemImovel> confirmarColetaDados(AssistenteLevantamentoBemImovel assistenteLev) {
        AssistenteBarraProgresso assistente = assistenteLev.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Confirmando Coleta Dados para Levantamento de Bens Imóveis...");

        LevantamentoColetaDados entity = assistenteLev.getLevantameno();
        assistente.setTotal(entity.getItens().size());

        for (LevantamentoColetaDadosItem item : entity.getItens()) {
            item.getLevantamentoBemImovel().setSituacaoLevantamento(SituacaoLevantamentoImovel.AGUARDANDO_EFETIVACAO);
            em.merge(item);
            assistente.conta();
        }
        return new AsyncResult<>(assistenteLev);
    }


    private void criarItensLevantamento(AssistenteLevantamentoBemImovel assistenteLev, List<LevantamentoColetaDadosItemVo> itens, LevantamentoColetaDados entity) {
        AssistenteBarraProgresso assistente = assistenteLev.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Salvando Coleta Dados para Levantamento de Bens Imóveis...");
        assistente.setTotal(itens.size());

        for (LevantamentoColetaDadosItemVo item : itens) {
            CadastroImobiliario ci = cadastroImobiliarioFacade.recuperarSimples(item.getIdBci());

            LevantamentoBemImovel novoLev = new LevantamentoBemImovel();
            novoLev.setSituacaoLevantamento(SituacaoLevantamentoImovel.COLETA_DADOS);
            novoLev.setCadastroImobiliario(ci);
            novoLev.setUnidadeAdministrativa(assistenteLev.getUnidadeAdm());
            novoLev.setUnidadeOrcamentaria(assistenteLev.getUnidadeOrc());
            novoLev.setDescricaoImovel(ci.getComplementoEndereco());
            novoLev.setValorBem(item.getValorImovel());
            novoLev.setLocalizacao(item.getEnderecoCompleto());

            LevantamentoColetaDadosItem novoItemColetaDados = new LevantamentoColetaDadosItem();
            novoItemColetaDados.setLevantamentoColetaDados(entity);
            novoItemColetaDados.setLevantamentoBemImovel(novoLev);
            em.merge(novoItemColetaDados);
            assistente.conta();
        }
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("");
    }

    public List<LevantamentoColetaDadosItemVo> buscarCadastroImobiliarioLevantamentoImovel(String condicao) {
        String sql = " " +
            "   select " +
            "       ci.id                                                as id, " +
            "       ci.inscricaocadastral                                as inscricao, " +
            "       coalesce(pf.nome, pj.razaosocial)                    as proprietario, " +
            "       lote.arealote                                        as area_total, " +
            "       lote.codigolote || ' - ' || lote.descricaoloteamento as lote, " +
            "       lg.nome || ' - ' || lg.codigo                        as logradouro," +
            "       p.id                                                 as id_proprietario " +
            "   from cadastroimobiliario ci " +
            "         inner join cadastro cad on cad.id = ci.id " +
            "         inner join propriedade prop on prop.imovel_id = ci.id and prop.finalvigencia is null " +
            "         inner join pessoa p on p.id = prop.pessoa_id " +
            "         left join pessoafisica pf on pf.id = p.id " +
            "         left join pessoajuridica pj on pj.id = p.id " +
            "         inner join lote on lote.id = ci.lote_id " +
            "         inner join testada te on te.lote_id = lote.id " +
            "         inner join face on face.id = te.face_id " +
            "         inner join logradourobairro lb on face.logradourobairro_id = lb.id " +
            "         inner join logradouro lg on lg.id = lb.logradouro_id " +
            "         inner join bairro br on br.id = lb.bairro_id " +
            "   where ci.ativo = :ativo " +
            "   and not exists (select 1 from levantamentobemimovel lev where lev.cadastroimobiliario_id = ci.id) " + condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("ativo", Boolean.TRUE);
        List<Object[]> list = q.getResultList();

        List<LevantamentoColetaDadosItemVo> levantamentos = Lists.newArrayList();
        for (Object[] obj : list) {
            LevantamentoColetaDadosItemVo lev = new LevantamentoColetaDadosItemVo();
            lev.setIdBci(((BigDecimal) obj[0]).longValue());
            lev.setInscricaoCadastral((String) obj[1]);
            lev.setProprietario((String) obj[2]);
            lev.setAreaTotal((BigDecimal) obj[3]);
            lev.setLote((String) obj[4]);
            lev.setLogradouro((String) obj[5]);
            lev.setIdProprietario(((BigDecimal) obj[6]).longValue());
            lev.setValorImovel(getValorImovel(lev.getIdBci()));
            levantamentos.add(lev);
        }
        return levantamentos;
    }

    public BigDecimal getValorImovel(Long idBci) {
        String sql = " " +
            "    select sum(ev.valor) from eventocalculobci ev " +
            "      inner join eventoconfiguradoBCI ec on ec.id = ev.eventocalculo_id " +
            "    where ev.cadastroimobiliario_id = :idBci " +
            "    and ec.identificacao = :valor_venal ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("valor_venal", EventoConfiguradoBCI.Identificacao.VALOR_VENAL.name());
        q.setParameter("idBci", idBci);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public List<LevantamentoColetaDadosItem> buscarItens(LevantamentoColetaDados entity) {
        String sql = " select item.* from levantamentocoletadadosit item where item.levantamentocoletadados_id = :idLev ";
        Query q = em.createNativeQuery(sql, LevantamentoColetaDadosItem.class);
        q.setParameter("idLev", entity.getId());
        return q.getResultList();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public PropriedadeFacade getPropriedadeFacade() {
        return propriedadeFacade;
    }
}
