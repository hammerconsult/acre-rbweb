package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Buzatto on 02/09/2015.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Concursos")
@Etiqueta(value = "Homologação Concurso")
public class HomologacaoConcurso extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @OneToOne
    @Etiqueta(value = "Concurso")
    private Concurso concurso;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data de Homologação")
    private Date dataHomologacao;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário Sistema")
    private UsuarioSistema usuarioSistema;

    public HomologacaoConcurso() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Date getDataHomologacao() {
        return dataHomologacao;
    }

    public void setDataHomologacao(Date dataHomologacao) {
        this.dataHomologacao = dataHomologacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public String toString() {
        return concurso.toStringParaAprovacao() + " - Homologado em " + DataUtil.getDataFormatada(dataHomologacao);
    }
}
