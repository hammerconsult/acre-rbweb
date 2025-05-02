package br.com.webpublico.entidades;

import br.com.webpublico.enums.PublicoAlvoPreferencial;
import br.com.webpublico.enums.TipoRefeicao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.HtmlToText;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Refeição")
public class Refeicao extends SuperEntidade {

    @Transient
    private final Logger logger = LoggerFactory.getLogger(Refeicao.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("Número")
    private Long numero;

    @Etiqueta("Situação")
    private Boolean ativo;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;

    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Refeição")
    private TipoRefeicao tipoRefeicao;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Público Alvo Preferencial")
    private PublicoAlvoPreferencial publicoAlvoPreferencial;

    @Tabelavel
    @Length(maximo = 3000)
    @Obrigatorio
    @Etiqueta("Descrição do Principal")
    private String descricaoPrincipal;

    @Tabelavel
    @Length(maximo = 3000)
    @Etiqueta("Descrição do Acompanhamento")
    private String descricaoAcompanhamento;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Composição Nutricional")
    private ComposicaoNutricional composicaoNutricional;

    @Tabelavel
    @Length(maximo = 3000)
    @Etiqueta("Observação Refeição Especial")
    private String observacaoRefeicaoEspecial;

    @Tabelavel
    @Etiqueta("Técnica de Preparo")
    private String tecnicaPreparo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Arquivo arquivo;

    @OneToMany(mappedBy = "refeicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefeicaoMaterial> materiais;

    @OneToMany(mappedBy = "refeicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefeicaoEspecial> refeicoesEspeciais;


    public Refeicao() {
        materiais = new ArrayList<>();
        refeicoesEspeciais = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public TipoRefeicao getTipoRefeicao() {
        return tipoRefeicao;
    }

    public void setTipoRefeicao(TipoRefeicao tipoRefeicao) {
        this.tipoRefeicao = tipoRefeicao;
    }

    public PublicoAlvoPreferencial getPublicoAlvoPreferencial() {
        return publicoAlvoPreferencial;
    }

    public void setPublicoAlvoPreferencial(PublicoAlvoPreferencial publicoAlvoPreferencial) {
        this.publicoAlvoPreferencial = publicoAlvoPreferencial;
    }

    public String getDescricaoPrincipal() {
        return descricaoPrincipal;
    }

    public void setDescricaoPrincipal(String descricaoPrincipal) {
        this.descricaoPrincipal = descricaoPrincipal;
    }

    public String getDescricaoAcompanhamento() {
        return descricaoAcompanhamento;
    }

    public void setDescricaoAcompanhamento(String descricaoAcompanhamento) {
        this.descricaoAcompanhamento = descricaoAcompanhamento;
    }

    public ComposicaoNutricional getComposicaoNutricional() {
        return composicaoNutricional;
    }

    public void setComposicaoNutricional(ComposicaoNutricional composicaoNutricional) {
        this.composicaoNutricional = composicaoNutricional;
    }

    public String getObservacaoRefeicaoEspecial() {
        return observacaoRefeicaoEspecial;
    }

    public void setObservacaoRefeicaoEspecial(String observacaoRefeicaoEspecial) {
        this.observacaoRefeicaoEspecial = observacaoRefeicaoEspecial;
    }

    public String getTecnicaPreparo() {
        return tecnicaPreparo;
    }

    public void setTecnicaPreparo(String tecnicaPreparo) {
        this.tecnicaPreparo = tecnicaPreparo;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public List<RefeicaoMaterial> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<RefeicaoMaterial> materiais) {
        this.materiais = materiais;
    }

    public List<RefeicaoEspecial> getRefeicoesEspeciais() {
        return refeicoesEspeciais;
    }

    public void setRefeicoesEspeciais(List<RefeicaoEspecial> refeicoesEspeciais) {
        this.refeicoesEspeciais = refeicoesEspeciais;
    }

    public String getDescricaoSimplificada() {
        HtmlToText htmlToText = new HtmlToText();
        try {
            htmlToText.parse(toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return htmlToText.getText();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        String meio = " - ";
        if (numero != null) {
            sb.append("Nº ").append(this.numero).append(meio);
        }
        if (tipoRefeicao != null) {
            sb.append(this.tipoRefeicao.getDescricao()).append(meio);
        }
        if (descricaoPrincipal != null) {
            sb.append(this.descricaoPrincipal);
        }
        if (descricaoAcompanhamento != null) {
            sb.append(meio).append(this.descricaoAcompanhamento);
        }
        return sb.toString();
    }

}
