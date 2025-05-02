/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author camila
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Arquivo do Registro de Óbito")
public class ArquivoRegistroDeObito extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Registro de Óbito")
    @ManyToOne
    private RegistroDeObito registroDeObito;
    @ManyToOne
    private Arquivo arquivo;
    private String descricao;
    private Boolean certidaoObito;

    public ArquivoRegistroDeObito() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistroDeObito getRegistroDeObito() {
        return registroDeObito;
    }

    public void setRegistroDeObito(RegistroDeObito registroDeObito) {
        this.registroDeObito = registroDeObito;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getCertidaoObito() {
        return certidaoObito != null ? certidaoObito : Boolean.FALSE;
    }

    public void setCertidaoObito(Boolean certidaoObito) {
        this.certidaoObito = certidaoObito;
    }

    @Override
    public String toString() {
        return arquivo.getNome();
    }
}
