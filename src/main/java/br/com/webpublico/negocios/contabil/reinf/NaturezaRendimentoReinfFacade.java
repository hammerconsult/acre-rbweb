package br.com.webpublico.negocios.contabil.reinf;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.contabil.reinf.NaturezaRendimentoReinf;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.contabil.reinf.TipoGrupoNaturezaRendimentoReinf;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class NaturezaRendimentoReinfFacade extends AbstractFacade<NaturezaRendimentoReinf> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaFacade contaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NaturezaRendimentoReinfFacade() {
        super(NaturezaRendimentoReinf.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<Conta> buscarContasDesdobradas(String parte) {
        return contaFacade.buscarContasFilhasDespesaORCPorTipo(
            parte.trim(),
            null,
            sistemaFacade.getExercicioCorrente(),
            TipoContaDespesa.SERVICO_DE_TERCEIRO,
            true);
    }

    @Override
    public void preSave(NaturezaRendimentoReinf entidade) {
        validarDataFimMaiorQueDataInicio(entidade);
    }

    public void atualizarFimVigenciaPorGrupo(Date fimVigencia, NaturezaRendimentoReinf natureza) {
        List<NaturezaRendimentoReinf> naturezas = buscarNaturezasPorGrupo(natureza);
        naturezas.forEach(nat -> {
            nat.setFimVigencia(fimVigencia);
            salvar(nat);
        });
    }

    public void validarDataFimMaiorQueDataInicio(NaturezaRendimentoReinf natureza) {
        if (natureza.getInicioVigencia() != null && natureza.getFimVigencia() != null) {
            ValidacaoException ve = new ValidacaoException();
            if (natureza.getInicioVigencia().equals(natureza.getFimVigencia()) || natureza.getInicioVigencia().after(natureza.getFimVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de Fim de Vigência deve ser superior a Data Início de Vigência. (" + natureza.toString() + ")");
            }
            ve.lancarException();
        }
    }

    public List<NaturezaRendimentoReinf> buscarNaturezasPorGrupo(NaturezaRendimentoReinf natureza) {
        String sql = " select nat.* from NaturezaRendimentoReinf nat where nat.tipoGrupo = :grupo ";
        if (natureza.getId() != null) {
            sql += " and nat.id <> :idNat ";
        }
        Query q = em.createNativeQuery(sql, NaturezaRendimentoReinf.class);
        q.setParameter("grupo", natureza.getTipoGrupo().name());
        if (natureza.getId() != null) {
            q.setParameter("idNat", natureza.getId());
        }
        return q.getResultList();
    }

    public NaturezaRendimentoReinf buscarNaturezaPorConta(Conta conta) {
        String sql = "select nat.* from NaturezaRendimentoReinf nat inner join conta c on c.id = nat.conta_id where c.codigo = :codigoConta ";
        Query q = em.createNativeQuery(sql, NaturezaRendimentoReinf.class);
        q.setParameter("codigoConta", conta.getCodigo());
        List<NaturezaRendimentoReinf> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public List<NaturezaRendimentoReinf> buscarNaturezasVigentes(String parte, Date dataReferencia) {
        String sql = " select nat.* " +
            " from NaturezaRendimentoReinf nat " +
            " where to_date(:dataReferencia, 'dd/MM/yyyy') between nat.iniciovigencia and coalesce(nat.fimvigencia, to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "   and (nat.codigo like :parte or nat.descricao like :parte) ";
        Query q = em.createNativeQuery(sql, NaturezaRendimentoReinf.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        return q.getResultList();
    }
}
