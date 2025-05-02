package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Solicitação de Estorno de Liquidação")
@Table(name = "SOLICITACAOLIQUIDACAOEST")
public class SolicitacaoLiquidacaoEstorno extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data da Solicitação")
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;

    @ManyToOne
    @Etiqueta("Estorno de Liquidação")
    private LiquidacaoEstorno liquidacaoEstorno;

    @ManyToOne
    @Etiqueta("Liquidação")
    private Liquidacao liquidacao;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Histórico")
    private String historico;

    @Etiqueta("Valor")
    private BigDecimal valor;

    public SolicitacaoLiquidacaoEstorno() {
        this.dataSolicitacao = new Date();
    }

    public LiquidacaoEstorno getLiquidacaoEstorno() {
        return liquidacaoEstorno;
    }

    public void setLiquidacaoEstorno(LiquidacaoEstorno liquidacaoEstorno) {
        this.liquidacaoEstorno = liquidacaoEstorno;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }


    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public boolean hasLiquidacaoEstornada(){
        return liquidacaoEstorno !=null;
    }

    @Override
    public String toString() {
        try {
            return DataUtil.getDataFormatada(dataSolicitacao) + " " + Util.formataValor(valor) + ", liquidação" + liquidacao.getNumero();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
