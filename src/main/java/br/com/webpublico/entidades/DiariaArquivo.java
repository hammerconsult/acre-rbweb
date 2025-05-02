package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by renatoromanini on 30/06/16.
 */
@Etiqueta("Concessão de Diária")
@GrupoDiagrama(nome = "ReservaDeDotacao")
@Audited
@Entity
public class DiariaArquivo extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    private String descricao;

    @ManyToOne
    @Obrigatorio
    private PropostaConcessaoDiaria propostaConcessaoDiaria;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Obrigatorio
    private Arquivo arquivo;

    private Boolean despesaCusteadaTerceiro;

    public DiariaArquivo() {
        despesaCusteadaTerceiro = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Boolean getDespesaCusteadaTerceiro() {
        return despesaCusteadaTerceiro;
    }

    public void setDespesaCusteadaTerceiro(Boolean despesaCusteadaTerceiro) {
        this.despesaCusteadaTerceiro = despesaCusteadaTerceiro;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
