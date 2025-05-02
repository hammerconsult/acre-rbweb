update conta c set c.codigotce = (select fr.codigotce from contadedestinacao cd
  inner join fontederecursos fr on cd.FONTEDERECURSOS_ID = fr.ID
  where c.id = cd.ID), c.codigosiconfi = (select fr.codigosiconfi from contadedestinacao cd
  inner join fontederecursos fr on cd.FONTEDERECURSOS_ID = fr.ID
where c.id = cd.ID)
where c.dtype = 'ContaDeDestinacao'
