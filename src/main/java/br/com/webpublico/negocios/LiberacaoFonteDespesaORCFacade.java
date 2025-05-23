/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.LiberacaoFonteDespesaOrc;
import br.com.webpublico.entidades.ReservaFonteDespesaOrc;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.enums.TipoOperacaoORC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * @author venon
 */
@Stateless
public class LiberacaoFonteDespesaORCFacade extends AbstractFacade<LiberacaoFonteDespesaOrc> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LiberacaoFonteDespesaORCFacade() {
        super(LiberacaoFonteDespesaOrc.class);
    }

    public LiberacaoFonteDespesaOrc geraLiberacao(BigDecimal valor, FonteDespesaORC fonte, OrigemReservaFonte origem, TipoOperacaoORC too, ReservaFonteDespesaOrc reserva) {
        LiberacaoFonteDespesaOrc lib = new LiberacaoFonteDespesaOrc();
        lib.getReservaFonteDespesaOrc().getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional();
        lib.setDataLiberacao(SistemaFacade.getDataCorrente());
        lib.setOrigemReservaFonte(origem);
        lib.setReservaFonteDespesaOrc(reserva);
        lib.setValor(reserva.getValor());
        em.persist(lib);
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
            reserva.getFonteDespesaORC(),
            OperacaoORC.RESERVADO,
            too,
            lib.getValor(),
            lib.getDataLiberacao(),
            lib.getReservaFonteDespesaOrc().getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            lib.getId().toString(),
            lib.getClass().getSimpleName(),
            lib.getId().toString(),
            "Liberação de Reserva de dotação feita manualmente pelo departamento de planejamento.");
        saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
        return lib;
    }

    public LiberacaoFonteDespesaOrc recuperaLiberacaoPorReserva(ReservaFonteDespesaOrc reserva) {
        String sql = "SELECT * FROM liberacaofontedespesaorc li WHERE li.reservafontedespesaorc_id = :param";
        Query q = em.createNativeQuery(sql, LiberacaoFonteDespesaOrc.class);
        q.setParameter("param", reserva.getId());
        try {
            return (LiberacaoFonteDespesaOrc) q.getSingleResult();
        } catch (NoResultException nre) {
            return new LiberacaoFonteDespesaOrc();
        }
    }
}
