package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/08/14
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Penalidade de Fiscalização de Secretaria")
@Table(name = "PenalidadeFiscSecretaria")
public class PenalidadeFiscalizacaoSecretaria extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Tabelavel
    @Etiqueta(value = "Secretaria de Fiscalização")
    @Obrigatorio
    @ManyToOne
    private SecretariaFiscalizacao secretariaFiscalizacao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Código")
    private Integer codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Descrição Reduzida")
    @Obrigatorio
    private String descricaoReduzida;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Descrição Detalhada")
    @Obrigatorio
    private String descricaoDetalhada;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Embasamento Legal")
    @Obrigatorio
    private String embasamentoLegal;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Grau")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private Grau grau;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Tipo de Cobrança")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCobranca tipoCobranca;
    @Pesquisavel
    @Tabelavel
    @Numerico
    @UFM
    @Etiqueta(value = "Valor")
    private BigDecimal valor;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Tributo")
    @ManyToOne
    private Tributo tributo;
    @Transient
    private Long criadoEm;


    public PenalidadeFiscalizacaoSecretaria() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SecretariaFiscalizacao getSecretariaFiscalizacao() {
        return secretariaFiscalizacao;
    }

    public void setSecretariaFiscalizacao(SecretariaFiscalizacao secretariaFiscalizacao) {
        this.secretariaFiscalizacao = secretariaFiscalizacao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public String getEmbasamentoLegal() {
        return embasamentoLegal;
    }

    public void setEmbasamentoLegal(String embasamentoLegal) {
        this.embasamentoLegal = embasamentoLegal;
    }

    public Grau getGrau() {
        return grau;
    }

    public void setGrau(Grau grau) {
        this.grau = grau;
    }

    public TipoCobranca getTipoCobranca() {
        return tipoCobranca;
    }

    public void setTipoCobranca(TipoCobranca tipoCobranca) {
        this.tipoCobranca = tipoCobranca;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public enum Grau {
        LEVE("Leve"), GRAVE("Grave"), GRAVISSIMA("Gravíssima");

        private String descricao;

        Grau(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum TipoCobranca {
        FIXO_UFM("Fixo (UFM)"), FIXO_REAL("Fixo (R$)"), NAO_APLICADA("Não Aplicada");

        private String descricao;

        TipoCobranca(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

        public boolean isValor() {
            return this.equals(FIXO_UFM) || this.equals(FIXO_REAL);
        }
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PenalidadeFiscalizacaoSecretaria)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (getTipoCobranca() != null &&
            (getTipoCobranca().equals(PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_UFM) ||
                getTipoCobranca().equals(PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_REAL)) &&
            (getValor() == null || getValor().compareTo(BigDecimal.ZERO) <= 0)) {
            if (PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_UFM.equals(getTipoCobranca())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Valor (UFM) deve ser informado.");
            } else if (PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_REAL.equals(getTipoCobranca())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Valor (R$) deve ser informado.");
            }
        }
        if (getTipoCobranca() != null &&
            (getTipoCobranca().equals(PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_UFM) ||
                getTipoCobranca().equals(PenalidadeFiscalizacaoSecretaria.TipoCobranca.FIXO_REAL)) &&
            (getTributo() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tributo deve ser informado.");
        }
        ve.lancarException();
    }
}
