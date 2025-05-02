package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ExecucaoProcessoFonteVO;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitações")
public class ExecucaoProcessoReserva extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Processo")
    private ExecucaoProcesso execucaoProcesso;

    @ManyToOne
    @Etiqueta("Classe Credor")
    private ClasseCredor classeCredor;

    @Etiqueta("Tipo Objeto de Compra")
    @Enumerated(EnumType.STRING)
    private TipoObjetoCompra tipoObjetoCompra;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @OneToMany(mappedBy = "execucaoProcessoReserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecucaoProcessoFonte> fontes;

    @Transient
    private List<ExecucaoProcessoFonteVO> fontesVO;

    public ExecucaoProcessoReserva() {
        fontes = Lists.newArrayList();
        fontesVO = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoAta) {
        this.execucaoProcesso = execucaoAta;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ExecucaoProcessoFonte> getFontes() {
        return fontes;
    }

    public void setFontes(List<ExecucaoProcessoFonte> fontes) {
        this.fontes = fontes;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public List<ExecucaoProcessoFonteVO> getFontesVO() {
        return fontesVO;
    }

    public void setFontesVO(List<ExecucaoProcessoFonteVO> fontesVO) {
        this.fontesVO = fontesVO;
    }

    public BigDecimal getValorTotalReservado() {
        BigDecimal valorReservado = BigDecimal.ZERO;
        if (getFontes() != null) {
            for (ExecucaoProcessoFonteVO fonte : getFontesVO()) {
                valorReservado = valorReservado.add(fonte.getValor());
            }
        }
        return valorReservado;
    }

    public Boolean hasFontes() {
        return fontesVO != null && !fontesVO.isEmpty();
    }

    public Boolean isObjetoCompraConsumoOrPermanenteMovel() {
        return tipoObjetoCompra != null && tipoObjetoCompra.isPermanenteOrConsumo();
    }

    @Override
    public String toString() {
        try {
            return "Execução: " + execucaoProcesso.getNumero() + " - " + " Tipo Objeto de Compra: " + tipoObjetoCompra.getDescricao() + " Valor R$: " + Util.formataValor(valor);
        } catch (NullPointerException e) {
            return "";
        }
    }
}
