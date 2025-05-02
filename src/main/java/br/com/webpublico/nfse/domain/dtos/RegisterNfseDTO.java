package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.exceptions.NfseOperacaoNaoPermitidaException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterNfseDTO extends UserNfseDTO implements Serializable {

    private DadosPessoaisNfseDTO dadosPessoais;
    private Boolean inserirPessoa;

    public RegisterNfseDTO() {
    }

    public DadosPessoaisNfseDTO getDadosPessoais() {
        return dadosPessoais;
    }

    public void setDadosPessoais(DadosPessoaisNfseDTO dadosPessoais) {
        this.dadosPessoais = dadosPessoais;
    }

    public Boolean getInserirPessoa() {
        return inserirPessoa != null ? inserirPessoa : Boolean.FALSE;
    }

    public void setInserirPessoa(Boolean inserirPessoa) {
        this.inserirPessoa = inserirPessoa;
    }

    @Override
    public String toString() {
        return "RegisterDTO{" +
            "dadosPessoais=" + dadosPessoais +
            '}';
    }

    @JsonIgnore
    public void validarDadosPessoaisUsuario() throws NfseOperacaoNaoPermitidaException {
        super.validarDadosPessoaisUsuario();
        List<String> mensagens = Lists.newArrayList();

        if (getDadosPessoais() == null) {
            mensagens.add("Informe ao menos um telefone ou um telefone celular!");
            mensagens.add("O campo cep é obrigatório!");
            mensagens.add("O campo logradouro é obrigatório!");
            mensagens.add("O campo número é obrigatório!");
            mensagens.add("O campo bairro é obrigatório!");
            mensagens.add("O campo município é obrigatório!");
            mensagens.add("O campo uf é obrigatório!");
        } else {
            if (Strings.isNullOrEmpty(getDadosPessoais().getNomeRazaoSocial())) {
                mensagens.add("O campo Nome ou Razação Social é obrigatório!");
            }
            if (Strings.isNullOrEmpty(getDadosPessoais().getCpfCnpj())) {
                mensagens.add("O campo PF ou CNPJ é obrigatório!");
            }
            if ((Strings.isNullOrEmpty(getDadosPessoais().getTelefone())) &&
                (Strings.isNullOrEmpty(getDadosPessoais().getCelular()))) {
                mensagens.add("Informe ao menos um telefone ou um telefone celular!");
            }
            if (Strings.isNullOrEmpty(getDadosPessoais().getCep())) {
                mensagens.add("O campo cep é obrigatório!");
            }
            if (Strings.isNullOrEmpty(getDadosPessoais().getLogradouro())) {
                mensagens.add("O campo logradouro é obrigatório!");
            }
            if (Strings.isNullOrEmpty(getDadosPessoais().getBairro())) {
                mensagens.add("O campo bairro é obrigatório!");
            }
            if (getDadosPessoais().getMunicipio() == null) {
                mensagens.add("O campo município é obrigatório!");
            }
        }

        if (!mensagens.isEmpty()) {
            throw new NfseOperacaoNaoPermitidaException("Atenção!", mensagens);
        }
    }

}
