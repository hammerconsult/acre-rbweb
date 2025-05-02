package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAdesao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/04/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Adesão")
public class Adesao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Ata de Registro de Preço")
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    private AtaRegistroPreco ataDeRegistroDePreco;

    @Etiqueta("Solicitação de Registro de Preço")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private SolicitacaoMaterialExterno solicitacaoMaterialExterno;

    @Etiqueta("Observação")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String observacao;

    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date data;

    @Etiqueta("Tipo da Adesão")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoAdesao tipoAdesao;

    @Etiqueta("Adesão Aceita")
    @Tabelavel
    @Pesquisavel
    private Boolean adesaoAceita;

    @Etiqueta("Número da Requisição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer numeroRequisicao;

    @Etiqueta("Data da Requisição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataRequisicao;

    @Etiqueta("Documento")
    @Obrigatorio
    @ManyToOne
    private Documento documento;

    @Etiqueta("Unidade Externa")
    @Obrigatorio
    @ManyToOne
    private UnidadeExterna unidadeExterna;

    public Adesao() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtaRegistroPreco getAtaDeRegistroDePreco() {
        return ataDeRegistroDePreco;
    }

    public void setAtaDeRegistroDePreco(AtaRegistroPreco ataDeRegistroDePreco) {
        this.ataDeRegistroDePreco = ataDeRegistroDePreco;
    }

    public SolicitacaoMaterialExterno getSolicitacaoMaterialExterno() {
        return solicitacaoMaterialExterno;
    }

    public void setSolicitacaoMaterialExterno(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        this.solicitacaoMaterialExterno = solicitacaoMaterialExterno;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public TipoAdesao getTipoAdesao() {
        return tipoAdesao;
    }

    public void setTipoAdesao(TipoAdesao tipoAdesao) {
        this.tipoAdesao = tipoAdesao;
    }

    public Boolean getAdesaoAceita() {
        return adesaoAceita;
    }

    public void setAdesaoAceita(Boolean adesaoAceita) {
        this.adesaoAceita = adesaoAceita;
    }

    public Integer getNumeroRequisicao() {
        return numeroRequisicao;
    }

    public void setNumeroRequisicao(Integer numeroRequisicao) {
        this.numeroRequisicao = numeroRequisicao;
    }

    public Date getDataRequisicao() {
        return dataRequisicao;
    }

    public void setDataRequisicao(Date dataRequisicao) {
        this.dataRequisicao = dataRequisicao;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public UnidadeExterna getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(UnidadeExterna unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("n° ").append(numeroRequisicao).append(" - ").append(DataUtil.getDataFormatada(data)).append(" - ").append(observacao);

        if (sb.length() > 80) {
            StringBuilder sbTemp = sb;
            sb = new StringBuilder();
            sb.append(sbTemp.substring(0, 80)).append("...");
        }
        return sb.toString();
    }

    public boolean isInterna() {
        return TipoAdesao.INTERNA.equals(tipoAdesao);
    }
}
