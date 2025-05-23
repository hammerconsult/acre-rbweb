merge into SALDOcreditoreceber s using (
select id, (select cd.id from fontederecursos fr
inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
inner join conta c on cd.id = c.id
where fr.codigo like '%01'
and cd.DETALHAMENTOFONTEREC_ID is null
and fr.EXERCICIO_ID = (select id from exercicio where ano = extract(year from datasaldo))) as CONTADEDESTINACAO_ID from SALDOcreditoreceber
) sld on (sld.id = s.id)
when matched then update set s.CONTADEDESTINACAO_ID = sld.CONTADEDESTINACAO_ID
