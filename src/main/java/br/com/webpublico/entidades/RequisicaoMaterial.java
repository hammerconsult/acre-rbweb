/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoRequisicaoMaterial;
import br.com.webpublico.enums.TipoRequisicaoMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Requisição de Material")
public class RequisicaoMaterial extends SuperEntidade implements Comparable<RequisicaoMaterial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Requisição")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date dataRequisicao;

    @Tabelavel
    @Pesquisavel
    @OneToOne
    @Etiqueta("Requerente e Autorizador")
    @Obrigatorio
    private UsuarioSistema requerenteEAutorizador;

    @ManyToOne
    @Etiqueta("Unidade Adimnistrativa Requerente")
    @Obrigatorio
    private UnidadeOrganizacional unidadeRequerente;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo da Requisição")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    protected TipoRequisicaoMaterial tipoRequisicao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação da Requisição")
    @Enumerated(EnumType.STRING)
    protected SituacaoRequisicaoMaterial tipoSituacaoRequisicao;

    @OneToMany(mappedBy = "requisicaoMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Itens Requisitados")
    private List<ItemRequisicaoMaterial> itensRequisitados;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Local de Estoque de Origem")
    private LocalEstoque localEstoqueOrigem;

    @ManyToOne
    @Etiqueta("Local de Estoque de Destino")
    private LocalEstoque localEstoqueDestino;

    public Boolean integrada;

    @Version
    private Long versao;

    public RequisicaoMaterial() {
        itensRequisitados = new ArrayList<>();
        integrada = false;
        tipoSituacaoRequisicao = SituacaoRequisicaoMaterial.NAO_AVALIADA;
        this.tipoRequisicao = null;
    }

    public RequisicaoMaterial(RequisicaoMaterial requisicaoMaterial, TipoRequisicaoMaterial tipoRequisicao) {
        this.setDescricao(requisicaoMaterial.getDescricao());
        this.setUnidadeRequerente(requisicaoMaterial.getUnidadeRequerente());
        this.setDataRequisicao(requisicaoMaterial.getDataRequisicao());
        this.setTipoSituacaoRequisicao(requisicaoMaterial.getTipoSituacaoRequisicao());
        this.setNumero(requisicaoMaterial.getNumero());
        this.setRequerenteEAutorizador(requisicaoMaterial.getRequerenteEAutorizador());
        this.setTipoRequisicao(tipoRequisicao);
        this.setItensRequisitados(itensRequisitados = new ArrayList<>());
    }

    public void setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial tipoSituacaoRequisicao) {
        this.tipoSituacaoRequisicao = tipoSituacaoRequisicao;
    }

    public SituacaoRequisicaoMaterial getTipoSituacaoRequisicao() {
        return tipoSituacaoRequisicao;
    }

    public TipoRequisicaoMaterial getTipoRequisicao() {
        return tipoRequisicao;
    }

    public void setTipoRequisicao(TipoRequisicaoMaterial tipoRequisicao) {
        this.tipoRequisicao = tipoRequisicao;
    }


    public boolean isRequisicaoConsumo(){
        return TipoRequisicaoMaterial.CONSUMO.equals(tipoRequisicao);
    }

    public boolean isRequisicaoTransferencia() {
        return TipoRequisicaoMaterial.TRANSFERENCIA.equals(tipoRequisicao);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRequisicao() {
        return dataRequisicao;
    }

    public void setDataRequisicao(Date dataRequisicao) {
        this.dataRequisicao = dataRequisicao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public UsuarioSistema getRequerenteEAutorizador() {
        return requerenteEAutorizador;
    }

    public void setRequerenteEAutorizador(UsuarioSistema requerenteEAutorizador) {
        this.requerenteEAutorizador = requerenteEAutorizador;
    }

    public UnidadeOrganizacional getUnidadeRequerente() {
        return unidadeRequerente;
    }

    public void setUnidadeRequerente(UnidadeOrganizacional unidadeRequerente) {
        this.unidadeRequerente = unidadeRequerente;
    }

    public List<ItemRequisicaoMaterial> getItensRequisitados() {
        return itensRequisitados;
    }

    public void setItensRequisitados(List<ItemRequisicaoMaterial> itensRequisitados) {
        this.itensRequisitados = itensRequisitados;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalEstoque getLocalEstoqueOrigem() {
        return localEstoqueOrigem;
    }

    public void setLocalEstoqueOrigem(LocalEstoque localEstoqueOrigem) {
        this.localEstoqueOrigem = localEstoqueOrigem;
    }

    public void setLocalEstoqueDestino(LocalEstoque localEstoqueDestino) {
        this.localEstoqueDestino = localEstoqueDestino;
    }

    public LocalEstoque getLocalEstoqueDestino() {
        return localEstoqueDestino;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public Boolean getIntegrada() {
        return integrada;
    }

    public void setIntegrada(Boolean guiaDistribuicao) {
        this.integrada = guiaDistribuicao;
    }

    @Override
    public String toString() {
        return "Nrº " + this.numero + " - " + DataUtil.getDataFormatada(dataRequisicao) + " - " + descricao;
    }

    public String toStringAutoComplete() {
        String complemento = "";

        if (tipoRequisicao != null) {
            switch (tipoRequisicao) {
                case CONSUMO:
                    complemento = "(" + TipoRequisicaoMaterial.CONSUMO.getDescricao() + ")";
                    break;
                case TRANSFERENCIA:
                    complemento = "(" + TipoRequisicaoMaterial.TRANSFERENCIA.getDescricao() + ")";
                    break;
                case EMPRESTIMO:
                    complemento = "(" + TipoRequisicaoMaterial.EMPRESTIMO.getDescricao() + ")";
                    break;
                default:
                    complemento = "";
                    break;
            }
        }
        try {
            return this.numero + " - " + this.descricao.toString().substring(0, pegarIndiceFinalDaDescricao()) + " - " + new SimpleDateFormat("dd/MM/yyyy").format(this.dataRequisicao) + " " + complemento;
        } catch (Exception e) {
            return "";
        }
    }

    private int pegarIndiceFinalDaDescricao() {
        if (this.descricao.toString().trim().length() > 0) {
            return this.descricao.toString().trim().length() > 30 ? 30 : this.descricao.toString().trim().length();
        }
        return 0;
    }

    public boolean requisicaoRejeitada() {
        return this.tipoSituacaoRequisicao.equals(SituacaoRequisicaoMaterial.REJEITADA);
    }

    public boolean podeSerUtilizadaEmSaidas() {
        return !tipoSituacaoRequisicao.equals(SituacaoRequisicaoMaterial.NAO_AVALIADA)
            && !tipoSituacaoRequisicao.equals(SituacaoRequisicaoMaterial.REJEITADA)
            && !tipoSituacaoRequisicao.equals(SituacaoRequisicaoMaterial.ATENDIDA_TOTALMENTE)
            && !tipoSituacaoRequisicao.equals(SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA);
    }

    @Override
    public int compareTo(RequisicaoMaterial o) {
        try {
            return this.getNumero().compareTo(o.getNumero());
        } catch (Exception e) {
            return 0;
        }
    }

    public void setQuantidadeAprovadaEMaterialAprovadoDoItem(BigDecimal quantidadeAprovada, Material materialAprovado, ItemRequisicaoMaterial irm) {
        for (ItemRequisicaoMaterial item : itensRequisitados) {
            if (item.equals(irm)) {
                item.setQuantidadeAutorizada(quantidadeAprovada);
                item.setMaterialAprovado(materialAprovado);
            }
        }
    }

    public void atribuirSituacaoRequisicao(Boolean quantidadeAtendidaMenorQueRequisitada) {
        if (quantidadeAtendidaMenorQueRequisitada) {
            setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.ATENDIDA_PARCIALMENTE);
        } else {
            setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.ATENDIDA_TOTALMENTE);
        }
    }

    public void atribuirSituacaoRequisicaoTransferencia(Boolean quantidadeAtendidaMenorQueRequisitada, boolean temEntrada) {
        if (quantidadeAtendidaMenorQueRequisitada) {
            if (temEntrada) {
                setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.SAIDA_PARCIAL_ENTRADAS_REALIZADAS);
            } else {
                setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.SAIDA_PARCIAL_ENTRADA_NAO_REALIZADA);
            }
        } else {
            if (temEntrada) {
                setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.SAIDA_TOTAL_ENTRADAS_REALIZADAS);
            } else {
                setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.SAIDA_TOTAL_ENTRADA_NAO_REALIZADA);
            }
        }
    }

    public void atualizarSituacaoRequisicaoTransferencia() {
        if (isRequisicaoTransferencia()) {
            setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA);
        } else {
            if (tipoSituacaoRequisicao.equals(SituacaoRequisicaoMaterial.SAIDA_PARCIAL_ENTRADA_NAO_REALIZADA)) {
                setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.SAIDA_PARCIAL_ENTRADAS_REALIZADAS);
            }

            if (tipoSituacaoRequisicao.equals(SituacaoRequisicaoMaterial.SAIDA_TOTAL_ENTRADA_NAO_REALIZADA)) {
                setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.SAIDA_TOTAL_ENTRADAS_REALIZADAS);
            }
        }
    }

    public Boolean getAprovada() {
        switch (this.tipoSituacaoRequisicao) {
            case ATENDIDA_TOTALMENTE:
                return Boolean.TRUE;
            case APROVADA_TOTALMENTE_E_NAO_ATENDIDA:
                return Boolean.TRUE;
            case APROVADA_PARCIALMENTE_E_NAO_ATENDIDA:
                return Boolean.TRUE;
            case ATENDIDA_PARCIALMENTE:
                return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
