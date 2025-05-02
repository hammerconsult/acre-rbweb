package br.com.webpublico.entidadesauxiliares.rh;

import org.omg.CORBA.PRIVATE_MEMBER;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author octavio
 */
public class RelatorioServidoresAtivosPorSecretariaVO {
    private String servidor;
    private String numero;
    private String matricula;
    private String codigoOrgao;
    private String orgao;
    private Date mesAdmissao;
    private Integer totalServidores;
    private BigDecimal totalBruto;
    private BigDecimal totalLiquido;
    private BigDecimal totalGeralBruto;
    private BigDecimal totalGeralLiquido;
    private Integer mesFolha;
    private Integer anoFolha;

    public RelatorioServidoresAtivosPorSecretariaVO() {
        this.totalBruto = BigDecimal.ZERO;
        this.totalLiquido = BigDecimal.ZERO;
        this.totalGeralBruto = BigDecimal.ZERO;
        this.totalGeralLiquido = BigDecimal.ZERO;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public Date getMesAdmissao() {
        return mesAdmissao;
    }

    public void setMesAdmissao(Date mesAdmissao) {
        this.mesAdmissao = mesAdmissao;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
    }

    public BigDecimal getTotalLiquido() {
        return totalLiquido;
    }

    public void setTotalLiquido(BigDecimal totalLiquido) {
        this.totalLiquido = totalLiquido;
    }

    public BigDecimal getTotalGeralBruto() {
        return totalGeralBruto;
    }

    public void setTotalGeralBruto(BigDecimal totalGeralBruto) {
        this.totalGeralBruto = totalGeralBruto;
    }

    public BigDecimal getTotalGeralLiquido() {
        return totalGeralLiquido;
    }

    public void setTotalGeralLiquido(BigDecimal totalGeralLiquido) {
        this.totalGeralLiquido = totalGeralLiquido;
    }

    public Integer getTotalServidores() {
        return totalServidores;
    }

    public void setTotalServidores(Integer totalServidores) {
        this.totalServidores = totalServidores;
    }

    public Integer getMesFolha() {
        return mesFolha;
    }

    public void setMesFolha(Integer mesFolha) {
        this.mesFolha = mesFolha;
    }

    public Integer getAnoFolha() {
        return anoFolha;
    }

    public void setAnoFolha(Integer anoFolha) {
        this.anoFolha = anoFolha;
    }
}
