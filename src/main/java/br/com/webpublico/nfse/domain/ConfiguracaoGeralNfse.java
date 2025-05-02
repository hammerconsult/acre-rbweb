package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.ConfiguracaoGeralNfseDTO;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by william on 13/09/2017.
 */
@Entity
@Audited
public class ConfiguracaoGeralNfse extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean obrigatorioAidfe;

    public static List<ConfiguracaoGeralNfseDTO> toListConfiguracaoGeralNfseDTO(List<ConfiguracaoGeralNfse> configuracoes) {
        List<ConfiguracaoGeralNfseDTO> retorno = Lists.newArrayList();
        if (configuracoes != null && !configuracoes.isEmpty()) {
            for (ConfiguracaoGeralNfse configuracao : configuracoes) {
                retorno.add(configuracao.toConfiguracaoDTO());
            }
        }
        return retorno;
    }

    public static ConfiguracaoGeralNfse toConfiguracaoGeralNfse(ConfiguracaoGeralNfseDTO configuracaoGeralNfseDTO) {
        ConfiguracaoGeralNfse configuracaoGeralNfse = new ConfiguracaoGeralNfse();
        configuracaoGeralNfse.setId(configuracaoGeralNfseDTO.getId());
        configuracaoGeralNfse.setObrigatorioAidfe(configuracaoGeralNfseDTO.getObrigatorioAidfe());
        return configuracaoGeralNfse;
    }

    public ConfiguracaoGeralNfseDTO toConfiguracaoDTO() {
        ConfiguracaoGeralNfseDTO configuracaoGeralNfseDTO = new ConfiguracaoGeralNfseDTO();
        configuracaoGeralNfseDTO.setId(this.getId());
        configuracaoGeralNfseDTO.setObrigatorioAidfe(this.getObrigatorioAidfe());
        return configuracaoGeralNfseDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getObrigatorioAidfe() {
        return obrigatorioAidfe;
    }

    public void setObrigatorioAidfe(Boolean obrigatorioAidfe) {
        this.obrigatorioAidfe = obrigatorioAidfe;
    }

}
