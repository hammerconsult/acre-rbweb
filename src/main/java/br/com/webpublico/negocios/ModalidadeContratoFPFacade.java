/*
 * Codigo gerado automaticamente em Wed Jun 29 13:47:09 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.RegraDeducaoDDF;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ModalidadeContratoFPFacade extends AbstractFacade<ModalidadeContratoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ModalidadeContratoFPFacade() {
        super(ModalidadeContratoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ModalidadeContratoFP recuperar(Object id) {
        ModalidadeContratoFP modalidadeContratoFP = em.find(ModalidadeContratoFP.class, id);
        modalidadeContratoFP.getRegrasModalidadesTiposAfasts().size();
        modalidadeContratoFP.getRegrasDeducoesDDFs().size();

        for (RegraDeducaoDDF regraDDDF : modalidadeContratoFP.getRegrasDeducoesDDFs()) {
            regraDDDF.getItensRegraDeducaoDDF().size();
        }

        return modalidadeContratoFP;
    }

    public boolean verificaCodigoSalvo(String codigo) {
        Query q = em.createQuery("from ModalidadeContratoFP e where e.codigo = :codigo");
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean verificaCodigoEditar(ModalidadeContratoFP modalidade) {
        Query q = em.createQuery("from ModalidadeContratoFP e where (e.codigo = :codigo and e = :modalidade)");
        q.setParameter("modalidade", modalidade);
        q.setParameter("codigo", modalidade.getCodigo());
        return !q.getResultList().isEmpty();
    }

    public ModalidadeContratoFP recuperaModalidadePorCodigo(Long codigo) {
        Query q = em.createQuery(" from ModalidadeContratoFP m where m.codigo = :parametro ");
        q.setParameter("parametro", codigo);

        ArrayList<ModalidadeContratoFP> lista = (ArrayList<ModalidadeContratoFP>) q.getResultList();

        if (lista.isEmpty()) {
            return null;
        } else {
            return lista.get(0);
        }
    }

    public List<ModalidadeContratoFP> buscarModalidadesPorCodigoOrDescricao(String partes) {
        Query q = em.createQuery(" from ModalidadeContratoFP m where lower(to_char(m.codigo)) like :parametro or lower(m.descricao) like :parametro order by m.descricao");
        q.setParameter("parametro","%"+partes.toLowerCase()+"%");
        return q.getResultList().isEmpty()? new ArrayList<>() : q.getResultList();
    }

    public List<ModalidadeContratoFP> buscarDescricaoRelatorioServidoresPorVinculo() {
        String sql = "SELECT * from MODALIDADECONTRATOFP  where ID not in (" +
            "select ID from MODALIDADECONTRATOFP where CODIGO = :conselheiroTutelar OR CODIGO = :prestadorServicos) ";
        Query q = em.createNativeQuery(sql, ModalidadeContratoFP.class);
        q.setParameter("conselheiroTutelar", ModalidadeContratoFP.CONSELHEIRO_TUTELAR);
        q.setParameter("prestadorServicos", ModalidadeContratoFP.PRESTADOR_SERVICO);
        List<ModalidadeContratoFP> lista = q.getResultList();
        if(!lista.isEmpty()) {
            return lista;
        }
        return new ArrayList<>();
    }

    public List<ModalidadeContratoFP> buscarTodasAsModalidades() {
        Query q = em.createQuery(" from ModalidadeContratoFP order by descricao ");
        return q.getResultList().isEmpty()? new ArrayList<>() : q.getResultList();
    }

    public List<ModalidadeContratoFP> modalidadesAtivas() {
        String hql = "from ModalidadeContratoFP m where m.inativo is false or m.inativo is null";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }
}
