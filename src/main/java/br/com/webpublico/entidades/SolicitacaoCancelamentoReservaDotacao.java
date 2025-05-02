package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.enums.SituacaoSolicitacaoCancelamentoReservaDotacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 06/06/14
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Solicitação de Cancelamento de Reserva de Dotação")
@Table(name = "SOLCANCELARESERVADOTACAO")
public class SolicitacaoCancelamentoReservaDotacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    private Date data;
    @Etiqueta("Número")
    @Pesquisavel
    @Tabelavel
    private Long numero;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Unidade Organizacional")
    @Pesquisavel
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToOne
    @Etiqueta("Usuário Solicitante")
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema usuarioSolicitante;
    @Tabelavel
    @Etiqueta("Fonte de Despesa Orçamentária")
    @ManyToOne
    @Pesquisavel
    private FonteDespesaORC fonteDespesaORC;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Motivo")
    @Pesquisavel
    private String motivo;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Situação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoSolicitacaoCancelamentoReservaDotacao situacao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private OrigemReservaFonte origemReservaFonte;
    @Etiqueta("Valor (R$)")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public SolicitacaoCancelamentoReservaDotacao() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSolicitante() {
        return usuarioSolicitante;
    }

    public void setUsuarioSolicitante(UsuarioSistema usuarioSolicitante) {
        this.usuarioSolicitante = usuarioSolicitante;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public SituacaoSolicitacaoCancelamentoReservaDotacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoCancelamentoReservaDotacao situacao) {
        this.situacao = situacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OrigemReservaFonte getOrigemReservaFonte() {
        return origemReservaFonte;
    }

    public void setOrigemReservaFonte(OrigemReservaFonte origemReservaFonte) {
        this.origemReservaFonte = origemReservaFonte;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return numero + " - " + DataUtil.getDataFormatada(data) + " - " + fonteDespesaORC.getDespesaORC().toString() + "- " + Util.formataValor(valor);
    }
}
