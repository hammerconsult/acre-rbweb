package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 19/02/2018.
 */

@Etiqueta(value = "Configuração Tipo de Objeto de Compra")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class ConfigTipoObjetoCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Objeto de Compra")
    @Tabelavel
    @Pesquisavel
    private TipoObjetoCompra tipoObjetoCompra;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Subtipo Objeto de Compra")
    @Tabelavel
    @Pesquisavel
    private SubTipoObjetoCompra subtipoObjetoCompra;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    @Invisivel
    @Etiqueta("Conta de Despesa")
    @OneToMany(mappedBy = "configTipoObjetoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoObjetoCompraContaDespesa> contasDespesa;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public ConfigTipoObjetoCompra() {
        contasDespesa = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public SubTipoObjetoCompra getSubtipoObjetoCompra() {
        return subtipoObjetoCompra;
    }

    public void setSubtipoObjetoCompra(SubTipoObjetoCompra subtipoObjetoCompra) {
        this.subtipoObjetoCompra = subtipoObjetoCompra;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public List<TipoObjetoCompraContaDespesa> getContasDespesa() {
        return contasDespesa;
    }

    public void setContasDespesa(List<TipoObjetoCompraContaDespesa> contasDespesa) {
        this.contasDespesa = contasDespesa;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento != null ? erroDuranteProcessamento : Boolean.FALSE;
    }

    public void setErroDuranteProcessamento(Boolean erroDuranteProcessamento) {
        this.erroDuranteProcessamento = erroDuranteProcessamento;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    @Override
    public String toString() {
        try {
            return tipoObjetoCompra.getDescricao()
                + " - " + subtipoObjetoCompra.getDescricao()
                + " (" + DataUtil.getDataFormatada(this.inicioVigencia) + ")";
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public void validarObjetoEmLista(Conta contaDespesa) {
        ValidacaoException ve = new ValidacaoException();
        for (TipoObjetoCompraContaDespesa conta : getContasDespesa()) {
            if (conta.getContaDespesa().equals(contaDespesa)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A conta de despesa: " + contaDespesa + " já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }
}
