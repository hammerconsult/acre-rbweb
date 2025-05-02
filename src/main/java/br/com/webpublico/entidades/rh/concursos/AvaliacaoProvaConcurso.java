package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Created by venom on 13/11/14.
 */
@Entity
@Audited
@Etiqueta("Avaliação de Provas")
public class AvaliacaoProvaConcurso extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Prova")
    @Pesquisavel
    @Tabelavel
    private ProvaConcurso prova;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Concurso")
    @Pesquisavel
    @Tabelavel
    private Concurso concurso; // Facilitar nas consultas
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Usuário Responsável")
    @Pesquisavel
    @Tabelavel
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Inicio Recurso")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date inicioRecurso;
    @Etiqueta("Fim Recurso")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date fimRecurso;
    @Etiqueta("Ordem")
    private Integer ordem;
    @Etiqueta("Notas")
    @OneToMany(mappedBy = "avaliacaoProvaConcurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotaCandidatoConcurso> notas;

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ProvaConcurso getProva() {
        return prova;
    }

    public void setProva(ProvaConcurso prova) {
        this.prova = prova;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioRecurso() {
        return inicioRecurso;
    }

    public void setInicioRecurso(Date inicioRecurso) {
        this.inicioRecurso = inicioRecurso;
    }

    public Date getFimRecurso() {
        return fimRecurso;
    }

    public void setFimRecurso(Date fimRecurso) {
        this.fimRecurso = fimRecurso;
    }

    public List<NotaCandidatoConcurso> getNotas() {
        return notas;
    }

    public void setNotas(List<NotaCandidatoConcurso> notas) {
        this.notas = notas;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public boolean jaPossuiCandidatoAdicionado(InscricaoConcurso i) {
        if (notas == null || notas.isEmpty()) {
            return false;
        }

        for (NotaCandidatoConcurso nota : notas) {
            if (nota.getInscricao().equals(i)) {
                return true;
            }
        }

        return false;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public boolean temNotaLancadaParaCandidato(InscricaoConcurso inscricaoConcurso) {
        for (NotaCandidatoConcurso nota : this.notas) {
            if (nota.getInscricao().equals(inscricaoConcurso)) {
                if (nota.temNovaNota()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean temNotaLancadaParaTodosCandidatos() {
        for (NotaCandidatoConcurso nota : this.notas) {
            if (!nota.temNovaNota()) {
                return false;
            }
        }
        return true;
    }

    public List<NotaCandidatoConcurso> getNotasOrdenadasPeloNumeroInscricaoCandidato() {
        if (notas == null) {
            return new ArrayList<>();
        }
        Collections.sort(notas, new Comparator<NotaCandidatoConcurso>() {
            @Override
            public int compare(NotaCandidatoConcurso o1, NotaCandidatoConcurso o2) {
                return o1.getInscricao().getNumero().compareTo(o2.getInscricao().getNumero());
            }
        });
        return notas;
    }
}
