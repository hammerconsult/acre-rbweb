/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Ambiente;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Configuração de Storage")
public class ConfiguracaoStorage extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String host;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String accessKey;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String secretKey;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String bucket;
    @Enumerated(value = EnumType.STRING)
    @Obrigatorio
    private Ambiente ambiente;

    private Boolean excluirPartes;
    private Boolean sincronizaAutomatico;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
    }

    public Boolean getExcluirPartes() {
        return excluirPartes;
    }

    public void setExcluirPartes(Boolean excluirPartes) {
        this.excluirPartes = excluirPartes;
    }

    public Boolean getSincronizaAutomatico() {
        return sincronizaAutomatico;
    }

    public void setSincronizaAutomatico(Boolean sincronizaAutomatico) {
        this.sincronizaAutomatico = sincronizaAutomatico;
    }
}
