package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by venom on 12/11/14.
 */
@Entity
@Audited
@Etiqueta(value = "Etapa Concurso")
@GrupoDiagrama(nome = "Concursos")
public class EtapaConcurso extends SuperEntidade implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta(value = "Concurso")
    private Concurso concurso;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data")
    private Date data;



    @Temporal(TemporalType.TIMESTAMP)
    private Date criacao;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Tipo Etapa")
    private TipoEtapa tipoEtapa;

    public EtapaConcurso() {
        super();
    }

    public EtapaConcurso(Concurso concurso) {
        super();
        setConcurso(concurso);
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public TipoEtapa getTipoEtapa() {
        return tipoEtapa;
    }

    public void setTipoEtapa(TipoEtapa tipoEtapa) {
        this.tipoEtapa = tipoEtapa;
    }

    public Date getCriacao() {
        return criacao;
    }

    public void setCriacao(Date criacao) {
        this.criacao = criacao;
    }

    public boolean isTipo(TipoEtapa recurso) {
        return recurso.equals(this.tipoEtapa);
    }

    @Override
    public int compareTo(Object o) {
        return getCriacao().compareTo(((EtapaConcurso) o).getCriacao());
    }
}
