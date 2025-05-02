package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class DocumentoVeiculoOtt extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ParametrosOtt parametrosOtt;
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @Etiqueta("Extensões Permitidas")
    @Obrigatorio
    private String extensoesPermitidas;
    private Boolean alugado;
    private Boolean renovacao;
    private Boolean obrigatorio;
    private Boolean ativo;

    public DocumentoVeiculoOtt() {
        super();
        this.alugado = Boolean.FALSE;
        this.renovacao = Boolean.TRUE;
        this.obrigatorio = Boolean.TRUE;
        this.ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ParametrosOtt getParametrosOtt() {
        return parametrosOtt;
    }

    public void setParametrosOtt(ParametrosOtt parametrosOtt) {
        this.parametrosOtt = parametrosOtt;
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

    public Boolean getAlugado() {
        return alugado != null ? alugado : Boolean.FALSE;
    }

    public void setAlugado(Boolean alugado) {
        this.alugado = alugado;
    }

    public Boolean getRenovacao() {
        return renovacao != null ? renovacao : Boolean.FALSE;
    }

    public void setRenovacao(Boolean renovacao) {
        this.renovacao = renovacao;
    }

    public Boolean getObrigatorio() {
        return obrigatorio != null ? obrigatorio : Boolean.FALSE;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Boolean getAtivo() {
        return ativo != null ? ativo : Boolean.FALSE;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String buscarExtensoesPermitidas() {
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        extensoesPermitidas = extensoesPermitidas.replace(".", "");
        extensoesPermitidas = extensoesPermitidas.replace(",", "|");
        extensoesPermitidas = extensoesPermitidas.replace(" ", "");
        return "/(\\.|\\/)(" + extensoesPermitidas + ")$/";
    }

    public String buscarMensagemExtensoesPermitidas() {
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        return "Extensão invalida, apenas as extenções (" + extensoesPermitidas + ") são permitidas.";
    }
}
