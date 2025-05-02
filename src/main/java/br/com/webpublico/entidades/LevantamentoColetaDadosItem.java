package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLevantamentoImovelColetaDados;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
@Table(name = "LEVANTAMENTOCOLETADADOSIT")
public class LevantamentoColetaDadosItem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Levantamento Bem Imóvel - Coleta de Dados")
    private LevantamentoColetaDados levantamentoColetaDados;

    @OneToOne(cascade = CascadeType.ALL)
    @Etiqueta("Levantamento Bem Imóvel")
    private LevantamentoBemImovel levantamentoBemImovel;

    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoLevantamentoImovelColetaDados situacao;

    public LevantamentoColetaDadosItem() {
        situacao = SituacaoLevantamentoImovelColetaDados.EM_COLETA;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LevantamentoColetaDados getLevantamentoColetaDados() {
        return levantamentoColetaDados;
    }

    public void setLevantamentoColetaDados(LevantamentoColetaDados levantamentoColetaDados) {
        this.levantamentoColetaDados = levantamentoColetaDados;
    }

    public LevantamentoBemImovel getLevantamentoBemImovel() {
        return levantamentoBemImovel;
    }

    public void setLevantamentoBemImovel(LevantamentoBemImovel levantamentoBemImovel) {
        this.levantamentoBemImovel = levantamentoBemImovel;
    }

    public SituacaoLevantamentoImovelColetaDados getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLevantamentoImovelColetaDados situacao) {
        this.situacao = situacao;
    }
}
