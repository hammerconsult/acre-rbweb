package br.com.webpublico.enums;

import br.com.webpublico.entidades.BensImoveis;
import br.com.webpublico.entidades.BensIntangiveis;
import br.com.webpublico.entidades.BensMoveis;
import br.com.webpublico.webreportdto.dto.administrativo.TipoGrupoDTO;

import java.util.ArrayList;
import java.util.List;

public enum TipoGrupo {

    BEM_MOVEL_PRINCIPAL("Bem Móvel Principal", BensMoveis.class, TipoGrupoDTO.BEM_MOVEL_PRINCIPAL),
    BEM_MOVEL_INSERVIVEL("Bem Móvel Inservível", BensMoveis.class, TipoGrupoDTO.BEM_MOVEL_INSERVIVEL),
    BEM_MOVEL_ALIENAR("Bem Móvel a Alienar", BensMoveis.class, TipoGrupoDTO.BEM_MOVEL_ALIENAR),
    BEM_MOVEL_INTEGRACAO("Bem Móvel Integração", BensMoveis.class, TipoGrupoDTO.BEM_MOVEL_INTEGRACAO),
    BEM_IMOVEL_PRINCIPAL("Bem Imóvel Principal", BensImoveis.class, TipoGrupoDTO.BEM_IMOVEL_PRINCIPAL),
    BEM_IMOVEL_INSERVIVEL("Bem Imóvel Inservível", BensImoveis.class, TipoGrupoDTO.BEM_IMOVEL_INSERVIVEL),
    BEM_IMOVEL_ALIENAR("Bem Imóvel a Alienar", BensImoveis.class, TipoGrupoDTO.BEM_IMOVEL_ALIENAR),
    BEM_IMOVEL_INTEGRACAO("Bem Imóvel Integração", BensImoveis.class, TipoGrupoDTO.BEM_IMOVEL_INTEGRACAO),
    SOFTWARE_PRINCIPAL("Softwares Principal", BensIntangiveis.class, TipoGrupoDTO.SOFTWARE_PRINCIPAL),
    MARCAS_DIREITOS_PATENTES("Marcas, Direitos e Patentes Industriais Principal", BensIntangiveis.class, TipoGrupoDTO.MARCAS_DIREITOS_PATENTES),
    DIREITO_USO_IMOVEL("Direito de uso de Imóveis Principal", BensIntangiveis.class, TipoGrupoDTO.DIREITO_USO_IMOVEL),
    BEM_INTANGIVEL_INTEGRACAO("Bem Intangível Integração", BensIntangiveis.class, TipoGrupoDTO.BEM_INTANGIVEL_INTEGRACAO);
    private String descricao;
    private Class classeDeUtilizacao;
    private TipoGrupoDTO toDto;

    private TipoGrupo(String descricao, Class classeDeUtilizacao, TipoGrupoDTO toDto) {
        this.descricao = descricao;
        this.classeDeUtilizacao = classeDeUtilizacao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Class getClasseDeUtilizacao() {
        return classeDeUtilizacao;
    }

    public TipoGrupoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<TipoGrupo> tipoGrupoPorClasseDeUtilizacao(Class aClass) {
        List<TipoGrupo> retorno = new ArrayList<>();
        int x = 0;
        for (TipoGrupo t : TipoGrupo.values()) {
            if (t.getClasseDeUtilizacao().equals(aClass)) {
                retorno.add(t);
                x++;
            }
        }
        return retorno;
    }
}
