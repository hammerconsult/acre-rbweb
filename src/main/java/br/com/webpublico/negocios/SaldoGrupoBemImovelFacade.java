package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
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

@Stateless
public class SaldoGrupoBemImovelFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private ConfiguracaoContabil configuracaoContabil;

    @Lock(LockType.WRITE)
    public void geraSaldoGrupoBemImoveis(UnidadeOrganizacional unidade, GrupoBem grupoBem, BigDecimal valor, TipoGrupo tipoGrupo,
                                         Date data, TipoOperacaoBensImoveis operacao, TipoLancamento tipoLancamento,
                                         TipoOperacao tipoOperacao, Boolean validarSaldo) throws ExcecaoNegocioGenerica {

        NaturezaTipoGrupoBem naturezaTipoGrupoBem = recuperarNaturezaGrupoBem(operacao, tipoOperacao);
        validarNatuezaSaldo(operacao, tipoOperacao, naturezaTipoGrupoBem);

        MovimentoGrupoBensImoveis movimento = new MovimentoGrupoBensImoveis();
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

        SaldoGrupoBemImoveis ultimoSaldo = buscarUltimoSaldoPorData(movimento);
        List<SaldoGrupoBemImoveis> saldoPosterior = buscarSaldosPosterioresAData(movimento);

        gerarSaldo(movimento, ultimoSaldo);
        gerarSaldoPosterior(movimento, saldoPosterior);
        em.persist(movimento);
    }

    private void validarNatuezaSaldo(TipoOperacaoBensImoveis operacao, TipoOperacao tipoOperacao, NaturezaTipoGrupoBem naturezaTipoGrupoBem) {
        if (naturezaTipoGrupoBem == null) {
            throw new ExcecaoNegocioGenerica("Natureza do saldo não encontrada para a operação: " + operacao.getDescricao() + " para lançamento de: " + tipoOperacao.getDescricao() + ".");
        }
    }

    private void gerarSaldo(MovimentoGrupoBensImoveis movimento, SaldoGrupoBemImoveis ultimoSaldo) {
        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            ultimoSaldo = definirValoresNovoSaldo(ultimoSaldo, movimento);
            ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
            em.persist(ultimoSaldo);
        } else {
            if (DataUtil.getDataFormatada(movimento.getDataMovimento()).compareTo(DataUtil.getDataFormatada(ultimoSaldo.getDataSaldo())) == 0) {
                ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
                em.merge(ultimoSaldo);
            } else {
                SaldoGrupoBemImoveis novoSaldo;
                novoSaldo = definirValoresNovoSaldo(ultimoSaldo, movimento);
                novoSaldo = copiarValoresDoSaldoRecuperado(ultimoSaldo, novoSaldo, movimento);
                novoSaldo = alterarValorColunaCredioDebito(movimento, novoSaldo);
                em.persist(novoSaldo);
            }
        }
    }

    private void gerarSaldoPosterior(MovimentoGrupoBensImoveis movimento, List<SaldoGrupoBemImoveis> saldoPosterior) {
        if (!saldoPosterior.isEmpty()) {
            for (SaldoGrupoBemImoveis saldoParaAtualizar : saldoPosterior) {
                saldoParaAtualizar = alterarValorColunaCredioDebito(movimento, saldoParaAtualizar);
                em.merge(saldoParaAtualizar);
            }
        }
    }

    private SaldoGrupoBemImoveis definirValoresNovoSaldo(SaldoGrupoBemImoveis saldoGrupoBemImoveis, MovimentoGrupoBensImoveis movimento) {
        saldoGrupoBemImoveis = new SaldoGrupoBemImoveis();
        saldoGrupoBemImoveis.setDataSaldo(movimento.getDataMovimento());
        saldoGrupoBemImoveis.setGrupoBem(movimento.getGrupoBem());
        saldoGrupoBemImoveis.setTipoGrupo(movimento.getTipoGrupo());
        saldoGrupoBemImoveis.setUnidadeOrganizacional(movimento.getUnidadeOrganizacional());
        saldoGrupoBemImoveis.setNaturezaTipoGrupoBem(movimento.getNaturezaTipoGrupoBem());
        saldoGrupoBemImoveis.setCredito(new BigDecimal(BigInteger.ZERO));
        saldoGrupoBemImoveis.setDebito(new BigDecimal(BigInteger.ZERO));
        return saldoGrupoBemImoveis;
    }

    private SaldoGrupoBemImoveis alterarValorColunaCredioDebito(MovimentoGrupoBensImoveis movimento, SaldoGrupoBemImoveis saldoGrupoBemImoveis) {
        if (movimento.isOperacaoDebito()) {
            gerarValorDoDebito(movimento, saldoGrupoBemImoveis);
        } else {
            gerarValorDoCredito(movimento, saldoGrupoBemImoveis);
        }
        return saldoGrupoBemImoveis;
    }

    private SaldoGrupoBemImoveis copiarValoresDoSaldoRecuperado(SaldoGrupoBemImoveis saldoRecuperado, SaldoGrupoBemImoveis novoSaldo, MovimentoGrupoBensImoveis movimento) {
        novoSaldo.setDebito(saldoRecuperado.getDebito());
        novoSaldo.setCredito(saldoRecuperado.getCredito());
        return novoSaldo;
    }

    private void definirValorDebito(SaldoGrupoBemImoveis saldo, MovimentoGrupoBensImoveis movimento) {
        movimento.setDebito(movimento.getValor());
        if (saldo != null) {
            if (movimento.isLancamentoNormal()) {
                saldo.setDebito(saldo.getDebito().add(movimento.getValor()));
            } else {
                saldo.setDebito(saldo.getDebito().subtract(movimento.getValor()));
            }
        }
    }

    private void definirValorCredito(SaldoGrupoBemImoveis saldo, MovimentoGrupoBensImoveis movimento) {
        movimento.setCredito(movimento.getValor());
        if (saldo != null) {
            if (movimento.isLancamentoNormal()) {
                saldo.setCredito(saldo.getCredito().add(movimento.getValor()));
            } else {
                saldo.setCredito(saldo.getCredito().subtract(movimento.getValor()));
            }
        }
    }

    private void gerarValorDoDebito(MovimentoGrupoBensImoveis movimento, SaldoGrupoBemImoveis saldo) {
        ConfiguracaoContabil configuracaoContabil = getConfiguracaoContabil();

        if (movimento.isNaturezaOriginal() && verificarOperacaoParaNaturezaOriginalDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            definirValorDebito(saldo, movimento);
        }
        if (movimento.isNaturezaVPD() && verificarOperacaoParaNaturezaVPDDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            definirValorDebito(saldo, movimento);
        }
        if (movimento.isNaturezaDepreciacao() && verificarOperacaoParaNaturezaDepreciacaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            definirValorDebito(saldo, movimento);
        }
        if (movimento.isNaturezaAmortizacao() && verificarOperacaoParaNaturezaAmortizacaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            definirValorDebito(saldo, movimento);
        }
        if (movimento.isNaturezaExaustao() && verificarOperacaoParaNaturezaExaustaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            definirValorDebito(saldo, movimento);
        }
        if (movimento.isNaturezaReducao() && verificarOperacaoParaNaturezaReducaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            definirValorDebito(saldo, movimento);
        }
    }

    private void gerarValorDoCredito(MovimentoGrupoBensImoveis movimento, SaldoGrupoBemImoveis saldo) {

        if (movimento.isNaturezaOriginal()) {
            if (verificarOperacaoParaNaturezaOriginalCredito(movimento.getOperacao())) {
                definirValorCredito(saldo, movimento);
            }
        }
        if (movimento.isNaturezaVPA()) {
            if (verificarOperacaoParaNaturezaVPACredito(movimento.getOperacao())) {
                definirValorCredito(saldo, movimento);
            }
        }
        if (movimento.isNaturezaDepreciacao()) {
            if (verificarOperacaoParaNaturezaDepreciacaoCredito(movimento.getOperacao())) {
                definirValorCredito(saldo, movimento);
            }
        }
        if (movimento.isNaturezaAmortizacao()) {
            if (verificarOperacaoParaNaturezaAmortizacaoCredito(movimento.getOperacao())) {
                definirValorCredito(saldo, movimento);
            }
        }
        if (movimento.isNaturezaExaustao()) {
            if (verificarOperacaoParaNaturezaExaustaoCredito(movimento.getOperacao())) {
                definirValorCredito(saldo, movimento);
            }
        }
        if (movimento.isNaturezaReducao()) {
            if (verificarOperacaoParaNaturezaReducaoCredito(movimento.getOperacao())) {
                definirValorCredito(saldo, movimento);
            }
        }
    }

    private Boolean verificarOperacaoParaNaturezaOriginalDebito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case AQUISICAO_BENS_IMOVEIS:
                return true;
            case INCORPORACAO_BENS_IMOVEIS:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA:
                return true;
            case REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA:
                return true;
            case AJUSTE_BENS_IMOVEIS_AUMENTATIVO:
                return true;
            case AJUSTE_BENS_IMOVEIS_AUMENTATIVO_EMPRESA_PUBLICA:
                return true;
            case GANHOS_ALIENACAO_BENS_IMOVEIS:
                return true;
            case ALIENACAO_BENS_IMOVEIS:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaVPDDebito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case DEPRECIACAO_BENS_IMOVEIS:
                return true;
            case AMORTIZACAO_BENS_IMOVEIS:
                return true;
            case EXAUSTAO_BENS_IMOVEIS:
                return true;
            case REDUCAO_VALOR_RECUPERAVEL_BENS_IMOVEIS:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA:
                return true;
            case REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA:
                return true;
            case AJUSTE_BENS_IMOVEIS_DIMINUTIVO:
                return true;
            case AJUSTE_BENS_IMOVEIS_DIMINUTIVO_EMPRESA_PUBLICA:
                return true;
            case PERDAS_ALIENACAO_BENS_IMOVEIS:
                return true;
            case DESINCORPORACAO_BENS_IMOVEIS:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaDepreciacaoDebito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_DEPRECIACAO:
            case AJUSTE_BENS_IMOVEIS_DEPRECIACAO_AUMENTATIVO:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaAmortizacaoDebito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_AMORTIZACAO:
            case AJUSTE_BENS_IMOVEIS_AMORTIZACAO_AUMENTATIVO:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaExaustaoDebito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_EXAUSTAO:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaReducaoDebito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_REDUCAO_VALOR_RECUPERAVEL:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaOriginalCredito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_DEPRECIACAO:
                return true;
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_AMORTIZACAO:
                return true;
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_EXAUSTAO:
                return true;
            case APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_REDUCAO_VALOR_RECUPERAVEL:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA:
                return true;
            case REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA:
                return true;
            case AJUSTE_BENS_IMOVEIS_DIMINUTIVO:
                return true;
            case AJUSTE_BENS_IMOVEIS_DIMINUTIVO_EMPRESA_PUBLICA:
                return true;
            case PERDAS_ALIENACAO_BENS_IMOVEIS:
                return true;
            case ALIENACAO_BENS_IMOVEIS:
                return true;
            case DESINCORPORACAO_BENS_IMOVEIS:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaVPACredito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case INCORPORACAO_BENS_IMOVEIS:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA:
                return true;
            case REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA:
                return true;
            case AJUSTE_BENS_IMOVEIS_AUMENTATIVO:
                return true;
            case AJUSTE_BENS_IMOVEIS_AUMENTATIVO_EMPRESA_PUBLICA:
                return true;
            case GANHOS_ALIENACAO_BENS_IMOVEIS:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaDepreciacaoCredito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case DEPRECIACAO_BENS_IMOVEIS:
            case AJUSTE_BENS_IMOVEIS_DEPRECIACAO_DIMINUTIVO:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaAmortizacaoCredito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case AMORTIZACAO_BENS_IMOVEIS:
            case AJUSTE_BENS_IMOVEIS_AMORTIZACAO_DIMINUTIVO:
                return true;
            case TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaExaustaoCredito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case EXAUSTAO_BENS_IMOVEIS:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacaoParaNaturezaReducaoCredito(TipoOperacaoBensImoveis operacao) {
        switch (operacao) {
            case REDUCAO_VALOR_RECUPERAVEL_BENS_IMOVEIS:
                return true;
        }
        return false;
    }

    public NaturezaTipoGrupoBem recuperarNaturezaGrupoBem(TipoOperacaoBensImoveis operacao, TipoOperacao tipoOperacao) {
        NaturezaTipoGrupoBem naturezaTipoGrupoBem = null;
        if (TipoOperacao.DEBITO.equals(tipoOperacao)) {
            if (verificarOperacaoParaNaturezaOriginalDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.ORIGINAL;
            }
            if (verificarOperacaoParaNaturezaVPDDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.VPD;
            }
            if (verificarOperacaoParaNaturezaDepreciacaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.DEPRECIACAO;
            }
            if (verificarOperacaoParaNaturezaAmortizacaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.AMORTIZACAO;
            }
            if (verificarOperacaoParaNaturezaExaustaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.EXAUSTAO;
            }
            if (verificarOperacaoParaNaturezaReducaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.REDUCAO;
            }
        } else {
            if (verificarOperacaoParaNaturezaOriginalCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.ORIGINAL;
            }
            if (verificarOperacaoParaNaturezaVPACredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.VPA;
            }
            if (verificarOperacaoParaNaturezaDepreciacaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.DEPRECIACAO;
            }
            if (verificarOperacaoParaNaturezaAmortizacaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.AMORTIZACAO;
            }
            if (verificarOperacaoParaNaturezaExaustaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.EXAUSTAO;
            }
            if (verificarOperacaoParaNaturezaReducaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.REDUCAO;
            }
        }
        return naturezaTipoGrupoBem;
    }

    private void validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(MovimentoGrupoBensImoveis movimento, ConfiguracaoContabil configuracaoContabil) {
        if (configuracaoContabil.getBloquearSaldoNegativoBemImovel() && movimento.getValidarSaldo()) {
            SaldoGrupoBemImoveis ultimoSaldo = buscarUltimoSaldoPorData(movimento);
            BigDecimal saldoDoDia = ultimoSaldo.getCredito().subtract(ultimoSaldo.getDebito().add(movimento.getValor()));
            if (saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
                throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) + "</b>, na data " + DataUtil.getDataFormatada(ultimoSaldo.getDataSaldo()) + " referente a natureza: " + ultimoSaldo.getNaturezaTipoGrupoBem().getDescricao() + ".");
            }
        }
    }

    private void validarSaldoNegativo(MovimentoGrupoBensImoveis movimento) {
        SaldoGrupoBemImoveis ultimoSaldo = buscarUltimoSaldoPorData(movimento);
        BigDecimal saldoDoDia = ultimoSaldo.getCredito().subtract(ultimoSaldo.getDebito());
        if (movimento.getValidarSaldo() && saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) + "</b>, na data " + DataUtil.getDataFormatada(ultimoSaldo.getDataSaldo()) + " referente a natureza: " + ultimoSaldo.getNaturezaTipoGrupoBem().getDescricao() + ".");
        }
    }

    public SaldoGrupoBemImoveis buscarUltimoSaldoPorData(MovimentoGrupoBensImoveis movimento) throws ExcecaoNegocioGenerica {
        return buscarUltimoSaldoPorData(movimento.getGrupoBem(), movimento.getUnidadeOrganizacional(), movimento.getTipoGrupo(), movimento.getNaturezaTipoGrupoBem(), movimento.getDataMovimento());
    }

    public SaldoGrupoBemImoveis buscarUltimoSaldoPorData(GrupoBem grupoBem, UnidadeOrganizacional unidadeOrganizacional, TipoGrupo tipo, NaturezaTipoGrupoBem natureza, Date data) throws ExcecaoNegocioGenerica {
        try {
            String sql = " SELECT saldo.* FROM saldogrupobemimoveis saldo "
                + " WHERE trunc(saldo.datasaldo) <= to_date(:data, 'dd/mm/yyyy') "
                + " AND saldo.grupobem_id = :grupoBem"
                + " AND saldo.unidadeorganizacional_id = :unidade "
                + " AND saldo.tipogrupo = :tipoGrupo "
                + " AND saldo.naturezatipogrupobem = :natureza "
                + " ORDER BY saldo.datasaldo DESC ";

            Query q = em.createNativeQuery(sql, SaldoGrupoBemImoveis.class);

            q.setParameter("grupoBem", grupoBem.getId());
            q.setParameter("unidade", unidadeOrganizacional.getId());
            q.setParameter("tipoGrupo", tipo.name());
            q.setParameter("natureza", natureza.name());
            q.setParameter("data", DataUtil.getDataFormatada(data));
            q.setMaxResults(1);
            List<SaldoGrupoBemImoveis> resultado = q.getResultList();
            if (resultado.isEmpty()) {
                SaldoGrupoBemImoveis saldo = new SaldoGrupoBemImoveis();
                saldo.setDataSaldo(data);
                saldo.setGrupoBem(grupoBem);
                saldo.setTipoGrupo(tipo);
                saldo.setUnidadeOrganizacional(unidadeOrganizacional);
                saldo.setNaturezaTipoGrupoBem(natureza);
                saldo.setCredito(new BigDecimal(BigInteger.ZERO));
                saldo.setDebito(new BigDecimal(BigInteger.ZERO));
                return saldo;
            } else {
                return resultado.get(0);
            }
        } catch (IllegalArgumentException e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public List<SaldoGrupoBemImoveis> buscarSaldosPosterioresAData(MovimentoGrupoBensImoveis movimento) {

        String sql = " select s.* from saldogrupobemimoveis s "
            + " where trunc(s.dataSaldo) > to_date(:data, 'dd/mm/yyyy') "
            + " and s.unidadeOrganizacional_id = :unidade "
            + " and s.grupobem_id = :grupo "
            + " and s.naturezatipogrupobem = :natureza "
            + " and s.tipoGrupo = :tipoGrupo ";

        Query q = em.createNativeQuery(sql, SaldoGrupoBemImoveis.class);
        q.setParameter("grupo", movimento.getGrupoBem().getId());
        q.setParameter("unidade", movimento.getUnidadeOrganizacional().getId());
        q.setParameter("tipoGrupo", movimento.getTipoGrupo().name());
        q.setParameter("natureza", movimento.getNaturezaTipoGrupoBem().name());
        q.setParameter("data", DataUtil.getDataFormatada(movimento.getDataMovimento()));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public void excluirSaldoPorPeriodo(Date dataInidical, Date dataFinal, GrupoBem grupoBem) {

        String sql = " delete saldogrupobemimoveis s" +
            " where trunc(s.datasaldo) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
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

        String sql = " delete movimentogrupobensimoveis m " +
            " where trunc(m.datamovimento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
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

    public void limparConfiguracaoContabil() {
        configuracaoContabil = null;
    }
}
