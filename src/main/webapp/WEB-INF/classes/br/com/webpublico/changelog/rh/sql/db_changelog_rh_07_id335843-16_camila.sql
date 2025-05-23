UPDATE ARQUIVOREGISTRODEOBITO ardo
SET descricao = (select ARQUIVO.descricao
                 from ARQUIVOREGISTRODEOBITO a
                          inner join arquivo on a.ARQUIVO_ID = ARQUIVO.ID
                 where a.id = ardo.id);
