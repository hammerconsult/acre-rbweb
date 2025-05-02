package br.com.webpublico.entidadesauxiliares;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
public class VORelatorioITBIPessoa {
    private String nome;
    private Long pessoaId;

    public VORelatorioITBIPessoa() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }
}
