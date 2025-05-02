package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class AgendamentoViagemSaudEndereco extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Etiqueta("Logradouro")
    private String logradouro;
    @Obrigatorio
    @Etiqueta("Número")
    private String numero;
    @Obrigatorio
    @Etiqueta("Bairro")
    private String bairro;
    @Etiqueta("CEP")
    private String cep;
    @Obrigatorio
    @Etiqueta("Cidade")
    @ManyToOne
    private Cidade cidade;

    @Transient
    @Etiqueta("UF")
    @Obrigatorio
    private UF uf;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void realizarValidacoes(ValidacaoException ve, String preFixo) {
        if (Strings.isNullOrEmpty(logradouro)) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Logradouro deve ser informado.");
        }
        if (Strings.isNullOrEmpty(numero)) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Número deve ser informado.");
        }
        if (Strings.isNullOrEmpty(bairro)) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Bairro deve ser informado.");
        }
        if (uf == null) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo UF deve ser informado.");
        }
        if (cidade == null) {
            ve.adicionarMensagemDeCampoObrigatorio(preFixo + "O campo Cidade deve ser informado.");
        }
    }
}
