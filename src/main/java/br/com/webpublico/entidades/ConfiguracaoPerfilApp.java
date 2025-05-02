package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Audited
public class ConfiguracaoPerfilApp extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String mensagemRodape;
    private String nomeBaseProducao;
    private String mensagemRelatorios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagemRodape() {
        return mensagemRodape;
    }

    public void setMensagemRodape(String mensagemRodape) {
        this.mensagemRodape = mensagemRodape;
    }

    public String getNomeBaseProducao() {
        return nomeBaseProducao;
    }

    public void setNomeBaseProducao(String nomeBaseProducao) {
        this.nomeBaseProducao = nomeBaseProducao;
    }

    public String getMensagemRelatorios() {
        return mensagemRelatorios;
    }

    public void setMensagemRelatorios(String mensagemRelatorios) {
        this.mensagemRelatorios = mensagemRelatorios;
    }
}
