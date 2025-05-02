package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.TipoPermissaoRBTrans;

/**
 * Created by Filipe
 * On 15, Maio, 2019,
 * At 14:39
 */
public class ConsultaDAMRBTrans {

    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    private Exercicio anoDebito;
    private String cmc;
    private String nome;
    private Integer inicioDigitoFinalPermissao;
    private Integer fimDigitoFinalPermissao;
    private String nomePermissionario;


    public ConsultaDAMRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans, Exercicio anoDebito, String cmc, String nome, Integer inicioDigitoFinalPermissao, Integer fimDigitoFinalPermissao, String nomePermissionario) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
        this.anoDebito = anoDebito;
        this.cmc = cmc;
        this.nome = nome;
        this.inicioDigitoFinalPermissao = inicioDigitoFinalPermissao;
        this.fimDigitoFinalPermissao = fimDigitoFinalPermissao;
        this.nomePermissionario = nomePermissionario;
    }

    public Exercicio getAnoDebito() {
        return anoDebito;
    }

    public void setAnoDebito(Exercicio anoDebito) {
        this.anoDebito = anoDebito;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public String getCmc() {
        return cmc;
    }

    public void setCmc(String cmc) {
        this.cmc = cmc;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getInicioDigitoFinalPermissao() {
        return inicioDigitoFinalPermissao;
    }

    public void setInicioDigitoFinalPermissao(Integer inicioDigitoFinalPermissao) {
        this.inicioDigitoFinalPermissao = inicioDigitoFinalPermissao;
    }

    public Integer getFimDigitoFinalPermissao() {
        return fimDigitoFinalPermissao;
    }

    public void setFimDigitoFinalPermissao(Integer fimDigitoFinalPermissao) {
        this.fimDigitoFinalPermissao = fimDigitoFinalPermissao;
    }

    public String getNomePermissionario() {
        return nomePermissionario;
    }

    public void setNomePermissionario(String nomePermissionario) {
        this.nomePermissionario = nomePermissionario;
    }
}
