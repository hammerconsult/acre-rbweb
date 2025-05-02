package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ItemDAM;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.DamAgrupado;
import br.com.webpublico.entidadesauxiliares.DamAgrupadoLancamentos;
import br.com.webpublico.entidadesauxiliares.DamLancamentos;
import br.com.webpublico.negocios.DAMFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.CarneUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 14/03/2017.
 */
@Stateless
public class RelatorioDamFacade implements Serializable {

    private Logger logger = LoggerFactory.getLogger(RelatorioDamFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RelatorioDamFacade() {
    }

    public DamAgrupado buscarDamAgrupado(DAM dam) {
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT dam.vencimento, ");
        sql.append("    dam.numerodam, ");
        sql.append("    dam.valororiginal, ");
        sql.append("    dam.correcaomonetaria, ");
        sql.append("    dam.juros, ");
        sql.append("    dam.multa, ");
        sql.append("    dam.honorarios, ");
        sql.append("    dam.desconto, ");
        sql.append("    ((dam.valororiginal + dam.correcaomonetaria + dam.juros + dam.honorarios + dam.multa) - dam.desconto) AS valor, ");
        sql.append("    dam.codigobarras  AS codigobarrasdigitos, ");
        sql.append("    (SUBSTR(dam.codigoBarras, 0, 11) ");
        sql.append("    || SUBSTR(dam.codigoBarras, 15, 11) ");
        sql.append("    || SUBSTR(dam.codigoBarras, 29, 11) ");
        sql.append("    || SUBSTR(dam.codigoBarras, 43, 11)) AS codigobarras, ");
        sql.append("    configuracaodam.instrucaolinha1, ");
        sql.append("    configuracaodam.instrucaolinha2, ");
        sql.append("    configuracaodam.instrucaolinha3, ");
        sql.append("    configuracaodam.instrucaolinha4, ");
        sql.append("    configuracaodam.instrucaolinha5, ");
        sql.append("    COALESCE(pf.nome, pj.razaosocial) AS contribuinte, ");
        sql.append("    REPLACE(REPLACE(REPLACE(COALESCE(pf.cpf, pj.cnpj, ''), '.', ''),'-', ''), '/', '') AS cpf_cnpj, ");
        sql.append("    (select vwenderecopessoa.logradouro || ' n°: ' || vwenderecopessoa.numero || ' Bairro: ' || vwenderecopessoa.bairro ");
        sql.append("     || ' Complemento: ' || vwenderecopessoa.complemento || ' Município: ' || vwenderecopessoa.localidade || ' UF: ' || vwenderecopessoa.uf ");
        sql.append("     from vwenderecopessoa ");
        sql.append("     where vwenderecopessoa.pessoa_id = pessoa.id and rownum = 1) AS endereco, ");
        sql.append("    (select count(hid.id) from historicoimpressaodam hid where hid.dam_id = dam.ID and hid.parcela_id = ");
        sql.append("     (select max(hid2.parcela_id) from historicoimpressaodam hid2 where hid2.dam_id = hid.dam_id)) as numeroemissao, ");
        sql.append("    dam.qrcodepix as qrcodepix ");
        sql.append("  FROM dam ");
        sql.append("  INNER JOIN configuracaodam ON configuracaodam.id = ");
        sql.append("    (SELECT max(divida.configuracaodam_id) FROM itemdam ");
        sql.append("    INNER JOIN parcelavalordivida parcela ON parcela.id = itemdam.parcela_id ");
        sql.append("    INNER JOIN valordivida ON valordivida.id = parcela.valordivida_id ");
        sql.append("    INNER JOIN divida ON divida.id = valordivida.divida_id ");
        sql.append("    WHERE itemdam.dam_id = dam.id) ");
        sql.append("  INNER JOIN pessoa ON pessoa.id = ");
        sql.append("    (SELECT MAX(calculopessoa.pessoa_id) FROM itemdam ");
        sql.append("    INNER JOIN parcelavalordivida parcela ON parcela.id = itemdam.parcela_id ");
        sql.append("    INNER JOIN valordivida ON valordivida.id = parcela.valordivida_id ");
        sql.append("    INNER JOIN calculo ON calculo.id = valordivida.calculo_id ");
        sql.append("    INNER JOIN calculopessoa ON calculopessoa.calculo_id = calculo.id ");
        sql.append("    WHERE itemdam.dam_id = dam.id) ");
        sql.append("  LEFT JOIN pessoafisica pf ON pf.id = pessoa.id ");
        sql.append("  LEFT JOIN pessoajuridica pj ON pj.id = pessoa.id ");
        sql.append("  where dam.id = :DAM_ID ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("DAM_ID", dam.getId());
        DamAgrupado damAgrupado = new DamAgrupado();
        try {
            List<Object[]> resultados = q.getResultList();
            if (!resultados.isEmpty()) {
                for (Object[] obj : resultados) {
                    damAgrupado.setVencimento((Date) obj[0]);
                    damAgrupado.setNumeroDam((String) obj[1]);
                    damAgrupado.setValorOriginal((BigDecimal) obj[2]);
                    damAgrupado.setCorrecaoMonetaria((BigDecimal) obj[3]);
                    damAgrupado.setJuros((BigDecimal) obj[4]);
                    damAgrupado.setMulta((BigDecimal) obj[5]);
                    damAgrupado.setHonorarios((BigDecimal) obj[6]);
                    damAgrupado.setDesconto((BigDecimal) obj[7]);
                    damAgrupado.setValor((BigDecimal) obj[8]);
                    damAgrupado.setCodigoBarrasDigitos((String) obj[9]);
                    damAgrupado.setCodigoBarras((String) obj[10]);
                    damAgrupado.setInstrucaoLinha1((String) obj[11]);
                    damAgrupado.setInstrucaoLinha2((String) obj[12]);
                    damAgrupado.setInstrucaoLinha3((String) obj[13]);
                    damAgrupado.setInstrucaoLinha4((String) obj[14]);
                    damAgrupado.setInstrucaoLinha5((String) obj[15]);
                    damAgrupado.setContribuinte(obj[16] != null ? (String) obj[16] : "");
                    damAgrupado.setCpfCnpj(obj[17] != null ? (String) obj[17] : "");
                    damAgrupado.setEndereco(obj[18] != null ? (String) obj[18] : "");
                    damAgrupado.setNumeroEmissao((BigDecimal) obj[19]);
                    damAgrupado.setQrCodePix(obj[20] != null ? (String) obj[20] : null);
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao buscar os dados do DAM agrupado:", ex);
        }
        return damAgrupado;
    }

    public String buscarEnderecoDaPessoa(Pessoa pessoa) {
        StringBuilder sql = new StringBuilder();
        sql.append("select vwenderecopessoa.logradouro || ' n°: ' || vwenderecopessoa.numero || ' Bairro: ' || vwenderecopessoa.bairro ");
        sql.append("  || ' Complemento: ' || vwenderecopessoa.complemento || ' Município: ' || vwenderecopessoa.localidade || ' UF: ' || vwenderecopessoa.uf ");
        sql.append("     from vwenderecopessoa ");
        sql.append("   where vwenderecopessoa.pessoa_id = :pessoa_id and rownum = 1");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("pessoa_id", pessoa.getId());
        List<String[]> resultados = q.getResultList();
        if (!resultados.isEmpty()) {
            return String.valueOf(resultados.get(0));
        }
        return "";
    }

    public Calculo.TipoCalculo buscarTipoCalculoDoItemDam(ItemDAM itemDAM) {
        String sql = "select cal.tipoCalculo from itemDam idam " +
            "inner join ParcelaValorDivida pvd on pvd.id = idam.parcela_id " +
            "inner join ValorDivida vd on vd.id = pvd.valorDivida_id " +
            "inner join Calculo cal on cal.id = vd.calculo_id " +
            "where idam.id = :idItemDam";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemDam", itemDAM.getId());
        q.setMaxResults(1);
        List resultList = q.getResultList();
        return resultList != null && !resultList.isEmpty() ? Calculo.TipoCalculo.valueOf((String) resultList.get(0)) : null;
    }

    public DamAgrupadoLancamentos buscarLancamentoDoDam(ResultadoParcela parcela) {
        String sql = " select codigo, descricao, valor " +
            " from (select tributo.codigo    as codigo, " +
            "              tributo.descricao as descricao, " +
            "              sum(ipvd.valor)   as valor " +
            "       from itemparcelavalordivida ipvd " +
            "       inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id " +
            "       inner join tributo on tributo.id = ivd.tributo_id " +
            "       inner join parcelavalordivida pvd on pvd.id = ipvd.parcelavalordivida_id " +
            "       inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "       inner join calculo on calculo.id = vd.calculo_id " +
            "       where calculo.id = :idCalculo and pvd.id = :idParcela " +
            "       group by tributo.codigo, tributo.descricao) " +
            " order by codigo, descricao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcela.getIdParcela());
        q.setParameter("idCalculo", parcela.getIdCalculo());

        List<Object[]> resultados = q.getResultList();

        List<DamLancamentos> lancamentos = Lists.newArrayList();
        if (resultados != null && !resultados.isEmpty()) {

            for (Object[] obj : resultados) {
                DamLancamentos lancamento = new DamLancamentos();
                lancamento.setCodigoTributo(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
                lancamento.setDescricaoTributo(obj[1] != null ? (String) obj[1] : "");
                lancamento.setValorTributo(obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);

                lancamentos.add(lancamento);
            }
        }
        DamAgrupadoLancamentos lancamentoDamAgrupado = new DamAgrupadoLancamentos();
        preencherParcelaDamAgrupado(lancamentoDamAgrupado, parcela);
        lancamentoDamAgrupado.setLancamentos(lancamentos);

        return lancamentoDamAgrupado;
    }

    private void preencherParcelaDamAgrupado(DamAgrupadoLancamentos lancamentoDamAgrupado, ResultadoParcela parcela) {
        lancamentoDamAgrupado.setInscricaoCadastral(parcela.getCadastro());
        lancamentoDamAgrupado.setEnderecoCadastro(parcela.getEnderecoCadastro());
        lancamentoDamAgrupado.setDivida(parcela.getDivida());
        lancamentoDamAgrupado.setReferencia(parcela.getReferencia());
        lancamentoDamAgrupado.setParcela(parcela.getParcela());
        lancamentoDamAgrupado.setSd(parcela.getSd());
        lancamentoDamAgrupado.setVencimento(parcela.getVencimento());
        lancamentoDamAgrupado.setValorImposto(parcela.getValorImposto());
        lancamentoDamAgrupado.setValorTaxa(parcela.getValorTaxa());
        lancamentoDamAgrupado.setValorJuros(parcela.getValorJuros());
        lancamentoDamAgrupado.setValorMulta(parcela.getValorMulta());
        lancamentoDamAgrupado.setValorCorrecao(parcela.getValorCorrecao());
        lancamentoDamAgrupado.setValorHonorarios(parcela.getValorHonorarios());
        lancamentoDamAgrupado.setValorDesconto(parcela.getValorDesconto());
    }

    public void carregarDadosDamAgrupadoVazio(DamAgrupado damAgrupado, DAM dam) {
        logger.error("Deu Algum problem com os dados do DAM");
        damAgrupado.setNumeroDam(dam.getNumeroDAM());
        damAgrupado.setVencimento(dam.getVencimento());
        damAgrupado.setValorOriginal(dam.getValorOriginal());
        damAgrupado.setCorrecaoMonetaria(dam.getCorrecaoMonetaria());
        damAgrupado.setJuros(dam.getJuros());
        damAgrupado.setMulta(dam.getMulta());
        damAgrupado.setHonorarios(dam.getHonorarios());
        damAgrupado.setDesconto(dam.getDesconto());
        damAgrupado.setValor(dam.getValorTotal());
        damAgrupado.setCodigoBarrasDigitos(dam.getCodigoBarras());
        damAgrupado.setCodigoBarras(CarneUtil.montarCodigoBarrasDaLinhaDigitavel(dam.getCodigoBarras()));
        damAgrupado.setNumeroEmissao(new BigDecimal(1));
        damAgrupado.setQrCodePix(dam.getQrCodePIX());
    }
}
