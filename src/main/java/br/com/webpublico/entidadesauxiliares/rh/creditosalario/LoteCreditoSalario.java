package br.com.webpublico.entidadesauxiliares.rh.creditosalario;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.GrupoRecursoFP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

public class LoteCreditoSalario implements Serializable {


    protected static final Logger logger = LoggerFactory.getLogger(LoteCreditoSalario.class);
    private Banco banco;
    private BigDecimal valorLote;
    private Integer posicao;
    private Integer contadorLinhaLote;
    private String formaLancamento;
    private GrupoRecursoFP gruposRecursoFP;
    private Set<VinculoCreditoSalario> vinculosFPS;

    public LoteCreditoSalario() {
        contadorLinhaLote = 1;
        valorLote = BigDecimal.ZERO;
    }

    public synchronized void incrementarContadorLinhaLote() {
        contadorLinhaLote++;
    }

    public Integer getContadorLinhaLote() {
        return contadorLinhaLote;
    }

    public void setContadorLinhaLote(Integer contadorLinhaLote) {
        this.contadorLinhaLote = contadorLinhaLote;
    }

    public static Logger getLogger() {
        return logger;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public BigDecimal getValorLote() {
        return valorLote;
    }

    public void acrescentarValorNoLote(BigDecimal valor) {
        valorLote = valorLote.add(valor);
    }

    public void setValorLote(BigDecimal valorLote) {
        this.valorLote = valorLote;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }

    public String getFormaLancamento() {
        return formaLancamento;
    }

    public void setFormaLancamento(String formaLancamento) {
        this.formaLancamento = formaLancamento;
    }

    public GrupoRecursoFP getGruposRecursoFP() {
        return gruposRecursoFP;
    }

    public void setGruposRecursoFP(GrupoRecursoFP gruposRecursoFP) {
        this.gruposRecursoFP = gruposRecursoFP;
    }

    public Set<VinculoCreditoSalario> getVinculosFPS() {
        return vinculosFPS;
    }

    public void setVinculosFPS(Set<VinculoCreditoSalario> vinculosFPS) {
        this.vinculosFPS = vinculosFPS;
    }
}
