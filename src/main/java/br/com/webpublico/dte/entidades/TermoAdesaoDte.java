package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Etiqueta("Termo de Adesão - DTE")
@Entity
@Audited
public class TermoAdesaoDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Início Em")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date inicioEm;

    @Etiqueta("Conteúdo")
    private String conteudo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioEm() {
        return inicioEm;
    }

    public void setInicioEm(Date inicioEm) {
        this.inicioEm = inicioEm;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
