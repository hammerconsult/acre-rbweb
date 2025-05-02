package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class FichaFinanceiraFPVO {

    private BigDecimal ficha;
    private Integer mes;
    private Integer ano;
    private String tipoFolha;
    private String descricaoMes;
    private String versao;
    private List<FichaFinanceiraFPServidorAndAposentadoVO> fichasServidores;
    private List<FichaFinanceiraFPServidorAndAposentadoVO> fichasAposentados;
    private List<FichaFinanceiraFPPensionistaVO> fichasPensionistas;
    private List<FichaFinanceiraFPBeneficiarioVO> fichasBeneficiarios;
    private List<FichaFinanceiraFPItensVO> itensFicha;

    public FichaFinanceiraFPVO() {
        fichasServidores = Lists.newArrayList();
        fichasAposentados = Lists.newArrayList();
        fichasPensionistas = Lists.newArrayList();
        fichasBeneficiarios = Lists.newArrayList();
        itensFicha = Lists.newArrayList();
    }

    public BigDecimal getFicha() {
        return ficha;
    }

    public void setFicha(BigDecimal ficha) {
        this.ficha = ficha;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(String tipoFolha) {
        this.tipoFolha = tipoFolha;
    }

    public String getDescricaoMes() {
        return descricaoMes;
    }

    public void setDescricaoMes(String descricaoMes) {
        this.descricaoMes = descricaoMes;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public List<FichaFinanceiraFPServidorAndAposentadoVO> getFichasServidores() {
        return fichasServidores;
    }

    public void setFichasServidores(List<FichaFinanceiraFPServidorAndAposentadoVO> fichasServidores) {
        this.fichasServidores = fichasServidores;
    }

    public List<FichaFinanceiraFPServidorAndAposentadoVO> getFichasAposentados() {
        return fichasAposentados;
    }

    public void setFichasAposentados(List<FichaFinanceiraFPServidorAndAposentadoVO> fichasAposentados) {
        this.fichasAposentados = fichasAposentados;
    }

    public List<FichaFinanceiraFPPensionistaVO> getFichasPensionistas() {
        return fichasPensionistas;
    }

    public void setFichasPensionistas(List<FichaFinanceiraFPPensionistaVO> fichasPensionistas) {
        this.fichasPensionistas = fichasPensionistas;
    }

    public List<FichaFinanceiraFPBeneficiarioVO> getFichasBeneficiarios() {
        return fichasBeneficiarios;
    }

    public void setFichasBeneficiarios(List<FichaFinanceiraFPBeneficiarioVO> fichasBeneficiarios) {
        this.fichasBeneficiarios = fichasBeneficiarios;
    }

    public List<FichaFinanceiraFPItensVO> getItensFicha() {
        return itensFicha;
    }

    public void setItensFicha(List<FichaFinanceiraFPItensVO> itensFicha) {
        this.itensFicha = itensFicha;
    }
}
