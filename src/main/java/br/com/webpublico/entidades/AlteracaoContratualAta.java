package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitações")
public class AlteracaoContratualAta extends SuperEntidade {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Alteração Contratual")
    private AlteracaoContratual alteracaoContratual;

    @ManyToOne
    @Etiqueta("Ata Registro de Preço")
    private AtaRegistroPreco ataRegistroPreco;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }
}
