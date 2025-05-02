package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by venom on 13/11/14.
 */
@Entity
@Audited
@Etiqueta("Classificação Concurso")
public class ClassificacaoConcurso extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Etiqueta("Cargo")
    @Pesquisavel
    @Tabelavel
    private CargoConcurso cargo;
    @OneToMany(mappedBy = "classificacaoConcurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassificacaoInscricao> inscricoes;

    public ClassificacaoConcurso() {
        inscricoes = new ArrayList<>();
    }

    public List<ClassificacaoInscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(List<ClassificacaoInscricao> inscricoes) {
        this.inscricoes = inscricoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CargoConcurso getCargo() {
        return cargo;
    }

    public void setCargo(CargoConcurso cargo) {
        this.cargo = cargo;
    }

    public List<ClassificacaoInscricao> getInscricoesOrdenadas() {
        if (inscricoes == null) {
            return null;
        }
        Collections.sort(inscricoes);
        return inscricoes;
    }

    public ClassificacaoInscricao getClassificado(InscricaoConcurso inscricaoConcurso) {
        for (ClassificacaoInscricao classificacaoInscricao : inscricoes) {
            if (classificacaoInscricao.getInscricaoConcurso().equals(inscricaoConcurso)) {
                return classificacaoInscricao;
            }
        }
        return null;
    }

    public boolean possuiOutroCandidatoEmpatado(ClassificacaoInscricao inscricaoParametro) {
        for (ClassificacaoInscricao inscricaoDaVez : inscricoes) {
//            if (!inscricaoDaVez.equals(inscricaoParametro) && inscricaoDaVez.getMedia().compareTo(inscricaoParametro.getMedia()) == 0) {
            if (!inscricaoDaVez.equals(inscricaoParametro) && inscricaoDaVez.getPontuacao().compareTo(inscricaoParametro.getPontuacao()) == 0) {
                return true;
            }
//            }
        }
        return false;
    }

    public boolean possuiCandidatosEmpatados() {
        for (ClassificacaoInscricao inscricaoParada : inscricoes) {
            for (ClassificacaoInscricao inscricaoCorrente : inscricoes) {
                if (inscricaoParada.getPontuacao().compareTo(inscricaoCorrente.getPontuacao()) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public Comparator getOrdenadorPorMediaAndPontuacao() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
//                o1 = (ClassificacaoInscricao) o1;
//                o2 = (ClassificacaoInscricao) o2;

                int retorno = ((ClassificacaoInscricao) o2).getStatus().getOrdemApresentacao().compareTo(((ClassificacaoInscricao) o1).getStatus().getOrdemApresentacao());
                if (retorno == 0) {
                    retorno = ((ClassificacaoInscricao) o2).getMedia().compareTo(((ClassificacaoInscricao) o1).getMedia());
                    if (retorno == 0) {
                        retorno = ((ClassificacaoInscricao) o2).getPontuacao().compareTo(((ClassificacaoInscricao) o1).getPontuacao());
                    }
                }
                return retorno;
            }
        };
    }
}
