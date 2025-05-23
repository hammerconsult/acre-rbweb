insert all
      into conta (id, ativa, codigo, dataregistro, descricao, funcao, permitirdesdobramento, rubrica, tipocontacontabil, planodecontas_id, superior_id, dtype, exercicio_id, categoria, codigotce, codigosiconfi)
      values (HIBERNATE_SEQUENCE.nextval, ativa, codigo || '.01' , dataregistro, descricao, funcao, permitirdesdobramento, rubrica, tipocontacontabil,
              planodecontas_id, id, dtype, exercicio_id, categoria, codigotce, codigosiconfi)
      into contadedestinacao (id, FONTEDERECURSOS_ID, datacriacao)
      values (HIBERNATE_SEQUENCE.currval, FONTEDERECURSOS_ID, datacriacao)
select c.*, cd.FONTEDERECURSOS_ID, cd.DATACRIACAO from conta c
inner join contadedestinacao cd on c.id = cd.ID
where c.exercicio_id = (select id from exercicio where ano = 2021)
and c.superior_id is not null
