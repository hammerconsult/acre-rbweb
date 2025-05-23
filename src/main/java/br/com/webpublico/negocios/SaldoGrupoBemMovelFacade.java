package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
public class SaldoGrupoBemMovelFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private ConfiguracaoContabil configuracaoContabil;

    @Lock(LockType.WRITE)
    public void geraSaldoGrupoBemMoveis(UnidadeOrganizacional unidade, GrupoBem grupoBem, BigDecimal valor,
                                        TipoGrupo tipoGrupo, Date data, TipoOperacaoBensMoveis operacao,
                                        TipoLancamento tipoLancamento, TipoOperacao tipoOperacao, Boolean validarSaldo) throws ExcecaoNegocioGenerica {

        NaturezaTipoGrupoBem naturezaTipoGrupoBem = recuperarNaturezaGrupoBem(operacao, tipoOperacao);
        validarNaturezaDoSaldo(operacao, tipoOperacao, naturezaTipoGrupoBem);

        MovimentoGrupoBensMoveis movimento = new MovimentoGrupoBensMoveis();
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

        SaldoGrupoBem ultimoSaldo = recuperaUltimoSaldoPorData(grupoBem, unidade, tipoGrupo, naturezaTipoGrupoBem, data);
        List<SaldoGrupoBem> saldoPosterior = recuperaSaldosPosterioresAData(movimento);

        gerarSaldo(movimento, ultimoSaldo);
        gerarSaldoPosterior(movimento, saldoPosterior);
        em.merge(movimento);
    }

    private void validarNaturezaDoSaldo(TipoOperacaoBensMoveis operacao, TipoOperacao tipoOperacao, NaturezaTipoGrupoBem naturezaTipoGrupoBem) {
        if (naturezaTipoGrupoBem == null) {
            throw new ExcecaoNegocioGenerica("Natureza do saldo não encontrada para a operação: " + operacao.getDescricao() + " para o lançamento de: " + tipoOperacao.getDescricao() + ".");
        }
    }

    private void gerarSaldo(MovimentoGrupoBensMoveis movimento, SaldoGrupoBem ultimoSaldo) {
        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            ultimoSaldo = definirValoresNovoSaldo(ultimoSaldo, movimento);
            ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
            em.persist(ultimoSaldo);
        } else {
            if (DataUtil.dataSemHorario(movimento.getDataMovimento()).compareTo(DataUtil.dataSemHorario(ultimoSaldo.getDataSaldo())) == 0) {
                ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
                em.merge(ultimoSaldo);
            } else {
                SaldoGrupoBem novoSaldo;
                novoSaldo = definirValoresNovoSaldo(ultimoSaldo, movimento);
                novoSaldo = copiarValoresDoSaldoRecuperado(ultimoSaldo, novoSaldo);
                novoSaldo = alterarValorColunaCredioDebito(movimento, novoSaldo);
                em.persist(novoSaldo);
            }
        }
    }

    private void gerarSaldoPosterior(MovimentoGrupoBensMoveis movimento, List<SaldoGrupoBem> saldoPosterior) {
        if (!saldoPosterior.isEmpty()) {
            for (SaldoGrupoBem saldoParaAtualizar : saldoPosterior) {
                saldoParaAtualizar = alterarValorColunaCredioDebito(movimento, saldoParaAtualizar);
                em.merge(saldoParaAtualizar);
            }
        }
    }

    private SaldoGrupoBem alterarValorColunaCredioDebito(MovimentoGrupoBensMoveis movimento, SaldoGrupoBem saldoGrupoBem) {
        if (movimento.isOperacaoDebito()) {
            gerarValorDoDebito(movimento, saldoGrupoBem);
        } else {
            gerarValorDoCredito(movimento, saldoGrupoBem);
        }
        return saldoGrupoBem;
    }

    private SaldoGrupoBem copiarValoresDoSaldoRecuperado(SaldoGrupoBem saldoRecuperado, SaldoGrupoBem novoSaldo) {
        novoSaldo.setDebito(saldoRecuperado.getDebito());
        novoSaldo.setCredito(saldoRecuperado.getCredito());
        return novoSaldo;
    }

    private SaldoGrupoBem definirValoresNovoSaldo(SaldoGrupoBem saldoGrupoBem, MovimentoGrupoBensMoveis movimento) {
        saldoGrupoBem = new SaldoGrupoBem();
        saldoGrupoBem.setDataSaldo(movimento.getDataMovimento());
        saldoGrupoBem.setGrupoBem(movimento.getGrupoBem());
        saldoGrupoBem.setTipoGrupo(movimento.getTipoGrupo());
        saldoGrupoBem.setUnidadeOrganizacional(movimento.getUnidadeOrganizacional());
        saldoGrupoBem.setNaturezaTipoGrupoBem(movimento.getNaturezaTipoGrupoBem());
        saldoGrupoBem.setCredito(new BigDecimal(BigInteger.ZERO));
        saldoGrupoBem.setDebito(new BigDecimal(BigInteger.ZERO));
        return saldoGrupoBem;
    }


    private Boolean verificarOperacoesParaNaturezaOriginalDebito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case AQUISICAO_BENS_MOVEIS:
                return true;
            case INCORPORACAO_BENS_MOVEIS:
                return true;
            case TRANFERENCIA_BENS_MOVEIS_RECEBIDA:
                return true;
            case REAVALIACAO_BENS_MOVEIS_AUMENTATIVA:
                return true;
            case GANHOS_ALIENACAO_BENS_MOVEIS:
                return true;
            case ALIENACAO_BENS_MOVEIS:
                return true;
            case AJUSTE_BENS_MOVEIS_AUMENTATIVO:
                return true;
            case AJUSTE_BENS_MOVEIS_AUMENTATIVO_EMPRESA_PUBLICA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaVPDDebito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case DEPRECIACAO_BENS_MOVEIS:
                return true;
            case AMORTIZACAO_BENS_MOVEIS:
                return true;
            case EXAUSTAO_BENS_MOVEIS:
                return true;
            case REDUCAO_VALOR_RECUPERAVEL_BENS_MOVEIS:
                return true;
            case TRANFERENCIABENS_MOVEIS_CONCEDIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA:
                return true;
            case REAVALIACAO_BENS_MOVEIS_DIMINUTIVA:
                return true;
            case PERDAS_ALIENACAO_BENS_MOVEIS:
                return true;
            case DESINCORPORACAO_BENS_MOVEIS:
                return true;
            case AJUSTE_BENS_MOVEIS_DIMINUTIVO:
                return true;
            case AJUSTE_BENS_MOVEIS_DIMINUTIVO_EMPRESA_PUBLICA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaDepreciacaoDebito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_DEPRECIACAO:
                return true;
            case TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA:
                return true;
            case AJUSTE_BENS_MOVEIS_DEPRECIACAO_AUMENTATIVO:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaAmortizacaoDebito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_AMORTIZACAO:
            case AJUSTE_BENS_MOVEIS_AMORTIZACAO_AUMENTATIVO:
                return true;
            case TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaExaustaoDebito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_EXAUSTAO:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaReducaoDebito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_REDUCAO_VALOR_RECUPERAVEL:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaOriginalCredito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_DEPRECIACAO:
                return true;
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_AMORTIZACAO:
                return true;
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_EXAUSTAO:
                return true;
            case APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_REDUCAO_VALOR_RECUPERAVEL:
                return true;
            case TRANFERENCIABENS_MOVEIS_CONCEDIDA:
                return true;
            case REAVALIACAO_BENS_MOVEIS_DIMINUTIVA:
                return true;
            case PERDAS_ALIENACAO_BENS_MOVEIS:
                return true;
            case ALIENACAO_BENS_MOVEIS:
                return true;
            case DESINCORPORACAO_BENS_MOVEIS:
                return true;
            case AJUSTE_BENS_MOVEIS_DIMINUTIVO:
                return true;
            case AJUSTE_BENS_MOVEIS_DIMINUTIVO_EMPRESA_PUBLICA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaVPACredito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case INCORPORACAO_BENS_MOVEIS:
                return true;
            case TRANFERENCIA_BENS_MOVEIS_RECEBIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA:
                return true;
            case REAVALIACAO_BENS_MOVEIS_AUMENTATIVA:
                return true;
            case AJUSTE_BENS_MOVEIS_AUMENTATIVO:
                return true;
            case AJUSTE_BENS_MOVEIS_AUMENTATIVO_EMPRESA_PUBLICA:
                return true;
            case GANHOS_ALIENACAO_BENS_MOVEIS:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaDepreciacaoCredito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case DEPRECIACAO_BENS_MOVEIS:
                return true;
            case TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA:
                return true;
            case TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA:
                return true;
            case AJUSTE_BENS_MOVEIS_DEPRECIACAO_DIMINUTIVO:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaAmortizacaoCredito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case AMORTIZACAO_BENS_MOVEIS:
            case AJUSTE_BENS_MOVEIS_AMORTIZACAO_DIMINUTIVO:
                return true;
            case TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaExaustaoCredito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case EXAUSTAO_BENS_MOVEIS:
                return true;
        }
        return false;
    }

    private Boolean verificarOperacoesParaNaturezaReducaoCredito(TipoOperacaoBensMoveis operacao) {
        switch (operacao) {
            case REDUCAO_VALOR_RECUPERAVEL_BENS_MOVEIS:
                return true;
        }
        return false;
    }

    private void atribuirValorDebito(SaldoGrupoBem saldo, MovimentoGrupoBensMoveis movimento) {
        movimento.setDebito(movimento.getValor());
        if (saldo != null) {
            if (movimento.isLancamentoNormal()) {
                saldo.setDebito(saldo.getDebito().add(movimento.getValor()));
            } else {
                saldo.setDebito(saldo.getDebito().subtract(movimento.getValor()));
            }
        }
    }

    private void atribuirValorCredito(SaldoGrupoBem saldo, MovimentoGrupoBensMoveis movimento) {
        movimento.setCredito(movimento.getValor());
        if (saldo != null) {
            if (movimento.isLancamentoNormal()) {
                saldo.setCredito(saldo.getCredito().add(movimento.getValor()));
            } else {
                saldo.setCredito(saldo.getCredito().subtract(movimento.getValor()));
            }
        }
    }

    private void gerarValorDoDebito(MovimentoGrupoBensMoveis movimento, SaldoGrupoBem saldo) {
        ConfiguracaoContabil configuracaoContabil = getConfiguracaoContabil();

        if (movimento.getNaturezaTipoGrupoBem().isOriginal() && verificarOperacoesParaNaturezaOriginalDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            atribuirValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().isVPD() && verificarOperacoesParaNaturezaVPDDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            atribuirValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().isDepreciacao() && verificarOperacoesParaNaturezaDepreciacaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            atribuirValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().isAmortizacao() && verificarOperacoesParaNaturezaAmortizacaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            atribuirValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().isExaustao() && verificarOperacoesParaNaturezaExaustaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            atribuirValorDebito(saldo, movimento);
        }
        if (movimento.getNaturezaTipoGrupoBem().isReducao() && verificarOperacoesParaNaturezaReducaoDebito(movimento.getOperacao())) {
            validarSaldoNegativo(movimento);
            atribuirValorDebito(saldo, movimento);
        }
    }

    private void gerarValorDoCredito(MovimentoGrupoBensMoveis movimento, SaldoGrupoBem saldo) {

        if (movimento.getNaturezaTipoGrupoBem().isOriginal()) {
            if (verificarOperacoesParaNaturezaOriginalCredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().isVPA()) {
            if (verificarOperacoesParaNaturezaVPACredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().isDepreciacao()) {
            if (verificarOperacoesParaNaturezaDepreciacaoCredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().isAmortizacao()) {
            if (verificarOperacoesParaNaturezaAmortizacaoCredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().isExaustao()) {
            if (verificarOperacoesParaNaturezaExaustaoCredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
        if (movimento.getNaturezaTipoGrupoBem().isReducao()) {
            if (verificarOperacoesParaNaturezaReducaoCredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
    }

    public NaturezaTipoGrupoBem recuperarNaturezaGrupoBem(TipoOperacaoBensMoveis operacao, TipoOperacao tipoOperacao) {
        NaturezaTipoGrupoBem naturezaTipoGrupoBem = null;
        if (TipoOperacao.DEBITO.equals(tipoOperacao)) {
            if (verificarOperacoesParaNaturezaOriginalDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.ORIGINAL;
            }
            if (verificarOperacoesParaNaturezaVPDDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.VPD;
            }
            if (verificarOperacoesParaNaturezaDepreciacaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.DEPRECIACAO;
            }
            if (verificarOperacoesParaNaturezaAmortizacaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.AMORTIZACAO;
            }
            if (verificarOperacoesParaNaturezaExaustaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.EXAUSTAO;
            }
            if (verificarOperacoesParaNaturezaReducaoDebito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.REDUCAO;
            }
        } else {
            if (verificarOperacoesParaNaturezaOriginalCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.ORIGINAL;
            }
            if (verificarOperacoesParaNaturezaVPACredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.VPA;
            }
            if (verificarOperacoesParaNaturezaDepreciacaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.DEPRECIACAO;
            }
            if (verificarOperacoesParaNaturezaAmortizacaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.AMORTIZACAO;
            }
            if (verificarOperacoesParaNaturezaExaustaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.EXAUSTAO;
            }
            if (verificarOperacoesParaNaturezaReducaoCredito(operacao)) {
                naturezaTipoGrupoBem = NaturezaTipoGrupoBem.REDUCAO;
            }
        }
        return naturezaTipoGrupoBem;
    }

    private void validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(MovimentoGrupoBensMoveis movimento, ConfiguracaoContabil configuracaoContabil) {
        if (configuracaoContabil.getBloquearSaldoNegativoBemMovel() && movimento.getValidarSaldo()) {
            SaldoGrupoBem saldoGrupoBem = recuperaUltimoSaldoPorData(movimento.getGrupoBem(), movimento.getUnidadeOrganizacional(), movimento.getTipoGrupo(), movimento.getNaturezaTipoGrupoBem(), movimento.getDataMovimento());
            BigDecimal saldoDoDia = saldoGrupoBem.getCredito().subtract(saldoGrupoBem.getDebito().add(movimento.getValor()));
            if (saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
                throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) +
                    "</b>, na data " + DataUtil.getDataFormatada(movimento.getDataMovimento()) + " referente a natureza: " +
                    movimento.getNaturezaTipoGrupoBem().getDescricao() + ".");
            }
        }
    }

    private void validarSaldoNegativo(MovimentoGrupoBensMoveis movimento) {
        SaldoGrupoBem saldoGrupoBem = recuperaUltimoSaldoPorData(movimento.getGrupoBem(), movimento.getUnidadeOrganizacional(), movimento.getTipoGrupo(), movimento.getNaturezaTipoGrupoBem(), movimento.getDataMovimento());
        BigDecimal saldoDoDia = saldoGrupoBem.getCredito().subtract(saldoGrupoBem.getDebito());
        if (movimento.getValidarSaldo() && saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) +
                "</b>, na data " + DataUtil.getDataFormatada(saldoGrupoBem.getDataSaldo()) + " referente a natureza: " +
                saldoGrupoBem.getNaturezaTipoGrupoBem().getDescricao() + ".");
        }
    }

    public SaldoGrupoBem recuperaUltimoSaldoPorData(GrupoBem grupoBem, UnidadeOrganizacional unidadeOrganizacional, TipoGrupo tipo, NaturezaTipoGrupoBem natureza, Date data) throws ExcecaoNegocioGenerica {

        String sql = " SELECT saldo.* FROM saldogrupobem saldo "
            + " WHERE trunc(saldo.datasaldo) <= to_date(:dataSaldo, 'dd/mm/yyyy') "
            + " AND saldo.grupobem_id = :grupoBem"
            + " AND saldo.unidadeorganizacional_id = :unidade "
            + " AND saldo.tipogrupo = :tipoGrupo "
            + " AND saldo.naturezatipogrupobem = :natureza "
            + " ORDER BY saldo.datasaldo DESC ";

        Query q = em.createNativeQuery(sql, SaldoGrupoBem.class);

        q.setParameter("grupoBem", grupoBem.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("tipoGrupo", tipo.name());
        q.setParameter("natureza", natureza.name());
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(data));

        List<SaldoGrupoBem> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return new SaldoGrupoBem();
    }

    public List<SaldoGrupoBem> recuperaSaldosPosterioresAData(MovimentoGrupoBensMoveis movimento) {

        String sql = " select s.* from saldogrupobem s "
            + " where trunc(s.dataSaldo) > to_date(:dataSaldo, 'dd/mm/yyyy') "
            + " and s.unidadeOrganizacional_id = :unidade "
            + " and s.grupobem_id = :grupo "
            + " and s.naturezatipogrupobem = :natureza "
            + " and s.tipoGrupo = :tipoGrupo ";

        Query q = em.createNativeQuery(sql, SaldoGrupoBem.class);
        q.setParameter("grupo", movimento.getGrupoBem().getId());
        q.setParameter("unidade", movimento.getUnidadeOrganizacional().getId());
        q.setParameter("tipoGrupo", movimento.getTipoGrupo().name());
        q.setParameter("natureza", movimento.getNaturezaTipoGrupoBem().name());
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(movimento.getDataMovimento()));
        List<SaldoGrupoBem> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    public void excluirSaldoPorPeriodo(Date dataInicial, Date dataFinal, GrupoBem grupoBem) {

        String sql = " delete saldogrupobem s " +
            " where trunc(s.datasaldo) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
        if (grupoBem != null) {
            sql += " and s.grupobem_id = :idGrupoBem ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (grupoBem != null) {
            q.setParameter("idGrupoBem", grupoBem.getId());
        }
        q.executeUpdate();
    }


    public void excluirMovimentoPorPeriodo(Date dataInidical, Date dataFinal, GrupoBem grupoBem) {

        String sql = " delete movimentogrupobensmoveis m " +
            " where trunc(m.datamovimento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  ";
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

    public BigDecimal recuperarUltimoSaldoGrupoBem(GrupoBem grupoBem, UnidadeOrganizacional unidade, Date dataFinal, NaturezaTipoGrupoBem naturezaTipoGrupoBem) {
        String sql = "";
        sql = " select coalesce(sum(credito), 0) - coalesce(sum(debito), 0) as saldoFinal from ( " +
            " SELECT coalesce(saldo.credito, 0) as credito, " +
            "        coalesce(saldo.debito, 0) as debito " +
            "   FROM SALDOGRUPOBEM saldo  " +
            "  WHERE saldo.UNIDADEORGANIZACIONAL_ID = :unidade  " +
            "    AND saldo.TIPOGRUPO = :tipobemmovel  " +
            "    AND saldo.NATUREZATIPOGRUPOBEM = :natureza  ";
        sql += grupoBem != null ? " AND saldo.GRUPOBEM_ID = :grupo  " : "";
        sql += "    AND trunc(saldo.datasaldo) = (SELECT MAX(trunc(sld.datasaldo))  " +
            "                       FROM SALDOGRUPOBEM sld  " +
            "                      WHERE sld.UNIDADEORGANIZACIONAL_ID = saldo.UNIDADEORGANIZACIONAL_ID  " +
            "                        AND sld.TIPOGRUPO = saldo.TIPOGRUPO  " +
            "                        AND sld.NATUREZATIPOGRUPOBEM = saldo.NATUREZATIPOGRUPOBEM  " +
            "                        AND sld.GRUPOBEM_ID = saldo.grupobem_id  " +
            "                        and trunc(sld.datasaldo) <= to_date(:dataFinal, 'dd/mm/yyyy'))  " +
            " )  ";

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("unidade", unidade.getId());
        if (grupoBem != null) {
            q.setParameter("grupo", grupoBem.getId());
        }
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("natureza", naturezaTipoGrupoBem.name());
        q.setParameter("tipobemmovel", TipoGrupo.BEM_MOVEL_PRINCIPAL.name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
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
