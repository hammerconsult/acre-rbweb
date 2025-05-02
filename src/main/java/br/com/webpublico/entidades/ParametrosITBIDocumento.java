package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ParametrosITBIDocumento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ParametrosITBI parametrosITBI;
    @Etiqueta("Natureza do Documento")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private NaturezaDocumento naturezaDocumento;
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @Etiqueta("Extensões Permitidas")
    @Obrigatorio
    private String extensoesPermitidas;
    private Boolean obrigatorio;
    private Boolean ativo;

    public ParametrosITBIDocumento() {
        super();
        obrigatorio = Boolean.TRUE;
        ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametrosITBI getParametrosITBI() {
        return parametrosITBI;
    }

    public void setParametrosITBI(ParametrosITBI parametrosITBI) {
        this.parametrosITBI = parametrosITBI;
    }

    public NaturezaDocumento getNaturezaDocumento() {
        return naturezaDocumento;
    }

    public void setNaturezaDocumento(NaturezaDocumento naturezaDocumento) {
        this.naturezaDocumento = naturezaDocumento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getExtensoesPermitidas() {
        return extensoesPermitidas;
    }

    public void setExtensoesPermitidas(String extensoesPermitidas) {
        this.extensoesPermitidas = extensoesPermitidas;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public enum NaturezaDocumento {
        SOLICITACAO("Solicitação"),
        PROCURADOR("Procurador"),
        ADQUIRENTE("Adquirente"),
        TRANSMITENTE("Transmitente");

        private String descricao;

        NaturezaDocumento(String descricao) {
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
}
