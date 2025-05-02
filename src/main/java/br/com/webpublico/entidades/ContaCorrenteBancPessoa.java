/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.ContaCorrenteBancariaDTO;
import br.com.webpublico.pessoa.dto.EnderecoCorreioDTO;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Audited
@Entity

@GrupoDiagrama(nome = "RecursosHumanos")
public class ContaCorrenteBancPessoa extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private ContaCorrenteBancaria contaCorrenteBancaria;
    @ManyToOne
    private Pessoa pessoa;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private Boolean principal;
    private String observacao;
    @Invisivel
    @Transient
    private boolean editando;
    private String justificativa;
    @ManyToOne
    private UsuarioSistema responsavel;

    public ContaCorrenteBancPessoa() {
        dataRegistro = new Date();
        principal = Boolean.FALSE;
    }

    public static List<ContaCorrenteBancariaDTO> toContaCorrenteBancariaDTOs(List<ContaCorrenteBancPessoa> contasBancarias) {
        if (contasBancarias != null && !contasBancarias.isEmpty()) {
            List<ContaCorrenteBancariaDTO> dtos = Lists.newLinkedList();
            for (ContaCorrenteBancPessoa ccbp : contasBancarias) {
                ContaCorrenteBancariaDTO dto = toContaCorrenteBancariaDTO(ccbp);
                if (dto != null) {
                    dtos.add(dto);
                }
            }
            return dtos;
        }
        return null;
    }

    public static ContaCorrenteBancariaDTO toContaCorrenteBancariaDTO(ContaCorrenteBancPessoa ccbp) {
        if (ccbp == null) {
            return null;
        }
        ContaCorrenteBancariaDTO dto = new ContaCorrenteBancariaDTO();
        dto.setPrincipal(ccbp.getPrincipal());
        if (ccbp.getContaCorrenteBancaria() != null) {
            dto.setId(ccbp.getId());
            dto.setBancoDTO(Banco.toBancoDTO(ccbp.getBanco()));
            dto.setAgenciaDTO(Agencia.toAgenciaDTO(ccbp.getAgencia()));
            dto.setNameModalidadeConta(ccbp.getModalidade().name());
            dto.setNumeroConta(ccbp.getNumeroConta());
            dto.setDigitoVerificador(ccbp.getDigitoVerificador());
        }
        return dto;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Boolean getPrincipal() {
        return principal != null ? principal : false;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isEditando() {
        return editando;
    }

    public void setEditando(boolean editando) {
        this.editando = editando;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (contaCorrenteBancaria != null) {
            return retorno + contaCorrenteBancaria;
        }
        if (!"".equals(retorno)) {
            return retorno;
        }
        return "" + contaCorrenteBancaria;


    }

    public Banco getBanco() {
        return this.getContaCorrenteBancaria().getBanco();
    }

    public Agencia getAgencia() {
        return this.getContaCorrenteBancaria().getAgencia();
    }

    public String getNumeroConta() {
        return this.getContaCorrenteBancaria().getNumeroConta();
    }

    public String getDigitoVerificador() {
        return this.getContaCorrenteBancaria().getDigitoVerificador();
    }

    public SituacaoConta getSituacao() {
        return this.getContaCorrenteBancaria().getSituacao();
    }

    public ModalidadeConta getModalidade() {
        return this.getContaCorrenteBancaria().getModalidadeConta();
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
