package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class EnquadramentoTributoExercicioFacade extends AbstractFacade<EnquadramentoTributoExerc> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;

    public EnquadramentoTributoExercicioFacade() {
        super(EnquadramentoTributoExerc.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public boolean codigoEmUso(EnquadramentoTributoExerc selecionado) {
        if (selecionado.getCodigo() == null || selecionado.getCodigo().intValue() <= 0) {
            return false;
        }
        String hql = "from EnquadramentoTributoExerc ete where ete.codigo = :codigo";
        if (selecionado.getId() != null) {
            hql += " and ete != :enquadramento";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", selecionado.getCodigo());
        if (selecionado.getId() != null) {
            q.setParameter("enquadramento", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean descricaoEmUso(EnquadramentoTributoExerc selecionado) {
        if (selecionado.getDescricao() == null || !(selecionado.getDescricao().length() > 0)) {
            return false;
        }
        String hql = "from EnquadramentoTributoExerc ete where lower(ete.descricao) = :descricao";
        if (selecionado.getId() != null) {
            hql += " and ete != :enquadramento";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", selecionado.getDescricao().trim().toLowerCase());
        if (selecionado.getId() != null) {
            q.setParameter("enquadramento", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean isDataInicialValida(EnquadramentoTributoExerc enquadramento) {
        boolean retorno = true;
        String hql = "from EnquadramentoTributoExerc ete where ete.exercicioVigente = :exercicioVigente and :data >= ete.exercicioDividaInicial.ano and :data <= ete.exercicioDividaFinal.ano";
        if (enquadramento.getId() != null) {
            hql += " and ete != :enquadramento";
        }
        Query q = em.createQuery(hql);
        q.setParameter("exercicioVigente", enquadramento.getExercicioVigente());
        q.setParameter("data", enquadramento.getExercicioDividaInicial().getAno());
        if (enquadramento.getId() != null) {
            q.setParameter("enquadramento", enquadramento);
        }
        retorno = q.getResultList().isEmpty();
        return retorno;
    }

    public boolean isDataFinalValida(EnquadramentoTributoExerc enquadramento) {
        boolean retorno = true;
        String hql = "from EnquadramentoTributoExerc ete where ete.exercicioVigente = :exercicioVigente and :data >= ete.exercicioDividaInicial.ano and :data <= ete.exercicioDividaFinal.ano";
        if (enquadramento.getId() != null) {
            hql += " and ete != :enquadramento";
        }
        Query q = em.createQuery(hql);
        q.setParameter("exercicioVigente", enquadramento.getExercicioVigente());
        q.setParameter("data", enquadramento.getExercicioDividaFinal().getAno());
        if (enquadramento.getId() != null) {
            q.setParameter("enquadramento", enquadramento);
        }
        retorno = q.getResultList().isEmpty();
        return retorno;
    }

    public List<EnquadramentoTributoExerc> recuperaPorExercicioVigente(Exercicio exercicio) {
        Query q = em.createQuery("From EnquadramentoTributoExerc ete where ete.exercicioVigente = :exercicio");
        q.setParameter("exercicio", exercicio);
        return q.getResultList();
    }

    @Override
    public EnquadramentoTributoExerc recuperar(Object id) {
        EnquadramentoTributoExerc retorno = super.recuperar(id);
        retorno.getContas().size();
        return retorno;
    }

    public ContaTributoReceita contasDoTributo(EnquadramentoTributoExerc enquadramento, Tributo tributo) {
        ContaTributoReceita retorno = null;
        for (ContaTributoReceita conta : enquadramento.getContas()) {
            if (conta.getTributo().equals(tributo)) {
                retorno = conta;
                break;
            }
        }
        return retorno;
    }

    public ContaReceita buscarContasDoTributo(Long idTributo, Date dataPagamento, ContaTributoReceita.TipoContaReceita tipoContaReceita, boolean dividaAtiva) {
        Calendar dataReferencia = Calendar.getInstance();
        dataReferencia.setTime(dataPagamento);

        Query q = em.createQuery("select conta from ContaTributoReceita conta " +
            " where conta.tributo.id = :tributo " +
            " and conta.enquadramento.exercicioVigente.ano = :exercicioAtual" +
            " and :dataReferencia between conta.inicioVigencia and coalesce(conta.fimVigencia ,:dataReferencia)");
        q.setParameter("tributo", idTributo);
        q.setParameter("exercicioAtual", dataReferencia.get(Calendar.YEAR));
        q.setParameter("dataReferencia", dataPagamento);
        List<ContaTributoReceita> lista = q.getResultList();
        if (lista.isEmpty()) {
            return null;
        }
        ContaTributoReceita conta = lista.get(0);
        switch (tipoContaReceita) {
            case EXERCICIO:
                return dividaAtiva ? conta.getContaDividaAtiva() : conta.getContaExercicio();
            case RENUNCIA:
                return dividaAtiva ? conta.getContaRenunciaDividaAtiva() : conta.getContaRenunciaExercicio();
            case RESTITUICAO:
                return dividaAtiva ? conta.getContaRestituicaoDividaAtiva() : conta.getContaRestituicaoExercicio();
            case DESCONTO:
                return dividaAtiva ? conta.getContaDescontoDividaAtiva() : conta.getContaDescontoExercicio();
            case DEDUCAO:
                return dividaAtiva ? conta.getContaDeducoesDividaAtiva() : conta.getContaDeducoesExercicio();
            default:
                return null;
        }
    }

    public ContaReceita contasDoTributo(Tributo tributo, Date dataPagamento, ContaTributoReceita.TipoContaReceita tipoContaReceita, boolean dividaAtiva) {
        return buscarContasDoTributo(tributo.getId(), dataPagamento, tipoContaReceita, dividaAtiva);
    }

    public List<ContaTributoReceita> contasDoEnquadramento(EnquadramentoTributoExerc enquadramento) {
        Query q = em.createNativeQuery("select * from ContaTributoReceita where enquadramento_id = :idEnquadramento", ContaTributoReceita.class);
        q.setParameter("idEnquadramento", enquadramento.getId());
        return q.getResultList();
    }

    public static class TributoExercicio {
        private String entidade;
        private String codigoTributo;
        private String descricaoTributo;
        private String codigoConta;
        private String descricaoConta;
        private BigDecimal valor;

        public TributoExercicio() {

        }

        public String getCodigoTributo() {
            return codigoTributo;
        }

        public String getDescricaoTributo() {
            return descricaoTributo;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setCodigoTributo(String codigoTributo) {
            this.codigoTributo = codigoTributo;
        }

        public void setDescricaoTributo(String descricaoTributo) {
            this.descricaoTributo = descricaoTributo;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public String getEntidade() {
            return entidade;
        }

        public void setEntidade(String entidade) {
            this.entidade = entidade;
        }

        public String getCodigoConta() {
            return codigoConta;
        }

        public void setCodigoConta(String codigoConta) {
            this.codigoConta = codigoConta;
        }

        public String getDescricaoConta() {
            return descricaoConta;
        }

        public void setDescricaoConta(String descricaoConta) {
            this.descricaoConta = descricaoConta;
        }
    }


}
