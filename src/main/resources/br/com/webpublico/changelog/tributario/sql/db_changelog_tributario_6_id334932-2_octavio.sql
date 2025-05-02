update parcelacontacorrentetrib parcela_cct set parcela_cct.origem = (case when itemlotebaixa_id is not null then 'ARRECADACAO' else 'PROCESSO' end)
where parcela_cct.origem = 'RESTITUICAO';
