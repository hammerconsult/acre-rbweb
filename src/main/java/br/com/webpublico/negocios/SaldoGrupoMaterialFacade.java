package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoGrupoMaterialDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.contabil.ApiServiceContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
public class SaldoGrupoMaterialFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private ConfiguracaoContabil configuracaoContabil;

    @Lock(LockType.WRITE)
    public void geraSaldoGrupoMaterial(UnidadeOrganizacional unidade, GrupoMaterial grupoMaterial, BigDecimal valor,
                                       TipoEstoque tipoEstoque, Date data, TipoOperacaoBensEstoque operacao, TipoLancamento tipoLancamento,
                                       TipoOperacao tipoOperacao, Long idOrigem, Boolean validarSaldo) throws ExcecaoNegocioGenerica {
        if (configuracaoContabilFacade.isGerarSaldoUtilizandoMicroService("SALDOGRUPOMATERIALMICROSERVICE")) {
            SaldoGrupoMaterialDTO dto = new SaldoGrupoMaterialDTO();
            dto.setIdUnidadeOrganizacional(unidade.getId());
            dto.setIdGrupoMaterial(grupoMaterial.getId());
            dto.setValor(valor);
            dto.setTipoEstoque(tipoEstoque);
            dto.setTipoLancamento(tipoLancamento);
            dto.setTipoOperacaoBensEstoque(operacao);
            dto.setTipoOperacao(tipoOperacao);
            dto.setData(DataUtil.dateToLocalDate(data));
            dto.setValidarSaldo(validarSaldo);
            dto.setIdOrigem(idOrigem);
            ApiServiceContabil.getService().gerarSaldoGrupoMaterial(dto);
        } else {
            NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial = recuperarNaturezaGrupoMaterial(operacao, tipoOperacao);
            MovimentoGrupoMaterial movimento = new MovimentoGrupoMaterial();
            movimento.setId(null);
            movimento.setOrigem(idOrigem);
            movimento.setDataMovimento(data);
            movimento.setUnidadeOrganizacional(unidade);
            movimento.setGrupoMaterial(grupoMaterial);
            movimento.setNaturezaTipoGrupoMaterial(naturezaTipoGrupoMaterial);
            movimento.setTipoEstoque(tipoEstoque);
            movimento.setOperacao(operacao);
            movimento.setTipoLancamento(tipoLancamento);
            movimento.setTipoOperacao(tipoOperacao);
            movimento.setValor(valor);
            movimento.setValidarSaldo(validarSaldo);
            alterarValorColunaCredioDebito(movimento, null);
            SaldoGrupoMaterial ultimoSaldo = buscarUltimoSaldoPorData(movimento);
            List<SaldoGrupoMaterial> saldosPosteriores = recuperaSaldosPosterioresAData(movimento);
            gerarSaldo(movimento, ultimoSaldo);
            gerarSaldoPosterior(movimento, saldosPosteriores);
            em.merge(movimento);
        }
    }

    private void gerarSaldo(MovimentoGrupoMaterial movimento, SaldoGrupoMaterial ultimoSaldo) {

        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            ultimoSaldo = atribuirValoresParaNovoSaldo(ultimoSaldo, movimento);
            ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
            em.persist(ultimoSaldo);
        } else {
            if (DataUtil.getDataFormatada(movimento.getDataMovimento()).compareTo(DataUtil.getDataFormatada(ultimoSaldo.getDataSaldo())) == 0) {
                ultimoSaldo = alterarValorColunaCredioDebito(movimento, ultimoSaldo);
                em.merge(ultimoSaldo);
            } else {
                SaldoGrupoMaterial novoSaldo;
                novoSaldo = atribuirValoresParaNovoSaldo(ultimoSaldo, movimento);
                novoSaldo = copiarValoresDoSaldoRecuperado(ultimoSaldo, novoSaldo);
                novoSaldo = alterarValorColunaCredioDebito(movimento, novoSaldo);
                em.persist(novoSaldo);
            }
        }
    }

    private void gerarSaldoPosterior(MovimentoGrupoMaterial movimento, List<SaldoGrupoMaterial> saldosPosteriores) {
        if (!saldosPosteriores.isEmpty()) {
            for (SaldoGrupoMaterial saldoParaAtualizar : saldosPosteriores) {
                saldoParaAtualizar = alterarValorColunaCredioDebito(movimento, saldoParaAtualizar);
                em.merge(saldoParaAtualizar);
            }
        }
    }

    private SaldoGrupoMaterial copiarValoresDoSaldoRecuperado(SaldoGrupoMaterial saldoRecuperado, SaldoGrupoMaterial novoSaldo) {
        novoSaldo.setDebito(saldoRecuperado.getDebito());
        novoSaldo.setCredito(saldoRecuperado.getCredito());
        return novoSaldo;
    }

    private SaldoGrupoMaterial alterarValorColunaCredioDebito(MovimentoGrupoMaterial movimento, SaldoGrupoMaterial saldoGrupoMaterial) {
        if (movimento.getTipoOperacao().equals(TipoOperacao.DEBITO)) {
            gerarValorDoDebito(movimento, saldoGrupoMaterial);
        } else {
            gerarValorDoCredito(movimento, saldoGrupoMaterial);
        }
        return saldoGrupoMaterial;
    }

    private SaldoGrupoMaterial atribuirValoresParaNovoSaldo(SaldoGrupoMaterial saldoGrupoMaterial, MovimentoGrupoMaterial movimento) {
        saldoGrupoMaterial = new SaldoGrupoMaterial();
        saldoGrupoMaterial.setDataSaldo(movimento.getDataMovimento());
        saldoGrupoMaterial.setGrupoMaterial(movimento.getGrupoMaterial());
        saldoGrupoMaterial.setTipoEstoque(movimento.getTipoEstoque());
        saldoGrupoMaterial.setUnidadeOrganizacional(movimento.getUnidadeOrganizacional());
        saldoGrupoMaterial.setNaturezaTipoGrupoMaterial(movimento.getNaturezaTipoGrupoMaterial());
        saldoGrupoMaterial.setCredito(new BigDecimal(BigInteger.ZERO));
        saldoGrupoMaterial.setDebito(new BigDecimal(BigInteger.ZERO));
        return saldoGrupoMaterial;
    }

    private void atribuirValorDebito(SaldoGrupoMaterial saldo, MovimentoGrupoMaterial movimento) {
        if (TipoLancamento.NORMAL.equals(movimento.getTipoLancamento())) {
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

    private void atribuirValorCredito(SaldoGrupoMaterial saldo, MovimentoGrupoMaterial movimento) {
        if (TipoLancamento.NORMAL.equals(movimento.getTipoLancamento())) {
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

    private void gerarValorDoDebito(MovimentoGrupoMaterial movimento, SaldoGrupoMaterial saldo) {
        ConfiguracaoContabil configuracaoContabil = getConfiguracaoContabil();

        if (movimento.isNaturezaOriginal() && recuperarOperacoesParaNaturezaOriginalDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            atribuirValorDebito(saldo, movimento);
        }
        if (movimento.isNaturezaVPD() && recuperarOperacoesParaNaturezaVPDDebito(movimento.getOperacao())) {
            validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(movimento, configuracaoContabil);
            atribuirValorDebito(saldo, movimento);
        }
        if (movimento.isNaturezaAjustePerda() && recuperarOperacoesParaNaturezaAjustePerdaDebito(movimento.getOperacao())) {
            validarSaldo(movimento);
            atribuirValorDebito(saldo, movimento);
        }
    }

    private void gerarValorDoCredito(MovimentoGrupoMaterial movimento, SaldoGrupoMaterial saldo) {

        if (movimento.isNaturezaOriginal()) {
            if (recuperarOperacoesParaNaturezaOriginalCredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
        if (movimento.isNaturezaVPA()) {
            if (recuperarOperacoesParaNaturezaVPACredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
        if (movimento.isNaturezaAjustePerda()) {
            if (recuperarOperacoesParaNaturezaAjustePerdaCredito(movimento.getOperacao())) {
                atribuirValorCredito(saldo, movimento);
            }
        }
    }

    private Boolean recuperarOperacoesParaNaturezaOriginalDebito(TipoOperacaoBensEstoque operacao) {
        switch (operacao) {
            case AQUISICAO_BENS_ESTOQUE:
                return true;
            case INCORPORACAO_BENS_ESTOQUE:
                return true;
            case TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA:
                return true;
            case REAVALIACAO_BENS_ESTOQUE_AUMENTATIVA:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaVPDDebito(TipoOperacaoBensEstoque operacao) {
        switch (operacao) {
            case AJUSTE_PERDAS_BENS_ESTOQUE:
                return true;
            case AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE:
                return true;
            case TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA:
                return true;
            case REAVALIACAO_BENS_ESTOQUE_DIMINUTIVA:
                return true;
            case BAIXA_BENS_ESTOQUE_POR_CONSUMO:
                return true;
            case BAIXA_BENS_ESTOQUE_POR_DESINCORPORACAO:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaAjustePerdaDebito(TipoOperacaoBensEstoque operacao) {
        switch (operacao) {
            case REVERSAO_AJUSTE_PERDAS_BENS_ESTOQUE:
                return true;
            case REVERSAO_AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaOriginalCredito(TipoOperacaoBensEstoque operacao) {
        switch (operacao) {
            case TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA:
                return true;
            case REAVALIACAO_BENS_ESTOQUE_DIMINUTIVA:
                return true;
            case BAIXA_BENS_ESTOQUE_POR_CONSUMO:
                return true;
            case BAIXA_BENS_ESTOQUE_POR_DESINCORPORACAO:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaVPACredito(TipoOperacaoBensEstoque operacao) {
        switch (operacao) {
            case INCORPORACAO_BENS_ESTOQUE:
                return true;
            case REVERSAO_AJUSTE_PERDAS_BENS_ESTOQUE:
                return true;
            case REVERSAO_AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE:
                return true;
            case TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA:
                return true;
            case REAVALIACAO_BENS_ESTOQUE_AUMENTATIVA:
                return true;
        }
        return false;
    }

    private Boolean recuperarOperacoesParaNaturezaAjustePerdaCredito(TipoOperacaoBensEstoque operacao) {
        switch (operacao) {
            case AJUSTE_PERDAS_BENS_ESTOQUE:
                return true;
            case AJUSTE_REDUCAO_VALOR_MERCADO_BENS_ESTOQUE:
                return true;
        }
        return false;
    }

    public NaturezaTipoGrupoMaterial recuperarNaturezaGrupoMaterial(TipoOperacaoBensEstoque operacao, TipoOperacao tipoOperacao) {
        NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial = null;
        if (TipoOperacao.DEBITO.equals(tipoOperacao)) {
            if (recuperarOperacoesParaNaturezaOriginalDebito(operacao)) {
                naturezaTipoGrupoMaterial = NaturezaTipoGrupoMaterial.ORIGINAL;
            }
            if (recuperarOperacoesParaNaturezaVPDDebito(operacao)) {
                naturezaTipoGrupoMaterial = NaturezaTipoGrupoMaterial.VPD;
            }
            if (recuperarOperacoesParaNaturezaAjustePerdaDebito(operacao)) {
                naturezaTipoGrupoMaterial = NaturezaTipoGrupoMaterial.AJUSTE_PERDA;
            }
        } else {
            if (recuperarOperacoesParaNaturezaOriginalCredito(operacao)) {
                naturezaTipoGrupoMaterial = NaturezaTipoGrupoMaterial.ORIGINAL;
            }
            if (recuperarOperacoesParaNaturezaVPACredito(operacao)) {
                naturezaTipoGrupoMaterial = NaturezaTipoGrupoMaterial.VPA;
            }
            if (recuperarOperacoesParaNaturezaAjustePerdaCredito(operacao)) {
                naturezaTipoGrupoMaterial = NaturezaTipoGrupoMaterial.AJUSTE_PERDA;
            }
        }
        return naturezaTipoGrupoMaterial;
    }

    private void validarSaldoNegativoVerificandoBloqueioConfiguracaoContabil(MovimentoGrupoMaterial movimento, ConfiguracaoContabil configuracaoContabil) {
        if (configuracaoContabil.getBloquearSaldoNegativoBemEstoque() && movimento.getValidarSaldo()) {
            SaldoGrupoMaterial saldoGrupoMaterial = buscarUltimoSaldoPorData(movimento);
            BigDecimal saldoDoDia = saldoGrupoMaterial.getCredito().subtract(saldoGrupoMaterial.getDebito().add(movimento.getValor()));
            if (saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
                throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b>" + Util.formataValor(saldoDoDia) + "</b>, na data " + DataUtil.getDataFormatada(saldoGrupoMaterial.getDataSaldo()) + " referente a natureza: " + saldoGrupoMaterial.getNaturezaTipoGrupoMaterial().getDescricao() + ".");
            }
        }
    }

    private void validarSaldo(MovimentoGrupoMaterial movimento) {
        SaldoGrupoMaterial saldoGrupoMaterial = buscarUltimoSaldoPorData(movimento);
        BigDecimal saldoDoDia = saldoGrupoMaterial.getCredito().subtract(saldoGrupoMaterial.getDebito());
        if (movimento.getValidarSaldo() && saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) + "</b>, na data " + DataUtil.getDataFormatada(saldoGrupoMaterial.getDataSaldo()) + " referente a natureza: " + saldoGrupoMaterial.getNaturezaTipoGrupoMaterial().getDescricao() + ".");
        }
    }

    public SaldoGrupoMaterial buscarUltimoSaldoPorData(MovimentoGrupoMaterial movimento) throws ExcecaoNegocioGenerica {
        return buscarUltimoSaldoPorData(movimento.getGrupoMaterial(), movimento.getUnidadeOrganizacional(), movimento.getTipoEstoque(), movimento.getNaturezaTipoGrupoMaterial(), movimento.getDataMovimento());
    }
    public SaldoGrupoMaterial buscarUltimoSaldoPorData(GrupoMaterial grupoMaterial, UnidadeOrganizacional unidadeOrganizacional, TipoEstoque tipoEstoque, NaturezaTipoGrupoMaterial natureza, Date data) throws ExcecaoNegocioGenerica {
        try {
            String sql = " SELECT saldo.* FROM saldoGrupoMaterial saldo "
                + " WHERE trunc(saldo.datasaldo) <= to_date(:dataSaldo, 'dd/mm/yyyy') "
                + " AND saldo.grupomaterial_id = :grupoMaterial"
                + " AND saldo.unidadeorganizacional_id = :unidade "
                + " AND saldo.tipoestoque = :tipoEstoque "
                + " AND saldo.naturezatipogrupomaterial = :natureza "
                + " ORDER BY saldo.datasaldo DESC ";

            Query q = em.createNativeQuery(sql, SaldoGrupoMaterial.class);

            q.setParameter("grupoMaterial", grupoMaterial.getId());
            q.setParameter("unidade", unidadeOrganizacional.getId());
            q.setParameter("tipoEstoque", tipoEstoque.name());
            q.setParameter("natureza", natureza.name());
            q.setParameter("dataSaldo", DataUtil.getDataFormatada(data));
            q.setMaxResults(1);
            List<SaldoGrupoMaterial> resultado = q.getResultList();
            if (resultado.isEmpty()) {
                SaldoGrupoMaterial saldoGrupo = new SaldoGrupoMaterial();
                saldoGrupo.setDataSaldo(data);
                saldoGrupo.setGrupoMaterial(grupoMaterial);
                saldoGrupo.setTipoEstoque(tipoEstoque);
                saldoGrupo.setUnidadeOrganizacional(unidadeOrganizacional);
                saldoGrupo.setNaturezaTipoGrupoMaterial(natureza);
                saldoGrupo.setCredito(BigDecimal.ZERO);
                saldoGrupo.setDebito(BigDecimal.ZERO);
                return saldoGrupo;
            } else {
                return resultado.get(0);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public List<SaldoGrupoMaterial> recuperaSaldosPosterioresAData(MovimentoGrupoMaterial movimento) {

        String sql = " select s.* from SaldoGrupoMaterial s "
            + " where trunc(s.dataSaldo) > to_date(:dataSaldo, 'dd/mm/yyyy') "
            + " and s.unidadeOrganizacional_id = :unidade "
            + " and s.grupoMaterial_id = :grupo "
            + " and s.naturezaTipoGrupoMaterial = :natureza "
            + " and s.tipoEstoque = :tipoEstoque ";

        Query q = em.createNativeQuery(sql, SaldoGrupoMaterial.class);
        q.setParameter("grupo", movimento.getGrupoMaterial().getId());
        q.setParameter("unidade", movimento.getUnidadeOrganizacional().getId());
        q.setParameter("tipoEstoque", movimento.getTipoEstoque().name());
        q.setParameter("natureza", movimento.getNaturezaTipoGrupoMaterial().name());
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(movimento.getDataMovimento()));
        List<SaldoGrupoMaterial> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    public BigDecimal buscarUltimoSaldo(GrupoMaterial grupo, UnidadeOrganizacional unidade, TipoEstoque tipoEstoque, NaturezaTipoGrupoMaterial natureza, Date dataReferencia) {
        String sql = " " +
            " select coalesce(sum(s.credito - s.debito), 0) as valor " +
            "from saldogrupomaterial s " +
            "         inner join (select max(saldo.datasaldo) as data, " +
            "                            saldo.unidadeorganizacional_id, " +
            "                            saldo.grupomaterial_id, " +
            "                            saldo.naturezatipogrupomaterial, " +
            "                            saldo.tipoestoque " +
            "                     from saldogrupomaterial saldo " +
            "                     where trunc(saldo.datasaldo) <= to_date(:dataReferencia, 'dd/mm/yyyy') " +
            "                     group by saldo.unidadeorganizacional_id, saldo.grupomaterial_id, saldo.naturezatipogrupomaterial, " +
            "                              saldo.tipoestoque " +
            ") maiorsaldo on s.datasaldo = maiorsaldo.data " +
            "    and s.unidadeorganizacional_id = maiorsaldo.unidadeorganizacional_id " +
            "    and s.grupomaterial_id = maiorsaldo.grupomaterial_id " +
            "    and s.naturezatipogrupomaterial = maiorsaldo.naturezatipogrupomaterial " +
            "    and s.tipoestoque = maiorsaldo.tipoestoque " +
            "where s.unidadeorganizacional_id = :idUnidade " +
            "  and s.grupomaterial_id = :idGrupo " +
            "  and s.naturezatipogrupomaterial = :natureza " +
            "  and s.tipoestoque = :tipoEstoque ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idGrupo", grupo.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("tipoEstoque", tipoEstoque.name());
        q.setParameter("natureza", natureza.name());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public void excluirSaldoPorPeriodo(Date dataInidical, Date dataFinal, GrupoMaterial grupoMaterial) {

        String sql = " delete saldogrupomaterial s " +
            " where trunc(s.datasaldo) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
        if (grupoMaterial != null) {
            sql += " and s.grupomaterial_id = :idGrupoMaterial ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInidical));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (grupoMaterial != null) {
            q.setParameter("idGrupoMaterial", grupoMaterial.getId());
        }
        q.executeUpdate();
    }

    public void excluirMovimentoPorPeriodo(Date dataInidical, Date dataFinal, GrupoMaterial grupoMaterial) {

        String sql = " delete movimentogrupomaterial m " +
            " where trunc(m.datamovimento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
        if (grupoMaterial != null) {
            sql += " and m.grupomaterial_id = :idGrupoMaterial ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInidical));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (grupoMaterial != null) {
            q.setParameter("idGrupoMaterial", grupoMaterial.getId());
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
