package br.com.webpublico.entidadesauxiliares;

/**
 * Created by William on 17/06/2019.
 */
public class RelatorioServidoresQueEntraramOuSairamDaFPComMotivo {
    private Long idVinculo;
    private String matricula;
    private String numero;
    private String nome;
    private String situacaoFuncional;
    private String motivo;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(String situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }
}
