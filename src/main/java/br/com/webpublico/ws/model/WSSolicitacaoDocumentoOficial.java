package br.com.webpublico.ws.model;


import br.com.webpublico.entidades.SolicitacaoDoctoOficial;
import br.com.webpublico.enums.SituacaoSolicitacao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class WSSolicitacaoDocumentoOficial implements Serializable {

    private Long id;
    private Integer codigo;
    private Date dataSolicitacao;
    private String tipoDocumento;
    private String tipoCadastro;
    private String cadastro;
    private String situacao;
    private Date validade;
    private Boolean vencida;

    public WSSolicitacaoDocumentoOficial() {
    }

    public WSSolicitacaoDocumentoOficial(Object[] obj) {
        this.codigo = ((BigDecimal) obj[0]).intValue();
        this.dataSolicitacao = (Date) obj[1];
        this.tipoDocumento = (String) obj[2];
        this.tipoCadastro = (String) obj[3];
        this.cadastro = (String) obj[4];
        this.situacao = "Em Aberto";
        this.vencida = Boolean.FALSE;
    }

    public WSSolicitacaoDocumentoOficial(SolicitacaoDoctoOficial solicitacao) {
        this.id = solicitacao.getId();
        this.codigo = solicitacao.getCodigo().intValue();
        this.dataSolicitacao = solicitacao.getDataSolicitacao();
        this.tipoDocumento = solicitacao.getTipoDoctoOficial().getDescricao();
        this.tipoCadastro = solicitacao.getTipoCadastro();
        this.cadastro = solicitacao.getNomeCadastroTabelavel();

        Calendar validade = Calendar.getInstance();
        validade.setTime(dataSolicitacao);
        validade.add(Calendar.DAY_OF_MONTH, solicitacao.getTipoDoctoOficial().getValidadeDocto());

        if (!SituacaoSolicitacao.CANCELADO.equals(solicitacao.getSituacaoSolicitacao())) {
            if (solicitacao.getTipoDoctoOficial().getValidadeDocto() != null && dataSolicitacao != null) {
                this.validade = validade.getTime();
                this.situacao = this.validade.after(new Date()) ? "Em Aberto" : "Vencida";
                this.vencida = this.validade.before(new Date());
            } else {
                this.situacao = "Em Aberto";
                this.vencida = Boolean.FALSE;
            }
        } else {
            this.situacao = "Cancelado";
            this.vencida = Boolean.TRUE;
            this.validade = validade.getTime();
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public Boolean getVencida() {
        return vencida;
    }

    public void setVencida(Boolean vencida) {
        this.vencida = vencida;
    }
}
