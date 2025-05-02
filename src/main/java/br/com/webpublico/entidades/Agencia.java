package br.com.webpublico.entidades;

import br.com.webpublico.enums.Situacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.AgenciaDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "Bancario")
@Entity
@Audited
@Etiqueta("Agência Bancária")
public class Agencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Banco")
    @ManyToOne
    @Obrigatorio
    private Banco banco;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número da Agência")
    @Obrigatorio
    private String numeroAgencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Dígito Verificador")
    @Obrigatorio
    private String digitoVerificador;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Column(length = 40)
    @Etiqueta("Descrição da Agência")
    private String nomeAgencia;
    @Tabelavel
    @Etiqueta(value = "Endereco")
    @ManyToOne(cascade = CascadeType.ALL)
    private EnderecoCorreio enderecoCorreio;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private Situacao situacao;

    public Agencia(Banco banco, String numeroAgencia, String digitoVerificador, String nomeAgencia, EnderecoCorreio endereco) {
        this.banco = banco;
        this.numeroAgencia = numeroAgencia;
        this.digitoVerificador = digitoVerificador;
        this.nomeAgencia = nomeAgencia;
        this.enderecoCorreio = endereco;
        situacao = Situacao.ATIVO;
    }

    public static List<AgenciaDTO> toAgenciaDTOs(List<Agencia> agencias) {
        if (agencias != null && !agencias.isEmpty()) {
            List<AgenciaDTO> dtos = Lists.newLinkedList();
            agencias.forEach(agencia -> dtos.add(toAgenciaDTO(agencia)));
            return dtos;
        }
        return null;
    }

    public static AgenciaDTO toAgenciaDTO(Agencia agencia) {
        if (agencia == null) {
            return null;
        }
        return new AgenciaDTO(agencia.getId(), agencia.getNomeAgencia(), agencia.getDigitoVerificador() != null ? agencia.getNumeroAgenciaComDigitoVerificador() : agencia.getNumeroAgencia());
    }

    public String getNumeroAgenciaComDigitoVerificador() {
        return this.numeroAgencia + " - " + this.digitoVerificador;
    }

    public Agencia() {
        enderecoCorreio = new EnderecoCorreio();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnderecoCorreio getEnderecoCorreio() {
        return enderecoCorreio;
    }

    public void setEnderecoCorreio(EnderecoCorreio endereco) {
        this.enderecoCorreio = endereco;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Agencia)) {
            return false;
        }
        Agencia other = (Agencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = agenciaEDigitoVerificador();

        if (nomeAgencia != null) {
            retorno += "   " + nomeAgencia;
        }
        return retorno;
    }

    public String agenciaEDigitoVerificador() {
        String retorno = "";
        if (this.numeroAgencia != null) {
            retorno = this.numeroAgencia;
        }
        if (this.digitoVerificador != null) {
            retorno += "-" + digitoVerificador;
        }
        return retorno;
    }

    public String toStringAgencia() {
        String retorno = "";
        if (this.numeroAgencia != null) {
            retorno = this.numeroAgencia;
        }
        if (this.digitoVerificador != null) {
            retorno += "-" + digitoVerificador;
        }
        if (this.nomeAgencia != null) {
            retorno += " - " + nomeAgencia;
        }
        return retorno;
    }

    /**
     * @return the nomeAgencia
     */
    public String getNomeAgencia() {
        return nomeAgencia;
    }

    /**
     * @param nomeAgencia the nomeAgencia to set
     */
    public void setNomeAgencia(String nomeAgencia) {
        this.nomeAgencia = nomeAgencia;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }
}
