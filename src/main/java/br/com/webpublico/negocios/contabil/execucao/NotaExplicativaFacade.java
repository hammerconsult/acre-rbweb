package br.com.webpublico.negocios.contabil.execucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidades.contabil.NotaExplicativa;
import br.com.webpublico.entidades.contabil.NotaExplicativaItem;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.negocios.SistemaFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class NotaExplicativaFacade extends AbstractFacade<NotaExplicativa> {
    private static final String TAG_NOTAS = "$NOTAS";
    private static final String TAG_NUMERO_NOTA = "$NUMERO_NOTA";
    private static final String TAG_DESCRICAO_ITEM = "$DESCRICAO_ITEM";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;

    public NotaExplicativaFacade() {
        super(NotaExplicativa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public NotaExplicativa recuperar(Object id) {
        NotaExplicativa entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        return entity;
    }

    public List<RelatoriosItemDemonst> buscarRelatorios(String parte, TipoRelatorioItemDemonstrativo tipoRelatorio) {
        return relatoriosItemDemonstFacade.buscarRelatoriosPorExercicioETipo(parte, sistemaFacade.getExercicioCorrente(), tipoRelatorio);
    }

    public List<ItemDemonstrativo> buscarItensPorRelatorio(String parte, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatoriosItemDemonstFacade.getItemDemonstrativoFacade().buscarItensPorRelatorioOrdenadosPorGrupoEOrdem(parte, relatoriosItemDemonst);
    }

    public String recuperarNumeroNota(ItemDemonstrativo itemDemonstrativo) {
        if (itemDemonstrativo == null || itemDemonstrativo.getId() == null) return "";
        String sql = "select to_char(numero) from notaexplicativaitem where itemdemonstrativo_id = :idItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", itemDemonstrativo.getId());
        List<String> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : "";
    }

    public String buscarConteudoNotaExplicativa(Exercicio exercicioCorrente, String relatorio, TipoRelatorioItemDemonstrativo tipo, String dataReferencia) {
        if (exercicioCorrente == null || relatorio == null || tipo == null || dataReferencia == null || dataReferencia.isEmpty() || relatorio.isEmpty()) return "";
        String sql = " select ne.* " +
            " from notaexplicativa ne " +
            "    inner join relatoriositemdemonst rel on ne.relatoriositemdemonst_id = rel.id " +
            " where rel.exercicio_id = :exerc " +
            "   and lower(rel.descricao) = :descricaoRelatorio " +
            "   and rel.tiporelatorioitemdemonstrativo = :tipo " +
            "   and to_date(:dataReferencia) between ne.iniciovigencia and coalesce(ne.fimvigencia, to_date(:dataReferencia)) ";
        Query q = getEntityManager().createNativeQuery(sql, NotaExplicativa.class);
        q.setParameter("exerc", exercicioCorrente.getId());
        q.setParameter("descricaoRelatorio", relatorio.trim().toLowerCase());
        q.setParameter("tipo", tipo.name());
        q.setParameter("dataReferencia", dataReferencia);
        List<NotaExplicativa> resultado = q.getResultList();
        String retorno = "";
        if (!resultado.isEmpty()) {
            NotaExplicativa nota = resultado.get(0);
            if (nota != null) {
                retorno = nota.getConteudo();
                if (retorno.contains(TAG_NOTAS)) {
                    String conteudoItens = "";
                    String juncao = "";
                    for (NotaExplicativaItem notaItem : nota.getItens()) {
                        conteudoItens += juncao + notaItem.getDescricao()
                            .replace(TAG_NUMERO_NOTA, notaItem.getNumero().toString())
                            .replace(TAG_DESCRICAO_ITEM, notaItem.getItemDemonstrativo().getNome());
                        juncao = "<br/>";
                    }
                    retorno = retorno.replace(TAG_NOTAS, conteudoItens);
                }
            }
        }
        return retorno;
    }
}
