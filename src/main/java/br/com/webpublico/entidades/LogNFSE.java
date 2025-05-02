package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
@Deprecated /*Utilizar a classe de entidade LogGeralNfse*/
public class LogNFSE extends SuperEntidade implements Serializable, Comparable<LogNFSE> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String conteudoEnviado;
    private String conteudoRetorno;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvio;
    @ManyToOne
    private CalculoNfse calculoNfse;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudoEnviado() {
        return conteudoEnviado;
    }

    public void setConteudoEnviado(String conteudoEnviado) {
        this.conteudoEnviado = conteudoEnviado;
    }

    public String getConteudoRetorno() {
        return conteudoRetorno;
    }

    public void setConteudoRetorno(String conteudoRetorno) {
        this.conteudoRetorno = conteudoRetorno;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public CalculoNfse getCalculoNfse() {
        return calculoNfse;
    }

    public void setCalculoNfse(CalculoNfse calculoNfse) {
        this.calculoNfse = calculoNfse;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    @Override
    public int compareTo(LogNFSE o) {
        return o.getDataEnvio().compareTo(this.dataEnvio);
    }

    public static enum Tipo {
        ENTRADA("Entrada"), SAIDA("Saída");

        private String descricao;

        Tipo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
