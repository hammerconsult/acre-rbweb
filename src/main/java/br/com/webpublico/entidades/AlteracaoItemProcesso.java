package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAlteracaoItem;
import br.com.webpublico.enums.TipoProcesso;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.util.Date;

@Entity
@Etiqueta("Alteração Item Processo")
public class AlteracaoItemProcesso extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @Enumerated(EnumType.STRING)
    private TipoProcesso tipoProcesso;

    @Enumerated(EnumType.STRING)
    private TipoAlteracaoItem tipoAlteracaoItem;

    @Etiqueta("Histórico")
    private String historicoProcesso;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public TipoAlteracaoItem getTipoAlteracaoItem() {
        return tipoAlteracaoItem;
    }

    public void setTipoAlteracaoItem(TipoAlteracaoItem tipoAlteracaoItem) {
        this.tipoAlteracaoItem = tipoAlteracaoItem;
    }

    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public String getHistoricoProcesso() {
        return historicoProcesso;
    }

    public void setHistoricoProcesso(String historicoProcesso) {
        this.historicoProcesso = historicoProcesso;
    }
}
