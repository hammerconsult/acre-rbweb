package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by peixe on 22/09/2015.
 */
@Entity
@Audited
public class ErroEconsig extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String matricula;
    private Integer contrato;
    private String erro;
    private String verba;
    private String linha;
    private String codigoConsignataria;
    @ManyToOne
    private ArquivoEconsig arquivoEconsig;

    public ErroEconsig(String matricula, Integer contrato) {
        this.matricula = matricula;
        this.contrato = contrato;
    }

    public ErroEconsig(String matricula, Integer contrato, String erro, String verba, String codigoConsignataria, String linha) {
        this.matricula = matricula;
        this.contrato = contrato;
        this.erro = erro;
        this.verba = verba;
        this.linha = linha;
        this.codigoConsignataria = codigoConsignataria;
    }


    public ErroEconsig() {

    }

    public String getCodigoConsignataria() {
        return codigoConsignataria;
    }

    public void setCodigoConsignataria(String codigoConsignataria) {
        this.codigoConsignataria = codigoConsignataria;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Integer getContrato() {
        return contrato;
    }

    public void setContrato(Integer contrato) {
        this.contrato = contrato;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getVerba() {
        return verba;
    }

    public void setVerba(String verba) {
        this.verba = verba;
    }

    public ArquivoEconsig getArquivoEconsig() {
        return arquivoEconsig;
    }

    public void setArquivoEconsig(ArquivoEconsig arquivoEconsig) {
        this.arquivoEconsig = arquivoEconsig;
    }
}
