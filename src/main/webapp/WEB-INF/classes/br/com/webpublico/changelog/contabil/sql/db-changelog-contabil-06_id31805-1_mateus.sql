update creditoreceber div set div.CONTADEDESTINACAO_ID = (select cd.id from fontederecursos fr
inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
inner join conta c on cd.id = c.id
where fr.codigo like '%10'
and cd.DETALHAMENTOFONTEREC_ID is null
and fr.EXERCICIO_ID = div.exercicio_id)
where div.OPERACAOCREDITORECEBER  = 'TRANSFERENCIA_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO';

update saldocreditoreceber set intervalo = 'LONGO_PRAZO', contadedestinacao_id = 887148720 where id = 925737839;

update saldocreditoreceber set intervalo = 'LONGO_PRAZO', contadedestinacao_id = 887148720 where id = 925737841;

update saldocreditoreceber set contadedestinacao_id = 887148720 where id = 925737840;

update saldocreditoreceber set contadedestinacao_id = 887148720 where id = 925737842;
