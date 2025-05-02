package br.com.webpublico.entidadesauxiliares.administrativo.relatorio.demonstrativocompras;

import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 9
 *9
 */
public class DemonstrativoMovimentacoesAlmoxarifado implements Serializable {

    private String codigoMaterial;
    private String descricaoMaterial;
    private String unidade;
    private BigDecimal saldoAnteriorQuantidade;
    private BigDecimal saldoAnteriorFinanceiro;
    private BigDecimal entradaQuantidade;
    private BigDecimal entradaFinanceiro;
    private BigDecimal saidaQuantidade;
    private BigDecimal saidaFinanceiro;
    private BigDecimal valorFinanceiroTotal;
    private String codigoOrgao;
    private String descricaoOrgao;
    private String codigoUnidade;
    private String descricaoUnidade;

    public DemonstrativoMovimentacoesAlmoxarifado() {
    }

    public static List<DemonstrativoMovimentacoesAlmoxarifado> preencherDados(List<Object[]> objetos, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional, ApresentacaoRelatorio apresentacaoRelatorio) {
        List<DemonstrativoMovimentacoesAlmoxarifado> toReturn = new ArrayList<>();
        for (Object[] obj : objetos) {
            DemonstrativoMovimentacoesAlmoxarifado movimento = new DemonstrativoMovimentacoesAlmoxarifado();
            movimento.setCodigoMaterial((String) obj[0]);
            movimento.setDescricaoMaterial((String) obj[1]);
            movimento.setUnidade((String) obj[2]);
            movimento.setSaldoAnteriorQuantidade((BigDecimal) obj[3]);
            movimento.setSaldoAnteriorFinanceiro((BigDecimal) obj[4]);
            movimento.setEntradaQuantidade((BigDecimal) obj[5]);
            movimento.setEntradaFinanceiro((BigDecimal) obj[6]);
            movimento.setSaidaQuantidade((BigDecimal) obj[7]);
            movimento.setSaidaFinanceiro((BigDecimal) obj[8]);
            if (!ApresentacaoRelatorio.CONSOLIDADO.equals(apresentacaoRelatorio)) {
                movimento.setCodigoOrgao((String) obj[9]);
                movimento.setDescricaoOrgao((String) obj[10]);
                if (ApresentacaoRelatorio.UNIDADE.equals(apresentacaoRelatorio)) {
                    movimento.setCodigoUnidade((String) obj[11]);
                    movimento.setDescricaoUnidade((String) obj[12]);
                }
            }
            toReturn.add(movimento);
        }
        return toReturn;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public void setCodigoMaterial(String codigoMaterial) {
        this.codigoMaterial = codigoMaterial;
    }

    public String getDescricaoMaterial() {
        return descricaoMaterial;
    }

    public void setDescricaoMaterial(String descricaoMaterial) {
        this.descricaoMaterial = descricaoMaterial;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getSaldoAnteriorQuantidade() {
        return saldoAnteriorQuantidade;
    }

    public void setSaldoAnteriorQuantidade(BigDecimal saldoAnteriorQuantidade) {
        this.saldoAnteriorQuantidade = saldoAnteriorQuantidade;
    }

    public BigDecimal getSaldoAnteriorFinanceiro() {
        return saldoAnteriorFinanceiro;
    }

    public void setSaldoAnteriorFinanceiro(BigDecimal saldoAnteriorFinanceiro) {
        this.saldoAnteriorFinanceiro = saldoAnteriorFinanceiro;
    }

    public BigDecimal getEntradaQuantidade() {
        return this.entradaQuantidade;
    }

    public void setEntradaQuantidade(BigDecimal entradaQuantidade) {
        this.entradaQuantidade = entradaQuantidade;
    }

    public BigDecimal getEntradaFinanceiro() {
        return entradaFinanceiro;
    }

    public void setEntradaFinanceiro(BigDecimal entradaFinanceiro) {
        this.entradaFinanceiro = entradaFinanceiro;
    }

    public BigDecimal getSaidaQuantidade() {
        return saidaQuantidade;
    }

    public void setSaidaQuantidade(BigDecimal saidaQuantidade) {
        this.saidaQuantidade = saidaQuantidade;
    }

    public BigDecimal getSaidaFinanceiro() {
        return saidaFinanceiro;
    }

    public void setSaidaFinanceiro(BigDecimal saidaFinanceiro) {
        this.saidaFinanceiro = saidaFinanceiro;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public BigDecimal getValorFinanceiroAtual() {
        return saldoAnteriorFinanceiro.add(entradaFinanceiro).subtract(saidaFinanceiro);
    }

    public BigDecimal getQuantidadeAtual() {
        return saldoAnteriorQuantidade.add(entradaQuantidade).subtract(saidaQuantidade);
    }

    public BigDecimal getVlUnitarioAtual() {
        try {
            return getValorFinanceiroAtual().divide(getQuantidadeAtual(), 9, BigDecimal.ROUND_UP);
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }


    public BigDecimal getSaidaVlUnitario() {
        try {
            return this.saidaFinanceiro.divide(this.saidaQuantidade, 9, BigDecimal.ROUND_UP);
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }


    public BigDecimal getEntradaVlUnitario() {
        try {
            return this.entradaFinanceiro.divide(this.entradaQuantidade, 9, BigDecimal.ROUND_UP);
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSaldoAnteriorVlUnitario() {
        try {
            return this.saldoAnteriorFinanceiro.divide(this.saldoAnteriorQuantidade, 9, BigDecimal.ROUND_UP);
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        }
    }

}
