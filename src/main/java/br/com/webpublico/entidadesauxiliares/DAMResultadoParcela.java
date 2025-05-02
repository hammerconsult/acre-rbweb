package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import org.jetbrains.annotations.NotNull;

public class DAMResultadoParcela implements Comparable<DAMResultadoParcela> {
    private Long id;
    private ResultadoParcela resultadoParcela;
    private DAM dam;
    private Integer numeroCalculo;

    public DAMResultadoParcela() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public Integer getNumeroCalculo() {
        return numeroCalculo;
    }

    public void setNumeroCalculo(Integer numeroCalculo) {
        this.numeroCalculo = numeroCalculo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DAMResultadoParcela that = (DAMResultadoParcela) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull DAMResultadoParcela o) {
        ResultadoParcela parcela1 = this.getResultadoParcela();
        ResultadoParcela parcela2 = o.getResultadoParcela();
        return ComparisonChain.start()
                .compare(this.getNumeroCalculo(), o.getNumeroCalculo())
                .compare(parcela1.getCadastro(), parcela2.getCadastro())
                .compare(parcela1.getOrdemApresentacao(), parcela2.getOrdemApresentacao())
                .compare(parcela1.getDivida(), parcela2.getDivida())
                .compare(parcela1.getExercicio(), parcela2.getExercicio())
                .compare(parcela1.getReferencia(), parcela2.getReferencia())
                .compare(parcela1.getSd(), parcela2.getSd())
                .compare(parcela1.getCotaUnica(), parcela2.getCotaUnica())
                .compare(parcela1.getVencimento(), parcela2.getVencimento())
                .compare(parcela1.getParcela(), parcela2.getParcela())
                .compare(parcela1.getSituacao(), parcela2.getSituacao()).result();
    }
}
