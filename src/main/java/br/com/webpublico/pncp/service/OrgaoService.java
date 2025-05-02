package br.com.webpublico.pncp.service;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.pncp.dto.EntidadePncpDTO;
import br.com.webpublico.pncp.dto.OrgaoPncpDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.Query;
import java.util.List;

@Service
public class OrgaoService extends PncpService {

    private final String urlEntidadePncp = "/api/v1/orgaos/";

    public void buscarOrgaosCadastradosPncp(List<EntidadePncpDTO> entidades, ValidacaoException ve) {
        for (EntidadePncpDTO entidade : entidades) {
            for (OrgaoPncpDTO orgao : entidade.getOrgaos()) {
                try {
                    ResponseEntity<OrgaoPncpDTO> forEntity = restTemplate.getForEntity(getBaseUrl(ve) + urlEntidadePncp + entidade.getCnpj() + "/orgaos/" + orgao.getCodigo(), OrgaoPncpDTO.class);
                    if (forEntity.getStatusCode().is2xxSuccessful()) {
                        orgao.setIntegradoPNCP(true);
                    }
                } catch (HttpClientErrorException ex) {
                    orgao.setIntegradoPNCP(false);
                }
            }
        }
    }

    public void enviarOrgaosPncp(List<EntidadePncpDTO> entidades, ValidacaoException ve) {
        for (EntidadePncpDTO entidade : entidades) {
            if (entidade.getIntegradoPNCP()) {
                for (OrgaoPncpDTO orgao : entidade.getOrgaos()) {
                    if (!orgao.getIntegradoPNCP()) {
                        try {
                            restTemplate.postForEntity(getBaseUrl(ve) + urlEntidadePncp + entidade.getCnpj() + "/orgaos", orgao, String.class);
                        } catch (HttpClientErrorException ex) {
                            tratarErroGenerico(ve, ex);
                        }
                    }
                }
            }

        }
    }

    public void enviarOrgaoPncp(OrgaoPncpDTO orgao, String cnpjEntidade, ValidacaoException ve) {
        try {
            restTemplate.postForEntity(getBaseUrl(ve) + urlEntidadePncp + cnpjEntidade + "/orgaos", orgao, String.class);
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
    }

    public void atualizarOrgaoPncp(OrgaoPncpDTO orgao, String cnpjEntidade, ValidacaoException ve) {
        try {
            restTemplate.put(getBaseUrl(ve) + urlEntidadePncp + cnpjEntidade + "/orgaos", orgao, String.class);
        } catch (HttpClientErrorException ex) {
            tratarErroGenerico(ve, ex);
        }
    }

    public void buscarOrgaos(List<EntidadePncpDTO> entidades) {
        for (EntidadePncpDTO entidade : entidades) {
            String sql = " select distinct vw.id, " +
                "      vw.CODIGO as codigo, " +
                "      vw.DESCRICAO as  descricao " +
                " from VWHIERARQUIAADMINISTRATIVA vw " +
                "      inner join entidade ent on ent.ID = vw.ENTIDADE_ID " +
                "      inner join pessoajuridica pj on pj.ID = ent.PESSOAJURIDICA_ID " +
                "      inner join VWHIERARQUIAADMINISTRATIVA superior on vw.SUPERIOR_ID = superior.SUBORDINADA_ID " +
                " where to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(vw.INICIOVIGENCIA) and coalesce(trunc(vw.FIMVIGENCIA), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
                " and sysdate between trunc(superior.INICIOVIGENCIA) and coalesce(trunc(superior.FIMVIGENCIA), sysdate) " +
                "      and superior.codigo = :codigoSuperior ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            q.setParameter("codigoSuperior", entidade.getCodigo());

            List<OrgaoPncpDTO> response = Lists.newArrayList();
            List<Object[]> list = q.getResultList();
            if (list != null && !list.isEmpty()) {
                for (Object[] obj : list) {
                    OrgaoPncpDTO dto = new OrgaoPncpDTO();
                    dto.setId(Long.valueOf(obj[0].toString()));
                    dto.setCodigo((String) obj[1]);
                    dto.setNome((String) obj[2]);
                    dto.setCodigoIBGE(getConfiguracaoTributario().getCidade().getCodigoIBGE().toString());
                    response.add(dto);
                }
            }
            entidade.getOrgaos().clear();
            entidade.getOrgaos().addAll(response);
        }
    }
}
