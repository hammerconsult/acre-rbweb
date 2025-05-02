package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class VoReducaoValorBemContabil {

    private BigDecimal reducao;
    private BigDecimal bensMoveis;
    private BigDecimal grupoBem;
    private BigDecimal contabil;
    private BigDecimal diferenca;
    private String grupo;

    public BigDecimal getReducao() {
        return reducao;
    }

    public void setReducao(BigDecimal reducao) {
        this.reducao = reducao;
    }

    public BigDecimal getBensMoveis() {
        return bensMoveis;
    }

    public void setBensMoveis(BigDecimal bensMoveis) {
        this.bensMoveis = bensMoveis;
    }

    public BigDecimal getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(BigDecimal grupoBem) {
        this.grupoBem = grupoBem;
    }

    public BigDecimal getContabil() {
        return contabil;
    }

    public void setContabil(BigDecimal contabil) {
        this.contabil = contabil;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public boolean hasDiferenca() {
        return diferenca != null && diferenca.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean hasDiferencaBensMoveis() {
        return bensMoveis != null && reducao.subtract(bensMoveis).compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean hasDiferencaGrupoBem() {
        return grupoBem !=null && reducao.subtract(grupoBem).compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean hasDiferencaContabil() {
        return contabil !=null && reducao.subtract(contabil).compareTo(BigDecimal.ZERO) != 0;
    }

    public static List<VoReducaoValorBemContabil> preencherArrayObjeto(List<Object[]> lista) {
        List<VoReducaoValorBemContabil> reducoes = Lists.newArrayList();
        for (Object[] obj : lista) {
            VoReducaoValorBemContabil reducao = new VoReducaoValorBemContabil();
            reducao.setReducao(((BigDecimal) obj[0]));
            reducao.setBensMoveis(((BigDecimal) obj[1]));
            reducao.setGrupoBem(((BigDecimal) obj[2]));
            reducao.setContabil(((BigDecimal) obj[3]));
            reducao.setDiferenca(reducao.getReducao().subtract(reducao.getBensMoveis()));
            reducao.setGrupo((String) obj[4]);
            reducoes.add(reducao);
        }
        return reducoes;
    }
}
