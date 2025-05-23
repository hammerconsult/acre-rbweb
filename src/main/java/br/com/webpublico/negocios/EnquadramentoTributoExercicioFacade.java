package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroCreditoReceberTributario;
import br.com.webpublico.entidadesauxiliares.LinhaRelatorioCreditoReceberTributario;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.webreportdto.dto.tributario.TipoCreditoReceberTributario;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.LocalDate;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<List<LinhaRelatorioCreditoReceberTributario>> gerarRelatorioCreditoReceber(FiltroCreditoReceberTributario filtro) {
        List<LinhaRelatorioCreditoReceberTributario> result = buscarValorSaldoTributoNoPeriodo(filtro);
        return new AsyncResult<>(result);
    }

    public List<LinhaRelatorioCreditoReceberTributario> buscarValorSaldoTributoNoPeriodo(FiltroCreditoReceberTributario filtro) {

        String sql = "SELECT codigoTributo, descricaoTributo, codigoConta, descricaoConta, tipocredito, entidade, emissao, " +
            "  sum(valor) AS valor " +
            " FROM " +
            "  (SELECT " +
            "     trib.codigo           AS codigoTributo, " +
            "     trib.descricao        AS descricaoTributo, " +
            "     conta.codigo          AS codigoConta, " +
            "     conta.descricao                                   AS descricaoConta, " +
            "     contaReceita.TIPOSCREDITO                         AS tipocredito, " +
            "     coalesce(entidade.nome, 'Entidade não Informada') AS entidade," +
            "     calc.DATACALCULO emissao, " +
            "     ipvd.VALOR " +
            "     FROM PARCELAVALORDIVIDA pvd" +
            "     INNER JOIN VALORDIVIDA vd ON vd.id = pvd.valordivida_id" +
            "     INNER JOIN ITEMPARCELAVALORDIVIDA ipvd ON ipvd.parcelavalordivida_id = pvd.id" +
            "     INNER JOIN ITEMVALORDIVIDA itemvd ON itemvd.id = ipvd.itemvalordivida_id" +
            "     INNER JOIN TRIBUTO trib ON trib.id = itemvd.tributo_id" +
            "     INNER JOIN situacaoparcelavalordivida spvd ON spvd.id = pvd.SITUACAOATUAL_ID " +
            "     INNER JOIN OPCAOPAGAMENTO OP ON OP.ID = PVD.OPCAOPAGAMENTO_ID AND OP.PROMOCIONAL = 0 " +
            "     INNER JOIN contatributoreceita ctr ON ctr.tributo_id = trib.id " +
            "     INNER JOIN EnquadramentoTributoExerc enquadramento ON enquadramento.id = ctr.enquadramento_id " +
            "     INNER JOIN exercicio exercicioEnquadramento ON exercicioEnquadramento.id = enquadramento.exercicioVigente_id " +
            "     LEFT JOIN entidade ON entidade.id = trib.entidade_id " +
            "     INNER JOIN ContaReceita contaReceita " +
            "          ON ((pvd.DIVIDAATIVA <> 1 AND contaReceita.id = ctr.contaexercicio_id) " +
            "              OR (pvd.DIVIDAATIVA <> 0 AND contaReceita.id = ctr.contaDividaAtiva_id))" +
            "          and contaReceita.tiposCredito in (:tiposCredito) " +
            "     INNER JOIN conta conta ON conta.id = contaReceita.id " +
            "     INNER JOIN calculo calc ON calc.ID = vd.CALCULO_ID" +
            "   WHERE itemvd.valor > 0 " +
            "         AND exercicioEnquadramento.ano = extract(YEAR FROM sysdate) ";
        sql += "AND (";
        for (int x = 0; x < filtro.getPrazos().size(); x++) {
            sql += "trunc(calc.DATACALCULO) between :inicio" + x + " and :fim" + x + " OR ";
        }
        sql = sql.substring(0, sql.length()-4)+")";
        sql += "         AND spvd.SITUACAOPARCELA in ('EM_ABERTO', 'BAIXADO', 'BAIXADO_OUTRA_OPCAO', 'PAGO')" +
            "         AND trib.tipoTributo in (:tipoTributo)";

        if (filtro.getTributos() != null && !filtro.getTributos().isEmpty()) {
            sql += " and trib.id in (:tributos)";
        }
        if (filtro.getContas() != null && !filtro.getContas().isEmpty()) {
            sql += " and contaReceita.id in (:contas)";
        }
        if (filtro.getDividas() != null && !filtro.getDividas().isEmpty()) {
            sql += " and vd.divida_id in (:dividas)";
        }

        sql += "  ) dados  GROUP BY  codigoTributo, descricaoTributo, codigoConta, descricaoConta, tipocredito, entidade, emissao ";


        Query q = em.createNativeQuery(sql);


        List<String> tipos = Lists.newArrayList();
        for (TipoCreditoReceberTributario tipo : filtro.getTiposCredito()) {
            tipos.add(tipo.name());
        }
        q.setParameter("tiposCredito", tipos);


        q.setParameter("tipoTributo", Lists.newArrayList(Tributo.TipoTributo.TAXA.name(), Tributo.TipoTributo.IMPOSTO.name()));

        if (filtro.getTributos() != null && !filtro.getTributos().isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            for (Tributo tributo : filtro.getTributos()) {
                ids.add(tributo.getId());
            }
            q.setParameter("tributos", ids);
        }
        if (filtro.getContas() != null && !filtro.getContas().isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            for (ContaReceita conta : filtro.getContas()) {
                ids.add(conta.getId());
            }
            q.setParameter("contas", ids);
        }
        if (filtro.getDividas() != null && !filtro.getDividas().isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            for (Divida divida : filtro.getDividas()) {
                ids.add(divida.getId());
            }
            q.setParameter("dividas", ids);
        }

        for (int x = 0; x < filtro.getPrazos().size(); x++) {
            q.setParameter("inicio" + x, filtro.getPrazos().get(x).getInicio().toDate());
            q.setParameter("fim" + x, filtro.getPrazos().get(x).getFim().toDate());
        }

        List<Object[]> result = q.getResultList();
        List<LinhaRelatorioCreditoReceberTributario> toReturn = Lists.newArrayList();

        for (Object[] obj : result) {
            LinhaRelatorioCreditoReceberTributario linha = new LinhaRelatorioCreditoReceberTributario();
            linha.setCodigoTributo(obj[0].toString());
            linha.setDescricaoTributo(obj[1].toString());
            linha.setCodigoConta(obj[2].toString());
            linha.setDescricaoConta(obj[3].toString());
            linha.setTiposCredito(TiposCredito.valueOf(obj[4].toString()));
            linha.setEntidade(obj[5].toString());
            linha.setEmissao(LocalDate.fromDateFields((Date) obj[6]));
            linha.setValor((BigDecimal) obj[7]);
            toReturn.add(linha);
        }

        return toReturn;
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
