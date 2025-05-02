package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/11/14
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Patrimônio")
@Etiqueta("Ajuste de Perdas")
public class AvariaBem extends EventoBem {

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema responsavel;

    @Tabelavel
    @Etiqueta("Responsável")
    @Transient
    private String descricaoResponsavel;

    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;

    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Etiqueta("Valor da Avaria")
    @Monetario
    private BigDecimal valorAvaria;

    @OneToOne(mappedBy = "avariaBem", cascade = CascadeType.ALL, orphanRemoval = true)
    private EstornoAvariaBem estornoAvariaBem;

    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @Etiqueta("Unidade Administrativa")
    @ManyToOne
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Unidade Administrativa")
    @Tabelavel
    @Transient
    private String descricaoUnidade;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Transient
    @Etiqueta("Estornado")
    private Boolean estorno;

    public AvariaBem() {
        super(TipoEventoBem.AVARIABEM, TipoOperacao.CREDITO);
        estorno = Boolean.FALSE;
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
    }

    public String getDescricaoResponsavel() {
        return descricaoResponsavel;
    }

    public void setDescricaoResponsavel(String descricaoResponsavel) {
        this.descricaoResponsavel = descricaoResponsavel;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getValorAvaria() {
        return valorAvaria;
    }

    public void setValorAvaria(BigDecimal valorAvaria) {
        setValorDoLancamento(valorAvaria);
        this.valorAvaria = valorAvaria;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return valorAvaria;
    }

    public Boolean getEstorno() {
        return estornoAvariaBem != null ? Boolean.TRUE : Boolean.FALSE;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
