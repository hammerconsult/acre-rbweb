package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 50000)
public class SaldoGrupoBemIntangivelFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private ConfiguracaoContabil configuracaoContabil;

    @Lock(LockType.WRITE)
    public void geraSaldoGrupoBemIntangiveis(UnidadeOrganizacional unidade, GrupoBem grupoBem, BigDecimal valor, TipoGrupo tipoGrupo,
                                             Date data, TipoOperacaoBensIntangiveis operacao, TipoLancamento tipoLancamento,
                                             TipoOperacao tipoOperacao, Boolean validarSaldo) throws ExcecaoNegocioGenerica {

        NaturezaTipoGrupoBem naturezaTipoGrupoBem = recuperarNaturezaGrupoBem(operacao, tipoOperacao);

        MovimentoGrupoBensIntangiveis movimento = new MovimentoGrupoBensIntangiveis();
        movimento.setId(null);
        movimento.setDataMovimento(data);
        movimento.setUnidadeOrganizacional(unidade);
        movimento.setGrupoBem(grupoBem);
        movimento.setNaturezaTipoGrupoBem(naturezaTipoGrupoBem);
        movimento.setTipoGrupo(tipoGrupo);
        movimento.setOperacao(operacao);
        movimento.setTipoLancamento(tipoLancamento);
        movimento.setTipoOperacao(tipoOperacao);
        movimento.setValor(valor);
        movimento.setValidarSaldo(validarSaldo);
        alterarValorColunaCredioDebito(movimento, null);

        SaldoGrupoBemIntangiveis ultimoSaldo = recuperaUltimoSaldoPorData(movimento);
        List<SaldoGrupoBemIntangiveis> saldosPosterior = recuperaSaldosPosterioresAData(movimento);

        gerarSaldo(movimento, ultimoSaldo);
        gerarSaldoPosterior(movimento, saldosPosterior);
        em.merge(movimento);
    }

    private void gerarSaldo(MovimentoGrupoBensIntangiveis movimento, SaldoGrupoBemIntangiveis ultimoSaldo) {
        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            ultimoSaldo = setarValoresNovoSaldo(ultimoSaldo, movimento);
            ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
            em.persist(ultimoSaldo);
        } else {
            if (DataUtil.getDataFormatada(movimento.getDataMovimento()).compareTo(DataUtil.getDataFormatada(ultimoSaldo.getDataSaldo())) == 0) {
                ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
                em.merge(ultimoSaldo);
            } else {
                SaldoGrupoBemIntangiveis novoSaldo;
                novoSaldo = setarValoresNovoSaldo(ultimoSaldo, movimento);
                novoSaldo = copiarValoresDoSaldoRecuperado(ultimoSaldo, novoSaldo, movimento);
                novoSaldo = alterarValorColunaCredioDebito(movimento, novoSaldo);
                em.persist(novoSaldo);
            }
        }
    }

    private void gerarSaldoPosterior(MovimentoGrupoBensIntangiveis movimento, List<SaldoGrupoBemIntangiveis> saldoPosterior) {
        if (!saldoPosterior.isEmpty()) {
            for (SaldoGrupoBemIntangiveis saldoParaAtualizar : saldoPosterior) {
                saldoParaAtualizar = alterarValorColunaCredioDebito(movimento, saldoParaAtualizar);
                em.merge(saldoParaAtualizar);
            }
        }
    }

    private SaldoGrupoBemIntangiveis alterarValorColunaCredioDebito(MovimentoGrupoBensIntangiveis movimento, SaldoGrupoBemIntangiveis saldoGrupoBemIntangiveis) {
        if (movimento.getTipoOperacao().equals(TipoOperacao.DEBITO)) {
            gerarValorDoDebito(movimento, saldoGrupoBemIntangiveis);
        } else {
            gerarValorDoCredito(movimento, saldoGrupoBemIntangiveis);
        }
        return saldoGrupoBemIntangiveis;
    }

    public SaldoGrupoBemIntangiveis copiarValoresDoSaldoRecuperado(SaldoGrupoBemIntangiveis saldoRecuperado, SaldoGrupoBemIntangiveis novoSaldo, MovimentoGrupoBensIntangiveis movimento) {
        if (movimento.getTipoOperacao().equals(TipoOperacao.DEBITO)) {
            novoSaldo.setDebito(saldoRecuperado.getDebito());
        } else {
            novoSaldo.setCredito(saldoRecuperado.getCredito());
        }
        return novoSaldo;
    }

    public SaldoGrupoBemIntangiveis setarValoresNovoSaldo(SaldoGrupoBemIntangiveis saldoGrupoBemIntangiveis, MovimentoGrupoBensIntangiveis movimento) {
        saldoGrupoBemIntangiveis = new SaldoGrupoBemIntangiveis();
        saldoGrupoBemIntangiveis.setDataSaldo(movimento.getDataMovimento());
        saldoGrupoBemIntangiveis.setGrupoBem(movimento.getGrupoBem());
        saldoGrupoBemIntangiveis.setTipoGrupo(movimento.getTipoGrupo());
        saldoGrupoBemIntangiveis.setUnidadeOrganizacional(movimento.getUnidadeOrganizacional());
        saldoGrupoBemIntangiveis.setNaturezaTipoGrupoBem(movimento.getNaturezaTipoGrupoBem());
        saldoGrupoBemIntangiveis.setCredito(new BigDecimal(BigInteger.ZERO));
        saldoGrupoBemIntangiveis.setDebito(new BigDecimal(BigInteger.ZERO));
        return saldoGrupoBemIntangiveis;
    }

    //    RECUPERAR AS OPERAÇÕES DE DEBITO
    private Boolean recuperarOperacoesParaNaturezaOriginalDebito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case AQUISICAO_BENS_INTANGIVEIS:
                return true;
            case INCORPORACAO_BENS_INTANGIVEIS:
                return true;
            case TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA:
                return true;
            case GANHOS_ALIENACAO_BENS_INTANGIVEIS:
                return true;
            case ALIENACAO_BENS_INTANGIVEIS:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaVPDDebito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case AMORTIZACAO_BENS_INTANGIVEIS:
                return true;
            case REDUCAO_VALOR_RECUPERAVEL_BENS_INTANGIVEIS:
                return true;
            case TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA:
                return true;
            case PERDAS_ALIENACAO_BENS_INTANGIVEIS:
                return true;
            case DESINCORPORACAO_BENS_INTANGIVEIS:
                return true;
            case TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaAmortizacaoDebito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_AMORTIZACAO:
                return true;
            case TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaReducaoDebito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_REDUCAO_VALOR_RECUPERAVEL:
                return true;
        }
        return false;
    }
    //    FIM RECUPERAR AS OPERAÇÕES DE DEBITO


    //    RECUPERAR AS OPERAÇÕES DE CREDITO
    private Boolean recuperarOperacoesParaNaturezaOriginalCredito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_AMORTIZACAO:
                return true;
            case APURACAO_VALOR_LIQUIDO_BENS_INTAGIVEIS_REDUCAO_VALOR_RECUPERAVEL:
                return true;
            case TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA:
                return true;
            case PERDAS_ALIENACAO_BENS_INTANGIVEIS:
                return true;
            case ALIENACAO_BENS_INTANGIVEIS:
                return true;
            case DESINCORPORACAO_BENS_INTANGIVEIS:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaVPACredito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case INCORPORACAO_BENS_INTANGIVEIS:
                return true;
            case TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA:
                return true;
            case GANHOS_ALIENACAO_BENS_INTANGIVEIS:
                return true;
            case TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_CONCEDIDA:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaAmortizacaoCredito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case AMORTIZACAO_BENS_INTANGIVEIS:
                return true;
            case TRANSFERENCIA_AMORTIZACAO_BENS_INTANGIVEIS_RECEBIDA:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaReducaoCredito(TipoOperacaoBensIntangiveis operacao) {
        switch (operacao) {
            case REDUCAO_VALOR_RECUPERAVEL_BENS_INTANGIVEIS:
                return true;
        }
        return false;
    }
    //    FIM RECUPERAR AS OPERAÇÕES DE CREDITO

    private void setarValorDebito(SaldoGrupoBemIntangiveis saldo, MovimentoGrupoBensIntangiveis movimento) {
        if (movimento.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            movimento.setDebito(movimento.getValor());
            if (saldo != null) {
                saldo.setDebito(saldo.getDebito().add(movimento.getValor()));
            }
        } else {
            movimento.setDebito(movimento.getValor());
            if (saldo != null) {
                saldo.setDebito(saldo.getDebito().subtract(movimento.getValor()));
            }
        }
    }

    private void setarValorCredito(SaldoGrupoBemIntangiveis saldo, MovimentoGrupoBensIntangiveis movimento) {
        if (movimento.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
            movimento.setCredito(movimento.getValor());
            if (saldo != null) {
                saldo.setCredito(saldo.getCredito().add(movimento.getValor()));
            }
        } else {
            movimento.setCredito(movimento.getValor());
            if (saldo != null) {
                saldo.setCredito(saldo.getCredito().subtract(movimento.getValor()));
            }
        }
    }

    public void gerarValorDoDebito(MovimentoGrupoBensIntangiveis movimento, SaldoGrupoBemIntangiveis saldo) {
        ConfiguracaoContabil configuracaoContabil = getConfiguracaoContabil();

        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.ORIGINAL) && recuperarOperacoesParaNaturezaOriginalDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            setarValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.VPD) && recuperarOperacoesParaNaturezaVPDDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            setarValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.AMORTIZACAO) && recuperarOperacoesParaNaturezaAmortizacaoDebito(movimento.getOperacao())) {
            validarSaldo(movimento);
            setarValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.REDUCAO) && recuperarOperacoesParaNaturezaReducaoDebito(movimento.getOperacao())) {
            validarSaldo(movimento);
            setarValorDebito(saldo, movimento);
        }
    }

    public void gerarValorDoCredito(MovimentoGrupoBensIntangiveis movimento, SaldoGrupoBemIntangiveis saldo) {

        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.ORIGINAL)) {
            if (recuperarOperacoesParaNaturezaOriginalCredito(movimento.getOperacao())) {
                setarValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.VPA)) {
            if (recuperarOperacoesParaNaturezaVPACredito(movimento.getOperacao())) {
                setarValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.AMORTIZACAO)) {
            if (recuperarOperacoesParaNaturezaAmortizacaoCredito(movimento.getOperacao())) {
                setarValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().equals(NaturezaTipoGrupoBem.REDUCAO)) {
            if (recuperarOperacoesParaNaturezaReducaoCredito(movimento.getOperacao())) {
                setarValorCredito(saldo, movimento);
            }
        }
    }

    //    RECUPERA A NATUREZA DO SALDO POR OPERAÇÃO DO GRUPO MATERIAL
    private NaturezaTipoGrupoBem recuperarNaturezaGrupoBem(TipoOperacaoBensIntangiveis operacao, TipoOperacao tipoOperacao) {
        NaturezaTipoGrupoBem naturezaTipoGrupoBem = null;
        if (tipoOperacao.equals(TipoOperacao.DEBITO)) {
            if (recuperarOperacoesParaNaturezaOriginalDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.ORIGINAL;
            }
            if (recuperarOperacoesParaNaturezaVPDDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.VPD;
            }
            if (recuperarOperacoesParaNaturezaAmortizacaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.AMORTIZACAO;
            }
            if (recuperarOperacoesParaNaturezaReducaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.REDUCAO;
            }
        } else {
            if (recuperarOperacoesParaNaturezaOriginalCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.ORIGINAL;
            }
            if (recuperarOperacoesParaNaturezaVPACredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.VPA;
            }
            if (recuperarOperacoesParaNaturezaAmortizacaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.AMORTIZACAO;
            }
            if (recuperarOperacoesParaNaturezaReducaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.REDUCAO;
            }
        }
        return naturezaTipoGrupoBem;
    }

    private void validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(MovimentoGrupoBensIntangiveis movimento, ConfiguracaoContabil configuracaoContabil) {
        if (configuracaoContabil.getBloquearSaldoNegativoBemIntangivel() && movimento.getValidarSaldo()) {
            SaldoGrupoBemIntangiveis saldoGrupoBemIntangiveis = recuperaUltimoSaldoPorData(movimento);
            BigDecimal saldoDoDia = saldoGrupoBemIntangiveis.getCredito().subtract(saldoGrupoBemIntangiveis.getDebito().add(movimento.getValor()));
            if (saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
                throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) + "</b>, na data " + DataUtil.getDataFormatada(saldoGrupoBemIntangiveis.getDataSaldo()) + " referente a natureza: " + saldoGrupoBemIntangiveis.getNaturezaTipoGrupoBem().getDescricao() + ".");
            }
        }
    }

    private void validarSaldo(MovimentoGrupoBensIntangiveis movimento) {
        SaldoGrupoBemIntangiveis saldoGrupoBemIntangiveis = recuperaUltimoSaldoPorData(movimento);
        BigDecimal saldoDoDia = saldoGrupoBemIntangiveis.getCredito().subtract(saldoGrupoBemIntangiveis.getDebito());
        if (movimento.getValidarSaldo() && saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) + "</b>, na data " + DataUtil.getDataFormatada(saldoGrupoBemIntangiveis.getDataSaldo()) + " referente a natureza: " + saldoGrupoBemIntangiveis.getNaturezaTipoGrupoBem().getDescricao() + ".");
        }
    }

    public SaldoGrupoBemIntangiveis recuperaUltimoSaldoPorData(MovimentoGrupoBensIntangiveis movimento) throws ExcecaoNegocioGenerica {
        try {
            String sql = " SELECT saldo.* FROM saldogrupobemintangiveis saldo "
                    + " WHERE trunc(saldo.datasaldo) <= to_date(:data, 'dd/mm/yyyy') "
                    + " AND saldo.grupobem_id = :grupoBem"
                    + " AND saldo.unidadeorganizacional_id = :unidade "
                    + " AND saldo.tipogrupo = :tipoGrupo "
                    + " AND saldo.naturezatipogrupobem = :natureza "
                    + " ORDER BY saldo.datasaldo DESC ";

            Query q = em.createNativeQuery(sql, SaldoGrupoBemIntangiveis.class);

            q.setParameter("grupoBem", movimento.getGrupoBem().getId());
            q.setParameter("unidade", movimento.getUnidadeOrganizacional().getId());
            q.setParameter("tipoGrupo", movimento.getTipoGrupo().name());
            q.setParameter("natureza", movimento.getNaturezaTipoGrupoBem().name());
            q.setParameter("data", DataUtil.getDataFormatada(movimento.getDataMovimento()));
            q.setMaxResults(1);
            if (q.getResultList().isEmpty()) {
                SaldoGrupoBemIntangiveis saldo = new SaldoGrupoBemIntangiveis();
                saldo.setDataSaldo(movimento.getDataMovimento());
                saldo.setGrupoBem(movimento.getGrupoBem());
                saldo.setTipoGrupo(movimento.getTipoGrupo());
                saldo.setUnidadeOrganizacional(movimento.getUnidadeOrganizacional());
                saldo.setNaturezaTipoGrupoBem(movimento.getNaturezaTipoGrupoBem());
                saldo.setCredito(new BigDecimal(BigInteger.ZERO));
                saldo.setDebito(new BigDecimal(BigInteger.ZERO));
                return saldo;
            } else {
                return (SaldoGrupoBemIntangiveis) q.getSingleResult();
            }
        } catch (IllegalArgumentException e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public List<SaldoGrupoBemIntangiveis> recuperaSaldosPosterioresAData(MovimentoGrupoBensIntangiveis movimento) {

        String sql = " select s.* from saldogrupobemintangiveis s "
                + " where trunc(s.dataSaldo) > to_date(:data, 'dd/mm/yyyy') "
                + " and s.unidadeOrganizacional_id = :unidade "
                + " and s.grupobem_id = :grupo "
                + " and s.naturezatipogrupobem = :natureza "
                + " and s.tipoGrupo = :tipoGrupo ";

        Query q = em.createNativeQuery(sql, SaldoGrupoBemIntangiveis.class);
        q.setParameter("grupo", movimento.getGrupoBem().getId());
        q.setParameter("unidade", movimento.getUnidadeOrganizacional().getId());
        q.setParameter("tipoGrupo", movimento.getTipoGrupo().name());
        q.setParameter("natureza", movimento.getNaturezaTipoGrupoBem().name());
        q.setParameter("data", DataUtil.getDataFormatada(movimento.getDataMovimento()));
        if (q.getResultList().isEmpty()) {
            return new ArrayList<SaldoGrupoBemIntangiveis>();
        } else {
            return q.getResultList();
        }
    }

    public void excluirSaldoPorPeriodo(Date dataInidical, Date dataFinal, GrupoBem grupoBem) {

        String sql = " delete saldogrupobemintangiveis s" +
                " where trunc(s.datasaldo) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        if (grupoBem != null) {
            sql += " and s.grupobem_id = :idGrupoBem ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInidical));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (grupoBem != null) {
            q.setParameter("idGrupoBem", grupoBem.getId());
        }
        q.executeUpdate();
    }

    public void excluirMovimentoPorPeriodo(Date dataInidical, Date dataFinal, GrupoBem grupoBem) {

        String sql = " delete movimentogrupobensintang m " +
                " where trunc(m.datamovimento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        if (grupoBem != null) {
            sql += " and m.grupobem_id = :idGrupoBem ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInidical));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (grupoBem != null) {
            q.setParameter("idGrupoBem", grupoBem.getId());
        }
        q.executeUpdate();
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        if (configuracaoContabil == null) {
            configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
        }
        return configuracaoContabil;
    }
    public void limparConfiguracaoContabil(){
        configuracaoContabil = null;
    }
}
