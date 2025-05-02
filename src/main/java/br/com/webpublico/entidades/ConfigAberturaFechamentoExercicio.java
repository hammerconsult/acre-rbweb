package br.com.webpublico.entidades;

import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.TipoMovimentoContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by mateus on 12/12/17.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Abertura e Fechamento de Exercício")
@Table(name = "CONFIGABERTURAFECHAMENTOEX")
public class ConfigAberturaFechamentoExercicio extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Movimento Contábil")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private TipoMovimentoContabil tipoMovimentoContabil;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Patrimonio Líquido")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private PatrimonioLiquido patrimonioLiquido;

    public ConfigAberturaFechamentoExercicio() {
        super();
    }

    public TipoMovimentoContabil getTipoMovimentoContabil() {
        return tipoMovimentoContabil;
    }

    public void setTipoMovimentoContabil(TipoMovimentoContabil tipoMovimentoContabil) {
        this.tipoMovimentoContabil = tipoMovimentoContabil;
    }

    public PatrimonioLiquido getPatrimonioLiquido() {
        return patrimonioLiquido;
    }

    public void setPatrimonioLiquido(PatrimonioLiquido patrimonioLiquido) {
        this.patrimonioLiquido = patrimonioLiquido;
    }

    public boolean isPublico() {
        return PatrimonioLiquido.PUBLICO.equals(patrimonioLiquido);
    }

    public boolean isPrivado() {
        return PatrimonioLiquido.PRIVADO.equals(patrimonioLiquido);
    }
}
