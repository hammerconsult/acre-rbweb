/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemOrdemServico;
import br.com.webpublico.enums.TipoOrdemDeServicoContrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Ordem de Serviço de Contrato")
public class OrdemDeServicoContrato extends SuperEntidade implements ValidadorEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    private Long id;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Etiqueta("Número")
    private Integer numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Recebimento")
    private Date dataRecebimento;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Ordem")
    private TipoOrdemDeServicoContrato tipo;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Expedição")
    private Date dataExpedicao;

    @Invisivel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem do Serviço")
    private OrigemOrdemServico origem;

    public OrdemDeServicoContrato() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(Date dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public TipoOrdemDeServicoContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoOrdemDeServicoContrato tipo) {
        this.tipo = tipo;
    }

    public Date getDataExpedicao() {
        return dataExpedicao;
    }

    public void setDataExpedicao(Date dataExpedicao) {
        this.dataExpedicao = dataExpedicao;
    }

    public OrigemOrdemServico getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemOrdemServico origem) {
        this.origem = origem;
    }

    @Override
    public String toString() {
        return id == null ? "Dados ainda não gravados" : "Ordem de serviço de contrato código: " + id;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {

    }
}
