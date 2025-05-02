package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */


@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@Etiqueta("Configuração de Bens Imóveis")
public class ConfigBensImoveis extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta(value = "Operação de Bens Imóveis")
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensImoveis operacaoBensImoveis;

    public TipoOperacaoBensImoveis getOperacaoBensImoveis() {
        return operacaoBensImoveis;
    }

    public void setOperacaoBensImoveis(TipoOperacaoBensImoveis operacaoBensImoveis) {
        this.operacaoBensImoveis = operacaoBensImoveis;
    }
}
