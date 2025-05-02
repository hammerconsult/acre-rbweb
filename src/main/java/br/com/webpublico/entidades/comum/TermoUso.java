package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Sistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class TermoUso extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @Etiqueta("Sistema")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private Sistema sistema;

    @Temporal(TemporalType.TIMESTAMP)
    private Date inicioVigencia;

    private String conteudo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
