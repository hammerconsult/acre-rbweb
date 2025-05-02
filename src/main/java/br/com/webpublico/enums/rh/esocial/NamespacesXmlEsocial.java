package br.com.webpublico.enums.rh.esocial;

import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.webreportdto.dto.rh.NamespacesXmlEsocialDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum NamespacesXmlEsocial {
    S1200_v02_05_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtRemun/v02_05_00\"", "/eSocial/evtRemun/dmDev/infoPerApur/ideEstabLot/remunPerApur/matricula", "/eSocial/evtRemun/ideTrabalhador/cpfTrab", "/eSocial/evtRemun/dmDev/ideDmDev", NamespacesXmlEsocialDTO.S1200_v02_05_00),
    S1200_v_S_01_00_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtRemun/v_S_01_00_00\"", "/eSocial/evtRemun/dmDev/infoPerApur/ideEstabLot/remunPerApur/matricula", "/eSocial/evtRemun/ideTrabalhador/cpfTrab", "/eSocial/evtRemun/dmDev/ideDmDev", NamespacesXmlEsocialDTO.S1200_v_S_01_00_00),
    S1200_v_S_01_01_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtRemun/v_S_01_01_00\"", "/eSocial/evtRemun/dmDev/infoPerApur/ideEstabLot/remunPerApur/matricula", "/eSocial/evtRemun/ideTrabalhador/cpfTrab", "/eSocial/evtRemun/dmDev/ideDmDev", NamespacesXmlEsocialDTO.S1200_v_S_01_01_00),
    S1202_v02_05_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtRmnRPPS/v02_05_00\"", "/eSocial/evtRmnRPPS/dmDev/infoPerApur/ideEstab/remunPerApur/matricula", "/eSocial/evtRmnRPPS/ideTrabalhador/cpfTrab", "/eSocial/evtRmnRPPS/dmDev/ideDmDev", NamespacesXmlEsocialDTO.S1202_v02_05_00),
    S1202_v_S_01_00_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtRmnRPPS/v_S_01_00_00\"", "/eSocial/evtRmnRPPS/dmDev/infoPerApur/ideEstab/remunPerApur/matricula", "/eSocial/evtRmnRPPS/ideTrabalhador/cpfTrab", "/eSocial/evtRmnRPPS/dmDev/ideDmDev", NamespacesXmlEsocialDTO.S1200_v_S_01_00_00),
    S1202_v_S_01_01_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtRmnRPPS/v_S_01_01_00\"", "/eSocial/evtRmnRPPS/dmDev/infoPerApur/ideEstab/remunPerApur/matricula", "/eSocial/evtRmnRPPS/ideTrabalhador/cpfTrab", "/eSocial/evtRmnRPPS/dmDev/ideDmDev", NamespacesXmlEsocialDTO.S1200_v_S_01_01_00),
    S1210_v02_05_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtPgtos/v02_05_00\"", "", "/eSocial/evtPgtos/ideBenef/cpfBenef", "/eSocial/evtPgtos/ideBenef/infoPgto[1]/ideDmDev", NamespacesXmlEsocialDTO.S1210_v02_05_00),
    S1210_v_S_01_00_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtPgtos/v_S_01_00_00\"", "", "/eSocial/evtPgtos/ideBenef/cpfBenef", "/eSocial/evtPgtos/ideBenef/infoPgto[1]/ideDmDev", NamespacesXmlEsocialDTO.S1210_v_S_01_00_00),
    S1210_v_S_01_01_00("xmlns=\"http://www.esocial.gov.br/schema/evt/evtPgtos/v_S_01_01_00\"", "", "/eSocial/evtPgtos/ideBenef/cpfBenef", "/eSocial/evtPgtos/ideBenef/infoPgto[1]/ideDmDev", NamespacesXmlEsocialDTO.S1210_v_S_01_01_00);
    private String descricao;
    private String caminhoMatricula;
    private String caminhoCPF;
    private String caminhoIdFicha;
    private NamespacesXmlEsocialDTO toDto;

    NamespacesXmlEsocial(String descricao, String caminhoMatricula, String caminhoCPF, String caminhoIdFicha, NamespacesXmlEsocialDTO toDto) {
        this.descricao = descricao;
        this.caminhoMatricula = caminhoMatricula;
        this.caminhoCPF = caminhoCPF;
        this.caminhoIdFicha = caminhoIdFicha;
        this.toDto = toDto;
    }

    public static List<NamespacesXmlEsocial> getNamespacesPorEvento(TipoArquivoESocial tipo) {
        List<NamespacesXmlEsocial> retorno = Lists.newArrayList();
        if (TipoArquivoESocial.S1200.equals(tipo)) {
            retorno.add(S1200_v02_05_00);
            retorno.add(S1200_v_S_01_00_00);
            retorno.add(S1200_v_S_01_01_00);
        } else if (TipoArquivoESocial.S1202.equals(tipo)) {
            retorno.add(S1202_v02_05_00);
            retorno.add(S1202_v_S_01_00_00);
            retorno.add(S1202_v_S_01_01_00);
        } else if (TipoArquivoESocial.S1210.equals(tipo)) {
            retorno.add(S1210_v02_05_00);
            retorno.add(S1210_v_S_01_00_00);
            retorno.add(S1210_v_S_01_01_00);
        }
        return retorno;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCaminhoMatricula() {
        return caminhoMatricula;
    }

    public String getCaminhoCPF() {
        return caminhoCPF;
    }

    public String getCaminhoIdFicha() {
        return caminhoIdFicha;
    }

    public NamespacesXmlEsocialDTO getToDto() {
        return toDto;
    }
}
