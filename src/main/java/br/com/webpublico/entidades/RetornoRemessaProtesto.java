package br.com.webpublico.entidades;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RetornoRemessaProtesto extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataArquivo;

    private String xmlRetorno;
    private String nomeArquivo;

    public RetornoRemessaProtesto() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataArquivo() {
        return dataArquivo;
    }

    public void setDataArquivo(Date dataArquivo) {
        this.dataArquivo = dataArquivo;
    }

    public String getXmlRetorno() {
        return xmlRetorno;
    }

    public void setXmlRetorno(String xmlRetorno) {
        this.xmlRetorno = xmlRetorno;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}
